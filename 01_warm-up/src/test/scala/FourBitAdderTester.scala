// ADS I Class Project
// Chisel Introduction

package adder // This tester belongs to the adder package.

import chisel3._ // Import Chisel data types such as UInt.
import chiseltest._ // Import ChiselTest functions such as poke and expect.
import org.scalatest.flatspec.AnyFlatSpec // Import the ScalaTest style used by this project.

// This tester checks all possible 4-bit input combinations of the FourBitAdder.
class FourBitAdderTester extends AnyFlatSpec with ChiselScalatestTester { // Define the test class.

  "4-bit Adder" should "work" in { // Name the behavior that is being tested.
    test(new FourBitAdder).withAnnotations(Seq(WriteVcdAnnotation)) { dut => // Create the device under test.

      for (a <- 0 to 15) { // Iterate over every possible 4-bit value for input a.
        for (b <- 0 to 15) { // Iterate over every possible 4-bit value for input b.
          val sum = a + b // Calculate the expected mathematical sum in Scala.

          dut.io.a.poke(a.U) // Apply input a to the hardware.
          dut.io.b.poke(b.U) // Apply input b to the hardware.

          if (sum > 15) { // If the result does not fit in 4 bits, there is an overflow.
            dut.io.s.expect((sum - 16).U) // The 4-bit sum is the lower 4 bits of the result.
            dut.io.co.expect(1.U) // Carry-out must be 1 when overflow happens.
          } else { // If the result fits in 4 bits, there is no overflow.
            dut.io.s.expect(sum.U) // The sum output must equal the mathematical sum.
            dut.io.co.expect(0.U) // Carry-out must be 0 when there is no overflow.
          } // End of overflow check.
        } // End of b loop.
      } // End of a loop.
    } // End of test body.
  } // End of test case.
} // End of FourBitAdderTester.
