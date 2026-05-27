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

    val seq = "10010101"
    
    dut.io.rxd.poke(0.U)
    dut.clock.step(1)

    for (i <- 0 until 8) {
    
      val bit = seq(i).asDigit
      dut.io.rxd.poke(bit.U)

      dut.clock.step(1)

      println(
        s"cycle=$i bit=$bit " +
        s"co=${dut.io.debug_cnt_s.peek().litValue} " +
        s"ps=${dut.io.debug_ps.peek().litValue} " +
        s"count=${dut.io.debug_count.peek().litValue} " +
        s"data=${dut.io.data.peek().litValue.toString(2)} " +
        s"valid=${dut.io.valid.peek().litValue}"
      )
    }

    dut.io.data.expect(BigInt(seq, 2).U)
    dut.io.valid.expect(1.U)
  }
}
}

