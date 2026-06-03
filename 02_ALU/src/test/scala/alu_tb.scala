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
      dut.io.aluResult.expect(result.U)
      dut.clock.step(1)

      //ToDo: add more test cases for SUB operation

    }
  }
}

// ---------------------------------------------------
// ToDo: Add test classes for all other ALU operations
//---------------------------------------------------
