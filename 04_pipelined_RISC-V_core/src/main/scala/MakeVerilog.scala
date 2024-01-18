// ADS I Class Project
// Pipelined RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/15/2023 by Tobias Jauch (@tojauch)

package makeverilog

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

import PipelinedRV32I._


object Verilog_Gen extends App {
  emitVerilog(new PipelinedRV32I("src/test/programs/BinaryFile"), Array("--target-dir", "generated-src"))
}
