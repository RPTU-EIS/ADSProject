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

import chisel3._                 // Import the basic Chisel hardware types
import chisel3.util._            // Import switch and is
import Assignment02.{ALU, ALUOp} // Import the ALU from Assignment 02
import uopc._                    // Import the decoded operation names

// -----------------------------------------
// Execute Stage
// -----------------------------------------

class EX extends Module {
  val io = IO(new Bundle {
    val uop         = Input(uopc())      // Get the operation decoded by ID stage
    val rd          = Input(UInt(5.W))   // Get the destination register
    val operandA    = Input(UInt(32.W))  // Get the first ALU operand
    val operandB    = Input(UInt(32.W))  // Get the second ALU operand
    val XcptInvalid = Input(Bool())      // Get invalid instruction flag

    val aluResult = Output(UInt(32.W))  // Send the ALU result to the next stage
    val outRD     = Output(UInt(5.W))   // Send the destination register to the next stage
    val exception = Output(Bool())      // Send the exception flag to the next stage
  })

  val alu = Module(new ALU())  // Create the ALU hardware

  alu.io.operandA := io.operandA  // Connect operandA to the ALU
  alu.io.operandB := io.operandB  // Connect operandB to the ALU
  alu.io.operation := ALUOp.ADD   // Use ADD as safe default operation

  switch(io.uop) {  // Convert the decoded instruction name into an ALU operation
    is(uopc.ADD)   { alu.io.operation := ALUOp.ADD  }
    is(uopc.ADDI)  { alu.io.operation := ALUOp.ADD  }
    is(uopc.SUB)   { alu.io.operation := ALUOp.SUB  }
    is(uopc.XOR)   { alu.io.operation := ALUOp.XOR  }
    is(uopc.XORI)  { alu.io.operation := ALUOp.XOR  }
    is(uopc.OR)    { alu.io.operation := ALUOp.OR   }
    is(uopc.ORI)   { alu.io.operation := ALUOp.OR   }
    is(uopc.AND)   { alu.io.operation := ALUOp.AND  }
    is(uopc.ANDI)  { alu.io.operation := ALUOp.AND  }
    is(uopc.SLL)   { alu.io.operation := ALUOp.SLL  }
    is(uopc.SLLI)  { alu.io.operation := ALUOp.SLL  }
    is(uopc.SRL)   { alu.io.operation := ALUOp.SRL  }
    is(uopc.SRLI)  { alu.io.operation := ALUOp.SRL  }
    is(uopc.SRA)   { alu.io.operation := ALUOp.SRA  }
    is(uopc.SRAI)  { alu.io.operation := ALUOp.SRA  }
    is(uopc.SLT)   { alu.io.operation := ALUOp.SLT  }
    is(uopc.SLTI)  { alu.io.operation := ALUOp.SLT  }
    is(uopc.SLTU)  { alu.io.operation := ALUOp.SLTU }
    is(uopc.SLTIU) { alu.io.operation := ALUOp.SLTU }
  }

  io.aluResult := Mux(io.uop === uopc.NOP, 0.U, alu.io.aluResult)  // Send zero for NOP, otherwise send ALU result
  io.outRD     := io.rd                                            // Pass the destination register forward
  io.exception := io.XcptInvalid                                   // Pass the invalid flag forward
}
