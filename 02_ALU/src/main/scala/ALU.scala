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
  //WE ARE MISSING THE CASE OF UNVALID OPCODE
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
  val PASSB = Value(10.U)
}

class ALU extends Module {

  val io = IO(new Bundle {
    val operandA = Input(UInt(32.W))
    val operandB = Input(UInt(32.W))
    val operation = Input(ALUOp())
    val aluResult = Output(UInt(32.W))

    val negativeNum = Output(UInt(1.W))
  })

  val shift_amount = io.operandB(4, 0)

  io.aluResult := 0.U
  io.negativeNum := 0.U

  //MISSING DEFAULT CASE FOR UNVALID OPCODE

  switch(io.operation) {

    //STRAIGHTFORWARD. NO CHANGES.
    is(ALUOp.ADD) {
      io.aluResult := io.operandA + io.operandB
    }

    //FINISHED
    //WE NEED AN ADDITIONAL OUTPUT TO LET KNOW THE NUMBER IS NEGATIVE
    //DISCUSS IN PERSON
    is(ALUOp.SUB) {
      io.aluResult := (io.operandA - io.operandB)
      when(io.operandA < io.operandB)
        {io.negativeNum := 1.U}
    }

    //CANNOT THINK ABOUT CORNER CASES. LOOKS STRAIGHTFORWARD.
    is(ALUOp.AND) {
      io.aluResult := io.operandA & io.operandB
    }

    //CANNOT THINK ABOUT CORNER CASES. LOOKS STRAIGHTFORWARD.
    is(ALUOp.OR) {
      io.aluResult := io.operandA | io.operandB
    }

    //CANNOT THINK ABOUT CORNER CASES. LOOKS STRAIGHTFORWARD.
    is(ALUOp.XOR) {
      io.aluResult := io.operandA ^ io.operandB
    }

    //NOTHING ELSE TO DO
    is(ALUOp.SLL) {
      io.aluResult := (io.operandA << shift_amount)
    }

    //NOTHING ELSE TO DO
    is(ALUOp.SRL) {
      io.aluResult := io.operandA >> shift_amount
    }

    //NOTHING ELSE TO DO
    is(ALUOp.SRA) {
      io.aluResult := (io.operandA.asSInt >> shift_amount).asUInt
    }

    //IMPLICITLY EXTENDING ZEROES TO THE 0 or 1 COMPARISON RESULT
    is(ALUOp.SLT) {
      io.aluResult := (io.operandA.asSInt < io.operandB.asSInt).asUInt
    }

    //IMPLICITLY EXTENDING ZEROES TO THE 0 or 1 COMPARISON RESULT
    is(ALUOp.SLTU) {
      io.aluResult := (io.operandA < io.operandB).asUInt
    }

    //JUST STRAIGHTFORWARD
    is(ALUOp.PASSB) {
      io.aluResult := io.operandB
    }
  }
}