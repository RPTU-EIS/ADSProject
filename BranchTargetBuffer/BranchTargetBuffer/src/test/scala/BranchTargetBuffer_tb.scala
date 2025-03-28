package BranchTargetBuffer
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
class BTBTest extends AnyFlatSpec with ChiselScalatestTester {
  // Test input signals
  val pc1 = 0x1000.U(32.W) // Example PC address 1 00010000000'000'00
  val pc12 = 0x1100.U(32.W) // same index but should be mapped to second row
  val pc13 = 0x1200.U(32.W) // third pc with same index
  val pc2 = 0x1004.U(32.W) // Example PC address 2 00010000000'001'00
  val pc3 = 0x1008.U(32.W) // Example PC address 1 00010000000'010'00
  val pc4 = 0x100C.U(32.W) // Example PC address 2 00010000000'011'00
  val pc5 = 0x1010.U(32.W) // Example PC address 1 00010000000'100'00
  val pc6 = 0x1014.U(32.W) // Example PC address 2 00010000000'101'00
  val target1 = 0x2000.U(32.W) // Target for PC 1
  val target2 = 0x2004.U(32.W) // Target for PC 2
  val target3 = 0x2008.U(32.W) // Target address for PC 3
  val mispredicted = true.B // Example misprediction signal
  "BTB_Tester" should "work" in {
    test(new BranchTargetBuffer).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)
      // **TEST 1: Insert an entry and check prediction**
      dut.io.update.poke(true.B)
      dut.io.updatePC.poke(pc1)
      dut.io.updateTarget.poke(target1)
      dut.clock.step(1)
      // Query the same PC to check if it predicts correctly
      dut.io.update.poke(false.B)
      dut.io.pc.poke(pc1)
      dut.clock.step(2)
      dut.io.valid.expect(true.B)
      dut.io.target.expect(target1)
      // **TEST 2: Add another entry and check it does not overwrite**
      dut.io.update.poke(true.B)
      dut.io.updatePC.poke(pc2)
      dut.io.updateTarget.poke(target2)
      dut.clock.step(1)
      dut.io.update.poke(false.B)
      dut.io.pc.poke(pc1)
      dut.clock.step(2)
      dut.io.valid.expect(true.B) // Should still be a valid hit
      dut.io.target.expect(target1)
      dut.io.pc.poke(pc2)
      dut.clock.step(2)
      dut.io.valid.expect(true.B)
      dut.io.target.expect(target2)
      // **TEST 3: LRU policy
      // **Step 1: Insert pc1 into BTB**
      dut.io.update.poke(true.B)
      dut.io.updatePC.poke(pc1)
      dut.io.updateTarget.poke(target1)
      dut.clock.step(1)
      // **Step 2: Insert pc12 (same index, different tag)**
      dut.io.update.poke(true.B)
      dut.io.updatePC.poke(pc12)
      dut.io.updateTarget.poke(target2)
      dut.clock.step(1)
      // **Step 3: Access pc1 (Make pc1 the most recently used)**
      dut.io.update.poke(false.B)
      dut.io.pc.poke(pc1)
      dut.clock.step(2)
      dut.io.valid.expect(true.B)
      dut.io.target.expect(target1) // pc1 should still be valid
      // **Step 4: Insert pc3 (same index, different tag)**
      dut.io.update.poke(true.B)
      dut.io.updatePC.poke(pc13)
      dut.io.updateTarget.poke(target3)
      dut.clock.step(1)
      // **Step 5: Check if LRU correctly evicted pc12 (which was least recently used)**
      dut.io.update.poke(false.B)
      dut.io.pc.poke(pc1)
      dut.clock.step(2)
      dut.io.valid.expect(true.B) // pc1 should still exist
      dut.io.target.expect(target1)
      dut.io.pc.poke(pc13)
      dut.clock.step(2)
      dut.io.valid.expect(true.B) // pc3 should now be present
      dut.io.target.expect(target3)
      dut.io.pc.poke(pc12)
      dut.clock.step(2)
      dut.io.valid.expect(false.B) // pc12 should be evicted
      // TEST4 FSM update logic
      // **Step 1: Insert pc1 into BTB, initializes to weakTaken**
      dut.io.update.poke(true.B)
      dut.io.updatePC.poke(pc1)
      dut.io.updateTarget.poke(target1)
      dut.clock.step(1)
      // Query pc1: It should predict 'taken' (since weakTaken has MSB = 1)
      dut.io.update.poke(false.B)
      dut.io.pc.poke(pc1)
      dut.clock.step(2)
      dut.io.valid.expect(true.B)
      dut.io.predictedTaken.expect(true.B) // Expect weakTaken (01) → taken
      // **Step 2: Confirm FSM moves to strongTaken after correct prediction**
      dut.io.mispredicted.poke(false.B) // No misprediction
      dut.clock.step(1) // Allow FSM to update
      dut.io.predictedTaken.expect(true.B) // strongTaken (11) → taken
      // **Step 3: Simulate a misprediction**
      dut.io.mispredicted.poke(mispredicted) // Force misprediction
      dut.clock.step(1)
      dut.io.predictedTaken.expect(true.B) // Should weaken (11 → 10 → weakTaken)
      // **Step 4: Continue mispredicting to move predictor to strongNotTaken**
      dut.io.mispredicted.poke(mispredicted) // Another misprediction
      dut.clock.step(1)
      dut.io.predictedTaken.expect(false.B) // weakTaken (10) → Strong Not Taken (00)
      // Step5: check from strongNotTaken -> weakNotTaken
      dut.io.mispredicted.poke(true.B) // Another misprediction
      dut.clock.step(1)
      dut.io.predictedTaken.expect(false.B) // StrongNotTaken (00) → WeakNotTaken (01)
      // ------------------------------------------------------------------------
    }
  }
}
/*
package BranchTargetBuffer
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
class BTBTest extends AnyFlatSpec with ChiselScalatestTester {
  "BTB" should "correctly predict branches and update LRU policy" in {
    test(new BranchTargetBuffer) {c =>
	  
      // Initialize inputs
      c.io.pc.poke(0x00000020.U)
      c.io.update.poke(false.B)
      c.io.updatePC.poke(0.U)
      c.io.updateTarget.poke(0.U)
      c.io.mispredicted.poke(false.B)

      // Initially, the BTB should not have a valid prediction
      c.clock.step()
      c.clock.step()
      c.clock.step()
      c.clock.step()
      c.clock.step()
      c.io.valid.expect(false.B)
      c.io.target.expect(0.U)
      c.io.predictedTaken.expect(false.B)

      // Update BTB with a branch entry
	  c.clock.step()
      c.clock.step()
      c.io.update.poke(true.B)
      c.io.updatePC.poke(0x00000020.U)
      c.io.updateTarget.poke(0x00000080.U)
	  c.io.mispredicted.poke(false.B)
      c.clock.step()
      c.io.updatePC.poke(0x00000000.U)
      c.io.update.poke(false.B)
      c.clock.step()
      c.clock.step()

      // Query the BTB for the same PC
      c.io.pc.poke(0x00000020.U)
      c.io.valid.expect(true.B)
      c.io.target.expect(0x00000080.U)
      c.io.predictedTaken.expect(true.B) // Initial predictor state is weak taken (2)
      c.io.update.poke(false.B)
      c.clock.step()
      c.io.pc.poke(0x00000024.U)
      c.clock.step()
      c.clock.step()

      // Simulate a misprediction and update the predictor FSM
      c.clock.step()
      c.clock.step()
      c.io.pc.poke(0x00000020.U)
      c.clock.step()
      c.io.pc.poke(0x00000024.U)
      c.clock.step()
      c.io.mispredicted.poke(true.B)
      c.clock.step()
      c.clock.step()
      c.clock.step()


      // Check predictor update
      c.io.pc.poke(0x00000020.U)
      c.io.valid.expect(true.B)
      c.clock.step()
      c.io.pc.poke(0x00000024.U)
      c.clock.step()
      c.io.predictedTaken.expect(false.B) // Predictor should shift to a strong not Taken state
      c.clock.step()
      c.clock.step()
      c.clock.step()

	  // Simulate a misprediction and update the predictor FSM
      c.io.pc.poke(0x00000020.U)
      c.clock.step()
      c.io.pc.poke(0x00000024.U)
      c.clock.step()
      c.io.mispredicted.poke(true.B)
      c.clock.step()
      c.clock.step()
      c.clock.step()
      c.io.pc.poke(0x00000020.U)
      c.clock.step()
      c.io.pc.poke(0x00000024.U)
      c.clock.step()
	  c.io.mispredicted.poke(true.B)
      c.clock.step()
      c.clock.step()
      c.clock.step()

      // Check predictor update
      c.io.pc.poke(0x00000020.U)
      c.io.valid.expect(true.B)
      c.io.predictedTaken.expect(true.B) // Predictor should shift to a weak taken state after two new mispredictions
      c.clock.step()
      c.io.pc.poke(0x00000000.U)
	  

	  // Add a new entry to the same index 
	  c.clock.step()
      c.io.update.poke(true.B)
      c.io.updatePC.poke(0x00001020.U)
      c.io.updateTarget.poke(0x00000040.U)
	  c.io.mispredicted.poke(false.B)
      c.clock.step()
      c.io.update.poke(false.B)
      c.clock.step()
      c.clock.step()
	  
	  // Query the BTB for the same PC
      c.io.pc.poke(0x00001020.U)
      c.io.valid.expect(true.B)
      c.io.target.expect(0x00000040.U)
      c.io.predictedTaken.expect(true.B) // Initial predictor state is weak taken (2)
      c.clock.step()
      c.io.pc.poke(0x00000000.U)
      c.clock.step()
      c.clock.step()

	  // Query the BTB for the previous pc
      c.io.pc.poke(0x00000020.U)
      c.io.valid.expect(true.B)
      c.io.target.expect(0x00000080.U)
      c.io.predictedTaken.expect(true.B) // Initial predictor state is weak taken (2)
      c.clock.step()
      c.io.pc.poke(0x00000000.U)
      c.clock.step()

	  // Add a new entry to the same index 

      c.io.update.poke(true.B)
      c.io.updatePC.poke(0x00000120.U)
      c.io.updateTarget.poke(0x00000140.U)
	  c.io.mispredicted.poke(false.B)
      c.clock.step()
      c.io.update.poke(false.B)
      c.clock.step()
      c.clock.step()

	  // Query the BTB for the first pc, it should be false because has been repalced (the las recently)
      c.io.pc.poke(0x00000020.U)
      c.io.valid.expect(false.B)
      c.clock.step()
      c.io.pc.poke(0x00000000.U)
      c.clock.step()

      //Ad again first PC to BTB
      c.io.update.poke(true.B)
      c.io.updatePC.poke(0x00000020.U)
      c.io.updateTarget.poke(0x00000080.U)
      c.io.mispredicted.poke(false.B)
      c.clock.step()
      c.io.updatePC.poke(0x00000000.U)
      c.io.update.poke(false.B)
      c.clock.step()
      c.clock.step()

      // Query the BTB for the first pc, it should be true because now is the last recent
      c.io.pc.poke(0x00000020.U)
      c.io.valid.expect(true.B)
      c.io.target.expect(0x00000080.U)
      c.io.predictedTaken.expect(true.B) // Initial predictor state is weak taken (2)
      c.clock.step()

      c.clock.step()
      c.clock.step()
      c.clock.step()


      //We check the 4 program counter again
      c.io.pc.poke(0x00000120.U)
      c.io.valid.expect(true.B)
      c.io.target.expect(0x00000140.U)
      c.io.predictedTaken.expect(true.B) // Initial predictor state is weak taken (2)
      c.clock.step()
      c.io.pc.poke(0x00000000.U)
      c.clock.step()

    }
  }
}
*/