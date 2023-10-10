// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package makeverilog

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

import adder._
import basicadder._
import readserial._


object Verilog_Gen extends App {
  emitVerilog(new BasicAdder(), Array("--target-dir", "generated-src"))
  //emitVerilog(new HalfAdder(), Array("--target-dir", "generated-src"))
  //emitVerilog(new FullAdder(), Array("--target-dir", "generated-src"))
  //emitVerilog(new FourBitAdder(), Array("--target-dir", "generated-src"))
  //emitVerilog(new ReadSerialAdder(), Array("--target-dir", "generated-src"))
}
