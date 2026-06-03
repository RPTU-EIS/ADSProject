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

//ToDo: define AluOp Enum

object ALUOp extends ChiselEnum {
  val ADD   = Value(0.U)
  val SUB   = Value(1.U)
  val AND   = Value(2.U)
  val OR    = Value(3.U)
  val XOR   = Value(4.U)
  val SLL   = Value(5.U)
  val SRL   = Value(6.U)
  val SRA   = Value(7.U)
  val SLT   = Value(8.U)
  val SLTU  = Value(9.U)
  val PASSB = Value(10.U)
}

class ALU extends Module {
  
  val io = IO(new Bundle {
    val operandA  = Input(UInt(32.W))
    val operandB  = Input(UInt(32.W))
    val operation = Input(ALUOp())
    val aluResult = Output(UInt(32.W))
  })

  val shift_amount = (io.operandB & 31.U)(4,0)

  io.aluResult := 0.U

  when(io.operation === ALUOp.ADD) {
    io.aluResult := io.operandA + io.operandB
  }.
  elsewhen(io.operation === ALUOp.SUB){
    io.aluResult := io.operandA - io.operandB
  }.
  elsewhen(io.operation === ALUOp.AND){
    io.aluResult := io.operandA & io.operandB
  }.
  elsewhen(io.operation === ALUOp.OR){
    io.aluResult := io.operandA | io.operandB
  }.
  elsewhen(io.operation === ALUOp.XOR){
    io.aluResult := io.operandA ^ io.operandB
  }.
  elsewhen(io.operation === ALUOp.SLL){
    io.aluResult := io.operandA << shift_amount
  }.
  elsewhen(io.operation === ALUOp.SRL){
    io.aluResult := io.operandA >> shift_amount
  }.
  elsewhen(io.operation === ALUOp.SRA){
    io.aluResult := (io.operandA.asSInt >> shift_amount).asUInt
  }.
  elsewhen(io.operation === ALUOp.SLT){
    when(io.operandA.asSInt > io.operandB.asSInt){
      io.aluResult := 1.U
    }
  }.
  elsewhen(io.operation === ALUOp.SLTU){
    when(io.operandA.asUInt > io.operandB.asUInt){
      io.aluResult := 1.U
    }
  }.
  elsewhen(io.operation === ALUOp.PASSB){
    io.aluResult := io.operandB
  }
}