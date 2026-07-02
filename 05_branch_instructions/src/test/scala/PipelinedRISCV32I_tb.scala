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
      dut.io.result.expect("hFFFFFFFF".U) // ADDI x14, x0, -1
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      //B-Type Test

      // BEQ Test
      dut.io.result.expect(5.U)     // addi x14,x0,5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(5.U)     // addi x15,x0,5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U) // beq x14,x15,8
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U) // addi x16,x0,99
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(1.U)  //addi x17,x0,1
      dut.io.exception.expect(false.B)

      // BEQ Not Taken Test
      dut.clock.step(1)
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

      // BNE Not Taken Test
      dut.clock.step(1)
      dut.clock.step(1)
      dut.io.result.expect(37.U) // ADDI x26, x0, 37
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(37.U) // ADDI x27, x0, 37
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U) // BNE x26, x27, 8
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(99.U) // Branch NOT taken, so this executes
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(1.U) // ADDI x29, x0, 1
      dut.io.exception.expect(false.B)

      // BLT Taken Test
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

      // BLT Not Taken Test
      dut.clock.step(1)
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

      // BGE Taken Test
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

      // BGE Not Taken Test
      dut.clock.step(1)
      dut.clock.step(1)
      dut.io.result.expect(12.U) // ADDI x8, x0, 12
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(30.U) // ADDI x9, x0, 30
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(1.U) // BGE x8, x9, 8
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(99.U) // ADDI x10, x0, 99 executes
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(1.U) // ADDI x11, x0, 1 executes
      dut.io.exception.expect(false.B)

      // BLTU Taken Test
      dut.clock.step(1)
      dut.io.result.expect(15.U) // ADDI x12, x0, 15
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(40.U) // ADDI x13, x0, 40
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(1.U) // BLTU x12, x13, 8
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U) // Flushed ADDI x14, x0, 99
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(1.U) // ADDI x15, x0, 1
      dut.io.exception.expect(false.B)

      // BLTU NOT TAKEN TEST
      dut.clock.step(1)
      dut.clock.step(1)
      dut.io.result.expect(60.U) // addi x20, x0, 60
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(25.U) // addi x21, x0, 25
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U) // bltu x20, x21, 8
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(99.U) // addi x22, x0, 99 executes
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(1.U)  // addi x23, x0, 1
      dut.io.exception.expect(false.B)

      // BGEU TAKEN TEST
      dut.clock.step(1)
      dut.io.result.expect(80.U)      // ADDI x22, x0, 80
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(25.U)      // ADDI x23, x0, 25
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)       // BGEU (ALU result = 0)
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)       // Flushed instruction
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(1.U)       // ADDI x25, x0, 1
      dut.io.exception.expect(false.B)

      // BGEU NOT TAKEN TEST
      dut.clock.step(1)
      dut.clock.step(1)
      dut.io.result.expect(30.U)      // ADDI x26, x0, 30
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(75.U)      // ADDI x27, x0, 75
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(1.U)       // BGEU (30 < 75 = true, returns 1)
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(99.U)      // ADDI x28, x0, 99 (executes)
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(1.U)       // ADDI x29, x0, 1
      dut.io.exception.expect(false.B)
    }
  }

  "JAL_Test" should "work" in {
    test(new PipelinedRV32I("src/test/programs/BinaryFile_jal")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      dut.clock.setTimeout(0)
      dut.clock.step(5)
      // JAL x5, 8 (target = PC + 8)
      dut.io.result.expect(4.U)     // x5 = PC+4 = 4
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      // Flushed instruction (addi x6 should be skipped)
      dut.io.result.expect(0.U)
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      // ADDI x7, x0, 1
      dut.io.result.expect(1.U)
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      // ADDI x8, x0, 2
      dut.io.result.expect(2.U)
      dut.io.exception.expect(false.B)
    }
  }
}