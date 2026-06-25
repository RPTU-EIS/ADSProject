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

//ToDo: Add your implementation according to the specification above here 
class WBBarrier extends Module {
    val io = IO(new Bundle {
        val inCheckRes     = Input(UInt(32.W))
        val inXcptInvalid  = Input(Bool())

        val outCheckRes    = Output(UInt(32.W))
        val outXcptInvalid = Output(Bool())

        val inRD = Input(UInt(5.W))      // NEW: input rd from WB stage
        val outRD = Output(UInt(5.W))    // NEW: output rd to EX stage for forwarding
    })

    val check_resReg    = RegInit(0.U(32.W))
    val isInvalidReg    = RegInit(false.B)
    val rdReg = RegInit(0.U(5.W))

    check_resReg  := io.inCheckRes
    isInvalidReg  := io.inXcptInvalid
    rdReg := io.inRD

    io.outCheckRes    := check_resReg
    io.outXcptInvalid := isInvalidReg
    io.outRD := rdReg

}