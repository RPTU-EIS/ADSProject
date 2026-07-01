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
      dut.clock.step(5)
      dut.io.result.expect(0.U) // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(4.U) // ADDI x1, x0, 4
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(5.U) // ADDI x2, x0, 5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(9.U) // ADD x3, x1, x2
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(2047.U) // ADDI x4, x0, 2047
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(16.U) // ADDI x5, x0, 16
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(2031.U) // SUB x6, x4, x5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(2022.U) // XOR x7, x6, x3
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(2047.U) // OR x8, x6, x5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U) // AND x9, x6, x5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(64704.U) // SLL x10, x7, x2
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(63.U) // SRL x11, x7, x2
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(63.U) // SRA x12, x7, x2
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U) // SLT x13, x4, x4
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U) // SLT x13, x4, x5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(1.U) // SLT x13, x5, x4
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U) // SLTU x13, x4, x4
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U) // SLTU x13, x4, x5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(1.U) // SLTU x13, x5, x4
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      // ---------------------
      // BEQ Test
      // ---------------------

      dut.clock.step(1)
      dut.io.result.expect(5.U)
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(5.U)
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(0.U) // BEQ writes nothing ALU Result is =0
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(0.U) // instruction after BEQ is flushed, so no register write occurs. since ALU=0 we expect 0
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(1.U)
      dut.io.exception.expect(false.B)

      // BEQ Not Taken Test
      dut.clock.step(1)
      //dut.io.result.expect(1.U)              // Previous instruction finishing
      //dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(5.U) // ADDI x18, x0, 5
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(6.U) // ADDI x19, x0, 6
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect("hFFFFFFFF".U) // BEQ x18, x19, 8 (not taken) ALU Result= -1, so we expect "hFFFFFFFF"
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(99.U) // ADDI x20, x0, 99
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(1.U) // ADDI x21, x0, 1
      dut.io.exception.expect(false.B)

      // BNE Taken Test

      dut.clock.step(1)
      dut.io.result.expect(5.U) // ADDI x22, x0, 5
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(6.U) // ADDI x23, x0, 6
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect("hFFFFFFFF".U) // BNE x22, x23, 8 (branch taken) ALU result= -1, so we expect "hFFFFFFFF"
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(0.U) // Flushed instruction (99 should NOT appear)
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(1.U) // ADDI x25, x0, 1
      dut.io.exception.expect(false.B)

      // ----------------------
      // BNE Not Taken Test

      dut.clock.step(1) // Skip the final result from the previous test
      dut.clock.step(1)
      dut.io.result.expect(37.U) // ADDI x26, x0, 37
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(37.U) // ADDI x27, x0, 37
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(0.U) // BNE x26, x27, 8 (condition false) it is 0 because last stored value is 0
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(99.U) // Branch NOT taken, so this executes
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(1.U) // ADDI x29, x0, 1
      dut.io.exception.expect(false.B)

      // BLT Taken Test
      // --------------------------

      dut.clock.step(1)
      dut.io.result.expect(10.U) // ADDI x30, x0, 10
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(20.U) // ADDI x31, x0, 20
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(1.U) // BLT x30, x31, 8 (10 < 20) ALU =1 as it is True
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(0.U) // Flushed ADDI x5, x0, 99
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(1.U) // ADDI x6, x0, 1
      dut.io.exception.expect(false.B)
      // --------------------------
      // BLT Not Taken Test
      // --------------------------

      dut.clock.step(1)
      // dut.io.result.expect(1.U)           // Last instruction from previous test
      // dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(20.U) // ADDI x30, x0, 20
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(10.U) // ADDI x31, x0, 10
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(0.U) // BLT x30, x31, 8 (20 < 10 is false)
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(99.U) // ADDI x5, x0, 99 (branch not taken)
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(1.U) // ADDI x6, x0, 1
      dut.io.exception.expect(false.B)
      // --------------------------
      // BGE Taken Test
      // --------------------------

      dut.clock.step(1)
      dut.io.result.expect(20.U) // addi x30, x0, 20
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(10.U) // addi x31, x0, 10
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(0.U) // BGE: ALU computes (20 < 10) = false -> 0
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(0.U) // flushed instruction
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(1.U) // addi x6, x0, 1
      dut.io.exception.expect(false.B)
      // --------------------------
      // BGE Not Taken Test
      // --------------------------
      dut.clock.step(1)
      dut.clock.step(1)
      dut.io.result.expect(12.U) // ADDI x8, x0, 12
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(30.U) // ADDI x9, x0, 30
      dut.io.exception.expect(false.B)


      dut.clock.step(1)
      dut.io.result.expect(1.U) // BGE x8, x9, 8
      dut.io.exception.expect(false.B) // ALU performs SLT: (12 < 30) = true -> 1,Branch NOT taken


      dut.clock.step(1)
      dut.io.result.expect(99.U) // ADDI x10, x0, 99 executes
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(1.U) // ADDI x11, x0, 1 executes
      dut.io.exception.expect(false.B)

      // --------------------------
      // BLTU Taken Test
      // --------------------------

      dut.clock.step(1)
      dut.io.result.expect(15.U) // ADDI x12, x0, 15
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(40.U) // ADDI x13, x0, 40
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(1.U) // BLTU x12, x13, 8
      // ALU performs SLTU: (15 < 40) = true -> 1
      // Branch Taken
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(0.U) // Flushed ADDI x14, x0, 99
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(1.U) // ADDI x15, x0, 1
      dut.io.exception.expect(false.B)

      // -------------------------
      // BLTU NOT TAKEN TEST
      // -------------------------
      dut.clock.step(1) // consume previous test result
      // addi x20, x0, 60
      dut.clock.step(1)
      dut.io.result.expect(60.U)
      dut.io.exception.expect(false.B)
      // addi x21, x0, 25
      dut.clock.step(1)
      dut.io.result.expect(25.U)
      dut.io.exception.expect(false.B)
      // bltu x20, x21, 8
      // 60 < 25 = FALSE
      // ALU performs SLTU and returns 0
      dut.clock.step(1)
      dut.io.result.expect(0.U)
      dut.io.exception.expect(false.B)
      // Branch NOT taken
      // addi x22, x0, 99 executes
      dut.clock.step(1)
      dut.io.result.expect(99.U)
      dut.io.exception.expect(false.B)
      // addi x23, x0, 1
      dut.clock.step(1)
      dut.io.result.expect(1.U)
      dut.io.exception.expect(false.B)

      // -------------------------
      // BGEU TAKEN TEST
      // -------------------------
      //dut.clock.step(1) // consume previous test result

      // addi x26, x0, 80
      //dut.clock.step(1)
      //dut.io.result.expect(80.U)
      //dut.io.exception.expect(false.B)

      // addi x27, x0, 25
      //dut.clock.step(1)
      //dut.io.result.expect(25.U)
      //dut.io.exception.expect(false.B)

      // bgeu x26, x27, 8
      //dut.clock.step(1)
      //dut.io.result.expect(0.U)
      //dut.io.exception.expect(false.B)

      // addi x29, x0, 1
      //dut.clock.step(1)
      //dut.io.result.expect(1.U)
      //dut.io.exception.expect(false.B)
    }

  }
  "RV32I_BGEU_Taken" should "work" in {

    test(new PipelinedRV32I("src/test/programs/BinaryFile_bgeu_taken"))
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

        // Fill the pipeline
        dut.clock.step(5)

        // addi x26, x0, 80
        dut.io.result.expect(80.U)
        dut.io.exception.expect(false.B)

        // addi x27, x0, 25
        dut.clock.step(1)
        dut.io.result.expect(25.U)
        dut.io.exception.expect(false.B)

        // bgeu x26, x27, 8
        dut.clock.step(1)
        dut.io.result.expect(0.U)
        dut.io.exception.expect(false.B)

        // Branch taken -> addi x28 flushed
        dut.clock.step(1)
        dut.io.result.expect(0.U)
        dut.io.exception.expect(false.B)

        // addi x29, x0, 1
        dut.clock.step(1)
        dut.io.result.expect(1.U)
        dut.io.exception.expect(false.B)
      }
  }

  "RV32I_BGEU_NotTaken" should "work" in {

    test(new PipelinedRV32I("src/test/programs/BinaryFile_bgeu_not_taken"))
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

        dut.clock.step(5)

        // addi x20, x0, 30
        dut.io.result.expect(30.U)
        dut.io.exception.expect(false.B)

        // addi x21, x0, 75
        dut.clock.step(1)
        dut.io.result.expect(75.U)
        dut.io.exception.expect(false.B)

        // bgeu x20, x21, 8
        // ALU performs SLTU
        // 30 < 75 = TRUE -> ALU = 1
        // Branch NOT Taken
        dut.clock.step(1)
        dut.io.result.expect(1.U)
        dut.io.exception.expect(false.B)

        // addi x22, x0, 99 executes
        dut.clock.step(1)
        dut.io.result.expect(99.U)
        dut.io.exception.expect(false.B)

        // addi x23, x0, 1 executes
        dut.clock.step(1)
        dut.io.result.expect(1.U)
        dut.io.exception.expect(false.B)
      }
  }
  ///"RV32I_JAL" should "work" in {
 //   test(new PipelinedRV32I("src/test/programs/BinaryFile_jal"))
   //   .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
//
  //      dut.clock.setTimeout(0)

        // Fill the pipeline
    //    dut.clock.step(5)

        // -----------------------------
        // JAL x5, 8
        // -----------------------------
    //    dut.io.result.expect(4.U)      // x5 = PC + 4
    //    dut.io.exception.expect(false.B)

        // -----------------------------
        // addi x6, x0, 99
        // Should be flushed
        // -----------------------------
    //    dut.clock.step(1)
    //    dut.io.result.expect(0.U)
    //    dut.io.exception.expect(false.B)

        // -----------------------------
        // addi x7, x0, 1
        // -----------------------------
    //    dut.clock.step(1)
    //    dut.io.result.expect(1.U)
    //    dut.io.exception.expect(false.B)

        // -----------------------------
        // addi x8, x0, 2
        // -----------------------------
    //    dut.clock.step(1)
    //    dut.io.result.expect(2.U)
    //    dut.io.exception.expect(false.B)
    //  }
//  }
}