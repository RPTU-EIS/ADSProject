// ADS I Class Project
// Pipelined RISC-V Core - WB Barrier
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
WB-Barrier: final pipeline register after Writeback stage

Internal Registers:
    check_res: final result value for verification, initialized to 0
    isInvalid: invalid instruction flag, initialized to false

Inputs:
    inCheckRes: result from WB stage
    inXcptInvalid: exception flag from MEM barrier

Outputs:
    outCheckRes: final result for external observation
    outXcptInvalid: final exception flag (tied to invalid instruction in ID stage)

Functionality:
    Save all input signals to a register and output them in the following clock cycle
    Enable result observation without pipeline disruption (for result and exception signals)
*/

package core_tile

import chisel3._

// -----------------------------------------
// WB-Barrier
// -----------------------------------------

class WBBarrier extends Module {
  val io = IO(new Bundle {
    val inCheckRes    = Input(UInt(32.W))  // Get result from writeback stage
    val inXcptInvalid = Input(Bool())      // Get exception flag from writeback path

    val outCheckRes    = Output(UInt(32.W))  // Send result to external wrapper
    val outXcptInvalid = Output(Bool())      // Send exception flag to external wrapper
  })

  val checkResReg = RegInit(0.U(32.W))  // Register for the final visible result
  val invalidReg  = RegInit(false.B)    // Register for the final visible exception flag

  checkResReg := io.inCheckRes     // Store result
  invalidReg  := io.inXcptInvalid  // Store exception flag

  io.outCheckRes    := checkResReg  // Output stored result
  io.outXcptInvalid := invalidReg   // Output stored exception flag
}
