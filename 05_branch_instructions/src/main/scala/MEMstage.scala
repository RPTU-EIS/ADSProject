package core_tile

import chisel3._

// -----------------------------------------
// Memory Stage with RegWrite for forwarding
// -----------------------------------------

//Start code

//We added 2 new signals to support the Forwarding Unit:
//- inRegWrite: write enable flag coming from the EX barrier
//- outRegWrite: write enable flag going to the MEM barrier and the Forwarding Unit
//This stage does not use these signals, it just passes them through to the next barrier

class MEM extends Module {

  //We define the inputs and outputs of the MEM stage

  val io = IO(new Bundle {

    //Inputs from EX barrier
    val aluResult   = Input(UInt(32.W))      // ALU result coming in
    val rd          = Input(UInt(5.W))       // destination register
    val xcptInvalid = Input(Bool())          // invalid flag

    //New input from EX barrier, write enable flag for forwarding
    val inRegWrite  = Input(Bool())          // does this instruction write to a register?

    //Outputs to MEM barrier
    val aluResultOut   = Output(UInt(32.W))  // ALU result going out
    val rdOut          = Output(UInt(5.W))   // destination register going out
    val outXcptInvalid = Output(Bool())      // invalid flag going out

    //New output to MEM barrier, write enable flag for forwarding
    val outRegWrite    = Output(Bool())      // write enable flag
  })


  //We pass all signals straight through, MEM has nothing to do for R-type or I-type

  io.aluResultOut   := io.aluResult          // pass ALU result forward
  io.rdOut          := io.rd                 // pass destination register forward
  io.outXcptInvalid := io.xcptInvalid        // pass invalid flag forward

  //We pass the write enable flag forward so the Forwarding Unit can read it later
  io.outRegWrite    := io.inRegWrite         // pass write enable flag

}