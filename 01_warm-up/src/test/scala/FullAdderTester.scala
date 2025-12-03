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
          val tests =Seq(
          (0,0,0,0,0),
          (0,0,1,1,0),
          (0,1,0,1,0),
          (0,1,1,0,1),
          (1,0,0,1,0),
          (1,0,1,0,1),
          (1,1,0,0,1),
          (1,1,1,1,1)
        )
        for ((a, b,cin, expS, expCo) <- tests) {
          dut.io.a.poke(a.B)
          dut.io.b.poke(b.B)
          dut.io.cin.poke(cin.B)
          dut.io.s.expect(expS.B)
          dut.io.co.expect(expCo.B)
          


        }
    } 
}

}