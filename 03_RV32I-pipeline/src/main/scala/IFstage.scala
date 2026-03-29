package core_tile

import chisel3._
import chisel3.util.experimental.loadMemoryFromFile

class IF(BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    val instr      = Output(UInt(32.W))
    val pc         = Output(UInt(32.W))

    // EX stage redirect (highest priority)
    val redirectEn = Input(Bool())
    val redirectPC = Input(UInt(32.W))

    // BTB prediction (used when no EX redirect)
    val btbValid        = Input(Bool())
    val btbPredictTaken = Input(Bool())
    val btbTarget       = Input(UInt(32.W))
  })

  val IMem = Mem(4096, UInt(32.W))
  loadMemoryFromFile(IMem, BinaryFile)

  val PC = RegInit(0.U(32.W))

  io.instr := IMem(PC(31, 2))
  io.pc    := PC

  // Next PC selection (priority: EX redirect > BTB prediction > sequential)
  when(io.redirectEn) {
    PC := io.redirectPC
  }.elsewhen(io.btbValid && io.btbPredictTaken) {
    PC := io.btbTarget
  }.otherwise {
    PC := PC + 4.U
  }
}
