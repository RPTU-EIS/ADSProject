// ADS I Class Project
// Chisel Introduction

package readserial // This file belongs to the readserial package.

import chisel3._ // Import the basic Chisel hardware construction library.
import chisel3.util._ // Import Chisel utilities such as Enum, switch, is, and Cat.

// Controller is a finite state machine that detects the start bit and controls reception.
class Controller extends Module { // Define a hardware module called Controller.

  val io = IO(new Bundle { // Define the input/output interface of the controller.
    val rxd      = Input(UInt(1.W)) // Serial input line.
    val cnt_done = Input(Bool()) // Input from the counter: true when 8 bits were received.
    val cnt_en   = Output(Bool()) // Output to enable the counter and shift register.
    val valid    = Output(Bool()) // Output pulse: true for one cycle when data is ready.
  }) // End of the IO bundle.

  val idle :: receive :: Nil = Enum(2) // Define two FSM states: idle and receive.
  val stateReg = RegInit(idle) // Store the current state, starting in idle.
  val validReg = RegInit(false.B) // Register valid so it becomes a clean one-cycle pulse.

  switch(stateReg) { // Select behavior based on the current state.
    is(idle) { // Idle state waits for a start bit.
      when(io.rxd === 0.U) { // A 0 on rxd means the start bit was detected.
        stateReg := receive // Move to receive state.
      } // End of start-bit condition.
    } // End of idle state.

    is(receive) { // Receive state reads the 8 data bits.
      when(io.cnt_done) { // When the counter reports done, all 8 bits have been received.
        stateReg := idle // Go back to idle for the next byte.
      } // End of done condition.
    } // End of receive state.
  } // End of switch.

  io.cnt_en := (stateReg === receive) // Enable counting and shifting only while receiving.
  validReg  := (stateReg === receive) && io.cnt_done // Register valid when the last bit is received.
  io.valid  := validReg // Drive the valid output from the registered valid signal.
} // End of Controller.

// Counter counts the 8 received data bits.
class Counter extends Module { // Define a hardware module called Counter.

  val io = IO(new Bundle { // Define the input/output interface of the counter.
    val en   = Input(Bool()) // Enable input from the controller.
    val done = Output(Bool()) // Done output becomes true when the 8th bit is reached.
  }) // End of the IO bundle.

  val cntReg = RegInit(0.U(4.W)) // 4-bit register used to count from 0 to 7.

  when(io.en) { // If enabled, the receiver is currently reading data bits.
    cntReg := cntReg + 1.U // Increment the counter every clock cycle.
  } .otherwise { // If not enabled, the receiver is idle.
    cntReg := 0.U // Reset the counter so the next byte starts from 0.
  } // End of counter update logic.

  io.done := (cntReg === 7.U) // Done is true when the 8th data bit is being received.
} // End of Counter.

// ShiftRegister stores the incoming serial bits into an 8-bit parallel value.
class ShiftRegister extends Module { // Define a hardware module called ShiftRegister.

  val io = IO(new Bundle { // Define the input/output interface of the shift register.
    val in  = Input(UInt(1.W)) // Incoming serial bit.
    val en  = Input(Bool()) // Shift enable from the controller.
    val out = Output(UInt(8.W)) // 8-bit parallel output.
  }) // End of the IO bundle.

  val shiftReg = RegInit(0.U(8.W)) // 8-bit register initialized to zero.

  when(io.en) { // Shift only while receiving data bits.
    shiftReg := Cat(shiftReg(6, 0), io.in) // Shift left and insert the new bit at the LSB.
  } // End of shift logic.

  io.out := shiftReg // Drive the output with the current register value.
} // End of ShiftRegister.

// ReadSerial connects the controller, counter, and shift register together.
class ReadSerial extends Module { // Define the top-level serial receiver module.

  val io = IO(new Bundle { // Define the input/output interface of the top module.
    val rxd   = Input(UInt(1.W)) // Serial input line.
    val valid = Output(Bool()) // Output pulse: true when a full byte is ready.
    val data  = Output(UInt(8.W)) // Received byte.
  }) // End of the IO bundle.

  val controller = Module(new Controller()) // Instantiate the controller module.
  val counter    = Module(new Counter()) // Instantiate the counter module.
  val shiftReg   = Module(new ShiftRegister()) // Instantiate the shift register module.

  controller.io.rxd := io.rxd // Send the serial input to the controller so it can detect the start bit.
  shiftReg.io.in    := io.rxd // Send the serial input to the shift register so it can store data bits.

  counter.io.en  := controller.io.cnt_en // The controller enables the counter.
  shiftReg.io.en := controller.io.cnt_en // The controller also enables the shift register.

  controller.io.cnt_done := counter.io.done // The counter tells the controller when 8 bits are complete.

  io.valid := controller.io.valid // The top-level valid output comes from the controller.
  io.data  := shiftReg.io.out // The top-level data output comes from the shift register.
} // End of ReadSerial.
