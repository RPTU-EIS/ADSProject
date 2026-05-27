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
    val rst     = Input(UInt(1.W))
    val rxd     = Input(UInt(1.W))
    val co      = Input(UInt(1.W))
    val cnt_en  = Output(UInt(1.W))
    val valid   = Output(UInt(1.W))
    })

  // internal variables
  val ps = RegInit(0.U(2.W))
  val ns = RegInit(0.U(2.W))

  io.cnt_en := 0.U
  io.valid  := 0.U

  // state machine
  switch(ps){
    // IDLE
    is(0.U){
      when(io.rst === 1.U){
        ns := 0.U
      }.
      otherwise{
        when(io.rxd === 1.U){
          ns := 1.U
        }.
        elsewhen(io.rxd === 0.U){
          ns := 0.U
        }
      }
    }
    // TRANSMIT
    is(1.U){
      when(io.rst === 1.U){
        ns := 0.U
      }.
      otherwise{
        when(io.co === 0.U){
          io.cnt_en := 1.U
        }.
        elsewhen(io.co == 1.U){
          ns := 2.U
        }
      }
    }
    // DONE
    is(2.U){
      when(io.rst === 1.U){
        ns := 0.U
      }.
      otherwise{
        io.valid := 1.U
        ns := 0.U
      }
    }
  }

  ps := ns

}


/** counter class */
class Counter extends Module{
  
  val io = IO(new Bundle {
    val rst    = Input(UInt(1.W))
    val cnt_en = Input(UInt(1.W))
    val cnt_s  = Output(UInt(1.W))
    })

  // internal variables
  /* 
   * TODO: Define internal variables (registers and/or wires), if needed
   */

  // state machine
  /* 
   * TODO: Describe functionality if the counter as a state machine
   */


}

/** shift register class */
class ShiftRegister extends Module{
  
  val io = IO(new Bundle {
    val rxd  = Input(UInt(1.W))
    val data  = Output(UInt(8.W))
  })

  // internal variables
  val reg = RegInit(0.U(8.W))

  // functionality
  reg := io.rxd << 1
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
class ReadSerial extends Module{
  
  val io = IO(new Bundle {
      val reset_n   = Input(UInt(1.W))
      val rxd       = Input(UInt(1.W))
      val valid     = Output(UInt(1.W))
      val data      = Output(UInt(8.W))
    })


  // instanciation of modules
  val CU = Module(new Controller())

  // connections between modules
  CU.rxd := io.rxd
  CU.rst := io.reset_n

  // global I/O 
  /* 
   * TODO: Describe output behaviour based on the input values and the internal signals
   */

  io.valid := CU.valid

}
