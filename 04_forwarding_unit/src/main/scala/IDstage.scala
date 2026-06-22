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

    val instr = Input(UInt(32.W))

    //Outputs
    val uop = Output(uopc.Type())           //opcode = instruction field in hardware encoding, uop = decoded operation type used inside your design

    val rd = Output(UInt(5.W))              // Destination register index (5 bits for 32 registers)
    val operandA = Output(UInt(32.W))
    val operandB = Output(UInt(32.W))
    val XcptInvalid = Output(Bool())


    //Ports For FeedBack for WB stage
    val wb_req_en = Input(Bool())          
    val wb_req_addr = Input(UInt(5.W))     
    val wb_req_data = Input(UInt(32.W))



    // For Forwarding Unit
    val rs1   = Output(UInt(5.W))             // Source register 1 index
    val rs2   = Output(UInt(5.W))             // Source register 2 index
    val wr_en = Output(Bool())                // Write enable signal for forwarding unit

  })

  val regFile = Module(new regFile)        // Instantiate the register file module

  io.rs1 := io.instr(19, 15)               // Extract rs1 from the instruction (bits 19-15)
  io.rs2 := io.instr(24, 20)               // Extract rs2 from the instruction (bits 24-20)

  //Register Interfaces
  regFile.io.req_1.addr := io.rs1             // Connect rs1 to the first read port of the register file
  regFile.io.req_2.addr := io.rs2             // Connect rs2 to the second read port of the register file
  
  // Connecting the feedback loop
  regFile.io.req_3.wr_en := io.wb_req_en   // Write enable signal from the WB stage
  regFile.io.req_3.addr  := io.wb_req_addr // Write address from the WB stage
  regFile.io.req_3.data  := io.wb_req_data // Write data from the WB stage


  //Extraction
  val opcode = io.instr(6, 0)              // Extract opcode from the instruction (bits 6-0)
  val funct3 = io.instr(14, 12)            // Extract funct3 from the instruction (bits 14-12)
  val funct7 = io.instr(31, 25)            // Extract funct7 from the instruction (bits 31-25)
  val rd = io.instr(11, 7)                 // Extract rd from the instruction (bits 11-7)

  val imm = Cat(Fill(20, io.instr(31)), io.instr(31, 20)).asSInt.asUInt  //bit 32 is repeated 20 times for sign-extension, then concatenated with bits 31-20 to form the 32-bit immediate value  
//first interprets the 32-bit result as a signed integer (.asSInt), then converts it back to unsigned (.asUInt).
//The Cat function in Chisel places its arguments in order, so the sign-extended bits go on the high end and the original immediate on the low end, forming a proper 32-bit sign-extended immediate
 
 
  // Default values
  io.uop := uopc.NOP                      // Default to NOP (no operation) for unsupported instructions
  io.rd := rd
  io.XcptInvalid := false.B               // Default to false, set to true for invalid 

  val isRType = (opcode === "b0110011".U)  // R-type          // R-type instructions are identified by opcode 0110011
  val isIType = (opcode === "b0010011".U)  // I-type          // I-type instructions are identified by opcode 0010011

printf(p"ID: instr=0x${io.instr}, opcode=0x${opcode}, funct3=0x${funct3}, funct7=0x${funct7}, rd=0x${rd}\n")
 printf(p"ID: isRType=${isRType}, isIType=${isIType}\n")
  io.wr_en := isRType || isIType          // Write enable is true for R-type and I-type instructions

  io.operandA := regFile.io.resp_1.data    // operandA is always sourced from rs1, so we connect it to the first read port of the register file

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

