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
      // Test case 1: a = 0, b = 0
        dut.io.a.poke(0.U)
        dut.io.b.poke(0.U)
        //dut.clock.step(1)
        dut.io.sum.expect(0.U)    // Sum = 0 ^ 0 = 0
        dut.io.carry.expect(0.U)   // Carry = 0 & 0 = 0

      // Test case 2: a = 0, b = 1
        dut.io.a.poke(0.U)
        dut.io.b.poke(1.U)
        //dut.clock.step(1)
        dut.io.sum.expect(1.U)    // Sum = 0 ^ 1 = 1
        dut.io.carry.expect(0.U)   // Carry = 0 & 1 = 0
       // Test case 3: a = 1, b = 0
        dut.io.a.poke(1.U)
        dut.io.b.poke(0.U)
        //dut.clock.step(1)
        dut.io.sum.expect(1.U)    // Sum = 1 ^ 0 = 1
        dut.io.carry.expect(0.U)   // Carry = 1 & 0 = 0

      // Test case 4: a = 1, b = 1
        dut.io.a.poke(1.U)
        dut.io.b.poke(1.U)
        //dut.clock.step(1)
        dut.io.sum.expect(0.U)    // Sum = 1 ^ 1 = 0
        dut.io.carry.expect(1.U)   // Carry = 1 & 1 = 1
        }
    } 
}

