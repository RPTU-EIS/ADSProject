// ADS I Class Project
// Pipelined RISC-V Core - ID Stage
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
Instruction Decode (ID) Stage: decoding and operand fetch

Extracted Fields from 32-bit Instruction (see RISC-V specification for reference):
    opcode: instruction format identifier
    funct3: selects variant within instruction format
    funct7: further specifies operation type (R-type only)
    rd: destination register address
    rs1: first source register address
    rs2: second source register address
    imm: 12-bit immediate value (I-type, sign-extended)

Register File Interfaces:
    regFileReq_A, regFileResp_A: read port for rs1 operand
    regFileReq_B, regFileResp_B: read port for rs2 operand

Internal Signals:
    Combinational decoders for instructions

Functionality:
    Decode opcode to determine instruction and identify operation (ADD, SUB, XOR, ...)
    Output: uop (operation code), rd, operandA (from rs1), operandB (rs2 or immediate)

Outputs:
    uop: micro-operation code (identifies instruction type)
    rd: destination register index
    operandA: first operand
    operandB: second operand 
    XcptInvalid: exception flag for invalid instructions
*/

package core_tile

import chisel3._       // Import the basic Chisel types
import chisel3.util._  // Import switch, is, Cat, Fill, and helper tools
import uopc._          // Import the internal instruction operation names

// -----------------------------------------
// Decode Stage
// -----------------------------------------

class ID extends Module {
  val io = IO(new Bundle {
    val instr = Input(UInt(32.W))  // Get the instruction from the IF barrier

    val regFileReq_A  = Output(new regFileReadReq)   // Send rs1 address to register file
    val regFileResp_A = Input(new regFileReadResp)   // Get rs1 data from register file
    val regFileReq_B  = Output(new regFileReadReq)   // Send rs2 address to register file
    val regFileResp_B = Input(new regFileReadResp)   // Get rs2 data from register file

    val uop         = Output(uopc())      // Send the decoded operation to execute stage
    val rd          = Output(UInt(5.W))   // Send the destination register
    val operandA    = Output(UInt(32.W))  // Send the first ALU operand
    val operandB    = Output(UInt(32.W))  // Send the second ALU operand
    val XcptInvalid = Output(Bool())      // Send true when the instruction is not supported
  })

  // Extract the normal RISC-V instruction fields
  val opcode = io.instr(6, 0)    // Bits that tell the instruction format
  val rd     = io.instr(11, 7)   // Destination register address
  val funct3 = io.instr(14, 12)  // Small operation selector
  val rs1    = io.instr(19, 15)  // First source register address
  val rs2    = io.instr(24, 20)  // Second source register address
  val funct7 = io.instr(31, 25)  // Extra operation selector for R-type and shifts

  val immI = Cat(Fill(20, io.instr(31)), io.instr(31, 20))  // Create the sign-extended I-type immediate
  val shamt = io.instr(24, 20)                              // Get the shift amount for shift immediate instructions

  val controlUnit = Module(new ControlUnit())                // Create the Control Unit
  val immediateGenerator = Module(new ImmediateGenerator())   // Create the Immediate Generator

  controlUnit.io.instruction := io.instr                     // Send instruction to the Control Unit
  immediateGenerator.io.instruction := io.instr              // Send instruction to the Immediate Generator
  immediateGenerator.io.immSel := controlUnit.io.immSel      // Select immediate format from the Control Unit

  io.regFileReq_A.addr := rs1  // Ask the register file for rs1
  io.regFileReq_B.addr := rs2  // Ask the register file for rs2

  // Set safe default values before checking the instruction
  io.uop         := controlUnit.io.uop      // Use the operation decoded by the Control Unit
  io.rd          := rd                      // Default destination is the rd field
  io.operandA    := io.regFileResp_A.data   // Default first operand comes from rs1
  io.operandB    := io.regFileResp_B.data   // Default second operand comes from rs2
  io.XcptInvalid := controlUnit.io.invalid  // Use invalid flag from the Control Unit

  switch(opcode) {  // Check the opcode to know the instruction type

    is("b0110011".U) {
      // R-type instructions use rs1 and rs2 as operands
      switch(Cat(funct7, funct3)) {  // Join funct7 and funct3 to identify the exact R-type operation
        is("b0000000000".U) { io.uop := uopc.ADD  }
        is("b0100000000".U) { io.uop := uopc.SUB  }
        is("b0000000100".U) { io.uop := uopc.XOR  }
        is("b0000000110".U) { io.uop := uopc.OR   }
        is("b0000000111".U) { io.uop := uopc.AND  }
        is("b0000000001".U) { io.uop := uopc.SLL  }
        is("b0000000101".U) { io.uop := uopc.SRL  }
        is("b0100000101".U) { io.uop := uopc.SRA  }
        is("b0000000010".U) { io.uop := uopc.SLT  }
        is("b0000000011".U) { io.uop := uopc.SLTU }
      }

      when(io.uop === uopc.NOP && io.instr =/= "h00000013".U) {
        io.XcptInvalid := true.B  // Mark invalid if the R-type combination is not supported
      }
    }

    is("b0010011".U) {
      // I-type instructions use rs1 and an immediate value as operands
      io.operandB := immediateGenerator.io.immediate  // Replace the second operand with the generated immediate

      switch(funct3) {  // Check funct3 to identify the I-type operation
        is("b000".U) { io.uop := uopc.ADDI  }
        is("b100".U) { io.uop := uopc.XORI  }
        is("b110".U) { io.uop := uopc.ORI   }
        is("b111".U) { io.uop := uopc.ANDI  }
        is("b010".U) { io.uop := uopc.SLTI  }
        is("b011".U) { io.uop := uopc.SLTIU }
        is("b001".U) {
          io.operandB := shamt  // Shift instructions use only the shift amount
        }
        is("b101".U) {
          io.operandB := shamt  // Shift instructions use only the shift amount
        }
      }
    }

    is("b0000000".U) {
      io.rd := 0.U        // Clear destination for an empty instruction
      io.operandA := 0.U  // Clear first operand
      io.operandB := 0.U  // Clear second operand
    }
  }

}
