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

//ToDo: Add your implementation according to the specification above here 
class IDbarrier extends Module {
  val io = IO(new Bundle {
    val inUOP = Input(uopc.Type())
    val inRD = Input(UInt(5.W))
    val inOperandA = Input(UInt(32.W))
    val inOperandB = Input(UInt(32.W))
    val inXcptInvalid = Input(Bool())

    val outUOP = Output(uopc.Type())
    val outRD = Output(UInt(5.W))
    val outOperandA = Output(UInt(32.W))
    val outOperandB = Output(UInt(32.W))
    val outXcptInvalid = Output(Bool())
  })

  val uopReg = RegInit(uopc.NOP)
  val rdReg = RegInit(0.U(5.W))
  val operandAReg = RegInit(0.U(32.W))
  val operandBReg = RegInit(0.U(32.W))
  val xcptInvalidReg = RegInit(false.B)

  uopReg := io.inUOP
  rdReg := io.inRD
  operandAReg := io.inOperandA
  operandBReg := io.inOperandB
  xcptInvalidReg := io.inXcptInvalid

  io.outUOP := uopReg
  io.outRD := rdReg
  io.outOperandA := operandAReg
  io.outOperandB := operandBReg
  io.outXcptInvalid := xcptInvalidReg
}