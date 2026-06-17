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
    //ToDo: implement bundle for read request
}

class regFileReadResp extends Bundle {
    //ToDo: implement bundle for read response
}

class regFileWriteReq extends Bundle {
    //ToDo: implement bundle for write request
}

class regFile extends Module {
  val io = IO(new Bundle {
    //ToDo: Add I/O ports 
})

//ToDo: Add your implementation according to the specification above here 

}
