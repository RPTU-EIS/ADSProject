// ADS I Class Project
// Pipelined RISC-V Core - EX Stage
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
Instruction Execute (EX) Stage: ALU operations, exception detection,
branch condition evaluation, and jump target calculation
*/

package core_tile

import chisel3._
import chisel3.util._
import Assignment02.{ALU, ALUOp}
import uopc._

// -----------------------------------------
// Execute Stage with forwarding muxes + branch/jump support
// -----------------------------------------

//Start code

//We added support for branches and jumps:
//- branchTaken: tells the IF stage if the branch is taken
//- branchTarget: the new PC value if the branch is taken
//For JAL and JALR, branchTaken is always true
//For BEQ, BNE, BLT, BGE, BLTU, BGEU, we evaluate the condition first
//The PC and the immediate come from the ID barrier
class EX extends Module {

  val io = IO(new Bundle {

    //Inputs from ID barrier
    val uop           = Input(uopc())
    val inRD          = Input(UInt(5.W))
    val operandA      = Input(UInt(32.W))
    val operandB      = Input(UInt(32.W))
    val inXcptInvalid = Input(Bool())

    //Inputs from the Forwarding Unit
    val forwardA      = Input(UInt(2.W))
    val forwardB      = Input(UInt(2.W))

    //Inputs from MEM and WB barriers (forwarded values)
    val memData       = Input(UInt(32.W))
    val wbData        = Input(UInt(32.W))

    //Inputs to expose rs1 and rs2 to the Forwarding Unit
    val rs1           = Input(UInt(5.W))
    val rs2           = Input(UInt(5.W))

    //New inputs for branch and jump support
    val pcIn          = Input(UInt(32.W))    //current PC of this instruction
    val imm           = Input(UInt(32.W))    //immediate (B-type or J-type)

    //Outputs to EX barrier
    val aluResult      = Output(UInt(32.W))
    val outRD          = Output(UInt(5.W))
    val outXcptInvalid = Output(Bool())
    val outRegWrite    = Output(Bool())

    //New outputs for branch/jump handling
    val branchTaken    = Output(Bool())      //true if branch/jump is taken
    val branchTarget   = Output(UInt(32.W))  //new PC value if taken
  })


  //We create the ALU
  val alu = Module(new ALU())


  //We map the uop to the ALU operation code
  val aluOp = WireDefault(ALUOp.ADD)

  switch(io.uop) {
    is(ADD)  { aluOp := ALUOp.ADD  }
    is(SUB)  { aluOp := ALUOp.SUB  }
    is(AND)  { aluOp := ALUOp.AND  }
    is(OR)   { aluOp := ALUOp.OR   }
    is(XOR)  { aluOp := ALUOp.XOR  }
    is(SLL)  { aluOp := ALUOp.SLL  }
    is(SRL)  { aluOp := ALUOp.SRL  }
    is(SRA)  { aluOp := ALUOp.SRA  }
    is(SLT)  { aluOp := ALUOp.SLT  }
    is(SLTU) { aluOp := ALUOp.SLTU }

    is(ADDI)  { aluOp := ALUOp.ADD  }
    is(ANDI)  { aluOp := ALUOp.AND  }
    is(ORI)   { aluOp := ALUOp.OR   }
    is(XORI)  { aluOp := ALUOp.XOR  }
    is(SLLI)  { aluOp := ALUOp.SLL  }
    is(SRLI)  { aluOp := ALUOp.SRL  }
    is(SRAI)  { aluOp := ALUOp.SRA  }
    is(SLTI)  { aluOp := ALUOp.SLT  }
    is(SLTIU) { aluOp := ALUOp.SLTU }

    //For JAL and JALR we compute PC + 4 to save in rd
    is(JAL)   { aluOp := ALUOp.ADD }
    is(JALR)  { aluOp := ALUOp.ADD }

    is(NOP)   { aluOp := ALUOp.PASSB }
  }


  //We build the forwarding muxes
  //00 = no forwarding, 01 = forward from WB, 10 = forward from MEM
  val muxA = MuxLookup(io.forwardA, io.operandA, Seq(
    0.U -> io.operandA,
    1.U -> io.wbData,
    2.U -> io.memData
  ))

  val muxB = MuxLookup(io.forwardB, io.operandB, Seq(
    0.U -> io.operandB,
    1.U -> io.wbData,
    2.U -> io.memData
  ))


  //We connect the ALU inputs
  //For JAL and JALR, we save PC+4 in rd, so operandA is PC and operandB is 4
  val isJump = (io.uop === JAL) || (io.uop === JALR)

  alu.io.operandA  := Mux(isJump, io.pcIn, muxA)
  alu.io.operandB  := Mux(isJump, 4.U, muxB)
  alu.io.operation := aluOp


  //We send the ALU result and pass-through outputs to the EX barrier
  io.aluResult      := alu.io.aluResult
  io.outRD          := io.inRD
  io.outXcptInvalid := io.inXcptInvalid


  //We generate the write enable flag for the Forwarding Unit
  //R-type, I-type, JAL, and JALR all write to rd
  //Branches (BEQ, BNE, etc.) do NOT write to rd
  val isBranch = (io.uop === BEQ)  || (io.uop === BNE)  ||
                 (io.uop === BLT)  || (io.uop === BGE)  ||
                 (io.uop === BLTU) || (io.uop === BGEU)

  io.outRegWrite := !io.inXcptInvalid && (io.uop =/= NOP) && !isBranch


  //We evaluate the branch condition using the forwarded operands
  val branchCond = WireDefault(false.B)

  switch(io.uop) {
    is(BEQ)  { branchCond := (muxA === muxB) }
    is(BNE)  { branchCond := (muxA =/= muxB) }
    is(BLT)  { branchCond := (muxA.asSInt < muxB.asSInt) }
    is(BGE)  { branchCond := (muxA.asSInt >= muxB.asSInt) }
    is(BLTU) { branchCond := (muxA < muxB) }
    is(BGEU) { branchCond := (muxA >= muxB) }
  }


  //We decide if the branch or jump is taken
  //Conditional branches: taken only if branchCond is true
  //Unconditional jumps (JAL, JALR): always taken
  io.branchTaken := (isBranch && branchCond) || isJump


  //We compute the branch/jump target address
  //BEQ, BNE, BLT, BGE, BLTU, BGEU: target = PC + imm
  //JAL: target = PC + imm
  //JALR: target = (rs1 + imm) & ~1
  val branchTargetReg = WireDefault(0.U(32.W))

  when(io.uop === JALR) {
    branchTargetReg := (muxA + io.imm) & "hFFFFFFFE".U
  }.otherwise {
    branchTargetReg := io.pcIn + io.imm
  }

  io.branchTarget := branchTargetReg

}