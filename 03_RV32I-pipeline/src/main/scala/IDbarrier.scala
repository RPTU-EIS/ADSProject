package core_tile

import chisel3._
import uopc._

class IDBarrier extends Module {
  val io = IO(new Bundle {
    val inUOP          = Input(uopc());     val outUOP          = Output(uopc())
    val inRD           = Input(UInt(5.W));  val outRD           = Output(UInt(5.W))
    val inRS1          = Input(UInt(5.W));  val outRS1          = Output(UInt(5.W))
    val inRS2          = Input(UInt(5.W));  val outRS2          = Output(UInt(5.W))
    val inOperandA     = Input(UInt(32.W)); val outOperandA     = Output(UInt(32.W))
    val inOperandB     = Input(UInt(32.W)); val outOperandB     = Output(UInt(32.W))
    val inOpBSel       = Input(Bool());     val outOpBSel       = Output(Bool())
    val inWrEn         = Input(Bool());     val outWrEn         = Output(Bool())
    val inPC           = Input(UInt(32.W)); val outPC           = Output(UInt(32.W))
    val inBranchTarget = Input(UInt(32.W)); val outBranchTarget = Output(UInt(32.W))
    val inXcptInvalid  = Input(Bool());     val outXcptInvalid  = Output(Bool())
    // BTB prediction info (passthrough)
    val inBtbHit          = Input(Bool());  val outBtbHit          = Output(Bool())
    val inBtbPredictTaken = Input(Bool());  val outBtbPredictTaken = Output(Bool())
    val flush             = Input(Bool())
  })

  val uopR     = RegInit(NOP);          val rdR     = RegInit(0.U(5.W))
  val rs1R     = RegInit(0.U(5.W));     val rs2R    = RegInit(0.U(5.W))
  val opAR     = RegInit(0.U(32.W));    val opBR    = RegInit(0.U(32.W))
  val opBSelR  = RegInit(false.B);      val wrEnR   = RegInit(false.B)
  val pcR      = RegInit(0.U(32.W));    val btR     = RegInit(0.U(32.W))
  val xcptR    = RegInit(false.B)
  val btbHitR  = RegInit(false.B);      val btbPTR  = RegInit(false.B)

  when(io.flush) {
    uopR:=NOP; rdR:=0.U; rs1R:=0.U; rs2R:=0.U
    opAR:=0.U; opBR:=0.U; opBSelR:=false.B; wrEnR:=false.B
    pcR:=0.U; btR:=0.U; xcptR:=false.B
    btbHitR:=false.B; btbPTR:=false.B
  }.otherwise {
    uopR:=io.inUOP; rdR:=io.inRD; rs1R:=io.inRS1; rs2R:=io.inRS2
    opAR:=io.inOperandA; opBR:=io.inOperandB; opBSelR:=io.inOpBSel
    wrEnR:=io.inWrEn; pcR:=io.inPC; btR:=io.inBranchTarget
    xcptR:=io.inXcptInvalid
    btbHitR:=io.inBtbHit; btbPTR:=io.inBtbPredictTaken
  }

  io.outUOP:=uopR; io.outRD:=rdR; io.outRS1:=rs1R; io.outRS2:=rs2R
  io.outOperandA:=opAR; io.outOperandB:=opBR; io.outOpBSel:=opBSelR
  io.outWrEn:=wrEnR; io.outPC:=pcR; io.outBranchTarget:=btR
  io.outXcptInvalid:=xcptR
  io.outBtbHit:=btbHitR; io.outBtbPredictTaken:=btbPTR
}
