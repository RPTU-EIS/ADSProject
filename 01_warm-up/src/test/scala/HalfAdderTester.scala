// ADS I Class Project
// Chisel Introduction

package adder // This tester belongs to the adder package.

import chisel3._ // Import Chisel data types such as UInt.
import chiseltest._ // Import ChiselTest functions such as poke and expect.
import org.scalatest.flatspec.AnyFlatSpec // Import the ScalaTest style used by this project.

// This tester checks all possible input combinations of the HalfAdder.
class HalfAdderTester extends AnyFlatSpec with ChiselScalatestTester { // Define the test class.

  "HalfAdder" should "work" in { // Name the behavior that is being tested.
    test(new HalfAdder).withAnnotations(Seq(WriteVcdAnnotation)) { dut => // Create the device under test.

      dut.io.a.poke(0.U) // Apply input a = 0.
      dut.io.b.poke(0.U) // Apply input b = 0.
      dut.io.s.expect(0.U) // Expected sum is 0.
      dut.io.co.expect(0.U) // Expected carry-out is 0.

      dut.io.a.poke(0.U) // Apply input a = 0.
      dut.io.b.poke(1.U) // Apply input b = 1.
      dut.io.s.expect(1.U) // Expected sum is 1.
      dut.io.co.expect(0.U) // Expected carry-out is 0.

      dut.io.a.poke(1.U) // Apply input a = 1.
      dut.io.b.poke(0.U) // Apply input b = 0.
      dut.io.s.expect(1.U) // Expected sum is 1.
      dut.io.co.expect(0.U) // Expected carry-out is 0.

      dut.io.a.poke(1.U) // Apply input a = 1.
      dut.io.b.poke(1.U) // Apply input b = 1.
      dut.io.s.expect(0.U) // Expected sum is 0 because 1 + 1 produces a carry.
      dut.io.co.expect(1.U) // Expected carry-out is 1.
    } // End of test body.
  } // End of test case.
} // End of HalfAdderTester.
