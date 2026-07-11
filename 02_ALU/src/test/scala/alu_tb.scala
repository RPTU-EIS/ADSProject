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

      //ToDo: add more test cases for ADD operation
      
      //Zero
      dut.io.operandA.poke(0.U)
      dut.io.operandB.poke(20.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect(20.U)
      dut.clock.step(1)

      //Negative
      dut.io.operandA.poke(0xFFFFFFFBL.U)  // -5 in two's complement
      dut.io.operandB.poke(10.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect(5.U)
      dut.clock.step(1)

      //Overflow
      dut.io.operandA.poke(0xFFFFFFFFL.U)
      dut.io.operandB.poke(1.U)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)
    }
  }
}

// ---------------------------------------------------
// ToDo: Add test classes for all other ALU operations
//---------------------------------------------------

//Test SUB operation
class ALUSubTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sub_Tester" should "test SUB operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      //Positive Values
      dut.io.operandA.poke(20.U)
      dut.io.operandB.poke(10.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect(10.U)
      dut.clock.step(1)

      //Zero
      dut.io.operandA.poke(20.U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect(20.U)
      dut.clock.step(1)

      //Negative values
      dut.io.operandA.poke(0xFFFFFFFEL.U) // -2
      dut.io.operandB.poke(0xFFFFFFFCL.U) // -4
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect(2.U) // 2
      dut.clock.step(1)

      //Negative Result / Underflow
      dut.io.operandA.poke(5.U)
      dut.io.operandB.poke(10.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect(0xFFFFFFFBL.U)    // -5
      dut.clock.step(1)

    }
  }
}

//Test AND operation
class ALUAndTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_And_Tester" should "test AND operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // 1 & value
      dut.io.operandA.poke(0xFFFFFFFFL.U)    //all bits are 1
      dut.io.operandB.poke(0x12345678L.U)
      dut.io.operation.poke(ALUOp.AND)
      dut.io.aluResult.expect(0x12345678L.U)
      dut.clock.step(1)

      // alternating bits
      dut.io.operandA.poke(0xAAAAAAAAL.U)     //1010..1010
      dut.io.operandB.poke(0x55555555L.U)   //0101..0101
      dut.io.operation.poke(ALUOp.AND)
      dut.io.aluResult.expect(0.U)          //0000..0000
      dut.clock.step(1)

      //value &0
      dut.io.operandA.poke(0x12345678L.U)
      dut.io.operandB.poke(0x0.U)
      dut.io.operation.poke(ALUOp.AND)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)
    }
  }
}

//Test OR operation
class ALUOrTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Or_Tester" should "test OR operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      // 1 | value
      dut.io.operandA.poke(0xFFFFFFFFL.U)    //all bits are 1
      dut.io.operandB.poke(0x12345678L.U)
      dut.io.operation.poke(ALUOp.OR)
      dut.io.aluResult.expect(0xFFFFFFFFL.U)
      dut.clock.step(1)

      // alternating bits
      dut.io.operandA.poke(0xAAAAAAAAL.U)     //1010..1010
      dut.io.operandB.poke(0x55555555L.U)   //0101..0101
      dut.io.operation.poke(ALUOp.OR)
      dut.io.aluResult.expect(0xFFFFFFFFL.U)          //1111..1111
      dut.clock.step(1)

      //value | 0
      dut.io.operandA.poke(0x12345678L.U)
      dut.io.operandB.poke(0x0.U)
      dut.io.operation.poke(ALUOp.OR)
      dut.io.aluResult.expect(0x12345678L.U)
      dut.clock.step(1)
    }
  }
}

//Test XOR operation
class ALUXorTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Xor_Tester" should "test XOR operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      //  same value XOR (cancels to 0)
      dut.io.operandA.poke(0x12345678L.U)
      dut.io.operandB.poke(0x12345678L.U)
      dut.io.operation.poke(ALUOp.XOR)
      dut.io.aluResult.expect(0x0.U)
      dut.clock.step(1)

      // alternating bits
      dut.io.operandA.poke(0xAAAAAAAAL.U)     //1010..1010
      dut.io.operandB.poke(0x55555555L.U)   //0101..0101
      dut.io.operation.poke(ALUOp.XOR)
      dut.io.aluResult.expect(0xFFFFFFFFL.U)          //1111..1111
      dut.clock.step(1)

      //value XOR 0
      dut.io.operandA.poke(0x12345678L.U)
      dut.io.operandB.poke(0x0.U)
      dut.io.operation.poke(ALUOp.XOR)
      dut.io.aluResult.expect(0x12345678L.U)
      dut.clock.step(1)

      //value XOR 1 (flips all the bits)
      dut.io.operandA.poke(0x12345678L.U)
      dut.io.operandB.poke(0xFFFFFFFFL.U)
      dut.io.operation.poke(ALUOp.XOR)
      dut.io.aluResult.expect(0xEDCBA987L.U)  // Bitwise NOT of 0x12345678
      dut.clock.step(1)

    }
  }
}

//Test SLL operation
class ALUSllTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sll_Tester" should "test SLL operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      //  Basic Shift
      dut.io.operandA.poke(0x00000001.U)
      dut.io.operandB.poke(3.U)
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect(0x00000008.U)
      dut.clock.step(1)

      // No Shift
      dut.io.operandA.poke(0x12345678.U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect(0x12345678.U)
      dut.clock.step(1)

      // Lower 5 bits only for operandB - shift by 32 becomes 0
      dut.io.operandA.poke(0x00000001.U)
      dut.io.operandB.poke(32.U)             //100000
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect(0x00000001.U)
      dut.clock.step(1)

      // LSB shifted to MSB
      dut.io.operandA.poke(0x00000001.U)
      dut.io.operandB.poke(31.U)
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect(0x80000000L.U)
      dut.clock.step(1)

    }
  }
}

//Test SRL operation
class ALUSrlTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Srl_Tester" should "test SRL operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      //  Basic Shift
      dut.io.operandA.poke(0x00000008.U)
      dut.io.operandB.poke(3.U)
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect(0x00000001.U)
      dut.clock.step(1)

      // No Shift
      dut.io.operandA.poke(0x12345678.U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect(0x12345678.U)
      dut.clock.step(1)

      // Lower 5 bits only for operandB - shift by 32 becomes 0
      dut.io.operandA.poke(0x00000001.U)
      dut.io.operandB.poke(32.U)             //100000
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect(0x00000001.U)
      dut.clock.step(1)

      // MSB shifted to LSB
      dut.io.operandA.poke(0x80000000L.U)
      dut.io.operandB.poke(31.U)
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect(0x00000001L.U)
      dut.clock.step(1)

    }
  }
}

//Test SRA operation
class ALUSraTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sra_Tester" should "test SRA operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      //  Basic Shift
      dut.io.operandA.poke(0x00000008.U)
      dut.io.operandB.poke(3.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect(0x00000001.U)
      dut.clock.step(1)

      // No Shift
      dut.io.operandA.poke(0x12345678.U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect(0x12345678.U)
      dut.clock.step(1)

      // Lower 5 bits only for operandB - shift by 32 becomes 0
      dut.io.operandA.poke(0x00000001.U)
      dut.io.operandB.poke(32.U)             //100000
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect(0x00000001.U)
      dut.clock.step(1)

      // Sign Extension of MSB
      dut.io.operandA.poke(0x80000000L.U)      //MSB=1
      dut.io.operandB.poke(4.U)
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect(0xF8000000L.U)
      dut.clock.step(1)

    }
  }
}

//Test SLT operation
class ALUSltTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Slt_Tester" should "test SLT operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      //  Positive < Positive (True)
      dut.io.operandA.poke(5.U)
      dut.io.operandB.poke(10.U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      // Positive < Positive (False)
      dut.io.operandA.poke(10.U)
      dut.io.operandB.poke(5.U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // Negative<Positive
      dut.io.operandA.poke(0xFFFFFFF0L.U)
      dut.io.operandB.poke(5.U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      //Negative
      dut.io.operandA.poke(0xFFFFFFFEL.U) // -2
      dut.io.operandB.poke(0xFFFFFFFAL.U) // -6
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      // Equal values
      dut.io.operandA.poke(42.U)
      dut.io.operandB.poke(42.U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(0.U)

    }
  }
}

class ALUSltuTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sltu_Tester" should "test SLTU operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      //Equal values
      dut.io.operandA.poke(42.U)
      dut.io.operandB.poke(42.U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      //Positive < Positive (True)
      dut.io.operandA.poke(50.U)
      dut.io.operandB.poke(55.U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)

      //Positive < Positive (False)
      dut.io.operandA.poke(100.U)
      dut.io.operandB.poke(2.U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      //Unsigned comparison instead of negative number
      dut.io.operandA.poke(0x80000000L.U)  // 2,147,483,648 (unsigned)
      dut.io.operandB.poke(5.U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(0.U)

    }
  }
}

class ALUPassbTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Passb_Tester" should "test PASSB operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      dut.io.operandA.poke(23.U)
      dut.io.operandB.poke(20.U)
      dut.io.operation.poke(ALUOp.PASSB)
      dut.io.aluResult.expect(20.U)
      dut.clock.step(1)

      dut.io.operandA.poke(20.U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.PASSB)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

    }
  }
}
