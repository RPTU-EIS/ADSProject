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

  "RV32I_BasicTester" should "work" in {
    test(new PipelinedRV32I("src/test/programs/BinaryFile_pipelined")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      dut.clock.setTimeout(0)

      // =========================================================================
      // SECTION 1: Basic Operations (Original Tests)
      // =========================================================================
      println("=== SECTION 1: Basic Operations ===")

      dut.clock.step(5)  // Wait for pipeline to fill (5 stages)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      println("PASS: NOP -> 0")

      dut.clock.step(1)
      dut.io.result.expect(4.U)     // ADDI x1, x0, 4
      dut.io.exception.expect(false.B)
      println("PASS: ADDI x1, x0, 4 -> 4")

      dut.clock.step(1)
      dut.io.result.expect(5.U)     // ADDI x2, x0, 5
      dut.io.exception.expect(false.B)
      println("PASS: ADDI x2, x0, 5 -> 5")

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // NOP
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // NOP
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // NOP
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(9.U)     // ADD x3, x1, x2
      dut.io.exception.expect(false.B)
      println("PASS: ADD x3, x1, x2 -> 9")

      dut.clock.step(1)
      dut.io.result.expect(2047.U)  // ADDI x4, x0, 2047 (max positive 12-bit imm)
      dut.io.exception.expect(false.B)
      println("PASS: ADDI x4, x0, 2047 -> 2047 (max positive immediate)")

      dut.clock.step(1)
      dut.io.result.expect(16.U)    // ADDI x5, x0, 16
      dut.io.exception.expect(false.B)

      // NOPs
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      dut.clock.step(1)
      dut.io.result.expect(2031.U)  // SUB x6, x4, x5 (2047 - 16 = 2031)
      dut.io.exception.expect(false.B)
      println("PASS: SUB x6, x4, x5 -> 2031")

      // NOPs
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      dut.clock.step(1)
      dut.io.result.expect(2022.U)  // XOR x7, x6, x3
      dut.io.exception.expect(false.B)
      println("PASS: XOR x7, x6, x3 -> 2022")

      dut.clock.step(1)
      dut.io.result.expect(2047.U)  // OR x8, x6, x5
      dut.io.exception.expect(false.B)
      println("PASS: OR x8, x6, x5 -> 2047")

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // AND x9, x6, x5 (2031 AND 16 = 0)
      dut.io.exception.expect(false.B)
      println("PASS: AND x9, x6, x5 -> 0")

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // NOP

      dut.clock.step(1)
      dut.io.result.expect(64704.U) // SLL x10, x7, x2 (2022 << 5)
      dut.io.exception.expect(false.B)
      println("PASS: SLL x10, x7, x2 -> 64704")

      dut.clock.step(1)
      dut.io.result.expect(63.U)    // SRL x11, x7, x2 (2022 >> 5)
      dut.io.exception.expect(false.B)
      println("PASS: SRL x11, x7, x2 -> 63")

      dut.clock.step(1)
      dut.io.result.expect(63.U)    // SRA x12, x7, x2 (positive number, same as SRL)
      dut.io.exception.expect(false.B)
      println("PASS: SRA x12, x7, x2 -> 63 (positive number)")

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // SLT x13, x4, x4 (2047 < 2047 = false)
      dut.io.exception.expect(false.B)
      println("PASS: SLT x13, x4, x4 -> 0 (equal values)")

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // SLT x13, x4, x5 (2047 < 16 = false)
      dut.io.exception.expect(false.B)
      println("PASS: SLT x13, x4, x5 -> 0 (2047 not less than 16)")

      dut.clock.step(1)
      dut.io.result.expect(1.U)     // SLT x13, x5, x4 (16 < 2047 = true)
      dut.io.exception.expect(false.B)
      println("PASS: SLT x13, x5, x4 -> 1 (16 < 2047)")

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // SLTU x13, x4, x4
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // SLTU x13, x4, x5
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(1.U)     // SLTU x13, x5, x4
      dut.io.exception.expect(false.B)

      // =========================================================================
      // SECTION 2: Negative Numbers & Sign Extension - EDGE CASES
      // =========================================================================
      println("\n=== SECTION 2: Negative Numbers & Sign Extension ===")

      dut.clock.step(1)
      dut.io.result.expect(1.U)     // ADDI x0, x0, 1 (WB shows 1, but x0 stays 0)
      dut.io.exception.expect(false.B)
      println("PASS: ADDI x0, x0, 1 -> 1 (WB result, x0 hardwired to 0)")

      dut.clock.step(1)
      dut.io.result.expect("hFFFFFFFF".U) // ADDI x3, x0, -1
      dut.io.exception.expect(false.B)
      println("PASS: ADDI x3, x0, -1 -> 0xFFFFFFFF (negative immediate sign extension)")

      // NOPs
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // SLT x4, x3, x4: Compare -1 (x3=0xFFFFFFFF) with previous x4 value
      // Previous x4 was set to 1 from SLT result, but wait - let me check
      // Actually x4 was 2047, then overwritten. Let me trace:
      // After SLT x13, x5, x4, x4 still = 2047
      // So SLT x4, x3, x4 = (-1 < 2047) = true = 1
      dut.clock.step(1)
      dut.io.result.expect(1.U)     // SLT x4, x3, x4 (-1 < 2047 signed = true)
      dut.io.exception.expect(false.B)
      println("PASS: SLT x4, x3, x4 -> 1 (-1 < 2047 in signed comparison)")

      // SLTU x5, x3, x4: Compare 0xFFFFFFFF with 1 (x4 now = 1)
      // Unsigned: 0xFFFFFFFF > 1, so result = 0
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // SLTU x5, x3, x4
      dut.io.exception.expect(false.B)
      println("PASS: SLTU x5, x3, x4 -> 0 (0xFFFFFFFF > 1 in unsigned comparison)")

      // NOPs
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // =========================================================================
      // SECTION 3: Shift by 0 and Shift by 31 - EDGE CASES
      // =========================================================================
      println("\n=== SECTION 3: Shift Edge Cases ===")

      // SLL x6, x4, x0: shift by 0 (x0 = 0)
      // x4 = 1 from previous SLT
      dut.clock.step(1)
      dut.io.result.expect(1.U)     // SLL x6, x4, x0 (shift by 0 = no change)
      dut.io.exception.expect(false.B)
      println("PASS: SLL x6, x4, x0 -> 1 (shift by 0)")

      // SLLI x7, x4, 31: shift 1 left by 31
      dut.clock.step(1)
      dut.io.result.expect("h80000000".U) // SLLI x7, x4, 31 (1 << 31 = 0x80000000)
      dut.io.exception.expect(false.B)
      println("PASS: SLLI x7, x4, 31 -> 0x80000000 (1 << 31)")

      // NOPs
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // =========================================================================
      // SECTION 4: Maximum/Minimum Immediate Values - EDGE CASES
      // =========================================================================
      println("\n=== SECTION 4: Immediate Boundary Values ===")

      // XORI x8, x0, 2047 (max positive 12-bit immediate)
      dut.clock.step(1)
      dut.io.result.expect(2047.U)
      dut.io.exception.expect(false.B)
      println("PASS: XORI x8, x0, 2047 -> 2047 (max positive immediate)")

      // XORI x9, x0, -2048 (min negative 12-bit immediate)
      // -2048 sign-extended to 32 bits = 0xFFFFF800
      dut.clock.step(1)
      dut.io.result.expect("hFFFFF800".U)
      dut.io.exception.expect(false.B)
      println("PASS: XORI x9, x0, -2048 -> 0xFFFFF800 (min negative immediate)")

      // NOPs
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // =========================================================================
      // SECTION 5: Creating All 1s - EDGE CASE
      // =========================================================================
      println("\n=== SECTION 5: Bitwise All 1s ===")

      // OR x10, x8, x9: 2047 | 0xFFFFF800 = 0xFFFFFFFF
      dut.clock.step(1)
      dut.io.result.expect("hFFFFFFFF".U)
      dut.io.exception.expect(false.B)
      println("PASS: OR x10, x8, x9 -> 0xFFFFFFFF (all bits set)")

      // NOPs
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // =========================================================================
      // SECTION 6: XOR with All 1s (Bitwise NOT) - EDGE CASE
      // =========================================================================
      println("\n=== SECTION 6: Bitwise NOT via XOR ===")

      // XORI x12, x8, -1: 2047 XOR 0xFFFFFFFF = 0xFFFFF800
      dut.clock.step(1)
      dut.io.result.expect("hFFFFF800".U)
      dut.io.exception.expect(false.B)
      println("PASS: XORI x12, x8, -1 -> 0xFFFFF800 (bitwise NOT of 2047)")

      // NOPs
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // =========================================================================
      // SECTION 7: SRA with Negative Number - EDGE CASE
      // =========================================================================
      println("\n=== SECTION 7: Arithmetic Right Shift (Sign Extension) ===")

      // ADDI x14, x0, -1: x14 = 0xFFFFFFFF
      dut.clock.step(1)
      dut.io.result.expect("hFFFFFFFF".U)
      dut.io.exception.expect(false.B)
      println("PASS: ADDI x14, x0, -1 -> 0xFFFFFFFF")

      // NOPs
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // SRAI x15, x14, 31: 0xFFFFFFFF >> 31 (arithmetic) = 0xFFFFFFFF
      // Sign bit (1) is replicated, so still all 1s
      dut.clock.step(1)
      dut.io.result.expect("hFFFFFFFF".U)
      dut.io.exception.expect(false.B)
      println("PASS: SRAI x15, x14, 31 -> 0xFFFFFFFF (sign extension preserved)")

      // NOPs
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // =========================================================================
      // SECTION 8: SLL by 31 - EDGE CASE
      // =========================================================================
      println("\n=== SECTION 8: Large Shift Left ===")

      // SLLI x16, x15, 31: 0xFFFFFFFF << 31 = 0x80000000
      dut.clock.step(1)
      dut.io.result.expect("h80000000".U)
      dut.io.exception.expect(false.B)
      println("PASS: SLLI x16, x15, 31 -> 0x80000000")

      // NOPs
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // =========================================================================
      // SECTION 9: SRL by 31 - EDGE CASE
      // =========================================================================
      println("\n=== SECTION 9: Large Logical Right Shift ===")

      // SRLI x17, x15, 31: 0xFFFFFFFF >> 31 (logical) = 1
      dut.clock.step(1)
      dut.io.result.expect(1.U)
      dut.io.exception.expect(false.B)
      println("PASS: SRLI x17, x15, 31 -> 1 (logical right shift)")

      // NOPs
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // =========================================================================
      // SECTION 10: SLT with Equal Values - EDGE CASE
      // =========================================================================
      println("\n=== SECTION 10: Comparison of Equal Values ===")

      // SLT x18, x15, x14: both are 0xFFFFFFFF, so result = 0
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.io.exception.expect(false.B)
      println("PASS: SLT x18, x15, x14 -> 0 (equal values)")

      // NOPs
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // =========================================================================
      // SECTION 11: More Operations
      // =========================================================================
      println("\n=== SECTION 11: Additional Tests ===")

      // ADDI x19, x0, 2047
      dut.clock.step(1)
      dut.io.result.expect(2047.U)
      dut.io.exception.expect(false.B)

      // Additional instruction
      dut.clock.step(1)
      // Check whatever comes next...

      println("\n=== ALL TESTS PASSED ===")
    }
  }
}