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
       for (a <- 0 until 16) {
        for (b <- 0 until 16) {

            // poke inputs (4-bit UInt)
          dut.io.a.poke(a.U)
          dut.io.b.poke(b.U)

          // expected values
          val full = a + b
          val expectedSum = (full & 0xF)   // lower 4 bits
          val expectedCo  = (full >> 4) & 0x1

          // assertions with helpful messages
          dut.io.sum.expect(expectedSum.U, s"sum mismatch for a=$a b=$b")
          dut.io.co.expect((expectedCo == 1).B, s"carry mismatch for a=$a b=$b")
        }
      }
    }
  }
        
      /*
       * TODO: Insert your test cases
       */
      for(a<- 0 to 15)
        {
          for(b<- 0 to 15)
            {
              val result = a+b
              var sum = 0
              var carry = 0

              if(result > 15)
                {
                  sum = result-16 //Wrap around the result
                  carry = 1
                }
              else
                {
                  sum = result
                  carry = 0
                }

              dut.io.a.poke(a.U)
              dut.io.b.poke(b.U)

              dut.clock.step(1)

              println(s"Testing a=$a, b=$b -> expected sum=$sum, carry=$carry, got sum=${dut.io.s.peek().litValue}, carry=${dut.io.co.peek().litValue}")

              dut.io.s.expect(sum.U)
              dut.io.co.expect(carry.U)
            }
        }
      
    } 
  


