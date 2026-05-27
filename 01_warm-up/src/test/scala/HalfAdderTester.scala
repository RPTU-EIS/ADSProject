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
      for (a <- 0 to 1){
        for(b <- 0 to  1){
          val sum = a ^ b
          val c_out = (a & b)

          dut.io.a.poke(a.U)
          dut.io.b.poke(b.U)
          dut.clock.step(1)
          dut.io.s.expect(sum.U)
          dut.io.Co.expect(c_out.U)
        }
      }

      /*dut.io.a.poke(...)
       *dut.io.b.poke(...)
       *dut.io.ci.poke(...)
       *dut.io.s.expect(...)
       *dut.io.co.expect(...)
       *...
       *TODO: Insert your test cases
       */

    }
  }
}
