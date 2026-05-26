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

          /*dut.io.a.poke(...)
           *dut.io.b.poke(...)
           *dut.io.ci.poke(...)
           *dut.io.s.expect(...)
           *dut.io.co.expect(...)
           *...
           *TODO: Insert your test cases
           */
// Test case 1: a=0, b=0, ci=0 → s=0, co=0
      dut.io.a.poke(0.B)
      dut.io.b.poke(0.B)
      dut.io.ci.poke(0.B)
      dut.io.s.expect(0.B)
      dut.io.co.expect(0.B)
      dut.clock.step()

      // Test case 2: a=0, b=0, ci=1 → s=1, co=0
      dut.io.a.poke(0.B)
      dut.io.b.poke(0.B)
      dut.io.ci.poke(1.B)
      dut.io.s.expect(1.B)
      dut.io.co.expect(0.B)
      dut.clock.step()

      // Test case 3: a=0, b=1, ci=0 → s=1, co=0
      dut.io.a.poke(0.B)
      dut.io.b.poke(1.B)
      dut.io.ci.poke(0.B)
      dut.io.s.expect(1.B)
      dut.io.co.expect(0.B)
      dut.clock.step()

      // Test case 4: a=0, b=1, ci=1 → s=0, co=1
      dut.io.a.poke(0.B)
      dut.io.b.poke(1.B)
      dut.io.ci.poke(1.B)
      dut.io.s.expect(0.B)
      dut.io.co.expect(1.B)
      dut.clock.step()

      // Test case 5: a=1, b=0, ci=0 → s=1, co=0
      dut.io.a.poke(1.B)
      dut.io.b.poke(0.B)
      dut.io.ci.poke(0.B)
      dut.io.s.expect(1.B)
      dut.io.co.expect(0.B)
      dut.clock.step()

      // Test case 6: a=1, b=0, ci=1 → s=0, co=1
      dut.io.a.poke(1.B)
      dut.io.b.poke(0.B)
      dut.io.ci.poke(1.B)
      dut.io.s.expect(0.B)
      dut.io.co.expect(1.B)
      dut.clock.step()

      // Test case 7: a=1, b=1, ci=0 → s=0, co=1
      dut.io.a.poke(1.B)
      dut.io.b.poke(1.B)
      dut.io.ci.poke(0.B)
      dut.io.s.expect(0.B)
      dut.io.co.expect(1.B)
      dut.clock.step()

      // Test case 8: a=1, b=1, ci=1 → s=1, co=1
      dut.io.a.poke(1.B)
      dut.io.b.poke(1.B)
      dut.io.ci.poke(1.B)
      dut.io.s.expect(1.B)
      dut.io.co.expect(1.B)
      dut.clock.step()
        }
    } 
}

