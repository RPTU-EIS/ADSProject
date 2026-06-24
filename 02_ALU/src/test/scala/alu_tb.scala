// ADS I Class Project
// Pipelined RISC-V Core with Hazard Detection and Resolution
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 10/31/2025 by Tobias Jauch (tobias.jauch@rptu.de)

/*This file is used to test the ALU hardware module.
  The idea is to send values to operandA and operandB, select one ALU operation,
  and then check if aluResult gives the value that we expect.*/

import chisel3._                          // Gives the basic Chisel types like UInt
import chiseltest._                       // Gives poke, expect, clock.step, and test()
import org.scalatest.flatspec.AnyFlatSpec // Gives the test class style used by ScalaTest

import Assignment02._                     // Imports ALU and ALUOp from the main code

/*This test checks the ADD operation.First, it checks a normal addition. Then, it checks adding zero.
  Finally, it checks overflow, where 0xFFFFFFFF + 1 must wrap around to 0.*/

class ALUAddTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Add_Tester" should "test ADD operation" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)                         // Avoid timeout problems during the test

      dut.io.operandA.poke(10.U)                      // Put first number
      dut.io.operandB.poke(10.U)                      // Put second number
      dut.io.operation.poke(ALUOp.ADD)                // Select ADD operation
      dut.io.aluResult.expect(20.U)                   // Check 10 + 10 = 20
      dut.clock.step(1)                               // Move to the next test case

//ToDo: add more test cases for ADD operation
      // Corner Case: Overflow/Wraparound
      // 0xFFFFFFFF + 1 should result in 0
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke("h80000000".U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      dut.io.operandA.poke("hFFFFFFFB".U)
      dut.io.operandB.poke(5.U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.ADD)
      dut.io.aluResult.expect("hFFFFFFFE".U)
      dut.clock.step(1)
    }                              // Finish this test case
    }
  }


/*This test checks the SUB operation. First, it checks a normal subtraction.
  Then, it checks underflow, where 0 - 1 must wrap around to 0xFFFFFFFF.*/

class ALUSubTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sub_Tester" should "test SUB operation" in {
    test(new ALU) { dut =>

      dut.io.operandA.poke(20.U)                      // Put first number
      dut.io.operandB.poke(5.U)                       // Put number to subtract
      dut.io.operation.poke(ALUOp.SUB)                // Select SUB operation
      dut.io.aluResult.expect(15.U)                   // Check 20 - 5 = 15
      dut.clock.step(1)                               // Move to the next test case

      dut.io.operandA.poke(0.U)                       // Put zero
      dut.io.operandB.poke(1.U)                       // Subtract one
      dut.io.operation.poke(ALUOp.SUB)                // Select SUB operation
      dut.io.aluResult.expect("hFFFFFFFF".U)          // Check wraparound result
      dut.clock.step(1)      
      
      dut.io.operandA.poke(0.U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect(1.U)
      dut.clock.step(1)    
      
      dut.io.operandA.poke(10.U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect(11.U)
      dut.clock.step(1)

      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.SUB)
      dut.io.aluResult.expect("h7FFFFFFF".U)
      dut.clock.step(1)
                     // Finish this test case
    }
  }
}
/*This test checks the AND operation. First, it checks two values where no bits match.
  Then, it checks the identity case, where x AND x must return x.*/

class ALUAndTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_And_Tester" should "test AND operation" in {
    test(new ALU) { dut =>

      dut.io.operandA.poke("h0000F0F0".U)                // Put first bit pattern
      dut.io.operandB.poke("h00000F0F".U)                // Put second bit pattern
      dut.io.operation.poke(ALUOp.AND)               // Select AND operation
      dut.io.aluResult.expect(0.U)                   // Check that no bits match
      dut.clock.step(1)     
      
      dut.io.operandA.poke("h0000FFFF".U)
      dut.io.operandB.poke("h00001234".U)
      dut.io.operation.poke(ALUOp.AND)
      dut.io.aluResult.expect("h00001234".U)
      dut.clock.step(1)                         

      dut.io.operandA.poke("h0000ABCD".U)                // Put same value in operandA
      dut.io.operandB.poke("h0000ABCD".U)                // Put same value in operandB
      dut.io.operation.poke(ALUOp.AND)               // Select AND operation
      dut.io.aluResult.expect("h0000ABCD".U)             // Check x AND x = x
      dut.clock.step(1)    
      
      dut.io.operandA.poke("hAAAAAAAA".U) 
      dut.io.operandB.poke("h55555555".U) 
      dut.io.operation.poke(ALUOp.AND)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)                          // Finish this test case
    }
  }
}

/*This test checks the OR operation. First, it checks two values that should combine into 0xFFFF.
  Then, it checks the identity case, where x OR 0 must return x.*/

class ALUOrTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Or_Tester" should "test OR operation" in {
    test(new ALU) { dut =>

      dut.io.operandA.poke("hF0F0F0F0".U)                // Put first bit pattern
      dut.io.operandB.poke("h0F0F0F0F".U)                // Put second bit pattern
      dut.io.operation.poke(ALUOp.OR)                // Select OR operation
      dut.io.aluResult.expect("hFFFFFFFF".U)             // Check that the bits are combined
      dut.clock.step(1)                              // Move to the next test case

      dut.io.operandA.poke("h12345678".U)                // Put a normal value
      dut.io.operandB.poke(0.U)                      // Put zero
      dut.io.operation.poke(ALUOp.OR)                // Select OR operation
      dut.io.aluResult.expect("h12345678".U)             // Check x OR 0 = x
      dut.clock.step(1)    
      
      dut.io.operandA.poke("h00000000".U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.OR)
      dut.io.aluResult.expect("hFFFFFFFF".U)
      dut.clock.step(1)
    }                          // Finish this test case
    }
  }


/*This test checks the XOR operation. First, it checks the case where x XOR x must return 0.
  Then, it checks two different bit patterns that should return 0xFFFF.*/

class ALUXorTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Xor_Tester" should "test XOR operation" in {
    test(new ALU) { dut =>


      dut.io.operandA.poke("hFFFF0000".U)
      dut.io.operandB.poke("hF0F0F0F0".U)
      dut.io.operation.poke(ALUOp.XOR)
      dut.io.aluResult.expect("h0F0FF0F0".U)
      dut.clock.step(1)

      dut.io.operandA.poke("hDEADBEEF".U)
      dut.io.operandB.poke("hDEADBEEF".U)
      dut.io.operation.poke(ALUOp.XOR)
      dut.io.aluResult.expect(0.U)
      dut.clock.step(1)

      dut.io.operandA.poke("hABCDEF12".U)
      dut.io.operandB.poke(0.U)
      dut.io.operation.poke(ALUOp.XOR)
      dut.io.aluResult.expect("hABCDEF12".U)
      dut.clock.step(1)

      dut.io.operandA.poke("hAAAA".U)                
      dut.io.operandB.poke("hAAAA".U)                
      dut.io.operation.poke(ALUOp.XOR)               
      dut.io.aluResult.expect(0.U)                   
      dut.clock.step(1)                              

      dut.io.operandA.poke("hF0F0F0F0".U)          
      dut.io.operandB.poke("h0F0F0F0F".U)            
      dut.io.operation.poke(ALUOp.XOR)              
      dut.io.aluResult.expect("hFFFFFFFF".U)            
      dut.clock.step(1)                              
    }
  }
}
/*
  This test checks the SLL operation.
  SLL means shift left logical.
  First, it checks a normal left shift.
  Then, it checks that only the lower 5 bits of operandB are used as shift amount.
*/
class ALUSllTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sll_Tester" should "test SLL operation" in {
    test(new ALU) { dut =>

      dut.io.operandA.poke(1.U)                       // Put value to shift
      dut.io.operandB.poke(4.U)                       // Put shift amount
      dut.io.operation.poke(ALUOp.SLL)                // Select SLL operation
      dut.io.aluResult.expect(16.U)                   // Check 1 << 4 = 16
      dut.clock.step(1)   

      dut.io.operandA.poke("h00000001".U)                       // Put value to shift
      dut.io.operandB.poke(31.U)                       // Put shift amount
      dut.io.operation.poke(ALUOp.SLL)                // Select SLL operation
      dut.io.aluResult.expect("h80000000".U)                   // Check 1 << 4 = 16
      dut.clock.step(1)     
      
      dut.io.operandA.poke("h40000001".U)
      dut.io.operandB.poke("h00100001".U)
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect("h80000002".U)                        
      dut.clock.step(1)  

      dut.io.operandA.poke(1.U)                       // Put value to shift
      dut.io.operandB.poke(33.U)                      // Put shift amount bigger than 31
      dut.io.operation.poke(ALUOp.SLL)                // Select SLL operation
      dut.io.aluResult.expect(2.U)                    // Check 33 becomes shift by 1
      dut.clock.step(1)        
      
            // Wraparround
      dut.io.operandA.poke("hF0000001".U)
      dut.io.operandB.poke(1.U)
      dut.io.operation.poke(ALUOp.SLL)
      dut.io.aluResult.expect("hE0000002".U)
      dut.clock.step(1)                              
    }
  }
}

/* This test checks the SRL operation. SRL means shift right logical.
  It moves the bits to the right and fills the left side with zeros. */

class ALUSrlTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Srl_Tester" should "test SRL operation" in {
    test(new ALU) { dut =>

      dut.io.operandA.poke(16.U)                      // Put value to shift
      dut.io.operandB.poke(4.U)                       // Put shift amount
      dut.io.operation.poke(ALUOp.SRL)                // Select SRL operation
      dut.io.aluResult.expect(1.U)                    // Check 16 >> 4 = 1
      dut.clock.step(1)                               

      dut.io.operandA.poke("h80000000".U)             // Put value with the left bit set
      dut.io.operandB.poke("h00000001".U)             // Shift by one
      dut.io.operation.poke(ALUOp.SRL)                // Select SRL operation
      dut.io.aluResult.expect("h40000000".U)          // Check that zero is shifted in
      dut.clock.step(1)      
      
      dut.io.operandA.poke("h80000000".U)
      dut.io.operandB.poke("h00100001".U)
      dut.io.operation.poke(ALUOp.SRL)
      dut.io.aluResult.expect("h40000000".U)                         // Finish this test case
      dut.clock.step(1)                              
    }
  }
}

/* This test checks the SRA operation. SRA means shift right arithmetic.
  It moves the bits to the right but keeps the sign bit when the value is negative. */

class ALUSraTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sra_Tester" should "test SRA operation" in {
    test(new ALU) { dut =>

      dut.io.operandA.poke(16.U)                      // Put positive value
      dut.io.operandB.poke(4.U)                       // Put shift amount
      dut.io.operation.poke(ALUOp.SRA)                // Select SRA operation
      dut.io.aluResult.expect(1.U)                    // Check positive shift result
      dut.clock.step(1)                               // Move to the next test case

      dut.io.operandA.poke("h80000000".U)             // Put negative value in signed form
      dut.io.operandB.poke(1.U)                       // Shift by one
      dut.io.operation.poke(ALUOp.SRA)                // Select SRA operation
      dut.io.aluResult.expect("hC0000000".U)          // Check that sign bit is copied
      dut.clock.step(1)  
      
      dut.io.operandA.poke("h00000004".U)
      dut.io.operandB.poke(33.U) // 33 % 32 = 1
      dut.io.operation.poke(ALUOp.SRA)
      dut.io.aluResult.expect(2.U)
      dut.clock.step(1)                             // Finish this test case
    }
  }
}
/*
  This test checks the SLT operation.
  SLT means set less than using signed numbers.
  The important case here is 0xFFFFFFFF, because as signed it means -1.
*/
class ALUSltTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Slt_Tester" should "test SLT operation" in {
    test(new ALU) { dut =>

      dut.io.operandA.poke(5.U)                       // Put smaller number
      dut.io.operandB.poke(10.U)                      // Put bigger number
      dut.io.operation.poke(ALUOp.SLT)                // Select SLT operation
      dut.io.aluResult.expect(1.U)                    // Check 5 < 10 is true
      dut.clock.step(1)                               // Move to the next test case

      dut.io.operandA.poke("hFFFFFFFF".U)             // Put -1 if interpreted as signed
      dut.io.operandB.poke(1.U)                       // Put positive one
      dut.io.operation.poke(ALUOp.SLT)                // Select SLT operation
      dut.io.aluResult.expect(1.U)                    // Check -1 < 1 is true
      dut.clock.step(1)                               // Move to the next test case

      dut.io.operandA.poke(1.U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(0.U) // False
      dut.clock.step(1)  

      dut.io.operandA.poke(5.U)                       // Put same value
      dut.io.operandB.poke(5.U)                       // Put same value
      dut.io.operation.poke(ALUOp.SLT)                // Select SLT operation
      dut.io.aluResult.expect(0.U)                    // Check 5 < 5 is false
      dut.clock.step(1)   
      
      dut.io.operandA.poke("hFFFFFFFE".U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.SLT)
      dut.io.aluResult.expect(1.U) // True
      dut.clock.step(1)   
      
                       // Finish this test case
    }
  }
}

/*
  This test checks the SLTU operation. SLTU means set less than using unsigned numbers.
  The important case here is also 0xFFFFFFFF, but unsigned it is a very large number. */

class ALUSltuTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Sltu_Tester" should "test SLTU operation" in {
    test(new ALU) { dut =>

      dut.io.operandA.poke(5.U)                       // Put smaller number
      dut.io.operandB.poke(10.U)                      // Put bigger number
      dut.io.operation.poke(ALUOp.SLTU)               // Select SLTU operation
      dut.io.aluResult.expect(1.U)                    // Check 5 < 10 is true
      dut.clock.step(1)                               // Move to the next test case

      dut.io.operandA.poke("hFFFFFFFF".U)             // Put biggest unsigned value
      dut.io.operandB.poke(1.U)                       // Put small unsigned value
      dut.io.operation.poke(ALUOp.SLTU)               // Select SLTU operation
      dut.io.aluResult.expect(0.U)                    // Check 0xFFFFFFFF < 1 is false
      dut.clock.step(1)   
      
      dut.io.operandA.poke(1.U)
      dut.io.operandB.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.SLTU)
      dut.io.aluResult.expect(1.U) // True
      dut.clock.step(1)                             
      dut.io.operandA.poke(5.U)                       // Put same value
      dut.io.operandB.poke(5.U)                       // Put same value
      dut.io.operation.poke(ALUOp.SLTU)               // Select SLTU operation
      dut.io.aluResult.expect(0.U)                    // Check 5 < 5 is false
      dut.clock.step(1)                               // Finish this test case
    }
  }
}

/* This test checks the PASSB operation. PASSB does not calculate anything with operandA.
  It only sends operandB directly to the result. */
  
class ALUPassbTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU_Passb_Tester" should "test PASSB operation" in {
    test(new ALU) { dut =>

      dut.io.operandA.poke("hDEADBEEF".U)             // Put any value, it should be ignored
      dut.io.operandB.poke("h12345678".U)             // Put the value that should pass
      dut.io.operation.poke(ALUOp.PASSB)              // Select PASSB operation
      dut.io.aluResult.expect("h12345678".U)          // Check result is operandB
      dut.clock.step(1)   
      
      dut.io.operandA.poke("h12345678".U)             // Put any value, it should be ignored
      dut.io.operandB.poke("hDEADBEEF".U)             // Put the value that should pass
      dut.io.operation.poke(ALUOp.PASSB)              // Select PASSB operation
      dut.io.aluResult.expect("hDEADBEEF".U)          // Check result is operandB
      dut.clock.step(1)                              // Move to the next test case

      dut.io.operandA.poke("hFFFFFFFF".U)             // Put any value, it should be ignored
      dut.io.operandB.poke(0.U)                       // Put zero
      dut.io.operation.poke(ALUOp.PASSB)              // Select PASSB operation
      dut.io.aluResult.expect(0.U)                    // Check result is still operandB
      dut.clock.step(1)   
      
      dut.io.operandB.poke("h55555555".U)      
      dut.io.operandA.poke(0.U)
      dut.io.operation.poke(ALUOp.PASSB)
      dut.io.aluResult.expect("h55555555".U)
      dut.clock.step(1)
      
      dut.io.operandA.poke("hFFFFFFFF".U)
      dut.io.operation.poke(ALUOp.PASSB)
      dut.io.aluResult.expect("h55555555".U)
      dut.clock.step(1)                            // Finish this test case
    }
  }
}
