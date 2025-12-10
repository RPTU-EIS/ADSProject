// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package readserial

import chisel3._
import chisel3.util._


/** controller class */
class Controller extends Module {

  val io = IO(new Bundle {
    val reset_n = Input(UInt(1.W))
    val rxd = Input(UInt(1.W))
    val cnt_s = Input(UInt(4.W))
    val cnt_en = Output(UInt(1.W))
    val valid = Output(UInt(1.W))
  })

  val active = RegInit(0.U(1.W))

  io.cnt_en := active
  io.valid := 0.U

  when(active === 1.U) {
    when(io.cnt_s === 8.U) {
      io.valid := 1.U
      active := 0.U
    }
  }.otherwise {
    when(io.rxd === 0.U) {
      active := 1.U
    }
  }

  when(io.reset_n === 1.U) {
    active := 0.U
  }

}


/** counter class */
class Counter extends Module {

  val io = IO(new Bundle {
    val reset_n = Input(UInt(1.W))
    val cnt_en = Input(UInt(1.W))
    val cnt_s = Output(UInt(4.W))
  })

  val reg = RegInit(0.U(4.W))

  when(io.cnt_en === 1.U) {
    reg := reg + 1.U
  }.otherwise {
    reg := 0.U
  }

  when(io.reset_n === 1.U) {
    reg := 0.U
  }

  io.cnt_s := reg

}

/** shift register class */
class ShiftRegister extends Module {

  val io = IO(new Bundle {
    val rxd = Input(UInt(1.W))
    val data = Output(UInt(8.W))
  })

  val reg = RegInit(0.U(8.W))

  reg := Cat(reg(6, 0), io.rxd)

  io.data := reg
}

/**
 * The last warm-up task deals with a more complex component. Your goal is to design a serial receiver.
 * It scans an input line (“serial bus”) named rxd for serial transmissions of data bytes. A transmission
 * begins with a start bit ‘0’ followed by 8 data bits. The most significant bit (MSB) is transmitted first.
 * There is no parity bit and no stop bit. After the last data bit has been transferred a new transmission
 * (beginning with a start bit, ‘0’) may immediately follow. If there is no new transmission the bus line
 * goes high (‘1’, this is considered the “idle” bus signal). In this case the receiver waits until the next
 * transmission begins. The outputs of the design are an 8-bit parallel data signal and a valid signal.
 * The valid signal goes high (‘1’) for one clock cycle after the last serial bit has been transmitted,
 * indicating that a new data byte is ready.
 */
class ReadSerial extends Module {

  val io = IO(new Bundle {
    val reset_n = Input(UInt(1.W))
    val rxd = Input(UInt(1.W))
    val valid = Output(UInt(1.W))
    val data = Output(UInt(8.W))
  })

  val controller = Module(new Controller)
  val counter = Module(new Counter)
  val shiftRegister = Module(new ShiftRegister)

  controller.io.reset_n := io.reset_n
  controller.io.rxd := io.rxd
  controller.io.cnt_s := counter.io.cnt_s

  counter.io.reset_n := io.reset_n
  counter.io.cnt_en := controller.io.cnt_en

  shiftRegister.io.rxd := io.rxd

  io.valid := controller.io.valid
  io.data := shiftRegister.io.data
}