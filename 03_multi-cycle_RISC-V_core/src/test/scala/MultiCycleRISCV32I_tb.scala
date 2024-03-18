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
           
    }
  }
}


