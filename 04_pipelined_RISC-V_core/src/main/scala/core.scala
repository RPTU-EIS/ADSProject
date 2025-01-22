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
    val addr1 = Input(UInt(5.W))
    val addr2 = Input(UInt(5.W))
}

class regFileReadResp extends Bundle {
    // what signals does a read response need?
    val data1 = Output(UInt(32.W))
    val data2 = Output(UInt(32.W))
}

class regFileWriteReq extends Bundle {
    // what signals does a write request need?
    val addr = Input(UInt(5.W))
    val data = Output(UInt(32.W))
    val wen = Input(Bool()))
}

class regFile extends Module {
  val io = IO(new Bundle {
    val readReq  = new regFileReadReq
    val readResp = new regFileReadResp
    val writeReq = new regFileWriteReq
    // how many read and write ports do you need to handle all requests
    // from the pipeline to the register file simultaneously?
    // answer ---> 2 read ports, 1 write port
})
  
  /* 
    TODO: Initialize the register file as described in the task 
          and handle the read and write requests
   */


  // Register file initialization
  val regFile = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))

  // x0 is hard-wired to zero
  regFile(0) := 0.U

  // Read ports
  io.readResp.data1 := Mux(io.readReq.addr1 === 0.U, 0.U, regFile(io.readReq.addr1))
  io.readResp.data2 := Mux(io.readReq.addr2 === 0.U, 0.U, regFile(io.readReq.addr2))

  // Write port
  when(io.writeReq.wen && io.writeReq.addr =/= 0.U) { // don't write in x0
    regFile(io.writeReq.addr) := io.writeReq.data
  }
}


// -----------------------------------------
// Fetch Stage
// -----------------------------------------

class IF (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does this pipeline stage need?
    val pcOut = Output(UInt(32.W)) // Current program counter
    val instrOut = Output(UInt(32.W)) // Fetched instruction
  })

  /* 
    TODO: Initialize the IMEM as described in the task 
          and handle the instruction fetch.

    TODO: Update the program counter (no jumps or branches, 
          next PC always reads next address from IMEM)
   */

  val imem = Mem(4096, UInt(32.W)) // Memory with 4096 words (32-bit each)
  loadMemoryFromFile(imem, BinaryFile) // Load instructions from binary file

  val pc = RegInit(0.U(32.W)) // Program counter initialize to 0

  pc := pc + 4.U // Increment pc each cycle

  val instr = imem(pc >> 2) // Fetch instruction at address pc/4

  // Connect outputs to pipeline
  io.instrOut := instr
  io.pcOut := pc
}


// -----------------------------------------
// Decode Stage
// -----------------------------------------

class ID extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does this pipeline stage need?
    //val pcIn = Input(UInt(32.W)) // Next PC from the IF stage
    //val regFileReadReq = Output(new regFileReadReq) // Read request for register file
    val instrIn = Input(UInt(32.W)) // Fetched instruction from the IF stage
    val uop = Output(UInt(8.W)) // The micro-operation code (uopc)
    val immOut = Output(UInt(12.W))
    val rdOut = Output(UInt(5.W))
    val rs1Out = Output(UInt(5.W))
    val rs2Out = Output(UInt(5.W))
  })

  /* 
   * TODO: Any internal signals needed?
   */

  val opcode = io.instr(6, 0) 
  val rd = io.instr(11, 7) 
  val funct3 = io.instr(14, 12) 
  val rs1 = io.instr(19, 15) 
  val rs2 = io.instr(24, 20)
  val funct7 = io.instr(31, 25)
  val immOut = io.instr(31, 20)

  when(opcode === "b0110011".U && funct3 === "b000".U && funct7 === "b0000000".U) {
    io.uop := uopc.isADD
  }.elsewhen(opcode === "b0110011".U && funct3 === "b000".U && funct7 === "b0100000".U) {
    io.uop := uopc.isSUB
  }.elsewhen(opcode === "b0110011".U && funct3 === "b100".U && funct7 === "b0000000".U) { 
    io.uop := uopc.isXOR
  }.elsewhen(opcode === "b0110011".U && funct3 === "b110".U && funct7 === "b0000000".U) {
    io.uop := uopc.isOR
  }.elsewhen(opcode === "b0110011".U && funct3 === "b111".U && funct7 === "b0000000".U) {
    io.uop := uopc.isAND
  }.elsewhen(opcode === "b0110011".U && funct3 === "b001".U && funct7 === "b0000000".U) {
    io.uop := uopc.isSLL
  }.elsewhen(opcode === "b0110011".U && funct3 === "b101".U && funct7 === "b0000000".U) {
    io.uop := uopc.isSRL
  }.elsewhen(opcode === "b0110011".U && funct3 === "b101".U && funct7 === "b0100000".U) {
    io.uop := uopc.isSRA
  }.elsewhen(opcode === "b0110011".U && funct3 === "b010".U && funct7 === "b0000000".U) {
    io.uop := uopc.isSLT
  }.elsewhen(opcode === "b0110011".U && funct3 === "b011".U && funct7 === "b0000000".U) {
    io.uop := uopc.isSLTU
  }.elsewhen(opcode === "b0010011".U && funct3 === "b000".U) {
    io.uop := uopc.isADDI
  }.otherwise {
    io.uop := uopc.invalid  // Handle invalid opcode
  }

  /* 
   * TODO: Read the operands from teh register file
   */
  io.rs1Out := rs1
  io.rs2Out := rs2
  io.immOut := immOut
  io.rdOut := rd
}

// -----------------------------------------
// Execute Stage
// -----------------------------------------

class EX extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does this pipeline stage need?
    val opA = Input(UInt(32.W))
    val opB = Input(UInt(32.W))
    val uop = Input(UInt(8.W))
    val imm = Input(UInt(12.W))
    val result = Output(UInt(32.W))
  })

  when(io.uop == isADDI) {
    io.result := (io.imm.asSInt + io.opA.asSInt).asUInt
  }.elsewhen(io.uop == isADD) {
    io.result := (io.opA.asSInt + io.opB.asSInt).asUInt
  }.elsewhen(io.uop == isSUB) {
    io.result := io.opA - io.opB
  }.elsewhen(io.uop == isSLL) {
    io.result := io.opA << io.opB(4, 0) // Logical left shift
  }.elsewhen(io.uop == isSRL){
    io.result := io.opA >> io.opB(4, 0) // Logical right shift
  }.elsewhen(io.uop == isSRA){
    io.result := (io.opA.asSInt >> io.opB(4, 0)).asUInt // Arithmetic right shift
  }.elsewhen(io.uop == isSLT) {
    io.result := (io.opA.asSInt < io.opB.asSInt).asUInt // Set less than (signed)
  }.elsewhen(io.uop == isSLTU) {
    io.result := (io.opA < io.opB).asUInt // Set less than (unsigned)
  }.elsewhen(io.uop == isAND) {
    io.result := io.opA & io.opB // Bitwise AND
  }.elsewhen(io.uop == isOR) {
    io.result := io.opA | io.opB // Bitwise OR
  }.elsewhen(io.uop == isXOR) {
    io.result := io.opA ^ io.opB // Bitwise XOR
  }.otherwise {
    io.result := 5.U // Default case
  }
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
    val result = Input(UInt(32.W))
    val rdIn = Input(UInt(5.W))
    
    val addr = Output(UInt(5.W))
    val data = Output(UInt(32.W))
    val wen = Output(Bool()))
  })

  /* 
   * TODO: Perform the write back to the register file and set 
   *       the check_res signal for the testbench.
   */
  io.addr := io.rdIn
  io.data := io.result
  io.wen := 1.U && (io.rdIn =/= 0.U) //&& io.wenIn
}


// -----------------------------------------
// IF-Barrier
// -----------------------------------------

class IFBarrier extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does this barrier need?
    val instrIn = Input(UInt(32.W)) // Input instruction from IF stage
    //val pcIn = Input(UInt(32.W)) // Input pc from IF stage
    val instrOut = Output(UInt(32.W)) // Output instruction to ID stage
    //val pcOut = Output(UInt(32.W)) // Output PC to ID stage
  })

  /* 
   * TODO: Define registers
   *
   * TODO: Fill registers from the inputs and write regioster values to the outputs
   */

  val instrReg = Reg(UInt(32.W))
  //val pcReg = Reg(UInet(32.W))

  instrReg := io.instrIn
  //pcReg := io.pcIn

  io.instrOut := instrReg
  //io.pcOut := pcReg
}


// -----------------------------------------
// ID-Barrier
// -----------------------------------------

class IDBarrier extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does this barrier need?
    val op1In = Input(UInt(32.W))
    val op2In = Input(UInt(32.W))
    val uopIn = Input(UInt(8.W))
    val immIn = Input(UInt(12.W))
    val rdIn  = Input(UInt(5.W))
    val rs1In = Input(Uint(5.W))
    val rs2In = Input(Uint(5.W))

    val op1Out = Output(UInt(32.W))
    val op2Out = Output(UInt(32.W))
    val uopOut = Output(UInt(8.W))
    val immOut = Output(UInt(12.W))
    val rdOut  = Output(UInt(5.W))
    val rs1Out = Output(Uint(5.W))
    val rs2Out = Output(Uint(5.W))
  })

  /* 
   * TODO: Define registers
   *
   * TODO: Fill registers from the inputs and write regioster values to the outputs
   */

  val op1Reg = Reg(UInt(32.W))
  val op2Reg = Reg(UInt(32.W))
  val uopReg = Reg(UInt(8.W))
  val immReg = Reg(UInt(12.W))
  val rdReg  = Reg(UInt(5.W))
  val rs1Reg = Reg(UInt(5.W))
  val rs2Reg = Reg(UInt(5.W))

  op1Reg := io.op1In
  op2Reg := io.op2In
  uopReg := io.uopIn
  immReg := io.immIn
  rdReg  := io.rdIn
  rs1Reg := io.rs1In
  rs2Reg := io.rs2In
  
  io.op1Out := op1Reg
  io.op2Out := op2Reg
  io.uopOut := uopReg
  io.immOut := immReg
  io.rdOut  := rdReg
  io.rs1Out := rs1Reg
  io.rs2Out := rs2Reg
}


// -----------------------------------------
// EX-Barrier
// -----------------------------------------

class EXBarrier extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does this barrier need?
    val resultIn = Input(UInt(32.W))
    val rdIn  = Input(UInt(5.W))

    val resultOut = Output(UInt(32.W))
    val rdOut  = Output(UInt(5.W))
  })

  /* 
   * TODO: Define registers
   *
   * TODO: Fill registers from the inputs and write regioster values to the outputs
  */

  val resultReg = Reg(UInt(32.W))
  val rdReg  = Reg(UInt(5.W))

  resultReg := io.resultIn
  rdReg     := io.rdIn

  io.resultOut := resultReg
  io.rdOut     := rdReg
}


// -----------------------------------------
// MEM-Barrier
// -----------------------------------------

class MEMBarrier extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does this barrier need?
    val resultIn = Input(UInt(32.W))
    val rdIn  = Input(UInt(5.W))

    val resultOut = Output(UInt(32.W))
    val rdOut  = Output(UInt(5.W))
  })

  /* 
   * TODO: Define registers
   *
   * TODO: Fill registers from the inputs and write regioster values to the outputs
  */

  val resultReg = Reg(UInt(32.W))
  val rdReg  = Reg(UInt(5.W))

  resultReg := io.resultIn
  rdReg     := io.rdIn

  io.resultOut := resultReg
  io.rdOut     := rdReg
}


// -----------------------------------------
// WB-Barrier
// -----------------------------------------

class WBBarrier extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does this barrier need?
    val resultIn = Input(UInt(32.W))

    val resultOut = Output(UInt(32.W))
  })

  /* 
   * TODO: Define registers
   *
   * TODO: Fill registers from the inputs and write regioster values to the outputs
  */

  val resultReg = Reg(UInt(32.W))

  resultReg := io.resultIn

  io.resultOut := resultReg
}

class PipelinedRV32Icore (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    val check_res = Output(UInt(32.W))
  })


  /* 
   * TODO: Instantiate Barriers
   */
  val ifBarrier = Module(new IFBarrier)
  val idBarrier = Module(new IDBarrier)
  val exBarrier = Module(new EXBarrier)
  val memBarrier = Module(new MEMBarrier)
  val wbBarrier = Module(new WBBarrier)

  /* 
   * TODO: Instantiate Pipeline Stages
   */
  val ifStage = Module(new IF(BinaryFile))
  val idStage = Module(new ID)
  val exStage = Module(new EX)
  val memStage = Module(new MEM)
  val wbStage = Module(new WB)


  /* 
   * TODO: Instantiate Register File
   */

  val regFile = Module(new regFile)
  io.check_res := 0.U // necessary to make the empty design buildable TODO: change this

  /* 
   * TODO: Connect all IOs between the stages, barriers and register file.
   * Do not forget the global output of the core module
   */
  ifBarrier.io.instrIn := ifStage.io.instrOut
  //ifBarrier.io.pcIn := ifStage.io.pcOut

  idStage.io.instrIn := ifBarrier.io.instrOut
  //idStage.io.pcIn := ifBarrier.io.pcOut
 
  regFile.io.readReq.addr1 := idStage.io.rs1Out
  regFile.io.readReq.addr2 := idStage.io.rs2Out

  idBarrier.io.op1In := regFile.io.readResp.data1
  idBarrier.io.op2In := regFile.io.readResp.data2
  idBarrier.io.uopIn := idStage.io.uopOut
  idBarrier.io.immIn := idStage.io.immOut
  idBarrier.io.rdIn := idStage.io.rdOut
  idBarrier.io.rs1In := idStage.io.rs1Out
  idBarrier.io.rs2In := idStage.io.rs2Out

  exStage.io.opA := idBarrier.io.op1Out
  exStage.io.opB := idBarrier.io.op2Out
  exStage.io.rdIn := idBarrier.io.rdOut
  exStage.io.uop := idBarrier.io.uopOut
  exStage.io.imm := idBarrier.io.immOut

  exBarrier.io.resultIn := exStage.io.result
  exBarrier.io.rdIn := idBarrier.io.rdOut

  memBarrier.io.resultIn := exBarrier.io.resultOut
  memBarrier.io.rdIn := exBarrier.io.rdOut

  wbStage.io.result := memBarier.io.resultOut
  wbStage.io.rdIn := memBarier.io.rdOut

  regFile.io.writeReq.addr := wbStage.io.addr
  regFile.io.writeReq.data := wbStage.io.data
  regFile.io.writeReq.wen := wbStage.io.wen

  wbBarrier.io.resultIn := wbStage.io.data

  io.check_res := wbBarrier.io.resultOut
}

