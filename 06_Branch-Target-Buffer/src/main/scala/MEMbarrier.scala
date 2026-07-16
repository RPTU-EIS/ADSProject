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

class MEMBarrier extends Module{
    val io = IO(new Bundle {
        val ALUResultM   = Input(UInt(32.W))
        val rdM          = Input(UInt(5.W))
        val XcptInvalidM = Input(Bool())
        val WriteEnableM    = Input(Bool())

        val ALUResultW   = Output(UInt(32.W))
        val rdW          = Output(UInt(5.W))
        val XcptInvalidW = Output(Bool())
        val WriteEnableW    = Output(Bool())
    })

    val ALUResult   = RegInit(0.U(32.W))
    val rd          = RegInit(0.U(5.W))
    val XcptInvalid = RegInit(false.B)
    val WriteEnable    = RegInit(false.B)

    ALUResult   := io.ALUResultM
    rd          := io.rdM
    XcptInvalid := io.XcptInvalidM
    WriteEnable    := io.WriteEnableM

    io.ALUResultW   := ALUResult
    io.rdW          := rd
    io.XcptInvalidW := XcptInvalid
    io.WriteEnableW := WriteEnable
}