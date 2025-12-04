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
    //val reset_n = Input(UInst(1.W)) ???
    //val rxd = Input(UInst(1.W))
    //val valid = Output(UInt(1.W))
    })

  // internal variables


  // state machine
  /* 
   * TODO: Describe functionality if the controller as a state machine
   */

}


/** counter class */
class Counter extends Module{
  
  val io = IO(new Bundle {
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
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
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
    })

  // internal variables
  /* 
   * TODO: Define internal variables (registers and/or wires), if needed
   */

  // functionality
  /* 
   * TODO: Describe functionality if the shift register
   */
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
