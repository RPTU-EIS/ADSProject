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
    val rst = Input(UInt(1.W))
    val rxd = Input(UInt(1.W))
    val cnt_s = Input(UInt(1.W))
    val cnt_en = Output(UInt(1.W))
    val valid = Output(UInt(1.W))
    val ps_t = Output(UInt(2.W)) //debugging
  })

  // internal variables
  val ps = RegInit(0.U(2.W))
  val ns = WireDefault(ps)

  io.cnt_en := 0.U
  io.valid := 0.U

  // state machine

  when(io.rst === 1.U) {
    ns := 0.U
  }.
    otherwise {
      switch(ps) {
        // IDLE
        is(0.U) {
          when(io.rxd === 1.U) {
            ns := 0.U
          }.
          elsewhen(io.rxd === 0.U) {
            ns := 1.U
          }
        }
        // TRANSMIT
        is(1.U) {
          when(io.cnt_s === 0.U) {
            io.cnt_en := 1.U
            ns := 1.U
          }.
          elsewhen(io.cnt_s === 1.U) {
            ns := 2.U
            //io.valid := io.cnt_s // Mealy implementation of Valid
          }
        }
        // DONE
        is(2.U) {
          io.valid := 1.U
          ns := 0.U
        }
      }
    }

  ps := ns
  io.ps_t := ps

}

/** counter class */
class Counter extends Module {

  val io = IO(new Bundle {
    val rst = Input(UInt(1.W))
    val cnt_en = Input(UInt(1.W))
    val cnt_s = Output(UInt(1.W))
    val count_t = Output(UInt(3.W)) //debugging
  })

  // internal variables
  val count = RegInit(0.U(3.W))

  io.cnt_s := 0.U
  // state machine
  when(io.rst === 1.U) {
    count := 0.U
  }.elsewhen(io.cnt_en === 1.U && count < 7.U) {
    count := count + 1.U
  }

  when(count === 7.U) {
    io.cnt_s := 1.U
  }

  io.count_t := count
}

/** shift register class */
class ShiftRegister extends Module {

  val io = IO(new Bundle {
    val rxd = Input(UInt(1.W))
    val data = Output(UInt(8.W))
  })

  // internal variables
  val reg = RegInit(0.U(8.W))

  // functionality
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
    val debug_ps = Output(UInt(2.W))
    val debug_cnt_s = Output(UInt(1.W))
    val debug_count = Output(UInt(3.W))
  })


  // instanciation of modules
  val CU = Module(new Controller())
  val SR = Module(new ShiftRegister())
  val CNT = Module(new Counter())

  // connections between modules
  CU.io.rxd := io.rxd
  CU.io.rst := io.reset_n
  CU.io.cnt_s := CNT.io.cnt_s

  CNT.io.rst := io.reset_n
  CNT.io.cnt_en := CU.io.cnt_en


  SR.io.rxd := io.rxd

  // global I/O 

  io.valid := CU.io.valid
  io.data := SR.io.data
  io.debug_ps := CU.io.ps_t
  io.debug_cnt_s := CU.io.cnt_s
  io.debug_count := CNT.io.count_t

}
