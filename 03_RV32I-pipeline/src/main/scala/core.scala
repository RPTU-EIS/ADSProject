package core_tile

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile
import Assignment02.{ALU, ALUOp}
import uopc._

class PipelinedRV32Icore(BinaryFile: String, useBTB: Boolean = false) extends Module {
  val io = IO(new Bundle {
    val check_res = Output(UInt(32.W))
    val exception = Output(Bool())
    val total_branches    = Output(UInt(32.W))
    val total_mispredicts = Output(UInt(32.W))
  })

  // =========================================================================
  // Instantiate pipeline stages, barriers, forwarding unit
  // =========================================================================
  val ifStage    = Module(new IF(BinaryFile))
  val ifBarrier  = Module(new IFBarrier)
  val idStage    = Module(new ID)
  val idBarrier  = Module(new IDBarrier)
  val exStage    = Module(new EX)
  val exBarrier  = Module(new EXBarrier)
  val memStage   = Module(new MEM)
  val memBarrier = Module(new MEMBarrier)
  val wbStage    = Module(new WB)
  val wbBarrier  = Module(new WBBarrier)
  val regFile    = Module(new regFile)
  val fwdUnit    = Module(new ForwardingUnit)

  // =========================================================================
  // BTB (only when enabled)
  // =========================================================================
  // Signals for BTB prediction — default to static prediction (no BTB)
  val btbValid        = WireDefault(false.B)
  val btbTarget       = WireDefault(0.U(32.W))
  val btbPredictTaken = WireDefault(false.B)

  if (useBTB) {
    val btb = Module(new BTB)

    // Lookup: feed current IF PC to BTB
    btb.io.pc := ifStage.io.pc

    // BTB prediction outputs
    btbValid        := btb.io.valid
    btbTarget       := btb.io.target
    btbPredictTaken := btb.io.predictTaken

    // Update: connect from EX stage outputs
    btb.io.update       := exStage.io.btbUpdate
    btb.io.updatePC     := exStage.io.btbUpdatePC
    btb.io.updateTarget := exStage.io.btbUpdateTarget
    btb.io.mispredicted := exStage.io.btbMispredicted
  }

  // =========================================================================
  // IF Stage
  // =========================================================================
  ifStage.io.redirectEn := exStage.io.redirectEn
  ifStage.io.redirectPC := exStage.io.redirectTarget

  // BTB prediction to IF stage (for next PC selection)
  ifStage.io.btbValid        := btbValid
  ifStage.io.btbPredictTaken := btbPredictTaken
  ifStage.io.btbTarget       := btbTarget

  // =========================================================================
  // IF Barrier (with BTB info and flush)
  // =========================================================================
  ifBarrier.io.inInstr           := ifStage.io.instr
  ifBarrier.io.inPC              := ifStage.io.pc
  ifBarrier.io.inBtbHit          := btbValid
  ifBarrier.io.inBtbPredictTaken := btbPredictTaken
  ifBarrier.io.flush             := exStage.io.redirectEn

  // =========================================================================
  // ID Stage ↔ Register File
  // =========================================================================
  idStage.io.instr := ifBarrier.io.outInstr
  idStage.io.pc    := ifBarrier.io.outPC

  regFile.io.req_1 := idStage.io.regFileReq_A
  idStage.io.regFileResp_A := regFile.io.resp_1
  regFile.io.req_2 := idStage.io.regFileReq_B
  idStage.io.regFileResp_B := regFile.io.resp_2

  // =========================================================================
  // ID Barrier (with BTB passthrough and flush)
  // =========================================================================
  idBarrier.io.inUOP          := idStage.io.uop
  idBarrier.io.inRD           := idStage.io.rd
  idBarrier.io.inRS1          := idStage.io.rs1
  idBarrier.io.inRS2          := idStage.io.rs2
  idBarrier.io.inOperandA     := idStage.io.operandA
  idBarrier.io.inOperandB     := idStage.io.operandB
  idBarrier.io.inOpBSel       := idStage.io.opBSel
  idBarrier.io.inWrEn         := idStage.io.wrEn
  idBarrier.io.inPC           := idStage.io.pcOut
  idBarrier.io.inBranchTarget := idStage.io.branchTarget
  idBarrier.io.inXcptInvalid  := idStage.io.XcptInvalid
  // BTB prediction passes around ID (ID doesn't process it)
  idBarrier.io.inBtbHit          := ifBarrier.io.outBtbHit
  idBarrier.io.inBtbPredictTaken := ifBarrier.io.outBtbPredictTaken
  idBarrier.io.flush             := exStage.io.redirectEn

  // =========================================================================
  // Forwarding Unit
  // =========================================================================
  fwdUnit.io.idEx_rs1 := idBarrier.io.outRS1
  fwdUnit.io.idEx_rs2 := idBarrier.io.outRS2
  fwdUnit.io.exMem_rd := exBarrier.io.outRD
  fwdUnit.io.memWb_rd := memBarrier.io.outRD

  val fwdOperandA = MuxLookup(fwdUnit.io.forwardA, idBarrier.io.outOperandA, Seq(
    1.U -> memBarrier.io.outAluResult,
    2.U -> exBarrier.io.outAluResult
  ))

  val fwdOperandB = Mux(idBarrier.io.outOpBSel,
    idBarrier.io.outOperandB,
    MuxLookup(fwdUnit.io.forwardB, idBarrier.io.outOperandB, Seq(
      1.U -> memBarrier.io.outAluResult,
      2.U -> exBarrier.io.outAluResult
    ))
  )

  val fwdBranchOpB = MuxLookup(fwdUnit.io.forwardB, idBarrier.io.outOperandB, Seq(
    1.U -> memBarrier.io.outAluResult,
    2.U -> exBarrier.io.outAluResult
  ))

  // =========================================================================
  // EX Stage (with forwarded operands and BTB prediction)
  // =========================================================================
  exStage.io.uop          := idBarrier.io.outUOP
  exStage.io.operandA     := fwdOperandA
  exStage.io.operandB     := fwdOperandB
  exStage.io.pc           := idBarrier.io.outPC
  exStage.io.branchTarget := idBarrier.io.outBranchTarget
  exStage.io.XcptInvalid  := idBarrier.io.outXcptInvalid
  exStage.io.branchOpA    := fwdOperandA
  exStage.io.branchOpB    := fwdBranchOpB
  exStage.io.btbHit          := idBarrier.io.outBtbHit
  exStage.io.btbPredictTaken := idBarrier.io.outBtbPredictTaken

  // =========================================================================
  // EX Barrier → MEM Barrier → WB Stage → WB Barrier → Output
  // =========================================================================
  exBarrier.io.inAluResult   := exStage.io.aluResult
  exBarrier.io.inRD          := idBarrier.io.outRD
  exBarrier.io.inWrEn        := idBarrier.io.outWrEn
  exBarrier.io.inXcptInvalid := exStage.io.exception

  memBarrier.io.inAluResult := exBarrier.io.outAluResult
  memBarrier.io.inRD        := exBarrier.io.outRD
  memBarrier.io.inWrEn      := exBarrier.io.outWrEn
  memBarrier.io.inException := exBarrier.io.outXcptInvalid

  wbStage.io.aluResult := memBarrier.io.outAluResult
  wbStage.io.rd        := memBarrier.io.outRD
  wbStage.io.wrEn      := memBarrier.io.outWrEn

  regFile.io.req_3 := wbStage.io.regFileReq

  wbBarrier.io.inCheckRes    := wbStage.io.check_res
  wbBarrier.io.inXcptInvalid := memBarrier.io.outException

  io.check_res := wbBarrier.io.outCheckRes
  io.exception := wbBarrier.io.outXcptInvalid

  // Performance counters from EX stage to top-level I/O
  io.total_branches    := exStage.io.totalBranches
  io.total_mispredicts := exStage.io.totalMispredicts
}