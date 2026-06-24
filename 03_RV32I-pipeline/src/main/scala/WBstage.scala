// ADS I Class Project
// Pipelined RISC-V Core - WB Stage
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)


/*
Writeback (WB) Stage: result storage and register file updates

Register File Interface:
    regFileReq: write request bundle
        regFileReq.addr: destination register index
        regFileReq.data: result value to write
        regFileReq.wr_en: write enable signal

Inputs:
    aluResult: computation result from pipeline
    rd: destination register address

Internal Signals:
    Result forwarding paths
    Write enable control

Functionality:
    Forward aluResult to register file write port
    Set write address to rd
    Assert wr_en = true for all R-type and I-type instructions
    Output result on check_res for verification and debugging

Outputs:
    check_res: result value for verification
*/

package core_tile

import chisel3._

// -----------------------------------------
// Writeback Stage
// -----------------------------------------

//ToDo: Add your implementation according to the specification above here 

class WB extends Module {
  val io = IO(new Bundle {
    // Inputs from MEM Barrier
    val aluResult   = Input(UInt(32.W))
    val rd          = Input(UInt(5.W))
    val exception   = Input(Bool())

    // Register file write port
    val regFileReq  = Output(new regFileWriteReq)

    // Output for verification/debugging
    val check_res   = Output(UInt(32.W))
    val outException = Output(Bool())
  })

  // --- Wire register file write request ---
  io.regFileReq.addr  := io.rd
  io.regFileReq.data  := io.aluResult
  // Only write if no exception — don't commit bad instructions
  io.regFileReq.wr_en := !io.exception

  // --- Debug/verification output ---
  io.check_res := io.aluResult
  io.outException := io.exception   
}