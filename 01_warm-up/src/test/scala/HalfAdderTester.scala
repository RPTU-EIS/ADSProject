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

          /*dut.io.a.poke(...)
           *dut.io.b.poke(...)
           *dut.io.ci.poke(...)
           *dut.io.s.expect(...)
           *dut.io.co.expect(...)
           *...
           *TODO: Insert your test cases
           */
                // Test case 1: a=0, b=0 → s=0, co=0
      dut.io.a.poke(0.B)
      dut.io.b.poke(0.B)
      dut.io.s.expect(0.B)
      dut.io.co.expect(0.B)
      dut.clock.step()
      // Test case 2: a=0, b=1 → s=1, co=0
      dut.io.a.poke(0.B)
      dut.io.b.poke(1.B)
      dut.io.s.expect(1.B)
      dut.io.co.expect(0.B)
      dut.clock.step()
      // Test case 3: a=1, b=0 → s=1, co=0
      dut.io.a.poke(1.B)
      dut.io.b.poke(0.B)
      dut.io.s.expect(1.B)
      dut.io.co.expect(0.B)
      dut.clock.step()
      // Test case 4: a=1, b=1 → s=0, co=1
      dut.io.a.poke(1.B)
      dut.io.b.poke(1.B)
      dut.io.s.expect(0.B)
      dut.io.co.expect(1.B)
      dut.clock.step()
        }
    } 
}

