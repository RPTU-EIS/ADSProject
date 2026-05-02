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

      dut.io.operandA.poke(10.U)
      dut.io.operandB.poke(10.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect(20.U)
      dut.clock.step(1)

      // Corner Case: Modulo-2^32 Wraparound
      // 0xFFFFFFFF + 1 should wrap to 0
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(1.U)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // 0xFFFFFFFF + F should wrap to E
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke("hF".U)
      dut.io.aluResult.expect("hE".U)
      dut.clock.step(1)

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

      dut.io.operandA.poke(10.U)
      dut.io.operandB.poke(10.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      dut.io.operandA.poke(670.U)
      dut.io.operandB.poke(200.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect(470.U)
      dut.clock.step(1)

      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(1.U)
      dut.io.aluResult.expect("hFFFFFFFE".U)
      dut.clock.step(1)

      // 0 - F should wrap be -F or 0xFFFFFFFF - E
      dut.io.operandA.poke("h0".U)
      dut.io.operandB.poke("hF".U)
      dut.io.aluResult.expect("hFFFFFFF1".U)
      dut.clock.step(1)

    }
  }
}

// Test Bitwise operations (AND, OR, XOR)
class ALUBitwiseTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Bitwise_Tester" should "test AND, OR, XOR operations" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      val opA = "hF0F0F0F0".U
      val opB = "h00FF00FF".U

      // AND
      dut.io.operandA.poke(opA)
      dut.io.operandB.poke(opB)
      dut.io.operation.poke(ALUOp.AND)
      dut.io.aluResult.expect("h00F000F0".U)
      dut.clock.step(1)

      // OR
      dut.io.operation.poke(ALUOp.OR)
      dut.io.aluResult.expect("hF0FFF0FF".U)
      dut.clock.step(1)

      // XOR
      dut.io.operation.poke(ALUOp.XOR)
      dut.io.aluResult.expect("hF00FF00F".U)
      dut.clock.step(1)
    }
  }
}

// Test Shift operations (SLL, SRL, SRA)
class ALUShiftTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Shift_Tester" should "test SLL, SRL, SRA operations including 5-bit masking" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // Shift Left Logical (SLL)
      dut.io.operandA.poke("h00000001".U)
      dut.io.operandB.poke(4.U)
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect("h00000010".U)
      dut.clock.step(1)

      // Corner Case: Masking operandB to 5 bits (0x24 = 36. 36 % 32 = 4)
      dut.io.operandB.poke("h24".U) 
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect("h00000010".U)
      dut.clock.step(1)

      // Shift Right Logical (SRL)
      dut.io.operandA.poke("hF0000000".U)
      dut.io.operandB.poke(4.U)
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect("h0F000000".U) // Fills with 0s
      dut.clock.step(1)

      // Corner Case: Masking operandB to 5 bits (0x28 = 36. 40 % 32 = 8)
      dut.io.operandB.poke("h28".U) 
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect("h00F00000".U)
      dut.clock.step(1)

      // Shift Right Arithmetic (SRA) - No Wraparound for unsigned input, should behave like SRL
      dut.io.operandA.poke("h00000001".U) 
      dut.io.operandB.poke(4.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect("h00000000".U) 
      dut.clock.step(1)

      // -15 shifted by 1
      dut.io.operandA.poke("hFFFFFFF1".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect("hFFFFFFF8".U)
      dut.clock.step(1)

      // 0xF0000000 has MSB=1. Shifting right by 4 should fill with 1s.
      dut.io.operandA.poke("hF0000000".U) 
      dut.io.operandB.poke(4.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect("hFF000000".U) 
      dut.clock.step(1)

      // Corner Case: Maximum valid shift (31 bits) on a negative number
      // Shifting 1000... right by 31 should copy the sign bit 31 times, resulting in all 1s (-1)
      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke(31.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect("hFFFFFFFF".U)
      dut.clock.step(1)

      // Corner Case: Maximum valid shift (31 bits) on a positive number
      // Shifting 0111... right by 31 should result in all 0s
      dut.io.operandA.poke("h7FFFFFFF".U)
      dut.io.operandB.poke(31.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect("h00000000".U)
      dut.clock.step(1)
    }
  }
}

// Test PASSB operation
class ALUPassBTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_PassB_Tester" should "test PASSB operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // Standard test: operandA has data, but should be ignored completely
      dut.io.operandA.poke("hDEADBEEF".U)
      dut.io.operandB.poke("hCAFEBABE".U)
      dut.io.operation.poke(ALUOp.PASSB)
      dut.io.aluResult.expect("hCAFEBABE".U)
      dut.clock.step(1)

      // Corner Case: operandB is 0 (ensure no bits from operandA bleed over)
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.PASSB)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // Corner Case: operandB is max value
      dut.io.operandA.poke(0.U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.PASSB)
      dut.io.aluResult.expect("hFFFFFFFF".U)
      dut.clock.step(1)
    }
  }
}

// Test Comparison operations (SLT, SLTU)
class ALUCompareTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Compare_Tester" should "test SLT (signed) and SLTU (unsigned) operations" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // ----------------------------------------------------
      // SLT (Signed Comparisons)
      // ----------------------------------------------------
      dut.io.operation.poke(ALUOp.SLT)

      // Standard: 10 < 20 (True)
      dut.io.operandA.poke(10.U)
      dut.io.operandB.poke(20.U)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // Corner Case: -1 < 1 (True in signed math)
      // 0xFFFFFFFF is interpreted as -1
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(1.U)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // Corner Case: 1 < -1 (False)
      dut.io.operandA.poke(1.U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // ----------------------------------------------------
      // SLTU (Unsigned Comparisons)
      // ----------------------------------------------------
      dut.io.operation.poke(ALUOp.SLTU)

      // Standard: 10 < 20 (True)
      dut.io.operandA.poke(10.U)
      dut.io.operandB.poke(20.U)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // Corner Case: 0xFFFFFFFF < 1 (False in unsigned math)
      // 0xFFFFFFFF is interpreted as 4,294,967,295
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(1.U)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // Corner Case: 1 < 0xFFFFFFFF (True)
      dut.io.operandA.poke(1.U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)
    }
  }
}