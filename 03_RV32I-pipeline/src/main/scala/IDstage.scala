// ADS I Class Project
// Pipelined RISC-V Core - ID Stage
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)
// Modified for Assignment 04: Added rs1, rs2, opBSel outputs for forwarding unit

package core_tile

import chisel3._
import chisel3.util._
import uopc._

// -----------------------------------------
// Decode Stage
// -----------------------------------------
class ID extends Module {
  val io = IO(new Bundle {
    // Input from IF barrier
    val instr = Input(UInt(32.W))

    // Register file read interface
    val regFileReq_A  = Output(new regFileReadReq)
    val regFileResp_A = Input(new regFileReadResp)
    val regFileReq_B  = Output(new regFileReadReq)
    val regFileResp_B = Input(new regFileReadResp)

    // Outputs to ID barrier
    val uop         = Output(uopc())       // Micro-operation code
    val rd          = Output(UInt(5.W))    // Destination register
    val rs1         = Output(UInt(5.W))    // Source register 1 (for forwarding unit)
    val rs2         = Output(UInt(5.W))    // Source register 2 (for forwarding unit)
    val operandA    = Output(UInt(32.W))   // First operand (from rs1)
    val operandB    = Output(UInt(32.W))   // Second operand (from rs2 or immediate)
    val opBSel      = Output(Bool())       // true = operandB is immediate (no forwarding on B)
    val XcptInvalid = Output(Bool())       // Invalid instruction flag
  })

  // =====================================================
  // Extract instruction fields
  // =====================================================
  val opcode = io.instr(6, 0)
  val rd     = io.instr(11, 7)
  val funct3 = io.instr(14, 12)
  val rs1    = io.instr(19, 15)
  val rs2    = io.instr(24, 20)
  val funct7 = io.instr(31, 25)

  // I-type immediate: sign-extend bits [31:20]
  val immI = io.instr(31, 20).asSInt.pad(32).asUInt

  // =====================================================
  // Request register file reads
  // =====================================================
  io.regFileReq_A.addr := rs1
  io.regFileReq_B.addr := rs2

  // =====================================================
  // Output source register addresses (for forwarding unit)
  // =====================================================
  io.rs1 := rs1
  io.rs2 := rs2

  // =====================================================
  // Decode instruction and determine micro-op
  // =====================================================

  // Default values
  io.uop := NOP
  io.XcptInvalid := false.B
  io.rd := rd
  io.operandA := io.regFileResp_A.data  // Default: rs1 value
  io.operandB := io.regFileResp_B.data  // Default: rs2 value (R-type)
  io.opBSel := false.B                  // Default: operandB from register (R-type)

  // R-type instructions (opcode = 0110011)
  when(opcode === Opcodes.R_TYPE) {
    io.operandB := io.regFileResp_B.data  // Use rs2 for R-type
    io.opBSel := false.B                  // operandB from register

    switch(funct3) {
      is(Funct3.ADD_SUB) {
        when(funct7 === Funct7.NORMAL) {
          io.uop := ADD
        }.elsewhen(funct7 === Funct7.ALT) {
          io.uop := SUB
        }.otherwise {
          io.uop := NOP
          io.XcptInvalid := true.B
        }
      }
      is(Funct3.SLL) {
        io.uop := SLL
      }
      is(Funct3.SLT) {
        io.uop := SLT
      }
      is(Funct3.SLTU) {
        io.uop := SLTU
      }
      is(Funct3.XOR) {
        io.uop := XOR
      }
      is(Funct3.SRL_SRA) {
        when(funct7 === Funct7.NORMAL) {
          io.uop := SRL
        }.elsewhen(funct7 === Funct7.ALT) {
          io.uop := SRA
        }.otherwise {
          io.uop := NOP
          io.XcptInvalid := true.B
        }
      }
      is(Funct3.OR) {
        io.uop := OR
      }
      is(Funct3.AND) {
        io.uop := AND
      }
    }
  }
    // I-type instructions (opcode = 0010011)
    .elsewhen(opcode === Opcodes.I_TYPE) {
      io.operandB := immI  // Use sign-extended immediate for I-type
      io.opBSel := true.B  // operandB is immediate — do NOT forward to operandB

      switch(funct3) {
        is(Funct3.ADD_SUB) {
          io.uop := ADDI
        }
        is(Funct3.SLT) {
          io.uop := SLTI
        }
        is(Funct3.SLTU) {
          io.uop := SLTIU
        }
        is(Funct3.XOR) {
          io.uop := XORI
        }
        is(Funct3.OR) {
          io.uop := ORI
        }
        is(Funct3.AND) {
          io.uop := ANDI
        }
        is(Funct3.SLL) {
          when(funct7 === Funct7.NORMAL) {
            io.uop := SLLI
            io.operandB := io.instr(24, 20).pad(32)  // shamt only
          }.otherwise {
            io.uop := NOP
            io.XcptInvalid := true.B
          }
        }
        is(Funct3.SRL_SRA) {
          when(funct7 === Funct7.NORMAL) {
            io.uop := SRLI
            io.operandB := io.instr(24, 20).pad(32)  // shamt only
          }.elsewhen(funct7 === Funct7.ALT) {
            io.uop := SRAI
            io.operandB := io.instr(24, 20).pad(32)  // shamt only
          }.otherwise {
            io.uop := NOP
            io.XcptInvalid := true.B
          }
        }
      }
    }
    // Invalid opcode
    .otherwise {
      io.uop := NOP
      io.XcptInvalid := true.B
    }
}
