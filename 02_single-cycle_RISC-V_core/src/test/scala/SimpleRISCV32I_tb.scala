// ADS I Class Project
// Single-Cycle RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 05/10/2023 by Tobias Jauch (@tojauch)

package SimpleRV32I_Tester

import chisel3._
import chiseltest._
import SimpleRV32I._
import org.scalatest.flatspec.AnyFlatSpec

class SimpleRISCV32ITest extends AnyFlatSpec with ChiselScalatestTester {

"SimpleRV32I_Tester" should "work" in {
    test(new SimpleRV32I("src/test/programs/BinaryFile")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      dut.clock.setTimeout(0)

      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.clock.step(1)
      dut.io.result.expect(4.U)     // ADDI x1, x0, 4
      dut.clock.step(1)
      dut.io.result.expect(5.U)     // ADDI x2, x0, 5
      dut.clock.step(1)
      dut.io.result.expect(9.U)     // ADD x3, x1, x2


      
        /* 
         * TODO: Add testcases for all R-type instructions in 'BinaryFile' and check the expected results here
         */

      dut.clock.step(1)
      dut.io.result.expect(10.U)     // addi x1, x0, 10
      dut.clock.step(1) // addi x2, x0, 20
      dut.clock.step(1) // addi x3, x0, -5
      dut.clock.step(1) // addi x4, x0, 15
      dut.clock.step(1) // addi x5, x0, 3
      dut.clock.step(1)
      dut.io.result.expect(10.U) // sub x6, x2, x1
      dut.clock.step(1)
      dut.io.result.expect(120.U) // sll x7, x4, x5
      dut.clock.step(1)
      dut.io.result.expect(1.U) // srl x8, x4, x5
      dut.clock.step(1)
      dut.io.result.expect("hffffffff".U) // sra x9, x3, x5 (-1) (0xffffffff) a2's complement
      dut.clock.step(1)
      dut.io.result.expect(1.U) // slt x10, x1, x2
      dut.clock.step(1)
      dut.io.result.expect(3.U) // and x12, x4, x5
      dut.clock.step(1)
      dut.io.result.expect(15.U) // or x13, x4, x5
      dut.clock.step(1)
      dut.io.result.expect(12.U) // xor x14, x4, x5
      dut.clock.step(1)
      dut.io.result.expect(0.U) //sltu x11, x3, x2
      dut.clock.step(1)
      //dut.io.result.expect("hFFFFFFFF".U)
           
    }
  }
}
/*
0x00a00093 // addi x1, x0, 10
0x01400113 // addi x2, x0, 20
0xffb00193 // addi x3, x0, -5
0x00f00213 // addi x4, x0, 15
0x00300293 // addi x5, x0, 3
0x40110333 // sub x6, x2, x1
0x005213b3 // sll x7, x4, x5
0x00525433 // srl x8, x4, x5
0x4051d4b3 // sra x9, x3, x5
0x0020a533 // slt x10, x1, x2
0x00527633 // and x12, x4, x5
0x005266b3 // or x13, x4, x5
0x00524733 // xor x14, x4, x5
0x0021b5b3 // sltu x11, x3, x2

*/