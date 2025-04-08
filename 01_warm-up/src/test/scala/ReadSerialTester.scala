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
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class ReadSerialTester extends AnyFlatSpec with ChiselScalatestTester {
  def sendBit(dut: ReadSerial, bit: Boolean): Unit = {
    dut.io.rxd.poke(bit.B)
    dut.clock.step(1)
  }

  "ReadSerial" should "correctly receive one byte 0b10101010" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // 1. Initialize
      dut.io.reset_n.poke(true.B)
      dut.io.rxd.poke(true.B) // Idle
      dut.clock.step(2)
      dut.io.valid.expect(false.B)

      // 2. Start bit
      sendBit(dut, false) // Start bit (0)

      // 3. Send data bits (MSB first)
      val testByte = 0xAA.U
      for (i <- 0 until 8) {
        sendBit(dut, testByte(7 - i).asBool) // MSB first
        dut.io.valid.expect(false.B)
      }

      // 4. Verify output
      dut.clock.step(1) // Valid appears next cycle
      dut.io.valid.expect(true.B)
      dut.io.data.expect(0xAA.U) // Now correct!

      // 5. Verify valid drops
      dut.clock.step(1)
      dut.io.valid.expect(false.B)
    }
  }
}
