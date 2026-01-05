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
class HalfAdder extends Module{
  
  val io = IO(new Bundle {
    /* 
     * TODO: Define IO ports of a half adder as presented in the lecture
     */
    val a = Input(UInt(1.W))
    val b = Input(UInt(1.W))
    val s = Output(UInt(1.W))
    val co = Output(UInt(1.W))
    })
  /* 
   * TODO: Describe output behaviour based on the input values
   */
  io.s := io.a^io.b //Sum = XOR
  io.co := io.a&io.b //Carry = AND
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
class FullAdder extends Module{

  val io = IO(new Bundle {
    /* 
     * TODO: Define IO ports of a half adder as presented in the lecture
     */
    val a = Input(UInt(1.W))
    val b = Input(UInt(1.W))
    val ci = Input(UInt(1.W))
    val s = Output(UInt(1.W))
    val co = Output(UInt(1.W))
    })


  /* 
   * TODO: Instanciate the two half adders you want to use based on your HalfAdder class
   */
  val ha1 = Module(new HalfAdder())
  val ha2 = Module(new HalfAdder())

  ha1.io.a := io.a
  ha1.io.b := io.b

  ha2.io.a := ha1.io.s
  ha2.io.b := io.ci

  /* 
   * TODO: Describe output behaviour based on the input values and the internal signals
   */
  io.s := ha2.io.s
  io.co := ha1.io.co | ha2.io.co

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
class FourBitAdder extends Module{

  val io = IO(new Bundle {
    /* 
     * TODO: Define IO ports of a 4-bit ripple-carry-adder as presented in the lecture
     */
    val a = Input(UInt(4.W))
    val b = Input(UInt(4.W))
    val s = Output(UInt(4.W))
    val co = Output(UInt(4.W))

    })

  /* 
   * TODO: Instanciate the full adders and one half adderbased on the previously defined classes
   */
  val ha = Module(new HalfAdder())
  val fa1 = Module(new FullAdder())
  val fa2 = Module(new FullAdder())
  val fa3 = Module(new FullAdder())

  //Half adder
  ha.io.a := io.a(0)
  ha.io.b := io.b(0)
  val s0 = ha.io.s
  val c0 = ha.io.co

  //Full adder1
  fa1.io.a := io.a(1)
  fa1.io.b := io.b(1)
  fa1.io.ci := c0
  val s1 = fa1.io.s
  val c1 = fa1.io.co

  //Full adder2
  fa2.io.a := io.a(2)
  fa2.io.b := io.b(2)
  fa2.io.ci := c1
  val s2 = fa2.io.s
  val c2 = fa2.io.co

  //Full adder3
  fa3.io.a := io.a(3)
  fa3.io.b := io.b(3)
  fa3.io.ci := c2
  val s3 = fa3.io.s
  val c3 = fa3.io.co


  /* 
   * TODO: Describe output behaviour based on the input values and the internal 
   */
  io.s := Cat(s3, s2, s1, s0) //Cat will concatinate all the sums and print as "s3 s2 s1 s0"
  io.co := c3

}