package PipelinedRV32I

import chisel3._
import chisel3.util._

import core_tile._


// ---- TOP-LEVEL WRAPPER ----
/*
WHY: This is the very top of the project, above the core itself.
It is a small wrapper around the real processor.
Why do we need a wrapper if we already have core.scala?
Because the testbench needs a clean and simple interface.
The testbench does not care about all the internal signals.
It only wants two things: the final result and an exception flag.
So this wrapper hides all the complexity and only shows what is needed.
This makes testing easier and keeps the design organized.
*/
class PipelinedRV32I (BinaryFile: String) extends Module {


  // ---- IO PORTS ----
  /*
  WHY: We only expose two signals to the outside world.
  result is the final value the processor calculated.
  exception is true if an invalid instruction was found.
  Everything else stays hidden inside the core.
  This is the public interface of the processor.
  */
  val io = IO(new Bundle {
    val result    = Output(UInt(32.W))       // final result, 32 bits
    val exception = Output(Bool())           // exception flag, true or false
  })


  // ---- CREATE THE CORE ----
  /*
  WHY: We make one instance of the real processor (the core).
  BinaryFile is the path to the program we want to run.
  We pass it down so the Instruction Memory inside the core knows what to load.
  This is the only module instantiated here, because all the work happens inside the core.
  */
  val core = Module(new PipelinedRV32Icore(BinaryFile))   // create the processor core


  // ---- CONNECT CORE OUTPUTS TO TOP IO ----
  /*
  WHY: We take the outputs of the core and send them to the top-level IO.
  check_res becomes result, the value used by the testbench.
  isInvalid becomes exception, the flag used by the testbench.
  This is just a clean rename so the outside world sees friendly names.
  */
  io.result    := core.io.check_res          // forward the final result
  io.exception := core.io.isInvalid          // forward the exception flag
}