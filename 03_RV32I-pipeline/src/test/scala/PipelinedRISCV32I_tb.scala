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
    test(new PipelinedRV32I("src/test/programs/jalr_BinaryFile_pipelined")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

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
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
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
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(2031.U)  // SUB x6, x4, x5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
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
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
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
      // ----------------------------------------------------
      // EXTENDED TESTS: I-Type Instructions & Corner Cases
      // ----------------------------------------------------
      
      // SEQUENCE 1: I-Type Logical Ops
      dut.io.result.expect("hffffffff".U) // ADDI x14, x0, -1 (0xFFFFFFFF)
      dut.clock.step(1)                   // (3 NOPs + : No longer needed due to Fowarding unit) 1 Execution cycle
      dut.io.result.expect("hffffff00".U) // XORI x15, x14, 255 (0xFFFFFF00)
      dut.clock.step(1)
      dut.io.result.expect("hffffff0f".U) // ORI x16, x15, 15 (0xFFFFFF0F)
      dut.clock.step(1)
      dut.io.result.expect(15.U)          // ANDI x17, x16, 15 (0x0000000F)
      dut.clock.step(1)

      // SEQUENCE 2: I-Type Shifts
      dut.io.result.expect("hfffffff0".U) // SLLI x18, x14, 4 (0xFFFFFFF0)
      dut.clock.step(1)
      dut.io.result.expect("h0fffffff".U) // SRLI x19, x14, 4 (Logical Right Shift)
      dut.clock.step(1)
      dut.io.result.expect("hffffffff".U) // SRAI x20, x14, 4 (Arithmetic Right Shift)
      dut.clock.step(1)

      // SEQUENCE 3: Set Less Than Immediate (Signed vs Unsigned)
      dut.io.result.expect(5.U)           // ADDI x21, x0, 5
      dut.clock.step(1)
      dut.io.result.expect(0.U)           // SLTI x22, x21, -10 (5 < -10 is false)
      dut.clock.step(1)
      dut.io.result.expect(1.U)           // SLTIU x23, x21, -10 (5 < 4294967286 is true)
      dut.clock.step(1)

      // SEQUENCE 4: x0 Immutability Corner Case
      // The ALU will compute 5 + 10 = 15, so the WB output wire will read 15. But RegFile will not allow x0 to be updated, 
      // so the next instruction that tries to read x0 should still get 0.
      dut.io.result.expect(15.U)          // ADDI x0, x21, 10
      dut.clock.step(1)
      
      // However, x0 must NOT be updated inside the Register File! 
      // If x0 is properly hardwired to 0, this next addition will be: 0 + 5 = 5.
      dut.io.result.expect(5.U)           // ADD x24, x0, x21
      dut.clock.step(1)
  
      // Setting up Registers for JAL Test
      dut.io.result.expect(0.U)           // ADDI x1, x0, 0
      dut.clock.step(1)

      dut.io.result.expect(0.U)           // ADDI x2, x0, 0
      dut.clock.step(1)

      dut.io.result.expect(0.U)           // ADDI x3, x0, 0
      dut.clock.step(1)

      dut.io.result.expect(4.U)           // ADDI x1, x1, 4
      dut.clock.step(1)

      dut.io.result.expect(2.U)           // ADDI x2, x2, 2
      dut.clock.step(1)

      dut.io.result.expect(3.U)           // ADDI x3, x3, 3
      dut.clock.step(1)

      // JALR Test: Jump backwards by 3 instructions (-12 bytes + x1 = -8)
      dut.clock.step(1) 
      dut.clock.step(1)

      dut.io.result.expect(4.U)           // ADDI x2, x2, 2
      dut.clock.step(1)

      dut.io.result.expect(6.U)           // ADDI x3, x3, 3
      dut.clock.step(1)

      dut.clock.step(1)
      dut.clock.step(1)

      dut.io.result.expect(6.U)           // ADDI x2, x2, 2
      dut.clock.step(1)

      dut.io.result.expect(9.U)           // ADDI x3, x3, 3
      dut.clock.step(1)

    }
  }
}