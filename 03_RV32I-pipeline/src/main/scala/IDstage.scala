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
class ID extends Module {
  val io = IO(new Bundle {
    val inst = Input(UInt(32.W))
    val pc = Input(UInt(32.W))

    val regFileReq_A = Output(new RegFileReadReq) // Read request for rs1
    val regFileResp_A = Input(new RegFileReadResp) // Read response for rs1

    val regFileReq_B = Output(new RegFileReadReq) // Read request for rs2
    val regFileResp_B = Input(new RegFileReadResp) // Read response for rs2
    
    val uop = Output(UInt(7.W))
    val rd_idx = Output(UInt(5.W))
    val operandA = Output(UInt(32.W))
    val operandB = Output(UInt(32.W))
    val XcptInvalid = Output(Bool())

    val pcSel = Output(Bool()) // Input for PC selection signal from EX stage (for branch/jump)
    val targetPC = Output(UInt(32.W)) // Output for target PC to IF stage for branch/jump
  })
  
  val opcode = io.inst(6, 0) // Extract opcode from instruction
  val funct3 = io.inst(14, 12) // Extract funct3 from instruction
  val funct7 = io.inst(31, 25) // Extract funct7 from instruction
  val rd = io.inst(11, 7) // Extract rd from instruction
  val rs1 = io.inst(19, 15) // Extract rs1 from instruction
  val rs2 = io.inst(24, 20) // Extract rs2 from instruction
  val immI = io.inst(31, 20).asSInt.pad(32).asUInt // Extract immediate for I-type instructions
  val immJ = Cat(io.inst(31), io.inst(19, 12), io.inst(20), io.inst(30, 21), 0.U(1.W)).asSInt.pad(32).asUInt // Extract immediate for JAL instruction, Bit 0 as it is a multiples of 2 bytes
  
  io.uop := NOP.asUInt // Default to NOP
  io.rd_idx := rd // Output destination register index
  io.XcptInvalid := true.B // Default to invalid instruction, will be cleared for valid instructions

  io.regFileReq_A.addr := rs1 // Set read address for rs1
  io.regFileReq_B.addr := rs2 // Set read address for rs2
  io.operandA := Mux((opcode === "b1101111".U || opcode === "b1100111".U), io.pc, io.regFileResp_A.data) // Output operandA from regFile response
  io.operandB := Mux((opcode === "b1101111".U || opcode === "b1100111".U), 4.U(32.W), 
                 Mux(opcode === "b0010011".U, immI, io.regFileResp_B.data)) // Output operandB: immediate for I-type, regFile response for R-type, 4.U for J-type to calculate return address

  io.pcSel := false.B // Default to not taking branch/jump, will be set in EX stage if needed
  io.targetPC := Mux(opcode === "b1101111".U, io.pc + immJ, // Calculate target PC for JAL instruction, will be used in IF stage for PC update
                 Mux(opcode === "b1100111".U, ((io.regFileResp_A.data + immI).asUInt & "hfffffffe".U(32.W)), 0.U(32.W))) // Calculate target PC for JALR instruction, will be used in IF stage for PC update

  when(opcode === "b0110011".U) { // R-type instructions
    switch(funct3) {
      is("b000".U) { // ADD/SUB
        when(funct7 === "b0000000".U) { // ADD
          io.uop := ADD.asUInt
          io.XcptInvalid := false.B // Valid instruction
        } .elsewhen(funct7 === "b0100000".U) { // SUB
          io.uop := SUB.asUInt
          io.XcptInvalid := false.B // Valid instruction
        }
      }
      is("b100".U) { // XOR
        io.uop := XOR.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
      is("b110".U) { // OR
        io.uop := OR.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
      is("b111".U) { // AND
        io.uop := AND.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
      is("b001".U) { // SLL
        io.uop := SLL.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
      is("b101".U) { // SRL/SRA
        when(funct7 === "b0000000".U) { // SRL
          io.uop := SRL.asUInt
          io.XcptInvalid := false.B // Valid instruction
        } .elsewhen(funct7 === "b0100000".U) { // SRA
          io.uop := SRA.asUInt
          io.XcptInvalid := false.B // Valid instruction
        }
      }
      is("b010".U) { // SLT
        io.uop := SLT.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
      is("b011".U) { // SLTU
        io.uop := SLTU.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
    }
  } .elsewhen(opcode === "b0010011".U) { // I-type instructions (ADDI, ANDI, ORI, ...)
    switch(funct3) {
      is("b000".U) { // ADDI
        io.uop := ADDI.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
      is("b100".U) { // XORI
        io.uop := XORI.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
      is("b110".U) { // ORI
        io.uop := ORI.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
      is("b111".U) { // ANDI
        io.uop := ANDI.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
      is("b001".U) { // SLLI
        io.uop := SLLI.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
      is("b101".U) { // SRLI/SRAI
        when(funct7 === "b0000000".U) { // SRLI
          io.uop := SRLI.asUInt
          io.XcptInvalid := false.B // Valid instruction
        } .elsewhen(funct7 === "b0100000".U) { // SRAI
          io.uop := SRAI.asUInt
          io.XcptInvalid := false.B // Valid instruction
        }
      }
      is("b010".U) { // SLTI
        io.uop := SLTI.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
      is("b011".U) { // SLTIU
        io.uop := SLTIU.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
    }
  }. elsewhen(opcode === "b1100011".U) { //B-type instructions
    switch(funct3) {
      is("b000".U) { // BEQ
        io.uop := BEQ.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
      is("b001".U) { // BNE
        io.uop := BNE.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
      is("b100".U) { // BLT
        io.uop := BLT.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
      is("b101".U) { // BGE
        io.uop := BGE.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
      is("b110".U) { // BLTU
        io.uop := BLTU.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
      is("b111".U) { // BGEU
        io.uop := BGEU.asUInt
        io.XcptInvalid := false.B // Valid instruction
      }
    }
  }. elsewhen(opcode === "b1100111".U) { // JALR-type instructions
    switch(funct3) {
      is("b000".U) { // JALR
        io.uop := JALR.asUInt
        io.XcptInvalid := false.B // Valid instruction
        io.pcSel := true.B // Set PC selection signal for JALR instruction
      }
    }
  } .elsewhen(opcode === "b1101111".U) { // JAL-type instructions
    io.uop := JAL.asUInt
    io.XcptInvalid := false.B // Valid instruction
    io.pcSel := true.B // Set PC selection signal for JAL instruction
  }

}