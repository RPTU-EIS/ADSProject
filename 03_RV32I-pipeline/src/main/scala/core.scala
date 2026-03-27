// ADS I Class Project
// Pipelined RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/15/2023 by Tobias Jauch (@tojauch)
// Modified for Assignment 04: Forwarding unit integration

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
  // Instantiate all pipeline stages, barriers, and forwarding unit
  // =========================================================================

  // Stage 1: Instruction Fetch
  val ifStage   = Module(new IF(BinaryFile))
  val ifBarrier = Module(new IFBarrier)

  // Stage 2: Instruction Decode
  val idStage   = Module(new ID)
  val idBarrier = Module(new IDBarrier)

  // Stage 3: Execute
  val exStage   = Module(new EX)
  val exBarrier = Module(new EXBarrier)

  // Stage 4: Memory (placeholder)
  val memStage   = Module(new MEM)
  val memBarrier = Module(new MEMBarrier)

  // Stage 5: Writeback
  val wbStage   = Module(new WB)
  val wbBarrier = Module(new WBBarrier)

  // Register File (shared between ID and WB stages)
  val regFile = Module(new regFile)

  // Forwarding Unit (NEW)
  val fwdUnit = Module(new ForwardingUnit)

  // =========================================================================
  // Connect pipeline: IF Stage -> IF Barrier
  // =========================================================================
  ifBarrier.io.inInstr := ifStage.io.instr

  // =========================================================================
  // Connect pipeline: IF Barrier -> ID Stage
  // =========================================================================
  idStage.io.instr := ifBarrier.io.outInstr

  // =========================================================================
  // ID Stage <-> Register File (read ports)
  // =========================================================================
  regFile.io.req_1  := idStage.io.regFileReq_A
  idStage.io.regFileResp_A := regFile.io.resp_1
  regFile.io.req_2  := idStage.io.regFileReq_B
  idStage.io.regFileResp_B := regFile.io.resp_2

  // =========================================================================
  // Connect pipeline: ID Stage -> ID Barrier (including new signals)
  // =========================================================================
  idBarrier.io.inUOP         := idStage.io.uop
  idBarrier.io.inRD          := idStage.io.rd
  idBarrier.io.inRS1         := idStage.io.rs1          // NEW
  idBarrier.io.inRS2         := idStage.io.rs2          // NEW
  idBarrier.io.inOperandA    := idStage.io.operandA
  idBarrier.io.inOperandB    := idStage.io.operandB
  idBarrier.io.inOpBSel      := idStage.io.opBSel       // NEW
  idBarrier.io.inXcptInvalid := idStage.io.XcptInvalid

  // =========================================================================
  // Forwarding Unit connections (NEW)
  // =========================================================================
  fwdUnit.io.idEx_rs1 := idBarrier.io.outRS1       // rs1 of instruction in EX
  fwdUnit.io.idEx_rs2 := idBarrier.io.outRS2       // rs2 of instruction in EX
  fwdUnit.io.exMem_rd := exBarrier.io.outRD         // rd of instruction in MEM
  fwdUnit.io.memWb_rd := memBarrier.io.outRD         // rd of instruction in WB

  // =========================================================================
  // Forwarding Muxes (NEW)
  //
  // Select the correct operand source:
  //   0 = no forwarding (use value from ID/EX barrier)
  //   1 = forward from WB stage (MEM/WB barrier output)
  //   2 = forward from MEM stage (EX/MEM barrier output)
  // =========================================================================

  // Forwarding mux for operandA — always applies (rs1 is always a register)
  val fwdOperandA = MuxLookup(fwdUnit.io.forwardA, idBarrier.io.outOperandA, Seq(
    1.U -> memBarrier.io.outAluResult,    // Forward from WB stage
    2.U -> exBarrier.io.outAluResult      // Forward from MEM stage
  ))

  // Forwarding mux for operandB — only when operandB is from a register (R-type)
  // For I-type instructions (opBSel=true), operandB is an immediate and must NOT be forwarded
  val fwdOperandB = Mux(idBarrier.io.outOpBSel,
    idBarrier.io.outOperandB,  // I-type: use immediate as-is
    MuxLookup(fwdUnit.io.forwardB, idBarrier.io.outOperandB, Seq(
      1.U -> memBarrier.io.outAluResult,  // Forward from WB stage
      2.U -> exBarrier.io.outAluResult    // Forward from MEM stage
    ))
  )

  // =========================================================================
  // Connect pipeline: ID Barrier -> EX Stage (with forwarded operands)
  // =========================================================================
  exStage.io.uop         := idBarrier.io.outUOP
  exStage.io.operandA    := fwdOperandA              // CHANGED: use forwarded value
  exStage.io.operandB    := fwdOperandB              // CHANGED: use forwarded value
  exStage.io.XcptInvalid := idBarrier.io.outXcptInvalid

  // =========================================================================
  // Connect pipeline: EX Stage -> EX Barrier
  // =========================================================================
  exBarrier.io.inAluResult   := exStage.io.aluResult
  exBarrier.io.inRD          := idBarrier.io.outRD
  exBarrier.io.inXcptInvalid := exStage.io.exception

  // =========================================================================
  // Connect pipeline: EX Barrier -> MEM Barrier (pass through MEM stage)
  // =========================================================================
  memBarrier.io.inAluResult := exBarrier.io.outAluResult
  memBarrier.io.inRD        := exBarrier.io.outRD
  memBarrier.io.inException := exBarrier.io.outXcptInvalid

  // =========================================================================
  // Connect pipeline: MEM Barrier -> WB Stage
  // =========================================================================
  wbStage.io.aluResult := memBarrier.io.outAluResult
  wbStage.io.rd        := memBarrier.io.outRD

  // =========================================================================
  // WB Stage <-> Register File (write port)
  // =========================================================================
  regFile.io.req_3 := wbStage.io.regFileReq

  // =========================================================================
  // Connect pipeline: WB Stage -> WB Barrier -> Outputs
  // =========================================================================
  wbBarrier.io.inCheckRes    := wbStage.io.check_res
  wbBarrier.io.inXcptInvalid := memBarrier.io.outException

  io.check_res := wbBarrier.io.outCheckRes
  io.exception := wbBarrier.io.outXcptInvalid
}
