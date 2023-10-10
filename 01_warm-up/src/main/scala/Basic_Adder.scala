// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

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
