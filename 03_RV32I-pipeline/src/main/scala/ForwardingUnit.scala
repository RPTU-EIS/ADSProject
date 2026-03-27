// ADS I Class Project
// Pipelined RISC-V Core - Forwarding Unit
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created for Assignment 04

/*
Forwarding Unit: Detects and resolves RAW data hazards

The forwarding unit compares the source registers (rs1, rs2) of the
instruction currently in the EX stage with the destination registers (rd)
of instructions in the MEM and WB stages.

Forwarding paths:
    forwardA/forwardB = 0 (00): No hazard — use value from ID/EX barrier
    forwardA/forwardB = 1 (01): MEM hazard — forward from MEM/WB barrier (WB stage)
    forwardA/forwardB = 2 (10): EX hazard  — forward from EX/MEM barrier (MEM stage)

Priority: EX hazard (MEM stage) takes precedence over MEM hazard (WB stage),
          because the MEM-stage instruction is more recent.

Conditions:
    - rd must not be x0 (writes to x0 are discarded)
    - rd must match rs1 or rs2 of the EX-stage instruction
    - MEM hazard only applies if EX hazard does not already forward the same register
*/

package core_tile

import chisel3._
import chisel3.util._

class ForwardingUnit extends Module {
  val io = IO(new Bundle {
    // Source registers from ID/EX barrier (instruction in EX stage)
    val idEx_rs1 = Input(UInt(5.W))
    val idEx_rs2 = Input(UInt(5.W))

    // Destination register from EX/MEM barrier (instruction in MEM stage)
    val exMem_rd = Input(UInt(5.W))

    // Destination register from MEM/WB barrier (instruction in WB stage)
    val memWb_rd = Input(UInt(5.W))

    // Forwarding select signals
    val forwardA = Output(UInt(2.W))  // Mux select for operand A
    val forwardB = Output(UInt(2.W))  // Mux select for operand B
  })

  // =========================================================================
  // Default: no forwarding
  // =========================================================================
  io.forwardA := 0.U
  io.forwardB := 0.U

  // =========================================================================
  // MEM hazard (forward from WB stage — 2 instructions ahead)
  // Check first so EX hazard can override with higher priority
  // =========================================================================
  when(io.memWb_rd =/= 0.U && (io.memWb_rd === io.idEx_rs1)) {
    io.forwardA := 1.U
  }
  when(io.memWb_rd =/= 0.U && (io.memWb_rd === io.idEx_rs2)) {
    io.forwardB := 1.U
  }

  // =========================================================================
  // EX hazard (forward from MEM stage — 1 instruction ahead)
  // Higher priority: overwrites MEM hazard if both match
  // This correctly handles the "double data hazard" case where both
  // MEM and WB stage write to the same register
  // =========================================================================
  when(io.exMem_rd =/= 0.U && (io.exMem_rd === io.idEx_rs1)) {
    io.forwardA := 2.U
  }
  when(io.exMem_rd =/= 0.U && (io.exMem_rd === io.idEx_rs2)) {
    io.forwardB := 2.U
  }
}
