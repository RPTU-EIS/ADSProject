package core_tile

import chisel3._
import chisel3.util._
import Assignment02.{ALU, ALUOp}
import uopc._

class EX extends Module {
  val io = IO(new Bundle {
    // From ID barrier (with forwarding applied in core)
    val uop         = Input(uopc())
    val operandA    = Input(UInt(32.W))    // forwarded rs1 value
    val operandB    = Input(UInt(32.W))    // forwarded rs2 or immediate
    val pc          = Input(UInt(32.W))    // PC of this instruction
    val branchTarget= Input(UInt(32.W))   // precomputed B/JAL target from ID
    val XcptInvalid = Input(Bool())

    // For branch comparison (original rs1, rs2 before opBSel mux)
    val branchOpA   = Input(UInt(32.W))    // forwarded rs1 value (always)
    val branchOpB   = Input(UInt(32.W))    // forwarded rs2 value (always)

    // Outputs to EX barrier
    val aluResult   = Output(UInt(32.W))   // result for pipeline (ALU or PC+4)
    val exception   = Output(Bool())

    // Redirect outputs (to IF stage and flush logic)
    val redirectEn  = Output(Bool())
    val redirectTarget = Output(UInt(32.W))
  })

  // ===========================================================
  // ALU — computes result for R/I type, and target for JALR
  // ===========================================================
  val alu = Module(new ALU)
  alu.io.operandA := io.operandA
  alu.io.operandB := io.operandB
  alu.io.operation := ALUOp.ADD  // default

  switch(io.uop) {
    is(ADD)   { alu.io.operation := ALUOp.ADD }
    is(ADDI)  { alu.io.operation := ALUOp.ADD }
    is(SUB)   { alu.io.operation := ALUOp.SUB }
    is(AND)   { alu.io.operation := ALUOp.AND }
    is(ANDI)  { alu.io.operation := ALUOp.AND }
    is(OR)    { alu.io.operation := ALUOp.OR }
    is(ORI)   { alu.io.operation := ALUOp.OR }
    is(XOR)   { alu.io.operation := ALUOp.XOR }
    is(XORI)  { alu.io.operation := ALUOp.XOR }
    is(SLL)   { alu.io.operation := ALUOp.SLL }
    is(SLLI)  { alu.io.operation := ALUOp.SLL }
    is(SRL)   { alu.io.operation := ALUOp.SRL }
    is(SRLI)  { alu.io.operation := ALUOp.SRL }
    is(SRA)   { alu.io.operation := ALUOp.SRA }
    is(SRAI)  { alu.io.operation := ALUOp.SRA }
    is(SLT)   { alu.io.operation := ALUOp.SLT }
    is(SLTI)  { alu.io.operation := ALUOp.SLT }
    is(SLTU)  { alu.io.operation := ALUOp.SLTU }
    is(SLTIU) { alu.io.operation := ALUOp.SLTU }
    is(NOP)   { alu.io.operation := ALUOp.ADD }
    // JALR: ALU computes rs1 + imm = target address
    is(JALR)  { alu.io.operation := ALUOp.ADD }
  }

  // ===========================================================
  // Return address for JAL/JALR
  // ===========================================================
  val returnAddr = io.pc + 4.U

  // JALR target = (rs1 + imm) & ~1 (clear bit 0)
  val jalrTarget = alu.io.aluResult & "hFFFFFFFE".U

  // ===========================================================
  // Branch condition evaluation (for B-type only)
  // Uses branchOpA/branchOpB which are the forwarded rs1/rs2 values
  // ===========================================================
  val branchTaken = Wire(Bool())
  branchTaken := false.B

  switch(io.uop) {
    is(BEQ)  { branchTaken := io.branchOpA === io.branchOpB }
    is(BNE)  { branchTaken := io.branchOpA =/= io.branchOpB }
    is(BLT)  { branchTaken := io.branchOpA.asSInt < io.branchOpB.asSInt }
    is(BGE)  { branchTaken := io.branchOpA.asSInt >= io.branchOpB.asSInt }
    is(BLTU) { branchTaken := io.branchOpA < io.branchOpB }
    is(BGEU) { branchTaken := io.branchOpA >= io.branchOpB }
  }

  // ===========================================================
  // Determine instruction type flags
  // ===========================================================
  val isBranch = (io.uop === BEQ || io.uop === BNE || io.uop === BLT ||
                  io.uop === BGE || io.uop === BLTU || io.uop === BGEU)
  val isJAL    = io.uop === JAL
  val isJALR   = io.uop === JALR

  // ===========================================================
  // Result selection
  // ===========================================================
  // R/I type → ALU result
  // JAL/JALR → return address (PC + 4)
  // B-type   → 0 (no register write anyway)
  io.aluResult := alu.io.aluResult                     // default: R/I type
  when(isJAL || isJALR) {
    io.aluResult := returnAddr                          // write PC+4 to link register
  }.elsewhen(isBranch) {
    io.aluResult := 0.U                                // branches don't produce a result
  }

  // ===========================================================
  // Redirect logic
  // Static prediction: conditional branches predicted NOT taken
  //   → redirect only if branch is actually taken (misprediction)
  // JAL/JALR: always taken → always redirect
  // ===========================================================
  io.redirectEn := false.B
  io.redirectTarget := io.branchTarget

  when(isBranch && branchTaken) {
    io.redirectEn     := true.B
    io.redirectTarget := io.branchTarget                // B-type target from ID
  }.elsewhen(isJAL) {
    io.redirectEn     := true.B
    io.redirectTarget := io.branchTarget                // JAL target from ID
  }.elsewhen(isJALR) {
    io.redirectEn     := true.B
    io.redirectTarget := jalrTarget                     // JALR target = (rs1+imm)&~1
  }

  // ===========================================================
  // Exception passthrough
  // ===========================================================
  io.exception := io.XcptInvalid
}
