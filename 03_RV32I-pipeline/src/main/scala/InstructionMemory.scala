// ADS I Class Project
// Assignment 03: Pipelined RISC-V Core
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 11/01/2025

/*
This file is for the Instruction Memory.
The Instruction Memory receives the PC address and returns the instruction saved in that address.
*/

package core_tile

import chisel3._                                      // Import the basic Chisel hardware tools
import chisel3.util.experimental.loadMemoryFromFile   // Import the tool to load instructions from a file

//Start Instruction Memory code

class InstructionMemory(BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    val address     = Input(UInt(32.W))   // Get the PC address
    val instruction = Output(UInt(32.W))  // Send the instruction to the processor
  })

  //We create the memory where the instructions are saved

  val instruction_memory = Mem(4096, UInt(32.W))  // Memory with 4096 positions of 32 bits

  //We load the instructions from the binary file

  loadMemoryFromFile(instruction_memory, BinaryFile)  // Put the program file inside instruction memory

  //We convert the PC from byte address to word address

  val word_address = io.address(13, 2)  // Remove the last two bits because each instruction has 4 bytes

  //We read the instruction using the word address

  io.instruction := instruction_memory(word_address)  // Output the instruction selected by the PC
}
