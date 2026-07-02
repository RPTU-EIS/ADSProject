// ADS I Class Project
// Pipelined RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/15/2023 by Tobias Jauch (@tojauch)

package core_tile

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile
import Assignment02.{ALU, ALUOp}
import uopc._

// -----------------------------------------
// Top Module with Forwarding Unit + Branch/Jump support
// -----------------------------------------

//Start code

//We added control hazard handling to the top module
//Branch and jump instructions are evaluated in EX, and if taken,
//the EX stage sends a flush signal and a new PC target back to IF
//The IF stage updates the PC, and the IF and ID barriers are flushed
//to remove the wrong instructions that were already fetched
class PipelinedRV32Icore (BinaryFile: String) extends Module {

  val io = IO(new Bundle {
    val check_res = Output(UInt(32.W))
    val isInvalid = Output(Bool())
  })


  //We create one instance of each pipeline stage and barrier
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

  //We create the Forwarding Unit (from Assignment 04)
  val fwd_unit    = Module(new ForwardingUnit)


  //IF stage receives the branch target and the flush signal from EX
  //If the branch is taken, the PC jumps to branchTarget
  //Otherwise, PC keeps going to PC+4 (static prediction: not taken)
  if_stage.io.branchTaken  := ex_stage.io.branchTaken
  if_stage.io.branchTarget := ex_stage.io.branchTarget


  //IF to IF barrier (with flush support)
  if_barrier.io.inInstr := if_stage.io.instr
  if_barrier.io.flush   := ex_stage.io.branchTaken


  //IF barrier to ID stage
  id_stage.io.instr := if_barrier.io.outInstr


  //We pass the PC through the pipeline so EX can compute branch targets
  if_barrier.io.inPC := if_stage.io.pcOut
  id_stage.io.pcIn   := if_barrier.io.outPC


  //ID to ID barrier (with flush support)
  id_barrier.io.inUOP         := id_stage.io.uop
  id_barrier.io.inRD          := id_stage.io.rd
  id_barrier.io.inOperandA    := id_stage.io.operandA
  id_barrier.io.inOperandB    := id_stage.io.operandB
  id_barrier.io.inXcptInvalid := id_stage.io.xcptInvalid
  id_barrier.io.inRS1         := id_stage.io.rs1Out
  id_barrier.io.inRS2         := id_stage.io.rs2Out
  id_barrier.io.inPC          := id_stage.io.pcOut
  id_barrier.io.inImm         := id_stage.io.immOut
  id_barrier.io.flush         := ex_stage.io.branchTaken


  //ID barrier to EX stage
  ex_stage.io.uop           := id_barrier.io.outUOP
  ex_stage.io.inRD          := id_barrier.io.outRD
  ex_stage.io.operandA      := id_barrier.io.outOperandA
  ex_stage.io.operandB      := id_barrier.io.outOperandB
  ex_stage.io.inXcptInvalid := id_barrier.io.outXcptInvalid
  ex_stage.io.rs1           := id_barrier.io.outRS1
  ex_stage.io.rs2           := id_barrier.io.outRS2
  ex_stage.io.pcIn          := id_barrier.io.outPC
  ex_stage.io.imm           := id_barrier.io.outImm


  //Forwarding Unit connections
  fwd_unit.io.rs1_EX   := id_barrier.io.outRS1
  fwd_unit.io.rs2_EX   := id_barrier.io.outRS2
  fwd_unit.io.rd_MEM   := ex_barrier.io.outRD
  fwd_unit.io.wrEn_MEM := ex_barrier.io.outRegWrite
  fwd_unit.io.rd_WB    := mem_barrier.io.outRD
  fwd_unit.io.wrEn_WB  := mem_barrier.io.outRegWrite

  ex_stage.io.forwardA := fwd_unit.io.forwardA
  ex_stage.io.forwardB := fwd_unit.io.forwardB
  ex_stage.io.memData  := ex_barrier.io.outAluResult
  ex_stage.io.wbData   := mem_barrier.io.outAluResult


  //EX to EX barrier
  ex_barrier.io.inAluResult   := ex_stage.io.aluResult
  ex_barrier.io.inRD          := ex_stage.io.outRD
  ex_barrier.io.inXcptInvalid := ex_stage.io.outXcptInvalid
  ex_barrier.io.inRegWrite    := ex_stage.io.outRegWrite


  //EX barrier to MEM stage
  mem_stage.io.aluResult   := ex_barrier.io.outAluResult
  mem_stage.io.rd          := ex_barrier.io.outRD
  mem_stage.io.xcptInvalid := ex_barrier.io.outXcptInvalid
  mem_stage.io.inRegWrite  := ex_barrier.io.outRegWrite


  //MEM to MEM barrier
  mem_barrier.io.inAluResult := mem_stage.io.aluResultOut
  mem_barrier.io.inRD        := mem_stage.io.rdOut
  mem_barrier.io.inException := mem_stage.io.outXcptInvalid
  mem_barrier.io.inRegWrite  := mem_stage.io.outRegWrite


  //MEM barrier to WB stage
  wb_stage.io.aluResult := mem_barrier.io.outAluResult
  wb_stage.io.rd        := mem_barrier.io.outRD
  wb_stage.io.exception := mem_barrier.io.outException


  //WB to WB barrier
  wb_barrier.io.inCheckRes    := wb_stage.io.check_res
  wb_barrier.io.inXcptInvalid := mem_barrier.io.outException


  //WB to ID (register file writeback)
  id_stage.io.req_3 := wb_stage.io.regFileReq


  //Top-level outputs
  io.check_res := wb_barrier.io.outCheckRes
  io.isInvalid := wb_barrier.io.outXcptInvalid

}