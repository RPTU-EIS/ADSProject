import chisel3._
import chiseltest._
import org.scalatest.flatspeBTM_dut.AnyFlatSpec

class BTBTest extends AnyFlatSpec with ChiselScalatestTester {
  "BTB" should "correctly predict branches and update LRU policy" in {
    test(new BTB) { BTM_dut =>
      // Initialize inputs
      BTM_dut.io.pBTM_dut.poke(0x00000010.U) // PC = 16
      BTM_dut.io.update.poke(false.B)
      BTM_dut.io.mispredicted.poke(false.B)
      BTM_dut.clock.step(1)
      
      // Expect no hit initially
      BTM_dut.io.valid.expect(false.B)
      
      // Insert a branch entry
      BTM_dut.io.update.poke(true.B)
      BTM_dut.io.updatePBTM_dut.poke(0x00000010.U)
      BTM_dut.io.updateTarget.poke(0x00000040.U)
      BTM_dut.clock.step(1)
      BTM_dut.io.update.poke(false.B)
      
      // Check if the branch was inserted correctly
      BTM_dut.io.pBTM_dut.poke(0x00000010.U)
      BTM_dut.clock.step(1)
      BTM_dut.io.valid.expect(true.B)
      BTM_dut.io.target.expect(0x00000040.U)
	  BTM_dut.io.predictedTaken(true.B)
      
      // Test misprediction correction
      BTM_dut.io.mispredicted.poke(true.B)
      //BTM_dut.io.update.poke(true.B)
      BTM_dut.clock.step(1)
      //BTM_dut.io.update.poke(false.B)
      
      // Ensure predictor updates
      BTM_dut.io.pBTM_dut.poke(0x00000010.U)
      BTM_dut.clock.step(1)
      BTM_dut.io.valid.expect(true.B)
	  BTM_dut.io.predictedTaken(true.B)
	  
	   // Test misprediction correction 2 time
      BTM_dut.io.mispredicted.poke(true.B)
      //BTM_dut.io.update.poke(true.B)
      BTM_dut.clock.step(1)
      //BTM_dut.io.update.poke(false.B)
      
	  // Ensure predictor updates
      BTM_dut.io.pBTM_dut.poke(0x00000010.U)
      BTM_dut.clock.step(1)
      BTM_dut.io.valid.expect(true.B)
	  BTM_dut.io.predictedTaken(false.B)
	  
      // Test LRU replacement by inserting another entry in the same set
      BTM_dut.io.update.poke(true.B)
      BTM_dut.io.updatePBTM_dut.poke(0x00000110.U) // Different PC in the same set **Not sure ** 
      BTM_dut.io.updateTarget.poke(0x00000050.U)
      BTM_dut.clock.step(1)
      BTM_dut.io.update.poke(false.B)
      
      // Check that LRU replacement works
      BTM_dut.io.pBTM_dut.poke(0x00000010.U)
      BTM_dut.clock.step(1)
      BTM_dut.io.valid.expect(false.B) // Old entry should be evicted
      
      BTM_dut.io.pBTM_dut.poke(0x00000110.U)
      BTM_dut.clock.step(1)
      BTM_dut.io.valid.expect(true.B)
      BTM_dut.io.target.expect(0x00000050.U)
    }
  }
}
