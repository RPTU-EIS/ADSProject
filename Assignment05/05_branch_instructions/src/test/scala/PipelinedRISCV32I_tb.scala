package PipelinedRV32I_Tester

import chisel3._
import chiseltest._
import PipelinedRV32I._
import org.scalatest.flatspec.AnyFlatSpec

// -----------------------------------------
// Test cases for Branch and Jump instructions (Assignment 05)
// -----------------------------------------
//
// Program layout (see BinaryFile_pipelined_dump for the full commented listing):
//   (1)  base value setup with ADDI                              idx 0-2
//   (2)  BEQ taken (equal case), verifies 2-slot flush            idx 3-6
//   (3)  BEQ not taken (unequal case)                             idx 7-9
//   (4)  BNE taken and not taken                                  idx 10-16
//   (5)  BLT signed true/false                                    idx 17-25
//   (6)  BGE signed true/false                                    idx 26-32
//   (7)  BLTU / BGEU unsigned true/false                          idx 33-45
//   (8)  backward branch loop (3 iterations)                      idx 46-53
//   (9)  JAL forward jump + link register                         idx 54-57
//   (10) JALR to register + immediate                             idx 58-62
//   (11) branch depending on a just-computed register (fwd+ctrl)  idx 63-67
//   (12) two branches back-to-back                                idx 68-73
//
// Every taken branch or jump in this core flushes the 2 instructions fetched
// right behind it (both the IF barrier and the ID barrier are cleared using
// the same ex_stage.io.branchTaken signal), so clock.step() counts below
// always skip exactly 2 flushed slots after a taken branch/jump.
//
//Start code
class PipelinedRISCV32ITest extends AnyFlatSpec with ChiselScalatestTester {
  "RV32I_BranchJumpTester" should "work" in {
    test(new PipelinedRV32I("src/test/programs/BinaryFile_pipelined")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)

      //We wait for the pipeline to fill up (IF->ID->EX->MEM->WB->WBBarrier) so the
      //first instruction's result is visible at dut.io.result
      dut.clock.step(5)
      dut.io.result.expect(4.U)  // idx 0: ADDI x1, x0, 4
      dut.clock.step(1)
      dut.io.result.expect(5.U)  // idx 1: ADDI x2, x0, 5
      dut.clock.step(1)
      dut.io.result.expect(4.U)  // idx 2: ADDI x3, x0, 4
      dut.clock.step(1)
      dut.io.result.expect(8.U)  // idx 3: BEQ x1, x3, beq_taken_target
      dut.clock.step(3)
      dut.io.result.expect(6.U)  // idx 6: ADDI x6, x0, 6
      dut.clock.step(1)
      dut.io.result.expect(9.U)  // idx 7: BEQ x1, x2, beq_nottaken_target
      dut.clock.step(1)
      dut.io.result.expect(7.U)  // idx 8: ADDI x7, x0, 7
      dut.clock.step(1)
      dut.io.result.expect(8.U)  // idx 9: ADDI x8, x0, 8
      dut.clock.step(1)
      dut.io.result.expect(9.U)  // idx 10: BNE x1, x2, bne_taken_target
      dut.clock.step(3)
      dut.io.result.expect(10.U)  // idx 13: ADDI x11, x0, 10
      dut.clock.step(1)
      dut.io.result.expect(8.U)  // idx 14: BNE x1, x3, bne_nottaken_target
      dut.clock.step(1)
      dut.io.result.expect(96.U)  // idx 15: ADDI x12, x0, 96
      dut.clock.step(1)
      dut.io.result.expect(128.U)  // idx 16: ADDI x13, x0, 128
      dut.clock.step(1)
      dut.io.result.expect("hfffffffb".U)  // idx 17: ADDI x14, x0, 4091
      dut.clock.step(1)
      dut.io.result.expect(3.U)  // idx 18: ADDI x15, x0, 3
      dut.clock.step(1)
      dut.io.result.expect("hfffffffe".U)  // idx 19: BLT x14, x15, blt_true_target
      dut.clock.step(3)
      dut.io.result.expect(20.U)  // idx 22: ADDI x18, x0, 20
      dut.clock.step(1)
      dut.io.result.expect("hfffffffe".U)  // idx 23: BLT x15, x14, blt_false_target
      dut.clock.step(1)
      dut.io.result.expect(21.U)  // idx 24: ADDI x19, x0, 21
      dut.clock.step(1)
      dut.io.result.expect(22.U)  // idx 25: ADDI x20, x0, 22
      dut.clock.step(1)
      dut.io.result.expect("hfffffffe".U)  // idx 26: BGE x15, x14, bge_true_target
      dut.clock.step(3)
      dut.io.result.expect(26.U)  // idx 29: ADDI x23, x0, 26
      dut.clock.step(1)
      dut.io.result.expect("hfffffffe".U)  // idx 30: BGE x14, x15, bge_false_target
      dut.clock.step(1)
      dut.io.result.expect(27.U)  // idx 31: ADDI x24, x0, 27
      dut.clock.step(1)
      dut.io.result.expect(28.U)  // idx 32: ADDI x25, x0, 28
      dut.clock.step(1)
      dut.io.result.expect("hffffffff".U)  // idx 33: ADDI x26, x0, 4095
      dut.clock.step(1)
      dut.io.result.expect(1.U)  // idx 34: ADDI x27, x0, 1
      dut.clock.step(1)
      dut.io.result.expect(0.U)  // idx 35: BLTU x27, x26, bltu_true_target
      dut.clock.step(3)
      dut.io.result.expect(38.U)  // idx 38: ADDI x30, x0, 38
      dut.clock.step(1)
      dut.io.result.expect(0.U)  // idx 39: BGEU x26, x27, bgeu_true_target
      dut.clock.step(3)
      dut.io.result.expect(42.U)  // idx 42: ADDI x7, x0, 42
      dut.clock.step(1)
      dut.io.result.expect(0.U)  // idx 43: BLTU x26, x27, bltu_false_target
      dut.clock.step(1)
      dut.io.result.expect(43.U)  // idx 44: ADDI x8, x0, 43
      dut.clock.step(1)
      dut.io.result.expect(44.U)  // idx 45: ADDI x9, x0, 44
      dut.clock.step(1)
      dut.io.result.expect(3.U)  // idx 46: ADDI x28, x0, 3
      dut.clock.step(1)
      dut.io.result.expect(0.U)  // idx 47: ADDI x29, x0, 0
      dut.clock.step(1)
      dut.io.result.expect(1.U)  // idx 48: ADDI x29, x29, 1
      dut.clock.step(1)
      dut.io.result.expect(2.U)  // idx 49: ADDI x28, x28, 4095
      dut.clock.step(1)
      dut.io.result.expect(2.U)  // idx 50: BNE x28, x0, loop_top
      dut.clock.step(3)
      dut.io.result.expect(2.U)  // idx 48: ADDI x29, x29, 1
      dut.clock.step(1)
      dut.io.result.expect(1.U)  // idx 49: ADDI x28, x28, 4095
      dut.clock.step(1)
      dut.io.result.expect(1.U)  // idx 50: BNE x28, x0, loop_top
      dut.clock.step(3)
      dut.io.result.expect(3.U)  // idx 48: ADDI x29, x29, 1
      dut.clock.step(1)
      dut.io.result.expect(0.U)  // idx 49: ADDI x28, x28, 4095
      dut.clock.step(1)
      dut.io.result.expect(0.U)  // idx 50: BNE x28, x0, loop_top
      dut.clock.step(1)
      dut.io.result.expect(999.U)  // idx 51: ADDI x10, x0, 999
      dut.clock.step(1)
      dut.io.result.expect(998.U)  // idx 52: ADDI x11, x0, 998
      dut.clock.step(1)
      dut.io.result.expect(55.U)  // idx 53: ADDI x12, x0, 55
      dut.clock.step(1)
      dut.io.result.expect(220.U)  // idx 54: JAL x15, jal_target
      dut.clock.step(3)
      dut.io.result.expect(63.U)  // idx 57: ADDI x18, x0, 63
      dut.clock.step(1)
      dut.io.result.expect(248.U)  // idx 58: ADDI x19, x0, 248
      dut.clock.step(1)
      dut.io.result.expect(240.U)  // idx 59: JALR x20, x19, 0
      dut.clock.step(3)
      dut.io.result.expect(73.U)  // idx 62: ADDI x23, x0, 73
      dut.clock.step(1)
      dut.io.result.expect(4.U)  // idx 63: ADDI x24, x0, 4
      dut.clock.step(1)
      dut.io.result.expect(8.U)  // idx 64: BEQ x24, x1, hazard_target
      dut.clock.step(3)
      dut.io.result.expect(80.U)  // idx 67: ADDI x27, x0, 80
      dut.clock.step(1)
      dut.io.result.expect(9.U)  // idx 68: BEQ x1, x2, b2b_target_unreached
      dut.clock.step(1)
      dut.io.result.expect(9.U)  // idx 69: BNE x1, x2, b2b_target
      dut.clock.step(3)
      dut.io.result.expect(90.U)  // idx 72: ADDI x31, x0, 90
      dut.clock.step(1)
      dut.io.result.expect(992.U)  // idx 73: ADDI x5, x0, 992
      dut.clock.step(1)
      dut.io.result.expect(0.U)  // idx 74: ADDI x0, x0, 0
      dut.clock.step(1)
      dut.io.result.expect(0.U)  // idx 75: ADDI x0, x0, 0
      dut.clock.step(1)
      dut.io.result.expect(0.U)  // idx 76: ADDI x0, x0, 0
      dut.clock.step(1)
      dut.io.result.expect(0.U)  // idx 77: ADDI x0, x0, 0
      dut.clock.step(1)
      dut.io.result.expect(0.U)  // idx 78: ADDI x0, x0, 0
      dut.clock.step(2)

      //If we reach this point, every branch, jump, forwarding and flush scenario
      //in the program executed with the correct result and the correct timing.
    }
  }
}