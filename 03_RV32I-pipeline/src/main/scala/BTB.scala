// ADS I Class Project
// Pipelined RISC-V Core - Branch Target Buffer (BTB)
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// Assignment 04, Task 4.3
//
// 2-way set-associative BTB with 8 sets
// Each entry: valid bit, tag, target address, 2-bit saturating counter
// LRU replacement policy
// Only used for conditional branch instructions (B-type)

package core_tile

import chisel3._
import chisel3.util._

class BTB extends Module {
  val io = IO(new Bundle {
    // Lookup (from IF stage)
    val pc           = Input(UInt(32.W))

    // Prediction outputs
    val valid        = Output(Bool())        // BTB has a valid entry for this PC
    val target       = Output(UInt(32.W))    // Predicted branch target
    val predictTaken = Output(Bool())        // 2-bit FSM predicts taken

    // Update (from EX stage)
    val update       = Input(Bool())         // Update BTB with new info
    val updatePC     = Input(UInt(32.W))     // PC of the branch being updated
    val updateTarget = Input(UInt(32.W))     // Computed branch target
    val mispredicted = Input(Bool())         // Whether prediction was wrong
  })

  // ===========================================================================
  // Constants
  // ===========================================================================
  val numSets = 8
  val numWays = 2

  // 2-bit predictor states
  val strongNotTaken = 0.U(2.W)
  val weakNotTaken   = 1.U(2.W)
  val weakTaken      = 2.U(2.W)
  val strongTaken    = 3.U(2.W)

  // ===========================================================================
  // Storage: 8 sets × 2 ways
  // Using Reg/Vec (not Mem) since we need multi-port synchronous read/write
  // ===========================================================================
  val validArray     = RegInit(VecInit(Seq.fill(numSets)(VecInit(Seq.fill(numWays)(false.B)))))
  val tagArray       = Reg(Vec(numSets, Vec(numWays, UInt(27.W))))
  val targetArray    = Reg(Vec(numSets, Vec(numWays, UInt(32.W))))
  val predictorArray = RegInit(VecInit(Seq.fill(numSets)(VecInit(Seq.fill(numWays)(weakTaken)))))

  // LRU bit per set: 0 = evict way 0, 1 = evict way 1
  val lru = RegInit(VecInit(Seq.fill(numSets)(0.U(1.W))))

  // ===========================================================================
  // Address decomposition
  // PC[1:0] = 00 (RV32I alignment)
  // PC[4:2] = set index (3 bits → 8 sets)
  // PC[31:5] = tag (27 bits)
  // ===========================================================================
  def getIndex(pc: UInt): UInt = pc(4, 2)
  def getTag(pc: UInt):   UInt = pc(31, 5)

  // ===========================================================================
  // LOOKUP — combinational read (IF stage)
  // ===========================================================================
  val lookupIdx = getIndex(io.pc)
  val lookupTag = getTag(io.pc)

  val hit0 = validArray(lookupIdx)(0.U) && (tagArray(lookupIdx)(0.U) === lookupTag)
  val hit1 = validArray(lookupIdx)(1.U) && (tagArray(lookupIdx)(1.U) === lookupTag)

  // Default outputs: no prediction
  io.valid        := false.B
  io.target       := 0.U
  io.predictTaken := false.B

  when(hit0) {
    io.valid        := true.B
    io.target       := targetArray(lookupIdx)(0.U)
    io.predictTaken := predictorArray(lookupIdx)(0.U)(1)   // bit[1]=1 → taken
  }.elsewhen(hit1) {
    io.valid        := true.B
    io.target       := targetArray(lookupIdx)(1.U)
    io.predictTaken := predictorArray(lookupIdx)(1.U)(1)
  }

  // ===========================================================================
  // UPDATE — synchronous write (from EX stage)
  // ===========================================================================
  when(io.update) {
    val updIdx = getIndex(io.updatePC)
    val updTag = getTag(io.updatePC)

    val match0 = validArray(updIdx)(0.U) && (tagArray(updIdx)(0.U) === updTag)
    val match1 = validArray(updIdx)(1.U) && (tagArray(updIdx)(1.U) === updTag)

    when(match0) {
      // -----------------------------------------------------------
      // HIT way 0: update target and predictor FSM
      // -----------------------------------------------------------
      targetArray(updIdx)(0.U) := io.updateTarget

      val pred = predictorArray(updIdx)(0.U)
      val predTaken = pred(1)                               // current prediction
      // actuallyTaken = predictedTaken XOR mispredicted
      val actuallyTaken = predTaken ^ io.mispredicted
      when(actuallyTaken) {
        // Branch was taken → move counter up (toward strongTaken)
        when(pred < strongTaken) { predictorArray(updIdx)(0.U) := pred + 1.U }
      }.otherwise {
        // Branch was not taken → move counter down (toward strongNotTaken)
        when(pred > strongNotTaken) { predictorArray(updIdx)(0.U) := pred - 1.U }
      }

      // Update LRU: way 0 just used → evict way 1 next
      lru(updIdx) := 1.U

    }.elsewhen(match1) {
      // -----------------------------------------------------------
      // HIT way 1: update target and predictor FSM
      // -----------------------------------------------------------
      targetArray(updIdx)(1.U) := io.updateTarget

      val pred = predictorArray(updIdx)(1.U)
      val predTaken = pred(1)
      val actuallyTaken = predTaken ^ io.mispredicted
      when(actuallyTaken) {
        when(pred < strongTaken) { predictorArray(updIdx)(1.U) := pred + 1.U }
      }.otherwise {
        when(pred > strongNotTaken) { predictorArray(updIdx)(1.U) := pred - 1.U }
      }

      lru(updIdx) := 0.U

    }.otherwise {
      // -----------------------------------------------------------
      // MISS: allocate new entry using LRU replacement
      // -----------------------------------------------------------
      val way = lru(updIdx)
      validArray(updIdx)(way)     := true.B
      tagArray(updIdx)(way)       := updTag
      targetArray(updIdx)(way)    := io.updateTarget

      // Initialize predictor based on actual outcome:
      //   BTB miss → implicit prediction was "not taken"
      //   mispredicted=true → branch was actually taken → init to weakTaken
      //   mispredicted=false → branch was not taken → init to weakNotTaken
      when(io.mispredicted) {
        predictorArray(updIdx)(way) := weakTaken
      }.otherwise {
        predictorArray(updIdx)(way) := weakNotTaken
      }

      // Flip LRU: just allocated into 'way', so the other way becomes LRU
      lru(updIdx) := ~way
    }
  }
}
