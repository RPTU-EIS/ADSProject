// ADS I Class Project
// Pipelined RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/15/2023 by Tobias Jauch (@tojauch)

package PipelinedRV32I_Tester

import chisel3._
import chiseltest._
import PipelinedRV32I._
import org.scalatest.flatspec.AnyFlatSpec

class PipelinedRISCV32ITest extends AnyFlatSpec with ChiselScalatestTester {

  // ── Helper: step past pipeline fill latency then check a sequence of results ──
  def checkSequence(dut: PipelinedRV32I, expected: Seq[Int]): Unit = {
    dut.clock.step(5)
    dut.io.result.expect(expected(0).U)
    expected.tail.foreach { v =>
      dut.clock.step(1)
      dut.io.result.expect(v.U)
      dut.io.exception.expect(false.B)
    }
  }

"RV32I_BasicTester" should "work" in {
    test(new PipelinedRV32I("src/test/programs/BinaryFile_pipelined")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      dut.clock.setTimeout(0)
      dut.clock.step(5)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(4.U)     // ADDI x1, x0, 4
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(5.U)     // ADDI x2, x0, 5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(9.U)     // ADD x3, x1, x2
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(2047.U)  // ADDI x4, x0, 2047
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(16.U)    // ADDI x5, x0, 16
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(2031.U)  // SUB x6, x4, x5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(2022.U)  // XOR x7, x6, x3
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(2047.U)  // OR x8, x6, x5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // AND x9, x6, x5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(64704.U) // SLL x10, x7, x2
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(63.U)    // SRL x11, x7, x2
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(63.U)    // SRA x12, x7, x2
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // SLT x13, x4, x4
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // SLT x13, x4, x5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(1.U)     // SLT x13, x5, x4
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // SLTU x13, x4, x4
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // SLTU x13, x4, x5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(1.U)     // SLTU x13, x5, x4
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
    }
  }

  // ── Control Hazard Tests (no NOPs — hardware must suppress wrong-path writes) ──

  // Test 1: BEQ not taken — instructions immediately after branch must execute
  "ControlHazard_BEQ_not_taken" should "execute fall-through instructions" in {
    test(new PipelinedRV32I("src/test/programs/BinaryFile_ch_beq_not_taken"))
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)
      // BEQ x1,x2 not taken (10≠20); ALU result = 10+20 = 30 written to "rd" field
      // x3=7 and x4=8 execute, ADD x5=15
      checkSequence(dut, Seq(10, 20, 30, 7, 8, 15))
    }
  }

  // Test 2: BNE not taken — fall-through instructions execute
  "ControlHazard_BNE_not_taken" should "execute fall-through instructions" in {
    test(new PipelinedRV32I("src/test/programs/BinaryFile_ch_bne_not_taken"))
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)
      // BNE x1,x2 not taken (5==5); ALU=10; x3=11, x4=22 execute → ADD=33
      checkSequence(dut, Seq(5, 5, 10, 11, 22, 33))
    }
  }

  // Test 3: BLT taken — signed less-than branch flushes 2 wrong-path instructions
  "ControlHazard_BLT_taken" should "suppress wrong-path register writes" in {
    test(new PipelinedRV32I("src/test/programs/BinaryFile_ch_blt_taken"))
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)
      // BLT x1,x2 taken (3 < 9); ALU=12; wrong-path x10=77,x11=66 suppressed → ADD=0
      checkSequence(dut, Seq(3, 9, 12, 77, 66, 0))
    }
  }

  // Test 4: BGE taken — signed ≥ branch flushes 2 wrong-path instructions
  "ControlHazard_BGE_taken" should "suppress wrong-path register writes" in {
    test(new PipelinedRV32I("src/test/programs/BinaryFile_ch_bge_taken"))
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)
      // BGE x1,x2 taken (9 >= 3); ALU=12; wrong-path x10=33,x11=22 suppressed → ADD=0
      checkSequence(dut, Seq(9, 3, 12, 33, 22, 0))
    }
  }
}
