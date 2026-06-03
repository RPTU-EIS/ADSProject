// ADS I Class Project
// Chisel Introduction

package readserial // This tester belongs to the readserial package.

import chisel3._ // Import Chisel data types such as UInt and Bool.
import chiseltest._ // Import ChiselTest functions such as poke, expect, and clock.step.
import org.scalatest.flatspec.AnyFlatSpec // Import the ScalaTest style used by this project.

// This tester checks that ReadSerial receives bytes in the required serial format.
class ReadSerialTester extends AnyFlatSpec with ChiselScalatestTester { // Define the test class.

  "ReadSerial" should "work" in { // Name the behavior that is being tested.
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut => // Create the device under test.

      def sendByte(value: Int): Unit = { // Helper function to send one byte over rxd.
        dut.io.rxd.poke(0.U) // Send the start bit, which is 0.
        dut.clock.step(1) // Advance one clock cycle.

        for (i <- 7 to 0 by -1) { // Send the 8 data bits, most-significant bit first.
          val bit = (value >> i) & 0x1 // Extract bit i from the byte value.
          dut.io.rxd.poke(bit.U) // Drive the extracted bit onto rxd.
          dut.clock.step(1) // Advance one clock cycle for each received bit.
        } // End of bit loop.
      } // End of sendByte helper.

      dut.io.rxd.poke(1.U) // Put the serial line in idle state.
      dut.clock.step(2) // Wait two cycles to make sure the receiver is idle.
      dut.io.valid.expect(false.B) // valid must be false while idle.

      sendByte(0xFF) // Send byte 11111111.
      dut.io.valid.expect(true.B) // After the byte, valid must be true for one cycle.
      dut.io.data.expect("hFF".U) // The received data must be 0xFF.
      dut.io.rxd.poke(1.U) // Return the line to idle.
      dut.clock.step(1) // Advance one cycle.
      dut.io.valid.expect(false.B) // valid must go low again after one cycle.

      sendByte(0x00) // Send byte 00000000.
      dut.io.valid.expect(true.B) // valid must be true when the byte is ready.
      dut.io.data.expect("h00".U) // The received data must be 0x00.
      dut.io.rxd.poke(1.U) // Return the line to idle.
      dut.clock.step(1) // Advance one cycle.
      dut.io.valid.expect(false.B) // valid must only last one cycle.

      sendByte(0xA5) // Send byte 10100101 to verify bit order.
      dut.io.valid.expect(true.B) // valid must be true when the byte is ready.
      dut.io.data.expect("hA5".U) // The received data must match 0xA5.
      dut.io.rxd.poke(1.U) // Return the line to idle.
      dut.clock.step(1) // Advance one cycle.
      dut.io.valid.expect(false.B) // valid must return to false.
    } // End of test body.
  } // End of test case.
} // End of ReadSerialTester.
