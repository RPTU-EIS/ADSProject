package core_tile

import chisel3._
import chisel3.experimental.ChiselEnum

object uopc extends ChiselEnum {
  val ADD   = Value
  val SUB   = Value
  val SLL   = Value
  val SLT   = Value
  val SLTU  = Value
  val XOR   = Value
  val SRL   = Value
  val SRA   = Value
  val OR    = Value
  val AND   = Value
  val ADDI  = Value
  val SLTI  = Value
  val SLTIU = Value
  val XORI  = Value
  val ORI   = Value
  val ANDI  = Value
  val SLLI  = Value
  val SRLI  = Value
  val SRAI  = Value
  val NOP   = Value
}

object Opcodes {
  val R_TYPE = "b0110011".U(7.W)
  val I_TYPE = "b0010011".U(7.W)
}

object Funct3 {
  val ADD_SUB = "b000".U(3.W)
  val SLL     = "b001".U(3.W)
  val SLT     = "b010".U(3.W)
  val SLTU    = "b011".U(3.W)
  val XOR     = "b100".U(3.W)
  val SRL_SRA = "b101".U(3.W)
  val OR      = "b110".U(3.W)
  val AND     = "b111".U(3.W)
}

object Funct7 {
  val NORMAL  = "b0000000".U(7.W)
  val ALT     = "b0100000".U(7.W)
}
