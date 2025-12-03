// ADS I Class Project
// Chisel Introduction
//
// Serial receiver implementation for Task 1.5
// Chair of Electronic Design Automation, RPTU in Kaiserslautern

package readserial

import chisel3._
import chisel3.util._

/** Controller class
  *
  * io.rxd  : serial input line (1 = idle, start bit = 0)
  * io.rst  : external reset input (when high, abort ongoing reception)
  * io.en   : enable signal for counter/shift register (true while receiving data bits)
  * io.valid: one-cycle pulse indicating an 8-bit word is available
  */
class Controller extends Module {
  val io = IO(new Bundle {
    val rxd   = Input(Bool())
    val rst   = Input(Bool())
    val en    = Output(Bool())   // enable receiving (counter + shift)
    val done  = Input(Bool())    // from counter: true when last bit received
    val valid = Output(Bool())   // one-cycle pulse when byte is ready
  })

  // FSM states
  val sIdle :: sRecv :: Nil = Enum(2)
  val state = RegInit(sIdle)

  // valid pulse register
  val validReg = RegInit(false.B)

  // Default outputs
  io.en := false.B
  io.valid := false.B

  // State transitions and outputs
  when(io.rst) {
    // external reset aborts everything and makes outputs 0
    state := sIdle
    validReg := false.B
  } .otherwise {
    switch(state) {
      is(sIdle) {
        validReg := false.B
        // wait for start bit (rxd == 0)
        when(io.rxd === false.B) {
          // detected start bit -> begin receiving next cycle
          state := sRecv
          // enable receiving for the first data bit sample immediately
          io.en := true.B
        } .otherwise {
          io.en := false.B
        }
      }
      is(sRecv) {
        // while receiving, keep enable true
        io.en := true.B
        // if counter signals done, produce valid pulse for one cycle and go to Idle
        when(io.done) {
          validReg := true.B
          state := sIdle
          io.en := false.B
        } .otherwise {
          validReg := false.B
        }
      }
    }
  }

  // Drive valid output (pulse)
  io.valid := validReg
}

/** Counter class
  *
  * Counts 0..7 on each rising clock while io.en is true.
  * io.done goes high in the cycle when the counter reaches 7 and io.en was true.
  *
  * IO:
  *  - en   : enable counting
  *  - rst  : external reset (synchronous)
  *  - done : output pulse when last bit received (count reached 7)
  */
class Counter extends Module {
  val io = IO(new Bundle {
    val en   = Input(Bool())
    val rst  = Input(Bool())
    val done = Output(Bool())
    val value = Output(UInt(3.W)) // current count (0..7)
  })

  val cnt = RegInit(0.U(3.W))
  io.done := false.B
  io.value := cnt

  when(io.rst) {
    cnt := 0.U
    io.done := false.B
  } .elsewhen(io.en) {
    when(cnt === 7.U) {
      // if this was the 8th bit, assert done and reset counter (next reception will start at 0)
      io.done := true.B
      cnt := 0.U
    } .otherwise {
      cnt := cnt + 1.U
      io.done := false.B
    }
  } .otherwise {
    // hold count when not enabled
    io.done := false.B
  }
}

/** Shift register class
  *
  * Samples serial bits (msb-first) and accumulates them into an 8-bit word.
  *
  * Behavior:
  *  - When io.en is true, on each rising clock the module shifts left by 1 bit and
  *    appends the sampled bit into LSB, i.e. reg := Cat(reg(6,0), io.in)
  *  - This procedure with MSB-first input results in the final register containing
  *    the received byte with correct bit order (MSB in bit 7).
  *
  * IO:
  *  - in   : serial input bit
  *  - en   : sample/shift enable
  *  - rst  : external reset
  *  - out  : 8-bit parallel data
  */
class ShiftRegister extends Module {
  val io = IO(new Bundle {
    val in  = Input(Bool())
    val en  = Input(Bool())
    val rst = Input(Bool())
    val out = Output(UInt(8.W))
  })

  val reg = RegInit(0.U(8.W))

  when(io.rst) {
    reg := 0.U
  } .elsewhen(io.en) {
    // shift left and append sampled bit at LSB
    // Example (4-bit shown): after receiving bits b3 b2 b1 b0 (msb-first),
    // successive operations produce final reg = b3 b2 b1 b0
    reg := Cat(reg(6, 0), io.in)
  } .otherwise {
    reg := reg // hold
  }

  io.out := reg
}

/** Top-level ReadSerial module
  *
  * IO:
  *  - rxd   : serial input line (1 = idle)
  *  - rst   : synchronous external reset (when high aborts reception)
  *  - data  : 8-bit parallel output
  *  - valid : one-cycle pulse when data is valid (new byte ready)
  */
class ReadSerial extends Module {
  val io = IO(new Bundle {
    val rxd   = Input(Bool())
    val rst   = Input(Bool())
    val data  = Output(UInt(8.W))
    val valid = Output(Bool())
  })

  // instantiate submodules
  val ctrl = Module(new Controller)
  val cnt  = Module(new Counter)
  val shf  = Module(new ShiftRegister)

  // connect reset and rxd
  ctrl.io.rxd := io.rxd
  ctrl.io.rst := io.rst

  // controller drives enable for counter and shift register
  // Note: controller also receives done from counter
  cnt.io.en := ctrl.io.en
  cnt.io.rst := io.rst
  ctrl.io.done := cnt.io.done

  // shift register connections
  shf.io.in := io.rxd
  shf.io.en := ctrl.io.en
  shf.io.rst := io.rst

  // top-level outputs
  io.data := shf.io.out
  io.valid := ctrl.io.valid
}
