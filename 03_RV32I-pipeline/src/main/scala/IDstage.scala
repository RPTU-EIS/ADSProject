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
import chisel3.experimental.ChiselEnum

object Op extends ChiselEnum {
    val INVALID = Value(0.U)

    val ADD  = Value(1.U)
    val SUB  = Value(2.U)
    val XOR  = Value(3.U)
    val OR   = Value(4.U)
    val AND  = Value(5.U)

    val ADDI = Value(6.U)
    val XORI = Value(7.U)
    val ORI  = Value(8.U)
    val ANDI = Value(9.U)

    val LW   = Value(10.U)
    val SW   = Value(11.U)

    val BEQ  = Value(12.U)
    val BNE  = Value(13.U)

    val LUI  = Value(14.U)
}

// -----------------------------------------
// Decode Stage
// -----------------------------------------

class ID extends Module{
    val io = IO(new Bundle{
        val inst        = Input(UInt(32.W))
        val w_en        = Input(Bool())
        val rd_in       = Input(UInt(5.W))
        val write_data  = Input(UInt(32.W)) 
        
        val uop         = Output(Op())
        val rd_out      = Output(UInt(5.W))
        val operandA    = Output(UInt(32.W))
        val operandB    = Output(UInt(32.W))
        val XcptInvalid = Output(Bool())
    })

//ToDo: Add your implementation according to the specification above here 
    val opcode  = io.inst(6,0)
    val funct3  = io.inst(14,12)
    val funct7  = io.inst(31,25)
    val rs1     = io.inst(19,15)
    val rs2     = io.inst(24,20)
    val rd      = io.inst(11,7)
    val i_imm   = Cat(Fill(20, io.inst(31)), io.inst(31,20))
    val s_imm   = Cat(io.inst(31,25), io.inst(11,7))
    val u_imm   = io.inst(31,12)

    val rf = Module(new regFile)

    val OPC_R = "b0110011".U
    val OPC_I = "b0010011".U

    io.uop := Op.INVALID
    io.XcptInvalid := true.B

    switch(opcode){
        is(OPC_R){
            when(funct3 === "b000".U && funct7 === "b0000000".U) {
                io.uop := Op.ADD
                io.XcptInvalid := false.B
            }
            .elsewhen(funct3 === "b000".U && funct7 === "b0100000".U) {
                io.uop := Op.SUB
                io.XcptInvalid := false.B
            }
            .elsewhen(funct3 === "b100".U && funct7 === "b0000000".U) {
                io.uop := Op.XOR
                io.XcptInvalid := false.B
            }
            .elsewhen(funct3 === "b110".U && funct7 === "b0000000".U) {
                io.uop := Op.OR
                io.XcptInvalid := false.B
            }
            .elsewhen(funct3 === "b111".U && funct7 === "b0000000".U) {
                io.uop := Op.AND
                io.XcptInvalid := false.B
            }
        }
        is(OPC_I){
            switch(funct3) {
                is("b000".U) {
                    io.uop := Op.ADDI
                    io.XcptInvalid := false.B
                }
                is("b100".U) {
                    io.uop := Op.XORI
                    io.XcptInvalid := false.B
                }
                is("b110".U) {
                    io.uop := Op.ORI
                    io.XcptInvalid := false.B
                }
                is("b111".U) {
                    io.uop := Op.ANDI
                    io.XcptInvalid := false.B
                }
            }
        }
    }

    rf.io.req_1.addr := rs1
    rf.io.req_2.addr := rs2
    rf.io.req_3.addr := io.rd_in
    rf.io.req_3.w_en := io.w_en
    rf.io.req_3.data := io.write_data

    io.operandA := rf.io.resp_1.data

    when(opcode === OPC_I) {
        io.operandB := i_imm
    }
    .otherwise {
        io.operandB := rf.io.resp_2.data
    }

    io.rd_out := rd
}