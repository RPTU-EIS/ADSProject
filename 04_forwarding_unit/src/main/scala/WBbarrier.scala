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
    val inCheckRes     = Input(UInt(32.W))
    val inXcptInvalid  = Input(Bool())
    val inRD           = Input(UInt(5.W))
    val inWriteEn      = Input(Bool())
    val outCheckRes    = Output(UInt(32.W))
    val outXcptInvalid = Output(Bool())
    val outRD          = Output(UInt(5.W))
    val outWriteEn     = Output(Bool())
  })

  val checkResReg = RegInit(0.U(32.W))
  val xcptReg     = RegInit(false.B)
  val rdReg       = RegInit(0.U(5.W))
  val writeEnReg  = RegInit(false.B)

  checkResReg := io.inCheckRes
  xcptReg     := io.inXcptInvalid
  rdReg       := io.inRD
  writeEnReg  := io.inWriteEn

  io.outCheckRes    := checkResReg
  io.outXcptInvalid := xcptReg
  io.outRD          := rdReg
  io.outWriteEn     := writeEnReg
}
