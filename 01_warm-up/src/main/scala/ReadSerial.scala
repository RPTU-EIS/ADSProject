// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package readserial

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

object ReadSerial {
  object State extends ChiselEnum {
    val idle, receiving, done = Value
  }
}
/** controller class */
class Controller extends Module{
  import ReadSerial.State
  import ReadSerial.State._
  val io = IO(new Bundle {
    /*
     * TODO: Define IO ports of a the component as stated in the documentation
     */
    val rxd = Input(UInt(1.W))// Serial input
    val cnt_en = Output(Bool()) // Enable counter when receiving
    val cnt_s = Input(Bool())
    val valid = Output(Bool()) // Signal when data is valid
    val state = Output(State())
    })

  // internal variables
  /* 
   * TODO: Define internal variables (registers and/or wires), if needed
   */
  io.cnt_en := false.B
  io.valid := false.B
  val state = RegInit(idle)
  io.state := state

  // state machine
  /* 
   * TODO: Describe functionality if the controller as a state machine
   */
  switch(state) {
    is(idle) {
      when(io.rxd === 0.U) { // Detect start bit
        state := receiving
        io.cnt_en := true.B
      }
    }
    is(receiving) {
      io.cnt_en := true.B
      when(io.cnt_s === true.B){
        state := done
        io.cnt_en := false.B
      }
    }
    is(done) {
      io.valid := true.B
      state := idle
    }
  }

}


/** counter class */
class Counter extends Module{
  
  val io = IO(new Bundle {
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
    val enable = Input(Bool()) // Enable counting
    val done = Output(Bool()) // Signal when counting is complete
    })

  // internal variables
  /* 
   * TODO: Define internal variables (registers and/or wires), if needed
   */
  val count = RegInit(0.U(3.W)) // 3-bit counter (0 to 7)
  val d_count = RegInit(false.B)

  io.done := d_count

  // state machine
  /* 
   * TODO: Describe functionality if the counter as a state machine
   */

  when(io.enable) {
    count := count + 1.U
    d_count := false.B
    when(count === 7.U) {
      d_count := true.B
      count := 0.U // Reset after completion
    }
  }



}

/** shift register class */
class ShiftRegister extends Module{
  
  val io = IO(new Bundle {
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
    val in = Input(Bool()) // Serial input
    val out = Output(UInt(8.W)) // Parallel output (8-bit data)


    })

  // internal variables
  /* 
   * TODO: Define internal variables (registers and/or wires), if needed
   */
  val reg = RegInit(0.U(8.W)) // 8-bit shift register

  io.out := reg
  // functionality
  /* 
   * TODO: Describe functionality if the shift register
   */
    reg := Cat(reg(6, 0), io.in) // Shift left and insert new bit
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
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
    val rxd = Input(UInt(1.W)) // Serial input
    val data = Output(UInt(8.W)) // Parallel data output
    val valid = Output(Bool()) // Valid signa
    })


  // instanciation of modules
  /* 
   * TODO: Instanciate the modules that you need
   */
  val controller = Module(new Controller)
  val counter = Module(new Counter)
  val shiftRegister = Module(new ShiftRegister)

  // connections between modules
  /* 
   * TODO: connect the signals between the modules
   */

  // Connect controller to counter
  controller.io.rxd   := io.rxd
  counter.io.enable   := controller.io.cnt_en

  // Connect counter to controller
  controller.io.cnt_s := counter.io.done

  // Connect shift register
  shiftRegister.io.in := io.rxd

  // global I/O 
  /* 
   * TODO: Describe output behaviour based on the input values and the internal signals
   */
  // Output assignments
  io.data := shiftRegister.io.out
  io.valid := controller.io.valid

}
