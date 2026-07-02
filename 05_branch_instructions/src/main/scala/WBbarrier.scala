package core_tile

import chisel3._

// -----------------------------------------
// WB Barrier (final output)
// -----------------------------------------

//Start code

//We define the final barrier of the pipeline
//It holds the result and the invalid flag for one cycle before sending
//them to the top-level outputs, so the testbench sees stable values
class WBBarrier extends Module {

  val io = IO(new Bundle {

    //Inputs from WB stage
    val inCheckRes     = Input(UInt(32.W))
    val inXcptInvalid  = Input(Bool())

    //Outputs for external observation
    val outCheckRes    = Output(UInt(32.W))
    val outXcptInvalid = Output(Bool())
  })


  //We create the pipeline registers
  val checkResReg  = RegInit(0.U(32.W))
  val isInvalidReg = RegInit(false.B)


  //We capture the inputs on every clock tick
  checkResReg  := io.inCheckRes
  isInvalidReg := io.inXcptInvalid


  //We drive the outputs from the registers
  io.outCheckRes    := checkResReg
  io.outXcptInvalid := isInvalidReg

}