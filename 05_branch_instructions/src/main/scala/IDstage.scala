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
    imm: immediate value (12-bit for I/B-type, 20-bit for J-type, sign-extended)

Register File Interfaces:
    regFileReq_A, regFileResp_A: read port for rs1 operand
    regFileReq_B, regFileResp_B: read port for rs2 operand

Internal Signals:
    Combinational decoders for instructions

Functionality:
    Decode opcode to determine instruction and identify operation (ADD, SUB, XOR, ...)
    Handle branch and jump instructions
    Pass PC for return address calculation in JAL instructions

Outputs:
    uop: micro-operation code (identifies instruction type)
    rd: destination register index
    operandA: first operand
    operandB: second operand
    pc: program counter (for return address in JAL)
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
    val instr         = Input(UInt(32.W))
    val pc            = Input(UInt(32.W))
    val regFileReq_A  = Output(new regFileReadReq)
    val regFileResp_A = Input(new regFileReadResp)
    val regFileReq_B  = Output(new regFileReadReq)
    val regFileResp_B = Input(new regFileReadResp)
    val uop           = Output(uopc())
    val rd            = Output(UInt(5.W))
    val rs1           = Output(UInt(5.W))
    val rs2           = Output(UInt(5.W))
    val operandA      = Output(UInt(32.W))
    val operandB      = Output(UInt(32.W))
    val pcOut         = Output(UInt(32.W))
    val XcptInvalid   = Output(Bool())
    val wr_en         = Output(Bool())
  })

  val opcode = io.instr(6,  0)
  val rdF    = io.instr(11, 7)
  val funct3 = io.instr(14, 12)
  val rs1F   = io.instr(19, 15)
  val rs2F   = io.instr(24, 20)
  val funct7 = io.instr(31, 25)

  // I-type immediate (12-bit, sign-extended)
  val immI   = Cat(Fill(20, io.instr(31)), io.instr(31, 20))

  // B-type immediate (12-bit, sign-extended): [31:25] and [11:7] contain bits [12|10:5] and [4:1|11]
  val immB   = Cat(Fill(20, io.instr(31)), io.instr(31), io.instr(7), io.instr(30, 25), io.instr(11, 8), 0.U(1.W))

  // J-type immediate (20-bit, sign-extended): [31:12] contain bits [20|10:1|11|19:12]
  val immJ   = Cat(Fill(12, io.instr(31)), io.instr(19, 12), io.instr(20), io.instr(30, 21), 0.U(1.W))

  io.regFileReq_A.addr := rs1F
  io.regFileReq_B.addr := rs2F

  io.uop         := uopc.isNOP
  io.rd          := rdF
  io.rs1         := rs1F
  io.rs2         := rs2F
  io.operandA    := io.regFileResp_A.data
  io.operandB    := io.regFileResp_B.data
  io.pcOut       := io.pc
  io.XcptInvalid := false.B
  io.wr_en       := false.B

  // R-type instructions (opcode 0x33)
  when(opcode === "b0110011".U) {
    io.wr_en := true.B
    when(funct3 === "b000".U && funct7 === "b0000000".U)      { io.uop := uopc.isADD  }
    .elsewhen(funct3 === "b000".U && funct7 === "b0100000".U) { io.uop := uopc.isSUB  }
    .elsewhen(funct3 === "b001".U && funct7 === "b0000000".U) { io.uop := uopc.isSLL  }
    .elsewhen(funct3 === "b010".U && funct7 === "b0000000".U) { io.uop := uopc.isSLT  }
    .elsewhen(funct3 === "b011".U && funct7 === "b0000000".U) { io.uop := uopc.isSLTU }
    .elsewhen(funct3 === "b100".U && funct7 === "b0000000".U) { io.uop := uopc.isXOR  }
    .elsewhen(funct3 === "b101".U && funct7 === "b0000000".U) { io.uop := uopc.isSRL  }
    .elsewhen(funct3 === "b101".U && funct7 === "b0100000".U) { io.uop := uopc.isSRA  }
    .elsewhen(funct3 === "b110".U && funct7 === "b0000000".U) { io.uop := uopc.isOR   }
    .elsewhen(funct3 === "b111".U && funct7 === "b0000000".U) { io.uop := uopc.isAND  }
    .otherwise                                                 { io.XcptInvalid := true.B; io.wr_en := false.B }
  // I-type instructions (opcode 0x13)
  }.elsewhen(opcode === "b0010011".U) {
    io.wr_en    := true.B
    io.operandB := immI
    when(funct3 === "b000".U)                                  { io.uop := uopc.isADDI  }
    .elsewhen(funct3 === "b010".U)                             { io.uop := uopc.isSLTI  }
    .elsewhen(funct3 === "b011".U)                             { io.uop := uopc.isSLTIU }
    .elsewhen(funct3 === "b100".U)                             { io.uop := uopc.isXORI  }
    .elsewhen(funct3 === "b110".U)                             { io.uop := uopc.isORI   }
    .elsewhen(funct3 === "b111".U)                             { io.uop := uopc.isANDI  }
    .elsewhen(funct3 === "b001".U && funct7 === "b0000000".U)  { io.uop := uopc.isSLLI  }
    .elsewhen(funct3 === "b101".U && funct7 === "b0000000".U)  { io.uop := uopc.isSRLI  }
    .elsewhen(funct3 === "b101".U && funct7 === "b0100000".U)  { io.uop := uopc.isSRAI  }
    .otherwise                                                 { io.XcptInvalid := true.B; io.wr_en := false.B }
  // B-type instructions (opcode 0x63) — do NOT write to rd
  }.elsewhen(opcode === "b1100011".U) {
    io.operandB := immB
    when(funct3 === "b000".U)      { io.uop := uopc.isBEQ  }
    .elsewhen(funct3 === "b001".U) { io.uop := uopc.isBNE  }
    .elsewhen(funct3 === "b100".U) { io.uop := uopc.isBLT  }
    .elsewhen(funct3 === "b101".U) { io.uop := uopc.isBGE  }
    .elsewhen(funct3 === "b110".U) { io.uop := uopc.isBLTU }
    .elsewhen(funct3 === "b111".U) { io.uop := uopc.isBGEU }
    .otherwise                     { io.XcptInvalid := true.B }
  // JAL (opcode 0x6f) — writes return address to rd
  }.elsewhen(opcode === "b1101111".U) {
    io.uop      := uopc.isJAL
    io.operandB := immJ
    io.wr_en    := true.B
  // JALR (opcode 0x67) — writes return address to rd
  }.elsewhen(opcode === "b1100111".U) {
    when(funct3 === "b000".U) {
      io.uop      := uopc.isJALR
      io.operandB := immI
      io.wr_en    := true.B
    }.otherwise {
      io.XcptInvalid := true.B
    }
  }.otherwise {
    io.XcptInvalid := true.B
  }
}
