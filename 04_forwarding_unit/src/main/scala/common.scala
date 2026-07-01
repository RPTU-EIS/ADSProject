// ADS I Class Project
// Pipelined RISC-V Core - Common Definitions
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
Global Definitions and Data Types

Enumerations:
    uopc: ChiselEnum defining micro-operation codes for all supported RV32I instructions:
        R-type instructions 
        I-type instructions
        NOP (no operation, default case)

This enum is used throughout the pipeline:
    Decode stage assigns uop based on instruction fields
    Execute stage maps uop to ALU operations
*/

package core_tile

import chisel3._
import chisel3.experimental.ChiselEnum

// -----------------------------------------
// Global Definitions and Data Types
// -----------------------------------------

object uopc extends ChiselEnum {
  val isADD, isSUB, isAND, isOR, isXOR, isSLL, isSRL, isSRA, isSLT, isSLTU,
      isADDI, isANDI, isORI, isXORI, isSLLI, isSRLI, isSRAI, isSLTI, isSLTIU,
      isNOP = Value
}

// Register file request/response types
class regFileReadReq extends Bundle {
  val addr = UInt(5.W)
}

class regFileReadResp extends Bundle {
  val data = UInt(32.W)
}

class regFileWriteReq extends Bundle {
  val addr  = UInt(5.W)
  val data  = UInt(32.W)
  val wr_en = Bool()
}