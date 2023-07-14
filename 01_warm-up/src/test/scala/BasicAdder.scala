// Chisel Introduction
// Chair of Electronic Design Automation, TU Kaiserslautern
// File created on 18/10/2022 by M.Sc. Tobias Jauch (@tojauch)

package basicadder

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


/** 
  *basic adder tester
  */
class BasicAdderTester extends AnyFlatSpec with ChiselScalatestTester {

  "BasicAdder" should "work" in {
    test(new BasicAdder).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
          for(a <- 0 to 7){
            for(b <- 0 to 7){

              val result = a + b 

              dut.io.a.poke(a.U)        // input a is set to value a 
              dut.io.b.poke(b.U)        // input b is set to value b
              dut.clock.step(1)         // move one clock cycle ahead       
              dut.io.c.expect(result.U) // see if output c matches expected result
           }
         }
    }
  } 
}

