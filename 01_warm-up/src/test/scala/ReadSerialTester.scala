// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package readserial

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


/**
  *read serial tester
  */
class ReadSerialTester extends AnyFlatSpec with ChiselScalatestTester {

  behavior.of("ReadSerial")

  private def sendFrame(dut: ReadSerial, bits: Seq[Int]): Unit = {
    bits.foreach { b =>
      dut.io.rxd.poke(b.B)
      dut.clock.step(1)
    }
  }

  it should "receive a single byte and assert valid for one cycle" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Initialize: idle high
      dut.io.reset_n.poke(true.B)
      dut.io.rxd.poke(true.B)
      dut.clock.step(2)

      // Transmit 0xA5 = 0b1010_0101 (MSB-first)
      // Sequence: start(0) + data bits [1,0,1,0,0,1,0,1]
      val frameA5 = Seq(0, 1, 0, 1, 0, 0, 1, 0, 1)
      sendFrame(dut, frameA5)

      // After the last data bit (8th), valid should be high and data should equal 0xA5
      dut.io.valid.expect(true.B)
      dut.io.data.expect("hA5".U)

      // Next cycle valid should return low
      dut.clock.step(1)
      dut.io.valid.expect(false.B)
    }
  }

  it should "receive back-to-back frames without stop bit" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Init
      dut.io.reset_n.poke(true.B)
      dut.io.rxd.poke(true.B)
      dut.clock.step(2)

      // Frame 1: 0xFF = 1111_1111 (MSB-first)
      val frameFF = Seq(0) ++ Seq.fill(8)(1) // start + 8 ones
      sendFrame(dut, frameFF)
      dut.io.valid.expect(true.B)
      dut.io.data.expect("hFF".U)
      dut.clock.step(1)
      dut.io.valid.expect(false.B)

      // Immediately follow with Frame 2: 0x00 = 0000_0000 (MSB-first)
      val frame00 = Seq(0) ++ Seq.fill(8)(0) // start + 8 zeros
      sendFrame(dut, frame00)
      dut.io.valid.expect(true.B)
      dut.io.data.expect("h00".U)
      dut.clock.step(1)
      dut.io.valid.expect(false.B)
    }
  }

  it should "abort reception when reset_n goes low" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Begin a frame for 0xC3 = 1100_0011 (MSB-first)
      dut.io.reset_n.poke(true.B)
      dut.io.rxd.poke(true.B)
      dut.clock.step(2)

      // Start + first three data bits: 0, 1, 1, 0
      val partial = Seq(0, 1, 1, 0)
      sendFrame(dut, partial)

      // Assert active-low reset_n to abort mid-frame
      dut.io.reset_n.poke(false.B)
      dut.clock.step(1)
      dut.io.reset_n.poke(true.B)
      dut.io.rxd.poke(true.B) // back to idle
      dut.clock.step(1)

      // No valid should have been produced due to abort
      dut.io.valid.expect(false.B)

      // Send a full new frame: 0x3C = 0011_1100 (MSB-first)
      val frame3C = Seq(0, 0, 0, 1, 1, 1, 1, 0, 0)
      sendFrame(dut, frame3C)

      dut.io.valid.expect(true.B)
      dut.io.data.expect("h3C".U)
      dut.clock.step(1)
      dut.io.valid.expect(false.B)
    }
  }
}

