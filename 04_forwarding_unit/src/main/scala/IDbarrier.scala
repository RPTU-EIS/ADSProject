package core_tile

import chisel3._
import uopc._

// -----------------------------------------
// ID Barrier with rs1 and rs2 forwarding
// -----------------------------------------

//Start code

//We added 4 new signals to carry rs1 and rs2 through the barrier:
//- inRS1, inRS2: source registers coming from the ID stage
//- outRS1, outRS2: source registers going to the EX stage
//The Forwarding Unit reads these to detect data hazards

class IDBarrier extends Module {

  //We define the inputs and outputs of the ID Barrier

  val io = IO(new Bundle {

    //Inputs from ID stage
    val inUOP          = Input(uopc())       // operation code
    val inRD           = Input(UInt(5.W))    // destination register
    val inOperandA     = Input(UInt(32.W))   // first operand
    val inOperandB     = Input(UInt(32.W))   // second operand
    val inXcptInvalid  = Input(Bool())       // invalid flag

    //New inputs from ID stage, source registers for the Forwarding Unit
    val inRS1          = Input(UInt(5.W))    // source register 1
    val inRS2          = Input(UInt(5.W))    // source register 2

    //Outputs to EX stage
    val outUOP         = Output(uopc())      // operation code
    val outRD          = Output(UInt(5.W))   // destination register
    val outOperandA    = Output(UInt(32.W))  // first operand
    val outOperandB    = Output(UInt(32.W))  // second operand
    val outXcptInvalid = Output(Bool())      // invalid flag

    //New outputs to EX stage, source registers for the Forwarding Unit
    val outRS1         = Output(UInt(5.W))   // source register 1
    val outRS2         = Output(UInt(5.W))   // source register 2
  })


  //We create the pipeline registers, all initialized to safe defaults

  val uopReg         = RegInit(NOP)          // operation code register
  val rdReg          = RegInit(0.U(5.W))     // destination register
  val operandAReg    = RegInit(0.U(32.W))    // first operand register
  val operandBReg    = RegInit(0.U(32.W))    // second operand register
  val XcptInvalidReg = RegInit(false.B)      // invalid flag register

  //New registers for rs1 and rs2
  val rs1Reg         = RegInit(0.U(5.W))     // source register 1 storage
  val rs2Reg         = RegInit(0.U(5.W))     // source register 2 storage


  //We capture the inputs on every clock tick

  uopReg         := io.inUOP                 // save operation code
  rdReg          := io.inRD                  // save destination register
  operandAReg    := io.inOperandA            // save first operand
  operandBReg    := io.inOperandB            // save second operand
  XcptInvalidReg := io.inXcptInvalid         // save invalid flag

  //We save rs1 and rs2 so they reach the EX stage and the Forwarding Unit
  rs1Reg         := io.inRS1                 // save source register 1
  rs2Reg         := io.inRS2                 // save source register 2


  //We drive the outputs from the registers, this creates the one-cycle delay

  io.outUOP         := uopReg                // send operation code
  io.outRD          := rdReg                 // send destination register
  io.outOperandA    := operandAReg           // send first operand
  io.outOperandB    := operandBReg           // send second operand
  io.outXcptInvalid := XcptInvalidReg        // send invalid flag

  //We send rs1 and rs2 to the EX stage for the Forwarding Unit
  io.outRS1         := rs1Reg                // send source register 1
  io.outRS2         := rs2Reg                // send source register 2

}