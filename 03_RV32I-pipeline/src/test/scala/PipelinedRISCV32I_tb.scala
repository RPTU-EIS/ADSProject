// ADS I Class Project
// Pipelined RISC-V Core — Combined Testbench
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// Assignment 04: Old tests (NOP-padded) + New forwarding hazard tests

package PipelinedRV32I_Tester

import chisel3._
import chiseltest._
import PipelinedRV32I._
import org.scalatest.flatspec.AnyFlatSpec

class PipelinedRISCV32ITest extends AnyFlatSpec with ChiselScalatestTester {

  "RV32I_Combined_Tester" should "pass old NOP-padded tests and new forwarding tests" in {
    test(new PipelinedRV32I("src/test/programs/BinaryFile_forwarding")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      dut.clock.setTimeout(0)

      // *********************************************************************
      // PART A: OLD TESTS (NOP-padded, from Assignment 3)
      //         These verify that forwarding does not break existing behavior
      // *********************************************************************

      // =========================================================================
      // SECTION 1: Basic Operations (Original Tests)
      // =========================================================================
      println("=== PART A: Old NOP-padded Tests ===")
      println("=== SECTION 1: Basic Operations ===")

      dut.clock.step(5)  // Wait for pipeline to fill (5 stages)
      dut.io.result.expect(0.U)     // I000: ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      println("PASS I000: NOP -> 0")

      dut.clock.step(1)
      dut.io.result.expect(4.U)     // I001: ADDI x1, x0, 4
      dut.io.exception.expect(false.B)
      println("PASS I001: ADDI x1, x0, 4 -> 4")

      dut.clock.step(1)
      dut.io.result.expect(5.U)     // I002: ADDI x2, x0, 5
      dut.io.exception.expect(false.B)
      println("PASS I002: ADDI x2, x0, 5 -> 5")

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I003: NOP
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I004: NOP
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I005: NOP
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(9.U)     // I006: ADD x3, x1, x2
      dut.io.exception.expect(false.B)
      println("PASS I006: ADD x3, x1, x2 -> 9")

      dut.clock.step(1)
      dut.io.result.expect(2047.U)  // I007: ADDI x4, x0, 2047
      dut.io.exception.expect(false.B)
      println("PASS I007: ADDI x4, x0, 2047 -> 2047")

      dut.clock.step(1)
      dut.io.result.expect(16.U)    // I008: ADDI x5, x0, 16
      dut.io.exception.expect(false.B)

      // NOPs I009-I011
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      dut.clock.step(1)
      dut.io.result.expect(2031.U)  // I012: SUB x6, x4, x5
      dut.io.exception.expect(false.B)
      println("PASS I012: SUB x6, x4, x5 -> 2031")

      // NOPs I013-I015
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      dut.clock.step(1)
      dut.io.result.expect(2022.U)  // I016: XOR x7, x6, x3
      dut.io.exception.expect(false.B)
      println("PASS I016: XOR x7, x6, x3 -> 2022")

      dut.clock.step(1)
      dut.io.result.expect(2047.U)  // I017: OR x8, x6, x5
      dut.io.exception.expect(false.B)
      println("PASS I017: OR x8, x6, x5 -> 2047")

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I018: AND x9, x6, x5
      dut.io.exception.expect(false.B)
      println("PASS I018: AND x9, x6, x5 -> 0")

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I019: NOP

      dut.clock.step(1)
      dut.io.result.expect(64704.U) // I020: SLL x10, x7, x2
      dut.io.exception.expect(false.B)
      println("PASS I020: SLL x10, x7, x2 -> 64704")

      dut.clock.step(1)
      dut.io.result.expect(63.U)    // I021: SRL x11, x7, x2
      dut.io.exception.expect(false.B)
      println("PASS I021: SRL x11, x7, x2 -> 63")

      dut.clock.step(1)
      dut.io.result.expect(63.U)    // I022: SRA x12, x7, x2
      dut.io.exception.expect(false.B)
      println("PASS I022: SRA x12, x7, x2 -> 63")

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I023: SLT x13, x4, x4
      dut.io.exception.expect(false.B)
      println("PASS I023: SLT -> 0 (equal values)")

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I024: SLT x13, x4, x5
      dut.io.exception.expect(false.B)
      println("PASS I024: SLT -> 0 (2047 not < 16)")

      dut.clock.step(1)
      dut.io.result.expect(1.U)     // I025: SLT x13, x5, x4
      dut.io.exception.expect(false.B)
      println("PASS I025: SLT -> 1 (16 < 2047)")

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I026: SLTU x13, x4, x4
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I027: SLTU x13, x4, x5
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(1.U)     // I028: SLTU x13, x5, x4
      dut.io.exception.expect(false.B)

      // =========================================================================
      // SECTION 2: Negative Numbers & Sign Extension
      // =========================================================================
      println("\n=== SECTION 2: Negative Numbers & Sign Extension ===")

      dut.clock.step(1)
      dut.io.result.expect(1.U)     // I029: ADDI x0, x0, 1
      dut.io.exception.expect(false.B)
      println("PASS I029: ADDI x0, x0, 1 -> 1 (WB result, x0 stays 0)")

      dut.clock.step(1)
      dut.io.result.expect("hFFFFFFFF".U) // I030: ADDI x3, x0, -1
      dut.io.exception.expect(false.B)
      println("PASS I030: ADDI x3, x0, -1 -> 0xFFFFFFFF")

      // NOPs I031-I033
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      dut.clock.step(1)
      dut.io.result.expect(1.U)     // I034: SLT x4, x3, x4 (-1 < 2047)
      dut.io.exception.expect(false.B)
      println("PASS I034: SLT x4, x3, x4 -> 1")

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I035: SLTU x5, x3, x4
      dut.io.exception.expect(false.B)
      println("PASS I035: SLTU x5, x3, x4 -> 0")

      // NOPs I036-I038
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // =========================================================================
      // SECTION 3: Shift Edge Cases
      // =========================================================================
      println("\n=== SECTION 3: Shift Edge Cases ===")

      dut.clock.step(1)
      dut.io.result.expect(1.U)     // I039: SLL x6, x4, x0 (shift by 0)
      dut.io.exception.expect(false.B)
      println("PASS I039: SLL x6, x4, x0 -> 1 (shift by 0)")

      dut.clock.step(1)
      dut.io.result.expect("h80000000".U) // I040: SLLI x7, x4, 31
      dut.io.exception.expect(false.B)
      println("PASS I040: SLLI x7, x4, 31 -> 0x80000000")

      // NOPs I041-I043
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // =========================================================================
      // SECTION 4: Immediate Boundary Values
      // =========================================================================
      println("\n=== SECTION 4: Immediate Boundary Values ===")

      dut.clock.step(1)
      dut.io.result.expect(2047.U)  // I044: XORI x8, x0, 2047
      dut.io.exception.expect(false.B)
      println("PASS I044: XORI x8, x0, 2047 -> 2047")

      dut.clock.step(1)
      dut.io.result.expect("hFFFFF800".U) // I045: XORI x9, x0, -2048
      dut.io.exception.expect(false.B)
      println("PASS I045: XORI x9, x0, -2048 -> 0xFFFFF800")

      // NOPs I046-I048
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // =========================================================================
      // SECTION 5: Bitwise All 1s
      // =========================================================================
      println("\n=== SECTION 5: Bitwise All 1s ===")

      dut.clock.step(1)
      dut.io.result.expect("hFFFFFFFF".U) // I049: OR x10, x8, x9
      dut.io.exception.expect(false.B)
      println("PASS I049: OR x10, x8, x9 -> 0xFFFFFFFF")

      // NOPs I050-I052
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // =========================================================================
      // SECTION 6: Bitwise NOT via XOR
      // =========================================================================
      println("\n=== SECTION 6: Bitwise NOT via XOR ===")

      dut.clock.step(1)
      dut.io.result.expect("hFFFFF800".U) // I053: XORI x12, x8, -1
      dut.io.exception.expect(false.B)
      println("PASS I053: XORI x12, x8, -1 -> 0xFFFFF800")

      // NOPs I054-I056
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // =========================================================================
      // SECTION 7: Arithmetic Right Shift (Sign Extension)
      // =========================================================================
      println("\n=== SECTION 7: Arithmetic Right Shift ===")

      dut.clock.step(1)
      dut.io.result.expect("hFFFFFFFF".U) // I057: ADDI x14, x0, -1
      dut.io.exception.expect(false.B)
      println("PASS I057: ADDI x14, x0, -1 -> 0xFFFFFFFF")

      // NOPs I058-I060
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      dut.clock.step(1)
      dut.io.result.expect("hFFFFFFFF".U) // I061: SRAI x15, x14, 31
      dut.io.exception.expect(false.B)
      println("PASS I061: SRAI x15, x14, 31 -> 0xFFFFFFFF")

      // NOPs I062-I064
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // =========================================================================
      // SECTION 8: Large Shift Left
      // =========================================================================
      println("\n=== SECTION 8: Large Shift Left ===")

      dut.clock.step(1)
      dut.io.result.expect("h80000000".U) // I065: SLLI x16, x15, 31
      dut.io.exception.expect(false.B)
      println("PASS I065: SLLI x16, x15, 31 -> 0x80000000")

      // NOPs I066-I068
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // =========================================================================
      // SECTION 9: Large Logical Right Shift
      // =========================================================================
      println("\n=== SECTION 9: Large Logical Right Shift ===")

      dut.clock.step(1)
      dut.io.result.expect(1.U)     // I069: SRLI x17, x15, 31
      dut.io.exception.expect(false.B)
      println("PASS I069: SRLI x17, x15, 31 -> 1")

      // NOPs I070-I072
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // =========================================================================
      // SECTION 10: Comparison of Equal Values
      // =========================================================================
      println("\n=== SECTION 10: Comparison of Equal Values ===")

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I073: SLT x18, x15, x14 (both 0xFFFFFFFF)
      dut.io.exception.expect(false.B)
      println("PASS I073: SLT x18, x15, x14 -> 0")

      // NOPs I074-I076
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      // =========================================================================
      // SECTION 11: Additional Tests (tail of old program)
      // =========================================================================
      println("\n=== SECTION 11: Additional Old Tests ===")

      dut.clock.step(1)
      dut.io.result.expect(2047.U)  // I077: ADDI x19, x0, 2047
      dut.io.exception.expect(false.B)
      println("PASS I077: ADDI x19, x0, 2047 -> 2047")

      dut.clock.step(1)
      dut.io.result.expect("hFFFFF800".U) // I078: ADDI x20, x0, -2048
      dut.io.exception.expect(false.B)
      println("PASS I078: ADDI x20, x0, -2048 -> 0xFFFFF800")

      // NOPs I079-I081
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I082: SUB x20, x20, x20 -> 0
      dut.io.exception.expect(false.B)
      println("PASS I082: SUB x20, x20, x20 -> 0")

      // NOPs I083-I085
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I086: ADD x21, x0, x0 -> 0
      dut.io.exception.expect(false.B)
      println("PASS I086: ADD x21, x0, x0 -> 0")

      dut.clock.step(1)
      dut.io.result.expect(1.U)     // I087: ADDI x22, x0, 1 -> 1
      dut.io.exception.expect(false.B)
      println("PASS I087: ADDI x22, x0, 1 -> 1")

      // NOPs I088-I090
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I091: ADDI x23, x22, -1 -> 0
      dut.io.exception.expect(false.B)
      println("PASS I091: ADDI x23, x22, -1 -> 0")

      // NOPs I092-I094
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.clock.step(1)
      dut.io.result.expect(0.U)

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I095: ADD x24, x23, x23 -> 0
      dut.io.exception.expect(false.B)
      println("PASS I095: ADD x24, x23, x23 -> 0")

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I096: NOP
      dut.io.exception.expect(false.B)

      println("\n=== PART A COMPLETE: All old tests passed ===")

      // *********************************************************************
      // BRIDGE: Step through separator NOPs (I097-I100)
      // *********************************************************************

      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I097: NOP
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I098: NOP
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I099: NOP
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I100: NOP

      // *********************************************************************
      // PART B: NEW FORWARDING TESTS (back-to-back, no NOP padding)
      //         These verify that the forwarding unit resolves all hazards
      // *********************************************************************

      println("\n\n" + "=" * 65)
      println("  PART B: Forwarding Unit Tests (back-to-back dependencies)")
      println("=" * 65)

      // =====================================================================
      // FWD TEST 1: EX Hazard (1 instruction apart)
      // =====================================================================
      println("\n--- FWD 1: EX Hazard (1 apart) ---")

      dut.clock.step(1)
      dut.io.result.expect(10.U)    // I101: ADDI x1, x0, 10
      dut.io.exception.expect(false.B)
      println("PASS I101: ADDI x1, x0, 10 -> 10")

      dut.clock.step(1)
      dut.io.result.expect(15.U)    // I102: ADDI x2, x1, 5  [EX hazard: x1]
      dut.io.exception.expect(false.B)
      println("PASS I102: ADDI x2, x1, 5 -> 15 [EX hazard on x1]")

      // =====================================================================
      // FWD TEST 2: Double Forwarding (EX + MEM hazard in same instr)
      // =====================================================================
      println("\n--- FWD 2: Double Forwarding (EX + MEM) ---")

      dut.clock.step(1)
      dut.io.result.expect(25.U)    // I103: ADD x3, x1, x2  [MEM: x1, EX: x2]
      dut.io.exception.expect(false.B)
      println("PASS I103: ADD x3, x1, x2 -> 25 [MEM hazard x1, EX hazard x2]")

      dut.clock.step(1)
      dut.io.result.expect(10.U)    // I104: SUB x4, x3, x2  [EX: x3, MEM: x2]
      dut.io.exception.expect(false.B)
      println("PASS I104: SUB x4, x3, x2 -> 10 [EX hazard x3, MEM hazard x2]")

      // =====================================================================
      // FWD TEST 3: Chain of EX Hazards (accumulator pattern)
      // =====================================================================
      println("\n--- FWD 3: Chain of EX Hazards (accumulator) ---")

      dut.clock.step(1)
      dut.io.result.expect(1.U)     // I105: ADDI x5, x0, 1
      dut.io.exception.expect(false.B)
      println("PASS I105: ADDI x5, x0, 1 -> 1")

      dut.clock.step(1)
      dut.io.result.expect(2.U)     // I106: ADDI x5, x5, 1  [EX hazard]
      dut.io.exception.expect(false.B)
      println("PASS I106: ADDI x5, x5, 1 -> 2 [EX hazard on x5]")

      dut.clock.step(1)
      dut.io.result.expect(3.U)     // I107: ADDI x5, x5, 1  [EX hazard]
      dut.io.exception.expect(false.B)
      println("PASS I107: ADDI x5, x5, 1 -> 3 [EX hazard on x5]")

      // =====================================================================
      // FWD TEST 4: No Hazard (values from register file)
      // =====================================================================
      println("\n--- FWD 4: No Hazard (from register file) ---")

      dut.clock.step(1)
      dut.io.result.expect(35.U)    // I108: ADD x6, x3, x4  [no hazard]
      dut.io.exception.expect(false.B)
      println("PASS I108: ADD x6, x3, x4 -> 35 [no hazard, from reg file]")

      // =====================================================================
      // FWD TEST 5: x0 Forwarding Prevention
      // =====================================================================
      println("\n--- FWD 5: x0 Forwarding Prevention ---")

      dut.clock.step(1)
      dut.io.result.expect(42.U)    // I109: ADDI x0, x0, 42  [WB=42, x0 unchanged]
      dut.io.exception.expect(false.B)
      println("PASS I109: ADDI x0, x0, 42 -> 42 (WB result)")

      dut.clock.step(1)
      dut.io.result.expect(10.U)    // I110: ADD x7, x0, x1  [must NOT forward x0]
      dut.io.exception.expect(false.B)
      println("PASS I110: ADD x7, x0, x1 -> 10 [x0 NOT forwarded from I109]")

      // =====================================================================
      // FWD TEST 6: Double Data Hazard (same rd, EX priority)
      // =====================================================================
      println("\n--- FWD 6: Double Data Hazard (EX priority) ---")

      dut.clock.step(1)
      dut.io.result.expect(100.U)   // I111: ADDI x8, x0, 100
      dut.io.exception.expect(false.B)
      println("PASS I111: ADDI x8, x0, 100 -> 100")

      dut.clock.step(1)
      dut.io.result.expect(200.U)   // I112: ADDI x8, x0, 200
      dut.io.exception.expect(false.B)
      println("PASS I112: ADDI x8, x0, 200 -> 200")

      dut.clock.step(1)
      dut.io.result.expect(200.U)   // I113: ADD x9, x8, x0  [EX=200, MEM=100, EX wins]
      dut.io.exception.expect(false.B)
      println("PASS I113: ADD x9, x8, x0 -> 200 [double hazard, EX priority]")

      // =====================================================================
      // FWD TEST 7: I-type Forwarding (rs1 only, not immediate)
      // =====================================================================
      println("\n--- FWD 7: I-type rs1 Forwarding ---")

      dut.clock.step(1)
      dut.io.result.expect(7.U)     // I114: ADDI x10, x0, 7
      dut.io.exception.expect(false.B)
      println("PASS I114: ADDI x10, x0, 7 -> 7")

      dut.clock.step(1)
      dut.io.result.expect(248.U)   // I115: XORI x11, x10, 0xFF  [EX hazard on rs1]
      dut.io.exception.expect(false.B)
      println("PASS I115: XORI x11, x10, 0xFF -> 248 [EX hazard rs1, imm on B]")

      // =====================================================================
      // FWD TEST 8: Shift Operations with EX + MEM Hazard
      // =====================================================================
      println("\n--- FWD 8: Shift Operations with Forwarding ---")

      dut.clock.step(1)
      dut.io.result.expect("hFFFFFFFF".U) // I116: ADDI x12, x0, -1
      dut.io.exception.expect(false.B)
      println("PASS I116: ADDI x12, x0, -1 -> 0xFFFFFFFF")

      dut.clock.step(1)
      dut.io.result.expect("hFFFFFFFF".U) // I117: SRAI x13, x12, 16  [EX hazard]
      dut.io.exception.expect(false.B)
      println("PASS I117: SRAI x13, x12, 16 -> 0xFFFFFFFF [EX hazard on x12]")

      dut.clock.step(1)
      dut.io.result.expect("h0000FFFF".U) // I118: SRLI x14, x12, 16  [MEM hazard]
      dut.io.exception.expect(false.B)
      println("PASS I118: SRLI x14, x12, 16 -> 0x0000FFFF [MEM hazard on x12]")

      // =====================================================================
      // Final NOP
      // =====================================================================
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // I119: NOP
      dut.io.exception.expect(false.B)

      println("\n" + "=" * 65)
      println("  ALL TESTS PASSED (Part A: old + Part B: forwarding)")
      println("=" * 65)
    }
  }
}