// ADS I Class Project
// Pipelined RISC-V Core with Hazard Detection and Resolution
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 10/31/2025 by Tobias Jauch (tobias.jauch@rptu.de)

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

import Assignment02._

// Test ADD operation
class ALUAddTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Add_Tester" should "test ADD operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // Basic addition
      dut.io.operandA.poke(10.U)
      dut.io.operandB.poke(10.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect(20.U)
      dut.clock.step(1)

      // Add zero (identity)
      dut.io.operandA.poke(42.U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect(42.U)
      dut.clock.step(1)

      // Add two zeros
      dut.io.operandA.poke(0.U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // Overflow wraparound: 0xFFFFFFFF + 1 = 0
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // Large number addition with wraparound
      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke("h80000000".U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // Cross from positive to negative (signed interpretation)
      dut.io.operandA.poke("h7FFFFFFF".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect("h80000000".U)
      dut.clock.step(1)

    }
  }
}

// =============================================================================
// Test SUB operation
// =============================================================================
class ALUSubTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sub_Tester" should "test SUB operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // Basic subtraction
      dut.io.operandA.poke(10.U)
      dut.io.operandB.poke(3.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect(7.U)
      dut.clock.step(1)

      // Subtract equal numbers to get zero
      dut.io.operandA.poke(42.U)
      dut.io.operandB.poke(42.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // Subtract zero (identity)
      dut.io.operandA.poke(100.U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect(100.U)
      dut.clock.step(1)

      // Underflow wraparound: 0 - 1 = 0xFFFFFFFF
      dut.io.operandA.poke(0.U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect("hFFFFFFFF".U)
      dut.clock.step(1)

      // Subtraction resulting in large unsigned value
      dut.io.operandA.poke(1.U)
      dut.io.operandB.poke(2.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect("hFFFFFFFF".U)
      dut.clock.step(1)

    }
  }
}

// =============================================================================
// Test AND operation
// =============================================================================
class ALUAndTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_And_Tester" should "test AND operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // Basic AND
      dut.io.operandA.poke("h0F0F0F0F".U)
      dut.io.operandB.poke("h00FF00FF".U)
      dut.io.operation.poke(ALUOp.AND)
      dut.io.aluResult.expect("h000F000F".U)
      dut.clock.step(1)

      // AND with all ones (identity)
      dut.io.operandA.poke("h12345678".U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.AND)
      dut.io.aluResult.expect("h12345678".U)
      dut.clock.step(1)

      // AND with zero gives zero
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.AND)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // AND with itself gives same value
      dut.io.operandA.poke("hABCDEF12".U)
      dut.io.operandB.poke("hABCDEF12".U)
      dut.io.operation.poke(ALUOp.AND)
      dut.io.aluResult.expect("hABCDEF12".U)
      dut.clock.step(1)

    }
  }
}

// =============================================================================
// Test OR operation
// =============================================================================
class ALUOrTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Or_Tester" should "test OR operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // Basic OR
      dut.io.operandA.poke("h0F0F0F0F".U)
      dut.io.operandB.poke("h00FF00FF".U)
      dut.io.operation.poke(ALUOp.OR)
      dut.io.aluResult.expect("h0FFF0FFF".U)
      dut.clock.step(1)

      // OR with zero (identity)
      dut.io.operandA.poke("h12345678".U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.OR)
      dut.io.aluResult.expect("h12345678".U)
      dut.clock.step(1)

      // OR with all ones gives all ones
      dut.io.operandA.poke("h12345678".U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.OR)
      dut.io.aluResult.expect("hFFFFFFFF".U)
      dut.clock.step(1)

      // OR two zeros
      dut.io.operandA.poke(0.U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.OR)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

    }
  }
}

// =============================================================================
// Test XOR operation
// =============================================================================
class ALUXorTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Xor_Tester" should "test XOR operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // Basic XOR
      dut.io.operandA.poke("h0F0F0F0F".U)
      dut.io.operandB.poke("h00FF00FF".U)
      dut.io.operation.poke(ALUOp.XOR)
      dut.io.aluResult.expect("h0FF00FF0".U)
      dut.clock.step(1)

      // XOR with zero (identity)
      dut.io.operandA.poke("h12345678".U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.XOR)
      dut.io.aluResult.expect("h12345678".U)
      dut.clock.step(1)

      // XOR with itself gives zero
      dut.io.operandA.poke("h12345678".U)
      dut.io.operandB.poke("h12345678".U)
      dut.io.operation.poke(ALUOp.XOR)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // XOR with all ones inverts bits
      dut.io.operandA.poke("h0F0F0F0F".U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.XOR)
      dut.io.aluResult.expect("hF0F0F0F0".U)
      dut.clock.step(1)

    }
  }
}

// =============================================================================
// Test SLL (Shift Left Logical) operation
// =============================================================================
class ALUSllTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sll_Tester" should "test SLL operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // Basic shift left
      dut.io.operandA.poke(1.U)
      dut.io.operandB.poke(4.U)
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect(16.U)
      dut.clock.step(1)

      // Shift left by zero (no change)
      dut.io.operandA.poke("h12345678".U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect("h12345678".U)
      dut.clock.step(1)

      // Shift left by 31 (maximum)
      dut.io.operandA.poke(1.U)
      dut.io.operandB.poke(31.U)
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect("h80000000".U)
      dut.clock.step(1)

      // CRITICAL: Only lower 5 bits used (32 -> shift by 0)
      dut.io.operandA.poke(1.U)
      dut.io.operandB.poke(32.U)
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // Only lower 5 bits used (33 -> shift by 1)
      dut.io.operandA.poke(1.U)
      dut.io.operandB.poke(33.U)
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect(2.U)
      dut.clock.step(1)

      // Shift bits out of MSB
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(16.U)
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect("hFFFF0000".U)
      dut.clock.step(1)

    }
  }
}

// =============================================================================
// Test SRL (Shift Right Logical) operation
// =============================================================================
class ALUSrlTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Srl_Tester" should "test SRL operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // Basic shift right
      dut.io.operandA.poke(16.U)
      dut.io.operandB.poke(2.U)
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect(4.U)
      dut.clock.step(1)

      // Shift right by zero (no change)
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect("hFFFFFFFF".U)
      dut.clock.step(1)

      // CRITICAL: Fill with zeros (not sign extend)
      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke(4.U)
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect("h08000000".U)
      dut.clock.step(1)

      // Shift right by 31
      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke(31.U)
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // Shift all bits out
      dut.io.operandA.poke("h7FFFFFFF".U)
      dut.io.operandB.poke(31.U)
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // Only lower 5 bits used (32 -> shift by 0)
      dut.io.operandA.poke("hABCDEF12".U)
      dut.io.operandB.poke(32.U)
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect("hABCDEF12".U)
      dut.clock.step(1)

    }
  }
}

// =============================================================================
// Test SRA (Shift Right Arithmetic) operation
// =============================================================================
class ALUSraTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sra_Tester" should "test SRA operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // Shift positive number (same as logical)
      dut.io.operandA.poke("h40000000".U)
      dut.io.operandB.poke(4.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect("h04000000".U)
      dut.clock.step(1)

      // CRITICAL: Sign extension for negative numbers
      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke(4.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect("hF8000000".U)
      dut.clock.step(1)

      // -1 stays -1 regardless of shift
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(16.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect("hFFFFFFFF".U)
      dut.clock.step(1)

      // Shift by zero (no change)
      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect("h80000000".U)
      dut.clock.step(1)

      // Shift negative by 31 gives all ones
      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke(31.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect("hFFFFFFFF".U)
      dut.clock.step(1)

      // Shift positive by 31 gives zero
      dut.io.operandA.poke("h7FFFFFFF".U)
      dut.io.operandB.poke(31.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

    }
  }
}

// =============================================================================
// Test SLT (Set Less Than - Signed) operation
// =============================================================================
class ALUSltTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Slt_Tester" should "test SLT operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // A < B (both positive) -> 1
      dut.io.operandA.poke(5.U)
      dut.io.operandB.poke(10.U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // A > B -> 0
      dut.io.operandA.poke(10.U)
      dut.io.operandB.poke(5.U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // A == B -> 0
      dut.io.operandA.poke(42.U)
      dut.io.operandB.poke(42.U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // CRITICAL: 0xFFFFFFFF is -1 (signed), -1 < 1 -> 1
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // CRITICAL: Sign boundary - most negative < most positive
      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke("h7FFFFFFF".U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // Most positive > most negative -> 0
      dut.io.operandA.poke("h7FFFFFFF".U)
      dut.io.operandB.poke("h80000000".U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // Two negative numbers: -2 < -1 -> 1
      dut.io.operandA.poke("hFFFFFFFE".U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

    }
  }
}

// =============================================================================
// Test SLTU (Set Less Than - Unsigned) operation
// =============================================================================
class ALUSltuTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sltu_Tester" should "test SLTU operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // A < B -> 1
      dut.io.operandA.poke(5.U)
      dut.io.operandB.poke(10.U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // A > B -> 0
      dut.io.operandA.poke(10.U)
      dut.io.operandB.poke(5.U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // A == B -> 0
      dut.io.operandA.poke("hABCDEF00".U)
      dut.io.operandB.poke("hABCDEF00".U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // CRITICAL: 0xFFFFFFFF is large positive (unsigned), not -1
      // 4294967295 > 1 -> 0
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // 1 < 4294967295 -> 1
      dut.io.operandA.poke(1.U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // Zero comparison
      dut.io.operandA.poke(0.U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // 0x80000000 is large positive (2147483648) > 0x7FFFFFFF (2147483647)
      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke("h7FFFFFFF".U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

    }
  }
}

// =============================================================================
// Test PASSB operation
// =============================================================================
class ALUPassbTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Passb_Tester" should "test PASSB operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // Pass operandB unchanged (operandA ignored)
      dut.io.operandA.poke("hDEADBEEF".U)
      dut.io.operandB.poke("h12345678".U)
      dut.io.operation.poke(ALUOp.PASSB)
      dut.io.aluResult.expect("h12345678".U)
      dut.clock.step(1)

      // Pass zero
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.PASSB)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // Pass all ones
      dut.io.operandA.poke(0.U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.PASSB)
      dut.io.aluResult.expect("hFFFFFFFF".U)
      dut.clock.step(1)

      // Verify operandA is completely ignored
      dut.io.operandA.poke(0.U)
      dut.io.operandB.poke("hCAFEBABE".U)
      dut.io.operation.poke(ALUOp.PASSB)
      dut.io.aluResult.expect("hCAFEBABE".U)
      dut.clock.step(1)

      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke("hCAFEBABE".U)
      dut.io.operation.poke(ALUOp.PASSB)
      dut.io.aluResult.expect("hCAFEBABE".U)
      dut.clock.step(1)

    }
  }
}

