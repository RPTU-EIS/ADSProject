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

class IDStage extends Module {
  val io = IO(new Bundle {

    val instr   = Input(UInt(32.W))
    val inPC    = Input(UInt(32.W))
    val inFlush = Input(Bool())

    //Outputs
    val uop = Output(uopc.Type())
    val rd = Output(UInt(5.W))
    val operandA = Output(UInt(32.W))
    val operandB = Output(UInt(32.W))
    val XcptInvalid = Output(Bool())
    val wrEn        = Output(Bool())

    //Jump Control
    val outPCSrc = Output(Bool())
    val outPC    = Output(UInt(32.W))

    //Branch Control
    val outBranchDest = Output(UInt(32.W))

    //Ports For FeedBack for WB stage
    val wb_req_en = Input(Bool())
    val wb_req_addr = Input(UInt(5.W))
    val wb_req_data = Input(UInt(32.W))

    //Forwarding Unit
    val rs1 = Output(UInt(5.W))
    val rs2 = Output(UInt(5.W))

  })

  val regFile = Module(new regFile)

  val rs1 = io.instr(19, 15)
  val rs2 = io.instr(24, 20)

  //Register Interfaces
  regFile.io.req_1.addr := rs1
  regFile.io.req_2.addr := rs2

  // Connecting the feedback loop
  regFile.io.req_3.wr_en := io.wb_req_en
  regFile.io.req_3.addr  := io.wb_req_addr
  regFile.io.req_3.data  := io.wb_req_data


  //Extraction
  val opcode = io.instr(6, 0)
  val funct3 = io.instr(14, 12)
  val funct7 = io.instr(31, 25)
  val rd = io.instr(11, 7)

  //Forwarding Unit
  io.rs1 := rs1
  io.rs2 := rs2

  //Type Detection
  val isRType = (opcode === "b0110011".U)  // R-type
  val isIType = (opcode === "b0010011".U)  // I-type
  val isBranch = (opcode === "b1100011".U)
  val isJAL = (opcode === "b1101111".U)
  val isJALR = (opcode === "b1100111".U)

  io.wrEn := isRType || isIType || isJAL || (isJALR && funct3 === "b000".U)

  //I-type
  val immI = Cat(Fill(20, io.instr(31)), io.instr(31, 20)).asSInt.asUInt
  //B-type
  val immB = Cat(io.instr(31), io.instr(7), io.instr(30,25), io.instr(11,8), 0.U(1.W)).asSInt.asUInt
  //J-Type
  val immJ = Cat(io.instr(31), io.instr(19,12), io.instr(20), io.instr(30,21), 0.U(1.W)).asSInt.asUInt

  // Default values
  io.uop := uopc.NOP
  io.rd := rd
  io.XcptInvalid   := true.B
  io.outPCSrc      := false.B
  io.outPC         := 0.U
  io.outBranchDest := 0.U
  io.operandA := regFile.io.resp_1.data
  io.operandB := regFile.io.resp_2.data

  when(io.inFlush){

    io.uop         := uopc.NOP
    io.rd          := 0.U
    io.XcptInvalid := false.B
    io.outPCSrc    := false.B
  }.otherwise{

    // Decode R-type instructions
    when(isRType) {
      io.XcptInvalid := false.B

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
    .elsewhen(isIType) {
      io.XcptInvalid := false.B
      io.operandB    := immI

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

    //B-Type Instruction
    .elsewhen(isBranch){
      io.XcptInvalid := false.B
      io.rd          := 0.U
      io.wrEn        :=false.B

      io.outBranchDest := io.inPC + immB

      switch(funct3) {
        is("b000".U) { io.uop := uopc.BEQ }
        is("b001".U) { io.uop := uopc.BNE }
        is("b100".U) { io.uop := uopc.BLT }
        is("b101".U) { io.uop := uopc.BGE }
        is("b110".U) { io.uop := uopc.BLTU }
        is("b111".U) { io.uop := uopc.BGEU }
      }
    }

    //J-Type Instructions
    .elsewhen(isJAL){
      io.XcptInvalid := false.B
      io.uop := uopc.JAL
      io.rd := rd
      io.wrEn := true.B

      // Return address = PC + 4
      io.operandA := io.inPC + 4.U
      io.operandB := 0.U


      io.outBranchDest := io.inPC + immJ

      // Jump target and PC select
      io.outPCSrc := true.B
      io.outPC := io.inPC + immJ

    }

    .elsewhen(isJALR && funct3 === "b000".U) {
      io.XcptInvalid := false.B
      io.uop := uopc.JALR
      io.rd := rd
      io.wrEn := true.B

      // Return address = PC + 4
      io.operandA := io.inPC + 4.U
      io.operandB := 0.U



      // JALR target: rs1 + imm (clears LSB for 2-byte alignment)
      val jalrTarget = (regFile.io.resp_1.data + immI) & (~1.U(32.W))
      io.outPCSrc := true.B
      io.outPC := jalrTarget
    }
  }
}