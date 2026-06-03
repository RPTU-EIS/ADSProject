// ADS I Class Project
// Chisel Introduction

package adder // This tester belongs to the adder package.

import chisel3._ // Import Chisel data types such as UInt.
import chiseltest._ // Import ChiselTest functions such as poke and expect.
import org.scalatest.flatspec.AnyFlatSpec // Import the ScalaTest style used by this project.

// This tester checks all possible input combinations of the FullAdder.
class FullAdderTester extends AnyFlatSpec with ChiselScalatestTester { // Define the test class.

  "FullAdder" should "work" in { // Name the behavior that is being tested.
    test(new FullAdder).withAnnotations(Seq(WriteVcdAnnotation)) { dut => // Create the device under test.

      val truthTable = Seq( // Define the full truth table: a, b, ci, expected s, expected co.
        (0, 0, 0, 0, 0), // 0 + 0 + 0 = sum 0, carry 0.
        (0, 0, 1, 1, 0), // 0 + 0 + 1 = sum 1, carry 0.
        (0, 1, 0, 1, 0), // 0 + 1 + 0 = sum 1, carry 0.
        (0, 1, 1, 0, 1), // 0 + 1 + 1 = sum 0, carry 1.
        (1, 0, 0, 1, 0), // 1 + 0 + 0 = sum 1, carry 0.
        (1, 0, 1, 0, 1), // 1 + 0 + 1 = sum 0, carry 1.
        (1, 1, 0, 0, 1), // 1 + 1 + 0 = sum 0, carry 1.
        (1, 1, 1, 1, 1)  // 1 + 1 + 1 = sum 1, carry 1.
      ) // End of truth table.

      for ((a, b, ci, s, co) <- truthTable) { // Iterate over every row of the truth table.
        dut.io.a.poke(a.U) // Apply input a.
        dut.io.b.poke(b.U) // Apply input b.
        dut.io.ci.poke(ci.U) // Apply carry-in.
        dut.io.s.expect(s.U) // Check expected sum.
        dut.io.co.expect(co.U) // Check expected carry-out.
      } // End of loop.
    } // End of test body.
  } // End of test case.
} // End of FullAdderTester.
