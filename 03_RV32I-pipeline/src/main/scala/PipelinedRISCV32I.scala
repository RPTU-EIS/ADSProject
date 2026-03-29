package PipelinedRV32I
import chisel3._; import core_tile._

class PipelinedRV32I(BinaryFile: String, useBTB: Boolean = false) extends Module {
  val io = IO(new Bundle {
    val result = Output(UInt(32.W));
    val exception = Output(Bool())
  })

  val core = Module(new PipelinedRV32Icore(BinaryFile, useBTB))
  io.result := core.io.check_res;
  io.exception := core.io.exception
}
