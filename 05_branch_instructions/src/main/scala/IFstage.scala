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

import chisel3._
import chisel3.util.experimental.loadMemoryFromFile

// -----------------------------------------
// Fetch Stage
// -----------------------------------------

class IFStage (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    // ToDo: Add I/O ports
    val instr = Output(UInt(32.W))
    val PC = Output(UInt(32.W))

    // Jump control from ID stage
    val inPCSrc = Input(Bool())
    val inPCNew = Input(UInt(32.W))

    // Branch control from EX stage
    val inPCNewEx = Input(UInt(32.W))

    //flush
    val inFlush = Input(Bool())
  })

  //ToDo: Add your implementation according to the specification above here
  val PC = RegInit(0.U(32.W))
  val IMem = Mem(4096, UInt(32.W))
  val nextPC = WireDefault(PC + 4.U)

  loadMemoryFromFile(IMem, BinaryFile)

  io.PC := PC
  io.instr := IMem(PC >> 2.U)

  when(io.inFlush) {
    // Branch taken (misprediction) → branch target
    nextPC := io.inPCNewEx
  }.elsewhen(io.inPCSrc) {
    // Jump → jump target
    nextPC := io.inPCNew
  }

  PC := nextPC

}
