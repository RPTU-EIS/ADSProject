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
    val inInstr = Input(UInt(32.W)) // Input for the fetched instruction from IF stage
    val inPC = Input(UInt(32.W)) // Input for the current program counter from IF stage
    val flush = Input(Bool()) // Input for pipeline flush signal from EX stage (for branch misprediction recovery)

    val outInstr = Output(UInt(32.W)) // Output for the instruction to ID stage
    val outPC = Output(UInt(32.W)) // Output for the current program counter to ID stage
  })

//ToDo: Add your implementation according to the specification above here 
  val instrReg = RegInit(0.U(32.W)) // Register to hold instruction between pipeline stages, initialized to 0
  val pcReg = RegInit(0.U(32.W)) // Register to hold program counter between pipeline stages, initialized to 0

  when(io.flush) {
    instrReg := "h00000013".U(32.W) // If flush signal is set, clear the instruction to prevent incorrect execution
    pcReg := 0.U(32.W) // Clear the program counter
  } .otherwise {
    instrReg := io.inInstr // Save input instruction to register
    pcReg := io.inPC // Save input program counter to register
  }

  io.outInstr := instrReg // Output the instruction to ID stage in the following clock cycle
  io.outPC := pcReg // Output the program counter to ID stage in the following clock cycle
}
