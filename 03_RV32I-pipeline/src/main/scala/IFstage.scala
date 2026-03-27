package core_tile

import chisel3._
import chisel3.util.experimental.loadMemoryFromFile

class IF (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    val instr = Output(UInt(32.W))
  })

  val IMem = Mem(4096, UInt(32.W))
  loadMemoryFromFile(IMem, BinaryFile)

  val PC = RegInit(0.U(32.W))
  io.instr := IMem(PC)
  PC := PC + 1.U
}
