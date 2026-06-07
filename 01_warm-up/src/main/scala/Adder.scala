// ADS I Class Project
// Chisel Introduction

package adder // This file belongs to the adder package.

import chisel3._ // Import the basic Chisel hardware construction library.
import chisel3.util._ // Import Chisel utility functions such as Cat.

// HalfAdder adds two 1-bit inputs and produces a sum bit and a carry-out bit.
class HalfAdder extends Module { // Define a hardware module called HalfAdder.

  val io = IO(new Bundle { // Define the input/output interface of the module.
    val a  = Input(UInt(1.W)) // First 1-bit unsigned input.
    val b  = Input(UInt(1.W)) // Second 1-bit unsigned input.
    val s  = Output(UInt(1.W)) // 1-bit sum output.
    val co = Output(UInt(1.W)) // 1-bit carry-out output.
  }) // End of the IO bundle.

  io.s  := io.a ^ io.b // Sum is XOR: it is 1 when a and b are different.
  io.co := io.a & io.b // Carry-out is AND: it is 1 only when both inputs are 1.
} // End of HalfAdder.

// FullAdder adds two 1-bit inputs plus a carry-in.
class FullAdder extends Module { // Define a hardware module called FullAdder.

  val io = IO(new Bundle { // Define the input/output interface of the full adder.
    val a  = Input(UInt(1.W)) // First 1-bit unsigned input.
    val b  = Input(UInt(1.W)) // Second 1-bit unsigned input.
    val ci = Input(UInt(1.W)) // 1-bit carry-in input.
    val s  = Output(UInt(1.W)) // 1-bit sum output.
    val co = Output(UInt(1.W)) // 1-bit carry-out output.
  }) // End of the IO bundle.

  val ha1 = Module(new HalfAdder()) // Instantiate the first half adder.
  val ha2 = Module(new HalfAdder()) // Instantiate the second half adder.

  ha1.io.a := io.a // Connect input a to the first half adder.
  ha1.io.b := io.b // Connect input b to the first half adder.

  ha2.io.a := ha1.io.s // Connect the sum of the first half adder to the second half adder.
  ha2.io.b := io.ci // Connect the carry-in to the second half adder.

  io.s  := ha2.io.s // The final sum is the sum output of the second half adder.
  io.co := ha1.io.co | ha2.io.co // The final carry is true if either half adder produces a carry.
} // End of FullAdder.

// FourBitAdder adds two 4-bit unsigned values using a ripple-carry structure.
class FourBitAdder extends Module { // Define a hardware module called FourBitAdder.

  val io = IO(new Bundle { // Define the input/output interface of the 4-bit adder.
    val a  = Input(UInt(4.W)) // First 4-bit unsigned input.
    val b  = Input(UInt(4.W)) // Second 4-bit unsigned input.
    val s  = Output(UInt(4.W)) // 4-bit sum output.
    val co = Output(UInt(1.W)) // Final carry-out output.
  }) // End of the IO bundle.

  val ha  = Module(new HalfAdder()) // Instantiate one half adder for bit 0.
  val fa1 = Module(new FullAdder()) // Instantiate one full adder for bit 1.
  val fa2 = Module(new FullAdder()) // Instantiate one full adder for bit 2.
  val fa3 = Module(new FullAdder()) // Instantiate one full adder for bit 3.

  ha.io.a := io.a(0) // Connect bit 0 of input a to the half adder.
  ha.io.b := io.b(0) // Connect bit 0 of input b to the half adder.

  fa1.io.a  := io.a(1) // Connect bit 1 of input a to the first full adder.
  fa1.io.b  := io.b(1) // Connect bit 1 of input b to the first full adder.
  fa1.io.ci := ha.io.co // Carry-in of bit 1 comes from the carry-out of bit 0.

  fa2.io.a  := io.a(2) // Connect bit 2 of input a to the second full adder.
  fa2.io.b  := io.b(2) // Connect bit 2 of input b to the second full adder.
  fa2.io.ci := fa1.io.co // Carry-in of bit 2 comes from the carry-out of bit 1.

  fa3.io.a  := io.a(3) // Connect bit 3 of input a to the third full adder.
  fa3.io.b  := io.b(3) // Connect bit 3 of input b to the third full adder.
  fa3.io.ci := fa2.io.co // Carry-in of bit 3 comes from the carry-out of bit 2.

  io.s  := Cat(fa3.io.s, fa2.io.s, fa1.io.s, ha.io.s) // Concatenate all sum bits into one 4-bit output.
  io.co := fa3.io.co // The final carry-out is the carry-out of the most significant bit.
} // End of FourBitAdder.
