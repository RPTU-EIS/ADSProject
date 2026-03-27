package core_tile

import chisel3._
import chisel3.util._
import Assignment02.{ALU, ALUOp}
import uopc._

class EX extends Module {
  val io = IO(new Bundle {
    val uop         = Input(uopc())
    val operandA    = Input(UInt(32.W))
    val operandB    = Input(UInt(32.W))
    val XcptInvalid = Input(Bool())
    val aluResult   = Output(UInt(32.W))
    val exception   = Output(Bool())
  })

  val alu = Module(new ALU)
  alu.io.operandA := io.operandA
  alu.io.operandB := io.operandB
  alu.io.operation := ALUOp.ADD

  switch(io.uop) {
    is(ADD)   { alu.io.operation := ALUOp.ADD }
    is(ADDI)  { alu.io.operation := ALUOp.ADD }
    is(SUB)   { alu.io.operation := ALUOp.SUB }
    is(AND)   { alu.io.operation := ALUOp.AND }
    is(ANDI)  { alu.io.operation := ALUOp.AND }
    is(OR)    { alu.io.operation := ALUOp.OR }
    is(ORI)   { alu.io.operation := ALUOp.OR }
    is(XOR)   { alu.io.operation := ALUOp.XOR }
    is(XORI)  { alu.io.operation := ALUOp.XOR }
    is(SLL)   { alu.io.operation := ALUOp.SLL }
    is(SLLI)  { alu.io.operation := ALUOp.SLL }
    is(SRL)   { alu.io.operation := ALUOp.SRL }
    is(SRLI)  { alu.io.operation := ALUOp.SRL }
    is(SRA)   { alu.io.operation := ALUOp.SRA }
    is(SRAI)  { alu.io.operation := ALUOp.SRA }
    is(SLT)   { alu.io.operation := ALUOp.SLT }
    is(SLTI)  { alu.io.operation := ALUOp.SLT }
    is(SLTU)  { alu.io.operation := ALUOp.SLTU }
    is(SLTIU) { alu.io.operation := ALUOp.SLTU }
    is(NOP)   { alu.io.operation := ALUOp.ADD }
  }

  io.aluResult := alu.io.aluResult
  io.exception := io.XcptInvalid
}
