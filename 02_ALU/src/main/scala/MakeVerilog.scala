// ADS I Class Project
// Task 02: Arithmetic Logic Unit and UVM Testbench
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 05/21/2024 by Tobias Jauch (tobias.jauch@rptu.de)
// File updated on 09/21/2025 by Tharindu Samarakoon (gug75kex@rptu.de)

package makeverilog

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

import Assignment02._


object Verilog_Gen extends App {
  emitVerilog(new ALU, Array("--target-dir", "generated-src"))
}
