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
    val input1 = Input(UInt(1.W))
    val input2 = Input(UInt(1.W))
    val output = Output(UInt(1.W)) //sum
    val co     = Output(UInt(1.W)) //carry out
  })

  /* 
   * TODO: Describe output behaviour based on the input values
   */
    io.output:= io.input1 ^ io.input2 //XOR for sum A XOR B
    io.co    := io.input1 & io.input2 // AND for carry
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
    val input1 = Input(UInt(1.W))
    val input2 = Input(UInt(1.W))
    val ci     = Input(UInt(1.W)) //carry-in bit
    val output = Output(UInt(1.W))
    val co = Output(UInt(1.W)) //carry out bit
    })

//First Half Adder (a+b) giving intermediate sum ha1.io.output and ha1.io.co
// sum = a XOR b
// carry = a AND b
    val ha1 = Module (new HalfAdder)
    ha1.io.input1 := io.input1
    ha1.io.input2 := i o.input2

//Second Hald Adder
// sum = (a XOR b) XOR ci
// carry = (a XOR b) AND ci

    val ha2 = Module (new HalfAdder)
    ha2.io.input1 := ha1.io.output
    ha2.io.input2 := io.ci

 // Sum output ( output = input1 XOR input2 XOR ci )
    io.output := ha2.io.output
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
    /* 
     * TODO: Define IO ports of a 4-bit ripple-carry-adder as presented in the lecture
     */
    })

  /* 
   * TODO: Instanciate the full adders and one half adderbased on the previously defined classes
   */


  /* 
   * TODO: Describe output behaviour based on the input values and the internal 
   */
}
