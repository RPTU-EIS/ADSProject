package core_tile

import chisel3._
import chisel3.util._
import Assignment02.{ALU, ALUOp}
import uopc._

class EX extends Module {
  val io = IO(new Bundle {
    val uop          = Input(uopc())
    val operandA     = Input(UInt(32.W))
    val operandB     = Input(UInt(32.W))
    val pc           = Input(UInt(32.W))
    val branchTarget = Input(UInt(32.W))
    val XcptInvalid  = Input(Bool())

    // Branch comparison operands (forwarded rs1/rs2)
    val branchOpA = Input(UInt(32.W))
    val branchOpB = Input(UInt(32.W))

    // BTB prediction info from pipeline
    val btbHit          = Input(Bool())
    val btbPredictTaken = Input(Bool())

    // Outputs to EX barrier
    val aluResult = Output(UInt(32.W))
    val exception = Output(Bool())

    // Redirect outputs (to IF stage and flush logic)
    val redirectEn     = Output(Bool())
    val redirectTarget = Output(UInt(32.W))

    // BTB update outputs (to BTB module)
    val btbUpdate       = Output(Bool())
    val btbUpdatePC     = Output(UInt(32.W))
    val btbUpdateTarget = Output(UInt(32.W))
    val btbMispredicted = Output(Bool())

    // Performance counters
    val totalBranches    = Output(UInt(32.W))
    val totalMispredicts = Output(UInt(32.W))
  })

  // ===========================================================
  // ALU
  // ===========================================================
  val alu = Module(new ALU)
  alu.io.operandA := io.operandA
  alu.io.operandB := io.operandB
  alu.io.operation := ALUOp.ADD

  switch(io.uop) {
    is(ADD)  {alu.io.operation:=ALUOp.ADD}; is(ADDI) {alu.io.operation:=ALUOp.ADD}
    is(SUB)  {alu.io.operation:=ALUOp.SUB}
    is(AND)  {alu.io.operation:=ALUOp.AND}; is(ANDI) {alu.io.operation:=ALUOp.AND}
    is(OR)   {alu.io.operation:=ALUOp.OR};  is(ORI)  {alu.io.operation:=ALUOp.OR}
    is(XOR)  {alu.io.operation:=ALUOp.XOR}; is(XORI) {alu.io.operation:=ALUOp.XOR}
    is(SLL)  {alu.io.operation:=ALUOp.SLL}; is(SLLI) {alu.io.operation:=ALUOp.SLL}
    is(SRL)  {alu.io.operation:=ALUOp.SRL}; is(SRLI) {alu.io.operation:=ALUOp.SRL}
    is(SRA)  {alu.io.operation:=ALUOp.SRA}; is(SRAI) {alu.io.operation:=ALUOp.SRA}
    is(SLT)  {alu.io.operation:=ALUOp.SLT}; is(SLTI) {alu.io.operation:=ALUOp.SLT}
    is(SLTU) {alu.io.operation:=ALUOp.SLTU};is(SLTIU){alu.io.operation:=ALUOp.SLTU}
    is(NOP)  {alu.io.operation:=ALUOp.ADD}
    is(JALR) {alu.io.operation:=ALUOp.ADD}  // rs1 + imm = target
  }

  // ===========================================================
  // Instruction type flags
  // ===========================================================
  val isBranch = (io.uop===BEQ || io.uop===BNE || io.uop===BLT ||
    io.uop===BGE || io.uop===BLTU || io.uop===BGEU)
  val isJAL    = io.uop === JAL
  val isJALR   = io.uop === JALR

  // ===========================================================
  // Branch condition evaluation
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
  // Prediction analysis
  // ===========================================================
  // What was predicted:
  //   If BTB hit: use btbPredictTaken
  //   If BTB miss: implicit "not taken" (static prediction)
  val predictedTaken = io.btbHit && io.btbPredictTaken

  // Misprediction detection for conditional branches:
  //   predicted taken but actually not taken, OR
  //   predicted not taken but actually taken
  val branchMispredicted = isBranch && (predictedTaken =/= branchTaken)

  // False BTB hit: BTB predicted taken for a non-branch instruction
  // (due to aliasing or stale entry). Must correct.
  val falseBtbHit = !isBranch && !isJAL && !isJALR && predictedTaken

  // ===========================================================
  // Return address for JAL/JALR
  // ===========================================================
  val returnAddr = io.pc + 4.U
  val jalrTarget = alu.io.aluResult & "hFFFFFFFE".U

  // ===========================================================
  // Result selection
  // ===========================================================
  io.aluResult := alu.io.aluResult
  when(isJAL || isJALR) {
    io.aluResult := returnAddr
  }.elsewhen(isBranch) {
    io.aluResult := 0.U
  }

  // ===========================================================
  // Redirect logic
  // ===========================================================
  io.redirectEn     := false.B
  io.redirectTarget := io.branchTarget

  when(branchMispredicted) {
    io.redirectEn := true.B
    when(branchTaken) {
      // Predicted not-taken but actually taken → go to branch target
      io.redirectTarget := io.branchTarget
    }.otherwise {
      // Predicted taken but actually not-taken → go to sequential PC+4
      io.redirectTarget := io.pc + 4.U
    }
  }.elsewhen(falseBtbHit) {
    // BTB falsely predicted taken for non-branch → go back to sequential
    io.redirectEn     := true.B
    io.redirectTarget := io.pc + 4.U
  }.elsewhen(isJAL) {
    io.redirectEn     := true.B
    io.redirectTarget := io.branchTarget
  }.elsewhen(isJALR) {
    io.redirectEn     := true.B
    io.redirectTarget := jalrTarget
  }

  // ===========================================================
  // BTB update outputs
  // Send update for ALL conditional branches (hit or miss)
  // so BTB can create new entries or update existing FSM
  // ===========================================================
  io.btbUpdate       := isBranch
  io.btbUpdatePC     := io.pc
  io.btbUpdateTarget := io.branchTarget
  io.btbMispredicted := branchMispredicted

  // ===========================================================
  // Exception passthrough
  // ===========================================================
  io.exception := io.XcptInvalid

  // ===========================================================
  // Performance counters (hardware registers)
  // ===========================================================
  val branchCount     = RegInit(0.U(32.W))
  val mispredictCount = RegInit(0.U(32.W))

  when(isBranch) {
    branchCount := branchCount + 1.U
  }
  when(branchMispredicted) {
    mispredictCount := mispredictCount + 1.U
  }

  io.totalBranches    := branchCount
  io.totalMispredicts := mispredictCount
}