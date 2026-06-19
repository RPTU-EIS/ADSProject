package core_tile

import chisel3._

// -----------------------------------------
// MEM Barrier with RegWrite for forwarding
// -----------------------------------------

//Start code

//We added 2 new signals to support the Forwarding Unit:
//- inRegWrite: write enable flag coming from the MEM stage
//- outRegWrite: write enable flag going to the WB stage and the Forwarding Unit
//The Forwarding Unit needs this flag to know if the instruction in WB really writes to a register

class MEMBarrier extends Module {

  //We define the inputs and outputs of the MEM Barrier

  val io = IO(new Bundle {

    //Inputs from MEM stage
    val inAluResult  = Input(UInt(32.W))     // ALU result from MEM
    val inRD         = Input(UInt(5.W))      // destination register
    val inException  = Input(Bool())         // invalid flag

    //New input from MEM stage, write enable for forwarding logic
    val inRegWrite   = Input(Bool())         // does this instruction write to a register?

    //Outputs to WB stage
    val outAluResult = Output(UInt(32.W))    // ALU result to WB
    val outRD        = Output(UInt(5.W))     // destination register to WB
    val outException = Output(Bool())        // invalid flag to WB

    //New output to WB stage and Forwarding Unit
    val outRegWrite  = Output(Bool())        // write enable to Forwarding Unit
  })


  //We create the pipeline registers, all initialized to safe defaults

  val aluResultReg = RegInit(0.U(32.W))      // ALU result storage
  val rdReg        = RegInit(0.U(5.W))       // destination register storage
  val exceptionReg = RegInit(false.B)        // invalid flag storage

  //New register for the write enable flag
  val regWriteReg  = RegInit(false.B)        // write enable storage


  //We capture the inputs on every clock tick

  aluResultReg := io.inAluResult             // save ALU result
  rdReg        := io.inRD                    // save destination register
  exceptionReg := io.inException             // save invalid flag

  //We save the write enable flag so the Forwarding Unit can read it
  regWriteReg  := io.inRegWrite              // save write enable flag


  //We drive the outputs from the registers, this creates the one-cycle delay

  io.outAluResult := aluResultReg            // send saved ALU result
  io.outRD        := rdReg                   // send saved destination register
  io.outException := exceptionReg            // send saved invalid flag

  //We expose the write enable flag to the Forwarding Unit
  io.outRegWrite  := regWriteReg             // send write enable flag

}