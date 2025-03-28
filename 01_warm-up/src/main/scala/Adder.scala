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
    val sum = Output(UInt(1.W))
    val carry = Output(UInt(1.W))
    })

  /* 
   * TODO: Describe output behaviour based on the input values
   */
  
  // Define internal signals
  val SignalS = Wire(UInt(1.W))
  val SignalC_0 = Wire(UInt(1.W))

  SignalS := io.a ^ io.b    // XOR for sum
  SignalC_0 := io.a & io.b  // AND for carry

  // Connect internal signals to outputs
  io.sum := SignalS
  io.carry := SignalC_0

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
    val sum = Output(UInt(1.W))
    val ci = Input(UInt(1.W))
    val co = Output(UInt(1.W))
    })

  /* 
   * TODO: Instanciate the two half adders you want to use based on your HalfAdder class
   */

  val halfAdder1 = Module(new HalfAdder)
  val halfAdder2 = Module(new HalfAdder)

  /* 
   * TODO: Describe output behaviour based on the input values and the internal signals
   */

  // Connect inputs to the first Half Adder
  halfAdder1.io.a := io.a
  halfAdder1.io.b := io.b

  // Connect outputs of first Half Adder to inputs of the second
  halfAdder2.io.a := halfAdder1.io.sum
  halfAdder2.io.b := io.ci

  // Final outputs
  io.sum := halfAdder2.io.sum  // Final sum
  io.co := halfAdder1.io.carry | halfAdder2.io.carry  // Final carry-out

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

  /* 
   * TODO: Define IO ports of a 4-bit ripple-carry-adder as presented in the lecture
   */
  val io = IO(new Bundle {
    val a = Input(UInt(4.W))
    val b = Input(UInt(4.W))
    val sum = Output(UInt(4.W))
    val co = Output(UInt(1.W))
    })

  /* 
   * TODO: Instantiate the full adders and one half adderbased on the previously defined classes
   */

  val halfAdder = Module(new HalfAdder) // Instantitaion Halfadder
  val Adder1    = Module(new FullAdder) // Instantitaion Adder # 1
  val Adder2    = Module(new FullAdder) // Instantitaion Adder # 2
  val Adder3    = Module(new FullAdder) // Instantitaion Adder # 3

  /* 
   * TODO: Describe output behaviour based on the input values and the internal 
   */

  halfAdder.io.a := io.a(0)
  halfAdder.io.b := io.b(0)

  Adder1.io.a    := io.a(1)
  Adder1.io.b    := io.b(1)
  Adder1.io.ci   := halfAdder.io.carry

  Adder2.io.a    := io.a(2)
  Adder2.io.b    := io.b(2)
  Adder2.io.ci   := Adder1.io.co

  Adder3.io.a    := io.a(3)
  Adder3.io.b    := io.b(3)
  Adder3.io.ci   := Adder2.io.co

  // Connect the outputs
  io.sum := Cat(Adder3.io.sum, Adder2.io.sum, Adder1.io.sum, halfAdder.io.sum)
  io.co := Adder3.io.co

}
