package makeverilog

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import PipelinedRV32I._

object Verilog_Gen extends App {
  emitVerilog(new PipelinedRV32I("src/test/programs/BinaryFile_forwarding"), Array("--target-dir", "generated-src"))
}
