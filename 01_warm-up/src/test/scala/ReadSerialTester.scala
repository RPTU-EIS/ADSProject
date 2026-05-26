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

        /*dut.io.rxd.poke(...)
         *dut.clock.step(...)
         *dut.io.valid.expect(...)
         *dut.io.data.expect("b11111111".U)
         *...
         *TODO: Add your testcases here
         */
      //Initialize
      dut.io.rxd.poke(1.U)
      dut.io.reset_n.poke(1.U)
      dut.clock.step(1)
      dut.io.reset_n.poke(0.U)

      dut.clock.step(2)

      // Test byte 0x00 (all 0s)
      sendByte(dut, 0x0)
      dut.io.valid.expect(1.U)
      dut.io.data.expect(0x0.U)
      dut.clock.step(1)
      dut.io.valid.expect(0.U)

      dut.clock.step(2)

      // Test 0xFF (all ones)
      sendByte(dut, 0xff)
      dut.io.valid.expect(1.U)
      dut.io.data.expect(0xff.U)

      // Test transmission of 00110100
      dut.clock.step(2)
      dut.io.valid.expect(0.U)
      sendByte(dut, 0x34)
      dut.io.valid.expect(1.U)
      dut.io.data.expect(0x34.U)

      dut.clock.step(2)

      //Test reset
      sendBit(dut, 0.U)
      sendBit(dut, 1.U)
      sendBit(dut, 0.U)
      sendBit(dut, 1.U)
      dut.io.reset_n.poke(1.U)    //set reset
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.reset_n.poke(0.U)
      dut.clock.step(1)

      //Test after reset
      dut.io.valid.expect(0.U)
      dut.clock.step(1)
      sendByte(dut, 0x0f)
      dut.io.valid.expect(1.U)
      dut.io.data.expect(0x0f.U)

      dut.clock.step(2)

    }
  }

}

