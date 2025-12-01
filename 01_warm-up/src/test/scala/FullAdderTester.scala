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
 * Full adder tester
 * Use the truth table from the exercise sheet to test all possible input combinations and the corresponding results exhaustively
 */
class FullAdderTester extends AnyFlatSpec with ChiselScalatestTester {

  "FullAdder" should "work" in {
    test(new FullAdder).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      dut.io.a.poke(0.B)
      dut.io.b.poke(0.B)
      dut.io.c_i.poke(0.B)
      dut.clock.step(1)
      dut.io.s.expect(0.B)
      dut.io.c_o.expect(0.B)

      dut.io.a.poke(0.B)
      dut.io.b.poke(0.B)
      dut.io.c_i.poke(1.B)
      dut.clock.step(1)
      dut.io.s.expect(1.B)
      dut.io.c_o.expect(0.B)

      dut.io.a.poke(0.B)
      dut.io.b.poke(1.B)
      dut.io.c_i.poke(0.B)
      dut.clock.step(1)
      dut.io.s.expect(1.B)
      dut.io.c_o.expect(0.B)

      dut.io.a.poke(0.B)
      dut.io.b.poke(1.B)
      dut.io.c_i.poke(1.B)
      dut.clock.step(1)
      dut.io.s.expect(0.B)
      dut.io.c_o.expect(1.B)

      dut.io.a.poke(1.B)
      dut.io.b.poke(0.B)
      dut.io.c_i.poke(0.B)
      dut.clock.step(1)
      dut.io.s.expect(1.B)
      dut.io.c_o.expect(0.B)

      dut.io.a.poke(1.B)
      dut.io.b.poke(0.B)
      dut.io.c_i.poke(1.B)
      dut.clock.step(1)
      dut.io.s.expect(0.B)
      dut.io.c_o.expect(1.B)

      dut.io.a.poke(1.B)
      dut.io.b.poke(1.B)
      dut.io.c_i.poke(0.B)
      dut.clock.step(1)
      dut.io.s.expect(0.B)
      dut.io.c_o.expect(1.B)

      dut.io.a.poke(1.B)
      dut.io.b.poke(1.B)
      dut.io.c_i.poke(1.B)
      dut.clock.step(1)
      dut.io.s.expect(1.B)
      dut.io.c_o.expect(1.B)
    }
  }
}

