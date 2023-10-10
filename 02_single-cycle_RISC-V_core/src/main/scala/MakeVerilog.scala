// ADS I Class Project
// Single-Cycle RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 05/10/2023 by Tobias Jauch (@tojauch)

package makeverilog

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

import SimpleRV32I._


object Verilog_Gen extends App {
  emitVerilog(new SimpleRV32I("src/test/programs/BinaryFile"), Array("--target-dir", "generated-src"))
}
