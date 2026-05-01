// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package readserial

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum


/** controller class */
class Controller extends Module{
  
  // Define states using ChiselEnum
  object State extends ChiselEnum {
    val idle, receive, svalid = Value
  }
  
  // Import states for easier usage (import everything, so we can use state names directly instead of State.idle, State.receive, ...)
  import State._ 

  val io = IO(new Bundle {
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
    val reset_n = Input(Bool())
    val rxd = Input(Bool())
    val cnt_s = Input(UInt(3.W))

    val cnt_en = Output(Bool())
    val valid = Output(Bool())
    })

  // internal variables
  val state = RegInit(idle)
  
  // Default outputs - ensure all outputs are initialized
  io.cnt_en := false.B
  io.valid := false.B
  
  // state machine
  switch(state) {
    is(idle) {
      when (!io.rxd) { // Start bit detected
        state := receive
      }
    }
    is(receive) {
      io.cnt_en := true.B // Keep counter enabled while receiving bits
      when(io.cnt_s === 7.U) { // After receiving 8 bits (0-7)
        state := svalid
      }
    }
    is(svalid) {
      io.valid := true.B   // Indicate that data is valid
      when (!io.rxd) { // Start bit detected
        state := receive
      } .otherwise { // Wait for next transmission
        state := idle
      }
    }
  }

  when(!io.reset_n) {
    state := idle
  }
}


/** counter class */
class Counter extends Module {
  
  val io = IO(new Bundle {
    val reset_n = Input(Bool())
    val cnt_en = Input(Bool())

    val cnt_s = Output(UInt(3.W))
    })

  // internal variables
  val count = RegInit(0.U(3.W))

  // state machine
  when(!io.reset_n | !io.cnt_en) {
    count := 0.U
  } .elsewhen (io.cnt_en) {
    count := count + 1.U
  }

  io.cnt_s := count

}

/** shift register class */
class ShiftRegister extends Module{
  
  val io = IO(new Bundle {
    val rxd = Input(Bool())
    val cnt_en = Input(Bool())

    val data = Output(UInt(8.W))
    })

  // internal variables
  val shiftReg = RegInit(0.U(8.W))

  // functionality
  when(io.cnt_en) {
    shiftReg := Cat(shiftReg(6, 0), io.rxd) // Shift left and input new bit
  }

  io.data := shiftReg
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
class ReadSerial extends Module{
  
  val io = IO(new Bundle {
    val reset_n = Input(Bool())
    val rxd = Input(Bool())

    val valid = Output(Bool())
    val data = Output(UInt(8.W))
    })


  // instanciation of modules
  val controller = Module(new Controller())
  val counter = Module(new Counter())
  val shiftRegister = Module(new ShiftRegister())

  // connections between modules
  controller.io.reset_n := io.reset_n
  controller.io.rxd := io.rxd
  controller.io.cnt_s := counter.io.cnt_s

  counter.io.reset_n := io.reset_n
  counter.io.cnt_en := controller.io.cnt_en

  shiftRegister.io.rxd := io.rxd
  shiftRegister.io.cnt_en := controller.io.cnt_en

  // global I/O 
  io.valid := controller.io.valid
  io.data := shiftRegister.io.data

}
