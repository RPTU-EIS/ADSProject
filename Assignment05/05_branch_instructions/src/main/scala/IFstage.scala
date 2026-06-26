// ADS I Class Project
// Pipelined RISC-V Core - IF Stage
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
The Instruction Fetch (IF) stage is the first stage of the pipeline
and handles instruction retrieval from memory.
*/

package core_tile

import chisel3._
import chisel3.util.experimental.loadMemoryFromFile

// -----------------------------------------
// IF Stage with branch/jump target support
// -----------------------------------------

//Start code

//We added two inputs to support branch and jump instructions:
//- branchTaken: signal from the EX stage telling us if a branch was taken
//- branchTarget: the new PC value the processor must jump to
//If branchTaken is true, the PC jumps to branchTarget
//Otherwise the PC keeps going to PC+4 (static prediction: not taken)
//We also expose pcOut so the rest of the pipeline can use the PC value
class IF (BinaryFile: String) extends Module {

  val io = IO(new Bundle {

    //Inputs from EX stage (for branch/jump handling)
    val branchTaken  = Input(Bool())
    val branchTarget = Input(UInt(32.W))

    //Outputs to IF barrier
    val instr = Output(UInt(32.W))
    val pcOut = Output(UInt(32.W))
  })


  //We define the instruction memory and load the program
  val IMem = Mem(4096, UInt(32.W))
  loadMemoryFromFile(IMem, BinaryFile)


  //We define the Program Counter
  val PC = RegInit(0.U(32.W))


  //We read the instruction at PC (word-aligned)
  io.instr := IMem(PC >> 2)


  //We expose the current PC so the rest of the pipeline can use it
  io.pcOut := PC


  //We update the PC
  //If a branch is taken, jump to branchTarget
  //Otherwise, keep going to PC+4
  when(io.branchTaken) {
    PC := io.branchTarget
  }.otherwise {
    PC := PC + 4.U
  }

}