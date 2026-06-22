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

class EXStage extends Module {

  val io = IO(new Bundle {

    val inUOP         = Input(uopc.Type())
    val inRD          = Input(UInt(5.W))
    val inOperandA    = Input(UInt(32.W))
    val inOperandB    = Input(UInt(32.W))
    val inXcptInvalid = Input(Bool())

    val aluResult     = Output(UInt(32.W))
    val rd            = Output(UInt(5.W))
    val exception     = Output(Bool())

    // For Forwarding Unit
    val inRS1   = Input(UInt(5.W))              // Source register 1 index for forwarding unit
    val inRS2   = Input(UInt(5.W))              // Source register 2 index for forwarding unit
    val inWrEn  = Input(Bool())                 // Write enable signal for forwarding unit
    val outWrEn = Output(Bool())                // Forwarding unit write enable output

    // for source 1
    val ExBarrier_RD     = Input(UInt(5.W))              // Destination register index from EX barrier for forwarding unit
    val ExBarrier_Result = Input(UInt(32.W))             // ALU result from EX barrier for forwarding unit
    val ExBarrier_WrEn   = Input(Bool())                 // Write enable signal from EX barrier for forwarding unit
    // for source 2
    val MemBarrier_RD     = Input(UInt(5.W))              // Destination register index from MEM barrier for forwarding unit
    val MemBarrier_Result = Input(UInt(32.W))             // ALU result from MEM barrier for forwarding unit
    val MemBarrier_WrEn   = Input(Bool())                 // Write enable signal from MEM barrier for forwarding unit
  })
  // Forwarding Mux Logic
  def forwardMux(raw: UInt, rs: UInt, exWr: Bool, exRd: UInt, exRes: UInt, memWr: Bool, memRd: UInt, memRes: UInt): UInt = {
    val select = WireDefault(0.U(2.W)) // 0=raw, 1=EX, 2=MEM
    
    when(exWr && (exRd =/= 0.U) && (exRd === rs)) {select := 1.U} 
    .elsewhen(memWr && (memRd =/= 0.U) && (memRd === rs)) {select := 2.U}

    MuxLookup(select, raw, Seq(                    // muxlookup syntax: select, default, Seq of (key -> value) pairs
      0.U -> raw,
      1.U -> exRes,
      2.U -> memRes
    ))
  }

  // Apply forwarding logic to operands
  val opA_fwd = forwardMux(io.inOperandA, io.inRS1, io.ExBarrier_WrEn, io.ExBarrier_RD, io.ExBarrier_Result, io.MemBarrier_WrEn, io.MemBarrier_RD, io.MemBarrier_Result)
  val opB_fwd = forwardMux(io.inOperandB, io.inRS2, io.ExBarrier_WrEn, io.ExBarrier_RD, io.ExBarrier_Result, io.MemBarrier_WrEn, io.MemBarrier_RD, io.MemBarrier_Result)

  val alu = Module(new ALU)
  //Modified for Forwarding
  alu.io.operandA := opA_fwd
  alu.io.operandB := opB_fwd

  // default
  alu.io.operation := ALUOp.PASSB
   // Add this line right before your ALU switch statement!
  printf(p"DEBUG: UOP = ${io.inUOP}, OpA = ${opA_fwd}, OpB = ${opB_fwd}\n")
  switch(io.inUOP) {

    is(uopc.ADD)   { alu.io.operation := ALUOp.ADD }      // For R-type ADD instruction
    is(uopc.ADDI)  { alu.io.operation := ALUOp.ADD }      // For I-type ADDI instruction, we can use the same ALU operation as ADD since the immediate value is already prepared in operandB

    is(uopc.SUB)   { alu.io.operation := ALUOp.SUB }

    is(uopc.AND)   { alu.io.operation := ALUOp.AND }
    is(uopc.ANDI)  { alu.io.operation := ALUOp.AND }

    is(uopc.OR)    { alu.io.operation := ALUOp.OR }
    is(uopc.ORI)   { alu.io.operation := ALUOp.OR }

    is(uopc.XOR)   { alu.io.operation := ALUOp.XOR }
    is(uopc.XORI)  { alu.io.operation := ALUOp.XOR }

    is(uopc.SLL)   { alu.io.operation := ALUOp.SLL }
    is(uopc.SLLI)  { alu.io.operation := ALUOp.SLL }

    is(uopc.SRL)   { alu.io.operation := ALUOp.SRL }
    is(uopc.SRLI)  { alu.io.operation := ALUOp.SRL }

    is(uopc.SRA)   { alu.io.operation := ALUOp.SRA }
    is(uopc.SRAI)  { alu.io.operation := ALUOp.SRA }

    is(uopc.SLT)   { alu.io.operation := ALUOp.SLT }
    is(uopc.SLTI)  { alu.io.operation := ALUOp.SLT }

    is(uopc.SLTU)  { alu.io.operation := ALUOp.SLTU }
    is(uopc.SLTIU) { alu.io.operation := ALUOp.SLTU }
  }

  io.aluResult := alu.io.aluResult
  io.rd := io.inRD
  io.exception := io.inXcptInvalid
  io.outWrEn := io.inWrEn                              // Pass the write enable signal to the next stage for forwarding unit
}