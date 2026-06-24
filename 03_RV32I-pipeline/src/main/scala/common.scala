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
           
            // R-type
            val ADD  = Value
            val SUB  = Value
            val XOR  = Value
            val OR   = Value
            val AND  = Value
            val SLL  = Value  // shift left logical
            val SRL  = Value  // shift right logical
            val SRA  = Value  // shift right arithmetic
            val SLT  = Value  // set less than (signed)
            val SLTU = Value  // set less than (unsigned)

            // I-type ALU
            val ADDI  = Value
            val XORI  = Value
            val ORI   = Value
            val ANDI  = Value
            val SLLI  = Value
            val SRLI  = Value
            val SRAI  = Value
            val SLTI  = Value   // set less than immediate (signed)
            val SLTIU = Value   // set less than immediate (unsigned)

            // NOP / invalid
            val NOP = Value
            }