// ADS I Class Project
// Assignment 02: Arithmetic Logic Unit and UVM Testbench
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 09/21/2025 by Tharindu Samarakoon (gug75kex@rptu.de)
// File updated on 10/29/2025 by Tobias Jauch (tobias.jauch@rptu.de)

package Assignment02

import chisel3._                        // Import the basic Chisel tools for hardware
import chisel3.util._                   // Import switch, is, and Mux
import chisel3.experimental.ChiselEnum  // Import enum to name the ALU operations

object ALUOp extends ChiselEnum {
  val ADD, SUB, AND, OR, XOR, SLL, SRL, SRA, SLT, SLTU, PASSB = Value  // Define all ALU operations
}

class ALU extends Module {

  val io = IO(new Bundle {
    val operandA  = Input(UInt(32.W))   // Get the first 32-bit input
    val operandB  = Input(UInt(32.W))   // Get the second 32-bit input
    val operation = Input(ALUOp())      // Get the operation selected for the ALU
    val aluResult = Output(UInt(32.W))  // Send the final 32-bit result
  })

  io.aluResult := 0.U                   // Set result to zero as default value

  val shiftAmount = io.operandB(4, 0)   // Take only the lower 5 bits for RV32I shifts

  switch(io.operation) {                // Check which operation needs to be executed

    is(ALUOp.ADD) {
      io.aluResult := io.operandA + io.operandB   // Add operandA and operandB
    }

    is(ALUOp.SUB) {
      io.aluResult := io.operandA - io.operandB   // Subtract operandB from operandA
    }

    is(ALUOp.AND) {
      io.aluResult := io.operandA & io.operandB   // Do bitwise AND
    }

    is(ALUOp.OR) {
      io.aluResult := io.operandA | io.operandB   // Do bitwise OR
    }

    is(ALUOp.XOR) {
      io.aluResult := io.operandA ^ io.operandB   // Do bitwise XOR
    }

    is(ALUOp.SLL) {
      io.aluResult := (io.operandA << shiftAmount)(31, 0)  // Shift left and keep only 32 bits
    }

    is(ALUOp.SRL) {
      io.aluResult := io.operandA >> shiftAmount   // Shift right and fill with zeros
    }

    is(ALUOp.SRA) {
      io.aluResult := (io.operandA.asSInt >> shiftAmount).asUInt  // Shift right and keep the sign bit
    }

    is(ALUOp.SLT) {
      io.aluResult := Mux(io.operandA.asSInt < io.operandB.asSInt, 1.U, 0.U)  // Compare as signed numbers
    }

    is(ALUOp.SLTU) {
      io.aluResult := Mux(io.operandA < io.operandB, 1.U, 0.U)  // Compare as unsigned numbers
    }

    is(ALUOp.PASSB) {
      io.aluResult := io.operandB   // Pass operandB directly to the result
    }
  }
}