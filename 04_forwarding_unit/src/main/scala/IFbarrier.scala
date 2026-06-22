package core_tile

import chisel3._

// ---- IF BARRIER ----
/*
WHY: This is the pipeline register between IF and ID.
The IF stage just read an instruction from memory.
But the ID stage is not ready to decode it in the same cycle.
So we save the instruction here for one clock cycle.
When the next tick comes, the ID stage can grab it and decode it.
This is the smallest barrier in the pipeline 
because the IF stage only produces one signal: the instruction.
*/
class IFBarrier extends Module {
  val io = IO(new Bundle {

    // ---- INPUT FROM IF ----
    /*
    WHY: The instruction comes in from the IF stage.
    It is 32 bits long because every RISC-V instruction is 32 bits.
    */
    val inInstr  = Input(UInt(32.W))         // instruction coming in, 32 bits

    // ---- OUTPUT TO ID ----
    /*
    WHY: The instruction goes out to the ID stage in the next cycle.
    Same value as the input, just delayed by one clock tick.
    */
    val outInstr = Output(UInt(32.W))        // instruction going out, 32 bits
  })


  // ---- PIPELINE REGISTER ----
  /*
  WHY: A barrier is built with a register.
  A register holds a value for exactly one clock cycle.
  We start it at 0 so the pipeline begins clean.
  If we did not reset it, the ID stage could decode random garbage 
  on the first clock cycle and crash the processor.
  */
  val instrReg = RegInit(0.U(32.W))          // storage for instruction, starts at 0


  // ---- CAPTURE INPUT ----
  /*
  WHY: On every clock tick, the register grabs the new instruction.
  This is how the instruction moves from IF to ID.
  The new value comes in now and will be visible at the output next cycle.
  */
  instrReg := io.inInstr                     // grab the new instruction


  // ---- DRIVE OUTPUT ----
  /*
  WHY: The output comes from the saved register, not from the input directly.
  This creates the one-cycle delay needed for the pipeline.
  The ID stage will see this instruction in the next clock cycle.
  */
  io.outInstr := instrReg                    // send the saved instruction
}