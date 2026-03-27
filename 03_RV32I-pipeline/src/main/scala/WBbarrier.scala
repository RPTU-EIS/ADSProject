package core_tile

import chisel3._

class WBBarrier extends Module {
  val io = IO(new Bundle {
    val inCheckRes    = Input(UInt(32.W))
    val inXcptInvalid = Input(Bool())
    val outCheckRes    = Output(UInt(32.W))
    val outXcptInvalid = Output(Bool())
  })

  val checkResReg = RegInit(0.U(32.W))
  val xcptReg     = RegInit(false.B)

  checkResReg := io.inCheckRes
  xcptReg     := io.inXcptInvalid

  io.outCheckRes    := checkResReg
  io.outXcptInvalid := xcptReg
}
