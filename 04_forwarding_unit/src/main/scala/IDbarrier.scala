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

class IDBarrier extends Module{
    val io = IO(new Bundle{
        val inUOP          = Input(uopc())
        val inRD           = Input(UInt(5.W))
        val inOperandA     = Input(UInt(32.W))
        val inOperandB     = Input(UInt(32.W))
        val inXcptInvalid  = Input(Bool())
        val inWrten        = Input(Bool())
        val inALUsrc       = Input(Bool())
        val inImmExtnd     = Input(UInt(32.W))
        val rs1_ID         = Input(UInt(5.W)) // for fordwarding unit
        val rs2_ID         = Input(UInt(5.W)) // for fordwarding unit

        val rs1_EX         = Output(UInt(5.W)) // for fordwarding unit
        val rs2_EX         = Output(UInt(5.W)) // for fordwarding unit
        val outUOP         = Output(uopc())
        val outRD          = Output(UInt(5.W))
        val outOperandA    = Output(UInt(32.W))
        val outOperandB    = Output(UInt(32.W))
        val outXcptInvalid = Output(Bool())
        val outWrten       = Output(Bool())
        val outALUsrc      = Output(Bool())
        val outImmExtnd    = Output(UInt(32.W))
    })

//ToDo: Add your implementation according to the specification above here

    val uop      = RegInit(uopc.INVALID)
    val XcptInvalid  = RegInit(false.B)
    val rd       = RegInit(0.U(5.W))
    val operandA = RegInit(0.U(32.W))
    val operandB = RegInit(0.U(32.W))
    val wrten       = RegInit(false.B)
    val ALUsrc      = RegInit(false.B)
    val immExtnd    = RegInit(0.U(32.W))
    val rs1Addr     = RegInit(0.U(5.W))   // rs1 address pipeline reg
    val rs2Addr     = RegInit(0.U(5.W))   // rs2 address pipeline reg

    uop         := io.inUOP
    rd          := io.inRD
    operandA    := io.inOperandA
    operandB    := io.inOperandB
    XcptInvalid := io.inXcptInvalid
    wrten       := io.inWrten
    ALUsrc      := io.inALUsrc
    immExtnd    := io.inImmExtnd
    rs1Addr     := io.rs1_ID // forwarding
    rs2Addr     := io.rs2_ID // forwarding


    io.outUOP          := uop
    io.outXcptInvalid  := XcptInvalid
    io.outRD           := rd
    io.outOperandA     := operandA
    io.outOperandB     := operandB
    io.outWrten        := wrten
    io.outALUsrc       := ALUsrc
    io.outImmExtnd     := immExtnd
    io.rs1_EX          := rs1Addr // forwarding
    io.rs2_EX          := rs2Addr // forwarding
}