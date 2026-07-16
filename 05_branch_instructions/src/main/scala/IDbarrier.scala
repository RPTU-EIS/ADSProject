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
        val uopD          = Input(uopc())
        val rdD           = Input(UInt(5.W))
        val RD1D          = Input(UInt(32.W))
        val RD2D          = Input(UInt(32.W))
        val XcptInvalidD  = Input(Bool())
        val WriteEnableD     = Input(Bool())
        val ALUSrcD       = Input(Bool())
        val ImmExtD       = Input(UInt(32.W))
        val BranchD       = Input(Bool())
        val JumpD         = Input(Bool())
        val PCD           = Input(UInt(32.W))
        val PCPlus4D      = Input(UInt(32.W))
        val rs1D          = Input(UInt(5.W)) // for fordwarding unit
        val rs2D          = Input(UInt(5.W)) // for fordwarding unit
        val CLR           = Input(Bool())

        val Rs1E          = Output(UInt(5.W)) // for fordwarding unit
        val Rs2E          = Output(UInt(5.W)) // for fordwarding unit
        val uopE          = Output(uopc())
        val rdE           = Output(UInt(5.W))
        val RD1E          = Output(UInt(32.W))
        val RD2E          = Output(UInt(32.W))
        val XcptInvalidE  = Output(Bool())
        val WriteEnableE     = Output(Bool())
        val ALUSrcE       = Output(Bool())
        val ImmExtE       = Output(UInt(32.W))
        val BranchE       = Output(Bool())
        val JumpE         = Output(Bool())
        val PCE           = Output(UInt(32.W))
        val PCPlus4E      = Output(UInt(32.W))
    })

    //ToDo: Add your implementation according to the specification above here

    val uop          = RegInit(uopc.INVALID)
    val XcptInvalid  = RegInit(false.B)
    val rd           = RegInit(0.U(5.W))
    val RD1          = RegInit(0.U(32.W))
    val RD2          = RegInit(0.U(32.W))
    val WriteEnable     = RegInit(false.B)
    val ALUSrc       = RegInit(false.B)
    val ImmExt       = RegInit(0.U(32.W))
    val Branch       = RegInit(false.B)
    val Jump         = RegInit(false.B)
    val PC           = RegInit(0.U(32.W))
    val PCPlus4      = RegInit(0.U(32.W))
    val rs1Addr      = RegInit(0.U(5.W))   // rs1 address pipeline reg
    val rs2Addr      = RegInit(0.U(5.W))   // rs2 address pipeline reg

    when(io.CLR) {
        uop         := uopc.NOP
        XcptInvalid := false.B
        rd          := 0.U
        RD1         := 0.U
        RD2         := 0.U
        WriteEnable    := false.B
        ALUSrc      := false.B
        ImmExt      := 0.U
        Branch      := false.B
        Jump        := false.B
        PC          := 0.U
        PCPlus4     := 0.U
        rs1Addr     := 0.U
        rs2Addr     := 0.U
    }.otherwise {
        uop         := io.uopD
        rd          := io.rdD
        RD1         := io.RD1D
        RD2         := io.RD2D
        XcptInvalid := io.XcptInvalidD
        WriteEnable    := io.WriteEnableD
        ALUSrc      := io.ALUSrcD
        ImmExt      := io.ImmExtD
        Branch      := io.BranchD
        Jump        := io.JumpD
        PC          := io.PCD
        PCPlus4     := io.PCPlus4D
        rs1Addr     := io.rs1D // forwarding
        rs2Addr     := io.rs2D // forwarding
    }

    io.uopE         := uop
    io.XcptInvalidE := XcptInvalid
    io.rdE          := rd
    io.RD1E         := RD1
    io.RD2E         := RD2
    io.WriteEnableE    := WriteEnable
    io.ALUSrcE      := ALUSrc
    io.ImmExtE      := ImmExt
    io.BranchE      := Branch
    io.JumpE        := Jump
    io.PCE          := PC
    io.PCPlus4E     := PCPlus4
    io.Rs1E         := rs1Addr // forwarding
    io.Rs2E         := rs2Addr // forwarding
}