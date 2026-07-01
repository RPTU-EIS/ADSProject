// ADS I Class Project
// Pipelined RISC-V Core - MEM Barrier
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
MEM-Barrier: pipeline register between Memory and Writeback stages

Functionality:
    Pass aluResult, rd, exception flag, and write-enable through to WB stage.
    outWriteEn = wr_en && !exception (used by forwarding unit and WB stage).
*/

package core_tile

import chisel3._

class MEMBarrier extends Module {
  val io = IO(new Bundle {
    val inAluResult = Input(UInt(32.W))
    val inRD        = Input(UInt(5.W))
    val inException = Input(Bool())
    val inwr_en     = Input(Bool())

    val outAluResult = Output(UInt(32.W))
    val outRD        = Output(UInt(5.W))
    val outException = Output(Bool())
    val outWriteEn   = Output(Bool())
  })

  val aluResultReg = RegInit(0.U(32.W))
  val rdReg        = RegInit(0.U(5.W))
  val exceptionReg = RegInit(false.B)
  val wr_enReg     = RegInit(false.B)

  aluResultReg := io.inAluResult
  rdReg        := io.inRD
  exceptionReg := io.inException
  wr_enReg     := io.inwr_en

  io.outAluResult := aluResultReg
  io.outRD        := rdReg
  io.outException := exceptionReg
  io.outWriteEn   := wr_enReg && !exceptionReg
}
