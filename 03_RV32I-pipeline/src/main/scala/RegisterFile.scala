// ADS I Class Project
// Pipelined RISC-V Core - Register File
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

package core_tile

import chisel3._  // Import the basic Chisel hardware types

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
  val addr = UInt(5.W)  // Register address that needs to be read
}

class regFileReadResp extends Bundle {
  val data = UInt(32.W) // Data read from the register file
}

class regFileWriteReq extends Bundle {
  val addr  = UInt(5.W)   // Register address that needs to be written
  val data  = UInt(32.W)  // Data that will be written
  val wr_en = Bool()      // Write enable signal
}

class regFile extends Module {
  val io = IO(new Bundle {
    val req_1  = Input(new regFileReadReq)    // First read request
    val resp_1 = Output(new regFileReadResp)  // First read response
    val req_2  = Input(new regFileReadReq)    // Second read request
    val resp_2 = Output(new regFileReadResp)  // Second read response
    val req_3  = Input(new regFileWriteReq)   // Write request
  })

  val registers = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))  // Create 32 registers initialized to zero

  io.resp_1.data := Mux(io.req_1.addr === 0.U, 0.U, registers(io.req_1.addr))  // Read port 1, but x0 is always zero
  io.resp_2.data := Mux(io.req_2.addr === 0.U, 0.U, registers(io.req_2.addr))  // Read port 2, but x0 is always zero

  when(io.req_3.wr_en && io.req_3.addr =/= 0.U) {
    registers(io.req_3.addr) := io.req_3.data  // Write data only if write is enabled and register is not x0
  }

}
