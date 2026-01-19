// ADS I Class Project
// Pipelined RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 05/10/2023 by Tobias Jauch (@tojauch)

/*
This file contains the top-level module for the Pipelined RISC-V 32I core and acts as the interface between the core and external testbenches.
*/

package PipelinedRV32I

import chisel3._
import chisel3.util._

import core_tile._

class PipelinedRV32I (BinaryFile: String) extends Module {

val io = IO(new Bundle {
  val result    = Output(UInt(32.W)) 
  val exception = Output(Bool())
 })
  
  val core = Module(new PipelinedRV32Icore(BinaryFile))

  io.result    := core.io.check_res
  io.exception := core.io.exception

}
