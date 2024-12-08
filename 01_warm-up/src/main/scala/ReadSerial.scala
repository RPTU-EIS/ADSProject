// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package readserial

import chisel3._
import chisel3.util._
import os.stat


/** controller class */
class Controller(dataWidth: Int = 8) extends Module{
  
  val io = IO(new Bundle {
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
        // serial interface
        val reset_n = Input(UInt(1.W))
        val rxd     = Input(UInt(1.W))

        // output interface
        val valid   = Output(UInt(1.W))
        
        // internal IO
        val cnt_s   = Input(UInt(4.W))
        val cnt_en  = Output(UInt(1.W))
    })

  // internal variables
  /* 
   * TODO: Define internal variables (registers and/or wires), if needed
   */
  val idleState :: runState :: Nil = Enum(2)

  val state = RegInit(idleState)
  val endOfPacket = RegNext(io.cnt_s === (dataWidth-1).U)

  // state machine
  /* 
   * TODO: Describe functionality if the controller as a state machine
   */

    when (!io.reset_n) {
        state := idleState // reset the FSM
    }
    .otherwise {
        switch (state){
            is(idleState){
                when (io.rxd === 0.U) {
                    state := runState
                }
            }
            is (runState){
                when (io.cnt_s === (dataWidth-1).U) {
                    when (io.rxd === 1.U) {
                        state := idleState
                    }
                    .otherwise{
                        state := runState

                    }
                }
            }
            // is (doneState) {
            //     when (io.rxd === 1.U) {
            //         state := idleState
            //     }
            //     .elsewhen(io.rxd === 0.U) { // there is a consecutive next packet
            //         state := runState
            //     }
            // }
        }
    }

    io.cnt_en := state === runState // run the counter
    io.valid := endOfPacket // data valid after all the bits are gathered.

}


/** counter class */
class Counter (dataWidth: Int = 8) extends Module{
  
  val io = IO(new Bundle {
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
        val cnt_s = Output(UInt(log2Ceil(dataWidth).W))
        val cnt_en = Input(UInt(1.W))
    })

  // internal variables
  /* 
   * TODO: Define internal variables (registers and/or wires), if needed
   */

  // state machine
  /* 
   * TODO: Describe functionality if the counter as a state machine
   */
  val idleState :: runState :: Nil = Enum(2)
  val state = RegInit(idleState)

  val counter = RegInit(0.U(log2Ceil(dataWidth).W))

  when(io.cnt_en === 0.U){
    state := idleState
  }
  .otherwise{
      switch (state) {
        is (idleState){
            counter := 0.U
            when(io.cnt_en === 1.U){
                state := runState
            }
        }
        is (runState){
            counter := counter + 1.U
            when(counter === (dataWidth-1).U){
                state := idleState
            }
        }
      }
  }

  io.cnt_s := counter

}

/** shift register class */
class ShiftRegister(dataWidth: Int = 8) extends Module{
  
  val io = IO(new Bundle {
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
        val rxd = Input(UInt(1.W))
        val data = Output(UInt(dataWidth.W))
    })

  // internal variables
  /* 
   * TODO: Define internal variables (registers and/or wires), if needed
   */
  val shiftReg0 = RegInit(0.U(dataWidth.W))
  val shiftReg1 = RegInit(0.U(dataWidth.W))

  // functionality
  /* 
   * TODO: Describe functionality if the shift register
   */
  shiftReg0 := Cat(shiftReg0(dataWidth-2,0), io.rxd)
  shiftReg1 := shiftReg0

  io.data := shiftReg1
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
class ReadSerial (dataWidth: Int = 8) extends Module{
  
  val io = IO(new Bundle {
    /* 
     * TODO: Define IO ports of a the component as stated in the documentation
     */
        val reset_n = Input(UInt(1.W))
        val rxd     = Input(UInt(1.W))
        val valid   = Output(UInt(1.W))
        val data    = Output(UInt(dataWidth.W))
    })


  // instanciation of modules
  /* 
   * TODO: Instanciate the modules that you need
   */
    val controllerInst  = Module(new Controller)
    val counterInst     = Module(new Counter)
    val shiftRegInst    = Module(new ShiftRegister)

  // connections between modules
  /* 
   * TODO: connect the signals between the modules
   */

  counterInst.io.cnt_en        := controllerInst.io.cnt_en
  controllerInst.io.cnt_s      := counterInst.io.cnt_s
  
  // global I/O 
  /* 
  * TODO: Describe output behaviour based on the input values and the internal signals
  */
  controllerInst.io.reset_n    := io.reset_n
  controllerInst.io.rxd        := io.rxd
  shiftRegInst.io.rxd          := io.rxd

  io.data   := shiftRegInst.io.data
  io.valid  := controllerInst.io.valid

}
