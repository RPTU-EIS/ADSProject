// ADS I Class Project
// Pipelined RISC-V Core - WB Barrier
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
WB-Barrier: final pipeline register after Writeback stage

Functionality:
    Capture check_res and exception flag for external observation.
*/

package core_tile

import chisel3._

class WBBarrier extends Module {
  val io = IO(new Bundle {
    val inCheckRes    = Input(UInt(32.W))
    val inXcptInvalid = Input(Bool())

    val outCheckRes    = Output(UInt(32.W))
    val outXcptInvalid = Output(Bool())
  })

  val checkResReg = RegInit(0.U(32.W))
  val xcptReg     = RegInit(false.B)

  checkResReg := io.inCheckRes
  xcptReg     := io.inXcptInvalid

  io.outCheckRes    := checkResReg
  io.outXcptInvalid := xcptReg
}
