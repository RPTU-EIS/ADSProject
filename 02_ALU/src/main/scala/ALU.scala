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

object ALUOp extends ChiselEnum {
  val ADD, SUB, AND, OR, XOR, SLL, SRL, SRA, SLT, SLTU, PASSB = Value
}

class ALU extends Module {

  val io = IO(new Bundle {
    val opA = Input(UInt(32.W))
    val opB = Input(UInt(32.W))
    val op = Input(ALUOp())
    val result = Output(UInt(32.W))
  })

  io.result := 0.U

  switch(io.op) {
    is(ALUOp.ADD) { io.result := io.opA + io.opB }
    is(ALUOp.SUB) { io.result := io.opA - io.opB }
    is(ALUOp.AND) { io.result := io.opA & io.opB }
    is(ALUOp.OR) { io.result := io.opA | io.opB }
    is(ALUOp.XOR) { io.result := io.opA ^ io.opB }
    is(ALUOp.SLL) { io.result := io.opA << io.opB(4,0) }
    is(ALUOp.SRL) { io.result := io.opA >> io.opB(4,0) }
    is(ALUOp.SRA) { io.result := (io.opA.asSInt >> io.opB(4,0)).asUInt }
    is(ALUOp.SLT) { io.result := (io.opA.asSInt < io.opB.asSInt).asUInt }
    is(ALUOp.SLTU) { io.result := (io.opA < io.opB).asUInt }
    is(ALUOp.PASSB) { io.result := io.opB}
  }

}