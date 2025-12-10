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
<<<<<<< HEAD
    val a = Input(UInt(1.W))
    val b = Input(UInt(1.W))
    val co = Output(UInt(1.W))
    val s = Output(UInt(1.W))
    })

  io.s := io.a ^ io.b
  io.co := io.a & io.b

=======
    /* 
     * TODO: Define IO ports of a half adder as presented in the lecture
     */
    val a  = Input(UInt(1.W))
    val b  = Input(UInt(1.W))
    val s  = Output(UInt(1.W))
    val co  = Output(UInt(1.W))
    })

  /* 
   * TODO: Describe output behaviour based on the input values
   */
  io.s  := io.a ^ io.b
  io.co  := io.a & io.b
>>>>>>> 841f4d3542123fd7143114bf51561e27b55c5ffd
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

    val a  = Input(UInt(1.W))
    val b  = Input(UInt(1.W))
    val ci = Input(UInt(1.W))
    val s  = Output(UInt(1.W))
    val co = Output(UInt(1.W))
    })


  /* 
   * TODO: Instanciate the two half adders you want to use based on your HalfAdder class
   */
  val ha1 = Module(new HalfAdder())
  val ha2 = Module(new HalfAdder())

  /* 
   * TODO: Describe output behaviour based on the input values and the internal signals
   */
  ha1.io.a := io.a
  ha1.io.b := io.b

  ha2.io.a := ha1.io.s
  ha2.io.b := io.ci

  io.s  := ha2.io.s
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

    val a0  = Input(UInt(1.W))
    val b0  = Input(UInt(1.W))
    val a1  = Input(UInt(1.W))
    val b1  = Input(UInt(1.W))
    val a2  = Input(UInt(1.W))
    val b2  = Input(UInt(1.W))
    val a3  = Input(UInt(1.W))
    val b3  = Input(UInt(1.W))
    val s0  = Output(UInt(1.W))
    val s1  = Output(UInt(1.W))
    val s2  = Output(UInt(1.W))
    val s3  = Output(UInt(1.W))
    val co3 = Output(UInt(1.W))
    })

  /* 
   * TODO: Instanciate the full adders and one half adderbased on the previously defined classes
   */
    val ha0 = Module(new HalfAdder())
    val fa1 = Module(new FullAdder())
    val fa2 = Module(new FullAdder())
    val fa3 = Module(new FullAdder())
  /* 
   * TODO: Describe output behaviour based on the input values and the internal 
   */
    ha0.io.a := io.a0
    ha0.io.b := io.b0

    fa1.io.a := io.a1
    fa1.io.b := io.b1
    fa1.io.ci := ha0.io.co

    fa2.io.a := io.a2
    fa2.io.b := io.b2
    fa2.io.ci := fa1.io.co

    fa3.io.a := io.a3
    fa3.io.b := io.b3
    fa3.io.ci := fa2.io.co
    
    io.s0 := ha0.io.s
    io.s1 := fa1.io.s
    io.s2 := fa2.io.s
    io.s3 := fa3.io.s
    io.co3 := fa3.io.co

}
