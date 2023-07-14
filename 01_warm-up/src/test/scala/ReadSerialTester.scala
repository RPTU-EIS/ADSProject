// Chisel Introduction
// Chair of Electronic Design Automation, TU Kaiserslautern
// File created on 12/07/2023 by M.Sc. Tobias Jauch (@tojauch)

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
          dut.clock.step(...)
          dut.io.valid.expect(...)
          dut.io.data.expect("b11111111".U) 
          ...

          */
        }
    } 
}

