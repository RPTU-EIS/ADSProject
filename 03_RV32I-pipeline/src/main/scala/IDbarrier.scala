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

class ID extends Module{
    val io = IO(new Bundle{
        val uop_in          = Input(Op())
        val XcptInvalid_in  = Input(Bool())
        val rd_in           = Input(UInt(5.W))
        val operandA_in     = Input(UInt(32.W))
        val operandB_in     = Input(UInt(32.W))
        
        val uop_out         = Output(Op())
        val XcptInvalid_out = Output(Bool())
        val rd_out          = Output(UInt(5.W))
        val operandA_out    = Output(UInt(32.W))
        val operandB_out    = Output(UInt(32.W))
    })

//ToDo: Add your implementation according to the specification above here 

    val uop_reg      = RegInit(Op.INVALID)
    val XcptInvalid  = RegInit(0.U(1.W))
    val rd_reg       = RegInit(0.U(5.W))
    val operandA_reg = RegInit(0.U(32.W))
    val operandB_reg = RegInit(0.U(32.W))

    uop_reg      := io.uop_in
    XcptInvalid  := io.XcptInvalid_in
    rd_reg       := io.rd_in
    operandA_reg := io.operandA_in
    operandB_reg := io.operandB_in

    io.uop_out          := uop_reg
    io.XcptInvalid_out  := XcptInvalid
    io.rd_out           := rd_reg
    io.operandA_out     := operandA_reg
    io.operandB_out     := operandB_reg
}