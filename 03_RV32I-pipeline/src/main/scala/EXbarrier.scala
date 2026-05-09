// ADS I Class Project
// Pipelined RISC-V Core - EX Barrier
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
EX-Barrier: pipeline register between Execute and Memory stages

Internal Registers:
    aluResult: ALU computation result
    rd: destination register index
    exception: exception flag

Inputs:
    inAluResult: computation result from EX stage
    inRD: destination register from EX stage
    inXcptInvalid: exception flag from EX stage

Outputs:
    outAluResult: result to MEM stage
    outRD: destination register to MEM stage
    outXcptInvalid: exception flag to MEM stage

Functionality:
    Save all input signals to a register and output them in the following clock cycle
*/

package core_tile

import chisel3._

// -----------------------------------------
// EX-Barrier
// -----------------------------------------
class EXbarrier() extends Module {
  val io = IO(new Bundle {
  val inAluResult = Input(UInt(32.W))
  val inRD = Input(UInt(5.W))
  val inXcptInvalid = Input(UInt(1.W))

  val outAluResult = Output(UInt(32.W))
  val outRD = Output(UInt(5.W))
  val outXcptInvalid = Output(UInt(1.W))
  })
  
  val aluResult = RegInit(0.U(32.W))
  val rd = RegInit(0.U(5.W))
  val xcptInvalid = RegInit(0.U(1.W))
    
  aluResult := io.inAluResult
  rd := io.inRD
  xcptInvalid := io.inXcptInvalid

  io.outAluResult := aluResult
  io.outRD := rd
  io.outXcptInvalid := xcptInvalid
}
//ToDo: Add your implementation according to the specification above here 

