// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package adder

import chisel3._
import chisel3.util._


/**
 * Half Adder Class
 *
 * Your task is to implement a basic half adder as presented in the lecture.
 * Each signal should only be one bit wide (inputs and outputs).
 * There should be no delay between input and output signals, we want to have
 * a combinational behaviour of the component.
 */
class HalfAdder extends Module {

  val io = IO(new Bundle {
    val a = Input(UInt(1.W))
    val b = Input(UInt(1.W))
    val s = Output(UInt(1.W))
    val c_o = Output(UInt(1.W))
  })

  io.s := io.a ^ io.b
  io.c_o := io.a & io.b

}

/**
 * Full Adder Class
 *
 * Your task is to implement a basic full adder. The component's behaviour should
 * match the characteristics presented in the lecture. In addition, you are only allowed
 * to use two half adders (use the class that you already implemented) and basic logic
 * operators (AND, OR, ...).
 * Each signal should only be one bit wide (inputs and outputs).
 * There should be no delay between input and output signals, we want to have
 * a combinational behaviour of the component.
 */
class FullAdder extends Module {

  val io = IO(new Bundle {
    val a = Input(UInt(1.W))
    val b = Input(UInt(1.W))
    val c_i = Input(UInt(1.W))
    val s = Output(UInt(1.W))
    val c_o = Output(UInt(1.W))
  })

  val halfadder1 = Module(new HalfAdder)
  val halfadder2 = Module(new HalfAdder)

  halfadder1.io.a := io.a
  halfadder1.io.b := io.b

  halfadder2.io.a := halfadder1.io.s
  halfadder2.io.b := io.c_i

  io.s := halfadder2.io.s
  io.c_o := halfadder1.io.c_o | halfadder2.io.c_o

}

/**
 * 4-bit Adder class
 *
 * Your task is to implement a 4-bit ripple-carry-adder. The component's behaviour should
 * match the characteristics presented in the lecture.  Remember: An n-bit adder can be
 * build using one half adder and n-1 full adders.
 * The inputs and the result should all be 4-bit wide, the carry-out only needs one bit.
 * There should be no delay between input and output signals, we want to have
 * a combinational behaviour of the component.
 */
class FourBitAdder extends Module {

  val io = IO(new Bundle {
    val a = Input(UInt(4.W))
    val b = Input(UInt(4.W))
    val s = Output(UInt(4.W))
    val c_o = Output(UInt(1.W))
  })

  val ha = Module(new HalfAdder)
  val fa1 = Module(new FullAdder)
  val fa2 = Module(new FullAdder)
  val fa3 = Module(new FullAdder)

  ha.io.a := io.a(0)
  ha.io.b := io.b(0)

  fa1.io.a := io.a(1)
  fa1.io.b := io.b(1)
  fa1.io.c_i := ha.io.c_o

  fa2.io.a := io.a(2)
  fa2.io.b := io.b(2)
  fa2.io.c_i := fa1.io.c_o

  fa3.io.a := io.a(3)
  fa3.io.b := io.b(3)
  fa3.io.c_i := fa2.io.c_o

  io.s := Cat(fa3.io.s, fa2.io.s, fa1.io.s, ha.io.s)

  io.c_o := fa3.io.c_o
}
