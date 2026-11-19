package divider

import chisel3._

class SIntDivider extends Module {
  val io = IO(new Bundle {
    val a = Input(SInt(16.W))
    val b = Input(SInt(16.W))
    val rst = Input(Bool())
    val q = Output(SInt(16.W))
    val r = Output(SInt(16.W))
    val valid = Output(Bool())
  })

  val uIntDivider = Module(new UIntDivider)
  val signCalc = Module(new SignCalc)
  val toUnsigned_a = Module(new ToUnsigned)
  val toUnsigned_b = Module(new ToUnsigned)
  val toSigned_q = Module(new ToSigned)
  val toSigned_r = Module(new ToSigned)

  uIntDivider.io.rst := io.rst
  toUnsigned_a.io.valueIn := io.a
  toUnsigned_b.io.valueIn := io.b
  uIntDivider.io.a := toUnsigned_a.io.valueOut
  uIntDivider.io.b := toUnsigned_b.io.valueOut
  signCalc.io.a := io.a
  signCalc.io.b := io.b
  toSigned_q.io.valueIn := uIntDivider.io.q
  toSigned_q.io.sign := signCalc.io.q_sign
  toSigned_r.io.valueIn := uIntDivider.io.r
  toSigned_r.io.sign := signCalc.io.r_sign
  io.q := toSigned_q.io.valueOut
  io.r := toSigned_r.io.valueOut
  io.valid := uIntDivider.io.valid
}

class SignCalc extends Module {
  val io = IO(new Bundle {
    val a = Input(SInt(16.W))
    val b = Input(SInt(16.W))
    val q_sign = Output(Bool())
    val r_sign = Output(Bool())
  })

  io.q_sign := io.a(15) | io.b(15)
  io.r_sign := io.b(15)
}

class ToUnsigned extends Module {
  val io = IO(new Bundle {
    val valueIn = Input(SInt(16.W))
    val valueOut = Output(UInt(16.W))
  })

  when(io.valueIn(15)) {
    io.valueOut := (~io.valueIn).asUInt
  }.otherwise {
    io.valueOut := io.valueIn.asUInt
  }
}

class ToSigned extends Module {
  val io = IO(new Bundle {
    val valueIn = Input(UInt(16.W))
    val sign = Input(Bool())
    val valueOut = Output(SInt(16.W))
  })

  when(!io.sign) {
    io.valueOut := (~io.valueIn + 1.U).asSInt
  }.otherwise {
    io.valueOut := io.valueIn.asSInt
  }
}