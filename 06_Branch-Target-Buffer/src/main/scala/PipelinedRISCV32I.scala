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

  val core = Module(new PipelinedRV32Icore(BinaryFile))

  io.result         := core.io.check_res
  io.exception      := core.io.exception
  io.PCdebug        := core.io.PCdebug
  io.InstrDdebug    := core.io.InstrDdebug
  io.PCEdebug       := core.io.PCEdebug
  io.RS1Edebug      := core.io.RS1Edebug
  io.RS2Edebug      := core.io.RS2Edebug
  io.BranchEdebug   := core.io.BranchEdebug
  io.JumpEdebug     := core.io.JumpEdebug
  io.PCSrcEdebug    := core.io.PCSrcEdebug
  io.PCTargetEdebug := core.io.PCTargetEdebug
  io.WriteEnableWdebug := core.io.WriteEnableWdebug
  io.rdWdebug       := core.io.rdWdebug
  io.PCSrcE_debug   := core.io.PCSrcE_debug
}