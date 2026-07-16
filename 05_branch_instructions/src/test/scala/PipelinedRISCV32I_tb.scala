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

      dut.io.result.expect(4.U)
      dut.io.exception.expect(false.B)
      println("=================================================")
      println("--FROM WB STAGE--")
      println(f"ADDI x3, x0, 4: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")

      dut.clock.step(1)

      dut.io.result.expect(8.U)
      dut.io.exception.expect(false.B)
      println("--FROM WB STAGE--")
      println(f"ADDI x4, x0, 8: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")

      dut.clock.step(1)

      //dut.io.result.expect(6.U)
      dut.io.exception.expect(false.B)
      println("-- CYCLE --")
      println(f"ADDI x3, x3, 1: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      //==========================================================

      dut.io.exception.expect(false.B)
      println("-- BNE --")
      println(f"BNE  x4, x3, -4: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(3)

      dut.io.exception.expect(false.B)
      println("--2 CYCLES AFTER--")
      println(f"SHOULD CYCLE: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("-- BNE --")
      println(f"SHOULD CYCLE: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(3)

      dut.io.exception.expect(false.B)
      println("--2 CYCLES AFTER--")
      println(f"SHOULD CYCLE: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("-- BNE -")
      println(f"SHOULD CYCLE: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(3)

      dut.io.exception.expect(false.B)
      println("-- 2 CYCLES AFTER --")
      println(f"SHOULD CYCLE: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      //BNE FINISHED CYCLE

      dut.io.exception.expect(false.B)
      println("-- BNE --")
      println(f"SHOULD EXIT CYCLE: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("-- ADDI x5, x0, 16 --")
      println(f"ADDI x5, x0, 16: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.result.expect(24.U)
      dut.io.exception.expect(false.B)
      println("-- ADD x5, x5, x4 --")
      println(f"ADD x5, x5, x4: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.result.expect(12.U)
      dut.io.exception.expect(false.B)
      println("-- SRAI x5, x5, 1 --")
      println(f"SRAI x5, x5, 1: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("-- JAL x3, 8 --")
      println(f"JAL x3, 8: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(3)

      dut.io.exception.expect(false.B)
      dut.io.result.expect(12.U)
      println("-- AFTER 2 CYCLES --")
      println(f"ADDI x5, x5, 0: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("-- ADDI x3, x3, 0 --")
      println(f"ADDI x3, x3, 0: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("-- JALR x7, 0(x3) --")
      println(f"JALR x7, 0(x3): 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(3)

      dut.io.exception.expect(false.B)
      println("-- 2 CYCLES AFTER --")
      println(f"ADDI x5, x0, 3: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
      println("=================================================")
      dut.clock.step(1)

      dut.io.exception.expect(false.B)
      println("-- ADDI x5, x5, 0 --")
      println(f"ADDI x5, x5, 0: 0x${dut.io.result.peek().litValue}%08X")
      println(f"PCSrcE: 0x${dut.io.PCSrcE_debug.peek().litValue}%08X")
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