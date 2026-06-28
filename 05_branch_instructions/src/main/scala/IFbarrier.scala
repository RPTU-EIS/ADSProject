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
    //ToDo: Add I/O ports
    val instrReg = Input(UInt(32.W))
    val inPC     = Input(UInt(32.W))
    val inFlush  = Input(Bool())

    val outInstr = Output(UInt(32.W))
    val outPC    = Output(UInt(32.W))
  })

  //ToDo: Add your implementation according to the specification above here
  val instrReg = RegInit(0.U(32.W))
  val pcReg    = RegInit(0.U(32.W))

  instrReg := io.instrReg
  pcReg    := io.inPC

  when(io.inFlush){
    io.outInstr := "h00000013".U     //NOP
  }.otherwise{
    io.outInstr := instrReg
  }
  io.outPC    := pcReg
}
