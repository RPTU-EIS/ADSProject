package core_tile

import chisel3._


// ---- WB STAGE (WRITEBACK) ----
/*
WHY: This is the fifth and final stage of the pipeline.
The job of WB is to take the final result and save it back into the Register File.
This is how the program actually changes the state of the processor.
Without this stage, the ALU result would be calculated and then thrown away.
The WB stage also sends the result to the testbench so we can verify it.
*/
class WB extends Module {
  val io = IO(new Bundle {

    // ---- INPUTS FROM MEM BARRIER ----
    /*
    WHY: These come from the MEM stage through the MEM barrier.
    aluResult is the final value we want to save.
    rd is the destination register where the value must go.
    exception tells us if the instruction was invalid.
    We use exception to decide if we really save the value or not.
    */
    val aluResult   = Input(UInt(32.W))      // final result, 32 bits
    val rd          = Input(UInt(5.W))       // destination register, 5 bits
    val exception   = Input(Bool())          // invalid flag, true or false

    // ---- REGISTER FILE WRITE PORT ----
    /*
    WHY: This is the request we send to the Register File.
    It is a bundle with three fields: addr, data, and wr_en.
    We build it here and send it back to the ID stage 
    where the Register File lives.
    */
    val regFileReq  = Output(new regFileWriteReq)   // write request to Register File

    // ---- OUTPUTS FOR VERIFICATION ----
    /*
    WHY: These outputs go to the WB barrier and then to the testbench.
    check_res lets the testbench see the final result.
    outException lets the testbench know if there was an invalid instruction.
    These are not used by the processor itself, only for checking.
    */
    val check_res    = Output(UInt(32.W))    // final result for testbench
    val outException = Output(Bool())        // invalid flag for testbench
  })


  // ---- BUILD THE WRITE REQUEST ----
  /*
  WHY: We fill in the three fields of the write request.
  addr tells the Register File which register to write to (rd).
  data is the value we want to save (the ALU result).
  These two are direct connections, no logic needed.
  */
  io.regFileReq.addr  := io.rd               // write to destination register
  io.regFileReq.data  := io.aluResult        // save the ALU result


  // ---- WRITE ENABLE ----
  /*
  WHY: wr_en controls if the write really happens or not.
  If the instruction was invalid, we do NOT want to save anything.
  Saving a bad result would corrupt the Register File.
  So we set wr_en to the opposite of exception.
  If exception is false (good instruction), wr_en is true (do save).
  If exception is true (bad instruction), wr_en is false (do not save).
  This protects the processor from invalid instructions.
  */
  io.regFileReq.wr_en := !io.exception       // only write if no exception


  // ---- VERIFICATION OUTPUTS ----
  /*
  WHY: We send the result and the exception flag to the WB barrier.
  The testbench will use them to check if the processor produced the right answer.
  check_res shows the value of the last operation.
  outException shows if any instruction was invalid.
  These are observation signals, they do not affect the processor's behavior.
  */
  io.check_res    := io.aluResult            // expose result to testbench
  io.outException := io.exception            // expose invalid flag to testbench
}