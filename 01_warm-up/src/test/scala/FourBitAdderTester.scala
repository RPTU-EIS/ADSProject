package adder

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


class FourBitAdderTester extends AnyFlatSpec with ChiselScalatestTester {

  "4-bit Adder" should "work" in {
    test(new FourBitAdder).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      // Test all combinations of 4-bit inputs (0-15)
      for (a <- 0 to 15) {
        for (b <- 0 to 15) {
          val sum = a - b
          val expectedSum = sum & 0xF        // Lower 4 bits
          val expectedCarry = (sum >> 4) & 1 // Final carry out (co3)

          // Poke individual input bits
          dut.io.a0.poke((a & 1).U)
          dut.io.a1.poke(((a >> 1) & 1).U)
          dut.io.a2.poke(((a >> 2) & 1).U)
          dut.io.a3.poke(((a >> 3) & 1).U)
          dut.io.b0.poke((b & 1).U)
          dut.io.b1.poke(((b >> 1) & 1).U)
          dut.io.b2.poke(((b >> 2) & 1).U)
          dut.io.b3.poke(((b >> 3) & 1).U)

          // Expect individual output bits
          dut.io.s0.expect((expectedSum & 1).U)
          dut.io.s1.expect(((expectedSum >> 1) & 1).U)
          dut.io.s2.expect(((expectedSum >> 2) & 1).U)
          dut.io.s3.expect(((expectedSum >> 3) & 1).U)
          dut.io.co3.expect(expectedCarry.U)
        }
      }
      
    } 
  }
}

