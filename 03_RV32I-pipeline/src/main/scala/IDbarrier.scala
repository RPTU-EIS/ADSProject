// ADS I Class Project
// Pipelined RISC-V Core - ID Barrier
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)
// Modified for Assignment 04: Added rs1, rs2, opBSel for forwarding unit

package core_tile

import chisel3._
import uopc._

// -----------------------------------------
// ID-Barrier
// -----------------------------------------
class IDBarrier extends Module {
  val io = IO(new Bundle {
    // Inputs from ID stage
    val inUOP         = Input(uopc())
    val inRD          = Input(UInt(5.W))
    val inRS1         = Input(UInt(5.W))     // NEW: source register 1 address
    val inRS2         = Input(UInt(5.W))     // NEW: source register 2 address
    val inOperandA    = Input(UInt(32.W))
    val inOperandB    = Input(UInt(32.W))
    val inOpBSel      = Input(Bool())        // NEW: true = immediate on operandB
    val inXcptInvalid = Input(Bool())

    // Outputs to EX stage
    val outUOP         = Output(uopc())
    val outRD          = Output(UInt(5.W))
    val outRS1         = Output(UInt(5.W))   // NEW: to forwarding unit
    val outRS2         = Output(UInt(5.W))   // NEW: to forwarding unit
    val outOperandA    = Output(UInt(32.W))
    val outOperandB    = Output(UInt(32.W))
    val outOpBSel      = Output(Bool())      // NEW: to forwarding mux logic
    val outXcptInvalid = Output(Bool())
  })

  // Pipeline registers
  val uopReg      = RegInit(NOP)
  val rdReg       = RegInit(0.U(5.W))
  val rs1Reg      = RegInit(0.U(5.W))       // NEW
  val rs2Reg      = RegInit(0.U(5.W))       // NEW
  val operandAReg = RegInit(0.U(32.W))
  val operandBReg = RegInit(0.U(32.W))
  val opBSelReg   = RegInit(false.B)         // NEW
  val xcptReg     = RegInit(false.B)

  // Capture inputs on clock edge
  uopReg      := io.inUOP
  rdReg       := io.inRD
  rs1Reg      := io.inRS1                    // NEW
  rs2Reg      := io.inRS2                    // NEW
  operandAReg := io.inOperandA
  operandBReg := io.inOperandB
  opBSelReg   := io.inOpBSel                // NEW
  xcptReg     := io.inXcptInvalid

  // Output registered values
  io.outUOP         := uopReg
  io.outRD          := rdReg
  io.outRS1         := rs1Reg                // NEW
  io.outRS2         := rs2Reg                // NEW
  io.outOperandA    := operandAReg
  io.outOperandB    := operandBReg
  io.outOpBSel      := opBSelReg             // NEW
  io.outXcptInvalid := xcptReg
}
