// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package readserial

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


/** 
  *read serial tester
  */
class ReadSerialTester extends AnyFlatSpec with ChiselScalatestTester {

  "ReadSerial" should "work" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

        /*dut.io.rxd.poke(...)
         *dut.clock.step(...)
         *dut.io.valid.expect(...)
         *dut.io.data.expect("b11111111".U) 
         *...
         *TODO: Add your testcases here
         */

      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)

      dut.io.rxd.poke(0.U)
      dut.clock.step(1)

      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.rxd.poke(0.U)
      dut.clock.step(1)
      dut.io.rxd.poke(0.U)
      dut.clock.step(1)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.rxd.poke(0.U)
      dut.clock.step(1)
      dut.io.rxd.poke(0.U)
      dut.clock.step(1)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)

      dut.io.valid.expect(true.B)
      dut.io.data.expect(153.U) //10011001

      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.valid.expect(false.B)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)

      dut.io.rxd.poke(0.U)
      dut.clock.step(1)

      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)
      dut.io.rxd.poke(1.U)
      dut.clock.step(1)

      dut.io.valid.expect(true.B)
      dut.io.data.expect(255.U) //1111 1111
        }
    } 
}

