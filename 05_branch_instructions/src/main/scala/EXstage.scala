// ADS I Class Project
// Pipelined RISC-V Core - EX Stage
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
Instruction Execute (EX) Stage: ALU operations and exception detection

Instantiated Modules:
    ALU: Integrate your module from Assignment02 for arithmetic/logical operations

ALU Interface:
    alu.io.operandA: first operand input
    alu.io.operandB: second operand input
    alu.io.operation: operation code controlling ALU function
    alu.io.aluResult: computation result output

Internal Signals:
    Map uopc codes to ALUOp values

Functionality:
    Map instruction uop to ALU operation code
    Pass operands to ALU
    Output results to pipeline

Outputs:
    aluResult: computation result from ALU
    exception: pass exception flag
*/

package core_tile

import chisel3._
import chisel3.util._
import Assignment02.{ALU, ALUOp}
import uopc._

// -----------------------------------------
// Execute Stage
// -----------------------------------------

//ToDo: Add your implementation according to the specification above here

class EXStage extends Module {

  val io = IO(new Bundle {

    val inUOP         = Input(uopc.Type())
    val inRD          = Input(UInt(5.W))
    val inOperandA    = Input(UInt(32.W))
    val inOperandB    = Input(UInt(32.W))
    val inXcptInvalid = Input(Bool())
    val inPC = Input(UInt(32.W))

    val aluResult     = Output(UInt(32.W))
    val rd            = Output(UInt(5.W))
    val exception     = Output(Bool())

    //Forwarding Unit
    val inRs1 = Input(UInt(5.W))
    val inRs2 = Input(UInt(5.W))

    val rdEX = Input(UInt(5.W))
    val rdMEM = Input(UInt(5.W))
    val rdWB = Input(UInt(5.W))

    val aluResEX = Input(UInt(32.W))
    val aluResMEM = Input(UInt(32.W))
    val aluResWB = Input(UInt(32.W))

    //Branch/Jump
    val outFlush = Output(Bool())      // Flush on branch misprediction
    val outPCnew = Output(UInt(32.W))  // New PC for branch
    val inImm = Input(UInt(32.W))
  })

  val alu = Module(new ALU)

  alu.io.operandA := io.inOperandA
  alu.io.operandB := io.inOperandB

  // default
  alu.io.operation := ALUOp.PASSB

  //Forwarding Unit
  // FORWARDING LOGIC (from reference)
  when(io.inRs1 =/= 0.U && io.inRs1 === io.rdEX) {
    alu.io.operandA := io.aluResEX
  }.elsewhen(io.inRs1 =/= 0.U && io.inRs1 === io.rdMEM) {
    alu.io.operandA := io.aluResMEM
  }.elsewhen(io.inRs1 =/= 0.U && io.inRs1 === io.rdWB) {
    //alu.io.operandA := io.aluResWB
    alu.io.operandA := io.inOperandA
  }.otherwise {
    alu.io.operandA := io.inOperandA
  }

  when(io.inRs2 =/= 0.U && io.inRs2 === io.rdEX) {
    alu.io.operandB := io.aluResEX
  }.elsewhen(io.inRs2 =/= 0.U && io.inRs2 === io.rdMEM) {
    alu.io.operandB := io.aluResMEM
  }.elsewhen(io.inRs2 =/= 0.U && io.inRs2 === io.rdWB) {
    //alu.io.operandB := io.aluResWB
    alu.io.operandB := io.inOperandB
  }.otherwise {
    alu.io.operandB := io.inOperandB
  }

  val validOp = WireDefault(false.B)

when(!io.inXcptInvalid) {
    switch(io.inUOP) {

      is(uopc.ADD)   { alu.io.operation := ALUOp.ADD; validOp := true.B }
      is(uopc.SUB)   { alu.io.operation := ALUOp.SUB; validOp := true.B }
      is(uopc.AND)   { alu.io.operation := ALUOp.AND; validOp := true.B }
      is(uopc.OR)    { alu.io.operation := ALUOp.OR;  validOp := true.B }
      is(uopc.XOR)   { alu.io.operation := ALUOp.XOR; validOp := true.B }
      is(uopc.SLL)   { alu.io.operation := ALUOp.SLL; validOp := true.B }
      is(uopc.SRL)   { alu.io.operation := ALUOp.SRL; validOp := true.B }
      is(uopc.SRA)   { alu.io.operation := ALUOp.SRA; validOp := true.B }
      is(uopc.SLT)   { alu.io.operation := ALUOp.SLT; validOp := true.B }
      is(uopc.SLTU)  { alu.io.operation := ALUOp.SLTU; validOp := true.B }

      // I-type
      is(uopc.ADDI)  { alu.io.operation := ALUOp.ADD; validOp := true.B }
      is(uopc.ANDI)  { alu.io.operation := ALUOp.AND; validOp := true.B }
      is(uopc.ORI)   { alu.io.operation := ALUOp.OR;  validOp := true.B }
      is(uopc.XORI)  { alu.io.operation := ALUOp.XOR; validOp := true.B }
      is(uopc.SLLI)  { alu.io.operation := ALUOp.SLL; validOp := true.B }
      is(uopc.SRLI)  { alu.io.operation := ALUOp.SRL; validOp := true.B }
      is(uopc.SRAI)  { alu.io.operation := ALUOp.SRA; validOp := true.B }
      is(uopc.SLTI)  { alu.io.operation := ALUOp.SLT; validOp := true.B }
      is(uopc.SLTIU) { alu.io.operation := ALUOp.SLTU; validOp := true.B }

      // Branch instructions (ALU result used for comparison)
      is(uopc.BEQ)   { alu.io.operation := ALUOp.SUB; validOp := true.B }
      is(uopc.BNE)   { alu.io.operation := ALUOp.SUB; validOp := true.B }
      is(uopc.BLT)   { alu.io.operation := ALUOp.SLT; validOp := true.B }
      is(uopc.BGE)   { alu.io.operation := ALUOp.SLT; validOp := true.B }
      is(uopc.BLTU)  { alu.io.operation := ALUOp.SLTU; validOp := true.B }
      is(uopc.BGEU)  { alu.io.operation := ALUOp.SLTU; validOp := true.B }

      // Jump instructions
      is(uopc.JAL) { alu.io.operation := ALUOp.ADD; validOp := true.B }
      is(uopc.JALR) { alu.io.operation := ALUOp.PASSB; validOp := true.B }

      is(uopc.NOP)   { validOp := true.B }
    }
  }


  io.aluResult := alu.io.aluResult
  io.rd := io.inRD
  io.exception := io.inXcptInvalid
  io.outFlush := false.B
  io.outPCnew := 0.U

  val isBranch = io.inUOP === uopc.BEQ || io.inUOP === uopc.BNE ||
    io.inUOP === uopc.BLT || io.inUOP === uopc.BGE ||
    io.inUOP === uopc.BLTU || io.inUOP === uopc.BGEU
  val isJump = io.inUOP === uopc.JAL || io.inUOP === uopc.JALR

  val branchTaken = WireDefault(false.B)

  val target = WireDefault(0.U(32.W))
  when(isBranch) {
    target := io.inPC + io.inImm
  }.elsewhen(io.inUOP === uopc.JAL) {
    target := io.inPC + io.inImm
  }.elsewhen(io.inUOP === uopc.JALR) {
    target := (io.inOperandA + io.inImm) & (~1.U(32.W))  // ← THIS IS THE ONLY CHANGE
  }

  when(isBranch && validOp && !io.inXcptInvalid) {
    switch(io.inUOP) {
      is(uopc.BEQ)  { branchTaken := alu.io.aluResult === 0.U }
      is(uopc.BNE)  { branchTaken := alu.io.aluResult =/= 0.U }
      is(uopc.BLT)  { branchTaken := alu.io.aluResult === 1.U }
      is(uopc.BGE)  { branchTaken := alu.io.aluResult === 0.U }
      is(uopc.BLTU) { branchTaken := alu.io.aluResult === 1.U }
      is(uopc.BGEU) { branchTaken := alu.io.aluResult === 0.U }
    }
  }

  when((isBranch && branchTaken) || isJump) {
    io.outFlush := true.B
    io.outPCnew := target
  }
}