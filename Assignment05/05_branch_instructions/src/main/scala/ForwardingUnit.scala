// ADS I Class Project
// Pipelined RISC-V Core - Forwarding Unit
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 05/09/2026 by Tobias Jauch (@tojauch)

/*
Forwarding Unit: resolves data hazards by forwarding results from later
pipeline stages to the EX stage muxes
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

  val io = IO(new Bundle {

    //Source registers from the EX stage
    val rs1_EX    = Input(UInt(5.W))
    val rs2_EX    = Input(UInt(5.W))

    //Destination register and write enable from MEM barrier
    val rd_MEM    = Input(UInt(5.W))
    val wrEn_MEM  = Input(Bool())

    //Destination register and write enable from WB barrier
    val rd_WB     = Input(UInt(5.W))
    val wrEn_WB   = Input(Bool())

    //Control signals for the muxes in the EX stage
    val forwardA  = Output(UInt(2.W))
    val forwardB  = Output(UInt(2.W))
  })


  //We set default values, no forwarding by default
  io.forwardA := 0.U
  io.forwardB := 0.U


  //We check forwarding from MEM (highest priority, newer value)
  when(io.wrEn_MEM && (io.rd_MEM =/= 0.U)) {

    when(io.rd_MEM === io.rs1_EX) {
      io.forwardA := 2.U
    }

    when(io.rd_MEM === io.rs2_EX) {
      io.forwardB := 2.U
    }
  }


  //We check forwarding from WB (only if MEM does not already match)
  when(io.wrEn_WB && (io.rd_WB =/= 0.U)) {

    when((io.rd_WB === io.rs1_EX) && !(io.wrEn_MEM && (io.rd_MEM =/= 0.U) && (io.rd_MEM === io.rs1_EX))) {
      io.forwardA := 1.U
    }

    when((io.rd_WB === io.rs2_EX) && !(io.wrEn_MEM && (io.rd_MEM =/= 0.U) && (io.rd_MEM === io.rs2_EX))) {
      io.forwardB := 1.U
    }
  }

}