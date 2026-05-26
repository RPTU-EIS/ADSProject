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
      // Idle bus = 1
     
