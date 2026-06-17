// ADS I Class Project
// Pipelined RISC-V Core - MEM Barrier
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
MEM-Barrier: pipeline register between Memory and Writeback stages

Internal Registers:
    aluResult: computation result (or future load data)
    rd: destination register index
    exception: exception flag

Inputs:
    inAluResult: result from MEM stage
    inRD: destination register from MEM stage
    inException: exception flag from MEM stage

Outputs:
    outAluResult: result to WB stage
    outRD: destination register to WB stage
    outException: exception flag to WB stage

Functionality:
    Save all input signals to a register and output them in the following clock cycle
*/

package core_tile

import chisel3._

// -----------------------------------------
// MEM-Barrier
// -----------------------------------------

class MEMbarrier extends Module{

  val io = IO(new Bundle {
    val inALUResult     = Input(UInt(32.W))
    val inRD            = Input(UInt(32.W))
    val inException     = Input(UInt(1.W))

    val outALUResult    = Output(UInt(32.W))
    val outRD           = Output(UInt(32.W))
    val outException    = Output(UInt(1.W))
  })

//ToDo: Add your implementation according to the specification above here
    val aluResult = RegInit(0.U(32.W))
    val rd        = RegInit(0.U(32.W))
    val exception = RegInit(0.U(1.W))

    aluResult       := io.inALUResult
    rd              := io.inRD
    exception       := io.inException

    io.outALUResult := aluResult
    io.outRD        := rd
    io.outException := exception
}