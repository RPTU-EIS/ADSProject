// ADS I Class Project
// Pipelined RISC-V Core - IF Barrier
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
IF-Barrier: pipeline register between Fetch and Decode stages

Internal Registers:
    instrReg: holds instruction between pipeline stages, initialized to 0

Inputs:
    inInstr: fetched instruction from IF stage

Outputs:
    outInstr: instruction to ID stage

Functionality:
    Save all input signals to a register and output them in the following clock cycle
*/

package core_tile

import chisel3._

// -----------------------------------------
// IF-Barrier
// -----------------------------------------

class IFBarrier extends Module {
  val io = IO(new Bundle {
    val InstrF    = Input(UInt(32.W))
    val PCF       = Input(UInt(32.W))
    val PCPlus4F  = Input(UInt(32.W))
    val CLR       = Input(Bool())

    val InstrD    = Output(UInt(32.W))
    val PCD       = Output(UInt(32.W))
    val PCPlus4D  = Output(UInt(32.W))
  })

  //ToDo: Add your implementation above here
  val instrReg   = RegInit(0.asUInt(32.W))
  val pcReg      = RegInit(0.asUInt(32.W))
  val pcPlus4Reg = RegInit(0.asUInt(32.W))

  when(io.CLR) {
    instrReg   := 19.U //NOP operation
    pcReg      := 0.U
    pcPlus4Reg := 0.U
  }.otherwise {
    instrReg   := io.InstrF
    pcReg      := io.PCF
    pcPlus4Reg := io.PCPlus4F
  }

  io.InstrD   := instrReg
  io.PCD      := pcReg
  io.PCPlus4D := pcPlus4Reg
}