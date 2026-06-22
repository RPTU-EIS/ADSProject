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


            // =========================================
      // Initialization
      // =========================================

      dut.io.result.expect(4.U)    // ADDI x1, x0, 4
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      dut.io.result.expect(5.U)    // ADDI x2, x0, 5
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      // =========================================
      // EX->EX Forwarding Chain
      // =========================================

      dut.io.result.expect(9.U)    // ADD x3, x1, x2   
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      dut.io.result.expect(5.U)    // SUB x4, x3, x1
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      dut.io.result.expect(0.U)    // XOR x5, x4, x2
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      dut.io.result.expect(4.U)    // OR x6, x5, x1
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      dut.io.result.expect(4.U)    // AND x7, x6, x2
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      // =========================================
      // Shift Operations
      // =========================================

      dut.io.result.expect(64.U)   // SLL x8, x7, x1
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      dut.io.result.expect(4.U)    // SRL x9, x8, x1
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      dut.io.result.expect(0.U)    // SRA x10, x9, x1
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      // =========================================
      // Both operands forwarded
      // =========================================

      dut.io.result.expect(9.U)    // ADD x11, x1, x2
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      dut.io.result.expect(18.U)   // ADD x12, x11, x11
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      // =========================================
      // rs2 forwarding
      // =========================================

      dut.io.result.expect(10.U)   // ADDI x13, x0, 10
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      dut.io.result.expect(14.U)   // ADD x14, x1, x13
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      // =========================================
      // rs1 forwarding
      // =========================================

      dut.io.result.expect(20.U)   // ADDI x15, x0, 20
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      dut.io.result.expect(24.U)   // ADD x16, x15, x1
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      // =========================================
      // Long forwarding chain
      // =========================================

      dut.io.result.expect(1.U)    // ADDI x17, x0, 1
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      dut.io.result.expect(2.U)    // ADD x18, x17, x17
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      dut.io.result.expect(4.U)    // ADD x19, x18, x18
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      dut.io.result.expect(8.U)    // ADD x20, x19, x19
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      dut.io.result.expect(16.U)   // ADD x21, x20, x20
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      dut.io.result.expect(32.U)   // ADD x22, x21, x21
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      // =========================================
      // SLT / SLTU forwarding
      // =========================================

      dut.io.result.expect(1.U)    // SLT x23, x1, x2
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

      dut.io.result.expect(1.U)    // SLTU x24, x23, x2
      dut.io.exception.expect(false.B)
      dut.clock.step(1)

    

    }
  }
}