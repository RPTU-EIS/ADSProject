// ADS I Class Project
// Assignment 02: Arithmetic Logic Unit and UVM Testbench
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 09/21/2025 by Tharindu Samarakoon (gug75kex@rptu.de)
// File updated on 10/29/2025 by Tobias Jauch (tobias.jauch@rptu.de)

package Assignment02

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

object ALUOp extends ChiselEnum {
  val ADD   = Value(0.U(8.W))
  val SUB   = Value(1.U(8.W))
  val AND   = Value(2.U(8.W))
  val OR    = Value(3.U(8.W))
  val XOR   = Value(4.U(8.W))
  val SLL   = Value(5.U(8.W))
  val SRL   = Value(6.U(8.W))
  val SRA   = Value(7.U(8.W))
  val SLT   = Value(8.U(8.W))
  val SLTU  = Value(9.U(8.W))
  val PASSB = Value(10.U(8.W))
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
    is(ALUOp.ADD)  { io.aluResult := io.operandA + io.operandB }
    is(ALUOp.SUB)  { io.aluResult := io.operandA - io.operandB }
    is(ALUOp.AND)  { io.aluResult := io.operandA & io.operandB }
    is(ALUOp.OR)   { io.aluResult := io.operandA | io.operandB }
    is(ALUOp.XOR)  { io.aluResult := io.operandA ^ io.operandB }
    is(ALUOp.SLL)  { io.aluResult := io.operandA << shamt }
    is(ALUOp.SRL)  { io.aluResult := io.operandA >> shamt }
    is(ALUOp.SRA)  { io.aluResult := (io.operandA.asSInt >> shamt).asUInt }
    is(ALUOp.SLT)  { io.aluResult := (io.operandA.asSInt < io.operandB.asSInt).asUInt }
    is(ALUOp.SLTU) { io.aluResult := (io.operandA < io.operandB).asUInt }
    is(ALUOp.PASSB){ io.aluResult := io.operandB }
  }
}
