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
object uopc extends ChiselEnum {
  // No operation (default case) (kept first to maybe achieve 000 index)
  val NOP   = Value
  
  // R-type instructions
  val ADD   = Value
  val SUB   = Value
  val AND   = Value
  val OR    = Value
  val XOR   = Value
  val SLL   = Value
  val SRL   = Value
  val SRA   = Value
  val SLT   = Value
  val SLTU  = Value
  
  // I-type instructions
  val ADDI  = Value
  val ANDI  = Value
  val ORI   = Value
  val XORI  = Value
  val SLLI  = Value
  val SRLI  = Value
  val SRAI  = Value
  val SLTI  = Value
  val SLTIU = Value

  // B-type instruction
  val BEQ   = Value
  val BNE   = Value
  val BLT   = Value
  val BGE   = Value
  val BLTU  = Value
  val BGEU  = Value

  // J-type instruction
  val JAL   = Value
  val JALR  = Value
  
}
//ToDo: Add your implementation according to the specification above here 
