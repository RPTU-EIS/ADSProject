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

import chisel3._                        // Import the basic Chisel types
import chisel3.experimental.ChiselEnum  // Import enum to define instruction names

// -----------------------------------------
// Global Definitions and Data Types
// -----------------------------------------

/* This object defines the internal operation codes used by the processor.
  The decode stage reads the instruction and selects one of these names.
  Then the execute stage uses this name to select the correct ALU operation. */

object uopc extends ChiselEnum {
  val NOP = Value  // Define a no-operation instruction

  // Define the R-type operations
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

  // Define the I-type operations
  val ADDI  = Value
  val XORI  = Value
  val ORI   = Value
  val ANDI  = Value
  val SLLI  = Value
  val SRLI  = Value
  val SRAI  = Value
  val SLTI  = Value
  val SLTIU = Value
}

/* This object defines which immediate format the Immediate Generator must build.
  Each instruction type stores its immediate bits in a different position. */

object ImmSel extends ChiselEnum {
  val R = Value  // R-type does not use an immediate
  val I = Value  // I-type immediate uses instruction bits 31 to 20
  val S = Value  // S-type immediate is used by store instructions
  val B = Value  // B-type immediate is used by branch instructions
  val U = Value  // U-type immediate is used by LUI and AUIPC
  val J = Value  // J-type immediate is used by JAL
}

/* This object defines the writeback source.
  It is included for the control unit because later pipeline versions need it. */

object WBSel extends ChiselEnum {
  val ALU_RESULT = Value  // Write the ALU result into rd
  val MEM_DATA   = Value  // Write the data memory result into rd
  val PC_PLUS4   = Value  // Write PC + 4 into rd for jumps
}
