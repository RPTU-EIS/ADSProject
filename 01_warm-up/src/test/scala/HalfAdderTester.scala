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

      // Case 1: a = 0, b = 0 => sum = 0, carry-out = 0
      dut.io.a.poke(0.U)
      dut.io.b.poke(0.U)
      dut.io.s.expect(0.U)
      dut.io.co.expect(0.U)

      // Case 2: a = 0, b = 1 => sum = 1, carry-out = 0
      dut.io.a.poke(0.U)
      dut.io.b.poke(1.U)
      dut.io.s.expect(1.U)
      dut.io.co.expect(0.U)

      // Case 3: a = 1, b = 0 => sum = 1, carry-out = 0
      dut.io.a.poke(1.U)
      dut.io.b.poke(0.U)
      dut.io.s.expect(1.U)
      dut.io.co.expect(0.U)

      // Case 4: a = 1, b = 1 => sum = 0, carry-out = 1
      dut.io.a.poke(1.U)
      dut.io.b.poke(1.U)
      dut.io.s.expect(0.U)
      dut.io.co.expect(1.U)

        }
    } 
}

