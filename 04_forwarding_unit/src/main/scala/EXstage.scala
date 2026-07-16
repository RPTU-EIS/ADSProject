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

class ALUcontrol extends Module {
  val io = IO(new Bundle {
    val uop    = Input(uopc())
    val mapped = Output(ALUOp())
  })

    io.mapped := ALUOp.ADD // default works like ADD, which is the same as NOP

  switch(io.uop) {
    is(uopc.ADD,  uopc.ADDI)  { io.mapped := ALUOp.ADD  }
    is(uopc.SUB)              { io.mapped := ALUOp.SUB  }
    is(uopc.AND,  uopc.ANDI)  { io.mapped := ALUOp.AND  }
    is(uopc.OR,   uopc.ORI)   { io.mapped := ALUOp.OR   }
    is(uopc.XOR,  uopc.XORI)  { io.mapped := ALUOp.XOR  }
    is(uopc.SLL,  uopc.SLLI)  { io.mapped := ALUOp.SLL  }
    is(uopc.SRL,  uopc.SRLI)  { io.mapped := ALUOp.SRL  }
    is(uopc.SRA,  uopc.SRAI)  { io.mapped := ALUOp.SRA  }
    is(uopc.SLT,  uopc.SLTI)  { io.mapped := ALUOp.SLT  }
    is(uopc.SLTU, uopc.SLTIU) { io.mapped := ALUOp.SLTU }
  }
}

class EXstage extends Module {
  val io = IO(new Bundle {
    val operandA    = Input(UInt(32.W))
    val operandB    = Input(UInt(32.W))   // rs2 register value
    val immExtnd    = Input(UInt(32.W))   // sign/zero-extended immediate from ID
    val ALUsrc      = Input(Bool())       // false = rs2, true = immediate
    val rd_in       = Input(UInt(5.W))
    val uop         = Input(uopc())
    val wrten_in    = Input(Bool())       // passed through to WB
    val XcptInvalid = Input(Bool())
    val forwardSelA = Input(UInt(2.W)) // mux selector fordwarding unit
    val forwardSelB = Input(UInt(2.W)) // mux selector fordwarding unit
    val aluResultWB = Input(UInt(32.W)) // get reg from WB for fordwarding unit
    val aluResultMEM= Input(UInt(32.W)) // get reg from MEM for fordwarding unit

    val aluResult   = Output(UInt(32.W))
    val exception   = Output(Bool())
    val rd          = Output(UInt(5.W))
    val wrten       = Output(Bool())
  })

  val ALU        = Module(new ALU)
  val ALUcontrol = Module(new ALUcontrol)

  ALUcontrol.io.uop := io.uop

  val srcA = Wire(UInt(32.W))
  val srcB = Wire(UInt(32.W))

  srcA := io.operandA  // b00 default
  switch(io.forwardSelA) {
    is("b10".U) { srcA := io.aluResultMEM } // forward from MEM
    is("b01".U) { srcA := io.aluResultWB }  // forward from WB
  }

  srcB := io.operandB // b00 default
  switch(io.forwardSelB) {
    is("b10".U) { srcB := io.aluResultMEM }
    is("b01".U) { srcB := io.aluResultWB }
  }

  ALU.io.operandA  := srcA
  ALU.io.operandB  := Mux(io.ALUsrc, io.immExtnd, srcB)  // forwarded rs2 vs. immediate
  ALU.io.operation := ALUcontrol.io.mapped

  io.aluResult := ALU.io.aluResult
  io.exception := io.XcptInvalid
  io.rd        := io.rd_in
  io.wrten     := io.wrten_in
}

//ToDo: Add your implementation according to the specification above here