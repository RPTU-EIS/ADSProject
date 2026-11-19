package divider

import chisel3._
import chisel3.util._
import Assignment02.{ALU, ALUOp}

class UIntDivider extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(16.W))
    val b = Input(UInt(16.W))
    val rst = Input(Bool())
    val q = Output(UInt(16.W))
    val r = Output(UInt(16.W))
    val valid = Output(Bool())
  })

  val controller = Module(new Controller)
  controller.io.a := io.a
  controller.io.b := io.b
  controller.io.rst := io.rst
  io.q := controller.io.q
  io.r := controller.io.r
  io.valid := controller.io.valid
}

class Controller extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(16.W))
    val b = Input(UInt(16.W))
    val rst = Input(Bool())
    val q = Output(UInt(16.W))
    val r = Output(UInt(16.W))
    val valid = Output(Bool())
  })

  val alu = Module(new ALU)
  val divisorRegister = Module(new DivisorRegister)
  val quotientRegister = Module(new QuotientRegister)
  val remainderRegister = Module(new RemainderRegister)
  val iteration = RegInit(0.U(5.W))

  alu.io.operandA := remainderRegister.io.dataOut
  alu.io.operandB := divisorRegister.io.dataOut
  alu.io.operation := ALUOp.SUB
  remainderRegister.io.dataIn := alu.io.aluResult
  remainderRegister.io.dataWrite := io.a
  divisorRegister.io.dataLoad := io.b
  io.q := quotientRegister.io.dataOut
  io.r := remainderRegister.io.dataOut(15, 0)

  io.valid := false.B
  remainderRegister.io.write := false.B
  divisorRegister.io.load := false.B
  quotientRegister.io.dataIn := false.B

  when(io.rst) {
    iteration := 0.U
  }.otherwise {
    when(iteration === 0.U) {
      remainderRegister.io.write := true.B
      divisorRegister.io.load := true.B
      iteration := iteration + 1.U
    }.elsewhen(iteration === 17.U) {
      remainderRegister.io.write := true.B
      divisorRegister.io.load := true.B
      io.valid := true.B
      iteration := 1.U
    }.otherwise {
      remainderRegister.io.dataWrite := remainderRegister.io.dataOut
      when(alu.io.aluResult(31)) {
        remainderRegister.io.write := true.B
        quotientRegister.io.dataIn := false.B
      }.otherwise {
        remainderRegister.io.write := false.B
        quotientRegister.io.dataIn := true.B
      }
      iteration := iteration + 1.U
    }
  }
}

class DivisorRegister extends Module {
  val io = IO(new Bundle {
    val load = Input(Bool())
    val dataLoad = Input(UInt(16.W))
    val dataOut = Output(UInt(32.W))
  })
  val reg = RegInit(0.U(32.W))
  when(io.load) {
    reg := Cat(0.U(16.W), io.dataLoad)
  }.otherwise {
    reg := Cat(0.U, reg(31, 1))
  }
  io.dataOut := reg
}

class QuotientRegister extends Module {
  val io = IO(new Bundle {
    val dataIn = Input(Bool())
    val dataOut = Output(UInt(16.W))
  })
  val reg = RegInit(0.U(16.W))
  reg := Cat(reg(13, 0), io.dataIn)
  io.dataOut := reg
}

class RemainderRegister extends Module {
  val io = IO(new Bundle {
    val write = Input(Bool())
    val dataWrite = Input(UInt(32.W))
    val dataIn = Input(UInt(32.W))
    val dataOut = Output(UInt(32.W))
  })
  val reg = RegInit(0.U(32.W))
  when(io.write) {
    reg := io.dataIn
  }.otherwise {
    reg := io.dataWrite
  }
  io.dataOut := reg
}