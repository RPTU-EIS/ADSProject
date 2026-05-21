// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package adder

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


/** S
  * Full adder tester
  * Use the truth table from the exercise sheet to test all possible input combinations and the corresponding results exhaustively
  */
class FullAdderTester extends AnyFlatSpec with ChiselScalatestTester {

  "FullAdder" should "work" in {
    test(new FullAdder).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      for(a <- 0 to 1){
        for(b <- 0 to 1){
          for(cin <- 0 to 1){
            dut.io.a.poke(a.U)
            dut.io.b.poke(b.U)
            dut.io.cin.poke(cin.U)

            val expectedSum = ( a ^ b ^ cin) & 1
            val expectedCout = ((a & b) | (a & cin) | (b & cin)) &1

            dut.io.sum.expect(expectedSum.U)
            dut.io.cout.expect(expectedCout.U)
          }
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

