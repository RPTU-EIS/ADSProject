// ADS I Class Project
// Multi-Cycle RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 12/19/2023 by Tobias Jauch (@tojauch)

package MultiCycleRV32I_Tester

import chisel3._
import chiseltest._
import MultiCycleRV32I._
import org.scalatest.flatspec.AnyFlatSpec

class MultiCycleRISCV32ITest extends AnyFlatSpec with ChiselScalatestTester {

"MultiCycleRV32I_Tester" should "work" in {
    test(new MultiCycleRV32I("src/test/programs/BinaryFile")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

        /* 
         * TODO: Insert testcases from assignment 2 and adapt them for the multi-cycle core
         */
      dut.clock.setTimeout(0)
      dut.clock.step(4)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.clock.step(5)
      dut.io.result.expect(4.U)     // ADDI x1, x0, 4
      dut.clock.step(5)
      dut.io.result.expect(5.U)     // ADDI x2, x0, 5
      dut.clock.step(5)
      dut.io.result.expect(9.U)     // ADD x3, x1, x2



      /*
       * TODO: Add testcases for all R-type instructions in 'BinaryFile' and check the expected results here
       */

      dut.clock.step(5)
      dut.io.result.expect(10.U)     // addi x1, x0, 10
      dut.clock.step(5) // addi x2, x0, 20
      dut.clock.step(5) // addi x3, x0, -5
      dut.clock.step(5) // addi x4, x0, 15
      dut.clock.step(5) // addi x5, x0, 3
      dut.clock.step(5)
      dut.io.result.expect(10.U) // sub x6, x2, x1
      dut.clock.step(5)
      dut.io.result.expect(120.U) // sll x7, x4, x5
      dut.clock.step(5)
      dut.io.result.expect(1.U) // srl x8, x4, x5
      dut.clock.step(5)
      dut.io.result.expect("hffffffff".U) // sra x9, x3, x5 (-1) (0xffffffff) a2's complement
      dut.clock.step(5)
      dut.io.result.expect(1.U) // slt x10, x1, x2
      dut.clock.step(5)
      dut.io.result.expect(3.U) // and x12, x4, x5
      dut.clock.step(5)
      dut.io.result.expect(15.U) // or x13, x4, x5
      dut.clock.step(5)
      dut.io.result.expect(12.U) // xor x14, x4, x5
      dut.clock.step(5)
      dut.io.result.expect(0.U) //sltu x11, x3, x2
      dut.clock.step(5)
//      dut.io.result.expect("hFFFFFFFF".U)
    }
  }
}


