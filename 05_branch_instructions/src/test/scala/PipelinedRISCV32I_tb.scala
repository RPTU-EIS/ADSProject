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


// ======================================================
// Branch & Jump Tests
// ======================================================

// ADDI x1, x0, 4
 dut.clock.step(1)   
dut.io.result.expect(4.U)
dut.io.exception.expect(false.B)
dut.clock.step(1)

// ADDI x2, x0, 5
dut.io.result.expect(5.U)
dut.io.exception.expect(false.B)
dut.clock.step(1)

// ADDI x3, x0, -1
dut.io.result.expect("hFFFFFFFF".U)
dut.io.exception.expect(false.B)
dut.clock.step(1)

// ADDI x4, x0, 1
dut.io.result.expect(1.U)
dut.io.exception.expect(false.B)
dut.clock.step(1)


// -------------------------
// BEQ NOT TAKEN
// -------------------------

// BEQ
dut.io.exception.expect(false.B)
dut.clock.step(1)

// ADDI x5,10
dut.io.result.expect(10.U)
dut.io.exception.expect(false.B)
dut.clock.step(1)


// -------------------------
// BEQ TAKEN
// -------------------------

// ADD x6,x1,x2
dut.io.result.expect(9.U)
dut.io.exception.expect(false.B)
dut.clock.step(1)

// ADDI x7,9
dut.io.result.expect(9.U)
dut.io.exception.expect(false.B)
dut.clock.step(1)

// BEQ (taken)
dut.io.exception.expect(false.B)
dut.clock.step(1)

// Two instructions flushed
dut.clock.step(2)

// Target
dut.io.result.expect(20.U)
dut.io.exception.expect(false.B)
dut.clock.step(1)


// -------------------------
// BNE NOT TAKEN
// -------------------------

// BNE
dut.io.exception.expect(false.B)
dut.clock.step(1)

// ADDI x9,30
dut.io.result.expect(30.U)
dut.io.exception.expect(false.B)
dut.clock.step(1)


// -------------------------
// BNE TAKEN
// -------------------------

dut.io.exception.expect(false.B)
dut.clock.step(1)

// flushed
dut.clock.step(2)

// target
dut.io.result.expect(40.U)
dut.io.exception.expect(false.B)
dut.clock.step(1)


// -------------------------
// BLT TAKEN
// -------------------------

dut.io.exception.expect(false.B)
dut.clock.step(1)

// flushed
dut.clock.step(1)
dut.clock.step(1)

// target
dut.io.result.expect(50.U)
dut.io.exception.expect(false.B)
dut.clock.step(1)


// -------------------------
// BGE TAKEN
// -------------------------

dut.io.exception.expect(false.B)
dut.clock.step(1)

// flushed
dut.clock.step(1)
dut.clock.step(1)

// target
dut.io.result.expect(60.U)
dut.io.exception.expect(false.B)
dut.clock.step(1)


// -------------------------
// BLTU NOT TAKEN
// -------------------------

dut.io.exception.expect(false.B)
dut.clock.step(1)

dut.io.result.expect(70.U)
dut.io.exception.expect(false.B)
dut.clock.step(1)


// -------------------------
// BGEU TAKEN
// -------------------------

dut.io.exception.expect(false.B)
dut.clock.step(1)

// flushed
dut.clock.step(1)
dut.clock.step(1)

// target
dut.io.result.expect(80.U)
dut.io.exception.expect(false.B)
dut.clock.step(1)


// -------------------------
// JAL
// -------------------------

dut.io.exception.expect(false.B)
dut.clock.step(1)

// two flushed instructions
dut.clock.step(2)

// target
dut.io.result.expect(90.U)
dut.io.exception.expect(false.B)
dut.clock.step(1)


// -------------------------
// JALR
// -------------------------

// ADDI
dut.io.exception.expect(false.B)
dut.clock.step(1)

// ADDI
dut.io.result.expect(12.U)
dut.io.exception.expect(false.B)
dut.clock.step(1)

// JALR
dut.io.exception.expect(false.B)
dut.clock.step(1)

// two flushed instructions
dut.clock.step(1)
dut.clock.step(1)

// target
dut.io.result.expect(100.U)
dut.io.exception.expect(false.B)
dut.clock.step(1)       
    }
  }
}
