// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package readserial

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import scala.util.Random

/** 
  *read serial tester
  */
class ReadSerialTester extends AnyFlatSpec with ChiselScalatestTester {

    val dataWidth = 8
  "ReadSerial" should "work" in {
    test(new ReadSerial(dataWidth = dataWidth)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

        /*dut.io.rxd.poke(...)
         *dut.clock.step(...)
         *dut.io.valid.expect(...)
         *dut.io.data.expect("b11111111".U) 
         *...
         *TODO: Add your testcases here
         */
            val testRepeatCount = 5
            // initialize dut
            dut.io.rxd.poke(0.U)
            dut.io.reset_n.poke(0.U)
            dut.clock.step(1)
            dut.io.reset_n.poke(1.U)

            // test normal behavior
            for ( _ <- 0 to testRepeatCount){

                // wait before send a new packet (may be no wait)
                dut.io.rxd.poke(1.U)
                dut.clock.step(Random.nextInt(3)) //gap between 2 packets
                // input a data packet
                dut.io.rxd.poke(0.U)
                dut.clock.step(1)

                val dataVal = Random.nextInt(255) // generate a random data packet
                println(s"dataVal : $dataVal")
                for(bitIdx <- (dataWidth-1) to 0 by -1){
                    dut.io.rxd.poke((dataVal.U(8.W))(bitIdx))
                    // dut.io.valid.expect(0.U)   
                    dut.clock.step(1)
                }
                // dut.io.valid.expect(1.U)
                // dut.io.data.expect(dataVal.U)
            }
            dut.clock.step(5) // extra time to calculate output
        }
    } 
}

