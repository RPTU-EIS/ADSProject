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
    val check_res = Output(UInt(32.W)) // Output for verification and debugging
    val exception = Output(Bool()) // Exception flag for invalid instructions
  })

//ToDo: Add your implementation according to the specification above here 
  val ifStage = Module(new IF(BinaryFile))
  val ifBarrier = Module(new IFBarrier())

  val idStage = Module(new ID())
  val idBarrier = Module(new IDBarrier())
  
  val regFile = Module(new regFile())

  val exStage = Module(new EX())
  val exBarrier = Module(new EXBarrier())

  val wbStage = Module(new WB())
  val wbBarrier = Module(new WBBarrier())

  val memStage = Module(new MEM())
  val memBarrier = Module(new MEMBarrier())

  // Connect IF stage to IF barrier
  ifBarrier.io.inInstr := ifStage.io.instr

  // Connect IF barrier to ID stage
  idStage.io.inst := ifBarrier.io.outInstr

  // Connect ID stage to ID barrier
  idBarrier.io.inUOP := idStage.io.uop
  idBarrier.io.inRD := idStage.io.rd_idx
  idBarrier.io.inOperandA := idStage.io.operandA
  idBarrier.io.inOperandB := idStage.io.operandB
  idBarrier.io.inXcptInvalid := idStage.io.XcptInvalid

  // Connect ID barrier to EX stage
  exStage.io.uop := idBarrier.io.outUOP
  exStage.io.rd := idBarrier.io.outRD
  exStage.io.operandA := idBarrier.io.outOperandA
  exStage.io.operandB := idBarrier.io.outOperandB
  exStage.io.xcptInvalid := idBarrier.io.outXcptInvalid

  // Connect EX stage to EX barrier
  exBarrier.io.inAluResult := exStage.io.aluResult
  exBarrier.io.inRD := exStage.io.outRD
  exBarrier.io.inXcptInvalid := exStage.io.xcptInvalid

  // Connect EX barrier to MEM Barrier 
  memBarrier.io.inAluResult := exBarrier.io.outAluResult
  memBarrier.io.inRD := exBarrier.io.outRD
  memBarrier.io.inXcptInvalid := exBarrier.io.outXcptInvalid

  // Connect MEM barrier to WB Stage
  wbStage.io.aluResult := memBarrier.io.outAluResult
  wbStage.io.rd := memBarrier.io.outRD

  // Connect WB stage & MEM barrier to WB barrier
  wbBarrier.io.inCheckRes := wbStage.io.check_res
  wbBarrier.io.inXcptInvalid := memBarrier.io.outXcptInvalid

  // Connect WB barrier to output
  io.check_res := wbBarrier.io.outCheckRes
  io.exception := wbBarrier.io.outXcptInvalid
}
