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

        
      /*
       * TODO: Insert your test cases
       */  
        
      for (a <- 0 until 16) {
        for (b <- 0 until 16) {
          val sum = a + b
          val expectedSum = sum & 0xF        // Lower 4 bits
          val expectedCarry = (sum >> 4) & 1 // Final carry out (co3)

          // Poke individual input bits
          dut.io.a0.poke((a & 1).U)
          dut.io.a1.poke(((a >> 1) & 1).U)
          dut.io.a2.poke(((a >> 2) & 1).U)
          dut.io.a3.poke(((a >> 3) & 1).U)

          dut.io.b0.poke((b & 1).U)
          dut.io.b1.poke(((b >> 1) & 1).U)
          dut.io.b2.poke(((b >> 2) & 1).U)
          dut.io.b3.poke(((b >> 3) & 1).U)

          // Expect individual output bits
          dut.io.s0.expect((expectedSum & 1).U)
          dut.io.s1.expect(((expectedSum >> 1) & 1).U)
          dut.io.s2.expect(((expectedSum >> 2) & 1).U)
          dut.io.s3.expect(((expectedSum >> 3) & 1).U)
          dut.io.co3.expect(expectedCarry.U)
        }
      }
    } 
  }
}

