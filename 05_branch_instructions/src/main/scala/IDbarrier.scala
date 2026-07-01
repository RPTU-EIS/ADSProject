// ADS I Class Project
// Pipelined RISC-V Core - ID Barrier
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
ID-Barrier: pipeline register between Decode and Execute stages

Functionality:
    On flush: zero all registers (NOP with wr_en=false) to squash wrong-path instruction
    Otherwise: pass all decoded signals through to EX stage
*/

package core_tile

import chisel3._
import uopc._

class IDBarrier extends Module {
  val io = IO(new Bundle {
    val inUOP          = Input(uopc())
    val inRD           = Input(UInt(5.W))
    val inRS1          = Input(UInt(5.W))
    val inRS2          = Input(UInt(5.W))
    val inOperandA     = Input(UInt(32.W))
    val inOperandB     = Input(UInt(32.W))
    val inPC           = Input(UInt(32.W))
    val inXcptInvalid  = Input(Bool())
    val inwr_en        = Input(Bool())
    val flush          = Input(Bool())

    val outUOP         = Output(uopc())
    val outRD          = Output(UInt(5.W))
    val outRS1         = Output(UInt(5.W))
    val outRS2         = Output(UInt(5.W))
    val outOperandA    = Output(UInt(32.W))
    val outOperandB    = Output(UInt(32.W))
    val outPC          = Output(UInt(32.W))
    val outXcptInvalid = Output(Bool())
    val outwr_en       = Output(Bool())
  })

  val uopReg   = RegInit(uopc.isNOP)
  val rdReg    = RegInit(0.U(5.W))
  val rs1Reg   = RegInit(0.U(5.W))
  val rs2Reg   = RegInit(0.U(5.W))
  val opAReg   = RegInit(0.U(32.W))
  val opBReg   = RegInit(0.U(32.W))
  val pcReg    = RegInit(0.U(32.W))
  val xcptReg  = RegInit(false.B)
  val wr_enReg = RegInit(false.B)

  when(io.flush) {
    uopReg   := uopc.isNOP
    rdReg    := 0.U
    rs1Reg   := 0.U
    rs2Reg   := 0.U
    opAReg   := 0.U
    opBReg   := 0.U
    pcReg    := 0.U
    xcptReg  := false.B
    wr_enReg := false.B
  }.otherwise {
    uopReg   := io.inUOP
    rdReg    := io.inRD
    rs1Reg   := io.inRS1
    rs2Reg   := io.inRS2
    opAReg   := io.inOperandA
    opBReg   := io.inOperandB
    pcReg    := io.inPC
    xcptReg  := io.inXcptInvalid
    wr_enReg := io.inwr_en
  }

  io.outUOP         := uopReg
  io.outRD          := rdReg
  io.outRS1         := rs1Reg
  io.outRS2         := rs2Reg
  io.outOperandA    := opAReg
  io.outOperandB    := opBReg
  io.outPC          := pcReg
  io.outXcptInvalid := xcptReg
  io.outwr_en       := wr_enReg
}
