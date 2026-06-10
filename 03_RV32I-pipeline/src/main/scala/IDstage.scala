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

class IDstage extends Module {
  val io = IO(new Bundle {

    val instr = Input(UInt(32.W))

    //Outputs
    val uop = Output(uopc.Type())
    val rd = Output(UInt(5.W))
    val operandA = Output(UInt(32.W))
    val operandB = Output(UInt(32.W))
    val XcptInvalid = Output(Bool())
  })

  val regFile = Module(new regFile)

  val rs1 = io.instr(19, 15)
  val rs2 = io.instr(24, 20)

  //Register Interfaces
  regFile.io.req_1.addr := rs1
  regFile.io.req_2.addr := rs2
  //Write port is not used here
  regFile.io.req_3.wr_en := false.B
  regFile.io.req_3.addr := 0.U
  regFile.io.req_3.data := 0.U


  //Extraction
  val opcode = io.instr(6, 0)
  val funct3 = io.instr(14, 12)
  val funct7 = io.instr(31, 25)
  val rd = io.instr(11, 7)

  val imm = Cat(Fill(20, io.instr(31)), io.instr(31, 20)).asSInt.asUInt

  // Default values
  io.uop := uopc.NOP
  io.rd := rd
  io.XcptInvalid := false.B

  val isRType = (opcode === "b0110011".U)  // R-type
  val isIType = (opcode === "b0010011".U)  // I-type

  io.operandA := regFile.io.resp_1.data

  // operandB selection: rs2 (R-type) or immediate (I-type)
  when(isIType) {
    io.operandB := imm
  }.otherwise {
    io.operandB := regFile.io.resp_2.data  // R-type uses rs2
  }

  // Decode R-type instructions
  when(isRType) {
    switch(funct3) {
      is("b000".U) {  // ADD or SUB
        when(funct7 === 0.U) {
          io.uop := uopc.ADD
        }.elsewhen(funct7 === "b0100000".U) {
          io.uop := uopc.SUB
        }
      }
      is("b001".U) { io.uop := uopc.SLL }
      is("b010".U) { io.uop := uopc.SLT }
      is("b011".U) { io.uop := uopc.SLTU }
      is("b100".U) { io.uop := uopc.XOR }
      is("b101".U) {  // SRL or SRA
        when(funct7 === 0.U) {
          io.uop := uopc.SRL
        }.elsewhen(funct7 === "b0100000".U) {
          io.uop := uopc.SRA
        }
      }
      is("b110".U) { io.uop := uopc.OR }
      is("b111".U) { io.uop := uopc.AND }
    }
  }

  // Decode I-type instructions
  when(isIType) {
    switch(funct3) {
      is("b000".U) { io.uop := uopc.ADDI }
      is("b001".U) { io.uop := uopc.SLLI }
      is("b010".U) { io.uop := uopc.SLTI }
      is("b011".U) { io.uop := uopc.SLTIU }
      is("b100".U) { io.uop := uopc.XORI }
      is("b101".U) {  // SRLI or SRAI
        when(funct7 === 0.U) {
          io.uop := uopc.SRLI
        }.elsewhen(funct7 === "b0100000".U) {
          io.uop := uopc.SRAI
        }
      }
      is("b110".U) { io.uop := uopc.ORI }
      is("b111".U) { io.uop := uopc.ANDI }
    }
  }

  // Set invalid instruction flag for unsupported opcodes
  when(!isRType && !isIType && opcode =/= 0.U) {
    io.XcptInvalid := true.B
  }
}

