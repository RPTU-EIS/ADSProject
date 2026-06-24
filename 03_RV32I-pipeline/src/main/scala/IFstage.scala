// ADS I Class Project
// Pipelined RISC-V Core - IF Stage
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
The Instruction Fetch (IF) stage is the first stage of the pipeline and handles instruction retrieval from memory.

Memory:
    IMem: instruction memory with 4096 32-bit unsigned integer entires, loaded from a binary file at compile time

Internal Registers:
    PC: 32-bit unsigned integer register, initialized to 0 holding the current program counter address

Internal Signals:
    none

Functionality:
    Fetch the instruction at the current PC (word-aligned addressing)
    Increment the PC (word-aligned) each clock cycle to fetch the next sequential instruction

Parameters:
    BinaryFile: String - path to the binary file to load into instruction memory

Inputs:
    none

Outputs:
    instr: send the fetched instruction to IF Barrier
*/

package core_tile

import chisel3._  // Import basic Chisel hardware types

// -----------------------------------------
// Fetch Stage
// -----------------------------------------

class IF (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    val instr = Output(UInt(32.W))  // Send the fetched instruction to the next pipeline stage
  })

  val PC = RegInit(0.U(32.W))  // Create the program counter and start it at address zero

  val instructionMemory = Module(new InstructionMemory(BinaryFile))  // Create the instruction memory module

  instructionMemory.io.address := PC  // Send the current PC address to instruction memory

  io.instr := instructionMemory.io.instruction  // Send fetched instruction to the IF barrier

  PC := PC + 4.U  // Move the PC to the next instruction every clock cycle
  
}
