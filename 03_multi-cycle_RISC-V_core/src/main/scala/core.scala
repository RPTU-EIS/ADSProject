// ADS I Class Project
// Multi-Cycle RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 05/10/2023 by Tobias Jauch (@tojauch)

/*
The goal of this task is to implement a 5-stage multi-cycle 32-bit RISC-V processor (without pipelining) supporting parts of the RV32I instruction set architecture. The RV32I core is relatively basic 
and does not include features like memory operations, exception handling, or branch instructions. It is designed for a simplified subset of the RISC-V ISA. It mainly 
focuses on ALU operations and basic instruction execution. 

    Instruction Memory:
        The CPU has an instruction memory (IMem) with 4096 words, each of 32 bits.
        The content of IMem is loaded from a binary file specified during the instantiation of the MultiCycleRV32Icore module.

    CPU Registers:
        The CPU has a program counter (PC) and a register file (regFile) with 32 registers, each holding a 32-bit value.
        Register x0 is hard-wired to zero.

    Microarchitectural Registers / Wires:
        Various signals are defined as either registers or wires depending on whether they need to be used in the same cycle or in a later cycle.

    Processor Stages:
        The FSM of the processor has five stages: fetch, decode, execute, memory, and writeback.
        The current stage is stored in a register named stage.

        Fetch Stage:
            The instruction is fetched from the instruction memory based on the current value of the program counter (PC).

        Decode Stage:
            Instruction fields such as opcode, rd, funct3, and rs1 are extracted.
            For R-type instructions, additional fields like funct7 and rs2 are extracted.
            Control signals (isADD, isSUB, etc.) are set based on the opcode and funct3 values.
            Operands (operandA and operandB) are determined based on the instruction type.

        Execute Stage:
            Arithmetic and logic operations are performed based on the control signals and operands.
            The result is stored in the aluResult register.

        Memory Stage:
            No memory operations are implemented in this basic CPU.

        Writeback Stage:
            The result of the operation (writeBackData) is written back to the destination register (rd) in the register file.
            The program counter (PC) is updated for the next instruction.

        Other:
            If the processor state is not in any of the defined stages, an assertion is triggered to indicate an error.

    Check Result:
        The final result (writeBackData) is output to the io.check_res signal.
        In the fetch stage, a default value of 0 is assigned to io.check_res.
*/

package core_tile

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile
import chisel3.experimental.ChiselEnum
object aluOps extends ChiselEnum {
    val ADD, SUB, SLL, SLT, SLTU, XOR, SRL, SRA, OR, AND = Value
}
/* 
    aluOps(0=ADD)
    aluOps(1=SUB)                                                                                                                               
    aluOps(2=SLL)                                                                                                                               
    aluOps(3=SLT)                                                                                                                               
    aluOps(4=SLTU)                                                                                                                              
    aluOps(5=XOR)                                                                                                                               
    aluOps(6=SRL)                                                                                                                               
    aluOps(7=SRA)                                                                                                                               
    aluOps(8=OR)                                                                                                                                
    aluOps(9=AND)   
 */

class MultiCycleRV32Icore (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    val check_res = Output(UInt(32.W))
  })

  val fetch :: decode :: execute :: memory :: writeback :: Nil = Enum(5) // Enum datatype to define the stages of the processor FSM
  val stage = RegInit(fetch) 

  // state machine
  switch(stage){
    is(fetch)       {stage := decode}
    is(decode)      {stage := execute}
    is(execute)     {stage := memory}
    is(memory)      {stage := writeback}
    is(writeback)   {stage := fetch}
  }

  // -----------------------------------------
  // Instruction Memory
  // -----------------------------------------

  /*
   * TODO: Implement the memory as described above
   */
  val IMem = Mem(4096, UInt(32.W)) // This is the same for the data memory as well. But this design does not have memory operations
  loadMemoryFromFile(IMem, BinaryFile)

  // -----------------------------------------
  // CPU Registers
  // -----------------------------------------

  /*
   * TODO: Implement the program counter as a register, initialize with zero
   */
  val PC = RegInit(0.U(32.W))

  /*
   * TODO: Implement the Register File as described above
   */
  val regFile = Mem(32, UInt(32.W))

  // -----------------------------------------
  // Microarchitectural Registers / Wires
  // -----------------------------------------

  // if signal is processed in the same cycle --> wire
  // is signal is used in a later cycle       --> register

  /*
   * TODO: Implement the registers and wires you need in the individual stages of the processor 
   */
  
  // instruction register
  val instructionReg = RegInit(0.U(32.W))
  val opcode = instructionReg(6, 0)
  val funct7 = instructionReg(31, 25)
  val funct3 = instructionReg(14, 12)
  val rs1    = instructionReg(19, 15)
  val rs2    = instructionReg(24, 20)
  val rd     = instructionReg(11,  7)
  val immI   = instructionReg(31, 20)

  val RTypeOp = (opcode === "b0110011".U)

  import  aluOps._
  val aluControl = WireInit(aluOps.ADD)
  val unknownIns = WireInit(0.U(1.W)) // check_res = 0
  when(opcode === "b0110011".U && funct3 === "b000".U && funct7 === "b0000000".U){ aluControl := aluOps.ADD }
  .elsewhen(opcode === "b0110011".U && funct3 === "b000".U && funct7 === "b0100000".U){ aluControl := aluOps.SUB }
  .elsewhen(opcode === "b0110011".U && funct3 === "b001".U && funct7 === "b0000000".U){ aluControl := aluOps.SLL }
  .elsewhen(opcode === "b0110011".U && funct3 === "b010".U && funct7 === "b0000000".U){ aluControl := aluOps.SLT }
  .elsewhen(opcode === "b0110011".U && funct3 === "b011".U && funct7 === "b0000000".U){ aluControl := aluOps.SLTU }
  .elsewhen(opcode === "b0110011".U && funct3 === "b100".U && funct7 === "b0000000".U){ aluControl := aluOps.XOR }
  .elsewhen(opcode === "b0110011".U && funct3 === "b101".U && funct7 === "b0000000".U){ aluControl := aluOps.SRL }
  .elsewhen(opcode === "b0110011".U && funct3 === "b101".U && funct7 === "b0100000".U){ aluControl := aluOps.SRA }
  .elsewhen(opcode === "b0110011".U && funct3 === "b110".U && funct7 === "b0000000".U){ aluControl := aluOps.OR  }
  .elsewhen(opcode === "b0110011".U && funct3 === "b111".U && funct7 === "b0000000".U){ aluControl := aluOps.AND }
  .elsewhen(opcode === "b0010011".U && funct3 === "b000".U){ aluControl := aluOps.ADD } // ADDI
  .otherwise{ aluControl := aluOps.ADD; unknownIns := 1.U } // unknown instructions: ADD 0 + 0 = 0

  // register file
  val rs1_data = RegInit(0.U(32.W))
  val rs2_data = RegInit(0.U(32.W))

  // immediate extension
  val immIExtended = RegInit(0.U(32.W))
  
  // alu
  val aluOpA = Mux((unknownIns === 1.U), 0.U, rs1_data)
  val aluOpB = Mux((unknownIns === 1.U), 0.U, Mux(RTypeOp, rs2_data, immIExtended)) // either rs2_data or sign extended immediate
  val aluResult = Wire(UInt(32.W))
  val aluTargetBuf = RegInit(0.U(32.W))

  // IOs need default case
  io.check_res := "h_0000_0000".U


  // -----------------------------------------
  // Processor Stages
  // -----------------------------------------

  when (stage === fetch)
  {

  /*
   * TODO: Implement fetch stage
   */

    // update PC and instruction
    PC := PC + 4.U
    instructionReg := IMem(PC>>2.U)

  } 
    .elsewhen (stage === decode)
  {

  /*
   * TODO: Implement decode stage
   */
    // update regFile and immediate-extend output registers
    immIExtended := Cat(Fill(20, immI(11)), immI)
    rs1_data := Mux((rs1 =/= 0.U), regFile(rs1), 0.U)
    rs2_data := Mux((rs2 =/= 0.U), regFile(rs2), 0.U)

  } 
    .elsewhen (stage === execute)
  {

  /*
   * TODO: Implement execute stage
   */
    aluTargetBuf := aluResult    

  }
    .elsewhen (stage === memory)
  {

    // No memory operations implemented in this basic CPU

    // TODO: There might still something be missing here

  } 
    .elsewhen (stage === writeback)
  {

  /*
   * TODO: Implement Writeback stag
   */
    regFile.write(Mux((unknownIns === 1.U), 0.U, rd), aluTargetBuf)

  /*
   * TODO: Write result to output
   */
    io.check_res := aluTargetBuf
    

  }
    .otherwise 
  {

     // default case (needed for RTL-generation but should never be reached   

     assert(true.B, "Pipeline FSM must never be left")

  }

  // instantiate ALU
  val ALUInst = Module(new ALU)
  ALUInst.io.control := aluControl
  ALUInst.io.operandA := aluOpA
  ALUInst.io.operandB := aluOpB
  aluResult := ALUInst.io.result


}

class ALU extends  Module {
    val io = IO(new Bundle {
        val control = Input(aluOps())
        val operandA = Input(UInt(32.W))
        val operandB = Input(UInt(32.W))
        val result = Output(UInt(32.W))
        val zero = Output(UInt(1.W))
    })

    io.result := 0.U // default

    switch (io.control){ // ADD, SUB, SLL, SLT, SLTU, XOR, SRL, SRA, OR, AND
        is(aluOps.ADD){
            io.result := io.operandA + io.operandB
        }
        is(aluOps.SUB){
            io.result := io.operandA - io.operandB
        }
        is(aluOps.SLL){
            io.result := io.operandA << io.operandB(4,0).asUInt
        }
        is(aluOps.SLT){
            io.result := (io.operandA.asSInt < io.operandB.asSInt).asUInt
        }
        is(aluOps.SLTU){
            io.result := io.operandA.asUInt < io.operandB.asUInt
        }
        is(aluOps.XOR){
            io.result := io.operandA ^ io.operandB
        }
        is(aluOps.SRL){
            io.result := io.operandA >> io.operandB(4,0).asUInt
        }
        is(aluOps.SRA){
            io.result := (io.operandA.asSInt >> io.operandB(4,0).asUInt).asUInt
        }
        is(aluOps.OR){
            io.result := io.operandA | io.operandB
        }
        is(aluOps.AND){
            io.result := io.operandA & io.operandB
        }
    }
    io.zero := (io.result === 0.U)
}

