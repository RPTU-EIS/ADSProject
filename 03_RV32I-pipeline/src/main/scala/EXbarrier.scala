package core_tile

import chisel3._

class EXBarrier extends Module {
  val io = IO(new Bundle {
    val inAluResult   = Input(UInt(32.W))
    val inRD          = Input(UInt(5.W))
    val inXcptInvalid = Input(Bool())
    val outAluResult   = Output(UInt(32.W))
    val outRD          = Output(UInt(5.W))
    val outXcptInvalid = Output(Bool())
  })

  val aluResultReg = RegInit(0.U(32.W))
  val rdReg        = RegInit(0.U(5.W))
  val xcptReg      = RegInit(false.B)

  aluResultReg := io.inAluResult
  rdReg        := io.inRD
  xcptReg      := io.inXcptInvalid

  io.outAluResult   := aluResultReg
  io.outRD          := rdReg
  io.outXcptInvalid := xcptReg
}
