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
