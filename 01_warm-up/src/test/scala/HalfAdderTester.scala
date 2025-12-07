// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package adder

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


/** 
  * Half adder tester
  * Use the truth table from the exercise sheet to test all possible input combinations and the corresponding results exhaustively
  */
class HalfAdderTester extends AnyFlatSpec with ChiselScalatestTester {

  "HalfAdder" should "work" in {
    test(new HalfAdder).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Test all 4 possible input combinations
    val testCases = List(
        (false, false, false, false),
        (false, true,  true,  false),
        (true,  false, true,  false),
        (true,  true,  false, true)
      )

      for ((a, b, expectedSum, expectedCarry) <- testCases) {
        dut.io.a.poke(if (a) true.B else false.B)
        dut.io.b.poke(if (b) true.B else false.B)
        dut.clock.step(1)
        dut.io.s.expect(if (expectedSum) true.B else false.B)
        dut.io.co.expect(if (expectedCarry) true.B else false.B)
      }
    }
  }

}