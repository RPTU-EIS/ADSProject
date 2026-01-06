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

      dut.io.opA.poke(10.U) // 10
      dut.io.opB.poke(10.U) // 10
      dut.io.op.poke(ALUOp.ADD)
      dut.io.result.expect(20.U) // 20
      dut.clock.step(1)

      //max min
      dut.io.opA.poke("h7FFFFFFF".U) // max positive
      dut.io.opB.poke("h00000000".U) // 0
      dut.io.op.poke(ALUOp.ADD)
      dut.io.result.expect("h7FFFFFFF".U) // max positive
      dut.clock.step(1)

      //negative values
      dut.io.opA.poke("hFFFFFFFE".U) // -2
      dut.io.opB.poke("h00000001".U) // 1
      dut.io.op.poke(ALUOp.ADD)
      dut.io.result.expect("hFFFFFFFF".U) // -1
      dut.clock.step(1)

      //overflow
      dut.io.opA.poke("hFFFFFFFF".U) // -1
      dut.io.opB.poke("h00000001".U) // 1
      dut.io.op.poke(ALUOp.ADD)
      dut.io.result.expect("h00000000".U) // 0
      dut.clock.step(1)
    }
  }
}

class ALUSubTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sub_Tester" should "test SUB operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.io.opA.poke(10.U) // 10
      dut.io.opB.poke(5.U) // 5
      dut.io.op.poke(ALUOp.SUB)
      dut.io.result.expect(5.U) // 5
      dut.clock.step(1)

      //max min
      dut.io.opA.poke("h7FFFFFFF".U) // max positive
      dut.io.opB.poke("h00000000".U) // 0
      dut.io.op.poke(ALUOp.SUB)
      dut.io.result.expect("h7FFFFFFF".U) // max positive
      dut.clock.step(1)

      //negative values
      dut.io.opA.poke("hFFFFFFFE".U) // -2
      dut.io.opB.poke("hFFFFFFFC".U) // -4
      dut.io.op.poke(ALUOp.SUB)
      dut.io.result.expect("h00000002".U) // 2
      dut.clock.step(1)

      //overflow
      dut.io.opA.poke("hFFFFFFFF".U) // -1
      dut.io.opB.poke("h00000001".U) // 1
      dut.io.op.poke(ALUOp.SUB)
      dut.io.result.expect("hFFFFFFFE".U) //-2
      dut.clock.step(1)
    }
  }
}

class ALUAndTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_And_Tester" should "test AND operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.io.opA.poke("b00000000000000000000000000000000".U)
      dut.io.opB.poke("b00000000000000000000000000000000".U)
      dut.io.op.poke(ALUOp.AND)
      dut.io.result.expect("b00000000000000000000000000000000".U)
      dut.clock.step(1)

      dut.io.opA.poke("b11111111111111111111111111111111".U)
      dut.io.opB.poke("b11111111111111111111111111111111".U)
      dut.io.op.poke(ALUOp.AND)
      dut.io.result.expect("b11111111111111111111111111111111".U)
      dut.clock.step(1)

      dut.io.opA.poke("b10101010101010101010101010101010".U)
      dut.io.opB.poke("b11111111111111111111111111111111".U)
      dut.io.op.poke(ALUOp.AND)
      dut.io.result.expect("b10101010101010101010101010101010".U)
      dut.clock.step(1)

      dut.io.opA.poke("b10101010101010101010101010101010".U)
      dut.io.opB.poke("b01010101010101010101010101010101".U)
      dut.io.op.poke(ALUOp.AND)
      dut.io.result.expect("b00000000000000000000000000000000".U)
      dut.clock.step(1)
    }
  }
}

class ALUOrTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Or_Tester" should "test OR operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.io.opA.poke("b00000000000000000000000000000000".U)
      dut.io.opB.poke("b00000000000000000000000000000000".U)
      dut.io.op.poke(ALUOp.OR)
      dut.io.result.expect("b00000000000000000000000000000000".U)
      dut.clock.step(1)

      dut.io.opA.poke("b11111111111111111111111111111111".U)
      dut.io.opB.poke("b11111111111111111111111111111111".U)
      dut.io.op.poke(ALUOp.OR)
      dut.io.result.expect("b11111111111111111111111111111111".U)
      dut.clock.step(1)

      dut.io.opA.poke("b10101010101010101010101010101010".U)
      dut.io.opB.poke("b11111111111111111111111111111111".U)
      dut.io.op.poke(ALUOp.OR)
      dut.io.result.expect("b11111111111111111111111111111111".U)
      dut.clock.step(1)

      dut.io.opA.poke("b10101010101010101010101010101010".U)
      dut.io.opB.poke("b01010101010101010101010101010101".U)
      dut.io.op.poke(ALUOp.OR)
      dut.io.result.expect("b11111111111111111111111111111111".U)
      dut.clock.step(1)
    }
  }
}

class ALUXorTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Xor_Tester" should "test XOR operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.io.opA.poke("b00000000000000000000000000000000".U)
      dut.io.opB.poke("b00000000000000000000000000000000".U)
      dut.io.op.poke(ALUOp.XOR)
      dut.io.result.expect("b00000000000000000000000000000000".U)
      dut.clock.step(1)

      dut.io.opA.poke("b11111111111111111111111111111111".U)
      dut.io.opB.poke("b11111111111111111111111111111111".U)
      dut.io.op.poke(ALUOp.XOR)
      dut.io.result.expect("b00000000000000000000000000000000".U)
      dut.clock.step(1)

      dut.io.opA.poke("b10101010101010101010101010101010".U)
      dut.io.opB.poke("b11111111111111111111111111111111".U)
      dut.io.op.poke(ALUOp.XOR)
      dut.io.result.expect("b01010101010101010101010101010101".U)
      dut.clock.step(1)

      dut.io.opA.poke("b10101010101010101010101010101010".U)
      dut.io.opB.poke("b01010101010101010101010101010101".U)
      dut.io.op.poke(ALUOp.XOR)
      dut.io.result.expect("b11111111111111111111111111111111".U)
      dut.clock.step(1)
    }
  }
}

class ALUSllTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sll_Tester" should "test SLL operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.io.opA.poke("b00000000000000000000000000000010".U)
      dut.io.opB.poke(0.U)
      dut.io.op.poke(ALUOp.SLL)
      dut.io.result.expect("b00000000000000000000000000000010".U)
      dut.clock.step(1)

      dut.io.opA.poke("b00000000000000000000000000000010".U)
      dut.io.opB.poke(1.U)
      dut.io.op.poke(ALUOp.SLL)
      dut.io.result.expect("b00000000000000000000000000000100".U)
      dut.clock.step(1)

      dut.io.opA.poke("b00000000000000000000000000000001".U)
      dut.io.opB.poke(31.U)
      dut.io.op.poke(ALUOp.SLL)
      dut.io.result.expect("b10000000000000000000000000000000".U)
      dut.clock.step(1)

    }
  }
}

class ALUSrlTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Srl_Tester" should "test SRL operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.io.opA.poke("b01000000000000000000000000000000".U)
      dut.io.opB.poke(0.U)
      dut.io.op.poke(ALUOp.SRL)
      dut.io.result.expect("b01000000000000000000000000000000".U)
      dut.clock.step(1)

      dut.io.opA.poke("b01000000000000000000000000000000".U)
      dut.io.opB.poke(1.U)
      dut.io.op.poke(ALUOp.SRL)
      dut.io.result.expect("b00100000000000000000000000000000".U)
      dut.clock.step(1)

      dut.io.opA.poke("b10000000000000000000000000000000".U)
      dut.io.opB.poke(31.U)
      dut.io.op.poke(ALUOp.SRL)
      dut.io.result.expect("b00000000000000000000000000000001".U)
      dut.clock.step(1)

    }
  }
}

class ALUSraTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sra_Tester" should "test SRA operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.io.opA.poke("b01000000000000000000000000000000".U)
      dut.io.opB.poke(0.U)
      dut.io.op.poke(ALUOp.SRA)
      dut.io.result.expect("b01000000000000000000000000000000".U)
      dut.clock.step(1)

      dut.io.opA.poke("b01000000000000000000000000000000".U)
      dut.io.opB.poke(1.U)
      dut.io.op.poke(ALUOp.SRA)
      dut.io.result.expect("b00100000000000000000000000000000".U)
      dut.clock.step(1)

      dut.io.opA.poke("b10000000000000000000000000000000".U)
      dut.io.opB.poke(31.U)
      dut.io.op.poke(ALUOp.SRA)
      dut.io.result.expect("b11111111111111111111111111111111".U)
      dut.clock.step(1)

    }
  }
}

class ALUSltTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Slt_Tester" should "test SLT operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.io.opA.poke(12.U)
      dut.io.opB.poke(12.U)
      dut.io.op.poke(ALUOp.SLT)
      dut.io.result.expect(0.U)
      dut.clock.step(1)

      dut.io.opA.poke(50.U)
      dut.io.opB.poke(55.U)
      dut.io.op.poke(ALUOp.SLT)
      dut.io.result.expect(1.U)
      dut.clock.step(1)

      dut.io.opA.poke(100.U)
      dut.io.opB.poke(2.U)
      dut.io.op.poke(ALUOp.SLT)
      dut.io.result.expect(0.U)
      dut.clock.step(1)

      dut.io.opA.poke("hFFFFFFFE".U) // -2
      dut.io.opB.poke(2.U)
      dut.io.op.poke(ALUOp.SLT)
      dut.io.result.expect(1.U)
      dut.clock.step(1)

      dut.io.opA.poke("hFFFFFFFE".U) // -2
      dut.io.opB.poke("hFFFFFFFA".U) // -6
      dut.io.op.poke(ALUOp.SLT)
      dut.io.result.expect(0.U)
      dut.clock.step(1)

    }
  }
}

class ALUSltuTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sltu_Tester" should "test SLTU operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.io.opA.poke(12.U)
      dut.io.opB.poke(12.U)
      dut.io.op.poke(ALUOp.SLTU)
      dut.io.result.expect(0.U)
      dut.clock.step(1)

      dut.io.opA.poke(50.U)
      dut.io.opB.poke(55.U)
      dut.io.op.poke(ALUOp.SLTU)
      dut.io.result.expect(1.U)
      dut.clock.step(1)

      dut.io.opA.poke(100.U)
      dut.io.opB.poke(2.U)
      dut.io.op.poke(ALUOp.SLTU)
      dut.io.result.expect(0.U)
      dut.clock.step(1)

    }
  }
}

class ALUPassbTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Passb_Tester" should "test PASSB operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.io.opA.poke(43.U)
      dut.io.opB.poke(12.U)
      dut.io.op.poke(ALUOp.PASSB)
      dut.io.result.expect(12.U)
      dut.clock.step(1)

      dut.io.opA.poke(50.U)
      dut.io.opB.poke(0.U)
      dut.io.op.poke(ALUOp.PASSB)
      dut.io.result.expect(0.U)
      dut.clock.step(1)

    }
  }
}