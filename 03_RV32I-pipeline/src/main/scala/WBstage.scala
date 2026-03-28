package core_tile
import chisel3._

class WB extends Module {
  val io = IO(new Bundle {
    val aluResult  = Input(UInt(32.W))
    val rd         = Input(UInt(5.W))
    val wrEn       = Input(Bool())
    val regFileReq = Output(new regFileWriteReq)
    val check_res  = Output(UInt(32.W))
  })
  io.regFileReq.addr  := io.rd
  io.regFileReq.data  := io.aluResult
  io.regFileReq.wr_en := io.wrEn     // controlled by pipeline (false for branches)
  io.check_res        := io.aluResult
}
