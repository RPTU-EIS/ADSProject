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
    val inInstr  = Input(UInt(32.W))   // Get the instruction from fetch stage
    val outInstr = Output(UInt(32.W))  // Send the instruction to decode stage
  })

  val instrReg = RegInit(0.U(32.W))  // Register used to save the instruction one cycle

  instrReg := io.inInstr  // Store the fetched instruction
  io.outInstr := instrReg // Output the stored instruction

}
