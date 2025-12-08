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

  def sendBit(dut: ReadSerial, bit: Int): Unit = {
    dut.io.rxd.poke(bit.U)
    dut.clock.step(1)
  }

  def sendByte(dut: ReadSerial, byte: Int): Unit = {
    sendBit(dut, 0)

    for (i <- 7 to 0 by -1) {
      val b = (byte >> i) & 1
      sendBit(dut, b)
    }
  }

  "ReadSerial" should "work" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      dut.io.valid.expect(0.U)
      dut.clock.step(1)
      sendByte(dut, 0x0) // 0b00000000
      dut.io.valid.expect(1.U)
      dut.io.data.expect("b00000000".U)

      dut.io.valid.expect(0.U)
      dut.clock.step(1)
      sendByte(dut, 0xAB) // 0b10101011
      dut.io.valid.expect(1.U)
      dut.io.data.expect("b10101011".U)

    }
  }
}

