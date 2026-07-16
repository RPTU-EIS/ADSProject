// ADS I Class Project
// Pipelined RISC-V Core - ID Stage
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
Instruction Decode (ID) Stage: decoding and operand fetch

Extracted Fields from 32-bit Instruction (see RISC-V specification for reference):
    opcode: instruction format identifier
    funct3: selects variant within instruction format
    funct7: further specifies operation type (R-type only)
    rd: destination register address
    rs1: first source register address
    rs2: second source register address
    imm: 12-bit immediate value (I-type, sign-extended)

Register File Interfaces:
    regFileReq_A, regFileResp_A: read port for rs1 operand
    regFileReq_B, regFileResp_B: read port for rs2 operand

Internal Signals:
    Combinational decoders for instructions

Functionality:
    Decode opcode to determine instruction and identify operation (ADD, SUB, XOR, ...)
    Output: uop (operation code), rd, operandA (from rs1), operandB (rs2 or immediate)

Outputs:
    uop: micro-operation code (identifies instruction type)
    rd: destination register index
    operandA: first operand
    operandB: second operand 
    XcptInvalid: exception flag for invalid instructions
*/

package core_tile

import chisel3._
import chisel3.util._
import uopc._

// -----------------------------------------
// Decode Stage
// -----------------------------------------

class ControlUnit extends Module {
    val io = IO(new Bundle {
        val opcode = Input(UInt(7.W))
        val funct3 = Input(UInt(3.W))
        val funct7 = Input(UInt(7.W))

        val uop         = Output(uopc())
        val ALUSrcD     = Output(Bool())   // operandB: true = immediate, false = rs2
        val immSel      = Output(UInt(2.W))   // 00: full, 01: shamt, 10: jump, 11: branch
        val BranchD     = Output(Bool())
        val JumpD       = Output(Bool())
        val WriteEnableD= Output(Bool())
        val XcptInvalid = Output(Bool())
    })

    val OPC_R    = "b0110011".U
    val OPC_I    = "b0010011".U
    val OPC_B    = "b1100011".U
    val OPC_JAL  = "b1101111".U
    val OPC_JALR = "b1100111".U

    io.uop         := uopc.INVALID
    io.ALUSrcD     := false.B
    io.immSel      := 0.U
    io.BranchD     := false.B
    io.JumpD       := false.B
    io.WriteEnableD:= false.B
    io.XcptInvalid := true.B

    switch(io.opcode){
        is(OPC_R){
            io.WriteEnableD := true.B
            when(io.funct7 === "b0000000".U){
                switch(io.funct3){
                    is("b000".U){ io.uop := uopc.ADD;  io.XcptInvalid := false.B }
                    is("b001".U){ io.uop := uopc.SLL;  io.XcptInvalid := false.B }
                    is("b010".U){ io.uop := uopc.SLT;  io.XcptInvalid := false.B }
                    is("b011".U){ io.uop := uopc.SLTU; io.XcptInvalid := false.B }
                    is("b100".U){ io.uop := uopc.XOR;  io.XcptInvalid := false.B }
                    is("b101".U){ io.uop := uopc.SRL;  io.XcptInvalid := false.B }
                    is("b110".U){ io.uop := uopc.OR;   io.XcptInvalid := false.B }
                    is("b111".U){ io.uop := uopc.AND;  io.XcptInvalid := false.B }
                }
            }.elsewhen(io.funct7 === "b0100000".U){
                switch(io.funct3){
                    is("b000".U){ io.uop := uopc.SUB; io.XcptInvalid := false.B }
                    is("b101".U){ io.uop := uopc.SRA; io.XcptInvalid := false.B }
                }
            }
        }
        is(OPC_I){
            io.ALUSrcD   := true.B
            io.WriteEnableD := true.B
            switch(io.funct3){
                is("b000".U){ io.uop := uopc.ADDI;  io.XcptInvalid := false.B }
                is("b010".U){ io.uop := uopc.SLTI;  io.XcptInvalid := false.B }
                is("b011".U){ io.uop := uopc.SLTIU; io.XcptInvalid := false.B }
                is("b100".U){ io.uop := uopc.XORI;  io.XcptInvalid := false.B }
                is("b110".U){ io.uop := uopc.ORI;   io.XcptInvalid := false.B }
                is("b111".U){ io.uop := uopc.ANDI;  io.XcptInvalid := false.B }
                is("b001".U){
                    when(io.funct7 === "b0000000".U){
                        io.uop := uopc.SLLI; io.immSel := 1.U; io.XcptInvalid := false.B
                    }
                }
                is("b101".U){
                    when(io.funct7 === "b0000000".U){
                        io.uop := uopc.SRLI; io.immSel := 1.U; io.XcptInvalid := false.B
                    }.elsewhen(io.funct7 === "b0100000".U){
                        io.uop := uopc.SRAI; io.immSel := 1.U; io.XcptInvalid := false.B
                    }
                }
            }
        }
        is(OPC_B){
            io.immSel  := 3.U   // B-Type imm calculation
            io.BranchD := true.B
            // WriteEnableD stays false: B-type has no rd field, inst[11:7] is imm bits
            switch(io.funct3){
                is("b000".U){ io.uop := uopc.BEQ;  io.XcptInvalid := false.B }
                is("b001".U){ io.uop := uopc.BNE;  io.XcptInvalid := false.B }
                is("b100".U){ io.uop := uopc.BLT;  io.XcptInvalid := false.B }
                is("b101".U){ io.uop := uopc.BGE;  io.XcptInvalid := false.B }
                is("b110".U){ io.uop := uopc.BLTU; io.XcptInvalid := false.B }
                is("b111".U){ io.uop := uopc.BGEU; io.XcptInvalid := false.B }
            }
        }
        is(OPC_JAL){
            io.immSel      := 2.U   // J-Type imm calculation
            io.JumpD       := true.B
            io.WriteEnableD   := true.B  // writes pc+4 to rd
            io.uop         := uopc.JAL
            io.XcptInvalid := false.B
        }
        is(OPC_JALR){
            // immSel stays default (0.U) -> JALR is I-Type encoded
            io.immSel      := 0.U
            io.ALUSrcD     := true.B
            io.JumpD       := true.B
            io.BranchD     := true.B
            io.WriteEnableD:= true.B  // writes pc+4 to rd
            io.uop         := uopc.JALR
            io.XcptInvalid := false.B
        }
    }
}

class SignExtend extends Module {
    val io = IO(new Bundle {
        val imm_in  = Input(UInt(25.W))    // inst[31:7]
        val sel     = Input(UInt(2.W))
        val imm_out = Output(UInt(32.W))
    })

    val full       = Cat(Fill(20, io.imm_in(24)), io.imm_in(24,13))   // I-type, 12-bit signed immediate
    val shamt      = Cat(0.U(27.W), io.imm_in(17,13))                 // 5-bit shamt, zero-extended
    val jump_cat   = Cat(io.imm_in(24), io.imm_in(12,5), io.imm_in(13), io.imm_in(23,14))
    val jump_imm   = Cat(Fill(11, jump_cat(19)), jump_cat, 0.U)       // J-type, 20-bit
    val branch_imm = Cat(Fill(19, io.imm_in(24)), io.imm_in(24), io.imm_in(0), io.imm_in(23,18), io.imm_in(4,1), 0.U)

    io.imm_out := full

    switch(io.sel){
        is(0.U){io.imm_out := full }
        is(1.U){io.imm_out := shamt }
        is(2.U){io.imm_out := jump_imm }
        is(3.U){io.imm_out := branch_imm }
    }
}

class ID extends Module{
    val io = IO(new Bundle{
        val inst           = Input(UInt(32.W))
        val pcD            = Input(UInt(32.W))
        val pcPlus4D       = Input(UInt(32.W))
        val WriteEnableW      = Input(Bool())      // write enable for the instruction currently in WB
        val rdW            = Input(UInt(5.W))
        val ResultW        = Input(UInt(32.W))

        val uop           = Output(uopc())
        val WriteEnableD     = Output(Bool())
        val ALUSrcD       = Output(Bool())
        val ImmExtD       = Output(UInt(32.W))
        val BranchD       = Output(Bool())
        val JumpD         = Output(Bool())
        val XcptInvalid   = Output(Bool())

        val rdD           = Output(UInt(5.W))
        val RD1D          = Output(UInt(32.W))
        val RD2D          = Output(UInt(32.W))
        val pcD_out        = Output(UInt(32.W))
        val pcPlus4D_out   = Output(UInt(32.W))
    })

    val opcode = io.inst(6,0)
    val funct3 = io.inst(14,12)
    val funct7 = io.inst(31,25)
    val rs1    = io.inst(19,15)
    val rs2    = io.inst(24,20)
    val rd     = io.inst(11,7)

    val rf   = Module(new regFile)
    val cu   = Module(new ControlUnit)
    val sigex = Module(new SignExtend)

    cu.io.opcode := opcode
    cu.io.funct3 := funct3
    cu.io.funct7 := funct7

    sigex.io.imm_in := io.inst(31,7)
    sigex.io.sel    := cu.io.immSel

    rf.io.req_1.addr := rs1
    rf.io.req_2.addr := rs2
    rf.io.req_3.addr := io.rdW
    rf.io.req_3.w_en := io.WriteEnableW
    rf.io.req_3.data := io.ResultW

    io.RD1D        := rf.io.resp_1.data
    io.RD2D        := rf.io.resp_2.data
    io.rdD         := rd
    io.uop         := cu.io.uop
    io.XcptInvalid := cu.io.XcptInvalid
    io.ImmExtD     := sigex.io.imm_out
    io.WriteEnableD   := cu.io.WriteEnableD
    io.ALUSrcD     := cu.io.ALUSrcD
    io.BranchD     := cu.io.BranchD
    io.JumpD       := cu.io.JumpD
    io.pcD_out      := io.pcD
    io.pcPlus4D_out := io.pcPlus4D
}