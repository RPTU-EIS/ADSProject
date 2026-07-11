// ADS I Class Project
// Pipelined RISC-V Core - Common Definitions
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
Global Definitions and Data Types

Enumerations:
    uopc: ChiselEnum defining micro-operation codes for all supported RV32I instructions:
        R-type instructions 
        I-type instructions
        NOP (no operation, default case)

This enum is used throughout the pipeline:
    Decode stage assigns uop based on instruction fields
    Execute stage maps uop to ALU operations
*/

package core_tile

import chisel3._
import chisel3.experimental.ChiselEnum

// -----------------------------------------
// Global Definitions and Data Types
// -----------------------------------------

//ToDo: Add your implementation according to the specification above here

object uopc extends ChiselEnum {
  //R-Type Instructions
  val ADD, SUB, AND, OR, XOR, SLL, SRL, SRA, SLT, SLTU = Value
  //I-Type Instruction
  val ADDI, ANDI, ORI, XORI, SLLI, SRLI, SRAI, SLTI, SLTIU = Value
  //Branch Instruction (B-Type)
  val BEQ, BNE, BLT, BGE, BLTU, BGEU = Value
  //Jump Instruction (J-Type)
  val JAL, JALR = Value
  //No operation
  val NOP = Value
}