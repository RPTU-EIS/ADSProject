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

//Start code

class ForwardingUnit extends Module {

  //We define the inputs and outputs of the Forwarding Unit

  val io = IO(new Bundle {

    //Source registers from the EX stage
    val rs1_EX    = Input(UInt(5.W))        // source register 1
    val rs2_EX    = Input(UInt(5.W))        // source register 2

    //Destination register and write enable from MEM barrier
    val rd_MEM    = Input(UInt(5.W))        // destination register in MEM
    val wrEn_MEM  = Input(Bool())           // write enable in MEM

    //Destination register and write enable from WB barrier
    val rd_WB     = Input(UInt(5.W))        // destination register in WB
    val wrEn_WB   = Input(Bool())           // write enable in WB

    //Control signals for the muxes in the EX stage
    val forwardA  = Output(UInt(2.W))       // mux control for operand A
    val forwardB  = Output(UInt(2.W))       // mux control for operand B
  })

  //We set default values for the outputs, no forwarding by default

  io.forwardA := 0.U                        // 00 = no forwarding
  io.forwardB := 0.U                        // 00 = no forwarding

  //We check forwarding from MEM, MEM has the most recent value so it takes priority

  when(io.wrEn_MEM && (io.rd_MEM =/= 0.U)) {

    //If rd_MEM matches rs1_EX, we forward to operand A
    when(io.rd_MEM === io.rs1_EX) {
      io.forwardA := 2.U                    // 10 = forward from MEM
    }

    //If rd_MEM matches rs2_EX, we forward to operand B
    when(io.rd_MEM === io.rs2_EX) {
      io.forwardB := 2.U                    // 10 = forward from MEM
    }
  }

  //We check forwarding from WB, only if MEM is not already forwarding the same register

  when(io.wrEn_WB && (io.rd_WB =/= 0.U)) {

    //Forward to operand A if WB matches rs1 and MEM does not match
    when((io.rd_WB === io.rs1_EX) && !(io.wrEn_MEM && (io.rd_MEM =/= 0.U) && (io.rd_MEM === io.rs1_EX))) {
      io.forwardA := 1.U                    // 01 = forward from WB
    }

    //Forward to operand B if WB matches rs2 and MEM does not match
    when((io.rd_WB === io.rs2_EX) && !(io.wrEn_MEM && (io.rd_MEM =/= 0.U) && (io.rd_MEM === io.rs2_EX))) {
      io.forwardB := 1.U                    // 01 = forward from WB
    }
  }

}