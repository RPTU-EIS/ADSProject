package adder

import chisel3._
import chisel3.util._

/**
 * Half Adder Class
 */
class HalfAdder extends Module {

    val io = IO(new Bundle {
    val a  = Input(UInt(1.W))
    val b  = Input(UInt(1.W))
    val s  = Output(UInt(1.W))
    val Co = Output(UInt(1.W))
  })

  io.s  := io.a ^ io.b
  io.Co := io.a & io.b
}

/**
 * Full Adder Class
 */
class FullAdder extends Module {

    val io = IO(new Bundle {
    val a   = Input(UInt(1.W))
    val b   = Input(UInt(1.W))
    val Cin = Input(UInt(1.W))
    val s   = Output(UInt(1.W))
    val Co  = Output(UInt(1.W))
  })

  val HA1 = Module(new HalfAdder())
  val HA2 = Module(new HalfAdder())

  HA1.io.a := io.a
  HA1.io.b := io.b

  HA2.io.a := HA1.io.s
  HA2.io.b := io.Cin

  io.s  := HA2.io.s
  io.Co := HA1.io.Co ^ HA2.io.Co
}

/**
 * 4-bit Adder (to be completed)
 */
class FourBitAdder extends Module {

  val io = IO(new Bundle {
    val a  = Input(UInt(4.W))
    val b  = Input(UInt(4.W))
    val s  = Output(UInt(4.W))
    val Co = Output(UInt(1.W))
  })

  val fa0 = Module(new FullAdder())
  val fa1 = Module(new FullAdder())
  val fa2 = Module(new FullAdder())
  val fa3 = Module(new FullAdder())

  // bit 0
  fa0.io.a := io.a(0)
  fa0.io.b := io.b(0)
  fa0.io.Cin := 0.U

  // bit 1
  fa1.io.a := io.a(1)
  fa1.io.b := io.b(1)
  fa1.io.Cin := fa0.io.Co

  // bit 2
  fa2.io.a := io.a(2)
  fa2.io.b := io.b(2)
  fa2.io.Cin := fa1.io.Co

  // bit 3
  fa3.io.a := io.a(3)
  fa3.io.b := io.b(3)
  fa3.io.Cin := fa2.io.Co

  io.s(0) := fa0.io.s
  io.s(1) := fa1.io.s
  io.s(2) := fa2.io.s
  io.s(3) := fa3.io.s

  io.Co := fa3.io.Co
}