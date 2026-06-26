package core_tile

import chisel3._

// -----------------------------------------
// Register File with internal forwarding
// -----------------------------------------

//Start code

//We define the bundle for the read request, only needs the address

class regFileReadReq extends Bundle {
  val addr = UInt(5.W)                       // register address, 5 bits
}

//We define the bundle for the read response, returns the data

class regFileReadResp extends Bundle {
  val data = UInt(32.W)                      // register value, 32 bits
}

//We define the bundle for the write request, needs address, data and enable

class regFileWriteReq extends Bundle {
  val addr  = UInt(5.W)                      // destination register, 5 bits
  val data  = UInt(32.W)                     // value to write, 32 bits
  val wr_en = Bool()                         // write enable
}


//We define the Register File module, 32 registers of 32 bits, 2 read ports and 1 write port

class regFile extends Module {

  //We define the inputs and outputs of the Register File

  val io = IO(new Bundle {

    //Read port 1 for rs1
    val req_1  = Input(new regFileReadReq)
    val resp_1 = Output(new regFileReadResp)

    //Read port 2 for rs2
    val req_2  = Input(new regFileReadReq)
    val resp_2 = Output(new regFileReadResp)

    //Write port from WB stage
    val req_3  = Input(new regFileWriteReq)
  })

  //We create the 32 registers, all initialized to 0

  val registers = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))


  //We check internal forwarding for read port 1
  //If WB is writing the same register that ID is reading in the same cycle, 
  //we return the NEW value coming from WB instead of the OLD one stored in the register

  when(io.req_1.addr === 0.U) {
    io.resp_1.data := 0.U                                          // x0 is always 0
  }.elsewhen(io.req_3.wr_en && (io.req_3.addr === io.req_1.addr) && (io.req_3.addr =/= 0.U)) {
    io.resp_1.data := io.req_3.data                                // forward new value from WB (slide 6-24 fix)
  }.otherwise {
    io.resp_1.data := registers(io.req_1.addr)                     // return the stored value
  }


  //We check internal forwarding for read port 2
  //Same logic as read port 1, if WB writes the same register we read, return the new value

  when(io.req_2.addr === 0.U) {
    io.resp_2.data := 0.U                                          // x0 is always 0
  }.elsewhen(io.req_3.wr_en && (io.req_3.addr === io.req_2.addr) && (io.req_3.addr =/= 0.U)) {
    io.resp_2.data := io.req_3.data                                // forward new value from WB (slide 6-24 fix)
  }.otherwise {
    io.resp_2.data := registers(io.req_2.addr)                     // return the stored value
  }


  //We write to the register file, only if wr_en is true and the address is not 0

  when(io.req_3.wr_en && io.req_3.addr =/= 0.U) {
    registers(io.req_3.addr) := io.req_3.data                      // save the value
  }

}