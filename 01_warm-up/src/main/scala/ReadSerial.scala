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

  val start = UInt(1.W)
  start := 0.U

  when(io.rxd === 0.U && start) {

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
  }

  when(io.reset_n === 1.U) {
    reg := 0.U
  }

  io.s := reg

}

/** shift register class */
class ShiftRegister extends Module {

  val io = IO(new Bundle {
    val in = Input(UInt(1.W))
    val data = Output(UInt(8.W))
  })

  val reg = RegInit(0.U(8.W))

  reg := Cat(io.in, reg(7, 1))

  io.out := reg
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
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
  })


  // instanciation of modules
  /* 
   * TODO: Instanciate the modules that you need
   */

  // connections between modules
  /* 
   * TODO: connect the signals between the modules
   */

  // global I/O 
  /* 
   * TODO: Describe output behaviour based on the input values and the internal signals
   */

}
