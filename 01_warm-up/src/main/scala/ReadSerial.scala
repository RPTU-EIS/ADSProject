// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package readserial

import chisel3._
import chisel3.util._


/** controller class */
class Controller extends Module{
  
  val io = IO(new Bundle {
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
    val rxd = Input(Bool())
    val cnt_s = Input(UInt(3.W)) // Input from Counter value
    val cnt_en = Output(Bool())  // Enable Counter and Shifter
    val rst = Output(Bool()) // Synchronous reset for the counter
    val valid = Output(Bool())   // Valid signal to top level
    })

  // internal variables
  /* 
   * TODO: Define internal variables (registers and/or wires), if needed
   */
  val sIdle :: sReading :: sValid :: Nil = Enum(3)
  val state = RegInit(sIdle)

  io.cnt_en := false.B
  io.rst := false.B
  io.valid := false.B


  // state machine
  /* 
   * TODO: Describe functionality if the controller as a state machine
   */
  switch(state)
  {
    is(sIdle)
    {
      io.rst := true.B

      when(io.rxd === false.B)
      {
        state := sReading
      }
    }

    is(sReading)
    {
      io.cnt_en := true.B

      when(io.cnt_s === 7.U) //when we have received 8 bits
      {
        state := sValid
      }
    }

    is(sValid)
    {
      io.valid := true.B
      io.rst := true.B

      when(io.rxd === false.B)
      {
        state := sReading
      }
        .otherwise
      {
        state := sIdle
      }
    }
  }

}


/** counter class */
class Counter extends Module{
  
  val io = IO(new Bundle {
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
    val en = Input(Bool()) //start counting
    val rst = Input(Bool()) //for reseting the counter
    val cnt = Output(UInt(3.W)) //for counting 8 bits
    })

  // internal variables
  /* 
   * TODO: Define internal variables (registers and/or wires), if needed
   */
  val countReg = RegInit(0.U(3.W))

  // state machine
  /* 
   * TODO: Describe functionality if the counter as a state machine
   */
  when(io.rst)
  {
    countReg := 0.U
  }
    .elsewhen(io.en)
  {
    countReg := countReg+1.U
  }
  io.cnt := countReg


}

/** shift register class */
class ShiftRegister extends Module{
  
  val io = IO(new Bundle {
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
    val rxd = Input(Bool())
    //val en = Input(Bool())
    val data = Output(UInt(8.W))
    })

  // internal variables
  /* 
   * TODO: Define internal variables (registers and/or wires), if needed
   */
  val shiftReg = RegInit(0.U(8.W))

  // functionality
  /* 
   * TODO: Describe functionality if the shift register
   */
  shiftReg := Cat(shiftReg(6,0),io.rxd) //MSB transmitted first
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
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
    val rxd = Input(Bool())
    val valid = Output(Bool())
    val data = Output(UInt(8.W))
    })


  // instanciation of modules
  /* 
   * TODO: Instanciate the modules that you need
   */
  val ctrl = Module(new Controller)
  val cntr = Module(new Counter)
  val shifter = Module(new ShiftRegister)

  // connections between modules
  /* 
   * TODO: connect the signals between the modules
   */
  ctrl.io.rxd := io.rxd
  ctrl.io.cnt_s := cntr.io.cnt

  cntr.io.en := ctrl.io.cnt_en
  cntr.io.rst := ctrl.io.rst

  shifter.io.rxd := io.rxd

  // global I/O 
  /* 
   * TODO: Describe output behaviour based on the input values and the internal signals
   */
  io.valid := ctrl.io.valid
  io.data := shifter.io.data

}