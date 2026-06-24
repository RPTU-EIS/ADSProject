// ADS I Class Project
// Assignment 03: Pipelined RISC-V Core
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 11/01/2025

/*
This file is for the Control Unit.
The Control Unit checks the instruction and decides what the processor has to do.
For this task we only work with R-type and I-type instructions.
*/

package core_tile

import chisel3._       // Import the basic Chisel hardware tools
import chisel3.util._  // Import switch, is, and Mux

//We define the ALU codes that the Control Unit can send to the ALU

object ControlALUCode {
  val ADD  = "b0000".U(4.W)  // Code for ADD and ADDI
  val SUB  = "b0001".U(4.W)  // Code for SUB
  val AND  = "b0010".U(4.W)  // Code for AND and ANDI
  val OR   = "b0011".U(4.W)  // Code for OR and ORI
  val XOR  = "b0100".U(4.W)  // Code for XOR and XORI
  val SLL  = "b0101".U(4.W)  // Code for SLL and SLLI
  val SRL  = "b0110".U(4.W)  // Code for SRL and SRLI
  val SRA  = "b0111".U(4.W)  // Code for SRA and SRAI
  val SLT  = "b1000".U(4.W)  // Code for SLT and SLTI
  val SLTU = "b1001".U(4.W)  // Code for SLTU and SLTIU
}

//Start Control Unit code

class ControlUnit extends Module {
  val io = IO(new Bundle {
    val instruction = Input(UInt(32.W))  // Get the complete 32-bit instruction

    val regWrite = Output(Bool())        // Tell if the instruction writes into rd
    val aluSrc   = Output(Bool())        // Tell if operand B comes from immediate
    val aluCode  = Output(UInt(4.W))     // Send the operation code to the ALU
    val immType  = Output(UInt(3.W))     // Tell which immediate format is used
    val invalid  = Output(Bool())        // Tell if the instruction is not supported
  })

  //We separate the important parts of the instruction

  val op_code = io.instruction(6, 0)    // This part tells the instruction format
  val rd      = io.instruction(11, 7)   // This part is the destination register
  val funct_3 = io.instruction(14, 12)  // This part tells the operation group
  val rs1     = io.instruction(19, 15)  // This part is the first source register
  val rs2     = io.instruction(24, 20)  // This part is the second source register
  val funct_7 = io.instruction(31, 25)  // This part helps with SUB, SRA, and SRAI

  //We define which funct7 values are correct for R-type

  val normal_r_type = funct_7 === "b0000000".U  // Normal R-type operations use funct7 equal to zero
  val special_r_type = funct_7 === "b0100000".U && (funct_3 === "b000".U || funct_3 === "b101".U)  // Only SUB and SRA use this funct7

  //We set default values before decoding

  io.regWrite := false.B                 // Default does not write a register
  io.aluSrc   := false.B                 // Default uses rs2 as operand B
  io.aluCode  := ControlALUCode.ADD      // Default ALU operation is ADD
  io.immType  := 0.U                     // Default has no immediate
  io.invalid  := false.B                 // Default instruction is valid

  //We check which instruction type we have

  switch(op_code) {

    is("b0110011".U) {
      //R-type instruction uses two registers

      io.regWrite := true.B              // R-type writes the result into rd
      io.aluSrc   := false.B             // R-type uses rs2, not immediate
      io.immType  := 0.U                 // R-type does not need immediate

      switch(funct_3) {

        is("b000".U) {
          io.aluCode := Mux(funct_7 === "b0100000".U, ControlALUCode.SUB, ControlALUCode.ADD)  // ADD or SUB
        }

        is("b001".U) {
          io.aluCode := ControlALUCode.SLL  // SLL
        }

        is("b010".U) {
          io.aluCode := ControlALUCode.SLT  // SLT
        }

        is("b011".U) {
          io.aluCode := ControlALUCode.SLTU // SLTU
        }

        is("b100".U) {
          io.aluCode := ControlALUCode.XOR  // XOR
        }

        is("b101".U) {
          io.aluCode := Mux(funct_7 === "b0100000".U, ControlALUCode.SRA, ControlALUCode.SRL)  // SRL or SRA
        }

        is("b110".U) {
          io.aluCode := ControlALUCode.OR   // OR
        }

        is("b111".U) {
          io.aluCode := ControlALUCode.AND  // AND
        }
      }

      when(!normal_r_type && !special_r_type) {
        io.invalid := true.B  // This R-type funct7 is not supported
      }
    }

    is("b0010011".U) {
      //I-type instruction uses one register and one immediate

      io.regWrite := true.B              // I-type writes the result into rd
      io.aluSrc   := true.B              // I-type uses immediate as operand B
      io.immType  := 1.U                 // I-type immediate format

      switch(funct_3) {

        is("b000".U) {
          io.aluCode := ControlALUCode.ADD  // ADDI
        }

        is("b001".U) {
          io.aluCode := ControlALUCode.SLL  // SLLI
          when(funct_7 =/= "b0000000".U) {
            io.invalid := true.B            // SLLI only accepts funct7 equal to zero
          }
        }

        is("b010".U) {
          io.aluCode := ControlALUCode.SLT  // SLTI
        }

        is("b011".U) {
          io.aluCode := ControlALUCode.SLTU // SLTIU
        }

        is("b100".U) {
          io.aluCode := ControlALUCode.XOR  // XORI
        }

        is("b101".U) {
          io.aluCode := Mux(funct_7 === "b0100000".U, ControlALUCode.SRA, ControlALUCode.SRL)  // SRLI or SRAI
          when(funct_7 =/= "b0000000".U && funct_7 =/= "b0100000".U) {
            io.invalid := true.B            // SRLI and SRAI only accept these funct7 values
          }
        }

        is("b110".U) {
          io.aluCode := ControlALUCode.OR   // ORI
        }

        is("b111".U) {
          io.aluCode := ControlALUCode.AND  // ANDI
        }
      }
    }
  }

  when(op_code =/= "b0110011".U && op_code =/= "b0010011".U) {
    io.invalid := true.B  // Only R-type and I-type instructions are used in this task
  }
}
