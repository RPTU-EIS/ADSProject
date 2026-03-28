package core_tile

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile
import Assignment02.{ALU, ALUOp}
import uopc._

class PipelinedRV32Icore (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    val check_res = Output(UInt(32.W))
    val exception = Output(Bool())
  })

  // =========================================================================
  // Instantiate modules
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
  // IF Stage → IF Barrier
  // =========================================================================
  ifStage.io.redirectEn := exStage.io.redirectEn
  ifStage.io.redirectPC := exStage.io.redirectTarget

  ifBarrier.io.inInstr := ifStage.io.instr
  ifBarrier.io.inPC    := ifStage.io.pc
  ifBarrier.io.flush   := exStage.io.redirectEn   // flush on redirect

  // =========================================================================
  // IF Barrier → ID Stage
  // =========================================================================
  idStage.io.instr := ifBarrier.io.outInstr
  idStage.io.pc    := ifBarrier.io.outPC

  // =========================================================================
  // ID Stage ↔ Register File (read ports)
  // =========================================================================
  regFile.io.req_1 := idStage.io.regFileReq_A
  idStage.io.regFileResp_A := regFile.io.resp_1
  regFile.io.req_2 := idStage.io.regFileReq_B
  idStage.io.regFileResp_B := regFile.io.resp_2

  // =========================================================================
  // ID Stage → ID Barrier
  // =========================================================================
  idBarrier.io.inUOP         := idStage.io.uop
  idBarrier.io.inRD          := idStage.io.rd
  idBarrier.io.inRS1         := idStage.io.rs1
  idBarrier.io.inRS2         := idStage.io.rs2
  idBarrier.io.inOperandA    := idStage.io.operandA
  idBarrier.io.inOperandB    := idStage.io.operandB
  idBarrier.io.inOpBSel      := idStage.io.opBSel
  idBarrier.io.inWrEn        := idStage.io.wrEn
  idBarrier.io.inPC          := idStage.io.pcOut
  idBarrier.io.inBranchTarget:= idStage.io.branchTarget
  idBarrier.io.inXcptInvalid := idStage.io.XcptInvalid
  idBarrier.io.flush         := exStage.io.redirectEn   // flush on redirect

  // =========================================================================
  // Forwarding Unit
  // =========================================================================
  fwdUnit.io.idEx_rs1 := idBarrier.io.outRS1
  fwdUnit.io.idEx_rs2 := idBarrier.io.outRS2
  fwdUnit.io.exMem_rd := exBarrier.io.outRD
  fwdUnit.io.memWb_rd := memBarrier.io.outRD

  // =========================================================================
  // Forwarding Muxes
  // =========================================================================
  // Forwarded operandA (rs1) — always forwarded
  val fwdOperandA = MuxLookup(fwdUnit.io.forwardA, idBarrier.io.outOperandA, Seq(
    1.U -> memBarrier.io.outAluResult,   // from WB stage
    2.U -> exBarrier.io.outAluResult     // from MEM stage
  ))

  // Forwarded operandB — only for register source (opBSel=false)
  val fwdOperandB = Mux(idBarrier.io.outOpBSel,
    idBarrier.io.outOperandB,            // I-type: use immediate
    MuxLookup(fwdUnit.io.forwardB, idBarrier.io.outOperandB, Seq(
      1.U -> memBarrier.io.outAluResult,
      2.U -> exBarrier.io.outAluResult
    ))
  )

  // Forwarded rs2 value (for branch comparison, always register-based)
  // This is needed because for B-type: opBSel=false, so fwdOperandB works.
  // But we keep a separate "branchOpB" that is ALWAYS forwarded (never gated by opBSel)
  // for use in branch comparisons.
  val fwdBranchOpB = MuxLookup(fwdUnit.io.forwardB, idBarrier.io.outOperandB, Seq(
    1.U -> memBarrier.io.outAluResult,
    2.U -> exBarrier.io.outAluResult
  ))

  // =========================================================================
  // ID Barrier → EX Stage (with forwarded operands)
  // =========================================================================
  exStage.io.uop          := idBarrier.io.outUOP
  exStage.io.operandA     := fwdOperandA
  exStage.io.operandB     := fwdOperandB
  exStage.io.pc           := idBarrier.io.outPC
  exStage.io.branchTarget := idBarrier.io.outBranchTarget
  exStage.io.XcptInvalid  := idBarrier.io.outXcptInvalid
  exStage.io.branchOpA    := fwdOperandA    // forwarded rs1 for branch comparison
  exStage.io.branchOpB    := fwdBranchOpB   // forwarded rs2 for branch comparison

  // =========================================================================
  // EX Stage → EX Barrier
  // =========================================================================
  exBarrier.io.inAluResult   := exStage.io.aluResult
  exBarrier.io.inRD          := idBarrier.io.outRD
  exBarrier.io.inWrEn        := idBarrier.io.outWrEn
  exBarrier.io.inXcptInvalid := exStage.io.exception

  // =========================================================================
  // EX Barrier → MEM Barrier (pass through empty MEM stage)
  // =========================================================================
  memBarrier.io.inAluResult := exBarrier.io.outAluResult
  memBarrier.io.inRD        := exBarrier.io.outRD
  memBarrier.io.inWrEn      := exBarrier.io.outWrEn
  memBarrier.io.inException := exBarrier.io.outXcptInvalid

  // =========================================================================
  // MEM Barrier → WB Stage
  // =========================================================================
  wbStage.io.aluResult := memBarrier.io.outAluResult
  wbStage.io.rd        := memBarrier.io.outRD
  wbStage.io.wrEn      := memBarrier.io.outWrEn

  // =========================================================================
  // WB Stage ↔ Register File (write port)
  // =========================================================================
  regFile.io.req_3 := wbStage.io.regFileReq

  // =========================================================================
  // WB Stage → WB Barrier → Outputs
  // =========================================================================
  wbBarrier.io.inCheckRes    := wbStage.io.check_res
  wbBarrier.io.inXcptInvalid := memBarrier.io.outException

  io.check_res := wbBarrier.io.outCheckRes
  io.exception := wbBarrier.io.outXcptInvalid
}
