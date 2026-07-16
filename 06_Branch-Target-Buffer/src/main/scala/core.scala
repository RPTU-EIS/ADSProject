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

// ADS I Class Project
// Pipelined RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/15/2023 by Tobias Jauch (@tojauch)


class PipelinedRV32Icore (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    val check_res = Output(UInt(32.W))
    val exception = Output(Bool())
    val PCdebug        = Output(UInt(32.W))
    val InstrDdebug    = Output(UInt(32.W))
    val PCEdebug       = Output(UInt(32.W))
    val RS1Edebug      = Output(UInt(32.W))
    val RS2Edebug      = Output(UInt(32.W))
    val BranchEdebug   = Output(Bool())
    val JumpEdebug     = Output(Bool())
    val PCSrcEdebug    = Output(Bool())
    val PCTargetEdebug = Output(UInt(32.W))
    val WriteEnableWdebug = Output(Bool())
    val rdWdebug       = Output(UInt(5.W))

    val PCSrcE_debug   = Output(Bool())
  })

  val IFstage = Module(new IF(BinaryFile: String))
  val IFBarrier = Module(new IFBarrier)

  val IDstage = Module(new ID)
  val IDBarrier = Module(new IDBarrier)

  val EXstage = Module(new EXstage)
  val EXBarrier = Module(new EXBarrier)

  // val MEMstage = Module(new MEM)   // unused: no memory instructions
  val MEMBarrier = Module(new MEMBarrier)

  val WBstage = Module(new WBstage)
  val WBBarrier = Module(new WBBarrier)

  val ForwardingUnit = Module(new ForwardingUnit)

  val BTB = Module(new BTB)

  //IF STAGE
  IFstage.io.PCSrcE    := EXstage.io.PCSrcE
  IFstage.io.PCTargetE := EXstage.io.PCTargetE

  //IF BARRIER
  IFBarrier.io.InstrF   := IFstage.io.InstrF
  IFBarrier.io.PCF      := IFstage.io.PCF
  IFBarrier.io.PCPlus4F := IFstage.io.PCPlus4F
  IFBarrier.io.CLR      := EXstage.io.FlushE

  //ID STAGE
  IDstage.io.inst        := IFBarrier.io.InstrD
  IDstage.io.pcD         := IFBarrier.io.PCD
  IDstage.io.pcPlus4D    := IFBarrier.io.PCPlus4D
  IDstage.io.WriteEnableW:= WBstage.io.WriteEnableReq.w_en
  IDstage.io.rdW         := WBstage.io.WriteEnableReq.addr
  IDstage.io.ResultW     := WBstage.io.WriteEnableReq.data

  //ID BARRIER
  IDBarrier.io.uopD         := IDstage.io.uop
  IDBarrier.io.rdD          := IDstage.io.rdD
  IDBarrier.io.RD1D         := IDstage.io.RD1D
  IDBarrier.io.RD2D         := IDstage.io.RD2D
  IDBarrier.io.XcptInvalidD := IDstage.io.XcptInvalid
  IDBarrier.io.WriteEnableD    := IDstage.io.WriteEnableD
  IDBarrier.io.ALUSrcD      := IDstage.io.ALUSrcD
  IDBarrier.io.ImmExtD      := IDstage.io.ImmExtD
  IDBarrier.io.BranchD      := IDstage.io.BranchD
  IDBarrier.io.JumpD        := IDstage.io.JumpD
  IDBarrier.io.PCD          := IDstage.io.pcD_out
  IDBarrier.io.PCPlus4D     := IDstage.io.pcPlus4D_out
  IDBarrier.io.rs1D         := IFBarrier.io.InstrD(19,15)  // rs1
  IDBarrier.io.rs2D         := IFBarrier.io.InstrD(24,20)  // rs2
  IDBarrier.io.CLR          := EXstage.io.FlushE

  //EX STAGE
  EXstage.io.RD1E         := IDBarrier.io.RD1E
  EXstage.io.RD2E         := IDBarrier.io.RD2E
  EXstage.io.ImmExtE      := IDBarrier.io.ImmExtE
  EXstage.io.ALUSrcE      := IDBarrier.io.ALUSrcE
  EXstage.io.rdE          := IDBarrier.io.rdE
  EXstage.io.uopE         := IDBarrier.io.uopE
  EXstage.io.WriteEnableE    := IDBarrier.io.WriteEnableE
  EXstage.io.XcptInvalidE := IDBarrier.io.XcptInvalidE
  EXstage.io.PCE          := IDBarrier.io.PCE
  EXstage.io.PCPlus4E     := IDBarrier.io.PCPlus4E
  EXstage.io.BranchE      := IDBarrier.io.BranchE
  EXstage.io.JumpE        := IDBarrier.io.JumpE

  //EX BARRIER
  EXBarrier.io.ALUResultE   := EXstage.io.ALUResultE
  EXBarrier.io.rdE          := EXstage.io.rdOutE
  EXBarrier.io.XcptInvalidE := EXstage.io.exceptionE
  EXBarrier.io.WriteEnableE    := EXstage.io.WriteEnableOutE

  //MEM STAGE (empty: connect MEM barrier straight to EX barrier outputs)
  MEMBarrier.io.ALUResultM   := EXBarrier.io.ALUResultM
  MEMBarrier.io.rdM          := EXBarrier.io.rdM
  MEMBarrier.io.XcptInvalidM := EXBarrier.io.XcptInvalidM
  MEMBarrier.io.WriteEnableM    := EXBarrier.io.WriteEnableM

  //WB STAGE
  WBstage.io.ALUResultW := MEMBarrier.io.ALUResultW
  WBstage.io.rdW        := MEMBarrier.io.rdW
  WBstage.io.WriteEnableW  := MEMBarrier.io.WriteEnableW

  //WB BARRIER
  WBBarrier.io.ResultW      := WBstage.io.ResultW
  WBBarrier.io.XcptInvalidW := MEMBarrier.io.XcptInvalidW

  //FORWARDING UNIT
  ForwardingUnit.io.rs1_EX   := IDBarrier.io.Rs1E
  ForwardingUnit.io.rs2_EX   := IDBarrier.io.Rs2E

  ForwardingUnit.io.rd_MEM   := EXBarrier.io.rdM
  ForwardingUnit.io.wrEn_MEM := EXBarrier.io.WriteEnableM

  ForwardingUnit.io.rd_WB    := MEMBarrier.io.rdW
  ForwardingUnit.io.wrEn_WB  := MEMBarrier.io.WriteEnableW

  EXstage.io.ForwardAE  := ForwardingUnit.io.forwardA
  EXstage.io.ForwardBE  := ForwardingUnit.io.forwardB
  EXstage.io.ALUResultM := EXBarrier.io.ALUResultM   // value forwarded from MEM
  EXstage.io.ResultW    := WBstage.io.ResultW        // value forwarded from WB

  //TOP-LEVEL OUTPUTS
  io.check_res := WBBarrier.io.check_res
  io.exception := WBBarrier.io.exception

  //DEBUG OUTPUTS
  io.PCdebug        := IFstage.io.PCF
  io.InstrDdebug    := IFBarrier.io.InstrD
  io.PCEdebug       := IDBarrier.io.PCE
  io.RS1Edebug      := IDBarrier.io.RD1E
  io.RS2Edebug      := IDBarrier.io.RD2E
  io.BranchEdebug   := IDBarrier.io.BranchE
  io.JumpEdebug     := IDBarrier.io.JumpE
  io.PCSrcEdebug    := EXstage.io.PCSrcE
  io.PCTargetEdebug := EXstage.io.PCTargetE
  io.WriteEnableWdebug := MEMBarrier.io.WriteEnableW
  io.rdWdebug       := MEMBarrier.io.rdW

  io.PCSrcE_debug      := EXstage.io.PCSrcE

  //BTB WIRING
  BTB.io.PC             := IFstage.io.PCF          // lookup con el PC que se está fetcheando
  BTB.io.update         := EXstage.io.BTBUpdate
  BTB.io.updatePC       := EXstage.io.BTBUpdatePC
  BTB.io.updateTarget   := EXstage.io.BTBUpdateTarget
  BTB.io.mispredicted   := EXstage.io.BTBMispredicted

  IFstage.io.BTBTarget        := BTB.io.target
  IFstage.io.BTBPredictTaken  := BTB.io.predictTaken

  IFBarrier.io.PredictTakenF     := IFstage.io.PredictTakenF
  IDstage.io.PredictTakenF       := IFBarrier.io.PredictTakenD
  IDBarrier.io.PredictTakenD     := IDstage.io.PredictTakenD
  EXstage.io.PredictTakenE       := IDBarrier.io.PredictTakenE

  printf(" \n\n\nCORE PRINT\n")
  printf("----------------------------------------------\n")
  printf(" BTB UPDATE: %x", BTB.io.update)
  printf("\n----------------------------------------------\n")
}