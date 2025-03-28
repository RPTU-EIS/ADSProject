// ADS I Class Project
// Pipelined RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 12/19/2023 by Tobias Jauch (@tojauch)

package PipelinedRV32I_Tester

import chisel3._
import chiseltest._
import PipelinedRV32I._
import org.scalatest.flatspec.AnyFlatSpec

class PipelinedRISCV32ITest extends AnyFlatSpec with ChiselScalatestTester {

"PipelinedRV32I_Tester" should "work" in {
    test(new PipelinedRV32I("src/test/programs/BinaryFile")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

        /* 
         * TODO: Insert your testcases from the previous assignments and adapt them for the pipelined core
         */
		dut.clock.step(5)
      dut.io.result.expect(10.U)     // addi x1, x0, 10
      dut.clock.step(1) // addi x2, x0, 20
	  dut.io.result.expect(20.U)     // addi x2, x0, 20
      dut.clock.step(1) // addi x3, x0, -5
	  dut.io.result.expect("hfffffffb".U)
      dut.clock.step(1) // addi x4, x0, 15
	  dut.io.result.expect(15.U)
      dut.clock.step(1) // addi x5, x0, 3
	  dut.io.result.expect(3.U)
      dut.clock.step(1) // NOP
	  dut.clock.step(1) // NOP
	  dut.clock.step(1) // NOP
	  dut.clock.step(1) // NOP
	  dut.clock.step(1)
      dut.io.result.expect(10.U) // sub x6, x2, x1
      dut.clock.step(1)
      dut.io.result.expect(120.U) // sll x7, x4, x5
      dut.clock.step(1)
      dut.io.result.expect(1.U) // srl x8, x4, x5
	  dut.clock.step(1) // NOP
	  dut.clock.step(1) // NOP
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
	  dut.clock.step(1) // NOP
	  dut.clock.step(1) // NOP
      dut.clock.step(1)
      dut.io.result.expect(0.U) //sltu x11, x3, x2
      dut.clock.step(1)
           
    }
  }
}


