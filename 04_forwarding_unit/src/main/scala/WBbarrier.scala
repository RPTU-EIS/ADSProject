package core_tile

import chisel3._


// ---- WB BARRIER ----
/*
WHY: This is the very last barrier in the pipeline, after WB.
The WB stage finished writing the result back to the Register File.
But we also need to expose the result to the outside world (the testbench).
This barrier holds the final result and the invalid flag for one cycle 
before sending them to the top-level outputs.
This is useful because it keeps the timing clean 
and lets the testbench observe the result without disturbing the pipeline.
It is the bridge between the inside of the processor and the outside world.
*/
class WBBarrier extends Module {
  val io = IO(new Bundle {

    // ---- INPUTS FROM WB ----
    /*
    WHY: These come from the WB stage.
    inCheckRes is the final result of the instruction.
    inXcptInvalid is the invalid flag that was carried through the whole pipeline.
    Both are needed so the testbench can check if the processor worked correctly.
    */
    val inCheckRes     = Input(UInt(32.W))   // final result coming in, 32 bits
    val inXcptInvalid  = Input(Bool())       // invalid flag coming in

    // ---- OUTPUTS FOR EXTERNAL OBSERVATION ----
    /*
    WHY: These go to the top-level IO of the processor.
    The testbench reads them to verify the result.
    outCheckRes is the final number the processor produced.
    outXcptInvalid is true if something went wrong.
    */
    val outCheckRes    = Output(UInt(32.W))  // final result going out
    val outXcptInvalid = Output(Bool())      // invalid flag going out
  })


  // ---- PIPELINE REGISTERS ----
  /*
  WHY: A barrier is built with registers.
  Each register holds a value for exactly one clock cycle.
  We start them at 0 and false so the processor begins clean.
  This is important because at startup the WB stage has no real data yet, 
  and we do not want the testbench to read garbage.
  */
  val checkResReg    = RegInit(0.U(32.W))    // storage for final result
  val isInvalidReg   = RegInit(false.B)      // storage for invalid flag


  // ---- CAPTURE INPUTS ----
  /*
  WHY: On every clock tick, the registers grab the new values from WB.
  This is how the result moves from inside the processor 
  to the visible outputs.
  */
  checkResReg  := io.inCheckRes              // grab the new result
  isInvalidReg := io.inXcptInvalid           // grab the new invalid flag


  // ---- DRIVE OUTPUTS ----
  /*
  WHY: The outputs come from the saved registers, not from the inputs directly.
  This adds one cycle of delay before the testbench sees the result.
  This delay keeps the timing safe and predictable.
  The testbench always sees a stable value, never a glitch.
  */
  io.outCheckRes    := checkResReg           // send the saved result
  io.outXcptInvalid := isInvalidReg          // send the saved invalid flag
}