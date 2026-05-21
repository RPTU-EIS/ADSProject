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

      //function to send a byte
      def sendByte(byte: Int): Unit = {
        dut.io.rxd.poke(0.B)
        dut.clock.step()

        for(i <- 7 to 1 by -1){
          val bit = ((byte >> i) & 1).B
          dut.io.rxd.poke(bit)
          dut.clock.step()
        }
      }

      //Function to wait in Ideal State
      def waitforIdle(cycles: Int = 2): Unit = {
        dut.io.rxd.poke(1.B)
        for(_ <- 0 to cycles ){
          dut.clock.step()
        }
      }

      //Verify the result
      def expectedByte(expectedByte: Int): Unit = {
        dut.io.valid.expect(true.B)
        dut.io.data.expect(expectedByte.U)
        dut.clock.step()
        dut.io.valid.expect(false.B)
      }

      //Initialize
      dut.io.reset.poke(true.B)
      dut.io.rxd.poke(1.B)       //Idle State
      dut.clock.step()
      dut.io.reset.poke(false.B)  //release reset
      dut.clock.step()

      //check valid is still low
      dut.io.valid.expect(false.B)


      //Test 1: Single byte transmission
      sendByte(0xAB)  //10101011 in binary
      expectedByte(0xAB)

      //Test 2: continuous transmission with no idle
      sendByte(0x34) //0110100 in binary
      expectedByte(0x34)

      //Idle period
      waitforIdle(5)
      sendByte(0x12)
      sendByte(0x12)

      //Reset during Transmission
      dut.io.rxd.poke(0.B)    //start bit
      dut.clock.step()

      dut.io.rxd.poke(0.B)    //1st bit
      dut.clock.step()
      dut.io.rxd.poke(1.B)    //2nd bit
      dut.clock.step()
      dut.io.rxd.poke(1.B)    //3rd bit
      dut.clock.step()

      dut.io.reset.poke(true.B)     //reset
      dut.clock.step()
      dut.io.reset.poke(false.B)     //reset
      dut.clock.step()

      dut.io.valid.expect(false.B)

      //Transmission after reset
      waitforIdle(1)
      sendByte(0x55)
      expectedByte(0x55)

      //send all 0s
      waitforIdle(1)
      sendByte(0x00)
      expectedByte(0x00)

      //send all 1s
      waitforIdle(1)
      sendByte(0xFF)
      expectedByte(0xFF)
    }

  }
}

