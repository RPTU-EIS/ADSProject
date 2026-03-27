package PipelinedRV32I

import chisel3._
import chisel3.util._
import core_tile._

class PipelinedRV32I (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    val result    = Output(UInt(32.W))
    val exception = Output(Bool())
  })

  val core = Module(new PipelinedRV32Icore(BinaryFile))
  io.result    := core.io.check_res
  io.exception := core.io.exception
}
