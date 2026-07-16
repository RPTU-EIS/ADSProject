// ToDo: Add your ALU implementation from Assignment02 here

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
  val ADD = Value(0.U)
  val SUB = Value(1.U)
  val AND = Value(2.U)
  val OR = Value(3.U)
  val XOR = Value(4.U)
  val SLL = Value(5.U)
  val SRL = Value(6.U)
  val SRA = Value(7.U)
  val SLT = Value(8.U)
  val SLTU = Value(9.U)
  val BEQ  = Value(10.U)
  val BNE  = Value(11.U)
  val BLT  = Value(12.U)
  val BGE  = Value(13.U)
  val BLTU = Value(14.U)
  val BGEU = Value(15.U)
}

class ALU extends Module {

  val io = IO(new Bundle {
    val operandA  = Input(UInt(32.W))
    val operandB  = Input(UInt(32.W))
    val operation = Input(ALUOp())
    val aluResult = Output(UInt(32.W))
    val zero      = Output(Bool())
  })

  val shift_amount = io.operandB(4, 0)

  io.aluResult := 0.U
  io.zero := io.aluResult === false.B

  switch(io.operation) {

    is(ALUOp.ADD)  {io.aluResult := io.operandA + io.operandB }
    is(ALUOp.SUB)  {io.aluResult := (io.operandA - io.operandB) }
    is(ALUOp.AND)  {io.aluResult := io.operandA & io.operandB }
    is(ALUOp.OR)   {io.aluResult := io.operandA | io.operandB }
    is(ALUOp.XOR)  {io.aluResult := io.operandA ^ io.operandB }
    is(ALUOp.SLL)  {io.aluResult := (io.operandA << shift_amount) }
    is(ALUOp.SRL)  {io.aluResult := io.operandA >> shift_amount }
    is(ALUOp.SRA)  {io.aluResult := (io.operandA.asSInt >> shift_amount).asUInt }
    is(ALUOp.SLT)  {io.aluResult := (io.operandA.asSInt < io.operandB.asSInt).asUInt }
    is(ALUOp.SLTU) {io.aluResult := (io.operandA < io.operandB).asUInt }
    is(ALUOp.BEQ)  { io.aluResult := 0.U; io.zero := io.operandA === io.operandB }
    is(ALUOp.BNE)  { io.aluResult := 0.U; io.zero := io.operandA =/= io.operandB }
    is(ALUOp.BLT)  { io.aluResult := 0.U; io.zero := io.operandA.asSInt < io.operandB.asSInt }
    is(ALUOp.BGE)  { io.aluResult := 0.U; io.zero := io.operandA.asSInt >= io.operandB.asSInt }
    is(ALUOp.BLTU) { io.aluResult := 0.U; io.zero := io.operandA < io.operandB }
    is(ALUOp.BGEU) { io.aluResult := 0.U; io.zero := io.operandA >= io.operandB }
  }

}