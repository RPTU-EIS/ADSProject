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
        val ALUsrc      = Output(Bool())   // operandB: true = immediate, false = rs2
        val immSel      = Output(Bool())   // sign-extend block: false = full imm, true = shamt
        val XcptInvalid = Output(Bool())
    })

    val OPC_R = "b0110011".U
    val OPC_I = "b0010011".U

    io.uop         := uopc.INVALID
    io.ALUsrc      := false.B
    io.immSel      := false.B
    io.XcptInvalid := true.B

    switch(io.opcode){
        is(OPC_R){
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
            io.ALUsrc := true.B                       //
            switch(io.funct3){
                is("b000".U){ io.uop := uopc.ADDI;  io.XcptInvalid := false.B }
                is("b010".U){ io.uop := uopc.SLTI;  io.XcptInvalid := false.B }
                is("b011".U){ io.uop := uopc.SLTIU; io.XcptInvalid := false.B }
                is("b100".U){ io.uop := uopc.XORI;  io.XcptInvalid := false.B }
                is("b110".U){ io.uop := uopc.ORI;   io.XcptInvalid := false.B }
                is("b111".U){ io.uop := uopc.ANDI;  io.XcptInvalid := false.B }
                is("b001".U){
                    when(io.funct7 === "b0000000".U){
                        io.uop := uopc.SLLI; io.immSel := true.B; io.XcptInvalid := false.B
                    }
                }
                is("b101".U){
                    when(io.funct7 === "b0000000".U){
                        io.uop := uopc.SRLI; io.immSel := true.B; io.XcptInvalid := false.B
                    }.elsewhen(io.funct7 === "b0100000".U){
                        io.uop := uopc.SRAI; io.immSel := true.B; io.XcptInvalid := false.B
                    }
                }
            }
        }
    }
}

class SignExtend extends Module {
    val io = IO(new Bundle {
        val imm_in  = Input(UInt(12.W))    // inst[31:20]
        val sel     = Input(Bool())         // false = full sign-ext imm, true = shamt (zero-ext)
        val imm_out = Output(UInt(32.W))
    })
    val full  = Cat(Fill(20, io.imm_in(11)), io.imm_in)   // 12-bit signed immediate
    val shamt = Cat(0.U(27.W), io.imm_in(4,0))            // 5-bit shamt, zero-extended
    io.imm_out := Mux(io.sel, shamt, full)
}

class ID extends Module{
    val io = IO(new Bundle{
        val inst        = Input(UInt(32.W))
        val w_en        = Input(Bool())
        val rd_in       = Input(UInt(5.W))
        val write_data  = Input(UInt(32.W))

        val uop         = Output(uopc())
        val wrten       = Output(Bool()) //
        val ALUsrc      = Output(Bool()) //
        val immExtnd    = Output(UInt(32.W)) //
        val XcptInvalid = Output(Bool())

        val rd_out      = Output(UInt(5.W))
        val operandA    = Output(UInt(32.W))
        val operandB    = Output(UInt(32.W))
    })

    val opcode = io.inst(6,0)
    val funct3 = io.inst(14,12)
    val funct7 = io.inst(31,25)
    val rs1    = io.inst(19,15)
    val rs2    = io.inst(24,20)
    val rd     = io.inst(11,7)
    val imm12  = io.inst(31,20)        // raw 12-bit immediate field

    val rf   = Module(new regFile)
    val cu   = Module(new ControlUnit)
    val sigex = Module(new SignExtend)

    // control unit: instruction -> uop + control signals
    cu.io.opcode := opcode
    cu.io.funct3 := funct3
    cu.io.funct7 := funct7

    // sign-extend, mode chosen by the control unit
    sigex.io.imm_in := imm12
    sigex.io.sel    := cu.io.immSel

    // register file
    rf.io.req_1.addr := rs1
    rf.io.req_2.addr := rs2
    rf.io.req_3.addr := io.rd_in
    rf.io.req_3.w_en := io.w_en
    rf.io.req_3.data := io.write_data

    // datapath outputs
    io.operandA    := rf.io.resp_1.data
    io.operandB    := rf.io.resp_2.data
    io.rd_out      := rd
    io.uop         := cu.io.uop
    io.XcptInvalid := cu.io.XcptInvalid
    io.immExtnd    := sigex.io.imm_out
    io.wrten       := true.B //HARDWIRED TO 1 BECAUSE WE ONLY DO R-TYPE AND I-TYPE
    io.ALUsrc       := cu.io.ALUsrc

}