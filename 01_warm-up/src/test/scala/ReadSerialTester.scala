// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)
/*
package readserial

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


/** 
  *read serial tester
  */
class ReadSerialTester extends AnyFlatSpec with ChiselScalatestTester {

  "ReadSerial" should "work" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

        /*dut.io.rxd.poke(...)
         *dut.clock.step(...)
         *dut.io.valid.expect(...)
         *dut.io.data.expect("b11111111".U) 
         *...
         *TODO: Add your testcases here
         */


      dut.io.rxd.poke(true.B)

      // Apply reset
      dut.reset.poke(true.B)
      dut.clock.step(2)

      // Release reset
      dut.reset.poke(false.B)
      dut.clock.step(2)

      // ==========================================
      // TRANSMIT BYTE : 10110011
      // MSB FIRST
      // ==========================================

      // Start bit
      dut.io.rxd.poke(false.B)
      dut.clock.step(1)

      // Bit 7
      dut.io.rxd.poke(true.B)
      dut.clock.step(1)

      // Bit 6
      dut.io.rxd.poke(false.B)
      dut.clock.step(1)

      // Bit 5
      dut.io.rxd.poke(true.B)
      dut.clock.step(1)

      // Bit 4
      dut.io.rxd.poke(true.B)
      dut.clock.step(1)

      // Bit 3
      dut.io.rxd.poke(false.B)
      dut.clock.step(1)

      // Bit 2
      dut.io.rxd.poke(false.B)
      dut.clock.step(1)

      // Bit 1
      dut.io.rxd.poke(true.B)
      dut.clock.step(1)

      // Bit 0
      dut.io.rxd.poke(true.B)
      dut.clock.step(1)

     


      // ==========================================
      // CHECK OUTPUTS
      // ==========================================
      
      dut.io.valid.expect(true.B)

      dut.io.data.expect("b10110011".U)

      // Return to idle

      dut.clock.step(1)
      dut.io.valid.expect(false.B)
        }
    } 
}
  */
package readserial

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class ReadSerialTester extends AnyFlatSpec with ChiselScalatestTester {

  behavior of "ReadSerial"

  /**
   * Helper function
   * Sends one complete serial frame:
   *
   * start bit = 0
   * 8 data bits (MSB first)
   */
  def sendByte(dut: ReadSerial, value: Int): Unit = {

    // Start bit
    dut.io.rxd.poke(false.B)
    dut.clock.step()

    // MSB first
    for(i <- 7 to 0 by -1) {
      val bit = ((value >> i) & 1)

      dut.io.rxd.poke((bit == 1).B)
      dut.clock.step()
    }
  }

  it should "stay idle when line is high" in {

    test(new ReadSerial) { dut =>

      dut.io.rxd.poke(true.B)

      for(_ <- 0 until 20) {
        dut.clock.step()

        dut.io.valid.expect(false.B)
        dut.io.data.expect(0.U)
      }
    }
  }

  it should "receive a single byte correctly" in {

    test(new ReadSerial) { dut =>

      dut.io.rxd.poke(true.B)
      dut.clock.step(5)

      sendByte(dut, 0xA5)

      dut.io.valid.expect(true.B)
      dut.io.data.expect("hA5".U)

      dut.clock.step()

      dut.io.valid.expect(false.B)
    }
  }

  it should "receive multiple bytes back to back" in {

    test(new ReadSerial) { dut =>

      sendByte(dut, 0x55)

      dut.io.valid.expect(true.B)
      dut.io.data.expect("h55".U)

      dut.clock.step()

      sendByte(dut, 0xAA)

      dut.io.valid.expect(true.B)
      dut.io.data.expect("hAA".U)

      dut.clock.step()

      sendByte(dut, 0xF0)

      dut.io.valid.expect(true.B)
      dut.io.data.expect("hF0".U)
    }
  }

  it should "generate valid for exactly one cycle" in {

    test(new ReadSerial) { dut =>

      sendByte(dut, 0x3C)

      dut.io.valid.expect(true.B)

      dut.clock.step()

      dut.io.valid.expect(false.B)

      dut.clock.step()

      dut.io.valid.expect(false.B)
    }
  }

  it should "receive all possible patterns" in {

    test(new ReadSerial) { dut =>

      val patterns =
        Seq(
          0x00,
          0xFF,
          0xAA,
          0x55,
          0x0F,
          0xF0
        )

      for(p <- patterns) {

        sendByte(dut, p)

        dut.io.valid.expect(true.B)
        dut.io.data.expect(p.U)

        dut.clock.step()
      }
    }
  }

  it should "handle random data stream" in {

    test(new ReadSerial) { dut =>

      val rand = new scala.util.Random(1234)

      for(_ <- 0 until 100) {

        val value = rand.nextInt(256)

        sendByte(dut, value)

        dut.io.valid.expect(true.B)
        dut.io.data.expect(value.U)

        dut.clock.step()
      }
    }
  }

  it should "recover after reset during reception" in {

    test(new ReadSerial) { dut =>

      dut.io.rxd.poke(false.B)
      dut.clock.step()

      dut.io.rxd.poke(true.B)
      dut.clock.step(3)

      dut.reset.poke(true.B)
      dut.clock.step()

      dut.reset.poke(false.B)
      dut.clock.step()

      sendByte(dut, 0x5A)

      dut.io.valid.expect(true.B)
      dut.io.data.expect("h5A".U)
    }
  }

  it should "receive continuous byte stream" in {

    test(new ReadSerial) { dut =>

      val stream =
        Seq(
          0x12,
          0x34,
          0x56,
          0x78,
          0x9A,
          0xBC,
          0xDE,
          0xF0
        )

      for(byte <- stream) {

        sendByte(dut, byte)

        dut.io.valid.expect(true.B)
        dut.io.data.expect(byte.U)

        dut.clock.step()
      }
    }
  }
}
