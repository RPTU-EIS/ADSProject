// ADS I Class Project
// Assignment 03: Pipelined RISC-V Core
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 11/01/2025

/*
  This file creates the Instruction Memory.
  The Program Counter gives a byte address, and this memory returns the
  32-bit instruction stored at that address.
*/

package core_tile

import chisel3._                                      // Import the basic Chisel hardware types
import chisel3.util.experimental.loadMemoryFromFile   // Import function to load a hex program file

// -----------------------------------------
// Instruction Memory
// -----------------------------------------

class InstructionMemory(BinaryFile: String, memDepth: Int = 4096) extends Module {
  val io = IO(new Bundle {
    val address     = Input(UInt(32.W))   // Get byte address from the PC
    val instruction = Output(UInt(32.W))  // Send the instruction stored in memory
  })

  val memory = Mem(memDepth, UInt(32.W))  // Create instruction memory with 32-bit words

  loadMemoryFromFile(memory, BinaryFile)  // Load the instructions from the program file

  io.instruction := memory(io.address(13, 2))  // Convert byte address to word address
}
