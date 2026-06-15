// ADS I Class Project
// Assignment 03: Pipelined RISC-V Core
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 11/01/2025

/*
  This file creates the Control Unit.
  The Control Unit reads the instruction bits and decides what the processor
  has to do with that instruction.

  In this assignment the tested pipeline executes R-type and I-type ALU
  instructions. The extra control signals are kept here because they make the
  module easier to extend for load, store, branch, jump, LUI, and AUIPC later.
*/

package core_tile

import chisel3._                 // Import the basic Chisel hardware types
import chisel3.util._            // Import switch, is, and Mux
import Assignment02.{ALUOp}      // Import ALU operation names from Assignment 02
import uopc._                    // Import internal operation names

// -----------------------------------------
// Control Unit
// -----------------------------------------

class ControlUnit extends Module {
  val io = IO(new Bundle {
    val instruction = Input(UInt(32.W))  // Get the full 32-bit instruction

    val uop         = Output(uopc())     // Send the internal operation name
    val immSel      = Output(ImmSel())   // Tell the Immediate Generator what format to use
    val useImm      = Output(Bool())     // Select immediate instead of rs2 when true
    val regWrite    = Output(Bool())     // Enable register writeback
    val memRead     = Output(Bool())     // Enable memory read for loads
    val memWrite    = Output(Bool())     // Enable memory write for stores
    val branch      = Output(Bool())     // Mark branch instructions
    val jump        = Output(Bool())     // Mark JAL instructions
    val jumpR       = Output(Bool())     // Mark JALR instructions
    val aluSrcA     = Output(Bool())     // Select PC as ALU operand A when true
    val aluOp       = Output(ALUOp())    // Send the ALU operation
    val wbSel       = Output(WBSel())    // Select writeback source
    val invalid     = Output(Bool())     // Mark unsupported instructions
  })

  val opcode = io.instruction(6, 0)    // Main instruction selector
  val funct3 = io.instruction(14, 12)  // Small operation selector
  val funct7 = io.instruction(31, 25)  // Extra operation selector

  // Set safe default values before decoding the real instruction
  io.uop      := uopc.NOP           // Default operation is NOP
  io.immSel   := ImmSel.R           // Default immediate format is R-type
  io.useImm   := false.B            // Default second operand comes from rs2
  io.regWrite := false.B            // Default does not write the register file
  io.memRead  := false.B            // Default does not read memory
  io.memWrite := false.B            // Default does not write memory
  io.branch   := false.B            // Default is not a branch
  io.jump     := false.B            // Default is not JAL
  io.jumpR    := false.B            // Default is not JALR
  io.aluSrcA  := false.B            // Default ALU operand A comes from rs1
  io.aluOp    := ALUOp.ADD          // Default ALU operation is ADD
  io.wbSel    := WBSel.ALU_RESULT   // Default writeback comes from the ALU
  io.invalid  := false.B            // Default instruction is valid

  switch(opcode) {  // Decode the instruction using the opcode

    is("b0110011".U) {
      io.regWrite := true.B   // R-type writes the ALU result into rd
      io.immSel   := ImmSel.R // R-type does not use an immediate

      switch(Cat(funct7, funct3)) {  // Decode exact R-type operation
        is("b0000000000".U) { io.uop := uopc.ADD;  io.aluOp := ALUOp.ADD  }
        is("b0100000000".U) { io.uop := uopc.SUB;  io.aluOp := ALUOp.SUB  }
        is("b0000000100".U) { io.uop := uopc.XOR;  io.aluOp := ALUOp.XOR  }
        is("b0000000110".U) { io.uop := uopc.OR;   io.aluOp := ALUOp.OR   }
        is("b0000000111".U) { io.uop := uopc.AND;  io.aluOp := ALUOp.AND  }
        is("b0000000001".U) { io.uop := uopc.SLL;  io.aluOp := ALUOp.SLL  }
        is("b0000000101".U) { io.uop := uopc.SRL;  io.aluOp := ALUOp.SRL  }
        is("b0100000101".U) { io.uop := uopc.SRA;  io.aluOp := ALUOp.SRA  }
        is("b0000000010".U) { io.uop := uopc.SLT;  io.aluOp := ALUOp.SLT  }
        is("b0000000011".U) { io.uop := uopc.SLTU; io.aluOp := ALUOp.SLTU }
      }

      when(io.uop === uopc.NOP) {
        io.invalid := true.B  // Mark invalid if this R-type combination is not supported
      }
    }

    is("b0010011".U) {
      io.regWrite := true.B  // I-type ALU writes the ALU result into rd
      io.immSel   := ImmSel.I
      io.useImm   := true.B  // I-type uses the immediate as operand B

      switch(funct3) {  // Decode exact I-type operation
        is("b000".U) { io.uop := uopc.ADDI;  io.aluOp := ALUOp.ADD  }
        is("b100".U) { io.uop := uopc.XORI;  io.aluOp := ALUOp.XOR  }
        is("b110".U) { io.uop := uopc.ORI;   io.aluOp := ALUOp.OR   }
        is("b111".U) { io.uop := uopc.ANDI;  io.aluOp := ALUOp.AND  }
        is("b010".U) { io.uop := uopc.SLTI;  io.aluOp := ALUOp.SLT  }
        is("b011".U) { io.uop := uopc.SLTIU; io.aluOp := ALUOp.SLTU }

        is("b001".U) {
          when(funct7 === "b0000000".U) {
            io.uop := uopc.SLLI
            io.aluOp := ALUOp.SLL
          } .otherwise {
            io.invalid := true.B  // Only funct7 0000000 is valid for SLLI
          }
        }

        is("b101".U) {
          when(funct7 === "b0000000".U) {
            io.uop := uopc.SRLI
            io.aluOp := ALUOp.SRL
          } .elsewhen(funct7 === "b0100000".U) {
            io.uop := uopc.SRAI
            io.aluOp := ALUOp.SRA
          } .otherwise {
            io.invalid := true.B  // Only funct7 0000000 and 0100000 are valid here
          }
        }
      }

      when(io.uop === uopc.NOP && io.instruction =/= "h00000013".U) {
        io.invalid := true.B  // Mark invalid if this I-type operation is not supported
      }
    }

    is("b0000000".U) {
      io.uop := uopc.NOP  // Empty instruction is treated like a bubble
    }
  }

  when(opcode =/= "b0110011".U && opcode =/= "b0010011".U && opcode =/= "b0000000".U) {
    io.invalid := true.B  // This assignment does not execute this opcode in the current pipeline
  }
}
