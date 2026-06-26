// ADS I Class Project
// Pipelined RISC-V Core - ID Stage
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
Instruction Decode (ID) Stage: decoding, operand fetch, and immediate generation
*/

package core_tile

import chisel3._
import chisel3.util._
import uopc._

// -----------------------------------------
// Instruction Decode Stage (Control Unit) + Branch/Jump support
// -----------------------------------------

//Start code

//We added decoding for B-type and J-type instructions:
//- B-type opcode is 1100011 (BEQ, BNE, BLT, BGE, BLTU, BGEU)
//- JAL opcode is 1101111
//- JALR opcode is 1100111
//We also generate the correct immediate for each format and expose
//the PC and the immediate so the EX stage can compute branch targets
class ID extends Module {

  val io = IO(new Bundle {

    //Input from IF barrier
    val instr       = Input(UInt(32.W))
    val pcIn        = Input(UInt(32.W))    //current PC of this instruction

    //Outputs to ID barrier
    val uop         = Output(uopc())
    val rd          = Output(UInt(5.W))
    val operandA    = Output(UInt(32.W))
    val operandB    = Output(UInt(32.W))
    val xcptInvalid = Output(Bool())

    //Outputs for the Forwarding Unit
    val rs1Out      = Output(UInt(5.W))
    val rs2Out      = Output(UInt(5.W))

    //New outputs for branch/jump support
    val pcOut       = Output(UInt(32.W))   //PC passed forward
    val immOut      = Output(UInt(32.W))   //immediate passed forward

    //Write port from WB stage
    val req_3       = Input(new regFileWriteReq)
  })


  //We create the Register File
  val rf = Module(new regFile())


  //We split the 32-bit instruction
  val opcode = io.instr(6, 0)
  val rd     = io.instr(11, 7)
  val funct3 = io.instr(14, 12)
  val rs1    = io.instr(19, 15)
  val rs2    = io.instr(24, 20)
  val funct7 = io.instr(31, 25)


  //We build the I-type immediate (12 bits, sign-extended)
  val immI = Cat(Fill(20, io.instr(31)), io.instr(31, 20))


  //We build the B-type immediate (13 bits, sign-extended)
  //Format: imm[12|10:5] in bits 31:25, imm[4:1|11] in bits 11:7
  val immB = Cat(
    Fill(19, io.instr(31)),   //sign extension
    io.instr(31),             //imm[12]
    io.instr(7),              //imm[11]
    io.instr(30, 25),         //imm[10:5]
    io.instr(11, 8),          //imm[4:1]
    0.U(1.W)                  //imm[0] is always 0
  )


  //We build the J-type immediate (21 bits, sign-extended) for JAL
  //Format: imm[20|10:1|11|19:12] in bits 31:12
  val immJ = Cat(
    Fill(11, io.instr(31)),   //sign extension
    io.instr(31),             //imm[20]
    io.instr(19, 12),         //imm[19:12]
    io.instr(20),             //imm[11]
    io.instr(30, 21),         //imm[10:1]
    0.U(1.W)                  //imm[0] is always 0
  )


  //We connect the Register File read ports
  rf.io.req_1.addr := rs1
  rf.io.req_2.addr := rs2

  //We connect the Register File write port from WB
  rf.io.req_3 := io.req_3


  //We set default values for the outputs
  io.uop         := NOP
  io.xcptInvalid := false.B
  io.rd          := rd
  io.operandA    := rf.io.resp_1.data
  io.operandB    := rf.io.resp_2.data
  io.rs1Out      := rs1
  io.rs2Out      := rs2
  io.pcOut       := io.pcIn
  io.immOut      := 0.U


  //We decode the instruction based on the opcode

  switch(opcode) {

    //R-type opcode is 0110011
    is("b0110011".U) {
      io.operandB := rf.io.resp_2.data

      switch(funct3) {
        is("b000".U) {
          switch(funct7) {
            is("b0000000".U) { io.uop := ADD }
            is("b0100000".U) { io.uop := SUB }
          }
          when(funct7 =/= "b0000000".U && funct7 =/= "b0100000".U) { io.xcptInvalid := true.B }
        }
        is("b001".U) { io.uop := SLL;  when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B } }
        is("b010".U) { io.uop := SLT;  when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B } }
        is("b011".U) { io.uop := SLTU; when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B } }
        is("b100".U) { io.uop := XOR;  when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B } }
        is("b101".U) {
          switch(funct7) {
            is("b0000000".U) { io.uop := SRL }
            is("b0100000".U) { io.uop := SRA }
          }
          when(funct7 =/= "b0000000".U && funct7 =/= "b0100000".U) { io.xcptInvalid := true.B }
        }
        is("b110".U) { io.uop := OR;   when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B } }
        is("b111".U) { io.uop := AND;  when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B } }
      }
    }


    //I-type opcode is 0010011
    is("b0010011".U) {
      io.operandB := immI
      io.immOut   := immI

      switch(funct3) {
        is("b000".U) { io.uop := ADDI  }
        is("b010".U) { io.uop := SLTI  }
        is("b011".U) { io.uop := SLTIU }
        is("b100".U) { io.uop := XORI  }
        is("b110".U) { io.uop := ORI   }
        is("b111".U) { io.uop := ANDI  }
        is("b001".U) { io.uop := SLLI; when(funct7 =/= "b0000000".U) { io.xcptInvalid := true.B } }
        is("b101".U) {
          switch(funct7) {
            is("b0000000".U) { io.uop := SRLI }
            is("b0100000".U) { io.uop := SRAI }
          }
          when(funct7 =/= "b0000000".U && funct7 =/= "b0100000".U) { io.xcptInvalid := true.B }
        }
      }
    }


    //New B-type opcode is 1100011 (conditional branches)
    is("b1100011".U) {
      io.operandA := rf.io.resp_1.data
      io.operandB := rf.io.resp_2.data
      io.immOut   := immB

      switch(funct3) {
        is("b000".U) { io.uop := BEQ  }
        is("b001".U) { io.uop := BNE  }
        is("b100".U) { io.uop := BLT  }
        is("b101".U) { io.uop := BGE  }
        is("b110".U) { io.uop := BLTU }
        is("b111".U) { io.uop := BGEU }
      }
    }


    //New JAL opcode is 1101111 (unconditional jump)
    is("b1101111".U) {
      io.uop      := JAL
      io.immOut   := immJ
    }


    //New JALR opcode is 1100111 (jump and link register)
    is("b1100111".U) {
      io.uop      := JALR
      io.operandA := rf.io.resp_1.data
      io.immOut   := immI
    }
  }


  //We handle unknown opcodes
  when(opcode =/= "b0110011".U &&
       opcode =/= "b0010011".U &&
       opcode =/= "b1100011".U &&
       opcode =/= "b1101111".U &&
       opcode =/= "b1100111".U) {
    io.uop         := NOP
    io.xcptInvalid := true.B
  }

}