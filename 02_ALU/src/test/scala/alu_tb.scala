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

      // zero + zero
      dut.io.operandA.poke(0.U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // add zero to value
      dut.io.operandA.poke(42.U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect(42.U)
      dut.clock.step(1)

      // wraparound: 0xFFFFFFFF + 1 = 0
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // max + max = 0xFFFFFFFE
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect("hFFFFFFFE".U)
      dut.clock.step(1)

      // large values
      dut.io.operandA.poke("h7FFFFFFF".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect("h80000000".U)
      dut.clock.step(1)
    }
  }
}

// Test SUB operation
class ALUSubTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sub_Tester" should "test SUB operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // basic subtraction
      dut.io.operandA.poke(20.U)
      dut.io.operandB.poke(10.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect(10.U)
      dut.clock.step(1)

      // subtract from self = 0
      dut.io.operandA.poke(42.U)
      dut.io.operandB.poke(42.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // subtract zero
      dut.io.operandA.poke(100.U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect(100.U)
      dut.clock.step(1)

      // wraparound: 0 - 1 = 0xFFFFFFFF
      dut.io.operandA.poke(0.U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect("hFFFFFFFF".U)
      dut.clock.step(1)

      // borrow wraparound: 5 - 10 = 0xFFFFFFFB
      dut.io.operandA.poke(5.U)
      dut.io.operandB.poke(10.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect("hFFFFFFFB".U)
      dut.clock.step(1)
    }
  }
}

// Test AND operation
class ALUAndTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_And_Tester" should "test AND operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // basic AND
      dut.io.operandA.poke("hFF".U)
      dut.io.operandB.poke("h0F".U)
      dut.io.operation.poke(ALUOp.AND)
      dut.io.aluResult.expect("h0F".U)
      dut.clock.step(1)

      // AND with zero = 0
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.AND)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // AND with all-ones = identity
      dut.io.operandA.poke("hABCD1234".U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.AND)
      dut.io.aluResult.expect("hABCD1234".U)
      dut.clock.step(1)

      // AND with itself
      dut.io.operandA.poke("hDEADBEEF".U)
      dut.io.operandB.poke("hDEADBEEF".U)
      dut.io.operation.poke(ALUOp.AND)
      dut.io.aluResult.expect("hDEADBEEF".U)
      dut.clock.step(1)

      // alternating bits
      dut.io.operandA.poke("hAAAAAAAA".U)
      dut.io.operandB.poke("h55555555".U)
      dut.io.operation.poke(ALUOp.AND)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)
    }
  }
}

// Test OR operation
class ALUOrTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Or_Tester" should "test OR operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // basic OR
      dut.io.operandA.poke("hF0".U)
      dut.io.operandB.poke("h0F".U)
      dut.io.operation.poke(ALUOp.OR)
      dut.io.aluResult.expect("hFF".U)
      dut.clock.step(1)

      // OR with zero = identity
      dut.io.operandA.poke("hABCD1234".U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.OR)
      dut.io.aluResult.expect("hABCD1234".U)
      dut.clock.step(1)

      // OR with all-ones = all-ones
      dut.io.operandA.poke("h12345678".U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.OR)
      dut.io.aluResult.expect("hFFFFFFFF".U)
      dut.clock.step(1)

      // alternating bits
      dut.io.operandA.poke("hAAAAAAAA".U)
      dut.io.operandB.poke("h55555555".U)
      dut.io.operation.poke(ALUOp.OR)
      dut.io.aluResult.expect("hFFFFFFFF".U)
      dut.clock.step(1)
    }
  }
}

// Test XOR operation
class ALUXorTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Xor_Tester" should "test XOR operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // basic XOR
      dut.io.operandA.poke("hFF".U)
      dut.io.operandB.poke("h0F".U)
      dut.io.operation.poke(ALUOp.XOR)
      dut.io.aluResult.expect("hF0".U)
      dut.clock.step(1)

      // XOR with zero = identity
      dut.io.operandA.poke("hABCD1234".U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.XOR)
      dut.io.aluResult.expect("hABCD1234".U)
      dut.clock.step(1)

      // XOR with itself = 0
      dut.io.operandA.poke("hDEADBEEF".U)
      dut.io.operandB.poke("hDEADBEEF".U)
      dut.io.operation.poke(ALUOp.XOR)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // XOR with all-ones = bitwise NOT
      dut.io.operandA.poke("hABCDEF01".U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.XOR)
      dut.io.aluResult.expect("h543210FE".U)
      dut.clock.step(1)
    }
  }
}

// Test SLL operation
class ALUSllTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sll_Tester" should "test SLL operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // shift by 0
      dut.io.operandA.poke(1.U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // shift by 1
      dut.io.operandA.poke(1.U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect(2.U)
      dut.clock.step(1)

      // shift to MSB: 1 << 31 = 0x80000000
      dut.io.operandA.poke(1.U)
      dut.io.operandB.poke(31.U)
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect("h80000000".U)
      dut.clock.step(1)

      // overflow wraps: MSB bit shifted out
      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // only lower 5 bits of operandB used: shift by 32 (=0) wraps to shift by 0
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(32.U)  // lower 5 bits = 0
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect("hFFFFFFFF".U)
      dut.clock.step(1)

      // shift all-ones left by 1
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect("hFFFFFFFE".U)
      dut.clock.step(1)
    }
  }
}

// Test SRL operation
class ALUSrlTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Srl_Tester" should "test SRL operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // shift by 0
      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect("h80000000".U)
      dut.clock.step(1)

      // logical: MSB filled with 0 (not sign extended)
      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect("h40000000".U)
      dut.clock.step(1)

      // shift all-ones right by 1
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect("h7FFFFFFF".U)
      dut.clock.step(1)

      // shift right by 31
      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke(31.U)
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // only lower 5 bits of operandB used
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(32.U)  // lower 5 bits = 0
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect("hFFFFFFFF".U)
      dut.clock.step(1)
    }
  }
}

// Test SRA operation
class ALUSraTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sra_Tester" should "test SRA operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // shift by 0
      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect("h80000000".U)
      dut.clock.step(1)

      // arithmetic: negative number sign-extends with 1
      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect("hC0000000".U)
      dut.clock.step(1)

      // positive number sign-extends with 0 (same as SRL)
      dut.io.operandA.poke("h7FFFFFFF".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect("h3FFFFFFF".U)
      dut.clock.step(1)

      // shift negative by 31: all bits become sign bit (1)
      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke(31.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect("hFFFFFFFF".U)
      dut.clock.step(1)

      // shift positive by 31: all bits become sign bit (0)
      dut.io.operandA.poke("h7FFFFFFF".U)
      dut.io.operandB.poke(31.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // all-ones >> 1 stays all-ones
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect("hFFFFFFFF".U)
      dut.clock.step(1)
    }
  }
}

// Test SLT operation (signed less-than)
class ALUSltTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Slt_Tester" should "test SLT operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // 5 < 10 → 1
      dut.io.operandA.poke(5.U)
      dut.io.operandB.poke(10.U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // 10 < 5 → 0
      dut.io.operandA.poke(10.U)
      dut.io.operandB.poke(5.U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // equal: 5 < 5 → 0
      dut.io.operandA.poke(5.U)
      dut.io.operandB.poke(5.U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // signed: -1 (0xFFFFFFFF) < 0 → 1
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // signed: 0 < -1 (0xFFFFFFFF) → 0
      dut.io.operandA.poke(0.U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // most negative < most positive → 1
      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke("h7FFFFFFF".U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // most positive < most negative → 0
      dut.io.operandA.poke("h7FFFFFFF".U)
      dut.io.operandB.poke("h80000000".U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)
    }
  }
}

// Test SLTU operation (unsigned less-than)
class ALUSltuTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sltu_Tester" should "test SLTU operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // 0 < 1 → 1
      dut.io.operandA.poke(0.U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // 1 < 0 → 0
      dut.io.operandA.poke(1.U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // equal → 0
      dut.io.operandA.poke(42.U)
      dut.io.operandB.poke(42.U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // unsigned: 0xFFFFFFFF is MAX, not less than 1
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // unsigned: 1 < 0xFFFFFFFF → 1
      dut.io.operandA.poke(1.U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // 0 < 0xFFFFFFFF → 1
      dut.io.operandA.poke(0.U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)
    }
  }
}

// Test PASSB operation
class ALUPassbTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Passb_Tester" should "test PASSB operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // passes operandB regardless of operandA
      dut.io.operandA.poke(0.U)
      dut.io.operandB.poke(42.U)
      dut.io.operation.poke(ALUOp.PASSB)
      dut.io.aluResult.expect(42.U)
      dut.clock.step(1)

      dut.io.operandA.poke("hDEADBEEF".U)
      dut.io.operandB.poke("h12345678".U)
      dut.io.operation.poke(ALUOp.PASSB)
      dut.io.aluResult.expect("h12345678".U)
      dut.clock.step(1)

      // zero passthrough
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.PASSB)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // max passthrough
      dut.io.operandA.poke(0.U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.PASSB)
      dut.io.aluResult.expect("hFFFFFFFF".U)
      dut.clock.step(1)
    }
  }
}
