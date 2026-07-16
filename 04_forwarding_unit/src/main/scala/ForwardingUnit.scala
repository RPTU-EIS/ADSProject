// ADS I Class Project
// Pipelined RISC-V Core - Forwarding Unit
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 05/09/2026 by Tobias Jauch (@tojauch)

/*
Forwarding Unit: resolves data hazards by forwarding results from later pipeline stages to the ID stage

Functionality (cf. slide 6-24ff of the lecture slides):
    Detects data hazards by comparing source registers in the EX stage with destination registers in MEM and WB stages (EX and MEM barriers).
    Generates control signals for the multiplexers in the EX stage to select the correct data source for the ALU inputs
    Handles cases where multiple hazards occur simultaneously (e.g., forwarding from both MEM and WB stages)

Inputs:
    rs1_EX: source register 1 in EX stage
    rs2_EX: source register 2 in EX stage
    rd_MEM: destination register in MEM stage
    rd_WB: destination register in WB stage
    wrEn_MEM: write enable signal for MEM stage
    wrEn_WB: write enable signal for WB stage

Outputs:
    forwardA: control signal for selecting source of operand A in EX stage
    forwardB: control signal for selecting source of operand B in EX stage

*/

package core_tile

import chisel3._
import chisel3.util._
import uopc._

// -----------------------------------------
// Forwarding Unit
// -----------------------------------------

class ForwardingUnit extends Module {
  val io = IO(new Bundle {
    val rs1_EX    = Input(UInt(32.W))
    val rs2_EX    = Input(UInt(32.W))
    val rd_MEM    = Input(UInt(32.W))
    val rd_WB     = Input(UInt(32.W))
    val wrEn_MEM  = Input(Bool())
    val wrEn_WB   = Input(Bool())

    val forwardA   = Output(UInt(2.W))
    val forwardB   = Output(UInt(2.W))
    // Add I/O ports according to the specification above here
  })

  // forwardA
  when((io.rs1_EX === io.rd_MEM) && io.wrEn_MEM && (io.rs1_EX =/= 0.U)) {
    io.forwardA := "b10".U // MEM
  }.elsewhen((io.rs1_EX === io.rd_WB) && io.wrEn_WB && (io.rs1_EX =/= 0.U)) {
    io.forwardA := "b01".U // WB
  }.otherwise {
    io.forwardA := "b00".U // No forward
  }

  // forwardB
  when((io.rs2_EX === io.rd_MEM) && io.wrEn_MEM && (io.rs2_EX =/= 0.U)) {
    io.forwardB := "b10".U // MEM
  }.elsewhen((io.rs2_EX === io.rd_WB) && io.wrEn_WB && (io.rs2_EX =/= 0.U)) {
    io.forwardB := "b01".U // WB
  }.otherwise {
    io.forwardB := "b00".U // No forward
  }
  //ToDo: Add your implementation according to the specification above here 

}