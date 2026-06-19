// ADS I Class Project
// Pipelined RISC-V Core with Forwarding Unit
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// Test cases for hazard detection and forwarding

package PipelinedRV32I_Tester

import chisel3._
import chiseltest._
import PipelinedRV32I._
import org.scalatest.flatspec.AnyFlatSpec

// -----------------------------------------
// Test cases for the Forwarding Unit
// -----------------------------------------

//Start code

//We test the Forwarding Unit by running a program full of data hazards
//Each instruction triggers a specific hazard scenario
//If the Forwarding Unit works, all expected values will match

class PipelinedRISCV32ITest extends AnyFlatSpec with ChiselScalatestTester {

  "RV32I_ForwardingTester" should "work" in {
    test(new PipelinedRV32I("src/test/programs/BinaryFile_pipelined")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      dut.clock.setTimeout(0)

      //We wait 5 cycles for the pipeline to fill up
      dut.clock.step(5)


      //=================================================
      //SETUP — Load base values, no hazards yet
      //=================================================

      dut.io.result.expect(0.U)        // ADDI x0, x0, 0
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      dut.io.result.expect(5.U)        // ADDI x1, x0, 5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      dut.io.result.expect(10.U)       // ADDI x2, x0, 10
      dut.io.exception.expect(false.B)
      dut.clock.step(1)


      //=================================================
      //CASE 1 — Mixed forwarding
      //x1 was written 2 cycles ago (in WB barrier)
      //x2 was written 1 cycle ago (in MEM barrier)
      //Forward x1 from WB and x2 from MEM
      //=================================================

      dut.io.result.expect(15.U)       // ADD x3, x1, x2 → 5 + 10 = 15
      dut.io.exception.expect(false.B)
      dut.clock.step(1)


      //=================================================
      //CASE 2 — Forward rs1 from MEM
      //x3 was just calculated, now in MEM barrier
      //Forward x3 to operand A
      //=================================================

      dut.io.result.expect(10.U)       // SUB x5, x3, x1 → 15 - 5 = 10
      dut.io.exception.expect(false.B)
      dut.clock.step(1)


      //=================================================
      //CASE 3 — Forward rs2 from WB
      //x3 is now in WB barrier (2 cycles old)
      //Forward x3 to operand B
      //=================================================

      dut.io.result.expect(25.U)       // ADD x7, x2, x3 → 10 + 15 = 25
      dut.io.exception.expect(false.B)
      dut.clock.step(1)


      //=================================================
      //CASE 4 — Forward rs1 from MEM
      //x7 was just calculated, now in MEM barrier
      //x8 is still 0 (never written before)
      //=================================================

      dut.io.result.expect(25.U)       // ADD x8, x7, x8 → 25 + 0 = 25
      dut.io.exception.expect(false.B)
      dut.clock.step(1)


      //=================================================
      //CASE 5 — Double forward from MEM
      //x8 was just calculated, used for BOTH operand A and operand B
      //Forward x8 from MEM to both inputs of the ALU
      //=================================================

      dut.io.result.expect(50.U)       // ADD x9, x8, x8 → 25 + 25 = 50
      dut.io.exception.expect(false.B)
      dut.clock.step(1)


      //=================================================
      //CASE 6 — Hazard check with stable register (no forwarding needed)
      //x1 was written long ago, already in the Register File
      //I-type uses immediate so only rs1 could hazard
      //In this case there is no hazard, just a clean read
      //=================================================

      dut.io.result.expect(5.U)        // ADDI x10, x1, 0 → 5 + 0 = 5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

    }
  }
}