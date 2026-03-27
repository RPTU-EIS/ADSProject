package core_tile

import chisel3._

class MEMBarrier extends Module {
  val io = IO(new Bundle {
    val inAluResult  = Input(UInt(32.W))
    val inRD         = Input(UInt(5.W))
    val inException  = Input(Bool())
    val outAluResult  = Output(UInt(32.W))
    val outRD         = Output(UInt(5.W))
    val outException  = Output(Bool())
  })

  val aluResultReg = RegInit(0.U(32.W))
  val rdReg        = RegInit(0.U(5.W))
  val exceptionReg = RegInit(false.B)

  aluResultReg := io.inAluResult
  rdReg        := io.inRD
  exceptionReg := io.inException

  io.outAluResult  := aluResultReg
  io.outRD         := rdReg
  io.outException  := exceptionReg
}
