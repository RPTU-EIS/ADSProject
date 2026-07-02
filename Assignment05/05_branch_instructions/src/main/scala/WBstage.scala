package core_tile

import chisel3._

// -----------------------------------------
// Write Back Stage
// -----------------------------------------

//Start code

//We define the WB stage, the last stage of the pipeline
//It takes the ALU result and saves it back into the Register File
//It also sends the result to the testbench for verification
class WB extends Module {

  val io = IO(new Bundle {

    //Inputs from MEM barrier
    val aluResult   = Input(UInt(32.W))
    val rd          = Input(UInt(5.W))
    val exception   = Input(Bool())

    //Write request to the Register File
    val regFileReq  = Output(new regFileWriteReq)

    //Outputs for verification
    val check_res    = Output(UInt(32.W))
    val outException = Output(Bool())
  })


  //We build the write request
  io.regFileReq.addr  := io.rd
  io.regFileReq.data  := io.aluResult

  //We only write if there is no exception
  io.regFileReq.wr_en := !io.exception


  //We send the verification outputs
  io.check_res    := io.aluResult
  io.outException := io.exception

}