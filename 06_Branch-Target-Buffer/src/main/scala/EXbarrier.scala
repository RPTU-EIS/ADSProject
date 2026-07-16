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
    val ALUResultE   = Input(UInt(32.W))
    val rdE          = Input(UInt(5.W))
    val XcptInvalidE = Input(Bool())
    val WriteEnableE    = Input(Bool())

    val ALUResultM   = Output(UInt(32.W))
    val rdM          = Output(UInt(5.W))
    val XcptInvalidM = Output(Bool())
    val WriteEnableM    = Output(Bool())
  })

  val ALUResult   = RegInit(0.U(32.W))
  val rd          = RegInit(0.U(5.W))
  val XcptInvalid = RegInit(false.B)
  val WriteEnable = RegInit(false.B)

  ALUResult   := io.ALUResultE
  rd          := io.rdE
  XcptInvalid := io.XcptInvalidE
  WriteEnable := io.WriteEnableE

  io.ALUResultM   := ALUResult
  io.rdM          := rd
  io.XcptInvalidM := XcptInvalid
  io.WriteEnableM := WriteEnable
}