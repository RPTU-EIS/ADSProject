// ADS I Class Project
// Pipelined RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/15/2023 by Tobias Jauch (@tojauch)

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
  val regfile    = Module(new regFile)

  // ── IF Stage ──────────────────────────────────────────────────────────────
  ifStage.io.flush        := exStage.io.flush
  ifStage.io.branchTarget := exStage.io.branchTarget
  ifStage.io.branchTaken  := exStage.io.flush

  // IF → IFBarrier (flush injects NOP to squash WP2)
  ifBarrier.io.inInstr := ifStage.io.instr
  ifBarrier.io.inPC    := ifStage.io.pc
  ifBarrier.io.flush   := exStage.io.flush

  // ── ID Stage ──────────────────────────────────────────────────────────────
  idStage.io.instr := ifBarrier.io.outInstr
  idStage.io.pc    := ifBarrier.io.outPC

  regfile.io.req_1         := idStage.io.regFileReq_A
  idStage.io.regFileResp_A := regfile.io.resp_1
  regfile.io.req_2         := idStage.io.regFileReq_B
  idStage.io.regFileResp_B := regfile.io.resp_2

  // ID → IDBarrier (flush zeroes out registers to squash WP1)
  idBarrier.io.inUOP         := idStage.io.uop
  idBarrier.io.inRD          := idStage.io.rd
  idBarrier.io.inRS1         := idStage.io.rs1
  idBarrier.io.inRS2         := idStage.io.rs2
  idBarrier.io.inOperandA    := idStage.io.operandA
  idBarrier.io.inOperandB    := idStage.io.operandB
  idBarrier.io.inPC          := idStage.io.pcOut
  idBarrier.io.inXcptInvalid := idStage.io.XcptInvalid
  idBarrier.io.inwr_en       := idStage.io.wr_en
  idBarrier.io.flush         := exStage.io.flush

  // ── EX Stage ──────────────────────────────────────────────────────────────
  exStage.io.uop         := idBarrier.io.outUOP
  exStage.io.operandA    := idBarrier.io.outOperandA
  exStage.io.operandB    := idBarrier.io.outOperandB
  exStage.io.rd          := idBarrier.io.outRD
  exStage.io.rs1         := idBarrier.io.outRS1
  exStage.io.rs2         := idBarrier.io.outRS2
  exStage.io.pc          := idBarrier.io.outPC
  exStage.io.XcptInvalid := idBarrier.io.outXcptInvalid

  // Forwarding from EX-MEM and MEM-WB barriers
  exStage.io.aluResult_MEM := exBarrier.io.outAluResult
  exStage.io.rd_MEM        := exBarrier.io.outRD
  exStage.io.wrEn_MEM      := exBarrier.io.outWriteEn
  exStage.io.aluResult_WB  := memBarrier.io.outAluResult
  exStage.io.rd_WB         := memBarrier.io.outRD
  exStage.io.wrEn_WB       := memBarrier.io.outWriteEn

  // EX → EXBarrier
  exBarrier.io.inAluResult   := exStage.io.aluResult
  exBarrier.io.inRD          := exStage.io.rdOut
  exBarrier.io.inXcptInvalid := exStage.io.exception
  exBarrier.io.inwr_en       := idBarrier.io.outwr_en

  // ── MEM Stage ─────────────────────────────────────────────────────────────
  memBarrier.io.inAluResult := exBarrier.io.outAluResult
  memBarrier.io.inRD        := exBarrier.io.outRD
  memBarrier.io.inException := exBarrier.io.outXcptInvalid
  memBarrier.io.inwr_en     := exBarrier.io.outWriteEn

  // ── WB Stage ──────────────────────────────────────────────────────────────
  wbStage.io.aluResult := memBarrier.io.outAluResult
  wbStage.io.rd        := memBarrier.io.outRD
  wbStage.io.writeEn   := memBarrier.io.outWriteEn

  regfile.io.req_3 := wbStage.io.regFileReq

  wbBarrier.io.inCheckRes    := wbStage.io.check_res
  wbBarrier.io.inXcptInvalid := memBarrier.io.outException

  io.check_res := wbBarrier.io.outCheckRes
  io.exception := wbBarrier.io.outXcptInvalid
}
