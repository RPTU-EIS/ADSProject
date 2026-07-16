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
    val c_o = Output(UInt(1.W))
  })

  io.s  := io.a ^ io.b
  io.c_o := io.a & io.b
}

/**
 * Full Adder Class
 */
class FullAdder extends Module {

  val io = IO(new Bundle {
    val a   = Input(UInt(1.W))
    val b   = Input(UInt(1.W))
    val c_i = Input(UInt(1.W))
    val s   = Output(UInt(1.W))
    val c_o  = Output(UInt(1.W))
  })

  val HA1 = Module(new HalfAdder())
  val HA2 = Module(new HalfAdder())

  HA1.io.a := io.a
  HA1.io.b := io.b

  HA2.io.a := HA1.io.s
  HA2.io.b := io.c_i

  io.s  := HA2.io.s
  io.c_o := HA1.io.c_o ^ HA2.io.c_o
}

/**
 * 4-bit Adder (to be c_ompleted)
 */
class FourBitAdder extends Module {

  val io = IO(new Bundle {
    val a  = Input(UInt(4.W))
    val b  = Input(UInt(4.W))
    val s  = Output(UInt(4.W))
    val c_o = Output(UInt(1.W))
  })

  val ha0 = Module(new HalfAdder())
  val fa1 = Module(new FullAdder())
  val fa2 = Module(new FullAdder())
  val fa3 = Module(new FullAdder())

  // bit 0
  ha0.io.a := io.a(0)
  ha0.io.b := io.b(0)

  // bit 1
  fa1.io.a := io.a(1)
  fa1.io.b := io.b(1)
  fa1.io.c_i := ha0.io.c_o

  // bit 2
  fa2.io.a := io.a(2)
  fa2.io.b := io.b(2)
  fa2.io.c_i := fa1.io.c_o

  // bit 3
  fa3.io.a := io.a(3)
  fa3.io.b := io.b(3)
  fa3.io.c_i := fa2.io.c_o

  io.s := Cat(fa3.io.s, fa2.io.s, fa1.io.s, ha0.io.s)
  io.c_o := fa3.io.c_o
}