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

      dut.io.result.expect(1.U)
      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--ADDI x3, x0, 1--")
      println(f"ADDI x3, x0, 1: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.result.expect(8.U)
      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--ADDI x4, x0, 8--")
      println(f"ADDI x4, x0, 8: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--ADDI x3, x3, 1--")
      println(f"ADDI x3, x3, 1: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--BNE  x4, x3, -4--")
      println(f"BNE  x4, x3, -4: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--SHOULD BE FLUSHED CYCLE--")
      println(f"SHOULD BE FLUSHED CYCLE: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--SHOULD BE FLUSHED CYCLE--")
      println(f"SHOULD BE FLUSHED CYCLE: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--ADDI x3, x3, 1--")
      println(f"ADDI x3, x3, 1: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--BNE  x4, x3, -4--")
      println(f"BNE  x4, x3, -4: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--SHOULD BE FLUSHED CYCLE--")
      println(f"SHOULD BE FLUSHED CYCLE: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--SHOULD BE FLUSHED CYCLE--")
      println(f"SHOULD BE FLUSHED CYCLE: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--ADDI x3, x3, 1--")
      println(f"ADDI x3, x3, 1: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--BNE  x4, x3, -4--")
      println(f"BNE  x4, x3, -4: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--SHOULD BE ADDI x3, x3, 1--")
      println(f"SHOULD BE ADDI x3, x3, 1: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--SHOULD BE BNE  x4, x3, -4--")
      println(f"SHOULD BE BNE  x4, x3, -4: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--SHOULD BE ADDI x3, x3, 1--")
      println(f"SHOULD BE ADDI x3, x3, 1: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--SHOULD BE BNE  x4, x3, -4--")
      println(f"SHOULD BE BNE  x4, x3, -4: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--SHOULD BE ADDI x3, x3, 1--")
      println(f"SHOULD BE ADDI x3, x3, 1: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--SHOULD BE BNE  x4, x3, -4--")
      println(f"SHOULD BE BNE  x4, x3, -4: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--ADDI x3, x3, 1--")
      println(f"ADDI x3, x3, 1: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--SHOULD BE BNE  x4, x3, -4--")
      println(f"SHOULD BE BNE  x4, x3, -4: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--ADDI x3, x3, 1--")
      println(f"ADDI x3, x3, 1: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--SHOULD BE BNE  x4, x3, -4--")
      println(f"SHOULD BE BNE  x4, x3, -4: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--SHOULD BE FLUSHED CYCLE--")
      println(f"SHOULD BE FLUSHED CYCLE: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--SHOULD BE FLUSHED CYCLE--")
      println(f"SHOULD BE FLUSHED CYCLE: 0x${dut.io.result.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)
    }
  }
}

/*
      dut.io.exception.expect(false.B)
      println("-- DEBUG --")
      println(f"DEBUG: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)
 */