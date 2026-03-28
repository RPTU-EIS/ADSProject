package core_tile

import chisel3._
import uopc._

class IDBarrier extends Module {
  val io = IO(new Bundle {
    val inUOP         = Input(uopc());     val outUOP         = Output(uopc())
    val inRD          = Input(UInt(5.W));  val outRD          = Output(UInt(5.W))
    val inRS1         = Input(UInt(5.W));  val outRS1         = Output(UInt(5.W))
    val inRS2         = Input(UInt(5.W));  val outRS2         = Output(UInt(5.W))
    val inOperandA    = Input(UInt(32.W)); val outOperandA    = Output(UInt(32.W))
    val inOperandB    = Input(UInt(32.W)); val outOperandB    = Output(UInt(32.W))
    val inOpBSel      = Input(Bool());     val outOpBSel      = Output(Bool())
    val inWrEn        = Input(Bool());     val outWrEn        = Output(Bool())
    val inPC          = Input(UInt(32.W)); val outPC          = Output(UInt(32.W))
    val inBranchTarget= Input(UInt(32.W)); val outBranchTarget= Output(UInt(32.W))
    val inXcptInvalid = Input(Bool());     val outXcptInvalid = Output(Bool())
    val flush         = Input(Bool())
  })

  val uopReg         = RegInit(NOP)
  val rdReg          = RegInit(0.U(5.W))
  val rs1Reg         = RegInit(0.U(5.W))
  val rs2Reg         = RegInit(0.U(5.W))
  val operandAReg    = RegInit(0.U(32.W))
  val operandBReg    = RegInit(0.U(32.W))
  val opBSelReg      = RegInit(false.B)
  val wrEnReg        = RegInit(false.B)
  val pcReg          = RegInit(0.U(32.W))
  val branchTargetReg= RegInit(0.U(32.W))
  val xcptReg        = RegInit(false.B)

  when(io.flush) {
    uopReg := NOP; rdReg := 0.U; rs1Reg := 0.U; rs2Reg := 0.U
    operandAReg := 0.U; operandBReg := 0.U; opBSelReg := false.B
    wrEnReg := false.B; pcReg := 0.U; branchTargetReg := 0.U; xcptReg := false.B
  }.otherwise {
    uopReg := io.inUOP;         rdReg := io.inRD
    rs1Reg := io.inRS1;         rs2Reg := io.inRS2
    operandAReg := io.inOperandA; operandBReg := io.inOperandB
    opBSelReg := io.inOpBSel;   wrEnReg := io.inWrEn
    pcReg := io.inPC;           branchTargetReg := io.inBranchTarget
    xcptReg := io.inXcptInvalid
  }

  io.outUOP := uopReg;           io.outRD := rdReg
  io.outRS1 := rs1Reg;           io.outRS2 := rs2Reg
  io.outOperandA := operandAReg;  io.outOperandB := operandBReg
  io.outOpBSel := opBSelReg;     io.outWrEn := wrEnReg
  io.outPC := pcReg;             io.outBranchTarget := branchTargetReg
  io.outXcptInvalid := xcptReg
}
