// ADS I Class Project
// Pipelined RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/15/2023 by Tobias Jauch (@tojauch)

package PipelinedRV32I_Tester

import chisel3._
import chiseltest._
import PipelinedRV32I._
import org.scalatest.flatspec.AnyFlatSpec

class PipelinedRISCV32ITest extends AnyFlatSpec with ChiselScalatestTester {

"RV32I_BasicTester" should "work" in {
    test(new PipelinedRV32I("src/test/programs/BinaryFile_pipelined")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      dut.clock.setTimeout(0)
      dut.clock.step(5)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(4.U)     // ADDI x1, x0, 4
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(5.U)     // ADDI x2, x0, 5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(9.U)     // ADD x3, x1, x2
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(2047.U)  // ADDI x4, x0, 2047
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(16.U)    // ADDI x5, x0, 16
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(2031.U)  // SUB x6, x4, x5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(2022.U)  // XOR x7, x6, x3
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(2047.U)  // OR x8, x6, x5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // AND x9, x6, x5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(64704.U) // SLL x10, x7, x2
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(63.U)    // SRL x11, x7, x2
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(63.U)    // SRA x12, x7, x2
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // SLT x13, x4, x4
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // SLT x13, x4, x5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(1.U)     // SLT x13, x5, x4
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // SLTU x13, x4, x4
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(0.U)     // SLTU x13, x4, x5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)
      dut.io.result.expect(1.U)     // SLTU x13, x5, x4
      dut.io.exception.expect(false.B)

      //OUR TEST CASES
      dut.clock.step(1)
      dut.io.result.expect(0xFFFFFFFDL.U)     // ADDI x14, x0, -3
      dut.io.exception.expect(false.B)

      //HAZARD RAW: x14 DOES NOT HOLD YET THE VALUE, IT IS STILL ZERO
      //x15 = 0 + 4 EXPECTED
      dut.clock.step(1)
      dut.io.result.expect(4.U)               // ADDI x15, x14, 4
      dut.io.exception.expect(false.B)
      println(f"RAW HAZARD [ADDI x15, x14, 4]: 0x${dut.io.result.peek().litValue}%08X")
      dut.clock.step(1)

      dut.io.result.expect(144.U)               //x3 = 9, SLLI x3, x3, 4
      dut.io.exception.expect(false.B)
      println(f"x3 = 9, SLLI x3, x3, 4: 0x${dut.io.result.peek().litValue}%08X")
      dut.clock.step(5) //ADDED NOPs TO LET x3 GET TO THE REGISTERS, WHICH IS USED IN THE NEXT OPERATION

      dut.io.result.expect(18.U)               //x3 = 288, SRAI x3, x3, 3
      dut.io.exception.expect(false.B)
      println(f"x3 = 288, SRAI x3, x3, 3: 0x${dut.io.result.peek().litValue}%08X")
      dut.clock.step(5) //ADDED NOPs TO LET x3 GET TO THE REGISTERS, WHICH IS USED IN THE NEXT OPERATION

      dut.io.result.expect(0xFFFFFF88L.U)               //x3 = -120, ADDI x3, x3, -138
      dut.io.exception.expect(false.B)
      println(f"x3 = -120, ADDI x3, x3, -138: 0x${dut.io.result.peek().litValue}%08X")
      dut.clock.step(5) //ADDED NOPs TO LET x3 GET TO THE REGISTERS, WHICH IS USED IN THE NEXT OPERATION

      dut.io.result.expect(0xFFFFFFF1L.U)               //x3 = -15, SRAI x3, x3, 3
      dut.io.exception.expect(false.B)
      println(f"x3 = -15, SRAI x3, x3, 3: 0x${dut.io.result.peek().litValue}%08X")
      dut.clock.step(5)

      dut.io.result.expect(0x0FFFFFFFL.U)               //x3 = 0x0FFFFFFF, SRLI, x3, x3, 4
      dut.io.exception.expect(false.B)
      println(f"x3 = 0x0FFFFFFF, SRLI x3, x3, 4: 0x${dut.io.result.peek().litValue}%08X")
      dut.clock.step(1)
    }
  }
}