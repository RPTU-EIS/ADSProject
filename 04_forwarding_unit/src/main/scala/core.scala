package core_tile

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile
import Assignment02.{ALU, ALUOp}
import uopc._

// -----------------------------------------
// Top Module with Forwarding Unit
// -----------------------------------------

//Start code

//We added the Forwarding Unit to detect and resolve data hazards (RAW)
//The Forwarding Unit reads rs1 and rs2 from the ID barrier
//and compares them with rd from the EX barrier (MEM stage) and MEM barrier (WB stage)
//Then it sends control signals (forwardA, forwardB) to the muxes in the EX stage
//We also pass memData and wbData to the EX stage so the muxes can use them

class PipelinedRV32Icore (BinaryFile: String) extends Module {

  //We define the top-level outputs of the processor

  val io = IO(new Bundle {
    val check_res = Output(UInt(32.W))       // final result for testbench
    val isInvalid = Output(Bool())           // exception flag for testbench
  })


  //We create one instance of each pipeline stage, barrier, and the Forwarding Unit

  val if_stage    = Module(new IF(BinaryFile))   // instruction fetch
  val if_barrier  = Module(new IFBarrier)        // IF to ID barrier
  val id_stage    = Module(new ID)               // instruction decode
  val id_barrier  = Module(new IDBarrier)        // ID to EX barrier
  val ex_stage    = Module(new EX)               // execute (ALU + muxes)
  val ex_barrier  = Module(new EXBarrier)        // EX to MEM barrier
  val mem_stage   = Module(new MEM)              // memory stage (not used)
  val mem_barrier = Module(new MEMBarrier)       // MEM to WB barrier
  val wb_stage    = Module(new WB)               // write back
  val wb_barrier  = Module(new WBBarrier)        // final output barrier

  //New module, the Forwarding Unit detects RAW hazards and controls the EX muxes
  val fwd_unit    = Module(new ForwardingUnit)   // forwarding unit


  //We connect IF stage to IF barrier

  if_barrier.io.inInstr := if_stage.io.instr


  //We connect IF barrier to ID stage

  id_stage.io.instr := if_barrier.io.outInstr


  //We connect ID stage to ID barrier

  id_barrier.io.inUOP         := id_stage.io.uop
  id_barrier.io.inRD          := id_stage.io.rd
  id_barrier.io.inOperandA    := id_stage.io.operandA
  id_barrier.io.inOperandB    := id_stage.io.operandB
  id_barrier.io.inXcptInvalid := id_stage.io.xcptInvalid

  //We pass rs1 and rs2 to the ID barrier so the Forwarding Unit can use them
  id_barrier.io.inRS1         := id_stage.io.rs1Out
  id_barrier.io.inRS2         := id_stage.io.rs2Out


  //We connect ID barrier to EX stage

  ex_stage.io.uop           := id_barrier.io.outUOP
  ex_stage.io.inRD          := id_barrier.io.outRD
  ex_stage.io.operandA      := id_barrier.io.outOperandA
  ex_stage.io.operandB      := id_barrier.io.outOperandB
  ex_stage.io.inXcptInvalid := id_barrier.io.outXcptInvalid

  //We pass rs1 and rs2 to the EX stage so the Forwarding Unit can read them
  ex_stage.io.rs1           := id_barrier.io.outRS1
  ex_stage.io.rs2           := id_barrier.io.outRS2


  //We connect the Forwarding Unit
  //It reads rs1 and rs2 from the ID barrier and rd from EX and MEM barriers

  fwd_unit.io.rs1_EX   := id_barrier.io.outRS1            // source register 1 in EX
  fwd_unit.io.rs2_EX   := id_barrier.io.outRS2            // source register 2 in EX
  fwd_unit.io.rd_MEM   := ex_barrier.io.outRD             // destination register in MEM
  fwd_unit.io.wrEn_MEM := ex_barrier.io.outRegWrite       // write enable in MEM
  fwd_unit.io.rd_WB    := mem_barrier.io.outRD            // destination register in WB
  fwd_unit.io.wrEn_WB  := mem_barrier.io.outRegWrite      // write enable in WB


  //We send the Forwarding Unit control signals to the EX stage muxes

  ex_stage.io.forwardA := fwd_unit.io.forwardA            // mux control for operand A
  ex_stage.io.forwardB := fwd_unit.io.forwardB            // mux control for operand B


  //We send the forwarded data values to the EX stage muxes

  ex_stage.io.memData  := ex_barrier.io.outAluResult      // value from MEM barrier
  ex_stage.io.wbData   := mem_barrier.io.outAluResult     // value from WB barrier


  //We connect EX stage to EX barrier

  ex_barrier.io.inAluResult   := ex_stage.io.aluResult
  ex_barrier.io.inRD          := ex_stage.io.outRD
  ex_barrier.io.inXcptInvalid := ex_stage.io.outXcptInvalid

  //We pass the write enable flag to the EX barrier for the Forwarding Unit
  ex_barrier.io.inRegWrite    := ex_stage.io.outRegWrite


  //We connect EX barrier to MEM stage

  mem_stage.io.aluResult   := ex_barrier.io.outAluResult
  mem_stage.io.rd          := ex_barrier.io.outRD
  mem_stage.io.xcptInvalid := ex_barrier.io.outXcptInvalid

  //We pass the write enable flag to the MEM stage so it can forward it
  mem_stage.io.inRegWrite  := ex_barrier.io.outRegWrite


  //We connect MEM stage to MEM barrier

  mem_barrier.io.inAluResult := mem_stage.io.aluResultOut
  mem_barrier.io.inRD        := mem_stage.io.rdOut
  mem_barrier.io.inException := mem_stage.io.outXcptInvalid

  //We pass the write enable flag to the MEM barrier for the Forwarding Unit
  mem_barrier.io.inRegWrite  := mem_stage.io.outRegWrite


  //We connect MEM barrier to WB stage

  wb_stage.io.aluResult := mem_barrier.io.outAluResult
  wb_stage.io.rd        := mem_barrier.io.outRD
  wb_stage.io.exception := mem_barrier.io.outException


  //We connect WB stage to WB barrier

  wb_barrier.io.inCheckRes    := wb_stage.io.check_res
  wb_barrier.io.inXcptInvalid := mem_barrier.io.outException


  //We connect WB stage back to ID stage for the register file writeback

  id_stage.io.req_3 := wb_stage.io.regFileReq


  //We connect the top-level outputs to the testbench

  io.check_res := wb_barrier.io.outCheckRes
  io.isInvalid := wb_barrier.io.outXcptInvalid

}