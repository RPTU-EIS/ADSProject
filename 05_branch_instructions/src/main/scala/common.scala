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

//Start code

//We define all the micro-operation codes used in the pipeline

//We added 8 new operation codes for branch and jump instructions:
//B-type: BEQ, BNE, BLT, BGE, BLTU, BGEU (conditional branches)
//J-type: JAL, JALR (unconditional jumps)
//The EX stage uses these codes to know if it has to evaluate a branch
//condition or compute a jump target address
object uopc extends ChiselEnum {

  //R-type operations
  val ADD  = Value
  val SUB  = Value
  val XOR  = Value
  val OR   = Value
  val AND  = Value
  val SLL  = Value
  val SRL  = Value
  val SRA  = Value
  val SLT  = Value
  val SLTU = Value

  //I-type operations
  val ADDI  = Value
  val XORI  = Value
  val ORI   = Value
  val ANDI  = Value
  val SLLI  = Value
  val SRLI  = Value
  val SRAI  = Value
  val SLTI  = Value
  val SLTIU = Value

  //New B-type operations (conditional branches)
  val BEQ  = Value
  val BNE  = Value
  val BLT  = Value
  val BGE  = Value
  val BLTU = Value
  val BGEU = Value

  //New J-type operations (unconditional jumps)
  val JAL  = Value
  val JALR = Value

  //Default
  val NOP = Value
}