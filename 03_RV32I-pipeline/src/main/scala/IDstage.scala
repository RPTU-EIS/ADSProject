// ADS I Class Project
// Pipelined RISC-V Core - ID Stage
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
Instruction Decode (ID) Stage: decoding and operand fetch

Extracted Fields from 32-bit Instruction (see RISC-V specification for reference):
    opcode: instruction format identifier
    funct3: selects variant within instruction format
    funct7: further specifies operation type (R-type only)
    rd: destination register address
    rs1: first source register address
    rs2: second source register address
    imm: 12-bit immediate value (I-type, sign-extended)

Register File Interfaces:
    regFileReq_A, regFileResp_A: read port for rs1 operand
    regFileReq_B, regFileResp_B: read port for rs2 operand

Internal Signals:
    Combinational decoders for instructions

Functionality:
    Decode opcode to determine instruction and identify operation (ADD, SUB, XOR, ...)
    Output: uop (operation code), rd, operandA (from rs1), operandB (rs2 or immediate)

Outputs:
    uop: micro-operation code (identifies instruction type)
    rd: destination register index
    operandA: first operand
    operandB: second operand 
    XcptInvalid: exception flag for invalid instructions
*/

package core_tile

import chisel3._
import chisel3.util._
import uopc._

// -----------------------------------------
// Decode Stage
// -----------------------------------------

//ToDo: Add your implementation according to the specification above here 

class ID extends Module {
  val io = IO(new Bundle {
    // Input from IF Barrier
    val instr       = Input(UInt(32.W))

    // Outputs to ID Barrier
    val uop         = Output(uopc())
    val rd          = Output(UInt(5.W))
    val operandA    = Output(UInt(32.W))
    val operandB    = Output(UInt(32.W))
    val xcptInvalid = Output(Bool())

    // Write port from WB stage
    val req_3       = Input(new regFileWriteReq)
  })

  // --- Instantiate Register File ---
  val rf = Module(new regFile())

  // --- Extract instruction fields ---
  val opcode = io.instr(6, 0)
  val rd     = io.instr(11, 7)
  val funct3 = io.instr(14, 12)
  val rs1    = io.instr(19, 15)
  val rs2    = io.instr(24, 20)
  val funct7 = io.instr(31, 25)

  // --- Sign-extended 12-bit immediate (I-type) ---
  val imm = Cat(Fill(20, io.instr(31)), io.instr(31, 20))

  // --- Register File: wire read ports ---
  rf.io.req_1.addr := rs1
  rf.io.req_2.addr := rs2

  // --- Register File: wire write port from WB ---
  rf.io.req_3 := io.req_3

  // --- Defaults ---
  io.uop         := NOP
  io.xcptInvalid := false.B
  io.rd          := rd
  io.operandA    := rf.io.resp_1.data
  io.operandB    := rf.io.resp_2.data

  // -----------------------------------------
  // Decode Logic
  // -----------------------------------------

  switch(opcode) {

    // -------------------------
    // R-type: opcode = 0110011
    // -------------------------
    is("b0110011".U) {
      io.operandB := rf.io.resp_2.data   // R-type always uses rs2

      switch(funct3) {
        is("b000".U) {  // ADD / SUB
          switch(funct7) {
            is("b0000000".U) { io.uop := ADD }
            is("b0100000".U) { io.uop := SUB }
          }
          when(funct7 =/= "b0000000".U && funct7 =/= "b0100000".U) {
            io.xcptInvalid := true.B
          }
        }
        is("b001".U) {  // SLL
          io.uop := SLL
          when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B }
        }
       is("b010".U) {  // SLT
          io.uop := SLT
          when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B }
        } 
        is("b011".U) {  // SLTU
          io.uop := SLTU
          when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B }
        }
        is("b100".U) {  // XOR
          io.uop := XOR
          when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B }
        }
        is("b101".U) {  // SRL / SRA
          switch(funct7) {
            is("b0000000".U) { io.uop := SRL }
            is("b0100000".U) { io.uop := SRA }
          }
          when(funct7 =/= "b0000000".U && funct7 =/= "b0100000".U) {
            io.xcptInvalid := true.B
          }
        }
        is("b110".U) {  // OR
          io.uop := OR
          when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B }
        }
        is("b111".U) {  // AND
          io.uop := AND
          when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B }
        }
      }
    }

    // -------------------------
    // I-type: opcode = 0010011
    // -------------------------
    is("b0010011".U) {
      io.operandB := imm                 // I-type always uses immediate

      switch(funct3) {
        is("b000".U) { io.uop := ADDI }
        is("b010".U) { io.uop := SLTI  }
        is("b011".U) { io.uop := SLTIU }
        is("b100".U) { io.uop := XORI  }
        is("b110".U) { io.uop := ORI   }
        is("b111".U) { io.uop := ANDI  }
        is("b001".U) {  // SLLI
          io.uop := SLLI
          when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B }
        }
        is("b101".U) {  // SRLI / SRAI
          switch(funct7) {
            is("b0000000".U) { io.uop := SRLI }
            is("b0100000".U) { io.uop := SRAI }
          }
          when(funct7 =/= "b0000000".U && funct7 =/= "b0100000".U) {
            io.xcptInvalid := true.B
          }
        }
      }
    }
  }

  // --- Unrecognized opcode ---
  when(opcode =/= "b0110011".U && opcode =/= "b0010011".U) {
    io.uop         := NOP
    io.xcptInvalid := true.B
  }
}