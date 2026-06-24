// ADS I Class Project
// Pipelined RISC-V Core - MEM Stage
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
Memory (MEM) Stage: load/store operations (placeholder for RV32I R/I-type subset)

Current Implementation:
    Empty placeholder module with no active ports or operations
    In current RV32I subset (R-type, I-type), no memory operations are performed

Rationale:
    Placeholder stage ensures proper pipeline depth and timing
    Allows future extension without architectural changes
*/

package core_tile

import chisel3._


  // No memory operations implemented in Assignment03, nothing to do here! :)


// -----------------------------------------
// Memory Stage
// -----------------------------------------

class MEM extends Module {
  val io = IO(new Bundle {
    // Inputs from EX Barrier
    val aluResult   = Input(UInt(32.W))
    val rd          = Input(UInt(5.W))
    val xcptInvalid = Input(Bool())

    // Outputs to MEM Barrier
    val aluResultOut   = Output(UInt(32.W))
    val rdOut          = Output(UInt(5.W))
    val outXcptInvalid = Output(Bool())
  })

  // No memory operations for R-type/I-type subset
  // Just pass signals straight through combinationally
  io.aluResultOut   := io.aluResult
  io.rdOut          := io.rd
  io.outXcptInvalid := io.xcptInvalid
}
