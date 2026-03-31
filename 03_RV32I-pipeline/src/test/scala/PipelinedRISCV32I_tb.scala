package PipelinedRV32I_Tester

import chisel3._
import chiseltest._
import PipelinedRV32I._
import org.scalatest.flatspec.AnyFlatSpec

class PipelinedRISCV32ITest extends AnyFlatSpec with ChiselScalatestTester {

  "RV32I_Combined_Tester" should "pass all tests" in {
    test(new PipelinedRV32I("src/test/programs/BinaryFile")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      dut.clock.setTimeout(0)

      // Helper: step and check
      def check(expected: BigInt, msg: String): Unit = {
        dut.clock.step(1)
        dut.io.result.expect(expected.U)
        dut.io.exception.expect(false.B)
        println(s"PASS: $msg -> ${expected}")
      }
      def checkH(expected: String, msg: String): Unit = {
        dut.clock.step(1)
        dut.io.result.expect(s"h${expected}".U)
        dut.io.exception.expect(false.B)
        println(s"PASS: $msg -> 0x${expected}")
      }
      def nop(n: Int = 1): Unit = (0 until n).foreach { _ =>
        dut.clock.step(1); dut.io.result.expect(0.U)
      }

      // =================================================================
      // PART A: Old NOP-padded tests (97 instructions, indices 0-96)
      // =================================================================
      println("=" * 60)
      println("  PART A: Old NOP-padded tests")
      println("=" * 60)

      dut.clock.step(5)
      dut.io.result.expect(0.U); dut.io.exception.expect(false.B)
      println("PASS: I000 NOP -> 0")

      check(4, "I001 ADDI x1=4")
      check(5, "I002 ADDI x2=5")
      nop(3) // I003-I005
      check(9, "I006 ADD x3=9")
      check(2047, "I007 ADDI x4=2047")
      check(16, "I008 ADDI x5=16")
      nop(3) // I009-I011
      check(2031, "I012 SUB x6=2031")
      nop(3) // I013-I015
      check(2022, "I016 XOR x7=2022")
      check(2047, "I017 OR x8=2047")
      check(0, "I018 AND x9=0")
      nop(1) // I019
      check(64704, "I020 SLL x10=64704")
      check(63, "I021 SRL x11=63")
      check(63, "I022 SRA x12=63")
      check(0, "I023 SLT eq")
      check(0, "I024 SLT 2047>16")
      check(1, "I025 SLT 16<2047")
      check(0, "I026 SLTU eq")
      check(0, "I027 SLTU 2047>16")
      check(1, "I028 SLTU 16<2047")
      check(1, "I029 ADDI x0 (WB=1)")
      checkH("FFFFFFFF", "I030 ADDI x3=-1")
      nop(3) // I031-I033
      check(1, "I034 SLT -1<2047")
      check(0, "I035 SLTU 0xFFFFFFFF>1")
      nop(3) // I036-I038
      check(1, "I039 SLL shift0")
      checkH("80000000", "I040 SLLI x4<<31")
      nop(3) // I041-I043
      check(2047, "I044 XORI max+imm")
      checkH("FFFFF800", "I045 XORI min-imm")
      nop(3) // I046-I048
      checkH("FFFFFFFF", "I049 OR all1s")
      nop(3) // I050-I052
      checkH("FFFFF800", "I053 XORI NOT")
      nop(3) // I054-I056
      checkH("FFFFFFFF", "I057 ADDI x14=-1")
      nop(3) // I058-I060
      checkH("FFFFFFFF", "I061 SRAI sign")
      nop(3) // I062-I064
      checkH("80000000", "I065 SLLI x15<<31")
      nop(3) // I066-I068
      check(1, "I069 SRLI logical")
      nop(3) // I070-I072
      check(0, "I073 SLT equal")
      nop(3) // I074-I076
      check(2047, "I077 ADDI x19=2047")
      checkH("FFFFF800", "I078 ADDI x20=-2048")
      nop(3) // I079-I081
      check(0, "I082 SUB self=0")
      nop(3) // I083-I085
      check(0, "I086 ADD x0+x0")
      check(1, "I087 ADDI x22=1")
      nop(3) // I088-I090
      check(0, "I091 ADDI x22-1=0")
      nop(3) // I092-I094
      check(0, "I095 ADD 0+0")
      nop(1) // I096

      println("\nPART A COMPLETE\n")

      // Separator NOPs (I097-I100)
      nop(4)

      // =================================================================
      // PART B: Forwarding tests (19 instructions, indices 101-119)
      // =================================================================
      println("=" * 60)
      println("  PART B: Forwarding tests (back-to-back)")
      println("=" * 60)

      check(10, "I101 ADDI x1=10")
      check(15, "I102 ADDI x2=x1+5 [EX fwd]")
      check(25, "I103 ADD x3=x1+x2 [MEM+EX fwd]")
      check(10, "I104 SUB x4=x3-x2 [EX+MEM fwd]")
      check(1, "I105 ADDI x5=1")
      check(2, "I106 ADDI x5++ [EX fwd]")
      check(3, "I107 ADDI x5++ [EX fwd]")
      check(35, "I108 ADD x6=x3+x4 [regfile]")
      check(42, "I109 ADDI x0=42 (WB)")
      check(10, "I110 ADD x7=x0+x1 [x0 NOT fwd]")
      check(100, "I111 ADDI x8=100")
      check(200, "I112 ADDI x8=200")
      check(200, "I113 ADD x9=x8 [dbl hazard EX wins]")
      check(7, "I114 ADDI x10=7")
      check(248, "I115 XORI x11=7^0xFF [EX fwd rs1]")
      checkH("FFFFFFFF", "I116 ADDI x12=-1")
      checkH("FFFFFFFF", "I117 SRAI x13=-1>>16 [EX fwd]")
      checkH("0000FFFF", "I118 SRLI x14=-1>>16 [MEM fwd]")
      nop(1) // I119

      println("\nPART B COMPLETE\n")

      // Separator NOPs (I120-I123)
      nop(4)

      // =================================================================
      // PART C: Branch and Jump tests (55 instructions, indices 124-178)
      // =================================================================
      println("=" * 60)
      println("  PART C: Branch and Jump tests")
      println("=" * 60)

      // --- Setup ---
      check(5, "I124 ADDI x1=5")
      check(10, "I125 ADDI x2=10")
      check(5, "I126 ADDI x3=5")

      // --- BEQ not taken ---
      println("\n--- BEQ ---")
      check(0, "I127 BEQ x1,x2 NOT TAKEN")
      check(1, "I128 ADDI x4=1 (after not-taken)")

      // --- BEQ taken +12 ---
      check(0, "I129 BEQ x1,x3 TAKEN")
      check(0, "Flushing")
      check(0, "FLushing")
      check(2, "I132 ADDI x5=2 (BEQ target)")

      // --- BNE taken +12 ---
      println("\n--- BNE ---")
      check(0, "I133 BNE x1,x2 TAKEN")
      nop(2)
      check(3, "I136 ADDI x6=3 (BNE target)")

      // --- BNE not taken ---
      check(0, "I137 BNE x1,x3 NOT TAKEN")
      check(4, "I138 ADDI x7=4")

      // --- BLT taken +12 ---
      println("\n--- BLT ---")
      check(0, "I139 BLT x1,x2 TAKEN (5<10)")
      nop(2)
      check(5, "I142 ADDI x8=5 (BLT target)")

      // --- BGE not taken, then taken ---
      println("\n--- BGE ---")
      check(0, "I143 BGE x1,x2 NOT TAKEN (5<10)")
      check(6, "I144 ADDI x9=6")
      check(0, "I145 BGE x2,x1 TAKEN (10>=5)")
      nop(2)
      check(7, "I148 ADDI x10=7 (BGE target)")

      // --- Negative number + unsigned comparisons ---
      println("\n--- BLTU / BGEU ---")
      checkH("FFFFFFFF", "I149 ADDI x11=-1")
      check(0, "I150 BLTU x1,x11 TAKEN (5<0xFFFFFFFF)")
      nop(2)
      check(8, "I153 ADDI x12=8 (BLTU target)")
      check(0, "I154 BGEU x11,x1 TAKEN")
      nop(2)
      check(9, "I157 ADDI x13=9 (BGEU target)")

      // --- JAL ---
      println("\n--- JAL ---")
      // JAL x14, +12 at PC=632. x14 = 636 (return addr)
      check(636, "I158 JAL x14 -> x14=636")
      nop(2) // flush
      check(10, "I161 ADDI x15=10 (JAL target)")

      // --- JALR ---
      println("\n--- JALR ---")
      check(664, "I162 ADDI x20=664 (target addr)")
      // JALR x16, x20, 0 at PC=652. x16 = 656
      check(656, "I163 JALR x16 -> x16=656, jump to 664")
      nop(2) // flush
      check(42, "I166 ADDI x18=42 (JALR target)")

      // --- Branch with forwarding ---
      println("\n--- Branch + Forwarding ---")
      check(100, "I167 ADDI x21=100")
      check(150, "I168 ADDI x22=x21+50 [EX fwd]")
      check(0, "I169 BEQ x21,x22 NOT TAKEN [fwd both]")
      check(0, "I170 BNE x21,x22 TAKEN [fwd both]")
      nop(2) // flush
      check(11, "I173 ADDI x23=11 (BNE+fwd target)")

      // --- Backward branch (loop: count to 3) ---
      println("\n--- Backward Branch Loop ---")
      check(0, "I174 ADDI x24=0 (counter)")
      check(3, "I175 ADDI x25=3 (limit)")

      // Iteration 1: x24 = 0+1 = 1, BNE taken (1!=3)
      check(1, "I176 ADDI x24=1 (iter1)")
      check(0, "I177 BNE x24,x25 TAKEN (1!=3)")
      nop(2) // flush

      // Iteration 2: x24 = 1+1 = 2, BNE taken (2!=3)
      check(2, "I176 ADDI x24=2 (iter2)")
      check(0, "I177 BNE x24,x25 TAKEN (2!=3)")
      nop(2) // flush

      // Iteration 3: x24 = 2+1 = 3, BNE NOT taken (3==3)
      check(3, "I176 ADDI x24=3 (iter3)")
      check(0, "I177 BNE x24,x25 NOT TAKEN (3==3)")

      // End
      check(0, "I178 NOP (end)")

      println("\n" + "=" * 60)
      println("  ALL TESTS PASSED (A: old + B: forwarding + C: branches)")
      println("=" * 60)
    }
  }

/*  "BTBPrediction" should "correctly predict loop branches" in {
    test(new PipelinedRV32I("src/test/programs/BinaryFile_btb", useBTB = true))
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

        dut.clock.setTimeout(0)

        def check(expected: BigInt, msg: String): Unit = {
          dut.clock.step(1); dut.io.result.expect(expected.U)
          dut.io.exception.expect(false.B); println(s"PASS: $msg -> $expected")
        }
        def nop(n: Int = 1): Unit = (0 until n).foreach { _ =>
          dut.clock.step(1); dut.io.result.expect(0.U)
        }

        println("=" * 60)
        println("  BTB Test: Loop counting to 5")
        println("=" * 60)

        // Pipeline fill
        dut.clock.step(5)
        dut.io.result.expect(0.U)
        println("PASS: NOP -> 0")

        // Setup
        check(0, "ADDI x1=0 (counter)")
        check(5, "ADDI x2=5 (limit)")

        // ---- Iter 1: BTB miss → predict not-taken → taken → MISPREDICTION ----
        println("\n--- Iter 1: BTB miss ---")
        check(1, "ADDI x1=1 (iter1)")
        check(0, "BNE: BTB miss, taken → misprediction")
        nop(2)  // flush bubbles

        // ---- Iter 2: BTB hit, weakTaken → predict taken → correct! ----
        println("\n--- Iter 2: BTB hit, correct (weakTaken) ---")
        check(2, "ADDI x1=2 (iter2)")
        check(0, "BNE: BTB hit, predicted taken, correct!")

        // ---- Iter 3: BTB hit, strongTaken → correct ----
        println("\n--- Iter 3: BTB hit, correct (strongTaken) ---")
        check(3, "ADDI x1=3 (iter3)")
        check(0, "BNE: BTB hit, predicted taken, correct!")

        // ---- Iter 4: BTB hit, strongTaken → correct ----
        println("\n--- Iter 4: BTB hit, correct (strongTaken) ---")
        check(4, "ADDI x1=4 (iter4)")
        check(0, "BNE: BTB hit, predicted taken, correct!")

        // ---- Iter 5: BTB hit, strongTaken → NOT taken → MISPREDICTION ----
        println("\n--- Iter 5: BTB exit misprediction ---")
        check(5, "ADDI x1=5 (iter5)")
        check(0, "BNE: BTB hit, predicted taken, actually NOT taken → misprediction")
        nop(2)  // flush bubbles

        // After loop
        check(99, "ADDI x3=99 (after loop)")
        check(0, "NOP (end)")

        println("\n" + "=" * 60)
        println("  BTB PREDICTION: ALL TESTS PASSED")
        println("  Cycle count: 23 (vs 27 with static = 15% improvement)")
        println("=" * 60)
      }
  }*/

  "Test_Branching" should "execute loops and flush pipeline correctly" in {
    test(new PipelinedRV32I("src/test/programs/Binary_file_branch"))
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

        dut.clock.setTimeout(0)

        var found = false
        var foundCycle = 0

        // Run for up to 500 cycles to find the success marker (result=100)
        for (cycle <- 1 to 500 if !found) {
          dut.clock.step(1)
          if (dut.io.result.peek().litValue == 100) {
            found = true
            foundCycle = cycle
          }
        }

        // Assert success
        assert(found, "Processor never output value 100! (Loop stuck or flush error)")

        // Read performance counters
        val branches    = dut.io.total_branches.peek().litValue.toDouble
        val mispredicts = dut.io.total_mispredicts.peek().litValue.toDouble
        val correct     = branches - mispredicts
        val accuracy    = if (branches > 0) (correct / branches) * 100.0 else 0.0

        println("=" * 55)
        println("       BRANCH TEST — PERFORMANCE REPORT")
        println("=" * 55)
        println(f"  Result 100 found at cycle:   $foundCycle")
        println(f"  Total Branches Executed:     ${branches.toInt}")
        println(f"  Correct Predictions:         ${correct.toInt}")
        println(f"  Mispredictions:              ${mispredicts.toInt}")
        println(f"  Prediction Accuracy:         $accuracy%.1f%%")
        println()
        println("  Branch types tested:")
        println("    BEQ  (taken + not-taken)")
        println("    BNE  (taken)")
        println("    BLT  (taken)")
        println("    BGE  (taken)")
        println("    BLTU (taken)")
        println("    BGEU (taken)")
        println("    JAL  (unconditional)")
        println("    JALR (unconditional)")
        println("    Backward BNE loop (10 iterations)")
        println("    BEQ verification (sum == 55)")
        println()
        println("  Flush verification:")
        println("    9 flushed ADDI x10,99 instructions (would corrupt x10)")
        println("    1 flushed ADDI x10,0 instruction (fail marker)")
        println("    Result=100 proves all 10 flushes worked correctly")
        println("=" * 55)
        println("  Test_Branching: PASSED")
        println("=" * 55)
      }
  }

  // ===================================================================
  // TEST 2: BTB Performance — Static Prediction (baseline)
  //         Nested loop: 5 outer × 4 inner = 25 branch executions
  // ===================================================================

  "Test_BTB_Static" should "run nested loop with static prediction" in {
    test(new PipelinedRV32I("src/test/programs/BinaryFile_btb", useBTB = false))
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

        dut.clock.setTimeout(0)

        var found = false
        var foundCycle = 0

        for (cycle <- 1 to 500 if !found) {
          dut.clock.step(1)
          if (dut.io.result.peek().litValue == 100) {
            found = true
            foundCycle = cycle
          }
        }

        assert(found, "Processor never output value 100 with static prediction!")

        val branches    = dut.io.total_branches.peek().litValue.toDouble
        val mispredicts = dut.io.total_mispredicts.peek().litValue.toDouble
        val correct     = branches - mispredicts
        val accuracy    = if (branches > 0) (correct / branches) * 100.0 else 0.0

        println("=" * 55)
        println("       STATIC PREDICTION — NESTED LOOP BASELINE")
        println("=" * 55)
        println(f"  Result 100 found at cycle:   $foundCycle")
        println(f"  Total Branches Executed:     ${branches.toInt}")
        println(f"  Correct Predictions:         ${correct.toInt}")
        println(f"  Mispredictions:              ${mispredicts.toInt}")
        println(f"  Prediction Accuracy:         $accuracy%.1f%%")
        println(f"  Wasted flush cycles:         ${mispredicts.toInt * 2}")
        println("=" * 55)
        println("  Test_BTB_Static: PASSED")
        println("=" * 55)
      }
  }

  // ===================================================================
  // TEST 3: BTB Performance — Dynamic BTB Prediction
  //         Same nested loop, with BTB enabled
  // ===================================================================

  "Test_BTB_Dynamic" should "improve prediction accuracy with BTB" in {
    test(new PipelinedRV32I("src/test/programs/BinaryFile_btb", useBTB = true))
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

        dut.clock.setTimeout(0)

        var found = false
        var foundCycle = 0

        for (cycle <- 1 to 500 if !found) {
          dut.clock.step(1)
          if (dut.io.result.peek().litValue == 100) {
            found = true
            foundCycle = cycle
          }
        }

        assert(found, "Processor never output value 100 with BTB prediction!")

        val branches    = dut.io.total_branches.peek().litValue.toDouble
        val mispredicts = dut.io.total_mispredicts.peek().litValue.toDouble
        val correct     = branches - mispredicts
        val accuracy    = if (branches > 0) (correct / branches) * 100.0 else 0.0

        println("=" * 55)
        println("       BTB PREDICTION — NESTED LOOP RESULTS")
        println("=" * 55)
        println(f"  Result 100 found at cycle:   $foundCycle")
        println(f"  Total Branches Executed:     ${branches.toInt}")
        println(f"  Correct Predictions:         ${correct.toInt}")
        println(f"  Mispredictions:              ${mispredicts.toInt}")
        println(f"  Prediction Accuracy:         $accuracy%.1f%%")
        println(f"  Wasted flush cycles:         ${mispredicts.toInt * 2}")
        println("=" * 55)
        println("  Test_BTB_Dynamic: PASSED")
        println("=" * 55)
      }
  }

  // ===================================================================
  // TEST 4: BTB Performance Comparison — Side-by-side evaluation
  //         Runs both modes and prints comparison table
  // ===================================================================

  "Test_BTB_Comparison" should "demonstrate BTB improvement over static" in {
    // --- Run Static ---
    var staticCycles = 0
    var staticBranches = 0.0
    var staticMispredicts = 0.0

    test(new PipelinedRV32I("src/test/programs/BinaryFile_btb", useBTB = false))
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
        dut.clock.setTimeout(0)
        var found = false
        for (cycle <- 1 to 500 if !found) {
          dut.clock.step(1)
          if (dut.io.result.peek().litValue == 100) { found = true; staticCycles = cycle }
        }
        assert(found, "Static: never found result 100!")
        staticBranches    = dut.io.total_branches.peek().litValue.toDouble
        staticMispredicts = dut.io.total_mispredicts.peek().litValue.toDouble
      }

    // --- Run BTB ---
    var btbCycles = 0
    var btbBranches = 0.0
    var btbMispredicts = 0.0

    test(new PipelinedRV32I("src/test/programs/BinaryFile_btb", useBTB = true))
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
        dut.clock.setTimeout(0)
        var found = false
        for (cycle <- 1 to 500 if !found) {
          dut.clock.step(1)
          if (dut.io.result.peek().litValue == 100) { found = true; btbCycles = cycle }
        }
        assert(found, "BTB: never found result 100!")
        btbBranches    = dut.io.total_branches.peek().litValue.toDouble
        btbMispredicts = dut.io.total_mispredicts.peek().litValue.toDouble
      }

    // --- Print Comparison ---
    val staticCorrect  = staticBranches - staticMispredicts
    val btbCorrect     = btbBranches - btbMispredicts
    val staticAccuracy = if (staticBranches > 0) (staticCorrect / staticBranches) * 100.0 else 0.0
    val btbAccuracy    = if (btbBranches > 0) (btbCorrect / btbBranches) * 100.0 else 0.0
    val cyclesSaved    = staticCycles - btbCycles
    val improvement    = if (staticCycles > 0) cyclesSaved * 100.0 / staticCycles else 0.0

    println()
    println("=" * 60)
    println("          BTB PERFORMANCE EVALUATION")
    println("          Nested Loop: 5 outer × 4 inner")
    println("=" * 60)
    println()
    println(f"  Metric                    Static      BTB")
    println(f"  ─────────────────────────────────────────────")
    println(f"  Cycles to result=100      $staticCycles          $btbCycles")
    println(f"  Total Branches            ${staticBranches.toInt}          ${btbBranches.toInt}")
    println(f"  Correct Predictions       ${staticCorrect.toInt}           ${btbCorrect.toInt}")
    println(f"  Mispredictions            ${staticMispredicts.toInt}          ${btbMispredicts.toInt}")
    println(f"  Prediction Accuracy       $staticAccuracy%.1f%%       $btbAccuracy%.1f%%")
    println(f"  Wasted Flush Cycles       ${staticMispredicts.toInt * 2}          ${btbMispredicts.toInt * 2}")
    println(f"  ─────────────────────────────────────────────")
    println(f"  Cycles Saved by BTB:      $cyclesSaved")
    println(f"  Performance Improvement:  $improvement%.1f%%")
    println()
    println("  BTB Configuration:")
    println("    Structure:  2-way set-associative, 8 sets")
    println("    Predictor:  2-bit saturating counter")
    println("    Replacement: LRU (Least Recently Used)")
    println("    Indexing:   PC[4:2] (3 bits)")
    println("    Tag:        PC[31:5] (27 bits)")
    println()
    println("  Analysis:")
    println("    Static prediction mispredicts every taken branch.")
    println("    BTB learns loop patterns after first iteration,")
    println("    correctly predicting taken branches in subsequent")
    println("    iterations. Only first encounter (BTB miss) and")
    println("    loop exit (predict taken, actually not taken)")
    println("    cause mispredictions.")
    println("=" * 60)

    // Assert BTB is actually better
    assert(btbCycles < staticCycles, s"BTB ($btbCycles) should be faster than static ($staticCycles)!")
    assert(btbAccuracy > staticAccuracy, s"BTB accuracy ($btbAccuracy%) should be higher than static ($staticAccuracy%)!")

    println("  Test_BTB_Comparison: PASSED")
    println("  (BTB is faster and more accurate than static prediction)")
    println("=" * 60)
  }
}