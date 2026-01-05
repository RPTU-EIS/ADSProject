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

      // 1. Standard Case
      dut.io.operandA.poke(10.U)
      dut.io.operandB.poke(10.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect(20.U)
      dut.clock.step(1)

      //  ToDo: add more test cases for ADD operation
      // 2. Test for zero (Identity Property)
      // 45 + 0 = 45
      dut.io.operandA.poke(45.U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.clock.step(1)
      dut.io.aluResult.expect(45.U)

      // 3. Test Maximum Value (Boundary Check)
      // 0xFFFFFFFE + 1 = 0xFFFFFFFF (Max 32-bit unsigned integer)
      dut.io.operandA.poke("hFFFFFFFE".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.clock.step(1)
      dut.io.aluResult.expect("hFFFFFFFF".U)

      // 4. Test Overflow (Wrap Around)
      // 0xFFFFFFFF + 1 = 0 (In 32-bit arithmetic, this wraps back to start)
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.clock.step(1)
      dut.io.aluResult.expect(0.U)

    }
  }
}

// ---------------------------------------------------
// ToDo: Add test classes for all other ALU operations
//---------------------------------------------------
// Test SUB operation
class ALUSubTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sub_Tester" should "test SUB operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // 1. Standard Case (Non-Zero Result)
      // 20 - 15 = 5
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.operandA.poke(20.U)
      dut.io.operandB.poke(15.U)
      dut.clock.step(1)
      dut.io.aluResult.expect(5.U)

      // 2. Zero Result Case
      // 10 - 10 = 0
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.operandA.poke(10.U)
      dut.io.operandB.poke(10.U)
      dut.clock.step(1)
      dut.io.aluResult.expect(0.U)

      // 3. Underflow Case (Negative Result)
      // 10 - 20 = -10. In 32-bit unsigned, this wraps to 0xFFFFFFF6
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.operandA.poke(10.U)
      dut.io.operandB.poke(20.U)
      dut.clock.step(1)
      dut.io.aluResult.expect("hFFFFFFF6".U)
    }
  }
}

// Test LOGIC operations
class ALULogicTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Logic_Tester" should "test AND, OR, XOR" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      val a = "b11001100".U(32.W) // Mask A
      val b = "b10101010".U(32.W) // Mask B

      // 1. Test AND
      // 11001100 & 10101010 = 10001000 (0x88)
      dut.io.operation.poke(ALUOp.AND)
      dut.io.operandA.poke(a)
      dut.io.operandB.poke(b)
      dut.clock.step(1)
      dut.io.aluResult.expect("b10001000".U)

      // 2. Test OR
      // 11001100 | 10101010 = 11101110 (0xEE)
      dut.io.operation.poke(ALUOp.OR)
      dut.io.operandA.poke(a)
      dut.io.operandB.poke(b)
      dut.clock.step(1)
      dut.io.aluResult.expect("b11101110".U)

      // 3. Test XOR
      // 11001100 ^ 10101010 = 01100110 (0x66)
      dut.io.operation.poke(ALUOp.XOR)
      dut.io.operandA.poke(a)
      dut.io.operandB.poke(b)
      dut.clock.step(1)
      dut.io.aluResult.expect("b01100110".U)
    }
  }
}

// Test SHIFT operations
class ALUShiftTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Shift_Tester" should "test SLL, SRL, SRA" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      // 1. Shift Left Logical (SLL)
      // 1 << 5 = 32
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.operandA.poke(1.U)
      dut.io.operandB.poke(5.U)
      dut.clock.step(1)
      dut.io.aluResult.expect(32.U)

      // 2. Shift Right Logical (SRL)
      // 32 >> 5 = 1
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.operandA.poke(32.U)
      dut.io.operandB.poke(5.U)
      dut.clock.step(1)
      dut.io.aluResult.expect(1.U)

      // 3. Shift Right Arithmetic (SRA)
      // -4 (111...1100) >> 1 should be -2 (111...1110)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.operandA.poke("hFFFFFFFC".U) // -4 in 32-bit hex
      dut.io.operandB.poke(1.U)
      dut.clock.step(1)
      dut.io.aluResult.expect("hFFFFFFFE".U) // -2 in 32-bit hex
    }
  }
}

// Test COMPARISON operations (SLT, SLTU)
class ALUCompareTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Compare_Tester" should "test SLT and SLTU" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      // 1. SLT (Signed Less Than)
      // Comparison: -1 < 1 ? YES (Result should be 1)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.operandA.poke("hFFFFFFFF".U) // -1
      dut.io.operandB.poke(1.U)
      dut.clock.step(1)
      dut.io.aluResult.expect(1.U)

      // 2. SLTU (Unsigned Less Than)
      // Comparison: 4,294,967,295 < 1 ? NO (Result should be 0)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.operandA.poke("hFFFFFFFF".U) // Max Unsigned Int
      dut.io.operandB.poke(1.U)
      dut.clock.step(1)
      dut.io.aluResult.expect(0.U)

      // 3. Equality Check (Edge Case)
      // 10 < 10 ? NO (Result should be 0)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.operandA.poke(10.U)
      dut.io.operandB.poke(10.U)
      dut.clock.step(1)
      dut.io.aluResult.expect(0.U)
    }
  }
}

// Test PASSB operation
class ALUPassBTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_PassB_Tester" should "test PASSB operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // Should output Operand B, ignoring Operand A
      dut.io.operation.poke(ALUOp.PASSB)
      dut.io.operandA.poke(12345.U)
      dut.io.operandB.poke(9999.U)
      dut.clock.step(1)

      dut.io.aluResult.expect(9999.U)
    }
  }
}