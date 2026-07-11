// ADS I Class Project
// Pipelined RISC-V Core - Branch Target Buffer
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 05/12/2026 by Tobias Jauch (@tojauch)

/*
Branch Target Buffer (BTB): a hardware component that predicts the target address of conditional branch instructions to improve pipeline performance

Functionality (cf. slide 6-48 of the lecture slides):
    Stores target addresses and prediction information for conditional branch instructions
    On a branch instruction, checks if the instruction is in the BTB and retrieves the predicted target address and prediction state
    If the prediction is taken, the processor fetches the instruction from the predicted target address; if not taken, it continues sequentially
    Updates the BTB entry based on the actual outcome of the branch instruction (taken or not taken) and updates the prediction state accordingly

Inputs:
    PC: A 32-bit program counter representing the address of the branch instruction being fetched or executed.
    update: A 1-bit signal indicating whether the BTB should be updated with new information.
    updatePC: A 32-bit program counter associated with the branch instruction being updated.
    updateTarget: A 32-bit branch target address to be stored in the BTB.
    mispredicted: A 1-bit signal indicating whether the prediction turned out to be incorrect during execution (used to update the predictor).

Outputs:
    valid: A 1-bit signal indicating whether the BTB has a valid prediction for the provided program counter.
    target: A 32-bit signal representing the predicted branch target address when a valid prediction exists.
    predictTaken: A 1-bit signal indicating whether the branch is predicted to be taken or not.

*/

package core_tile

import chisel3._
import chisel3.util._
import uopc._

// -----------------------------------------
// Branch Target Buffer
// -----------------------------------------

class BTB extends Module {
  val io = IO(new Bundle {
    // Add I/O ports according to the specification above here
    val PC      = Input(UInt(32.W))
    val update  = Input(Bool())
    val updatePC = Input(UInt(32.W))
    val updateTarget  = Input(UInt(32.W))
    val mispredicted  = Input(Bool())
    val branchTaken   = Input(Bool())

    val valid = Output(Bool())
    val target = Output(UInt(32.W))
    val predictTaken = Output(Bool())
  })

  //ToDo: Add your implementation according to the specification in assignment 6 here. 

  val NUM_SETS = 8
  val NUM_WAYS = 2

  val INDEX_BITS = log2Ceil(NUM_SETS)      //3bits
  val TAG_BITS = 32 - INDEX_BITS - 2      //27 bits

  class BTBEntry extends Bundle {
    val valid = Bool()
    val tag   = UInt(TAG_BITS.W)
    val target = UInt(32.W)
    val predictor = UInt(2.W)

    def init(initTag: UInt, initTarget: UInt): Unit = {
      valid := true.B
      tag := initTag
      target := initTarget
      // Initial state: Weakly Taken (10)
      predictor := "b10".U(2.W)
    }
  }

  val btb = Reg(Vec(NUM_SETS, Vec(NUM_WAYS, new BTBEntry)))

  // Initialize all entries to invalid
  for (set <- 0 until NUM_SETS) {
    for (way <- 0 until NUM_WAYS) {
      btb(set)(way).valid := false.B
      btb(set)(way).tag := 0.U(TAG_BITS.W)
      btb(set)(way).target := 0.U(32.W)
      btb(set)(way).predictor := "b10".U(2.W)  // Weakly Taken by default
    }
  }

  // LRU tracking: 8 sets, each tracking which way (0 or 1) was most recently used
  val lru = RegInit(VecInit(Seq.fill(NUM_SETS)(0.U(1.W))))

  // Extract index and tag from PC
  val index = io.PC(INDEX_BITS + 1, 2)  // 3 bits: PC[4:2]
  val tag = io.PC(31, INDEX_BITS + 2)   // 27 bits: PC[31:5]

  // Extract index and tag from updatePC
  val updateIndex = io.updatePC(INDEX_BITS + 1, 2)
  val updateTag = io.updatePC(31, INDEX_BITS + 2)

  // Read operation: check if the PC is in the BTB
  val hit = WireDefault(false.B)
  val hitWay = WireDefault(0.U(log2Ceil(NUM_WAYS).W))
  val hitTag = WireDefault(0.U(TAG_BITS.W))
  val hitTarget = WireDefault(0.U(32.W))
  val hitPredictor = WireDefault("b10".U(2.W))

  // Find matching entry
  for (way <- 0 until NUM_WAYS) {
    when(btb(index)(way).valid && btb(index)(way).tag === tag) {
      hit := true.B
      hitWay := way.U
      hitTag := btb(index)(way).tag
      hitTarget := btb(index)(way).target
      hitPredictor := btb(index)(way).predictor
    }
  }

  // Determine prediction based on the predictor state
  // 11 or 10: Taken, 01 or 00: Not Taken
  val predictTaken = WireDefault(false.B)
  when(hit) {
    predictTaken := hitPredictor(1)  // MSB indicates taken
  }

  // Update operation
  when(io.update) {
    // Find if the updatePC already exists in the BTB
    val updateHit = WireDefault(false.B)
    val updateHitWay = WireDefault(0.U(log2Ceil(NUM_WAYS).W))

    for (way <- 0 until NUM_WAYS) {
      when(btb(updateIndex)(way).valid && btb(updateIndex)(way).tag === updateTag) {
        updateHit := true.B
        updateHitWay := way.U
      }
    }

    when(updateHit) {
      // Update existing entry
      val way = updateHitWay
      // Update target
      btb(updateIndex)(way).target := io.updateTarget

      // Update 2-bit predictor based on actual branch outcome
      when(io.branchTaken) {
        // Branch was taken: increment predictor (saturating at 11)
        when(btb(updateIndex)(way).predictor =/= "b11".U(2.W)) {
          btb(updateIndex)(way).predictor := btb(updateIndex)(way).predictor + 1.U
        }
      }.otherwise {
        // Branch was not taken: decrement predictor (saturating at 00)
        when(btb(updateIndex)(way).predictor =/= "b00".U(2.W)) {
          btb(updateIndex)(way).predictor := btb(updateIndex)(way).predictor - 1.U
        }
      }

      // Update LRU: mark this way as most recently used
      when(way === 0.U) {
        lru(updateIndex) := 0.U
      }.otherwise {
        lru(updateIndex) := 1.U
      }

    }.otherwise {
      // Not found: allocate new entry using LRU replacement
      val replaceWay = lru(updateIndex)  // LRU way gets replaced

      // Initialize the new entry
      btb(updateIndex)(replaceWay).valid := true.B
      btb(updateIndex)(replaceWay).tag := updateTag
      btb(updateIndex)(replaceWay).target := io.updateTarget

      // Initialize predictor: start in Weakly Taken state
      when(io.branchTaken) {
        btb(updateIndex)(replaceWay).predictor := "b11".U(2.W)  // Strongly Taken
      }.otherwise {
        btb(updateIndex)(replaceWay).predictor := "b10".U(2.W)  // Weakly Taken
      }

      // Update LRU: mark the newly allocated way as most recently used
      when(replaceWay === 0.U) {
        lru(updateIndex) := 0.U
      }.otherwise {
        lru(updateIndex) := 1.U
      }
    }
  }

  // Read-only: Also update LRU on a hit
  when(hit && !io.update) {
    when(hitWay === 0.U) {
      lru(index) := 0.U
    }.otherwise {
      lru(index) := 1.U
    }
  }

  // Outputs
  io.valid := hit
  io.target := hitTarget
  io.predictTaken := predictTaken
}