// ToDo: Add your ALU implementation from Assignment02 here

package Assignment02

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

//ToDo: define AluOp Enum
object ALUOp extends ChiselEnum {
  val ADD, SUB, AND, OR, XOR, SLL, SRL, SRA, SLT, SLTU, PASSB, JAL = Value
}

class ALU extends Module {
  import ALUOp._
  
  val io = IO(new Bundle {
    //ToDo: define IOs
    val operandA = Input(UInt(32.W))
    val operandB = Input(UInt(32.W))
    val operation = Input(ALUOp())
    val aluResult = Output(UInt(32.W))
  })

  //ToDo: implement ALU functionality according to the task specification 
  io.aluResult := 0.U // Default value to avoid latches

  switch(io.operation) {
    is(ADD) {
      io.aluResult := (io.operandA + io.operandB) 
    }
    is(SUB) {
      io.aluResult := (io.operandA - io.operandB) 
    }
    is(AND) {
      io.aluResult := (io.operandA & io.operandB) 
    }
    is(OR) {
      io.aluResult := (io.operandA | io.operandB) 
    }
    is(XOR) {
      io.aluResult := (io.operandA ^ io.operandB) 
    }
    is(SLL) {
      io.aluResult := (io.operandA << io.operandB(4,0)) 
    }
    is(SRL) {
      io.aluResult := (io.operandA >> io.operandB(4,0)) 
    }
    is(SRA) {
      io.aluResult := (io.operandA.asSInt >> io.operandB(4,0)).asUInt 
    }
    is(SLT) {
      io.aluResult := Cat(0.U(31.W), (io.operandA.asSInt < io.operandB.asSInt).asUInt)
    }
    is(SLTU) {
      io.aluResult := Cat(0.U(31.W), (io.operandA < io.operandB).asUInt)
    }
    is(PASSB) {
      io.aluResult := io.operandB 
    }
    is(JAL) {
      io.aluResult := io.operandA + io.operandB // operandA is the PC, return PC+4 for JAL
    }
  }
}

