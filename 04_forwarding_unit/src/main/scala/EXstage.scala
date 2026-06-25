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
  })

  val alu = Module(new ALU)

  alu.io.operandA := io.inOperandA
  alu.io.operandB := io.inOperandB

  // default
  alu.io.operation := ALUOp.PASSB

  switch(io.inUOP) {

    is(uopc.ADD)   { alu.io.operation := ALUOp.ADD }
    is(uopc.ADDI)  { alu.io.operation := ALUOp.ADD }

    is(uopc.SUB)   { alu.io.operation := ALUOp.SUB }

    is(uopc.AND)   { alu.io.operation := ALUOp.AND }
    is(uopc.ANDI)  { alu.io.operation := ALUOp.AND }

    is(uopc.OR)    { alu.io.operation := ALUOp.OR }
    is(uopc.ORI)   { alu.io.operation := ALUOp.OR }

    is(uopc.XOR)   { alu.io.operation := ALUOp.XOR }
    is(uopc.XORI)  { alu.io.operation := ALUOp.XOR }

    is(uopc.SLL)   { alu.io.operation := ALUOp.SLL }
    is(uopc.SLLI)  { alu.io.operation := ALUOp.SLL }

    is(uopc.SRL)   { alu.io.operation := ALUOp.SRL }
    is(uopc.SRLI)  { alu.io.operation := ALUOp.SRL }

    is(uopc.SRA)   { alu.io.operation := ALUOp.SRA }
    is(uopc.SRAI)  { alu.io.operation := ALUOp.SRA }

    is(uopc.SLT)   { alu.io.operation := ALUOp.SLT }
    is(uopc.SLTI)  { alu.io.operation := ALUOp.SLT }

    is(uopc.SLTU)  { alu.io.operation := ALUOp.SLTU }
    is(uopc.SLTIU) { alu.io.operation := ALUOp.SLTU }
  }

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

  io.aluResult := alu.io.aluResult
  io.rd := io.inRD
  io.exception := io.inXcptInvalid
}