// Chisel Introduction
// Chair of Electronic Design Automation, TU Kaiserslautern
// File created on 18/10/2022 by M.Sc. Tobias Jauch (@tojauch)

package basicadder

import chisel3._
import chisel3.util._


/** Basic adder class */
class BasicAdder extends Module{
  
  val io = IO(new Bundle {
    val a  = Input(UInt(8.W))
    val b  = Input(UInt(8.W))
    val c  = Output(UInt(8.W))
    })

  val Reg = RegInit(0.U(8.W))

  Reg := io.a + io.b

  io.c := Reg

}

/*object Verilog_Gen extends App {
  emitVerilog(new BasicAdder(), Array("--target-dir", "generated-src"))
  //(new chisel3.stage.ChiselStage).emitVerilog(new Wrapper())
}*/
