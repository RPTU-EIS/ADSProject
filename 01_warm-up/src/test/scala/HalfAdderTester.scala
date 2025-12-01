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

      dut.io.a.poke(0)
      dut.io.b.poke(0)
      dut.clock.step(1)
      dut.io.s.expect(0)
      dut.io.co.expect(0)

      dut.io.a.poke(1)
      dut.io.b.poke(0)
      dut.clock.step(1)
      dut.io.s.expect(1)
      dut.io.co.expect(0)

      dut.io.a.poke(0)
      dut.io.b.poke(1)
      dut.clock.step(1)
      dut.io.s.expect(1)
      dut.io.co.expect(0)

      dut.io.a.poke(1)
      dut.io.b.poke(1)
      dut.clock.step(1)
      dut.io.s.expect(1)
      dut.io.co.expect(1)

    }
  }
}

