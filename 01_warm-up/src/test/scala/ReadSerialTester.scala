// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package readserial

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


class ReadSerialTester extends AnyFlatSpec with ChiselScalatestTester {

  // Helper: send one full transmission (start bit + 8 data bits).
  // After this call the FSM is in sDone and valid=1 (not yet stepped).
  def sendByte(dut: ReadSerial, byte: Int): Unit = {
    dut.io.rxd.poke(0.U)        // start bit
    dut.clock.step(1)
    for (i <- 7 to 0 by -1) {
      val bit = (byte >> i) & 1
      dut.io.rxd.poke(bit.U)
      dut.clock.step(1)
    }
  }

  // Helper: apply reset then release
  def doReset(dut: ReadSerial): Unit = {
    dut.io.reset_n.poke(0.U)
    dut.io.rxd.poke(1.U)
    dut.clock.step(2)
    dut.io.reset_n.poke(1.U)
    dut.clock.step(1)
  }

  // -------------------------------------------------------
  // Test 1: Basic transmission — 0xAA (10101010)
  // -------------------------------------------------------
  "ReadSerial" should "correctly receive 0xAA" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      doReset(dut)
      sendByte(dut, 0xAA)
      dut.io.valid.expect(1.U)
      dut.io.data.expect(0xAA.U)
      dut.clock.step(1)
      dut.io.valid.expect(0.U)
    }
  }

  // -------------------------------------------------------
  // Test 2: All zeros — 0x00
  // -------------------------------------------------------
  "ReadSerial" should "correctly receive 0x00" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      doReset(dut)
      sendByte(dut, 0x00)
      dut.io.valid.expect(1.U)
      dut.io.data.expect(0x00.U)
      dut.clock.step(1)
      dut.io.valid.expect(0.U)
    }
  }

  // -------------------------------------------------------
  // Test 3: All ones — 0xFF
  // -------------------------------------------------------
  "ReadSerial" should "correctly receive 0xFF" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      doReset(dut)
      sendByte(dut, 0xFF)
      dut.io.valid.expect(1.U)
      dut.io.data.expect(0xFF.U)
      dut.clock.step(1)
      dut.io.valid.expect(0.U)
    }
  }

  // -------------------------------------------------------
  // Test 4: 0x55 (01010101)
  // -------------------------------------------------------
  "ReadSerial" should "correctly receive 0x55" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      doReset(dut)
      sendByte(dut, 0x55)
      dut.io.valid.expect(1.U)
      dut.io.data.expect(0x55.U)
      dut.clock.step(1)
      dut.io.valid.expect(0.U)
    }
  }

  // -------------------------------------------------------
  // Test 5: valid is only high for exactly ONE clock cycle
  // -------------------------------------------------------
  "ReadSerial" should "assert valid for exactly one cycle" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      doReset(dut)
      sendByte(dut, 0xAB)
      dut.io.valid.expect(1.U)
      dut.clock.step(1)
      dut.io.valid.expect(0.U)
      dut.clock.step(1)
      dut.io.valid.expect(0.U)
    }
  }

  // -------------------------------------------------------
  // Test 6: Two back-to-back transmissions (no idle gap).
  // After checking valid=1 we must step once (sDone -> sIdle)
  // before the next start bit, otherwise the FSM misses it.
  // -------------------------------------------------------
  "ReadSerial" should "handle two consecutive transmissions" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      doReset(dut)

      // First byte
      sendByte(dut, 0xAA)
      dut.io.valid.expect(1.U)
      dut.io.data.expect(0xAA.U)

      // Step through sDone so FSM reaches sIdle before next start bit
      dut.clock.step(1)
      dut.io.valid.expect(0.U)

      // Second byte
      sendByte(dut, 0x55)
      dut.io.valid.expect(1.U)
      dut.io.data.expect(0x55.U)
      dut.clock.step(1)
      dut.io.valid.expect(0.U)
    }
  }

  // -------------------------------------------------------
  // Test 7: Idle gap between two transmissions
  // -------------------------------------------------------
  "ReadSerial" should "handle idle gap between transmissions" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      doReset(dut)

      sendByte(dut, 0x12)
      dut.io.valid.expect(1.U)
      dut.io.data.expect(0x12.U)
      dut.clock.step(1)
      dut.io.valid.expect(0.U)

      // Idle
      dut.io.rxd.poke(1.U)
      dut.clock.step(5)
      dut.io.valid.expect(0.U)

      sendByte(dut, 0x34)
      dut.io.valid.expect(1.U)
      dut.io.data.expect(0x34.U)
      dut.clock.step(1)
      dut.io.valid.expect(0.U)
    }
  }

  // -------------------------------------------------------
  // Test 8: Reset during idle — receiver recovers correctly
  // -------------------------------------------------------
  "ReadSerial" should "recover after reset during idle" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      doReset(dut)

      dut.io.rxd.poke(1.U)
      dut.clock.step(4)

      doReset(dut)

      sendByte(dut, 0xBE)
      dut.io.valid.expect(1.U)
      dut.io.data.expect(0xBE.U)
      dut.clock.step(1)
      dut.io.valid.expect(0.U)
    }
  }

  // -------------------------------------------------------
  // Test 9: Reset mid-transmission — aborts and restarts cleanly.
  // Manually send a few bits, then reset. Step enough cycles after
  // reset so the counter is fully cleared before the next sendByte.
  // -------------------------------------------------------
  "ReadSerial" should "abort transmission on reset and restart cleanly" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      doReset(dut)

      // Partial transmission — start bit + 4 data bits
      dut.io.rxd.poke(0.U)   // start bit
      dut.clock.step(1)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.rxd.poke(0.U)
      dut.clock.step(1)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.rxd.poke(0.U)
      dut.clock.step(1)

      // Reset mid-way
      dut.io.reset_n.poke(0.U)
      dut.io.rxd.poke(1.U)
      dut.clock.step(2)
      dut.io.valid.expect(0.U)

      // Release reset and wait for FSM/counter to settle
      dut.io.reset_n.poke(1.U)
      dut.clock.step(2)

      // Clean full transmission
      sendByte(dut, 0xF0)
      dut.io.valid.expect(1.U)
      dut.io.data.expect(0xF0.U)
      dut.clock.step(1)
      dut.io.valid.expect(0.U)
    }
  }

  // -------------------------------------------------------
  // Test 10: No spurious valid — line stays high, valid never fires
  // -------------------------------------------------------
  "ReadSerial" should "not assert valid while line is idle" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      doReset(dut)
      dut.io.rxd.poke(1.U)
      for (_ <- 0 until 20) {
        dut.clock.step(1)
        dut.io.valid.expect(0.U)
      }
    }
  }

}