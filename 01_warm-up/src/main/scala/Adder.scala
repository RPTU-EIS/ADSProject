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
    val a = Input(UInt(1.W))
    val b = Input(UInt(1.W))
    val s = Output(UInt(1.W)) //sum
    val co     = Output(UInt(1.W)) //carry out
  })

    io.s := io.a ^ io.b
    io.co    := io.a & io.b // AND for carry
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
    val a = Input(UInt(1.W))
    val b = Input(UInt(1.W))
    val ci= Input(UInt(1.W)) //carry-in bit
    val s = Output(UInt(1.W))
    val co = Output(UInt(1.W)) //carry out bit
    })

//First Half Adder (a+b) giving intermediate sum ha1.io.output and ha1.io.co
// sum = a XOR b
// carry = a AND b
    val ha1 = Module (new HalfAdder)
    ha1.io.a := io.a
    ha1.io.b := io.b


//Second Hald Adder
// sum = (a XOR b) XOR ci
// carry = (a XOR b) AND ci

    val ha2 = Module (new HalfAdder)
    ha2.io.a := ha1.io.s
    ha2.io.b := io.ci

 // Sum output ( output = input1 XOR input2 XOR ci )
    io.s := ha2.io.s
 // Carry-out (co = (input1 AND input2) OR (sum + ci)
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
    val a0 = Input(UInt(1.W)) //Definition of inputs
    val b0 = Input(UInt(1.W))
    val a1 = Input(UInt(1.W))
    val b1 = Input(UInt(1.W))
    val a2 = Input(UInt(1.W))
    val b2 = Input(UInt(1.W))
    val a3 = Input(UInt(1.W))
    val b3 = Input(UInt(1.W))

    val s0 = Output(UInt(1.W)) //Definition of outputs
    val s1 = Output(UInt(1.W))
    val s2 = Output(UInt(1.W))
    val s3 = Output(UInt(1.W))
    val co3 = Output(UInt(1.W)) //carry out bit
  })

//First Half Adder
    val ha1 = Module (new HalfAdder)
    ha1.io.a := io.a0
    ha1.io.b := io.b0

//First Full Adder
    val fa1 = Module (new FullAdder)
    fa1.io.a := io.a1
    fa1.io.b := io.b1
    fa1.io.ci := ha1.io.co

//Second Full Adder
    val fa2 = Module (new FullAdder)
    fa2.io.a := io.a2
    fa2.io.b := io.b2
    fa2.io.ci := fa1.io.co

//Third Full Adder
    val fa3 = Module (new FullAdder)
    fa3.io.a := io.a3
    fa3.io.b := io.b3
    fa3.io.ci := fa2.io.co

 //Output behavior
    io.s0 := ha1.io.s
    io.s1 := fa1.io.s
    io.s2 := fa2.io.s
    io.s3 := fa3.io.s
    io.co3 := fa3.io.co
}
