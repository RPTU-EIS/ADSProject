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
    val inInstr   = Input(UInt(32.W))
    val inPC      = Input(UInt(32.W))
    val flush     = Input(Bool())

    val outInstr  = Output(UInt(32.W))
    val outPC     = Output(UInt(32.W))
    val outKill   = Output(Bool())
  })

  val instrReg = RegInit(0.U(32.W))
  val pcReg    = RegInit(0.U(32.W))
  val killReg  = RegInit(false.B)

  instrReg := io.inInstr
  pcReg    := io.inPC
  killReg  := io.flush

  io.outInstr := instrReg
  io.outPC    := pcReg
  io.outKill  := killReg
}
