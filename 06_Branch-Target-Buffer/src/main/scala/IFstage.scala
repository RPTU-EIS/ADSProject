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


class IF (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    val PCSrcE    = Input(Bool())
    val PCTargetE = Input(UInt(32.W))

    val InstrF    = Output(UInt(32.W))
    val PCF       = Output(UInt(32.W))
    val PCPlus4F  = Output(UInt(32.W))

    //BTB SIGNALS
    val BTBTarget       = Input(UInt(32.W))   // NUEVO
    val BTBPredictTaken = Input(Bool())       // NUEVO
    val PredictTakenF   = Output(Bool())
  })

  //ToDo: Add your implementation according to the specification above here
  val IMem = Mem(4096, UInt(32.W))
  loadMemoryFromFile(IMem, BinaryFile)

  val PC = RegInit(0.U(32.W))
  val addr = PC >> 2
  io.InstrF := IMem(addr(11,0))

  val PCPlus4 = PC + 4.U

  // Prioridad: 1) corrección real desde EX  2) predicción de la BTB  3) PC+4
  val PCNext = Mux(io.PCSrcE, io.PCTargetE,
    Mux(io.BTBPredictTaken, io.BTBTarget, PCPlus4))

  PC := PCNext

  io.PCF      := PC
  io.PCPlus4F := PCPlus4

  //BTB SIGNALS
  io.PredictTakenF := io.BTBPredictTaken //HARDWIRE FOR NOW JUST TO COMPILE

  printf(" IF STAGE SIGNALS\n")
  printf("----------------------------------------------\n")
  printf(" PCSrcE:       %x\n", io.PCSrcE)
  printf(" PredictTaken: %x\n\n", io.PredictTakenF)

}