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
    val reset = Input(Bool())
    val cnt_s = Input(Bool())
    val cnt_en = Output(Bool())
    val valid = Output(Bool())
    })

  // internal variables
  /* 
   * TODO: Define internal variables (registers and/or wires), if needed
   */
  val idle = 0.U
  val receive = 1.U
  val state = RegInit(idle)

  // state machine
  /* 
   * TODO: Describe functionality if the controller as a state machine
   */
  io.cnt_en := false.B
  io.valid := false.B

  switch(state){
    is(idle){
      when(!io.rxd && !io.reset){
        state := receive
      }
    }
    is(receive){
      io.cnt_en := true.B

      when(io.cnt_s){
        io.valid := true.B
        state := idle
      }
    }
  }

  when(io.reset){
    state := idle
    io.cnt_en := false.B
    io.valid := false.B
  }

}


/** counter class */
class Counter extends Module{
  
  val io = IO(new Bundle {
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
    val cnt_en = Input(Bool())
    val reset = Input(Bool())
    val cnt_s = Output(Bool())
  })

  // internal variables
  /* 
   * TODO: Define internal variables (registers and/or wires), if needed
   */
  val count = RegInit(0.U(4.W))
  io.cnt_s := false.B
  // state machine
  /* 
   * TODO: Describe functionality if the counter as a state machine
   */
  when(io.reset){
    count:= 0.U
    io.cnt_s := false.B
  } .elsewhen(io.cnt_en){
    when(count === 8.U){
      count := 0.U
    } .otherwise{
      count := count + 1.U
    }

    io.cnt_s := (count === 8.U)
  }

}

/** shift register class */
class ShiftRegister extends Module{
  
  val io = IO(new Bundle {
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
    val rxd = Input(Bool())
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
  shiftReg := Cat(shiftReg(6,0), io.rxd.asUInt)  //slicing and adding a new bit at LSB
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
    val reset = Input(Bool())
    val data = Output(UInt(8.W))
    val valid = Output(Bool())
    })


  // instanciation of modules
  /*
   * TODO: Instanciate the modules that you need
   */
  val controller = Module(new Controller())
  val counter = Module(new Counter())
  val shiftReg = Module(new ShiftRegister())
  // connections between modules
  /* 
   * TODO: connect the signals between the modules
   */
  controller.io.rxd := io.rxd
  controller.io.reset := io.reset
  controller.io.cnt_s := counter.io.cnt_s

  counter.io.cnt_en := controller.io.cnt_en
  counter.io.reset := io.reset

  shiftReg.io.rxd := io.rxd

  // global I/O 
  /* 
   * TODO: Describe output behaviour based on the input values and the internal signals
   */
  io.valid := controller.io.valid
  io.data := shiftReg.io.data

}
