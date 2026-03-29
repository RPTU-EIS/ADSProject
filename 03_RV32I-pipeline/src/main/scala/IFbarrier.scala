package core_tile

import chisel3._

class IFBarrier extends Module {
  val io = IO(new Bundle {
    val inInstr           = Input(UInt(32.W))
    val inPC              = Input(UInt(32.W))
    val inBtbHit          = Input(Bool())
    val inBtbPredictTaken = Input(Bool())
    val flush             = Input(Bool())

    val outInstr           = Output(UInt(32.W))
    val outPC              = Output(UInt(32.W))
    val outBtbHit          = Output(Bool())
    val outBtbPredictTaken = Output(Bool())
  })

  val instrReg  = RegInit(0.U(32.W))
  val pcReg     = RegInit(0.U(32.W))
  val btbHitReg = RegInit(false.B)
  val btbPTReg  = RegInit(false.B)

  when(io.flush) {
    instrReg  := "h00000013".U   // NOP
    pcReg     := 0.U
    btbHitReg := false.B
    btbPTReg  := false.B
  }.otherwise {
    instrReg  := io.inInstr
    pcReg     := io.inPC
    btbHitReg := io.inBtbHit
    btbPTReg  := io.inBtbPredictTaken
  }

  io.outInstr           := instrReg
  io.outPC              := pcReg
  io.outBtbHit          := btbHitReg
  io.outBtbPredictTaken := btbPTReg
}
