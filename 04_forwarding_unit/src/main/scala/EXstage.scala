package core_tile

import chisel3._
import chisel3.util._
import Assignment02.{ALU, ALUOp}
import uopc._

// -----------------------------------------
// Execute Stage with forwarding muxes
// -----------------------------------------

//Start code

//We added 4 new inputs and 2 multiplexers to support forwarding:
//- forwardA and forwardB come from the Forwarding Unit
//- memData and wbData are the values to forward from MEM and WB barriers
//- rs1 and rs2 are exposed so the Forwarding Unit can read them
//- muxA and muxB select between 3 sources for the ALU operands
//We also added outRegWrite to tell the Forwarding Unit if this instruction writes to a register

class EX extends Module {

  //We define the inputs and outputs of the EX stage

  val io = IO(new Bundle {

    //Inputs from ID barrier
    val uop           = Input(uopc())        // operation code from decoder
    val inRD          = Input(UInt(5.W))     // destination register
    val operandA      = Input(UInt(32.W))    // first operand from Register File
    val operandB      = Input(UInt(32.W))    // second operand from Register File
    val inXcptInvalid = Input(Bool())        // invalid flag

    //New inputs from the Forwarding Unit
    val forwardA      = Input(UInt(2.W))     // mux control for operand A
    val forwardB      = Input(UInt(2.W))     // mux control for operand B

    //New inputs from MEM and WB barriers, used as forwarded values
    val memData       = Input(UInt(32.W))    // ALU result from MEM barrier
    val wbData        = Input(UInt(32.W))    // ALU result from WB barrier

    //New inputs to expose rs1 and rs2 so the Forwarding Unit can compare them
    val rs1           = Input(UInt(5.W))     // source register 1
    val rs2           = Input(UInt(5.W))     // source register 2

    //Outputs to EX barrier
    val aluResult      = Output(UInt(32.W))  // ALU result going out
    val outRD          = Output(UInt(5.W))   // destination register going out
    val outXcptInvalid = Output(Bool())      // invalid flag going out

    //New output for the Forwarding Unit, tells if this instruction writes to a register
    val outRegWrite    = Output(Bool())      // write enable flag
  })


  //We create the ALU

  val alu = Module(new ALU())


  //We map the uop to the ALU operation code, default is ADD

  val aluOp = WireDefault(ALUOp.ADD)

  switch(io.uop) {
    //R-type operations
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

    //I-type operations
    is(ADDI)  { aluOp := ALUOp.ADD  }
    is(ANDI)  { aluOp := ALUOp.AND  }
    is(ORI)   { aluOp := ALUOp.OR   }
    is(XORI)  { aluOp := ALUOp.XOR  }
    is(SLLI)  { aluOp := ALUOp.SLL  }
    is(SRLI)  { aluOp := ALUOp.SRL  }
    is(SRAI)  { aluOp := ALUOp.SRA  }
    is(SLTI)  { aluOp := ALUOp.SLT  }
    is(SLTIU) { aluOp := ALUOp.SLTU }

    //NOP, just pass operand B (which is 0)
    is(NOP)   { aluOp := ALUOp.PASSB }
  }


  //We create the forwarding muxes
  //The Forwarding Unit decides which value the ALU uses for each operand
  //Without these muxes the ALU would use the wrong value when a hazard happens

  //Mux for operand A, 3 possible sources:
  //00 = use the original operandA from the Register File
  //01 = forward the value from WB barrier
  //10 = forward the value from MEM barrier

  val muxA = MuxLookup(io.forwardA, io.operandA, Seq(
    0.U -> io.operandA,                      // no forwarding
    1.U -> io.wbData,                        // forward from WB
    2.U -> io.memData                        // forward from MEM
  ))

  //Mux for operand B, same logic as Mux A

  val muxB = MuxLookup(io.forwardB, io.operandB, Seq(
    0.U -> io.operandB,                      // no forwarding
    1.U -> io.wbData,                        // forward from WB
    2.U -> io.memData                        // forward from MEM
  ))


  //We connect the muxed operands and the operation code to the ALU
  //Now the ALU receives the value chosen by the mux, not the original operand directly

  alu.io.operandA  := muxA                   // forwarded or original operand A
  alu.io.operandB  := muxB                   // forwarded or original operand B
  alu.io.operation := aluOp                  // operation from uop


  //We send the outputs to the EX barrier

  io.aluResult      := alu.io.aluResult      // ALU result
  io.outRD          := io.inRD               // destination register
  io.outXcptInvalid := io.inXcptInvalid      // invalid flag

  //We generate the write enable flag for the Forwarding Unit
  //An instruction writes to a register only if it is valid and is not a NOP

  io.outRegWrite    := !io.inXcptInvalid && (io.uop =/= NOP)  // true if instruction writes

}