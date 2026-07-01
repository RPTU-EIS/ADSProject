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


Special Case for hazard resolution:    
    If a register is read and written in the same clock cycle, send the new data to data output!
*/

// -----------------------------------------
// Register File
// -----------------------------------------

class regFile extends Module {
  val io = IO(new Bundle {
    val req_1  = Input(new regFileReadReq)
    val resp_1 = Output(new regFileReadResp)
    val req_2  = Input(new regFileReadReq)
    val resp_2 = Output(new regFileReadResp)
    val req_3  = Input(new regFileWriteReq)
  })

  val regs = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))

  // Simultaneous read and write: read returns written value if addr matches (write-through forwarding)
  io.resp_1.data := Mux(io.req_1.addr === 0.U, 0.U,
                         Mux(io.req_3.wr_en && io.req_1.addr === io.req_3.addr,
                             io.req_3.data,
                             regs(io.req_1.addr)))

  io.resp_2.data := Mux(io.req_2.addr === 0.U, 0.U,
                         Mux(io.req_3.wr_en && io.req_2.addr === io.req_3.addr,
                             io.req_3.data,
                             regs(io.req_2.addr)))

  when(io.req_3.wr_en && io.req_3.addr =/= 0.U) {
    regs(io.req_3.addr) := io.req_3.data
  }
}
