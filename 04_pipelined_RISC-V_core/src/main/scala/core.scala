// ADS I Class Project
// Pipelined RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/15/2023 by Tobias Jauch (@tojauch)

/*
The goal of this task is to extend the 5-stage multi-cycle 32-bit RISC-V core from the previous task to a pipelined processor. 
All steps and stages have the same functionality as in the multi-cycle version from task 03, but are supposed to handle different instructions in each stage simultaneously.
This design implements a pipelined RISC-V 32-bit core with five stages: IF (Fetch), ID (Decode), EX (Execute), MEM (Memory), and WB (Writeback).

    Data Types:
        The uopc enumeration data type (enum) defines micro-operation codes representing ALU operations according to the RV32I subset used in the previous tasks.

    Register File (regFile):
        The regFile module represents the register file, which has read and write ports.
        It consists of a 32-entry register file (x0 is hard-wired to zero).
        Reading from and writing to the register file is controlled by the read request (regFileReadReq), read response (regFileReadResp), and write request (regFileWriteReq) interfaces.

    Fetch Stage (IF Module):
        The IF module represents the instruction fetch stage.
        It includes an instruction memory (IMem) of size 4096 words (32-bit each).
        Instructions are loaded from a binary file (provided to the testbench as a parameter) during initialization.
        The program counter (PC) is used as an address to access the instruction memory, and one instruction is fetched in each cycle.

    Decode Stage (ID Module):
        The ID module performs instruction decoding and generates control signals.
        It extracts opcode, operands, and immediate values from the instruction.
        It uses the uopc (micro-operation code) Enum to determine the micro-operation (uop) and sets control signals accordingly.
        The register file requests are generated based on the operands in the instruction.

    Execute Stage (EX Module):
        The EX module performs the arithmetic or logic operation based on the micro-operation code.
        It takes two operands and produces the result (aluResult).

    Memory Stage (MEM Module):
        The MEM module does not perform any memory operations in this basic CPU design.

    Writeback Stage (WB Module):
        The WB module writes the result back to the register file.

    IF, ID, EX, MEM, WB Barriers:
        IFBarrier, IDBarrier, EXBarrier, MEMBarrier, and WBBarrier modules serve as pipeline registers to separate the pipeline stages.
        They hold the intermediate results of each stage until the next clock cycle.

    PipelinedRV32Icore (PipelinedRV32Icore Module):
        The top-level module that connects all the pipeline stages, barriers and the register file.
        It interfaces with the external world through check_res, which is the result produced by the core.

Overall Execution Flow:

    1) Instructions are fetched from the instruction memory in the IF stage.
    2) The fetched instruction is decoded in the ID stage, and the corresponding micro-operation code is determined.
    3) The EX stage executes the operation using the operands.
    4) The MEM stage does not perform any memory operations in this design.
    5) The result is written back to the register file in the WB stage.

Note that this design only represents a simplified RISC-V pipeline. The structure could be equipped with further instructions and extension to support a real RISC-V ISA.
*/

package core_tile

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile


// -----------------------------------------
// Global Definitions and Data Types
// -----------------------------------------

object uopc extends ChiselEnum {

  val isADD   = Value(0x01.U)
  val isSUB   = Value(0x02.U)
  val isXOR   = Value(0x03.U)
  val isOR    = Value(0x04.U)
  val isAND   = Value(0x05.U)
  val isSLL   = Value(0x06.U)
  val isSRL   = Value(0x07.U)
  val isSRA   = Value(0x08.U)
  val isSLT   = Value(0x09.U)
  val isSLTU  = Value(0x0A.U)

  val isADDI  = Value(0x10.U)

  val invalid = Value(0xFF.U)
}

import uopc._


// -----------------------------------------
// Register File
// -----------------------------------------

class regFileReadReq extends Bundle {
    // what signals does a read request need?
    val rs1 = Input(UInt(32.W))
    val rs2 = Input(UInt(32.W))
}

class regFileReadResp extends Bundle {
    // what signals does a read response need?
    val rs1_data = Output(UInt(32.W))
    val rs2_data = Output(UInt(32.W))
}

class regFileWriteReq extends Bundle {
    // what signals does a write request need?
    val addr = UInt(32.W)
    val wrEn = UInt(1.W)
}

class regFile extends Module {
  val io = IO(new Bundle {
    val req  = new regFileReadReq
    val resp = new regFileReadResp
    // how many read and write ports do you need to handle all requests
    // from the ipeline to the register file simultaneously?
    val wrReq = Input(new regFileWriteReq)
    val wrData = Input(UInt(32.W))

})
  
  /* 
    TODO: Initialize the register file as described in the task 
          and handle the read and write requests
   */
    val regBank = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))
    regBank(0) := 0.U // x0 is always zero
  
    io.resp.rs1_data := regBank(io.req.rs1)
    io.resp.rs2_data := regBank(io.req.rs2)
    
    when((io.wrReq.addr =/= 0.U) && (io.wrReq.wrEn === 1.U)){
    regBank(io.wrReq.addr) := io.wrData
  }
}


// -----------------------------------------
// Fetch Stage
// -----------------------------------------

class IF (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does this pipeline stage need?
    val instruction = Output(UInt(32.W))
  })

  /* 
    TODO: Initialize the IMEM as described in the task 
          and handle the instruction fetch.

    TODO: Update the program counter (no jumps or branches, 
          next PC always reads next address from IMEM)
   */
    val PC = RegInit(0.U(32.W))
    PC := PC + 4.U

    val IMem = Mem(4096, UInt(32.W))
    loadMemoryFromFile(IMem, BinaryFile)
    
    io.instruction := IMem.read(PC>>2.U)
  
}


// -----------------------------------------
// Decode Stage
// -----------------------------------------

class ID extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does this pipeline stage need?
    // Inputs from ID stage
    val instruction = Input(UInt(32.W))

    // Outputs to Ex stage
    val immIExtended = Output(UInt(32.W))
    val uop = Output(uopc())

    // to RegisterFile
    val regFileReadReqOut = Flipped(new regFileReadReq)
    val regFileWriteReqOut = Output(new regFileWriteReq)

  })

  /* 
   * TODO: Any internal signals needed?
   */
  // decode instruction
  val opcode = io.instruction(6, 0)
  val funct7 = io.instruction(31, 25)
  val funct3 = io.instruction(14, 12)
  val rs1    = io.instruction(19, 15)
  val rs2    = io.instruction(24, 20)
  val rd     = io.instruction(11,  7)
  val immI   = io.instruction(31, 20)

  val RTypeOp = (opcode === "b0110011".U)
  val unknownIns = WireInit(0.U(1.W)) // check_res = 0

  /* 
    Determine the uop based on the disassembled instruction

    when( condition ){
      when( next condition ){
        io.upo := isXYZ
      }.otherwise{
        maybe declare a case to catch invalid instructions
      } 
    }.elsewhen( different condition ){
      when( next condition ){
        io.upo := isXYZ
      }.otherwise{
        maybe declare a case to catch invalid instructions
      } 
    }.otherwise{
      maybe declare a case to catch invalid instructions
    }
  */
  when     (opcode === "b0110011".U && funct3 === "b000".U && funct7 === "b0000000".U){ io.uop := uopc.isADD }
  .elsewhen(opcode === "b0110011".U && funct3 === "b000".U && funct7 === "b0100000".U){ io.uop := uopc.isSUB }
  .elsewhen(opcode === "b0110011".U && funct3 === "b001".U && funct7 === "b0000000".U){ io.uop := uopc.isSLL }
  .elsewhen(opcode === "b0110011".U && funct3 === "b010".U && funct7 === "b0000000".U){ io.uop := uopc.isSLT }
  .elsewhen(opcode === "b0110011".U && funct3 === "b011".U && funct7 === "b0000000".U){ io.uop := uopc.isSLTU}
  .elsewhen(opcode === "b0110011".U && funct3 === "b100".U && funct7 === "b0000000".U){ io.uop := uopc.isXOR }
  .elsewhen(opcode === "b0110011".U && funct3 === "b101".U && funct7 === "b0000000".U){ io.uop := uopc.isSRL }
  .elsewhen(opcode === "b0110011".U && funct3 === "b101".U && funct7 === "b0100000".U){ io.uop := uopc.isSRA }
  .elsewhen(opcode === "b0110011".U && funct3 === "b110".U && funct7 === "b0000000".U){ io.uop := uopc.isOR  }
  .elsewhen(opcode === "b0110011".U && funct3 === "b111".U && funct7 === "b0000000".U){ io.uop := uopc.isAND }
  .elsewhen(opcode === "b0010011".U && funct3 === "b000".U)                           { io.uop := uopc.isADDI} // ADDI
  .otherwise{ io.uop := uopc.invalid; unknownIns := 1.U } // unknown instructions: ADD 0 + 0 = 0

  /* 
   * TODO: Read the operands from teh register file
   */
  io.regFileReadReqOut.rs1 := rs1
  io.regFileReadReqOut.rs2 := rs2

  // write regFile
  io.regFileWriteReqOut.addr := rd
  io.regFileWriteReqOut.wrEn := (unknownIns === 0.U) // all instructions except invalid instructions write back to regFile in this design
  
  io.immIExtended := Cat(Fill(20, immI(11)), immI)
}

// -----------------------------------------
// Execute Stage
// -----------------------------------------

class EX extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does this pipeline stage need?
    // Inputs from Decode stage
    val immIExtended = Input(UInt(32.W))
    val regFileReadRespIn = Flipped(new regFileReadResp)
    val uop = Input(uopc())

    // Output to Memory stage
    val ALUResult = Output(UInt(32.W))
    val zero = Output(UInt(1.W))
  })

  /* 
    TODO: Perform the ALU operation based on the uopc

    when( uopc === isXYZ ){
      result := operandA + operandB
    }.elsewhen( uopc === isABC ){
      result := operandA - operandB
    }.otherwise{
      maybe also declare a case to catch invalid instructions
    }
  */

    io.ALUResult := 0.U // default

    switch (io.uop){ // ADD, SUB, SLL, SLT, SLTU, XOR, SRL, SRA, OR, AND
        is(uopc.isADD){
            io.ALUResult := io.regFileReadRespIn.rs1_data + io.regFileReadRespIn.rs2_data
        }
        is(uopc.isSUB){
            io.ALUResult := io.regFileReadRespIn.rs1_data - io.regFileReadRespIn.rs2_data
        }
        is(uopc.isSLL){
            io.ALUResult := io.regFileReadRespIn.rs1_data << io.regFileReadRespIn.rs2_data(4,0).asUInt
        }
        is(uopc.isSLT){
            io.ALUResult := (io.regFileReadRespIn.rs1_data.asSInt < io.regFileReadRespIn.rs2_data.asSInt).asUInt
        }
        is(uopc.isSLTU){
            io.ALUResult := io.regFileReadRespIn.rs1_data.asUInt < io.regFileReadRespIn.rs2_data.asUInt
        }
        is(uopc.isXOR){
            io.ALUResult := io.regFileReadRespIn.rs1_data ^ io.regFileReadRespIn.rs2_data
        }
        is(uopc.isSRL){
            io.ALUResult := io.regFileReadRespIn.rs1_data >> io.regFileReadRespIn.rs2_data(4,0).asUInt
        }
        is(uopc.isSRA){
            io.ALUResult := (io.regFileReadRespIn.rs1_data.asSInt >> io.regFileReadRespIn.rs2_data(4,0).asUInt).asUInt
        }
        is(uopc.isOR){
            io.ALUResult := io.regFileReadRespIn.rs1_data | io.regFileReadRespIn.rs2_data
        }
        is(uopc.isAND){
            io.ALUResult := io.regFileReadRespIn.rs1_data & io.regFileReadRespIn.rs2_data
        }
        is(uopc.isADDI){
            io.ALUResult := io.regFileReadRespIn.rs1_data + io.immIExtended
        }
        is(uopc.invalid){
            io.ALUResult := 0.U
        }
    }
    io.zero := Mux((io.uop === uopc.invalid), 0.U, (io.ALUResult === 0.U))
}

// -----------------------------------------
// Memory Stage
// -----------------------------------------

class MEM extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does this pipeline stage need?
  })

  // No memory operations implemented in this basic CPU

}


// -----------------------------------------
// Writeback Stage
// -----------------------------------------

class WB extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does this pipeline stage need?
    val ALUResult = Input(UInt(32.W))
    val regFileData = Output(UInt(32.W))
    val regFileWriteReqIn = Input(new regFileWriteReq)
    val regFileWriteReqOut = Output(new regFileWriteReq)
  })

  /* 
   * TODO: Perform the write back to the register file and set 
   *       the check_res signal for the testbench.
   */
  io.regFileWriteReqOut := io.regFileWriteReqIn
  io.regFileData := io.ALUResult

}


// -----------------------------------------
// IF-Barrier
// -----------------------------------------

class IFBarrier extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does this barrier need?
    // inputs from IF stage
    val instructionIn = Input(UInt(32.W))
    
    // outputs to ID stage
    val instructionOut = Output(UInt(32.W))
  })

  /* 
   * TODO: Define registers
   *
   * TODO: Fill registers from the inputs and write regioster values to the outputs
   */

  io.instructionOut := RegNext(io.instructionIn, 0.U)
}


// -----------------------------------------
// ID-Barrier
// -----------------------------------------

class IDBarrier extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does this barrier need?
    // Inputs from ID stage
    val regFileReadRespIn = Flipped(new regFileReadResp)
    val immIExtendedIn = Input(UInt(32.W))
    val uopIn = Input(uopc())
    val regFileWriteReqIn = Input(new regFileWriteReq)
    
    // Outputs to Ex stage
    val regFileReadRespOut = new regFileReadResp
    val immIExtendedOut = Output(UInt(32.W))
    val uopOut = Output(uopc())
    val regFileWriteReqOut = Output(new regFileWriteReq)
  })

  /* 
   * TODO: Define registers
   *
   * TODO: Fill registers from the inputs and write regioster values to the outputs
   */

    io.regFileReadRespOut := RegNext(io.regFileReadRespIn, 0.U.asTypeOf(new regFileReadResp))
    io.immIExtendedOut := RegNext(io.immIExtendedIn, 0.U)
    io.uopOut := RegNext(io.uopIn, 0.U)
    io.regFileWriteReqOut := RegNext(io.regFileWriteReqIn, 0.U.asTypeOf(new regFileWriteReq))
}


// -----------------------------------------
// EX-Barrier
// -----------------------------------------

class EXBarrier extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does this barrier need?
    // Inputs from Ex stage
    val ALUResultIn = Input(UInt(32.W))
    val regFileWriteReqIn = Input(new regFileWriteReq)

    // Outputs to Mem stage
    val ALUResultOut = Output(UInt(32.W))
    val regFileWriteReqOut= Output(new regFileWriteReq)
  })

  /* 
   * TODO: Define registers
   *
   * TODO: Fill registers from the inputs and write regioster values to the outputs
  */

    io.ALUResultOut := RegNext(io.ALUResultIn, 0.U)
    io.regFileWriteReqOut := RegNext(io.regFileWriteReqIn, 0.U.asTypeOf(new regFileWriteReq))
}


// -----------------------------------------
// MEM-Barrier
// -----------------------------------------

class MEMBarrier extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does this barrier need?
    // Inputs from Mem stage
    val ALUResultIn = Input(UInt(32.W))
    val regFileWriteReqIn = Input(new regFileWriteReq)

    // Outputs to WB stage
    val ALUResultOut = Output(UInt(32.W))
    val regFileWriteReqOut= Output(new regFileWriteReq)
  })

  /* 
   * TODO: Define registers
   *
   * TODO: Fill registers from the inputs and write regioster values to the outputs
  */

    io.ALUResultOut := RegNext(io.ALUResultIn, 0.U)
    io.regFileWriteReqOut := RegNext(io.regFileWriteReqIn, 0.U.asTypeOf(new regFileWriteReq))
}


// -----------------------------------------
// WB-Barrier
// -----------------------------------------

class WBBarrier extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does this barrier need?
  })

  /* 
   * TODO: Define registers
   *
   * TODO: Fill registers from the inputs and write regioster values to the outputs
  */

}



class PipelinedRV32Icore (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    val check_res = Output(UInt(32.W))
  })


  /* 
   * TODO: Instantiate Barriers
   */
    val IFBarrierInst  = Module(new IFBarrier)
    val IDBarrierInst  = Module(new IDBarrier)
    val EXBarrierInst  = Module(new EXBarrier)
    val MEMBarrierInst = Module(new MEMBarrier)


  /* 
   * TODO: Instantiate Pipeline Stages
   */
    val IFStage  = Module(new IF(BinaryFile))
    val IDStage  = Module(new ID)
    val EXStage  = Module(new EX)
    val MEMStage = Module(new MEM)
    val WBStage  = Module(new WB)


  /* 
   * TODO: Instantiate Register File
   */
    val regFileInst = Module(new regFile)

    io.check_res := WBStage.io.regFileData // necessary to make the empty design buildable TODO: change this

  /* 
   * TODO: Connect all IOs between the stages, barriers and register file.
   * Do not forget the global output of the core module
   */

    //IF stage (no inputs)

    //IF/ID buffer
    IFBarrierInst.io.instructionIn := IFStage.io.instruction
    
    //ID stage
    IDStage.io.instruction := IFBarrierInst.io.instructionOut
    
    //ID/EX buffer
    IDBarrierInst.io.regFileReadRespIn := regFileInst.io.resp
    IDBarrierInst.io.immIExtendedIn := IDStage.io.immIExtended
    IDBarrierInst.io.uopIn := IDStage.io.uop
    IDBarrierInst.io.regFileWriteReqIn := IDStage.io.regFileWriteReqOut

    //EX stage
    EXStage.io.immIExtended := IDBarrierInst.io.immIExtendedOut
    EXStage.io.regFileReadRespIn := IDBarrierInst.io.regFileReadRespOut
    EXStage.io.uop := IDBarrierInst.io.uopOut

    //EX/MEM buffer
    EXBarrierInst.io.ALUResultIn := EXStage.io.ALUResult
    EXBarrierInst.io.regFileWriteReqIn := IDBarrierInst.io.regFileWriteReqOut

    //MEM stage (No inputs)

    //MEM/WB buffer
    MEMBarrierInst.io.ALUResultIn := EXBarrierInst.io.ALUResultOut
    MEMBarrierInst.io.regFileWriteReqIn := EXBarrierInst.io.regFileWriteReqOut

    //WB stage
    WBStage.io.ALUResult := MEMBarrierInst.io.ALUResultOut
    WBStage.io.regFileWriteReqIn := MEMBarrierInst.io.regFileWriteReqOut

    // reg file
    regFileInst.io.req := IDStage.io.regFileReadReqOut
    regFileInst.io.wrReq := WBStage.io.regFileWriteReqOut
    regFileInst.io.wrData := WBStage.io.regFileData
}

