// ADS I Class Project
// Pipelined RISC-V Core with Hazard Detetcion and Resolution
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 05/21/2024 by Andro Mazmishvili (@Andrew8846)

/*
The goal of this task is to equip the pipelined 5-stage 32-bit RISC-V core from the previous task with a forwarding unit that takes care of hazard detetction and hazard resolution.
The functionality is the same as in task 4, but the core should now also be able to also process instructions with operands depending on the outcome of a previous instruction without stalling.

In addition to the pipelined design from task 4, you need to implement the following modules and functionality:

    Hazard Detection and Forwarding:
        Forwarding Unit: Determines if and from where data should be forwarded to resolve hazards. 
                         Resolves data hazards by forwarding the correct values from later pipeline stages to earlier ones.
                         - Inputs: Register identifiers from the ID, EX, MEM, and WB stages.
                         - Outputs: Forwarding select signals (forwardA and forwardB) indicating where to forward the values from.

        The forwarding logic utilizes multiplexers to select the correct operand values based on forwarding decisions.

Make sure that data hazards (dependencies between instructions in the pipeline) are detected and resolved without stalling the pipeline. For additional information, you can revise the ADS I lecture slides (6-25ff).

Note this design only represents a simplified RISC-V pipeline. The structure could be equipped with further instructions and extension to support a real RISC-V ISA.
*/

package core_tile

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile


// -----------------------------------------
// Global Definitions and Data Types
// -----------------------------------------

object uopc extends ChiselEnum {

  val isADD   = Value(0x01.U)
  val isSUB   = Value(0x02.U)
  val isXOR   = Value(0x03.U)
  val isOR    = Value(0x04.U)
  val isAND   = Value(0x05.U)
  val isSLL   = Value(0x06.U)
  val isSRL   = Value(0x07.U)
  val isSRA   = Value(0x08.U)
  val isSLT   = Value(0x09.U)
  val isSLTU  = Value(0x0A.U)

  val isADDI  = Value(0x10.U)

  val invalid = Value(0xFF.U)
}

object aluOpAMux extends  ChiselEnum {
    val opA_id, AluResult_mem, AluResult_wb = Value
}
object aluOpBMux extends  ChiselEnum {
    val opB_id, AluResult_mem, AluResult_wb = Value
}

import uopc._
import aluOpAMux._
import aluOpBMux._


// -----------------------------------------
// Register File
// -----------------------------------------

class regFileReadReq extends Bundle {
    val addr  = Input(UInt(5.W))
}

class regFileReadResp extends Bundle {
    val data  = Output(UInt(32.W))
}

class regFileWriteReq extends Bundle {
    val addr  = Input(UInt(5.W))
    val data  = Input(UInt(32.W))
    val wr_en = Input(Bool())
}

class regFile extends Module {
  val io = IO(new Bundle {
    val req_1  = new regFileReadReq
    val resp_1 = new regFileReadResp
    val req_2  = new regFileReadReq
    val resp_2 = new regFileReadResp
    val req_3  = new regFileWriteReq
})

  val regFile = Mem(32, UInt(32.W))
  regFile(0) := 0.U                           // hard-wired zero for x0

  when(io.req_3.wr_en){
    when(io.req_3.addr =/= 0.U){
      regFile(io.req_3.addr) := io.req_3.data
    }
  }

  io.resp_1.data := Mux((io.req_1.addr === 0.U), 0.U, (Mux((io.req_1.addr === io.req_3.addr), io.req_3.data, regFile(io.req_1.addr))))
  io.resp_2.data := Mux((io.req_2.addr === 0.U), 0.U, (Mux((io.req_2.addr === io.req_3.addr), io.req_3.data, regFile(io.req_2.addr))))

}

class ForwardingUnit extends Module {
  val io = IO(new Bundle {
    // What inputs and / or outputs does the forwarding unit need?

    // from decode stage
    val rs1_id          = Input(UInt(5.W))
    val rs2_id          = Input(UInt(5.W))
    val uop_id          = Input(uopc())

    // forwarded signals
    val rd_mem          = Input(UInt(5.W))
    val wrEn_mem        = Input(UInt(1.W))
    val rd_wb           = Input(UInt(5.W))
    val wrEn_wb         = Input(UInt(1.W))

    //  alu input mux controllers
    val aluOpA_ctrl = Output(aluOpAMux())
    val aluOpB_ctrl = Output(aluOpBMux())
  })


  /* TODO:
     Hazard detetction logic:
     Which pipeline stages are affected and how can a potential hazard be detetced there?
  */
  val rs1_mem_hazard = Wire(UInt(1.W))
  val rs2_mem_hazard = Wire(UInt(1.W))
  val rs1_wb_hazard = Wire(UInt(1.W))
  val rs2_wb_hazard = Wire(UInt(1.W))

  rs1_mem_hazard := ((io.uop_id =/= invalid) && (io.rs1_id === io.rd_mem) && (io.wrEn_mem === 1.U))
  rs1_wb_hazard := ((io.uop_id =/= invalid) && (io.rs1_id === io.rd_wb) && (io.wrEn_wb === 1.U))
  
  rs2_mem_hazard := ((io.uop_id =/= invalid) && (io.uop_id =/= isADDI) && (io.rs2_id === io.rd_mem) && (io.wrEn_mem === 1.U))
  rs2_wb_hazard := ((io.uop_id =/= invalid) && (io.uop_id =/= isADDI) && (io.rs2_id === io.rd_wb) && (io.wrEn_wb === 1.U))


  /* TODO:
     Forwarding Selection:
     Select the appropriate value to forward from one stage to another based on the hazard checks.
  */
  // operandA mux
  when (rs1_mem_hazard === 1.U){
    io.aluOpA_ctrl := aluOpAMux.AluResult_mem
  }
  .elsewhen(rs1_wb_hazard === 1.U){
    io.aluOpA_ctrl := aluOpAMux.AluResult_wb
  }
  .otherwise{
    io.aluOpA_ctrl := aluOpAMux.opA_id
  }

  // operandB mux
  when (rs2_mem_hazard === 1.U){
    io.aluOpB_ctrl := aluOpBMux.AluResult_mem
  }
  .elsewhen(rs2_wb_hazard === 1.U){
    io.aluOpB_ctrl := aluOpBMux.AluResult_wb
  }
  .otherwise{
    io.aluOpB_ctrl := aluOpBMux.opB_id
  }

}


// -----------------------------------------
// Fetch Stage
// -----------------------------------------

class IF (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    val instr = Output(UInt(32.W))
  })

  val IMem = Mem(4096, UInt(32.W))
  loadMemoryFromFile(IMem, BinaryFile)

  val PC = RegInit(0.U(32.W))
  
  io.instr := IMem(PC>>2.U)

  // Update PC
  // no jumps or branches, next PC always reads next address from IMEM
  PC := PC + 4.U
  
}


// -----------------------------------------
// Decode Stage
// -----------------------------------------

class ID extends Module {
  val io = IO(new Bundle {
    val regFileReq_A  = Flipped(new regFileReadReq) 
    val regFileResp_A = Flipped(new regFileReadResp) 
    val regFileReq_B  = Flipped(new regFileReadReq) 
    val regFileResp_B = Flipped(new regFileReadResp) 
    val instr         = Input(UInt(32.W))
    val uop           = Output(uopc())
    val rd            = Output(UInt(5.W))
    val rs1           = Output(UInt(5.W))
    val rs2           = Output(UInt(5.W))
    val operandA      = Output(UInt(32.W))
    val operandB      = Output(UInt(32.W))
    val wrEn          = Output(UInt(1.W))
  })

  val opcode  = io.instr(6, 0)
  io.rd      := io.instr(11, 7)
  val funct3  = io.instr(14, 12)
  val rs1     = io.instr(19, 15)

  // R-Type
  val funct7  = io.instr(31, 25)
  val rs2     = io.instr(24, 20)

  // I-Type
  val imm     = io.instr(31, 20) 

  when(opcode === "b0110011".U){
    when(funct3 === "b000".U){
      when(funct7 === "b0000000".U){
        io.uop := isADD
      }.elsewhen(funct7 === "b0100000".U){
        io.uop := isSUB
      }.otherwise{
        io.uop := invalid
      }
    }.elsewhen(funct3 === "b100".U){
      when(funct7 === "b0000000".U){
        io.uop := isXOR
      }.otherwise{
        io.uop := invalid
      }
    }.elsewhen(funct3 === "b110".U){
      when(funct7 === "b0000000".U){
        io.uop := isOR
      }.otherwise{
        io.uop := invalid
      }
    }.elsewhen(funct3 === "b111".U){
      when(funct7 === "b0000000".U){
        io.uop := isAND
      }.otherwise{
        io.uop := invalid
      }
    }.elsewhen(funct3 === "b001".U){
      when(funct7 === "b0000000".U){
        io.uop := isSLL
      }.otherwise{
        io.uop := invalid
      }
    }.elsewhen(funct3 === "b101".U){
      when(funct7 === "b0000000".U){
        io.uop := isSRL
      }.elsewhen(funct7 === "b0100000".U){
        io.uop := isSRA
      }.otherwise{
        io.uop := invalid
      }
    }.elsewhen(funct3 === "b010".U){
      when(funct7 === "b0000000".U){
        io.uop := isSLT
      }.otherwise{
        io.uop := invalid
      }
    }.elsewhen(funct3 === "b011".U){
      when(funct7 === "b0000000".U){
        io.uop := isSLTU
      }.otherwise{
        io.uop := invalid
      }
    }.otherwise{
      io.uop := invalid
    }
  }.elsewhen(opcode === "b0010011".U){
    when(funct3 === "b000".U){
      io.uop := isADDI
    }.otherwise{
      io.uop := invalid
    }
  }.otherwise{
    io.uop := invalid
  }

  // Operands
  io.regFileReq_A.addr := rs1
  io.regFileReq_B.addr := rs2

  io.operandA := io.regFileResp_A.data
  io.operandB := Mux(opcode === "b0110011".U, io.regFileResp_B.data, Mux(opcode === "b0010011".U, imm, 0.U))

  io.rs1     := rs1
  io.rs2     := rs2

  io.wrEn := (io.uop =/= invalid)
  
}

// -----------------------------------------
// Execute Stage
// -----------------------------------------

class EX extends Module {
  val io = IO(new Bundle {
    val uop       = Input(uopc())
    val operandA  = Input(UInt(32.W))
    val operandB  = Input(UInt(32.W))
    val aluResult = Output(UInt(32.W))
  })

  val operandA = io.operandA
  val operandB = io.operandB
  val uop      = io.uop

  when(uop === isADDI) { 
      io.aluResult := operandA + operandB 
    }.elsewhen(uop === isADD) {                           
      io.aluResult := operandA + operandB 
    }.elsewhen(uop === isSUB) {  
      io.aluResult := operandA - operandB 
    }.elsewhen(uop === isXOR) {  
      io.aluResult := operandA ^ operandB 
    }.elsewhen(uop === isOR) {  
      io.aluResult := operandA | operandB 
    }.elsewhen(uop === isAND) {  
      io.aluResult := operandA & operandB 
    }.elsewhen(uop === isSLL) {  
      io.aluResult := operandA << operandB(4, 0) 
    }.elsewhen(uop === isSRL) {  
      io.aluResult := operandA >> operandB(4, 0) 
    }.elsewhen(uop === isSRA) {  
      io.aluResult := operandA >> operandB(4, 0)          // automatic sign extension, if SInt datatype is used
    }.elsewhen(uop === isSLT) {  
      io.aluResult := Mux(operandA < operandB, 1.U, 0.U)  // automatic sign extension, if SInt datatype is used
    }.elsewhen(uop === isSLTU) {  
      io.aluResult := Mux(operandA < operandB, 1.U, 0.U) 
    }.otherwise{
      io.aluResult := "h_FFFF_FFFF".U // = 2^32 - 1; self-defined encoding for invalid operation, value is unlikely to be reached in a regular arithmetic operation
    } 

}

// -----------------------------------------
// Memory Stage
// -----------------------------------------

class MEM extends Module {
  val io = IO(new Bundle {

  })

  // No memory operations implemented in this basic CPU

}

// -----------------------------------------
// Writeback Stage
// -----------------------------------------

class WB extends Module {
  val io = IO(new Bundle {
    val regFileReq = Flipped(new regFileWriteReq) 
    val rd         = Input(UInt(5.W))
    val aluResult  = Input(UInt(32.W))
    val wrEn      = Input(UInt(1.W))
    val check_res  = Output(UInt(32.W))
  })

 io.regFileReq.addr  := io.rd
 io.regFileReq.data  := io.aluResult
 io.regFileReq.wr_en := io.wrEn
//  io.regFileReq.wr_en := io.aluResult =/= "h_FFFF_FFFF".U  // could depend on the current uopc, if ISA is extendet beyond R-type and I-type instructions

 io.check_res := io.aluResult

}


// -----------------------------------------
// IF-Barrier
// -----------------------------------------

class IFBarrier extends Module {
  val io = IO(new Bundle {
    val inInstr  = Input(UInt(32.W))
    val outInstr = Output(UInt(32.W))
  })

  val instrReg = RegInit(0.U(32.W))

  io.outInstr := instrReg
  instrReg    := io.inInstr

}


// -----------------------------------------
// ID-Barrier
// -----------------------------------------

class IDBarrier extends Module {
  val io = IO(new Bundle {
    val inUOP       = Input(uopc())
    val inRD        = Input(UInt(5.W))
    val inRS1       = Input(UInt(5.W))
    val inRS2       = Input(UInt(5.W))
    val inOperandA  = Input(UInt(32.W))
    val inOperandB  = Input(UInt(32.W))
    val outUOP      = Output(uopc())
    val outRD       = Output(UInt(5.W))
    val outRS1      = Output(UInt(5.W))
    val outRS2      = Output(UInt(5.W))
    val outOperandA = Output(UInt(32.W))
    val outOperandB = Output(UInt(32.W))
    val inWrEn      = Input(UInt(1.W))
    val outWrEn     = Output(UInt(1.W))
  })

  val uop      = Reg(uopc())
  val rd       = RegInit(0.U(5.W))
  val rs1      = RegInit(0.U(5.W))
  val rs2      = RegInit(0.U(5.W))
  val operandA = RegInit(0.U(32.W))
  val operandB = RegInit(0.U(32.W))
  val wrEn     = RegInit(0.U(1.W))

  io.outUOP := uop
  uop := io.inUOP
  io.outRD := rd
  rd := io.inRD
  io.outRS1 := rs1
  rs1 := io.inRS1
  io.outRS2 := rs2
  rs2 := io.inRS2
  io.outOperandA := operandA
  operandA := io.inOperandA
  io.outOperandB := operandB
  operandB := io.inOperandB
  io.outWrEn := wrEn
  wrEn := io.inWrEn

}


// -----------------------------------------
// EX-Barrier
// -----------------------------------------

class EXBarrier extends Module {
  val io = IO(new Bundle {
    val inAluResult  = Input(UInt(32.W))
    val outAluResult = Output(UInt(32.W))
    val inRD         = Input(UInt(5.W))
    val outRD        = Output(UInt(5.W))
    val inWrEn       = Input(UInt(1.W))
    val outWrEn      = Output(UInt(1.W))
  })

  val aluResult = RegInit(0.U(32.W))
  val rd       = RegInit(0.U(5.W))
  val wrEn     = RegInit(0.U(1.W))

  io.outAluResult := aluResult
  aluResult       := io.inAluResult

  io.outRD := rd
  rd := io.inRD

  io.outWrEn := wrEn
  wrEn := io.inWrEn

}


// -----------------------------------------
// MEM-Barrier
// -----------------------------------------

class MEMBarrier extends Module {
  val io = IO(new Bundle {
    val inAluResult  = Input(UInt(32.W))
    val outAluResult = Output(UInt(32.W))
    val inRD         = Input(UInt(5.W))
    val outRD        = Output(UInt(5.W))
    val inWrEn       = Input(UInt(1.W))
    val outWrEn      = Output(UInt(1.W))
  })

  val aluResult = RegInit(0.U(32.W))
  val rd        = RegInit(0.U(5.W))
  val wrEn      = RegInit(0.U(1.W))

  io.outAluResult := aluResult
  aluResult       := io.inAluResult

  io.outRD := rd
  rd := io.inRD

  io.outWrEn := wrEn
  wrEn := io.inWrEn

}


// -----------------------------------------
// WB-Barrier
// -----------------------------------------

class WBBarrier extends Module {
  val io = IO(new Bundle {
    val inCheckRes   = Input(UInt(32.W))
    val outCheckRes  = Output(UInt(32.W))
  })

  val check_res   = RegInit(0.U(32.W))

  io.outCheckRes := check_res
  check_res      := io.inCheckRes
}


// -----------------------------------------
// Main Class
// -----------------------------------------

class PipelinedRV32Icore (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    val check_res = Output(UInt(32.W))
  })


  // Pipeline Registers
  val IFBarrier  = Module(new IFBarrier)
  val IDBarrier  = Module(new IDBarrier)
  val EXBarrier  = Module(new EXBarrier)
  val MEMBarrier = Module(new MEMBarrier)
  val WBBarrier  = Module(new WBBarrier)

  // Pipeline Stages
  val IF  = Module(new IF(BinaryFile))
  val ID  = Module(new ID)
  val EX  = Module(new EX)
  val MEM = Module(new MEM)
  val WB  = Module(new WB)

  /* 
    TODO: Instantiate the forwarding unit.
  */
  val forwarding = Module(new ForwardingUnit)


  //Register File
  val regFile = Module(new regFile)

  // Connections for IOs
  IFBarrier.io.inInstr      := IF.io.instr
  
  ID.io.instr               := IFBarrier.io.outInstr
  ID.io.regFileReq_A        <> regFile.io.req_1
  ID.io.regFileReq_B        <> regFile.io.req_2
  ID.io.regFileResp_A       <> regFile.io.resp_1
  ID.io.regFileResp_B       <> regFile.io.resp_2

  IDBarrier.io.inUOP        := ID.io.uop
  IDBarrier.io.inRD         := ID.io.rd
  IDBarrier.io.inRS1        := ID.io.rs1
  IDBarrier.io.inRS2        := ID.io.rs2
  IDBarrier.io.inOperandA   := ID.io.operandA
  IDBarrier.io.inOperandB   := ID.io.operandB
  IDBarrier.io.inWrEn       := ID.io.wrEn

  /* 
    TODO: Connect the I/Os of the forwarding unit 
  */
  forwarding.io.rs1_id   := IDBarrier.io.outRS1
  forwarding.io.rs2_id   := IDBarrier.io.outRS2
  forwarding.io.uop_id   := IDBarrier.io.outUOP
  forwarding.io.rd_mem   := EXBarrier.io.outRD
  forwarding.io.wrEn_mem := EXBarrier.io.outWrEn
  forwarding.io.rd_wb    := MEMBarrier.io.outRD
  forwarding.io.wrEn_wb  := MEMBarrier.io.outWrEn

  /* 
    TODO: Implement MUXes to select which values are sent to the EX stage as operands
  */

  EX.io.uop := IDBarrier.io.outUOP

  /* 
    TODO: Connect operand inputs in EX stage to forwarding logic
  */

    EX.io.operandA := IDBarrier.io.outOperandA // default case
    EX.io.operandB := IDBarrier.io.outOperandB // default case
    switch(forwarding.io.aluOpA_ctrl){
      is(aluOpAMux.opA_id)        {EX.io.operandA := IDBarrier.io.outOperandA}
      is(aluOpAMux.AluResult_mem) {EX.io.operandA := EXBarrier.io.outAluResult}
      is(aluOpAMux.AluResult_wb)  {EX.io.operandA := MEMBarrier.io.outAluResult}
    }
    switch(forwarding.io.aluOpB_ctrl){
        is(aluOpBMux.opB_id)        {EX.io.operandB := IDBarrier.io.outOperandB}
        is(aluOpBMux.AluResult_mem) {EX.io.operandB := EXBarrier.io.outAluResult}
        is(aluOpBMux.AluResult_wb)  {EX.io.operandB := MEMBarrier.io.outAluResult}
    }

  EXBarrier.io.inRD         := IDBarrier.io.outRD
  EXBarrier.io.inAluResult  := EX.io.aluResult
  EXBarrier.io.inWrEn       := IDBarrier.io.outWrEn

  MEMBarrier.io.inRD        := EXBarrier.io.outRD
  MEMBarrier.io.inAluResult := EXBarrier.io.outAluResult
  MEMBarrier.io.inWrEn      := EXBarrier.io.outWrEn

  WB.io.rd                  := MEMBarrier.io.outRD
  WB.io.aluResult           := MEMBarrier.io.outAluResult
  WB.io.wrEn               := MEMBarrier.io.outWrEn
  WB.io.regFileReq          <> regFile.io.req_3

  WBBarrier.io.inCheckRes   := WB.io.check_res

  io.check_res              := WBBarrier.io.outCheckRes

}

