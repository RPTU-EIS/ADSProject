// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package readserial

import chisel3._
import chisel3.util._


/** controller class */
class Controller extends Module {

  val io = IO(new Bundle {
    val reset_n = Input(UInt(1.W))
    val rxd     = Input(UInt(1.W))
    val cnt_s   = Input(UInt(3.W))

    val cnt_en  = Output(UInt(1.W))
    val valid   = Output(UInt(1.W))
  })

  val sIdle :: sReceive :: sDone :: Nil = Enum(3)
  val state = RegInit(sIdle)

  io.cnt_en := 0.U
  io.valid  := 0.U

  when(io.reset_n === 0.U) {
    state     := sIdle
    io.cnt_en := 0.U
    io.valid  := 0.U
  }.otherwise {
    switch(state) {

      is(sIdle) {
        io.cnt_en := 0.U
        io.valid  := 0.U
        when(io.rxd === 0.U) {
          state := sReceive
        }
      }

      is(sReceive) {
        io.cnt_en := 1.U
        io.valid  := 0.U
        when(io.cnt_s === 7.U) {
          state := sDone
        }
      }

      is(sDone) {
        io.cnt_en := 0.U
        io.valid  := 1.U
        state     := sIdle
      }
    }
  }
}


/** counter class */
class Counter extends Module {

  val io = IO(new Bundle {
    val reset_n = Input(UInt(1.W))
    val cnt_en  = Input(UInt(1.W))
    val cnt_s   = Output(UInt(3.W))
  })

  val countReg = RegInit(0.U(3.W))

  when(io.reset_n === 0.U) {
    countReg := 0.U
  }.otherwise {
    when(io.cnt_en === 1.U) {
      when(countReg === 7.U) {
        countReg := 7.U
      }.otherwise {
        countReg := countReg + 1.U
      }
    }.otherwise {
      countReg := 0.U
    }
  }

  io.cnt_s := countReg
}


/** shift register class */
class ShiftRegister extends Module {

  val io = IO(new Bundle {
    val rxd  = Input(UInt(1.W))
    val data = Output(UInt(8.W))
  })

  val registerShift = RegInit(0.U(8.W))

  // Always shift unconditionally.
  // Original code reset to 0 when rxd=0, corrupting any '0' data bit.
  registerShift := (registerShift << 1) | io.rxd

  io.data := registerShift
}


/** top-level ReadSerial module */
class ReadSerial extends Module {

  val io = IO(new Bundle {
    val reset_n = Input(UInt(1.W))
    val rxd     = Input(UInt(1.W))

    val data    = Output(UInt(8.W))
    val valid   = Output(UInt(1.W))
  })

  val controller = Module(new Controller)
  val counter    = Module(new Counter)
  val shiftReg   = Module(new ShiftRegister)

  controller.io.reset_n := io.reset_n
  controller.io.rxd     := io.rxd
  controller.io.cnt_s   := counter.io.cnt_s

  counter.io.reset_n := io.reset_n
  counter.io.cnt_en  := controller.io.cnt_en

  shiftReg.io.rxd := io.rxd

  io.data  := shiftReg.io.data
  io.valid := controller.io.valid
}