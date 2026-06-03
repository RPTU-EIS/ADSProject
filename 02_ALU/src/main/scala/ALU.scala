// ADS I Class Project
// Assignment 02: Arithmetic Logic Unit and UVM Testbench
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 09/21/2025 by Tharindu Samarakoon (gug75kex@rptu.de)
// File updated on 10/29/2025 by Tobias Jauch (tobias.jauch@rptu.de)

package Assignment02

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

//ToDo: define AluOp Enum

object ALUOp extends ChiselEnum {
  val ADD = Value(0.U)
  val SUB = Value(1.U)
  val AND = Value(2.U)
}

class ALU extends Module {
  
  val io = IO(new Bundle {
    val operandA  = Input(UInt(32.W))
    val operandB  = Input(UInt(32.W))
    val operation = Input(ALUOp())
    val aluResult = Output(UInt(32.W))
  })

  io.aluResult := 0.U

  when(io.operation === ALUOp.ADD) {
    io.aluResult := io.operandA + io.operandB
  }.
  elsewhen (io.operation === ALUOp.SUB){
    io.aluResult := io.operandA - io.operandB
  }

  //ToDo: implement ALU functionality according to the task specification

}