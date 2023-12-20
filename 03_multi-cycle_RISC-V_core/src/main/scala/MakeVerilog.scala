// ADS I Class Project
// Multi-Cycle RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 12/19/2023 by Tobias Jauch (@tojauch)

package makeverilog

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

import MultiCycleRV32I._


object Verilog_Gen extends App {
  emitVerilog(new MultiCycleRV32I("src/test/programs/BinaryFile"), Array("--target-dir", "generated-src"))
}
