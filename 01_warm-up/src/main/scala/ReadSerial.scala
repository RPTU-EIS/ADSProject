// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package readserial

import chisel3._
import chisel3.util._

/** Controller
 * Inputs:  reset_n, rxd, cnt_s
 * Outputs: cnt_en, valid
 *
 * FSM:
 *  - Idle: wait for rxd == 0 (start bit)
 *  - Start: skip the start bit for one cycle; then enable counting
 *  - Receive: keep cnt_en high, wait for cnt_s (8th data bit)
 *  - Done: assert valid for one cycle, then go back to Idle
 */
class Controller extends Module {

  val io = IO(new Bundle {
    val reset_n = Input(Bool())   // active-low reset
    val rxd     = Input(Bool())   // serial input line
    val cnt_s   = Input(Bool())   // "done" pulse from Counter (8 bits received)
    val cnt_en  = Output(Bool())  // enable for Counter
    val valid   = Output(Bool())  // one-cycle pulse after last bit
  })

  val sIdle :: sStart :: sRecv :: sDone :: Nil = Enum(4)
  val state = RegInit(sIdle)

  io.cnt_en := false.B
  io.valid  := false.B

  when(!io.reset_n) {
    state     := sIdle
    io.cnt_en := false.B
    io.valid  := false.B
  }.otherwise {
    switch(state) {
      is(sIdle) {
        // Detect start bit '0'
        when(io.rxd === false.B) {
          state := sStart
        }
      }
      is(sStart) {
        // Skip the start bit one cycle, then start receiving data bits
        io.cnt_en := true.B
        state     := sRecv
      }
      is(sRecv) {
        // Enable counting until 8th data bit (cnt_s) occurs
        io.cnt_en := true.B
        when(io.cnt_s) {
          state := sDone
        }
      }
      is(sDone) {
        // Pulse valid for one cycle
        io.valid := true.B
        state    := sIdle
      }
    }
  }
}

/** Counter
 * Inputs:  reset_n, cnt_en
 * Output:  cnt_s (one-cycle pulse on the 8th data bit)
 *
 * Counts 0..7 while cnt_en is high; emits cnt_s on count==7 and wraps to 0.
 */
class Counter extends Module {

  val io = IO(new Bundle {
    val reset_n = Input(Bool()) // active-low reset
    val cnt_en  = Input(Bool()) // enable counting during data reception
    val cnt_s   = Output(Bool()) // done pulse on the 8th data bit
  })

  val cnt  = RegInit(0.U(4.W))   // 0..7 => 8 samples
  val done = WireDefault(false.B)

  when(!io.reset_n) {
    cnt  := 0.U
    done := false.B
  }.otherwise {
    when(io.cnt_en) {
      when(cnt === 7.U) {
        done := true.B
        cnt  := 0.U
      }.otherwise {
        cnt := cnt + 1.U
      }
    }.otherwise {
      // hold count when not enabled
      cnt := cnt
    }
  }

  io.cnt_s := done
}

/** ShiftRegister
 * Input:  rxd
 * Output: data
 *
 * Shifts MSB-first: new bit enters at bit[7], content shifts right.
 * The controller's valid indicates when the byte is meaningful.
 */
class ShiftRegister extends Module {

  val io = IO(new Bundle {
    val rxd  = Input(Bool())
    val data = Output(UInt(8.W))
  })

  val reg = RegInit(0.U(8.W))

  // Continuous MSB-first shift
  reg := Cat(io.rxd.asUInt, reg(7, 1))
  io.data := reg
}

/** ReadSerial top-level
 * Inputs:  reset_n, rxd
 * Outputs: valid, data
 */
class ReadSerial extends Module {

  val io = IO(new Bundle {
    val reset_n = Input(Bool())
    val rxd     = Input(Bool())
    val valid   = Output(Bool())
    val data    = Output(UInt(8.W))
  })

  val ctrl = Module(new Controller)
  val cnt  = Module(new Counter)
  val shft = Module(new ShiftRegister)

  // Wire up modules per diagram
  ctrl.io.reset_n := io.reset_n
  ctrl.io.rxd     := io.rxd
  ctrl.io.cnt_s   := cnt.io.cnt_s

  cnt.io.reset_n  := io.reset_n
  cnt.io.cnt_en   := ctrl.io.cnt_en

  shft.io.rxd     := io.rxd

  // Top outputs
  io.valid := ctrl.io.valid
  io.data  := shft.io.data
}