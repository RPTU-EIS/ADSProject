// ADS I Class Project
// Pipelined RISC-V Core - IF Barrier
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
IF-Barrier: pipeline register between Fetch and Decode stages
*/

package core_tile

import chisel3._

// -----------------------------------------
// IF Barrier with PC and flush support
// -----------------------------------------

//Start code

//We carry the instruction and the PC through this barrier
//We also added a flush signal to clear the barrier to NOP when a
//branch is taken, so the wrong fetched instruction does not reach ID
//NOP in machine code is 0x00000013 (ADDI x0, x0, 0)
class IFBarrier extends Module {

  val io = IO(new Bundle {

    //Inputs from IF stage
    val inInstr  = Input(UInt(32.W))
    val inPC     = Input(UInt(32.W))

    //Flush signal from EX stage (branch taken)
    val flush    = Input(Bool())

    //Outputs to ID stage
    val outInstr = Output(UInt(32.W))
    val outPC    = Output(UInt(32.W))
  })


  //We create the pipeline registers
  val instrReg = RegInit(0.U(32.W))
  val pcReg    = RegInit(0.U(32.W))


  //We capture the inputs, or flush to NOP if a branch was taken
  when(io.flush) {
    instrReg := "h00000013".U   //NOP instruction
    pcReg    := 0.U
  }.otherwise {
    instrReg := io.inInstr
    pcReg    := io.inPC
  }


  //We drive the outputs
  io.outInstr := instrReg
  io.outPC    := pcReg

}