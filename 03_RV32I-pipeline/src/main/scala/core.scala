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
        val check_res    = Output(UInt(32.W))  // final result for verification
        val isInvalid    = Output(Bool())      // exception flag for verification

  })

//ToDo: Add your implementation according to the specification above here 

// --- Instantiate all pipeline stages and barriers ---
  val if_stage    = Module(new IF(BinaryFile))
  val if_barrier  = Module(new IFBarrier)
  val id_stage    = Module(new ID)
  val id_barrier  = Module(new IDBarrier)
  val ex_stage    = Module(new EX)
  val ex_barrier  = Module(new EXBarrier)
  val mem_stage   = Module(new MEM)
  val mem_barrier = Module(new MEMBarrier)
  val wb_stage    = Module(new WB)
  val wb_barrier  = Module(new WBBarrier)

  // -------------------------------------------------------
  // IF → IFBarrier
  // -------------------------------------------------------
  if_barrier.io.inInstr   := if_stage.io.instr

  // -------------------------------------------------------
  // IFBarrier → ID
  // -------------------------------------------------------
  id_stage.io.instr       := if_barrier.io.outInstr

  // -------------------------------------------------------
  // ID → IDBarrier
  // -------------------------------------------------------
  id_barrier.io.inUOP         := id_stage.io.uop
  id_barrier.io.inRD          := id_stage.io.rd
  id_barrier.io.inOperandA    := id_stage.io.operandA
  id_barrier.io.inOperandB    := id_stage.io.operandB
  id_barrier.io.inXcptInvalid := id_stage.io.xcptInvalid

  // -------------------------------------------------------
  // IDBarrier → EX
  // -------------------------------------------------------
  ex_stage.io.uop         := id_barrier.io.outUOP
  ex_stage.io.inRD          := id_barrier.io.outRD
  ex_stage.io.operandA    := id_barrier.io.outOperandA
  ex_stage.io.operandB    := id_barrier.io.outOperandB
  ex_stage.io.inXcptInvalid := id_barrier.io.outXcptInvalid

  // -------------------------------------------------------
  // EX → EXBarrier
  // -------------------------------------------------------
  ex_barrier.io.inAluResult   := ex_stage.io.aluResult
  ex_barrier.io.inRD          := ex_stage.io.outRD
  ex_barrier.io.inXcptInvalid := ex_stage.io.outXcptInvalid

  // -------------------------------------------------------
  // EXBarrier → MEM
  // -------------------------------------------------------
  mem_stage.io.aluResult   := ex_barrier.io.outAluResult
  mem_stage.io.rd          := ex_barrier.io.outRD
  mem_stage.io.xcptInvalid := ex_barrier.io.outXcptInvalid

  // -------------------------------------------------------
  // MEM → MEMBarrier
  // -------------------------------------------------------
  mem_barrier.io.inAluResult := mem_stage.io.aluResultOut
  mem_barrier.io.inRD        := mem_stage.io.rdOut
  mem_barrier.io.inException := mem_stage.io.outXcptInvalid

  // -------------------------------------------------------
  // MEMBarrier → WB
  // -------------------------------------------------------
  wb_stage.io.aluResult := mem_barrier.io.outAluResult
  wb_stage.io.rd        := mem_barrier.io.outRD
  wb_stage.io.exception := mem_barrier.io.outException

  // -------------------------------------------------------
  // WB → WBBarrier
  // -------------------------------------------------------
  wb_barrier.io.inCheckRes    := wb_stage.io.check_res
  wb_barrier.io.inXcptInvalid := mem_barrier.io.outException

  // -------------------------------------------------------
  // WB → ID (register file writeback)
  // -------------------------------------------------------
  id_stage.io.req_3 := wb_stage.io.regFileReq

  // -------------------------------------------------------
  // Top-level outputs
  // -------------------------------------------------------
  io.check_res := wb_barrier.io.outCheckRes
  io.isInvalid := wb_barrier.io.outXcptInvalid

}
