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
 * read serial tester
 */
class ReadSerialTester extends AnyFlatSpec with ChiselScalatestTester {

  def sendBit(dut: ReadSerial, bit: UInt): Unit = {
    dut.io.rxd.poke(bit)
    dut.clock.step(1)
  }

  def sendByte(dut: ReadSerial, byte: Int): Unit = {
    sendBit(dut, 0.U)

    for (i <- 7 to 0 by -1) {
      val b = (byte >> i) & 1
      sendBit(dut, b.U)
    }
    dut.io.rxd.poke(1.U)
  }

  "ReadSerial" should "work" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      dut.io.rxd.poke(1.U)
      dut.io.reset_n.poke(1.U)
      dut.clock.step(1)
      dut.io.reset_n.poke(0.U)

      dut.clock.step(5)

      sendByte(dut, 0x0)
      dut.io.valid.expect(1.U)
      dut.io.data.expect(0x0.U)
      dut.clock.step(1)
      dut.io.valid.expect(0.U)

      dut.clock.step(5)

      dut.io.valid.expect(0.U)
      dut.clock.step(1)
      sendByte(dut, 0xff)
      dut.io.valid.expect(1.U)
      dut.io.data.expect(0xff.U)

      dut.clock.step(5)

      dut.io.valid.expect(0.U)
      dut.clock.step(1)
      sendByte(dut, 0xc9)
      dut.io.valid.expect(1.U)
      dut.io.data.expect(0xc9.U)

      dut.clock.step(5)

      sendBit(dut, 0.U)
      sendBit(dut, 1.U)
      sendBit(dut, 0.U)
      sendBit(dut, 1.U)
      dut.io.reset_n.poke(1.U)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.reset_n.poke(0.U)
      dut.clock.step(1)

      dut.io.valid.expect(0.U)
      dut.clock.step(1)
      sendByte(dut, 0x0f)
      dut.io.valid.expect(1.U)
      dut.io.data.expect(0x0f.U)

      dut.clock.step(5)

    }
  }
}

