// ADS I Class Project
// Pipelined RISC-V Core - Register File
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)
// Modified for Assignment 04: Added write-first bypass for same-cycle read/write

package core_tile

import chisel3._

// -----------------------------------------
// Register File
// -----------------------------------------

class regFileReadReq extends Bundle {
    val addr = Input(UInt(5.W))
}

class regFileReadResp extends Bundle {
    val data = Output(UInt(32.W))
}

class regFileWriteReq extends Bundle {
    val addr  = Input(UInt(5.W))
    val data  = Input(UInt(32.W))
    val wr_en = Input(Bool())
}

class regFile extends Module {
  val io = IO(new Bundle {
    val req_1  = new regFileReadReq
    val resp_1 = new regFileReadResp
    val req_2  = new regFileReadReq
    val resp_2 = new regFileReadResp
    val req_3  = new regFileWriteReq
  })

  val registers = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))

  // =========================================================================
  // Write-first bypass:
  // If WB writes to a register in the same cycle that ID reads it,
  // the combinational read would return the OLD value. This bypass
  // forwards the write data directly, ensuring the ID stage gets
  // the most recent value. This handles the case where an instruction
  // 3 stages ahead (now in WB) writes to a register that the current
  // instruction in ID reads — a case NOT covered by the forwarding unit.
  // =========================================================================

  // Read port 1 with bypass
  io.resp_1.data := Mux(io.req_1.addr === 0.U, 0.U,
    Mux(io.req_3.wr_en && io.req_3.addr =/= 0.U && io.req_3.addr === io.req_1.addr,
      io.req_3.data,           // Bypass: use write data
      registers(io.req_1.addr) // Normal: use register value
    ))

  // Read port 2 with bypass
  io.resp_2.data := Mux(io.req_2.addr === 0.U, 0.U,
    Mux(io.req_3.wr_en && io.req_3.addr =/= 0.U && io.req_3.addr === io.req_2.addr,
      io.req_3.data,           // Bypass: use write data
      registers(io.req_2.addr) // Normal: use register value
    ))

  // Write port
  when(io.req_3.wr_en && io.req_3.addr =/= 0.U) {
    registers(io.req_3.addr) := io.req_3.data
  }
}
