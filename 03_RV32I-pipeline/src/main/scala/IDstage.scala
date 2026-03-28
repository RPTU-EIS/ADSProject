package core_tile

import chisel3._
import chisel3.util._
import uopc._

class ID extends Module {
  val io = IO(new Bundle {
    val instr = Input(UInt(32.W))
    val pc    = Input(UInt(32.W))      // PC of this instruction (byte address)

    // Register file interface
    val regFileReq_A  = Output(new regFileReadReq)
    val regFileResp_A = Input(new regFileReadResp)
    val regFileReq_B  = Output(new regFileReadReq)
    val regFileResp_B = Input(new regFileReadResp)

    // Outputs to ID barrier
    val uop         = Output(uopc())
    val rd          = Output(UInt(5.W))
    val rs1         = Output(UInt(5.W))
    val rs2         = Output(UInt(5.W))
    val operandA    = Output(UInt(32.W))
    val operandB    = Output(UInt(32.W))
    val opBSel      = Output(Bool())       // true = operandB is immediate
    val wrEn        = Output(Bool())       // register write enable
    val pcOut       = Output(UInt(32.W))   // PC passthrough for EX stage
    val branchTarget= Output(UInt(32.W))   // precomputed branch/JAL target
    val XcptInvalid = Output(Bool())
  })

  // =====================================================
  // Extract instruction fields
  // =====================================================
  val opcode = io.instr(6, 0)
  val rd     = io.instr(11, 7)
  val funct3 = io.instr(14, 12)
  val rs1    = io.instr(19, 15)
  val rs2    = io.instr(24, 20)
  val funct7 = io.instr(31, 25)

  // I-type immediate: sign-extend bits [31:20]
  val immI = io.instr(31, 20).asSInt.pad(32).asUInt

  // B-type immediate: {inst[31], inst[7], inst[30:25], inst[11:8], 0}
  val immB = Cat(io.instr(31), io.instr(7), io.instr(30, 25),
                 io.instr(11, 8), 0.U(1.W)).asSInt.pad(32).asUInt

  // J-type immediate: {inst[31], inst[19:12], inst[20], inst[30:21], 0}
  val immJ = Cat(io.instr(31), io.instr(19, 12), io.instr(20),
                 io.instr(30, 21), 0.U(1.W)).asSInt.pad(32).asUInt

  // =====================================================
  // Register file reads
  // =====================================================
  io.regFileReq_A.addr := rs1
  io.regFileReq_B.addr := rs2

  // =====================================================
  // Default outputs
  // =====================================================
  io.uop         := NOP
  io.XcptInvalid := false.B
  io.rd          := rd
  io.rs1         := rs1
  io.rs2         := rs2
  io.operandA    := io.regFileResp_A.data
  io.operandB    := io.regFileResp_B.data
  io.opBSel      := false.B
  io.wrEn        := true.B
  io.pcOut       := io.pc
  io.branchTarget:= io.pc + immB  // default: B-type target

  // =====================================================
  // R-type instructions (opcode = 0110011)
  // =====================================================
  when(opcode === Opcodes.R_TYPE) {
    io.operandB := io.regFileResp_B.data
    io.opBSel   := false.B
    io.wrEn     := true.B

    switch(funct3) {
      is(Funct3.ADD_SUB) {
        when(funct7 === Funct7.NORMAL) { io.uop := ADD }
        .elsewhen(funct7 === Funct7.ALT) { io.uop := SUB }
        .otherwise { io.uop := NOP; io.XcptInvalid := true.B }
      }
      is(Funct3.SLL)     { io.uop := SLL }
      is(Funct3.SLT)     { io.uop := SLT }
      is(Funct3.SLTU)    { io.uop := SLTU }
      is(Funct3.XOR)     { io.uop := XOR }
      is(Funct3.SRL_SRA) {
        when(funct7 === Funct7.NORMAL) { io.uop := SRL }
        .elsewhen(funct7 === Funct7.ALT) { io.uop := SRA }
        .otherwise { io.uop := NOP; io.XcptInvalid := true.B }
      }
      is(Funct3.OR)      { io.uop := OR }
      is(Funct3.AND)     { io.uop := AND }
    }
  }
  // =====================================================
  // I-type ALU instructions (opcode = 0010011)
  // =====================================================
  .elsewhen(opcode === Opcodes.I_TYPE) {
    io.operandB := immI
    io.opBSel   := true.B
    io.wrEn     := true.B

    switch(funct3) {
      is(Funct3.ADD_SUB) { io.uop := ADDI }
      is(Funct3.SLT)     { io.uop := SLTI }
      is(Funct3.SLTU)    { io.uop := SLTIU }
      is(Funct3.XOR)     { io.uop := XORI }
      is(Funct3.OR)      { io.uop := ORI }
      is(Funct3.AND)     { io.uop := ANDI }
      is(Funct3.SLL) {
        when(funct7 === Funct7.NORMAL) {
          io.uop := SLLI; io.operandB := io.instr(24, 20).pad(32)
        }.otherwise { io.uop := NOP; io.XcptInvalid := true.B }
      }
      is(Funct3.SRL_SRA) {
        when(funct7 === Funct7.NORMAL) {
          io.uop := SRLI; io.operandB := io.instr(24, 20).pad(32)
        }.elsewhen(funct7 === Funct7.ALT) {
          io.uop := SRAI; io.operandB := io.instr(24, 20).pad(32)
        }.otherwise { io.uop := NOP; io.XcptInvalid := true.B }
      }
    }
  }
  // =====================================================
  // B-type branch instructions (opcode = 1100011)
  // =====================================================
  .elsewhen(opcode === Opcodes.B_TYPE) {
    io.operandA    := io.regFileResp_A.data  // rs1 for comparison
    io.operandB    := io.regFileResp_B.data  // rs2 for comparison
    io.opBSel      := false.B                // both from registers
    io.wrEn        := false.B                // branches don't write rd
    io.rd          := 0.U                    // no destination register
    io.branchTarget:= io.pc + immB           // branch target address

    switch(funct3) {
      is(BranchFunct3.BEQ)  { io.uop := BEQ }
      is(BranchFunct3.BNE)  { io.uop := BNE }
      is(BranchFunct3.BLT)  { io.uop := BLT }
      is(BranchFunct3.BGE)  { io.uop := BGE }
      is(BranchFunct3.BLTU) { io.uop := BLTU }
      is(BranchFunct3.BGEU) { io.uop := BGEU }
    }
  }
  // =====================================================
  // JAL (opcode = 1101111)
  // =====================================================
  .elsewhen(opcode === Opcodes.JAL) {
    io.uop          := JAL
    io.operandA     := 0.U                  // not used by ALU
    io.operandB     := 0.U                  // not used by ALU
    io.opBSel       := true.B               // prevent forwarding on B
    io.rs1          := 0.U                  // prevent false forwarding
    io.rs2          := 0.U                  // prevent false forwarding
    io.wrEn         := true.B               // write return addr to rd
    io.branchTarget := io.pc + immJ         // jump target
  }
  // =====================================================
  // JALR (opcode = 1100111)
  // =====================================================
  .elsewhen(opcode === Opcodes.JALR) {
    io.uop          := JALR
    io.operandA     := io.regFileResp_A.data // rs1 (base address)
    io.operandB     := immI                  // I-type immediate (offset)
    io.opBSel       := true.B               // operandB is immediate
    io.rs2          := 0.U                  // JALR has no rs2
    io.wrEn         := true.B               // write return addr to rd
    io.branchTarget := 0.U                  // computed in EX stage
  }
  // =====================================================
  // Invalid opcode
  // =====================================================
  .otherwise {
    io.uop := NOP; io.XcptInvalid := true.B
  }
}
