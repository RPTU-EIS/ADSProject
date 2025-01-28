// ADS I Class Project
// Pipelined RISC-V Core with Hazard Detection and Resolution
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 05/21/2024 by Tobias Jauch (@tojauch)

package HazardDetectionRV32I_Tester

import chisel3._
import chiseltest._
import HazardDetectionRV32I._
import org.scalatest.flatspec.AnyFlatSpec

class HazardDetectionRISCV32ITest extends AnyFlatSpec with ChiselScalatestTester {

"HazardDetectionRV32I_Tester" should "work" in {
    test(new HazardDetectionRV32I("src/test/programs/BinaryFile")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      dut.clock.setTimeout(0)

      dut.clock.step(5)             // it is important to wait until the first instruction travelled through the entire pipeline

      dut.io.result.expect(0.U)     // ADDI x0, x0, 0 (f 1CLK)
      dut.clock.step(1)
      dut.io.result.expect(4.U)     // ADDI x1, x0, 4 (F 2CLK)
      dut.clock.step(1)
      dut.io.result.expect(5.U)     // ADDI x2, x0, 5 (F 3CLK)
      dut.clock.step(1)
      dut.io.result.expect(9.U)     // ADD x3, x1, x2 (F 4CLK)
      dut.clock.step(1)
      dut.io.result.expect(2047.U)  // ADDI x4, x0, 2047 (F 5CLK)
      dut.clock.step(1)
      dut.io.result.expect(16.U)    // ADDI x5, x0, 16 (F 6CLK)
      dut.clock.step(1)
      dut.io.result.expect(2031.U)  // SUB x6, x4, x5 (F 7 CLK)
      dut.clock.step(1)
      dut.io.result.expect(2022.U)  // XOR x7, x6, x3 (F 8 CLK)
      dut.clock.step(1)
      dut.io.result.expect(2047.U)  // OR x8, x6, x5
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // AND x9, x6, x5
      dut.clock.step(1)
      dut.io.result.expect(64704.U) // SLL x10, x7, x2
      dut.clock.step(1)
      dut.io.result.expect(63.U)    // SRL x11, x7, x2
      dut.clock.step(1)
      dut.io.result.expect(63.U)    // SRA x12, x7, x2
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // SLT x13, x4, x4
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // SLT x13, x4, x5
      dut.clock.step(1)
      dut.io.result.expect(1.U)     // SLT x13, x5, x4
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // SLTU x13, x4, x4
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // SLTU x13, x4, x5
      dut.clock.step(1)
      dut.io.result.expect(1.U)     // SLTU x13, x5, x4
      dut.clock.step(1)
      dut.io.result.expect("hFFFFFFFF".U)
           
    }
  }
}


