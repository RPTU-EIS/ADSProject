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

class EX extends Module {
  val io = IO(new Bundle {
    // Inputs from ID Barrier
    val uop         = Input(uopc())
    val inRD          = Input(UInt(5.W))
    val operandA    = Input(UInt(32.W))
    val operandB    = Input(UInt(32.W))
    val inXcptInvalid = Input(Bool())

    // Outputs to EX Barrier
    val aluResult   = Output(UInt(32.W))
    val outRD          = Output(UInt(5.W))
    val outXcptInvalid = Output(Bool())
  })

  // --- Instantiate ALU ---
  val alu = Module(new ALU())

  // --- Map uopc → ALUOp ---
  val aluOp = WireDefault(ALUOp.ADD)

  switch(io.uop) {
    // R-type
    is(ADD)  { aluOp := ALUOp.ADD  }
    is(SUB)  { aluOp := ALUOp.SUB  }
    is(AND)  { aluOp := ALUOp.AND  }
    is(OR)   { aluOp := ALUOp.OR   }
    is(XOR)  { aluOp := ALUOp.XOR  }
    is(SLL)  { aluOp := ALUOp.SLL  }
    is(SRL)  { aluOp := ALUOp.SRL  }
    is(SRA)  { aluOp := ALUOp.SRA  }
    is(SLT)  { aluOp := ALUOp.SLT  }
    is(SLTU) { aluOp := ALUOp.SLTU }
    // I-type (same ALU ops, operandB is already the immediate from ID stage)
    is(ADDI)  { aluOp := ALUOp.ADD  }
    is(ANDI)  { aluOp := ALUOp.AND  }
    is(ORI)   { aluOp := ALUOp.OR   }
    is(XORI)  { aluOp := ALUOp.XOR  }
    is(SLLI)  { aluOp := ALUOp.SLL  }
    is(SRLI)  { aluOp := ALUOp.SRL  }
    is(SRAI)  { aluOp := ALUOp.SRA  }
    is(SLTI)  { aluOp := ALUOp.SLT  }
    is(SLTIU) { aluOp := ALUOp.SLTU }
    // NOP → PASSB just passes 0 through harmlessly
    is(NOP)   { aluOp := ALUOp.PASSB }
  }

  // --- Wire ALU ---
  alu.io.operandA  := io.operandA
  alu.io.operandB  := io.operandB
  alu.io.operation := aluOp

  // --- Outputs ---
  io.aluResult   := alu.io.aluResult
  io.outRD          := io.inRD
  io.outXcptInvalid := io.inXcptInvalid
}