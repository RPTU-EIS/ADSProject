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
  io.mapped := ALUOp.ADD
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
    is(uopc.BEQ)  { io.mapped := ALUOp.BEQ  }
    is(uopc.BNE)  { io.mapped := ALUOp.BNE  }
    is(uopc.BLT)  { io.mapped := ALUOp.BLT  }
    is(uopc.BGE)  { io.mapped := ALUOp.BGE  }
    is(uopc.BLTU) { io.mapped := ALUOp.BLTU }
    is(uopc.BGEU) { io.mapped := ALUOp.BGEU }
    is(uopc.JAL,  uopc.JALR)  { io.mapped := ALUOp.ADD  }
  }
}
class EXstage extends Module {
  val io = IO(new Bundle {
    val RD1E        = Input(UInt(32.W))
    val RD2E        = Input(UInt(32.W))
    val ImmExtE     = Input(UInt(32.W))
    val ALUSrcE     = Input(Bool())
    val rdE         = Input(UInt(5.W))
    val uopE        = Input(uopc())
    val WriteEnableE= Input(Bool())
    val XcptInvalidE= Input(Bool())
    val ForwardAE   = Input(UInt(2.W))
    val ForwardBE   = Input(UInt(2.W))
    val ResultW     = Input(UInt(32.W))
    val ALUResultM  = Input(UInt(32.W))
    val PCE         = Input(UInt(32.W))
    val PCPlus4E    = Input(UInt(32.W))
    val BranchE     = Input(Bool())
    val JumpE       = Input(Bool())

    val ALUResultE  = Output(UInt(32.W))
    val exceptionE  = Output(Bool())
    val rdOutE      = Output(UInt(5.W))
    val WriteEnableOutE= Output(Bool())
    val PCSrcE      = Output(Bool())
    val PCTargetE   = Output(UInt(32.W))
    val FlushE      = Output(Bool())  //NUEVO

    //BTB SIGNALS
    val PredictTakenE     = Input(Bool()) //THIS COMES FROM THE BTB IN IFstage
    val BTBUpdate         = Output(Bool())
    val BTBUpdatePC       = Output(UInt(32.W))
    val BTBUpdateTarget   = Output(UInt(32.W))
    val BTBMispredicted   = Output(Bool())
    val PredictTakenBTB   = Output(Bool())
  })
  val ALU        = Module(new ALU)
  val ALUcontrol = Module(new ALUcontrol)
  ALUcontrol.io.uop := io.uopE

  val srcA = Wire(UInt(32.W))
  val srcB = Wire(UInt(32.W))
  srcA := io.RD1E
  switch(io.ForwardAE) {
    is("b10".U) { srcA := io.ALUResultM }
    is("b01".U) { srcA := io.ResultW }
  }
  srcB := io.RD2E
  switch(io.ForwardBE) {
    is("b10".U) { srcB := io.ALUResultM }
    is("b01".U) { srcB := io.ResultW }
  }

  ALU.io.operandA  := srcA
  ALU.io.operandB  := Mux(io.ALUSrcE, io.ImmExtE, srcB)
  ALU.io.operation := ALUcontrol.io.mapped

  val jalr = io.JumpE && io.BranchE  // JALR condition
  val pcTargetAdder = io.PCE + io.ImmExtE   // pc + imm, for branches/JA(io.PCE + io.ImmExtE)(31, 0)L

  io.PCSrcE    := (io.BranchE && ALU.io.zero) || io.JumpE
  io.PCTargetE := Mux(jalr, (ALU.io.aluResult & ~1.U(32.W)), pcTargetAdder)

  io.ALUResultE   := Mux(io.JumpE, io.PCPlus4E, ALU.io.aluResult)  // rd = pc+4 for JAL/JALR
  io.exceptionE   := io.XcptInvalidE
  io.rdOutE       := io.rdE
  io.WriteEnableOutE := io.WriteEnableE

  //BTB SIGNALS
  io.BTBUpdate        := io.BranchE && !io.JumpE //BECAUSE WE USED BranchE=1 FOR JUMPS. EXCLUDED NOW
  io.BTBUpdatePC      := io.PCE
  io.BTBUpdateTarget  := io.PCTargetE
  io.BTBMispredicted  := (io.PCSrcE =/= io.PredictTakenE) && io.BranchE && !io.JumpE
  io.PredictTakenBTB  := io.PredictTakenE
  io.FlushE := io.JumpE || io.BTBMispredicted


  /*when(io.uopE === uopc.BNE) {
    printf("==============================================\n")
    printf(" EXECUTE STAGE SIGNALS (PC: %x)\n", io.PCE)
    printf("----------------------------------------------\n")
    printf(" PC            : %x\n", io.PCE)
    printf("==============================================\n\n")
  }
   */


}

//ToDo: Add your implementation according to the specification above here