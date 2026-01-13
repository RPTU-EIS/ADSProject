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
  val ADD, SUB, AND, OR, XOR, SLL, SRL, SRA, SLT, SLTU, PASSB = Value
}

class ALU extends Module {

  val io = IO(new Bundle {
    val operandA = Input(UInt(32.W))
    val operandB = Input(UInt(32.W))
    val operation = Input(ALUOp())
    val aluResult = Output(UInt(32.W))
  })



  io.aluResult := 0.U

  switch(io.operation) {
    is(ALUOp.ADD) { io.aluResult := io.operandA + io.operandB }
    is(ALUOp.SUB) { io.aluResult := io.operandA - io.operandB }
    is(ALUOp.AND) { io.aluResult := io.operandA & io.operandB }
    is(ALUOp.OR) { io.aluResult := io.operandA | io.operandB }
    is(ALUOp.XOR) { io.aluResult := io.operandA ^ io.operandB }
    is(ALUOp.SLL) { io.aluResult := io.operandA << io.operandB(4,0) }
    is(ALUOp.SRL) { io.aluResult := io.operandA >> io.operandB(4,0) }
    is(ALUOp.SRA) { io.aluResult := (io.operandA.asSInt >> io.operandB(4,0)).asUInt }
    is(ALUOp.SLT) { io.aluResult := (io.operandA.asSInt < io.operandB.asSInt).asUInt }
    is(ALUOp.SLTU) { io.aluResult := (io.operandA < io.operandB).asUInt }
    is(ALUOp.PASSB) { io.aluResult := io.operandB}
  }

}