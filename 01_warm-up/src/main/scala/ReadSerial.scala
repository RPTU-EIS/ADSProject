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
        val rxd    = Input(Bool())   // serial input
        val cnt_s  = Input(Bool())   // counter done
        val cnt_en = Output(Bool())  // enable counter
        val valid  = Output(Bool())  // data ready
    })

  // internal variables
  /* 
   * TODO: Define internal variables (registers and/or wires), if needed
   */

  // State definition
        val idle      = false.B
        val receiving = true.B
        val state     = RegInit(false.B)  // false=IDLE, true=RECEIVING

        // Default outputs
        io.cnt_en := false.B
        io.valid  := false.B

  // state machine
  /* 
   * TODO: Describe functionality if the controller as a state machine
   */
  when(reset.asBool) {
    // Reset — abort transmission
    state     := idle
    io.cnt_en := false.B
    io.valid  := false.B

  } .otherwise {

    switch(state) {

      is(idle) {
        // Wait for start bit
        when(io.rxd === false.B) {
          state := receiving
        }
      }

      is(receiving) {
        io.cnt_en := true.B
        // Wait for 8 bits
        when(io.cnt_s) {
          state     := idle
          io.valid  := true.B
          io.cnt_en := false.B
        }
      }
    }
  }
}




/** counter class */
/*class Counter extends Module{
  
  val io = IO(new Bundle {
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
        val cnt_en = Input(Bool())
        val cnt_s  = Output(Bool())

    })

  // internal variables
  /* 
   * TODO: Define internal variables (registers and/or wires), if needed
   */
      val count = RegInit(0.U(4.W))  // 4-bit counter to count up to 8
  // state machine
  /* 
   * TODO: Describe functionality if the counter as a state machine
   */ 
   io.cnt_s := false.B

  when(reset.asBool) {

    count := 0.U

  }.otherwise {

    when(io.cnt_en) {

      when(count === 7.U) {

        io.cnt_s := true.B
        count := 0.U

      }.otherwise {

        count := count + 1.U
      }

    }.otherwise {

      count := 0.U
    }
  }
}*/
class Counter extends Module {
  val io = IO(new Bundle {
    val cnt_en = Input(Bool())
    val cnt_s  = Output(Bool())
  })

  val count  = RegInit(0.U(4.W))
  val cnt_s_reg = RegInit(false.B)   // ← register breaks the loop

  io.cnt_s := cnt_s_reg              // output comes from register

  cnt_s_reg := false.B               // default: clear each cycle

  when(reset.asBool) {
    count     := 0.U
    cnt_s_reg := false.B
  } .otherwise {
    when(io.cnt_en) {
      when(count === 7.U) {          // ← count to 7, 
        cnt_s_reg := true.B
        count     := 0.U
      } .otherwise {
        count := count + 1.U
      }
    } .otherwise {
      count     := 0.U
      cnt_s_reg := false.B
    }
  }
}


/** shift register class */
class ShiftRegister extends Module{
  
  val io = IO(new Bundle {
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
        val rxd    = Input(Bool())      // serial input bit
        val cnt_en = Input(Bool())      // shift only when receiving
        val data   = Output(UInt(8.W))  // 8-bit parallel output
    })

  // internal variables
  /* 
   * TODO: Define internal variables (registers and/or wires), if needed
   */ val shift_reg = RegInit(0.U(8.W))

  // functionality
  /* 
   * TODO: Describe functionality if the shift register
   */
  
    when(reset.asBool) {
        // Reset — clear shift register
        shift_reg := 0.U

      } .otherwise {

        when(io.cnt_en) {
          // Shift in new bit — MSB first
          // new bit comes in at LSB
          // existing bits shift left
          shift_reg := Cat(shift_reg(6, 0), io.rxd)
        }

      }

      // Output is always current shift register value
      io.data := shift_reg
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
      val rxd   = Input(Bool())       // serial input
      val data  = Output(UInt(8.W))   // 8-bit parallel output
      val valid = Output(Bool())      // data ready signal
    })


  // instanciation of modules
  /* 
   * TODO: Instanciate the modules that you need
   */
      val controller    = Module(new Controller())
      val counter       = Module(new Counter())
      val shiftRegister = Module(new ShiftRegister())

    // connections between modules
    /* 
    * TODO: connect the signals between the modules
    */ 
    // Connect Controller
    controller.io.rxd   := io.rxd
    controller.io.cnt_s := counter.io.cnt_s

    // Connect Counter
    counter.io.cnt_en := controller.io.cnt_en

    // Connect Shift Register
    shiftRegister.io.rxd    := io.rxd
    shiftRegister.io.cnt_en := controller.io.cnt_en

    // global I/O 
    /* 
    * TODO: Describe output behaviour based on the input values and the internal signals
    */ 
    
    //Global outputs
    io.valid := controller.io.valid
    io.data  := shiftRegister.io.data

}
