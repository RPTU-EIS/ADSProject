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
      val A1 = 20
      val B1 = 40
      val result1 = A1 + B1
      val log = new StringBuilder

      dut.clock.setTimeout(0)

      dut.io.operandA.poke(A1.U)
      dut.io.operandB.poke(B1.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect(result1.U)
      dut.clock.step(1)

      log.append(s"\nADDITION - REGULAR:\n")
      log.append(s"A = $A1\n")
      log.append(s"B = $B1\n")
      log.append(s"ALU result = ${dut.io.aluResult.peek().litValue}\n")

      //ToDo: add more test cases for ADD operation
      //TESTING WRAPAROUND

      val A2 = BigInt("FFFFFFFF",16)
      val B2 = 2
      val result2 = 1

      dut.io.operandA.poke(A2.U)
      dut.io.operandB.poke(B2.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect(result2.U)
      dut.clock.step(1)

      log.append(s"\nADDITION - WRAPAROUND:\n")
      log.append(s"A = $A2\n")
      log.append(s"B = $B2\n")
      log.append(s"ALU result = ${dut.io.aluResult.peek().litValue}\n")
      print(log.toString())

    }
  }
}

class ALUSubTest extends AnyFlatSpec with ChiselScalatestTester {

  def wrap32(x: Int): BigInt = x.toLong & 0xFFFFFFFFL

  "ALU_Sub_Tester" should "test Sub operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      val A = 20
      val B = 40
      val result = wrap32(A - B)
      val log = new StringBuilder

      dut.clock.setTimeout(0)

      dut.io.operandA.poke(A.U)
      dut.io.operandB.poke(B.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect(result.U)
      dut.clock.step(1)

      log.append(s"\nSUBTRACTION - REGULAR:\n")
      log.append(s"A = $A\n")
      log.append(s"B = $B\n")
      log.append(s"ALU result = ${dut.io.aluResult.peek().litValue}\n")
      log.append(s"Negative Number = ${dut.io.negativeNum.peek().litValue}\n")

      //ToDo: add more test cases for SUB operation

      val A1 = 128
      val B1 = 63
      val result1 = wrap32(A1 - B1)

      dut.clock.setTimeout(0)

      dut.io.operandA.poke(A1.U)
      dut.io.operandB.poke(B1.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect(result1.U)
      dut.clock.step(1)

      log.append(s"\nSUBTRACTION - POSITIVE RESULT:\n")
      log.append(s"A = $A1\n")
      log.append(s"B = $B1\n")
      log.append(s"ALU result = ${dut.io.aluResult.peek().litValue}\n")
      log.append(s"Negative Number = ${dut.io.negativeNum.peek().litValue}\n")

      print(log.toString())
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

class ALUSrlTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_SRL_Tester" should "test SRL operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      val A = 16
      val B = 2
      val result = A >> (B & 0x0000001F)
      dut.clock.setTimeout(0)

      dut.io.operandA.poke(A.U)
      dut.io.operandB.poke(B.U)
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect(result.U)
      dut.clock.step(1)

      //ToDo: add more test cases for ADD operation

    }
  }
}

class ALUSraTest extends AnyFlatSpec with ChiselScalatestTester {

  def toUInt32(x: Int): BigInt = x.toLong & 0xFFFFFFFFL

  "ALU_SRA_Tester" should "test SRA operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      val A = -16
      val B = 2
      val result = A >> (B & 0x0000001F)
      dut.clock.setTimeout(0)

      dut.io.operandA.poke(toUInt32(A).U)
      dut.io.operandB.poke(toUInt32(B).U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect(toUInt32(result).U)
      dut.clock.step(1)

      //ToDo: add more test cases for ADD operation

    }
  }
}

class ALUSltTest extends AnyFlatSpec with ChiselScalatestTester {

  def toUInt32(x: Int): BigInt = x.toLong & 0xFFFFFFFFL

  "ALU_SLT_Tester" should "test SLT operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      val A = -16
      val B = 2
      val result = if (A < B) 1 else 0
      dut.clock.setTimeout(0)

      dut.io.operandA.poke(toUInt32(A).U)
      dut.io.operandB.poke(toUInt32(B).U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(result.U)
      dut.clock.step(1)

      //ToDo: add more test cases for ADD operation

    }
  }
}

class ALUSltuTest extends AnyFlatSpec with ChiselScalatestTester {

  def toUInt32(x: Int): BigInt = x.toLong & 0xFFFFFFFFL

  "ALU_SLTU_Tester" should "test SLTU operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      val A = 16
      val B = -2
      val result = if (toUInt32(A) < toUInt32(B)) 1 else 0
      dut.clock.setTimeout(0)

      dut.io.operandA.poke(toUInt32(A).U)
      dut.io.operandB.poke(toUInt32(B).U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(result.U)
      dut.clock.step(1)

      //ToDo: add more test cases for ADD operation

    }
  }
}

class ALUPassBTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_PASSB_Tester" should "test PASSB operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      val A = 16
      val B = 2
      val result = B
      dut.clock.setTimeout(0)

      dut.io.operandA.poke(A.U)
      dut.io.operandB.poke(B.U)
      dut.io.operation.poke(ALUOp.PASSB)
      dut.io.aluResult.expect(result.U)
      dut.clock.step(1)

      //ToDo: add more test cases for ADD operation

    }
  }
}

// ---------------------------------------------------
// ToDo: Add test classes for all other ALU operations
//---------------------------------------------------