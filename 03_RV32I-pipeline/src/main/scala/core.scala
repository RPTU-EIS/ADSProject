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

import chisel3._                                      // Import the basic Chisel hardware types
import chisel3.util._                                 // Import common Chisel utilities
import chisel3.util.experimental.loadMemoryFromFile   // Import memory file loader
import Assignment02.{ALU, ALUOp}                      // Import ALU definitions from Assignment 02
import uopc._                                         // Import internal operation names


class PipelinedRV32Icore (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    val check_res = Output(UInt(32.W))  // Send the final result to the outside tester
    val exception = Output(Bool())      // Send the final exception flag to the outside tester
  })

  // Create all pipeline stages and pipeline registers
  val fetch      = Module(new IF(BinaryFile))  // Create instruction fetch stage
  val ifBarrier  = Module(new IFBarrier())     // Create register between IF and ID
  val decode     = Module(new ID())            // Create instruction decode stage
  val idBarrier  = Module(new IDBarrier())     // Create register between ID and EX
  val execute    = Module(new EX())            // Create execute stage
  val exBarrier  = Module(new EXBarrier())     // Create register between EX and MEM
  val memBarrier = Module(new MEMBarrier())    // Create register between MEM and WB
  val writeback  = Module(new WB())            // Create writeback stage
  val wbBarrier  = Module(new WBBarrier())     // Create final register for test output
  val regs       = Module(new regFile())       // Create the register file

  ifBarrier.io.inInstr := fetch.io.instr  // Move fetched instruction into the IF barrier

  decode.io.instr := ifBarrier.io.outInstr        // Send instruction to decode stage
  regs.io.req_1 := decode.io.regFileReq_A         // Connect first register read request
  regs.io.req_2 := decode.io.regFileReq_B         // Connect second register read request
  decode.io.regFileResp_A := regs.io.resp_1       // Return first register read data
  decode.io.regFileResp_B := regs.io.resp_2       // Return second register read data

  idBarrier.io.inUOP         := decode.io.uop          // Store decoded operation
  idBarrier.io.inRD          := decode.io.rd           // Store destination register
  idBarrier.io.inOperandA    := decode.io.operandA     // Store first operand
  idBarrier.io.inOperandB    := decode.io.operandB     // Store second operand
  idBarrier.io.inXcptInvalid := decode.io.XcptInvalid  // Store invalid flag

  execute.io.uop         := idBarrier.io.outUOP          // Send operation to execute stage
  execute.io.rd          := idBarrier.io.outRD           // Send destination register to execute stage
  execute.io.operandA    := idBarrier.io.outOperandA     // Send first operand to execute stage
  execute.io.operandB    := idBarrier.io.outOperandB     // Send second operand to execute stage
  execute.io.XcptInvalid := idBarrier.io.outXcptInvalid  // Send invalid flag to execute stage

  exBarrier.io.inAluResult   := execute.io.aluResult  // Store ALU result
  exBarrier.io.inRD          := execute.io.outRD      // Store destination register
  exBarrier.io.inXcptInvalid := execute.io.exception  // Store exception flag

  memBarrier.io.inAluResult := exBarrier.io.outAluResult   // Pass ALU result through memory stage
  memBarrier.io.inRD        := exBarrier.io.outRD          // Pass destination register through memory stage
  memBarrier.io.inException := exBarrier.io.outXcptInvalid // Pass exception flag through memory stage

  writeback.io.aluResult := memBarrier.io.outAluResult  // Send result to writeback stage
  writeback.io.rd        := memBarrier.io.outRD         // Send destination register to writeback stage
  writeback.io.exception := memBarrier.io.outException  // Send exception flag to writeback stage

  regs.io.req_3 := writeback.io.regFileReq  // Connect writeback stage to register file write port

  wbBarrier.io.inCheckRes    := writeback.io.check_res       // Store result for external checking
  wbBarrier.io.inXcptInvalid := memBarrier.io.outException   // Store exception for external checking

  io.check_res := wbBarrier.io.outCheckRes       // Output final observed result
  io.exception := wbBarrier.io.outXcptInvalid    // Output final observed exception

}
