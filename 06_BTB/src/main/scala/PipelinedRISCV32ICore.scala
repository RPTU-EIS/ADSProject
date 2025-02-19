// ADS I Class Project
// Pipelined RISC-V Core with Hazard Detetcion and Resolution
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 05/21/2024 by Andro Mazmishvili (@Andrew8846)

package PipelinedRV32I

import chisel3._
import chisel3.util._

import core_tile._

class PipelinedRV32I (BinaryFile: String) extends Module {

  val io     = IO(new Bundle {
    val result = Output(UInt(32.W)) 
  })
  
  val core   = Module(new PipelinedRV32Icore(BinaryFile))

  io.result  := core.io.check_res

}

