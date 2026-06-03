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
      val A = 20
      val B = 40
      val result = A + B
      dut.clock.setTimeout(0)

      dut.io.operandA.poke(A.U)
      dut.io.operandB.poke(B.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect(result.U)
      dut.clock.step(1)

      //ToDo: add more test cases for ADD operation

    }
  }
}

class ALUSubTest extends AnyFlatSpec with ChiselScalatestTester {

  def wrap32(x: Int): BigInt = x.toLong & 0xFFFFFFFFL

  "ALU_Sub_Tester" should "test Sub operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      val A = 20
      val B = 40
      val result = A - B
      dut.clock.setTimeout(0)

      dut.io.operandA.poke(A.U)
      dut.io.operandB.poke(B.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect(wrap32(result).U)
      dut.clock.step(1)

      //ToDo: add more test cases for SUB operation

    }
  }
}

class ALUAndTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_AND_Tester" should "test AND operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      val A = 45
      val B = 37
      val result = A & B
      dut.clock.setTimeout(0)

      dut.io.operandA.poke(A.U)
      dut.io.operandB.poke(B.U)
      dut.io.operation.poke(ALUOp.AND)
      dut.io.aluResult.expect(result.U)
      dut.clock.step(1)

      //ToDo: add more test cases for ADD operation

    }
  }
}

class ALUOrTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_OR_Tester" should "test OR operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      val A = 45
      val B = 37
      val result = A | B
      dut.clock.setTimeout(0)

      dut.io.operandA.poke(A.U)
      dut.io.operandB.poke(B.U)
      dut.io.operation.poke(ALUOp.OR)
      dut.io.aluResult.expect(result.U)
      dut.clock.step(1)

      //ToDo: add more test cases for ADD operation

    }
  }
}

class ALUXorTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_XOR_Tester" should "test XOR operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      val A = 45
      val B = 37
      val result = A ^ B
      dut.clock.setTimeout(0)

      dut.io.operandA.poke(A.U)
      dut.io.operandB.poke(B.U)
      dut.io.operation.poke(ALUOp.XOR)
      dut.io.aluResult.expect(result.U)
      dut.clock.step(1)

      //ToDo: add more test cases for ADD operation

    }
  }
}

class ALUSllTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_SLL_Tester" should "test SLL operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      val A = 10
      val B = 16
      val result = A << (B & 0x0000001F)
      dut.clock.setTimeout(0)

      dut.io.operandA.poke(A.U)
      dut.io.operandB.poke(B.U)
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect(result.U)
      dut.clock.step(1)

      //ToDo: add more test cases for ADD operation

    }
  }
}

// ---------------------------------------------------
// ToDo: Add test classes for all other ALU operations
//---------------------------------------------------
