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
import chisel3.experimental.ChiselEnum
import Assignment02.{ALU, ALUOp}

import uopc._

// -----------------------------------------
// ID-Barrier
// -----------------------------------------


class IDBarrier extends Module {
  val io = IO(new Bundle {
    //ToDo: Add I/O ports
    val inUOP = Input(ALUOp()) // check the uopc, no idea yet
    val inRD = Input(UInt(5.W))
    val inOperandA = Input(UInt(32.W))
    val inOperandB = Input(UInt(32.W))
    val inXcptInvalid = Input(UInt(1.W))

    val outUOP = Output(ALUOp()) // check the uopc, no idea yet
    val outRD = Output(UInt(5.W))
    val outOperandA = Output(UInt(32.W))
    val outOperandB = Output(UInt(32.W))
    val outXcptInvalid = Output(UInt(1.W))
  })

  val uop = RegInit(ALUOp()) // check the uopc, no idea yet
  val rd = RegInit(0.asUInt(5.W))
  val operandA = RegInit(0.asUInt(32.W))
  val operandB = RegInit(0.asUInt(32.W))
  val xcptInvalid = RegInit(0.asUInt(1.W))

  uop := io.inUOP
  rd := io.inRD
  operandA := io.inOperandA
  operandB := io.inOperandB
  xcptInvalid := io.inXcptInvalid

  io.outUOP := uop
  io.outRD := rd
  io.outOperandA := operandA
  io.outOperandB := operandB
  io.outXcptInvalid := xcptInvalid
}
//ToDo: Add your implementation according to the specification above here



