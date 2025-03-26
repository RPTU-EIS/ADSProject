package makeverilog

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

import BranchTargetBuffer._


object Verilog_Gen extends App {
  emitVerilog(new BranchTargetBuffer(), Array("--target-dir", "generated-src"))
}
