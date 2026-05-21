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
          for(a <- 0 to 1){
            for(b <- 0 to 1){

              val result = a + b
              val sResult = result % 2
              val cResult = result / 2

              dut.io.aInput.poke(a.B)    
              dut.io.bInput.poke(b.B)        
              dut.clock.step(1)            
              dut.io.sOutput.expect(sResult.B)
              dut.io.cOutput.expect(cResult.B)
           }
         }

        }
    } 
}

