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

          /*dut.io.a.poke(...)
           *dut.io.b.poke(...)
           *dut.io.ci.poke(...)
           *dut.io.s.expect(...)
           *dut.io.co.expect(...)
           *...
           *TODO: Insert your test cases
           */

            // for (a <- 0 to 1){
            //     for (b <- 0 to 1){
            //         for (ci <- 0 to 1){
            //             val result = a+b+ci

            //             dut.io.a.poke(a.U) // input a is set to value a 
            //             dut.io.b.poke(b.U) // input b is set to value b
            //             dut.io.ci.poke(ci.U) // input ci is set to value ci
            //             dut.clock.step(1) // move one clock cycle ahead
            //             dut.io.s.expect(result.U(0))  // see if output s matches expected result's S bit
            //             dut.io.co.expect(result.U(1))  // see if output s matches expected result's Co bit
            //         }
            //     }
            // }

            dut.io.a.poke(0.U)
            dut.io.b.poke(0.U)
            dut.io.ci.poke(0.U)
            dut.io.s.expect(0.U)
            dut.io.co.expect(0.U)
            
            dut.clock.step()
            dut.io.a.poke(0.U)
            dut.io.b.poke(0.U)
            dut.io.ci.poke(1.U)
            dut.io.s.expect(1.U)
            dut.io.co.expect(0.U)
            
            dut.clock.step()
            dut.io.a.poke(0.U)
            dut.io.b.poke(1.U)
            dut.io.ci.poke(0.U)
            dut.io.s.expect(1.U)
            dut.io.co.expect(0.U)
            
            dut.clock.step()
            dut.io.a.poke(0.U)
            dut.io.b.poke(1.U)
            dut.io.ci.poke(1.U)
            dut.io.s.expect(0.U)
            dut.io.co.expect(1.U)
            
            dut.clock.step()
            dut.io.a.poke(1.U)
            dut.io.b.poke(0.U)
            dut.io.ci.poke(0.U)
            dut.io.s.expect(1.U)
            dut.io.co.expect(0.U)
            
            dut.clock.step()
            dut.io.a.poke(1.U)
            dut.io.b.poke(0.U)
            dut.io.ci.poke(1.U)
            dut.io.s.expect(0.U)
            dut.io.co.expect(1.U)
            
            dut.clock.step()
            dut.io.a.poke(1.U)
            dut.io.b.poke(1.U)
            dut.io.ci.poke(0.U)
            dut.io.s.expect(0.U)
            dut.io.co.expect(1.U)
            
            dut.clock.step()
            dut.io.a.poke(1.U)
            dut.io.b.poke(1.U)
            dut.io.ci.poke(1.U)
            dut.io.s.expect(1.U)
            dut.io.co.expect(1.U)
        }
    } 
}

