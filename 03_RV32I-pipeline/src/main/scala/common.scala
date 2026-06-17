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
  //WE ARE MISSING THE CASE OF UNVALID OPCODE
  val ADD = Value(0.U)
  val SLT = Value(1.U)
  val SLTU = Value(2.U)
  val AND = Value(3.U)
  val OR = Value(4.U)
  val XOR = Value(5.U)
  val SLL = Value(6.U)
  val SRRL = Value(7.U)
  val SUB = Value(8.U)
  val SRA = Value(9.U)
  val ADDI = Value(10.U)
  val SLTI = Value(11.U)
  val SLTIU = Value(12.U)
  val ANDI = Value(13.U)
  val ORI = Value(14.U)
  val XORI = Value(15.U)
  val SLLI = Value(16.U)
  val SRLI = Value(17.U)
  val SRAI = Value(18.U)
  val NOP  = Value(19.U)
}

//ToDo: Add your implementation according to the specification above here 