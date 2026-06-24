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

import chisel3._  // Import the basic Chisel hardware types

// -----------------------------------------
// Writeback Stage
// -----------------------------------------

class WB extends Module {
  val io = IO(new Bundle {
    val aluResult = Input(UInt(32.W))  // Get the final ALU result
    val rd        = Input(UInt(5.W))   // Get the destination register
    val exception = Input(Bool())      // Get the exception flag

    val regFileReq = Output(new regFileWriteReq)  // Send write request to register file
    val check_res  = Output(UInt(32.W))           // Send result to the testbench
  })

  io.regFileReq.addr  := io.rd         // Select which register will be written
  io.regFileReq.data  := io.aluResult  // Select the data that will be written
  io.regFileReq.wr_en := !io.exception // Write only if there is no exception

  io.check_res := io.aluResult  // Show the result outside for testing
}
