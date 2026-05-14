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
    val inUOP = Input(UInt(7.W))
    val inRD = Input(UInt(5.W))
    val inrs1 = Input(UInt(5.W))
    val inrs2 = Input(UInt(5.W))
    val inOperandA = Input(UInt(32.W))
    val inOperandB = Input(UInt(32.W))
    val inXcptInvalid = Input(Bool())

    val outUOP = Output(UInt(7.W))
    val outRD = Output(UInt(5.W))
    val outrs1 = Output(UInt(5.W))
    val outrs2 = Output(UInt(5.W))
    val outOperandA = Output(UInt(32.W))
    val outOperandB = Output(UInt(32.W))
    val outXcptInvalid = Output(Bool())
  })

  val uopReg = RegInit(0.U(7.W)) // Assuming 0 is NOP
  val rd = RegInit(0.U(5.W))
  val rs1 = RegInit(0.U(5.W))
  val rs2 = RegInit(0.U(5.W))
  val operandA = RegInit(0.U(32.W))
  val operandB = RegInit(0.U(32.W))
  val xcptInvalid = RegInit(false.B)

  uopReg := io.inUOP
  operandA := io.inOperandA
  operandB := io.inOperandB
  rd := io.inRD
  rs1 := io.inrs1
  rs2 := io.inrs2
  xcptInvalid := io.inXcptInvalid

  io.outUOP := uopReg
  io.outOperandA := operandA
  io.outOperandB := operandB
  io.outRD := rd
  io.outrs1 := rs1
  io.outrs2 := rs2
  io.outXcptInvalid := xcptInvalid
  
//RegNext means: "Create a register, feed this input into it, initialize it to this default value, and connect it to this output"
//   io.outUOP         := RegNext(io.inUOP, 0.U)
//   io.outRD          := RegNext(io.inRD, 0.U)
//   io.outOperandA    := RegNext(io.inOperandA, 0.U)
//   io.outOperandB    := RegNext(io.inOperandB, 0.U)
//   io.outXcptInvalid := RegNext(io.inXcptInvalid, false.B)
}
//ToDo: Add your implementation according to the specification above here 
