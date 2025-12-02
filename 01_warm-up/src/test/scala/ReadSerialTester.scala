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

  "ReadSerial" should "work" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

        /*dut.io.rxd.poke(...)
         *dut.clock.step(...)
         *dut.io.valid.expect(...)
         *dut.io.data.expect("b11111111".U) 
         *...
         *TODO: Add your testcases here
         */

      def transmitByte(data : Int): Unit=
        {
          dut.io.rxd.poke(false.B) //start bit =0
          dut.clock.step(1)

          for(i<- 7 to 0 by -1)
            {
              val bit = (data >> i) & 1
              dut.io.rxd.poke(bit.B)
              dut.clock.step(1)
            }

          dut.io.valid.expect(true.B)
          dut.io.data.expect(data.U)
        }

      //test-1 - Random samples
      dut.io.rxd.poke(true.B)
      dut.clock.step(2)

      println("Test#1--> Sending 10100101\n")
      transmitByte(0xA5)

      dut.io.rxd.poke(true.B) //return to idle->set rxd to high
      dut.clock.step(1)

      dut.io.valid.expect(false.B)
      dut.clock.step(2)

      //test-2 - All 1's
      println("Test#2--> Sending 11111111\n")
      transmitByte(0xFF)

      dut.io.rxd.poke(true.B) //return to idle
      dut.clock.step(2)

      //test-3 - All 0's
      println("Test#3--> Sending 00000000\n")
      transmitByte(0x00)

      dut.io.rxd.poke(true.B) //return to idle
      dut.clock.step(2)
        }
    } 
}

