package core_tile

import chisel3._
import chisel3.util.experimental.loadMemoryFromFile

class IF (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    val instr      = Output(UInt(32.W))
    val pc         = Output(UInt(32.W))   // Current PC (byte address)
    val redirectEn = Input(Bool())        // Redirect signal from EX stage
    val redirectPC = Input(UInt(32.W))    // Redirect target (byte address)
  })

  val IMem = Mem(4096, UInt(32.W))
  loadMemoryFromFile(IMem, BinaryFile)

  // Byte-addressed PC: starts at 0, increments by 4
  val PC = RegInit(0.U(32.W))

  // Fetch instruction at current PC (word-indexed: PC >> 2)
  io.instr := IMem(PC(31, 2))
  io.pc    := PC

  // Next PC: redirect or sequential
  when(io.redirectEn) {
    PC := io.redirectPC
  }.otherwise {
    PC := PC + 4.U
  }
}
