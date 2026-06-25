// ADS I Class Project
// Pipelined RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/15/2023 by Tobias Jauch (@tojauch)

/*
The goal of this task is to implement a 5-stage pipeline that features a subset of RV32I (all R-type and I-type instructions). 

    Instruction Memory:
        The CPU has an instruction memory (IMem) with 4096 words, each of 32 bits.
        The content of IMem is loaded from a binary file specified during the instantiation of the MultiCycleRV32Icore module.

    CPU Registers:
        The CPU has a program counter (PC) and a register file (regFile) with 32 registers, each holding a 32-bit value.
        Register x0 is hard-wired to zero.

    Microarchitectural Registers / Wires:
        Various signals are defined as either registers or wires depending on whether they need to be used in the same cycle or in a later cycle.

    Processor Stages:
        The FSM of the processor has five stages: fetch, decode, execute, memory, and writeback.
        All stages are active at the same time and process different instructions simultaneously.

        Fetch Stage:
            The instruction is fetched from the instruction memory based on the current value of the program counter (PC).

        Decode Stage:
            Instruction fields such as opcode, rd, funct3, and rs1 are extracted.
            For R-type instructions, additional fields like funct7 and rs2 are extracted.
            Control signals (isADD, isSUB, etc.) are set based on the opcode and funct3 values.
            Operands (operandA and operandB) are determined based on the instruction type.

        Execute Stage:
            Arithmetic and logic operations are performed based on the control signals and operands.
            The result is stored in the aluResult register.

        Memory Stage:
            No memory operations are implemented in this basic CPU.

        Writeback Stage:
            The result of the operation (writeBackData) is written back to the destination register (rd) in the register file.

    Check Result:
        The final result (writeBackData) is output to the io.check_res signal.
        The exception signal is also passed to the wrapper module. It indicates whether an invalid instruction has been encountered.
        In the fetch stage, a default value of 0 is assigned to io.check_res.
*/

package core_tile

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile
import Assignment02.{ALU, ALUOp}
import uopc._


class PipelinedRV32Icore (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    //ToDo: Add I/O ports

      val check_res = Output(UInt(32.W))
      val exception = Output(Bool())
    })
  // Temporary outputs to allow compilation.
  // Replace with WBBarrier outputs after pipeline integration.
  //io.check_res := 0.U
  //io.exception := false.B
  //ToDo: Add your implementation according to the specification above here

  //shaker 
  // instantiating the 5 pipeline stages
  val fetchStage     = Module(new IFStage(BinaryFile))
  val decodeStage    = Module(new IDStage())
  val executeStage   = Module(new EXStage())
  val memoryStage    = Module(new MEMStage())
  val writebackStage = Module(new WBStage())


  // instantiating the 5 barriers 
  // These hold the state between clock cycles to allow parallel execution
  val IfBarrier   = Module(new IFBarrier())
  val IdBarrier   = Module(new IDBarrier())
  val ExBarrier   = Module(new EXBarrier())
  val MemBarrier  = Module(new MEMBarrier())
  val WbBarrier   = Module(new WBBarrier())


  //stage 1: The fetch stage retrieves instructions from memory
  IfBarrier.io.instrReg := fetchStage.io.instr
  

 // Stage 2: Decodes raw instructions and fetches operands from the Register File

 decodeStage.io.instr := decodeStage.io.outInstr

 
 // Feedback loop: Connecting WB results back to the REgister File in ID
 // it supposed to be b/w RegisterFile and WBStage!


 //Passing decoded data to the ID/EX Barrier
 IdBarrier.io.inUOP          := decodeStage.io.uop
 IdBarrier.io.inRD           := decodeStage.io.rd
 IdBarrier.io.inOperandA     := decodeStage.io.operandA
 IdBarrier.io.inOperandB     := decodeStage.io.operandB
 IdBarrier.io.inXcptInvalid  := decodeStage.io.XcptInvalid

  // Forwarding connections
  IdBarrier.io.inRs1 := decodeStage.io.rs1
  IdBarrier.io.inRs2 := decodeStage.io.rs2

  executeStage.io.inRs1 := IdBarrier.io.outRs1
  executeStage.io.inRs2 := IdBarrier.io.outRs2

  executeStage.io.rdEX := ExBarrier.io.outRD
  executeStage.io.rdMEM := MemBarrier.io.outRD
  executeStage.io.rdWB := WbBarrier.io.outRD

  executeStage.io.aluResEX := ExBarrier.io.outAluResult
  executeStage.io.aluResMEM := MemBarrier.io.outAluResult
  executeStage.io.aluResWB := WbBarrier.io.outCheckRes



 //Stage 3: Performs ALU operations based on the micro-operation (uop) 
 executeStage.io.inUOP          := IdBarrier.io.outUOP
 executeStage.io.inOperandA     := IdBarrier.io.outOperandA
 executeStage.io.inOperandB     := IdBarrier.io.outOperandB
 executeStage.io.inRD           := IdBarrier.io.outRD
 executeStage.io.inXcptInvalid  := IdBarrier.io.outXcptInvalid


 ExBarrier.io.inAluResult   := executeStage.io.aluResult
 ExBarrier.io.inRD          := executeStage.io.rd
 ExBarrier.io.inXcptInvalid := executeStage.io.exception


 // Stage 4: directly connecting EXBarrier to MEMBarrier
 MemBarrier.io.inAluResult     := ExBarrier.io.outAluResult
 MemBarrier.io.inRD            := ExBarrier.io.outRD
 MemBarrier.io.inXcptInvalid   := ExBarrier.io.outXcptInvalid


 //Stage 5: Prepares the final results to be committed to the Register File
 writebackStage.io.inAluResult   := MemBarrier.io.outAluResult
 writebackStage.io.inRD          := MemBarrier.io.outRD
 writebackStage.io.inXcptInvalid := MemBarrier.io.outXcptInvalid
 

 //Feedback loop to ID
 decodeStage.io.wb_req_en   := writebackStage.io.regFileReq.wr_en
 decodeStage.io.wb_req_addr := writebackStage.io.regFileReq.addr
 decodeStage.io.wb_req_data := writebackStage.io.regFileReq.data


 // Last Barrier :synchronization for verification output
 WbBarrier.io.inCheckRes      := writebackStage.io.check_res
 WbBarrier.io.inXcptInvalid   := writebackStage.io.outXcptInvalid


 //Top level outputs :These connect to the PipelinedRV32I wrapper and the testbench
 io.check_res := WbBarrier.io.outCheckRes
 io.exception := WbBarrier.io.outXcptInvalid


}
