// ADS I Class Project
// Pipelined RISC-V Core - EX Barrier
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
EX-Barrier: pipeline register between Execute and Memory stages

Internal Registers:
    aluResult: ALU computation result
    rd: destination register index
    exception: exception flag

Inputs:
    inAluResult: computation result from EX stage
    inRD: destination register from EX stage
    inXcptInvalid: exception flag from EX stage

Outputs:
    outAluResult: result to MEM stage
    outRD: destination register to MEM stage
    outXcptInvalid: exception flag to MEM stage

Functionality:
    Save all input signals to a register and output them in the following clock cycle
*/

package core_tile

import chisel3._

// -----------------------------------------
// EX-Barrier
// -----------------------------------------

class EXBarrier extends Module {
  val io = IO(new Bundle {
    val inAluResult   = Input(UInt(32.W))  // Get ALU result from EX stage
    val inRD          = Input(UInt(5.W))   // Get destination register from EX stage
    val inXcptInvalid = Input(Bool())      // Get invalid flag from EX stage

    val outAluResult   = Output(UInt(32.W))  // Send ALU result to MEM stage
    val outRD          = Output(UInt(5.W))   // Send destination register to MEM stage
    val outXcptInvalid = Output(Bool())      // Send invalid flag to MEM stage
  })

  val aluResultReg = RegInit(0.U(32.W))  // Register for the ALU result
  val rdReg        = RegInit(0.U(5.W))   // Register for the destination register
  val invalidReg   = RegInit(false.B)    // Register for the invalid flag

  aluResultReg := io.inAluResult    // Store ALU result
  rdReg        := io.inRD           // Store destination register
  invalidReg   := io.inXcptInvalid  // Store invalid flag

  io.outAluResult   := aluResultReg  // Output stored ALU result
  io.outRD          := rdReg         // Output stored destination register
  io.outXcptInvalid := invalidReg    // Output stored invalid flag
}
