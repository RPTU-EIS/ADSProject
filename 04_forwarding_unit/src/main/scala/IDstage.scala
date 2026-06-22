package core_tile

import chisel3._
import chisel3.util._
import uopc._

// -----------------------------------------
// Instruction Decode Stage (Control Unit)
// -----------------------------------------

//Start code

//We added 2 new outputs to expose rs1 and rs2 to the Forwarding Unit:
//- rs1Out: source register 1, used by the Forwarding Unit to detect hazards
//- rs2Out: source register 2, used by the Forwarding Unit to detect hazards
//These travel through the ID barrier to reach the Forwarding Unit at the EX stage

class ID extends Module {

  //We define the inputs and outputs of the ID stage

  val io = IO(new Bundle {

    //Input from IF barrier
    val instr       = Input(UInt(32.W))      // raw 32-bit instruction

    //Outputs to ID barrier
    val uop         = Output(uopc())         // operation code for the ALU
    val rd          = Output(UInt(5.W))      // destination register
    val operandA    = Output(UInt(32.W))     // first operand for the ALU
    val operandB    = Output(UInt(32.W))     // second operand for the ALU
    val xcptInvalid = Output(Bool())         // invalid instruction flag

    //New outputs to expose rs1 and rs2 to the Forwarding Unit
    val rs1Out      = Output(UInt(5.W))      // source register 1
    val rs2Out      = Output(UInt(5.W))      // source register 2

    //Write port from WB stage to the Register File
    val req_3       = Input(new regFileWriteReq)
  })


  //We create the Register File, the 32 RISC-V registers live here

  val rf = Module(new regFile())


  //We split the 32-bit instruction into its fields

  val opcode = io.instr(6, 0)                // bits 0-6: instruction type
  val rd     = io.instr(11, 7)               // bits 7-11: destination register
  val funct3 = io.instr(14, 12)              // bits 12-14: operation variant
  val rs1    = io.instr(19, 15)              // bits 15-19: source register 1
  val rs2    = io.instr(24, 20)              // bits 20-24: source register 2
  val funct7 = io.instr(31, 25)              // bits 25-31: R-type extra info


  //We build the immediate value, sign-extending the 12 bits to 32 bits

  val imm = Cat(Fill(20, io.instr(31)), io.instr(31, 20))


  //We connect the Register File read ports to rs1 and rs2

  rf.io.req_1.addr := rs1                    // read rs1
  rf.io.req_2.addr := rs2                    // read rs2


  //We connect the Register File write port from WB stage

  rf.io.req_3 := io.req_3                    // writeback from WB


  //We set default values for the outputs, safety net for unknown instructions

  io.uop         := NOP                      // default: do nothing
  io.xcptInvalid := false.B                  // default: instruction is valid
  io.rd          := rd                       // forward destination register
  io.operandA    := rf.io.resp_1.data        // operandA comes from rs1
  io.operandB    := rf.io.resp_2.data        // operandB defaults to rs2

  //We expose rs1 and rs2 so the Forwarding Unit can detect hazards
  io.rs1Out      := rs1                      // expose rs1
  io.rs2Out      := rs2                      // expose rs2


  //We decode the instruction based on the opcode

  switch(opcode) {

    //R-type opcode is 0110011, uses two registers

    is("b0110011".U) {
      io.operandB := rf.io.resp_2.data       // R-type uses rs2 as second operand

      switch(funct3) {
        is("b000".U) {                       // ADD or SUB
          switch(funct7) {
            is("b0000000".U) { io.uop := ADD }
            is("b0100000".U) { io.uop := SUB }
          }
          when(funct7 =/= "b0000000".U && funct7 =/= "b0100000".U) {
            io.xcptInvalid := true.B
          }
        }
        is("b001".U) {
          io.uop := SLL
          when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B }
        }
        is("b010".U) {
          io.uop := SLT
          when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B }
        }
        is("b011".U) {
          io.uop := SLTU
          when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B }
        }
        is("b100".U) {
          io.uop := XOR
          when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B }
        }
        is("b101".U) {                       // SRL or SRA
          switch(funct7) {
            is("b0000000".U) { io.uop := SRL }
            is("b0100000".U) { io.uop := SRA }
          }
          when(funct7 =/= "b0000000".U && funct7 =/= "b0100000".U) {
            io.xcptInvalid := true.B
          }
        }
        is("b110".U) {
          io.uop := OR
          when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B }
        }
        is("b111".U) {
          io.uop := AND
          when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B }
        }
      }
    }


    //I-type opcode is 0010011, uses one register and one immediate

    is("b0010011".U) {
      io.operandB := imm                     // I-type uses immediate as second operand

      switch(funct3) {
        is("b000".U) { io.uop := ADDI  }
        is("b010".U) { io.uop := SLTI  }
        is("b011".U) { io.uop := SLTIU }
        is("b100".U) { io.uop := XORI  }
        is("b110".U) { io.uop := ORI   }
        is("b111".U) { io.uop := ANDI  }
        is("b001".U) {
          io.uop := SLLI
          when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B }
        }
        is("b101".U) {
          switch(funct7) {
            is("b0000000".U) { io.uop := SRLI }
            is("b0100000".U) { io.uop := SRAI }
          }
          when(funct7 =/= "b0000000".U && funct7 =/= "b0100000".U) {
            io.xcptInvalid := true.B
          }
        }
      }
    }
  }


  //We handle unknown opcodes, raise the invalid flag

  when(opcode =/= "b0110011".U && opcode =/= "b0010011".U) {
    io.uop         := NOP
    io.xcptInvalid := true.B
  }

}