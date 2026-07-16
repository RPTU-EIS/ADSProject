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
    val check_res = Output(UInt(32.W))
    val exception = Output(Bool())
  })

  val IFstage   = Module(new IF(BinaryFile: String))
  val IFBarrier = Module(new IFBarrier)

  val IDstage   = Module(new ID)
  val IDBarrier = Module(new IDBarrier)

  val EXstage   = Module(new EXstage)
  val EXBarrier = Module(new EXBarrier)

  // val MEMstage = Module(new MEM)   // unused: no memory instructions
  val MEMBarrier = Module(new MEMBarrier)

  val WBstage   = Module(new WBstage)
  val WBBarrier = Module(new WBBarrier)

  //IF STAGE & BARRIER
  IFBarrier.io.inInstr    := IFstage.io.inst

  //ID STAGE
  IDstage.io.inst         := IFBarrier.io.outInstr
  IDstage.io.w_en         := WBstage.io.regFileReq.w_en
  IDstage.io.rd_in        := WBstage.io.regFileReq.addr
  IDstage.io.write_data   := WBstage.io.regFileReq.data

  //ID BARRIER
  IDBarrier.io.inUOP          := IDstage.io.uop
  IDBarrier.io.inRD           := IDstage.io.rd_out
  IDBarrier.io.inXcptInvalid  := IDstage.io.XcptInvalid
  IDBarrier.io.inOperandA     := IDstage.io.operandA
  IDBarrier.io.inOperandB     := IDstage.io.operandB
  IDBarrier.io.inWrten        := IDstage.io.wrten
  IDBarrier.io.inALUsrc       := IDstage.io.ALUsrc
  IDBarrier.io.inImmExtnd     := IDstage.io.immExtnd

  //EX STAGE
  EXstage.io.uop          := IDBarrier.io.outUOP
  EXstage.io.rd_in        := IDBarrier.io.outRD
  EXstage.io.operandA     := IDBarrier.io.outOperandA
  EXstage.io.operandB     := IDBarrier.io.outOperandB
  EXstage.io.XcptInvalid  := IDBarrier.io.outXcptInvalid
  EXstage.io.wrten_in     := IDBarrier.io.outWrten
  EXstage.io.ALUsrc       := IDBarrier.io.outALUsrc
  EXstage.io.immExtnd     := IDBarrier.io.outImmExtnd

  //EX BARRIER
  EXBarrier.io.inAluResult    := EXstage.io.aluResult
  EXBarrier.io.inRD           := EXstage.io.rd
  EXBarrier.io.inXcptInvalid  := EXstage.io.exception
  EXBarrier.io.inWrten        := EXstage.io.wrten

  //MEM STAGE (empty: connect MEM barrier straight to EX barrier outputs)
  MEMBarrier.io.inALUResult := EXBarrier.io.outAluResult
  MEMBarrier.io.inRD        := EXBarrier.io.outRD
  MEMBarrier.io.inException := EXBarrier.io.outXcptInvalid
  MEMBarrier.io.inWrten     := EXBarrier.io.outWrten

  //WB STAGE
  WBstage.io.aluResult := MEMBarrier.io.outALUResult
  WBstage.io.rd        := MEMBarrier.io.outRD
  WBstage.io.wrten     := MEMBarrier.io.outWrten

  //WB BARRIER
  WBBarrier.io.inCheckRes     := WBstage.io.aluResult
  WBBarrier.io.inXcptInvalid  := MEMBarrier.io.outException

  io.check_res := WBBarrier.io.outCheckRes
  io.exception := WBBarrier.io.outXcptInvalid
}