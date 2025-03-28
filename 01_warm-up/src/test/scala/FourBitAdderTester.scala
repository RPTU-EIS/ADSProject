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
  4-bit adder tester
  *
  * Truth tables are not very efficient for testing more complex components, 
  * as they grow exponentially with the number of input bits. Therefore, we 
  * have to think of a more clever way to test the 4-bit adder. To test the 
  * Basic Adder design in our Chisel Introduction, we used loops to generate 
  * a sequence of increasing input values testing the design. To generate 
  * test cases for the 4-bit adder, you should also start by using two nested 
  * loops. To determine the borders of the loop counter, think about the lowest 
  * and the highest unsignes integer that you can represent with four bit. To 
  * test the result produced by your design, think about what happens to the 
  * result in case of an overflow and at which point this can happen. 
  * Hint: It might be helpful to check the expected output behaviour for two 
  * different scenarios with the help of a condition.
  */
class FourBitAdderTester extends AnyFlatSpec with ChiselScalatestTester {

  "4-bit Adder" should "work" in {
    test(new FourBitAdder).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      for (a <- 0 to 15) {
        for (b <- 0 to 15) {
          val expectedSum = a + b
          val expectedCarry = if (expectedSum >= 16) 1.U else 0.U

          dut.io.a.poke(a.U)
          dut.io.b.poke(b.U)
          dut.clock.step(1)

          dut.io.sum.expect((expectedSum & 0xF).U)
          dut.io.co.expect(expectedCarry)
        }
      }
    }
  }
}

