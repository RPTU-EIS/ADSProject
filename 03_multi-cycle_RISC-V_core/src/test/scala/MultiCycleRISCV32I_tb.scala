// ADS I Class Project
// Multi-Cycle RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 12/19/2023 by Tobias Jauch (@tojauch)

package MultiCycleRV32I_Tester

import chisel3._
import chiseltest._
import MultiCycleRV32I._
import org.scalatest.flatspec.AnyFlatSpec

class MultiCycleRISCV32ITest extends AnyFlatSpec with ChiselScalatestTester {

"MultiCycleRV32I_Tester" should "work" in {
    test(new MultiCycleRV32I("src/test/programs/BinaryFile")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

        /* 
         * TODO: Insert testcases from assignment 2 and adapt them for the multi-cycle core
         */
           
      dut.clock.setTimeout(0)

      dut.clock.step(4)
      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.clock.step(5)
      dut.io.result.expect(4.U)     // ADDI x1, x0, 4
      dut.clock.step(5)
      dut.io.result.expect(5.U)     // ADDI x2, x0, 5
      dut.clock.step(5)
      dut.io.result.expect(9.U)     // ADD x3, x1, x2

      def ToUInt(x: Int) = scala.math.pow(2,32).toLong + x
        
      dut.clock.step(5)
      dut.io.result.expect(ToUInt(-20).U)      // ADDI x4, x0, -20 // (x4 = -20)
      dut.clock.step(5)
      dut.io.result.expect(10.U)      // ADDI x5, x0, 10 // (x5 = 10)
      dut.clock.step(5)
      dut.io.result.expect(200.U)      // ADDI x6, x0, 200 // (x6 = 200)
      dut.clock.step(5)
      dut.io.result.expect(ToUInt(-200).U)      // ADDI x7, x0, -200 // (x7 = -200)
      
      //ADD operation
      dut.clock.step(5)
      dut.io.result.expect(14.U)          // ADD x10, x1, x5 // (x10 = 14)
      dut.clock.step(5)
      dut.io.result.expect(ToUInt(-16).U)          // ADD x10, x1, x4 // (x10 = -16)
      
      // SUB operation
      dut.clock.step(5)
      dut.io.result.expect(1.U) // SUB x11, x2, x1 // (x11 = 1)
      dut.clock.step(5)
      dut.io.result.expect(ToUInt(-1).U) // SUB x11, x1, x2 // (x11 = -1)
      dut.clock.step(5)
      dut.io.result.expect(24.U) // SUB x11, x1, x4 // (x11 = 24)
      
      // SLL operation
      dut.clock.step(5)
      dut.io.result.expect(80.U) // SLL x12, x2, x1 // (x12 = 80)
      
      // SRL operation
      dut.clock.step(5)
      dut.io.result.expect(12.U) // SRL x13, x6, x1 // (x13 = 12)
      
      // SRA operation
      dut.clock.step(5)
      dut.io.result.expect(ToUInt(-13).U) // SRA x14, x7, x1 // (x14 = -13)
      dut.clock.step(5)
      dut.io.result.expect(12.U) // SRA x14, x6, x1 // (x14 = 12)
      
      // SLT operation
      dut.clock.step(5)
      dut.io.result.expect(1.U) // SLT x15, x1, x2 // (x15 = 1)
      dut.clock.step(5)
      dut.io.result.expect(0.U) // SLT x15, x2, x1 // (x15 = 0)
      dut.clock.step(5)
      dut.io.result.expect(1.U) // SLT x15, x7, x4 // (x15 = 1)
      dut.clock.step(5)
      dut.io.result.expect(0.U) // SLT x15, x4, x7 // (x15 = 0)
      dut.clock.step(5)
      dut.io.result.expect(1.U) // SLT x15, x7, x6 // (x15 = 1)
      dut.clock.step(5)
      dut.io.result.expect(0.U) // SLT x15, x6, x7 // (x15 = 0)
      
      // SLTU operation
      dut.clock.step(5)
      dut.io.result.expect(1.U) // SLTU x16, x1, x2 // (x16 = 1)
      dut.clock.step(5)
      dut.io.result.expect(0.U) // SLTU x16, x2, x1 // (x16 = 0)
      dut.clock.step(5)
      dut.io.result.expect(0.U) // SLTU x16, x4, x5 // (x16 = 0)
      dut.clock.step(5)
      dut.io.result.expect(1.U) // SLTU x16, x5, x4 // (x16 = 1)
      
      // XOR operation
      dut.clock.step(5)
      dut.io.result.expect(1.U) // XOR x17, x1, x2 // (x17 = 1)
      dut.clock.step(5)
      dut.io.result.expect("hFFFFFFE6".U) // XOR x17, x4, x5 // (x17 = 0xffffffe6)
      
      // OR operation
      dut.clock.step(5)
      dut.io.result.expect(5.U) // OR x18, x1, x2 // (x18 = 5)
      dut.clock.step(5)
      dut.io.result.expect("hFFFFFFEE".U) // OR x18, x4, x5 // (x18 = oxffffffee)
      
      // AND operation
      dut.clock.step(5)
      dut.io.result.expect(4.U) // AND x19, x1, x2 // (X19 = 4)
      dut.clock.step(5)
      dut.io.result.expect(8.U) // AND x19, x4, x5 // (x19 = 8)
      
      // not supported instructions
      dut.clock.step(5)
      dut.io.result.expect(0.U) // sw a1,-24(s0)
      dut.clock.step(5)
      dut.io.result.expect(0.U) // lw a4,-20(s0)
      dut.clock.step(5)
      dut.io.result.expect(0.U) // jr ra
      dut.clock.step(5)
      dut.io.result.expect(0.U) // mv a0,a5
      
      // x0 hardwire to zero
      dut.clock.step(5)
      dut.io.result.expect(15.U) // ADDI x0, x5, 5 // (x0 = 15)
      dut.clock.step(5)
      dut.io.result.expect(0.U) // ADDI x0, x0, 0 // (x0 = 0)
      
      dut.clock.step(5)
      dut.io.result.expect("hFFFFFFFF".U) // ADDI x20, x0, -1 (x20 = -1)
           
    }
  }
}


