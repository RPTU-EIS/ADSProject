// ADS I Class Project
// Pipelined RISC-V Core - Testbench for Task 04: Hazard Detection and Forwarding
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern

package PipelinedRV32I_Tester

import chisel3._
import chiseltest._
import PipelinedRV32I._
import org.scalatest.flatspec.AnyFlatSpec

class PipelinedRISCV32ITest extends AnyFlatSpec with ChiselScalatestTester {

  "Forwarding_RAW_Hazard_MEM" should "work" in {
    // Test: RAW hazard resolved by forwarding from MEM stage
    // ADDI x1, x0, 10   (cycle 4: writes x1=10 in WB)
    // ADD x2, x1, x0    (cycle 4: reads x1 in EX, needs forwarding from MEM/exBarrier)
    // Expected: x2 = 10 + 0 = 10
    test(new PipelinedRV32I("src/test/programs/BinaryFile_forwarding_raw_mem")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // Initial cycles for pipeline to fill
      dut.clock.step(5)

      // Cycle 5: ADDI x1, x0, 10 writes back
      dut.io.result.expect(10.U)
      dut.io.exception.expect(false.B)

      // Cycle 6: ADD x2, x1, x0 (x1 forwarded from MEM, result should be 10)
      dut.clock.step(1)
      dut.io.result.expect(10.U)
      dut.io.exception.expect(false.B)
    }
  }

  "Forwarding_RAW_Hazard_WB" should "work" in {
    // Test: RAW hazard resolved by forwarding from WB stage
    // ADDI x1, x0, 15
    // ADDI x2, x0, 8
    // ADD x3, x1, x2    (cycle 6: x1 from WB, x2 from MEM/exBarrier - needs dual forwarding)
    // Expected: x3 = 15 + 8 = 23
    test(new PipelinedRV32I("src/test/programs/BinaryFile_forwarding_raw_wb")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.clock.step(5)
      dut.io.result.expect(15.U)  // ADDI x1, x0, 15
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(8.U)   // ADDI x2, x0, 8
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(23.U)  // ADD x3, x1, x2 (both forwarded)
      dut.io.exception.expect(false.B)
    }
  }

  "Forwarding_Both_Operands_MEM" should "work" in {
    // Test: Both operands from same register, forwarded from MEM
    // ADDI x1, x0, 7
    // ADD x2, x1, x1    (both operands are x1, need forwarding from MEM)
    // Expected: x2 = 7 + 7 = 14
    test(new PipelinedRV32I("src/test/programs/BinaryFile_forwarding_both_mem")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.clock.step(5)
      dut.io.result.expect(7.U)   // ADDI x1, x0, 7
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(14.U)  // ADD x2, x1, x1 (both forwarded from MEM)
      dut.io.exception.expect(false.B)
    }
  }

  "Forwarding_Chain_Dependencies" should "work" in {
    // Test: Chain of data dependencies
    // ADDI x1, x0, 3
    // ADD x2, x1, x1   (x2 = 6, needs forwarding from MEM)
    // ADD x3, x2, x0   (x3 = 6, needs forwarding from WB)
    // ADD x4, x3, x0   (x4 = 6, needs forwarding)
    // Expected chain: 3 -> 6 -> 6 -> 6
    test(new PipelinedRV32I("src/test/programs/BinaryFile_forwarding_chain")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.clock.step(5)
      dut.io.result.expect(3.U)   // ADDI x1, x0, 3
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(6.U)   // ADD x2, x1, x1 (6)
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(6.U)   // ADD x3, x2, x0 (6)
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(6.U)   // ADD x4, x3, x0 (6)
      dut.io.exception.expect(false.B)
    }
  }

  "Forwarding_Mixed_Sources" should "work" in {
    // Test: One operand from MEM, one from WB
    // ADDI x1, x0, 20
    // ADDI x2, x0, 5
    // SUB x3, x1, x2   (x1 from MEM, x2 from WB, both need forwarding)
    // Expected: x3 = 20 - 5 = 15
    test(new PipelinedRV32I("src/test/programs/BinaryFile_forwarding_mixed")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.clock.step(5)
      dut.io.result.expect(20.U)  // ADDI x1, x0, 20
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(5.U)   // ADDI x2, x0, 5
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(15.U)  // SUB x3, x1, x2 (15)
      dut.io.exception.expect(false.B)
    }
  }

  "Forwarding_Logical_Operations" should "work" in {
    // Test: Forwarding with logical operations
    // ADDI x1, x0, 0xFF
    // ADDI x2, x0, 0x0F
    // AND x3, x1, x2   (both forwarded from MEM and MEM/WB)
    // OR x4, x1, x2    (both forwarded)
    // XOR x5, x1, x2   (both forwarded)
    // Expected: x3=0x0F, x4=0xFF, x5=0xF0
    test(new PipelinedRV32I("src/test/programs/BinaryFile_forwarding_logical")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.clock.step(5)
      dut.io.result.expect(0xFF.U)  // ADDI x1, x0, 0xFF
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(0x0F.U)  // ADDI x2, x0, 0x0F
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(0x0F.U)  // AND x3, x1, x2
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(0xFF.U)  // OR x4, x1, x2
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(0xF0.U)  // XOR x5, x1, x2
      dut.io.exception.expect(false.B)
    }
  }

  "Forwarding_Shift_Operations" should "work" in {
    // Test: Forwarding with shift operations
    // ADDI x1, x0, 4
    // ADDI x2, x0, 2
    // SLL x3, x1, x2   (shift 4 left by 2 = 16)
    // SRL x4, x3, x2   (shift 16 right by 2 = 4)
    // Expected: x3=16, x4=4
    test(new PipelinedRV32I("src/test/programs/BinaryFile_forwarding_shift")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.clock.step(5)
      dut.io.result.expect(4.U)   // ADDI x1, x0, 4
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(2.U)   // ADDI x2, x0, 2
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(16.U)  // SLL x3, x1, x2 (both forwarded)
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(4.U)   // SRL x4, x3, x2 (both forwarded)
      dut.io.exception.expect(false.B)
    }
  }

  "Forwarding_Comparison_Operations" should "work" in {
    // Test: Forwarding with comparison operations
    // ADDI x1, x0, 10
    // ADDI x2, x0, 20
    // SLT x3, x1, x2   (10 < 20 = 1)
    // SLT x4, x2, x1   (20 < 10 = 0)
    // Expected: x3=1, x4=0
    test(new PipelinedRV32I("src/test/programs/BinaryFile_forwarding_cmp")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.clock.step(5)
      dut.io.result.expect(10.U)  // ADDI x1, x0, 10
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(20.U)  // ADDI x2, x0, 20
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(1.U)   // SLT x3, x1, x2 (both forwarded, 10<20=1)
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(0.U)   // SLT x4, x2, x1 (both forwarded, 20<10=0)
      dut.io.exception.expect(false.B)
    }
  }

  "Forwarding_No_Hazard" should "work" in {
    // Test: Instructions with no dependencies (no hazards)
    // ADDI x1, x0, 100
    // ADDI x2, x0, 200
    // ADDI x3, x0, 300
    // Expected: x1=100, x2=200, x3=300 (no forwarding needed)
    test(new PipelinedRV32I("src/test/programs/BinaryFile_no_hazard")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.clock.step(5)
      dut.io.result.expect(100.U)
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(200.U)
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(300.U)
      dut.io.exception.expect(false.B)
    }
  }

  "Forwarding_Multiple_Chains" should "work" in {
    // Test: Multiple independent dependency chains
    // Chain 1: x1=5 -> x2=10 -> x3=20
    // Chain 2: x4=3 -> x5=6  -> x6=12
    // ADDI x1, x0, 5
    // ADDI x4, x0, 3
    // ADD x2, x1, x1   (x2=10)
    // ADD x5, x4, x4   (x5=6)
    // ADD x3, x2, x0   (x3=10, x2 from WB)
    // ADD x6, x5, x0   (x6=6, x5 from WB)
    test(new PipelinedRV32I("src/test/programs/BinaryFile_multi_chains")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.clock.step(5)
      dut.io.result.expect(5.U)   // ADDI x1, x0, 5
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(3.U)   // ADDI x4, x0, 3
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(10.U)  // ADD x2, x1, x1 (x2=10)
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(6.U)   // ADD x5, x4, x4 (x5=6)
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(10.U)  // ADD x3, x2, x0 (x3=10, x2 from WB)
      dut.io.exception.expect(false.B)

      dut.clock.step(1)
      dut.io.result.expect(6.U)   // ADD x6, x5, x0 (x6=6, x5 from WB)
      dut.io.exception.expect(false.B)
    }
  }
}
