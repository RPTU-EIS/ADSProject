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

// ---- PACKAGE AND IMPORTS ----
/*
WHY: "core_tile" groups all the pipeline files together.
All stages (IF, ID, EX, MEM, WB) use this same package 
so they can see each other and share these definitions.
We import chisel3 because this is hardware design code.
We import ChiselEnum because we need a list of named operations 
instead of raw binary numbers.
*/
package core_tile

import chisel3._
import chisel3.experimental.ChiselEnum

// -----------------------------------------
// Global Definitions and Data Types
// -----------------------------------------

// ---- MICRO-OPERATION CODES ----
/*
WHY: "uopc" means micro-operation code.
The processor needs a clean internal code for each instruction.
The raw 32-bit instruction has the information spread in many fields 
(opcode, funct3, funct7), so it is hard to use directly.
The Control Unit in the ID stage reads the instruction 
and translates it into one of these codes.
Then the EX stage reads this code and tells the ALU what to do.
Without this shared list, each stage would not know 
what operation the other stages are talking about.
Every "Value" below gets a unique number automatically.
We do not assign the numbers manually because ChiselEnum does it for us.
*/
    object uopc extends ChiselEnum {
           
            // ---- R-TYPE INSTRUCTIONS ----
            /*
            WHY: R-type instructions use two registers as input.
            They read rs1 and rs2, do an operation, and write the result to rd.
            The opcode for all R-type is 0110011.
            We need a separate code for each operation 
            because funct3 and funct7 decide the specific operation,
            and the ALU needs to know exactly which one to execute.
            */
            val ADD  = Value  // rd = rs1 + rs2
            val SUB  = Value  // rd = rs1 - rs2
            val XOR  = Value  // rd = rs1 XOR rs2 (bitwise)
            val OR   = Value  // rd = rs1 OR rs2 (bitwise)
            val AND  = Value  // rd = rs1 AND rs2 (bitwise)
            val SLL  = Value  // rd = rs1 shifted left by rs2 positions
            val SRL  = Value  // rd = rs1 shifted right by rs2 positions (fills with 0)
            val SRA  = Value  // rd = rs1 shifted right by rs2 positions (keeps the sign bit)
            val SLT  = Value  // rd = 1 if rs1 < rs2 (signed comparison), else rd = 0
            val SLTU = Value  // rd = 1 if rs1 < rs2 (unsigned comparison), else rd = 0

            // ---- I-TYPE ALU INSTRUCTIONS ----
            /*
            WHY: I-type instructions use one register and one immediate value.
            An immediate is a number that comes directly inside the 32-bit instruction.
            The processor does not read a second register for these.
            Instead, it extracts the immediate from bits [31:20] of the instruction.
            We need separate codes because the EX stage must know 
            that the second operand comes from the immediate, not from rs2.
            The opcode for all I-type ALU operations is 0010011.
            */
            val ADDI  = Value  // rd = rs1 + immediate
            val XORI  = Value  // rd = rs1 XOR immediate
            val ORI   = Value  // rd = rs1 OR immediate
            val ANDI  = Value  // rd = rs1 AND immediate
            val SLLI  = Value  // rd = rs1 shifted left by immediate positions
            val SRLI  = Value  // rd = rs1 shifted right by immediate positions (fills with 0)
            val SRAI  = Value  // rd = rs1 shifted right by immediate positions (keeps the sign bit)
            val SLTI  = Value  // rd = 1 if rs1 < immediate (signed comparison), else rd = 0
            val SLTIU = Value  // rd = 1 if rs1 < immediate (unsigned comparison), else rd = 0

            // ---- NOP (DEFAULT) ----
            /*
            WHY: NOP means "no operation".
            When the Control Unit does not recognize an instruction 
            or the instruction is invalid, it assigns NOP.
            The ALU receives NOP and does nothing for that cycle.
            This keeps the pipeline running safely 
            without producing wrong results or crashing.
            */
            val NOP = Value
            }