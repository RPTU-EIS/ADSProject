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
    // Operand source registers in EX stage
    val rs1_EX = Input(UInt(5.W))
    val rs2_EX = Input(UInt(5.W))

    // Destination registers and write enables from pipeline stages
    val rd_MEM   = Input(UInt(5.W))
    val wrEn_MEM = Input(Bool())
    val rd_WB    = Input(UInt(5.W))
    val wrEn_WB  = Input(Bool())

    // Forwarding control signals for muxes
    val forwardA = Output(UInt(2.W))  // 0: ID, 1: MEM, 2: WB
    val forwardB = Output(UInt(2.W))  // 0: ID, 1: MEM, 2: WB
  })

  // Forward operand A from MEM (priority 1) or WB (priority 2), or pass through from ID (0)
  io.forwardA := Mux(io.wrEn_MEM && (io.rs1_EX === io.rd_MEM) && (io.rs1_EX =/= 0.U), 1.U,
                     Mux(io.wrEn_WB && (io.rs1_EX === io.rd_WB) && (io.rs1_EX =/= 0.U), 2.U, 0.U))

  // Forward operand B from MEM (priority 1) or WB (priority 2), or pass through from ID (0)
  io.forwardB := Mux(io.wrEn_MEM && (io.rs2_EX === io.rd_MEM) && (io.rs2_EX =/= 0.U), 1.U,
                     Mux(io.wrEn_WB && (io.rs2_EX === io.rd_WB) && (io.rs2_EX =/= 0.U), 2.U, 0.U))
}