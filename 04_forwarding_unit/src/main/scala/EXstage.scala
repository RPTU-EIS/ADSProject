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

class EX extends Module {
  val io = IO(new Bundle {
    val uop         = Input(uopc())
    val operandA    = Input(UInt(32.W))
    val operandB    = Input(UInt(32.W))
    val rd          = Input(UInt(5.W))
    val rs1         = Input(UInt(5.W))
    val rs2         = Input(UInt(5.W))
    val XcptInvalid = Input(Bool())

    // Forwarding inputs from MEM and WB stages
    val aluResult_MEM = Input(UInt(32.W))
    val rd_MEM        = Input(UInt(5.W))
    val wrEn_MEM      = Input(Bool())
    val aluResult_WB  = Input(UInt(32.W))
    val rd_WB         = Input(UInt(5.W))
    val wrEn_WB       = Input(Bool())

    val aluResult   = Output(UInt(32.W))
    val rdOut       = Output(UInt(5.W))
    val exception   = Output(Bool())
  })

  val alu = Module(new ALU)

  // Forwarding logic
  val forwardA = Wire(UInt(2.W))
  val forwardB = Wire(UInt(2.W))

  // Forward operand A: prioritize MEM over WB
  forwardA := Mux(io.wrEn_MEM && (io.rs1 === io.rd_MEM) && (io.rs1 =/= 0.U), 1.U,
                  Mux(io.wrEn_WB && (io.rs1 === io.rd_WB) && (io.rs1 =/= 0.U), 2.U, 0.U))

  // Forward operand B: prioritize MEM over WB
  forwardB := Mux(io.wrEn_MEM && (io.rs2 === io.rd_MEM) && (io.rs2 =/= 0.U), 1.U,
                  Mux(io.wrEn_WB && (io.rs2 === io.rd_WB) && (io.rs2 =/= 0.U), 2.U, 0.U))

  // Select ALU operand A
  val aluOperandA = Mux(forwardA === 1.U, io.aluResult_MEM,
                         Mux(forwardA === 2.U, io.aluResult_WB,
                             io.operandA))

  // Select ALU operand B
  val aluOperandB = Mux(forwardB === 1.U, io.aluResult_MEM,
                         Mux(forwardB === 2.U, io.aluResult_WB,
                             io.operandB))

  alu.io.operandA  := aluOperandA
  alu.io.operandB  := aluOperandB
  alu.io.operation := ALUOp.ADD

  switch(io.uop) {
    is(uopc.isADD)   { alu.io.operation := ALUOp.ADD  }
    is(uopc.isSUB)   { alu.io.operation := ALUOp.SUB  }
    is(uopc.isAND)   { alu.io.operation := ALUOp.AND  }
    is(uopc.isOR)    { alu.io.operation := ALUOp.OR   }
    is(uopc.isXOR)   { alu.io.operation := ALUOp.XOR  }
    is(uopc.isSLL)   { alu.io.operation := ALUOp.SLL  }
    is(uopc.isSRL)   { alu.io.operation := ALUOp.SRL  }
    is(uopc.isSRA)   { alu.io.operation := ALUOp.SRA  }
    is(uopc.isSLT)   { alu.io.operation := ALUOp.SLT  }
    is(uopc.isSLTU)  { alu.io.operation := ALUOp.SLTU }
    is(uopc.isADDI)  { alu.io.operation := ALUOp.ADD  }
    is(uopc.isANDI)  { alu.io.operation := ALUOp.AND  }
    is(uopc.isORI)   { alu.io.operation := ALUOp.OR   }
    is(uopc.isXORI)  { alu.io.operation := ALUOp.XOR  }
    is(uopc.isSLLI)  { alu.io.operation := ALUOp.SLL  }
    is(uopc.isSRLI)  { alu.io.operation := ALUOp.SRL  }
    is(uopc.isSRAI)  { alu.io.operation := ALUOp.SRA  }
    is(uopc.isSLTI)  { alu.io.operation := ALUOp.SLT  }
    is(uopc.isSLTIU) { alu.io.operation := ALUOp.SLTU }
    is(uopc.isNOP)   { alu.io.operation := ALUOp.ADD  }
  }

  io.aluResult := alu.io.aluResult
  io.rdOut     := io.rd
  io.exception := io.XcptInvalid
}
