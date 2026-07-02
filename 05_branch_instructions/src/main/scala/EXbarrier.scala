// ADS I Class Project
// Pipelined RISC-V Core - EX Barrier
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
EX-Barrier: pipeline register between Execute and Memory stages
*/

package core_tile

import chisel3._

// -----------------------------------------
// EX-Barrier with RegWrite for forwarding
// -----------------------------------------

//Start code

//We carry the RegWrite flag through this barrier so the Forwarding Unit
//can know if the instruction in MEM really writes to a register
class EXBarrier extends Module {

  val io = IO(new Bundle {

    //Inputs from EX stage
    val inAluResult    = Input(UInt(32.W))
    val inRD           = Input(UInt(5.W))
    val inXcptInvalid  = Input(Bool())
    val inRegWrite     = Input(Bool())

    //Outputs to MEM stage
    val outAluResult   = Output(UInt(32.W))
    val outRD          = Output(UInt(5.W))
    val outXcptInvalid = Output(Bool())
    val outRegWrite    = Output(Bool())
  })

  //We create the pipeline registers
  val aluResultReg   = RegInit(0.U(32.W))
  val rdReg          = RegInit(0.U(5.W))
  val XcptInvalidReg = RegInit(false.B)
  val regWriteReg    = RegInit(false.B)

  //We capture the inputs
  aluResultReg   := io.inAluResult
  rdReg          := io.inRD
  XcptInvalidReg := io.inXcptInvalid
  regWriteReg    := io.inRegWrite

  //We drive the outputs
  io.outAluResult   := aluResultReg
  io.outRD          := rdReg
  io.outXcptInvalid := XcptInvalidReg
  io.outRegWrite    := regWriteReg

}