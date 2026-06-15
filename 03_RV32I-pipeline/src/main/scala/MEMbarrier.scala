// ADS I Class Project
// Pipelined RISC-V Core - MEM Barrier
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
MEM-Barrier: pipeline register between Memory and Writeback stages

Internal Registers:
    aluResult: computation result (or future load data)
    rd: destination register index
    exception: exception flag

Inputs:
    inAluResult: result from MEM stage
    inRD: destination register from MEM stage
    inException: exception flag from MEM stage

Outputs:
    outAluResult: result to WB stage
    outRD: destination register to WB stage
    outException: exception flag to WB stage

Functionality:
    Save all input signals to a register and output them in the following clock cycle
*/

package core_tile

import chisel3._

// -----------------------------------------
// MEM-Barrier
// -----------------------------------------

class MEMBarrier extends Module {
  val io = IO(new Bundle {
    val inAluResult = Input(UInt(32.W))  // Get result from memory stage path
    val inRD        = Input(UInt(5.W))   // Get destination register
    val inException = Input(Bool())      // Get exception flag

    val outAluResult = Output(UInt(32.W))  // Send result to writeback stage
    val outRD        = Output(UInt(5.W))   // Send destination register to writeback stage
    val outException = Output(Bool())      // Send exception flag to writeback stage
  })

  val aluResultReg = RegInit(0.U(32.W))  // Register for result
  val rdReg        = RegInit(0.U(5.W))   // Register for destination register
  val exceptionReg = RegInit(false.B)    // Register for exception flag

  aluResultReg := io.inAluResult  // Store result
  rdReg        := io.inRD         // Store destination register
  exceptionReg := io.inException  // Store exception flag

  io.outAluResult := aluResultReg  // Output stored result
  io.outRD        := rdReg         // Output stored destination register
  io.outException := exceptionReg  // Output stored exception flag
}
