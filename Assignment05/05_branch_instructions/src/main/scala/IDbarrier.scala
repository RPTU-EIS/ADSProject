// ADS I Class Project
// Pipelined RISC-V Core - ID Barrier
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
ID-Barrier: pipeline register between Decode and Execute stages
*/

package core_tile

import chisel3._
import uopc._

// -----------------------------------------
// ID Barrier with rs1, rs2, PC, immediate, and flush support
// -----------------------------------------

//Start code

//We carry rs1, rs2, PC, and immediate through this barrier:
//- rs1, rs2: needed by the Forwarding Unit
//- PC and imm: needed by the EX stage to compute branch/jump targets
//We also added a flush signal to clear the barrier to NOP when a
//branch is taken, so the wrong instruction does not reach EX
class IDBarrier extends Module {

  val io = IO(new Bundle {

    //Inputs from ID stage
    val inUOP          = Input(uopc())
    val inRD           = Input(UInt(5.W))
    val inOperandA     = Input(UInt(32.W))
    val inOperandB     = Input(UInt(32.W))
    val inXcptInvalid  = Input(Bool())
    val inRS1          = Input(UInt(5.W))
    val inRS2          = Input(UInt(5.W))
    val inPC           = Input(UInt(32.W))
    val inImm          = Input(UInt(32.W))

    //Flush signal from EX stage (branch taken)
    val flush          = Input(Bool())

    //Outputs to EX stage
    val outUOP         = Output(uopc())
    val outRD          = Output(UInt(5.W))
    val outOperandA    = Output(UInt(32.W))
    val outOperandB    = Output(UInt(32.W))
    val outXcptInvalid = Output(Bool())
    val outRS1         = Output(UInt(5.W))
    val outRS2         = Output(UInt(5.W))
    val outPC          = Output(UInt(32.W))
    val outImm         = Output(UInt(32.W))
  })


  //We create the pipeline registers, all initialized to safe defaults
  val uopReg         = RegInit(NOP)
  val rdReg          = RegInit(0.U(5.W))
  val operandAReg    = RegInit(0.U(32.W))
  val operandBReg    = RegInit(0.U(32.W))
  val XcptInvalidReg = RegInit(false.B)
  val rs1Reg         = RegInit(0.U(5.W))
  val rs2Reg         = RegInit(0.U(5.W))
  val pcReg          = RegInit(0.U(32.W))
  val immReg         = RegInit(0.U(32.W))


  //We capture the inputs, OR flush to NOP if a branch was taken
  when(io.flush) {
    uopReg         := NOP
    rdReg          := 0.U
    operandAReg    := 0.U
    operandBReg    := 0.U
    XcptInvalidReg := false.B
    rs1Reg         := 0.U
    rs2Reg         := 0.U
    pcReg          := 0.U
    immReg         := 0.U
  }.otherwise {
    uopReg         := io.inUOP
    rdReg          := io.inRD
    operandAReg    := io.inOperandA
    operandBReg    := io.inOperandB
    XcptInvalidReg := io.inXcptInvalid
    rs1Reg         := io.inRS1
    rs2Reg         := io.inRS2
    pcReg          := io.inPC
    immReg         := io.inImm
  }


  //We drive the outputs from the registers
  io.outUOP         := uopReg
  io.outRD          := rdReg
  io.outOperandA    := operandAReg
  io.outOperandB    := operandBReg
  io.outXcptInvalid := XcptInvalidReg
  io.outRS1         := rs1Reg
  io.outRS2         := rs2Reg
  io.outPC          := pcReg
  io.outImm         := immReg

}