
// ADS I Class Project
// Assignment 02: Arithmetic Logic Unit and UVM Testbench
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 09/21/2025 by Tharindu Samarakoon (gug75kex@rptu.de)
// File updated on 10/29/2025 by Tobias Jauch (tobias.jauch@rptu.de)

package Assignment02

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

// =============================================================================
// ALU Operations Enum
// =============================================================================
// ChiselEnum creates a type-safe enumeration that synthesizes to hardware.
// Values are automatically assigned: ADD=0, SUB=1, AND=2, etc.
// IMPORTANT: This order must match the SystemVerilog enum in alu_tb_config_pkg.sv!

object ALUOp extends ChiselEnum {
  val ADD   = Value(0.U(8.W))   // 8'h0
  val SUB   = Value(1.U(8.W))   // 8'h1
  val AND   = Value(2.U(8.W))   // 8'h2
  val OR    = Value(3.U(8.W))   // 8'h3
  val XOR   = Value(4.U(8.W))   // 8'h4
  val SLL   = Value(5.U(8.W))   // 8'h5
  val SRL   = Value(6.U(8.W))   // 8'h6
  val SRA   = Value(7.U(8.W))   // 8'h7
  val SLT   = Value(8.U(8.W))   // 8'h8
  val SLTU  = Value(9.U(8.W))   // 8'h9
  val PASSB = Value(10.U(8.W))  // 8'hA
}

// =============================================================================
// ALU Module
// =============================================================================
// A purely combinational 32-bit ALU for the RV32I RISC-V ISA.
// No internal state - output depends only on current inputs.

class ALU extends Module {

  // ===========================================================================
  // I/O Interface
  // ===========================================================================
  val io = IO(new Bundle {
    val operandA  = Input(UInt(32.W))   // First operand (32-bit unsigned)
    val operandB  = Input(UInt(32.W))   // Second operand (32-bit unsigned)
    val operation = Input(ALUOp())      // Operation selector (enum type)
    val aluResult = Output(UInt(32.W))  // Result output (32-bit unsigned)
  })

  // ===========================================================================
  // Shift Amount Extraction
  // ===========================================================================
  // For RV32I, shift operations use only the lower 5 bits of operandB.
  // This is because 2^5 = 32, covering all valid shift amounts (0-31).
  // Example: If operandB = 33 (0b100001), shamt = 1 (only bits [4:0])

  val shamt = io.operandB(4, 0)

  // ===========================================================================
  // Default Output Value
  // ===========================================================================
  // CRITICAL: Always set a default value before the switch statement.
  // This handles undefined operation codes and prevents latch inference.

  io.aluResult := 0.U

  // ===========================================================================
  // ALU Operations
  // ===========================================================================
  switch(io.operation) {

    // -------------------------------------------------------------------------
    // ADD: Addition with two's complement wraparound
    // -------------------------------------------------------------------------
    // Adds operandA and operandB. Overflow wraps around (no exception).
    // Example: 0xFFFFFFFF + 1 = 0x00000000

    is(ALUOp.ADD) {
      io.aluResult := io.operandA + io.operandB
    }

    // -------------------------------------------------------------------------
    // SUB: Subtraction with two's complement wraparound
    // -------------------------------------------------------------------------
    // Subtracts operandB from operandA. Underflow wraps around.
    // Example: 0x00000000 - 1 = 0xFFFFFFFF

    is(ALUOp.SUB) {
      io.aluResult := io.operandA - io.operandB
    }

    // -------------------------------------------------------------------------
    // AND: Bitwise AND
    // -------------------------------------------------------------------------
    // Result bit is 1 only if both corresponding input bits are 1.
    // Example: 0x0F0F0F0F & 0x00FF00FF = 0x000F000F

    is(ALUOp.AND) {
      io.aluResult := io.operandA & io.operandB
    }

    // -------------------------------------------------------------------------
    // OR: Bitwise OR
    // -------------------------------------------------------------------------
    // Result bit is 1 if either corresponding input bit is 1.
    // Example: 0x0F0F0F0F | 0x00FF00FF = 0x0FFF0FFF

    is(ALUOp.OR) {
      io.aluResult := io.operandA | io.operandB
    }

    // -------------------------------------------------------------------------
    // XOR: Bitwise XOR
    // -------------------------------------------------------------------------
    // Result bit is 1 if corresponding input bits differ.
    // Example: 0x0F0F0F0F ^ 0x00FF00FF = 0x0FF00FF0

    is(ALUOp.XOR) {
      io.aluResult := io.operandA ^ io.operandB
    }

    // -------------------------------------------------------------------------
    // SLL: Shift Left Logical
    // -------------------------------------------------------------------------
    // Shifts operandA left by shamt positions. Zeros fill from the right.
    // Only lower 5 bits of operandB are used (shamt).
    // Example: 0x00000001 << 4 = 0x00000010

    is(ALUOp.SLL) {
      io.aluResult := io.operandA << shamt
    }

    // -------------------------------------------------------------------------
    // SRL: Shift Right Logical
    // -------------------------------------------------------------------------
    // Shifts operandA right by shamt positions. Zeros fill from the left.
    // Only lower 5 bits of operandB are used (shamt).
    // Example: 0x80000000 >> 4 = 0x08000000 (zeros fill)

    is(ALUOp.SRL) {
      io.aluResult := io.operandA >> shamt
    }

    // -------------------------------------------------------------------------
    // SRA: Shift Right Arithmetic
    // -------------------------------------------------------------------------
    // Shifts operandA right by shamt positions. Sign bit fills from the left.
    // This preserves the sign of negative numbers.
    // Implementation: Convert to SInt, shift, convert back to UInt.
    // Example: 0x80000000 >> 4 = 0xF8000000 (sign bit replicated)

    is(ALUOp.SRA) {
      io.aluResult := (io.operandA.asSInt >> shamt).asUInt
    }

    // -------------------------------------------------------------------------
    // SLT: Set Less Than (Signed Comparison)
    // -------------------------------------------------------------------------
    // Compares operandA and operandB as SIGNED two's complement numbers.
    // Returns 1 if operandA < operandB (signed), otherwise 0.
    // Example: 0xFFFFFFFF (-1) < 0x00000001 (1) → result = 1

    is(ALUOp.SLT) {
      io.aluResult := (io.operandA.asSInt < io.operandB.asSInt).asUInt
    }

    // -------------------------------------------------------------------------
    // SLTU: Set Less Than Unsigned
    // -------------------------------------------------------------------------
    // Compares operandA and operandB as UNSIGNED numbers.
    // Returns 1 if operandA < operandB (unsigned), otherwise 0.
    // Example: 0x00000001 < 0xFFFFFFFF → result = 1 (1 < 4294967295)

    is(ALUOp.SLTU) {
      io.aluResult := (io.operandA < io.operandB).asUInt
    }

    // -------------------------------------------------------------------------
    // PASSB: Pass operandB to output
    // -------------------------------------------------------------------------
    // Simply forwards operandB unchanged to the output.
    // Used for LUI instruction where an immediate needs to pass through.
    // Example: operandB = 0x12345000 → result = 0x12345000

    is(ALUOp.PASSB) {
      io.aluResult := io.operandB
    }
  }
}
