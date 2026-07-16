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
        regFileReq.w_en: write enable signal

Inputs:
    aluResult: computation result from pipeline
    rd: destination register address

Internal Signals:
    Result forwarding paths
    Write enable control

Functionality:
    Forward aluResult to register file write port
    Set write address to rd
    Assert w_en = true for all R-type and I-type instructions
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

class WBstage extends Module {
  val io = IO(new Bundle {
    val aluResult  = Input(UInt(32.W))
    val rd         = Input(UInt(5.W))
    val wrten      = Input(Bool())

    val regFileReq = new Bundle {
      val addr = Output(UInt(5.W))
      val data = Output(UInt(32.W))
      val w_en = Output(Bool())
    }
    val check_res = Output(UInt(32.W))
  })
  io.regFileReq.addr := io.rd
  io.regFileReq.data := io.aluResult
  io.regFileReq.w_en := io.wrten
  io.check_res       := io.aluResult
}