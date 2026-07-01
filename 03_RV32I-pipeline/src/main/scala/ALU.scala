// ADS I Class Project
// Assignment 02: Arithmetic Logic Unit
// Ported from Assignment02 for use in the pipelined core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern

package Assignment02

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

object ALUOp extends ChiselEnum {
  val ADD, SUB, AND, OR, XOR, SLL, SRL, SRA, SLT, SLTU, PASSB = Value
}

class ALU extends Module {

  val io = IO(new Bundle {
    val operandA  = Input(UInt(32.W))
    val operandB  = Input(UInt(32.W))
    val operation = Input(ALUOp())
    val aluResult = Output(UInt(32.W))
  })

  val shamt = io.operandB(4, 0)

  io.aluResult := 0.U

  switch(io.operation) {
    is(ALUOp.ADD)   { io.aluResult := io.operandA + io.operandB }
    is(ALUOp.SUB)   { io.aluResult := io.operandA - io.operandB }
    is(ALUOp.AND)   { io.aluResult := io.operandA & io.operandB }
    is(ALUOp.OR)    { io.aluResult := io.operandA | io.operandB }
    is(ALUOp.XOR)   { io.aluResult := io.operandA ^ io.operandB }
    is(ALUOp.SLL)   { io.aluResult := (io.operandA << shamt)(31, 0) }
    is(ALUOp.SRL)   { io.aluResult := io.operandA >> shamt }
    is(ALUOp.SRA)   { io.aluResult := (io.operandA.asSInt >> shamt).asUInt }
    is(ALUOp.SLT)   { io.aluResult := (io.operandA.asSInt < io.operandB.asSInt).asUInt }
    is(ALUOp.SLTU)  { io.aluResult := (io.operandA < io.operandB).asUInt }
    is(ALUOp.PASSB) { io.aluResult := io.operandB }
  }
}
