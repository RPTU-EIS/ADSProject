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

          // 000 00 a0 b0 ci0 s0 co0
          dut.io.a.poke(0.U)
          dut.io.b.poke(0.U)
          dut.io.ci.poke(0.U)
          dut.clock.step(1)
          dut.io.s.expect(0.U)
          dut.io.co.expect(0.U)

          // 001 10 a0 b0 ci1 s1 co0
          dut.io.a.poke(0.U)
          dut.io.b.poke(0.U)
          dut.io.ci.poke(1.U)
          dut.clock.step(1)
          dut.io.s.expect(1.U)
          dut.io.co.expect(0.U)

          // 010 10 a0 b1 ci0 s1 co0
          dut.io.a.poke(0.U)
          dut.io.b.poke(1.U)
          dut.io.ci.poke(0.U)
          dut.clock.step(1)
          dut.io.s.expect(1.U)
          dut.io.co.expect(0.U)

          // 011 01 a0 b1 ci1 s0 co1
          dut.io.a.poke(0.U)
          dut.io.b.poke(1.U)
          dut.io.ci.poke(1.U)
          dut.clock.step(1)
          dut.io.s.expect(0.U)
          dut.io.co.expect(1.U)

          // 100 10 a1 b0 ci0 s1 co0
          dut.io.a.poke(1.U)
          dut.io.b.poke(0.U)
          dut.io.ci.poke(0.U)
          dut.clock.step(1)
          dut.io.s.expect(1.U)
          dut.io.co.expect(0.U)

          // 101 01 a1 b0 ci1 s0 co1
          dut.io.a.poke(1.U)
          dut.io.b.poke(0.U)
          dut.io.ci.poke(1.U)
          dut.clock.step(1)
          dut.io.s.expect(0.U)
          dut.io.co.expect(1.U)

          // 110 01 a1 b1 ci0 s0 co1
          dut.io.a.poke(1.U)
          dut.io.b.poke(1.U)
          dut.io.ci.poke(0.U)
          dut.clock.step(1)
          dut.io.s.expect(0.U)
          dut.io.co.expect(1.U)

          // 111 11 a1 b0 ci1 s1 co1
          dut.io.a.poke(1.U)
          dut.io.b.poke(1.U)
          dut.io.ci.poke(1.U)
          dut.clock.step(1)
          dut.io.s.expect(1.U)
          dut.io.co.expect(2.U)

        }
    }
}

