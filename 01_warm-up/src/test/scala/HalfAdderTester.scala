// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package adder

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


class HalfAdderTester extends AnyFlatSpec with ChiselScalatestTester {

  "HalfAdder" should "work" in {
    test(new HalfAdder).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

<<<<<<< HEAD
      // Test case 1: a=0, b=0
      dut.io.a.poke(0.U)
      dut.io.b.poke(0.U)
      dut.io.s.expect(0.U)
      dut.io.co.expect(0.U)

      // Test case 2: a=0, b=1
      dut.io.a.poke(0.U)
      dut.io.b.poke(1.U)
      dut.io.s.expect(1.U)
      dut.io.co.expect(0.U)

      // Test case 3: a=1, b=0
      dut.io.a.poke(1.U)
      dut.io.b.poke(0.U)
      dut.io.s.expect(1.U)
      dut.io.co.expect(0.U)

      // Test case 4: a=1, b=1
      dut.io.a.poke(1.U)
      dut.io.b.poke(1.U)
      dut.io.s.expect(0.U)
      dut.io.co.expect(1.U)

    }
  } 
=======
          /*dut.io.a.poke(...)
           *dut.io.b.poke(...)
           *dut.io.ci.poke(...)
           *dut.io.s.expect(...)
           *dut.io.co.expect(...)
           *...
           *TODO: Insert your test cases
           */
          
          // case 1 a0 b0 s0 c0
          dut.io.a.poke(0.U)
          dut.io.b.poke(0.U)
          dut.clock.step(1)
          dut.io.s.expect(0.U)
          dut.io.co.expect(0.U)
          
          // case 2 a0 b1 s1 c0
          dut.io.a.poke(0.U)
          dut.io.b.poke(1.U)
          dut.clock.step(1)
          dut.io.s.expect(1.U)
          dut.io.co.expect(0.U)

          // case 3 a1 b0 s1 c0
          dut.io.a.poke(1.U)
          dut.io.b.poke(0.U)
          dut.clock.step(1)
          dut.io.s.expect(1.U)
          dut.io.co.expect(0.U)
          
          // case 4 a1 b1 s0 c1
          dut.io.a.poke(1.U)
          dut.io.b.poke(1.U)
          dut.clock.step(1)
          dut.io.s.expect(0.U)
          dut.io.co.expect(1.U)
        }
    } 
>>>>>>> 841f4d3542123fd7143114bf51561e27b55c5ffd
}

