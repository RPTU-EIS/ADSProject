package core_tile

import chisel3._

class IFBarrier extends Module {
  val io = IO(new Bundle {
    val inInstr  = Input(UInt(32.W))
    val outInstr = Output(UInt(32.W))
  })

  val instrReg = RegInit(0.U(32.W))
  instrReg := io.inInstr
  io.outInstr := instrReg
}
