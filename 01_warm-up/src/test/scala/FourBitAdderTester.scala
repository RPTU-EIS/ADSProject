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
    test(new FourBitAdder).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
      for (a <- 0 until 16) {
        for (b <- 0 until 16) {
          val aBits = (0 until 4).map(i => (a >> i) & 1)
          val bBits = (0 until 4).map(i => (b >> i) & 1)
          val result = a + b

          c.io.a0.poke(aBits(0).U)
          c.io.a1.poke(aBits(1).U)
          c.io.a2.poke(aBits(2).U)
          c.io.a3.poke(aBits(3).U)

          c.io.b0.poke(bBits(0).U)
          c.io.b1.poke(bBits(1).U)
          c.io.b2.poke(bBits(2).U)
          c.io.b3.poke(bBits(3).U)

          c.io.s0.expect((result >> 0 & 1).U)
          c.io.s1.expect((result >> 1 & 1).U)
          c.io.s2.expect((result >> 2 & 1).U)
          c.io.s3.expect((result >> 3 & 1).U)
          c.io.co3.expect((result >> 4 & 1).U)
            }
          }
        }
       }
    }
