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

  })
//ToDo: Add your implementation according to the specification above here 

  val IFstage = Module(new IF(BinaryFile: String))
  val IFbarrier = Module(new IFBarrier)

  val IDstage = Module(new ID)
  val IDbarrier = Module(new IDBarrier)

  val EXstage = Module(new EXstage)
 // val EXbarrier = Module(new )

  val MEMstage = Module(new MEM)
  val MEMbarrier = Module(new MEMbarrier)

  //val WBstage = Module(new )
  //val WBbarrier = Module(new )

  //IF STAGE & BARRIER WERKS
  IFstage.io.inst := IFbarrier.io.inInstr

  //ID STAGE
  IDstage.io.inst := IFbarrier.io.outInstr//DO NOT KNOW WHAT TO DO WITH OTHER INPUTS INSIDE IDSTAGE

  //ID BARRIER
  IDstage.io.uop := IDbarrier.io.inUOP
  IDstage.io.rd_out := IDbarrier.io.inRD
  IDstage.io.XcptInvalid := IDbarrier.io.inXcptInvalid
  IDstage.io.operandA := IDbarrier.io.inOperandA
  IDstage.io.operandB := IDbarrier.io.inOperandB

  //EX STAGE
  EXstage.io.operandA := IDbarrier.io.outOperandA
  EXstage.io.operandB := IDbarrier.io.outOperandB

}
