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
        // Initialize
        dut.io.reset_n.poke(false.B)
        dut.io.rxd.poke(true.B)  // Idle = high
        dut.clock.step(5)

        // Release reset
        dut.io.reset_n.poke(true.B)
        dut.clock.step(2)

        // Test: Send 0xAA (10101010) serially
        // Format: start bit (0) + 8 data bits (MSB first) + idle (1)
        
        // Start bit (0)
        dut.io.rxd.poke(false.B)
        dut.clock.step(1)

        // Data bits: 1 0 1 0 1 0 1 0 (0xAA)
        dut.io.rxd.poke(true.B)   // bit 7
        dut.clock.step(1)
        dut.io.rxd.poke(false.B)  // bit 6
        dut.clock.step(1)
        dut.io.rxd.poke(true.B)   // bit 5
        dut.clock.step(1)
        dut.io.rxd.poke(false.B)  // bit 4
        dut.clock.step(1)
        dut.io.rxd.poke(true.B)   // bit 3
        dut.clock.step(1)
        dut.io.rxd.poke(false.B)  // bit 2
        dut.clock.step(1)
        dut.io.rxd.poke(true.B)   // bit 1
        dut.clock.step(1)
        dut.io.rxd.poke(false.B)  // bit 0
        dut.clock.step(1)

        // Check valid signal and data output
        dut.io.valid.expect(true.B)
        dut.io.data.expect(0xAA.U)

        // Go back to idle
        dut.io.rxd.poke(true.B)
        dut.clock.step(1)
        dut.io.valid.expect(false.B)

        // Test another byte: 0x55 (01010101)
        dut.io.rxd.poke(false.B)  // Start bit
        dut.clock.step(1)
        dut.io.rxd.poke(false.B)  // bit 7
        dut.clock.step(1)
        dut.io.rxd.poke(true.B)   // bit 6
        dut.clock.step(1)
        dut.io.rxd.poke(false.B)  // bit 5
        dut.clock.step(1)
        dut.io.rxd.poke(true.B)   // bit 4
        dut.clock.step(1)
        dut.io.rxd.poke(false.B)  // bit 3
        dut.clock.step(1)
        dut.io.rxd.poke(true.B)   // bit 2
        dut.clock.step(1)
        dut.io.rxd.poke(false.B)  // bit 1
        dut.clock.step(1)
        dut.io.rxd.poke(true.B)   // bit 0
        dut.clock.step(1)

        dut.io.valid.expect(true.B)
        dut.io.data.expect(0x55.U)

        dut.io.rxd.poke(true.B)
        dut.clock.step(5)
        }
    } 
}

