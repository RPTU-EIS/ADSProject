// ADS I Class Project
// Pipelined RISC-V Core - ID Barrier
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
ID-Barrier: pipeline register between Decode and Execute stages

Internal Registers:
    uop: micro-operation code (from uopc enum)
    rd: destination register index, initialized to 0
    operandA: first source operand, initialized to 0
    operandB: second operand/immediate, initialized to 0

Inputs:
    inUOP: micro-operation code from ID stage
    inRD: destination register from ID stage
    inOperandA: first operand from ID stage
    inOperandB: second operand/immediate from ID stage
    inXcptInvalid: exception flag from ID stage

Outputs:
    outUOP: micro-operation code to EX stage
    outRD: destination register to EX stage
    outOperandA: first operand to EX stage
    outOperandB: second operand to EX stage
    outXcptInvalid: exception flag to EX stage
Functionality:
    Save all input signals to a register and output them in the following clock cycle
*/

package core_tile

import chisel3._
import uopc._

// -----------------------------------------
// ID-Barrier
// -----------------------------------------

class IDBarrier extends Module {
  val io = IO(new Bundle {
    val inUOP         = Input(uopc())      // Get decoded operation from ID stage
    val inRD          = Input(UInt(5.W))   // Get destination register from ID stage
    val inOperandA    = Input(UInt(32.W))  // Get first operand from ID stage
    val inOperandB    = Input(UInt(32.W))  // Get second operand from ID stage
    val inXcptInvalid = Input(Bool())      // Get invalid flag from ID stage

    val outUOP         = Output(uopc())      // Send decoded operation to EX stage
    val outRD          = Output(UInt(5.W))   // Send destination register to EX stage
    val outOperandA    = Output(UInt(32.W))  // Send first operand to EX stage
    val outOperandB    = Output(UInt(32.W))  // Send second operand to EX stage
    val outXcptInvalid = Output(Bool())      // Send invalid flag to EX stage
  })

  val uopReg       = RegInit(uopc.NOP)    // Register for the decoded operation
  val rdReg        = RegInit(0.U(5.W))    // Register for the destination register
  val operandAReg  = RegInit(0.U(32.W))   // Register for the first operand
  val operandBReg  = RegInit(0.U(32.W))   // Register for the second operand
  val invalidReg   = RegInit(false.B)     // Register for the invalid flag

  uopReg      := io.inUOP
  rdReg       := io.inRD
  operandAReg := io.inOperandA
  operandBReg := io.inOperandB
  invalidReg  := io.inXcptInvalid

  io.outUOP         := uopReg       // Output stored operation
  io.outRD          := rdReg        // Output stored destination
  io.outOperandA    := operandAReg  // Output stored first operand
  io.outOperandB    := operandBReg  // Output stored second operand
  io.outXcptInvalid := invalidReg   // Output stored invalid flag
}
