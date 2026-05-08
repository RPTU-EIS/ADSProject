// ADS I Class Project
// Pipelined RISC-V Core - Register File
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

package core_tile

import chisel3._

/*
Register File Module: 32x32-bit dual-read single-write register file

Memory:
    regFile: Register file according to the RISC-V 32I specification

Ports:
    req_1, resp_1: first read port
        req_1.addr: read address for register x[0-31]
        resp_1.data: register data output
    req_2, resp_2: second read port
        req_2.addr: read address for register x[0-31]
        resp_2.data: register data output
    req_3: write port
        req_3.addr: write destination address
        req_3.data: data to write
        req_3.wr_en: write enable signal

Functionality:
    Two read ports allow simultaneous reading of two operands
    Synchronous write updates register if wr_en is asserted
*/

// -----------------------------------------
// Register File
// -----------------------------------------

class regFileReadReq extends Bundle {
    val addr = UInt(5.W)
}

class regFileReadResp extends Bundle {
    val data = UInt(32.W)
}

class regFileWriteReq extends Bundle {
    //ToDo: implement bundle for write request
    val addr = UInt(5.W)
    val data = UInt(32.W)
    val wr_en = Bool()
}

class regFile extends Module {
  val io = IO(new Bundle {
    //ToDo: Add I/O ports 
    val req_1 = Input(new regFileReadReq)
    val resp_1 = Output(new regFileReadResp)

    val req_2 = Input(new regFileReadReq)
    val resp_2 = Output(new regFileReadResp)

    val req_3 = Input(new regFileWriteReq) //Entire write request bundle taken as input
})

//ToDo: Add your implementation according to the specification above here 
    val registers = RegInit(VecInit(Seq.fill(32)(0.U(32.W)))) // 32 registers initialized to 0

    // Read ports
    io.resp_1.data := Mux(io.req_1.addr === 0.U, 0.U, registers(io.req_1.addr)) // Read data for first read port
    io.resp_2.data := Mux(io.req_2.addr === 0.U, 0.U, registers(io.req_2.addr)) // Read data for second read port

    // Write port
    when(io.req_3.wr_en && io.req_3.addr =/= 0.U) { // Check if write enable is asserted and address other than x0 is being written to
        registers(io.req_3.addr) := io.req_3.data // Write data to register file
    }
}
