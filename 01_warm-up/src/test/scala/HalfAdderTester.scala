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

      val truthTable = Seq (
        (0,0,0,0),
        (0,1,1,0),
        (1,0,1,0),
        (1,1,0,1)
          )
          for ((input1,input2,expectedSum,expectedCarry) <- truthTable) {
            dut.io.input1.poke(input1.B)
            dut.io.input2.poke(input2.B)
            dut.io.output.expect(expectedSum.B)
            dut.io.co.expect(expectedCarry.B)
          }
        }
    } 
}

