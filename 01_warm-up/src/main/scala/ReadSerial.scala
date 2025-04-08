// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package readserial

import chisel3._
import chisel3.util._

/** controller class */
import chisel3._
import chisel3.util._

/** Controller class - manages the state of the serial receiver */
class Controller extends Module{

  val io = IO(new Bundle {
    val reset_n = Input(Bool())    // Active-low reset
    val rxd     = Input(Bool())    // Serial input
    val cnt_s   = Input(Bool())    // Counter done signal (8 bits received)
    val cnt_en  = Output(Bool())   // Enable signal for counter
    val valid   = Output(Bool())   // High when byte reception is complete
  })

  val sIdle :: sReceiving :: Nil = Enum(2) //definition of posible state of the controller
  val state = RegInit(sIdle)  // State start = sIdle

  val validReg = RegInit(false.B)  // one-cycle valid pulse

  io.cnt_en := false.B
  io.valid  := validReg

  validReg := false.B // default

  when(!io.reset_n){      //when reset is active, then state is sIdle
    state := sIdle
  } .otherwise {          //if reset is unactive , then ..
    switch (state) {
      is(sIdle) {         //if state is sIdle
        when(io.rxd === false.B) { //detect start bit
          state := sReceiving
        }
      }

      is(sReceiving) {    //if state is sReceiving
        io.cnt_en := true.B         //enable counter
        when(io.cnt_s) {            //if 8 bits received
          validReg := true.B        //indicate valid byte
          state := sIdle            //return to idle
        }
      }
    }
  }
}

/** counter class */
class Counter extends Module {
  val io = IO(new Bundle {
    val reset_n = Input(Bool())
    val cnt_en  = Input(Bool())
    val cnt_s   = Output(Bool())
  })

  val count = RegInit(0.U(3.W))
  val done  = RegInit(false.B)

  when(!io.reset_n) {
    count := 0.U
    done := false.B
  } .elsewhen(io.cnt_en) {
    when(count === 7.U) {
      done := true.B
      count := 0.U
    } .otherwise {
      done := false.B
      count := count + 1.U
    }
  } .otherwise {
    done := false.B
  }

  io.cnt_s := done
}

/** shift register class */
class ShiftRegister extends Module {
  val io = IO(new Bundle {
    val rxd  = Input(Bool())
    val load = Input(Bool()) // enable shift
    val data = Output(UInt(8.W))
  })

  val reg = RegInit(0.U(8.W))

  when(io.load) {
    // Correct MSB-first implementation:
    // Shift existing bits right and insert new bit at MSB
    reg := Cat(io.rxd, reg(7, 1))
  }

  io.data := reg
}

class ReadSerial extends Module{

  val io = IO(new Bundle {
    val rxd     = Input(Bool())
    val reset_n = Input(Bool())
    val data    = Output(UInt(8.W))
    val valid   = Output(Bool())
  })

  val controller = Module(new Controller)
  val counter    = Module(new Counter)
  val shifter    = Module(new ShiftRegister)

  // Connect global inputs
  controller.io.rxd     := io.rxd
  controller.io.reset_n := io.reset_n
  counter.io.reset_n    := io.reset_n

  // Connect controller <-> counter
  controller.io.cnt_s := counter.io.cnt_s
  counter.io.cnt_en   := controller.io.cnt_en

  // Connect controller -> shifter
  shifter.io.rxd  := io.rxd
  shifter.io.load := controller.io.cnt_en

  // Outputs
  io.data  := shifter.io.data
  io.valid := controller.io.valid
}

