// ADS I Class Project
// Pipelined RISC-V Core - Branch Target Buffer
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 05/12/2026 by Tobias Jauch (@tojauch)

package core_tile

import chisel3._
import chisel3.util._
import uopc._

// -----------------------------------------
// Branch Target Buffer
// -----------------------------------------

// THIS IS A WAY FOR THE 8 SETS TIMES 2 WAYS
class BTBway extends Bundle {
  val valid   = Bool()
  val tag     = UInt(27.W) // This is for PC[31:5]
  val target  = UInt(32.W)
  val counter = UInt(2.W)  // 00 Strongly Not Taken, 11 Strongly Taken
}

class BTB_ctrl extends Module {
  val io = IO(new Bundle {
    val currentState = Input(UInt(2.W))
    val taken        = Input(Bool())
    val nextState    = Output(UInt(2.W))
  }) 

  val SNT = "b00".U         // Strongly Not Taken
  val WNT = "b01".U         // Weakly Not Taken
  val WT  = "b10".U         // Weakly Taken
  val ST  = "b11".U         // Strongly Taken

  val ns = WireDefault(SNT)

  // Update based on actual branch outcome (`taken`).
  switch(io.currentState){
    is(SNT){
      when(io.taken){ ns := WNT }.otherwise{ ns := SNT }
    }
    is(WNT){
      when(io.taken){ ns := WT  }.otherwise{ ns := SNT }
    }
    is(WT){
      when(io.taken){ ns := ST  }.otherwise{ ns := WNT }
    }
    is(ST){
      when(io.taken){ ns := ST  }.otherwise{ ns := WT  }
    }
  }
  
  io.nextState := ns
}

class BTB extends Module {
  val io = IO(new Bundle {
    val PC            = Input(UInt(32.W))
    val update          = Input(Bool())
    val updatePC        = Input(UInt(32.W))
    val updateTarget    = Input(UInt(32.W))
    val mispredicted    = Input(Bool())

    val valid           = Output(Bool())
    val target          = Output(UInt(32.W))
    val predictTaken    = Output(Bool())
  })

  val numSets = 8
  val numWays = 2

  // 8 SETS WITH 2 WAYS REGISTERS
  val table = RegInit(
    VecInit(
      Seq.fill(numSets)(
        VecInit(
          Seq.fill(numWays)(0.U.asTypeOf(new BTBway)) // INITIALIZE THE TABLE ELEMENT WITH ZEROES
        )
      )
    )
  )

  // 1 LRU BIT TO POINT TO THE REWRITEABLE REGISTERS
  val lru   = RegInit(VecInit(Seq.fill(numSets)(0.U(1.W))))

  // COMBINATIONAL LOOKUP
  val lookupIdx = io.PC(4,2)
  val lookupTag = io.PC(31,5)

  val lWay0 = table(lookupIdx)(0)
  val lWay1 = table(lookupIdx)(1)
  val lHit0 = lWay0.valid && lWay0.tag === lookupTag
  val lHit1 = lWay1.valid && lWay1.tag === lookupTag
  val lHit  = lHit0 || lHit1
  val lEntry = Mux(lHit0, lWay0, lWay1)

  io.valid        := lHit
  io.target       := lEntry.target
  io.predictTaken := lHit && lEntry.counter(1)

  // UPDATE FROM EX STAGE
  val updIdx = io.updatePC(4, 2)
  val updTag = io.updatePC(31, 5)

  val uWay0 = table(updIdx)(0)
  val uWay1 = table(updIdx)(1)
  val uHit0 = uWay0.valid && uWay0.tag === updTag
  val uHit1 = uWay1.valid && uWay1.tag === updTag
  val uHit  = uHit0 || uHit1

  // Reconstruct actualTaken from the prediction made and whether it was wrong.
  // On a miss, implicit prediction was "not taken" (false).
  val oldPredictTaken = Mux(uHit, Mux(uHit0, uWay0.counter(1), uWay1.counter(1)), false.B)
  val actualTaken     = oldPredictTaken =/= io.mispredicted

  // If there's no hit, use a safe default counter (SNT)
  val oldCounter = Mux(uHit, Mux(uHit0, uWay0.counter, uWay1.counter), 0.U)

  // Instantiate the prediction state machine
  val btbCtrl = Module(new BTB_ctrl())
  btbCtrl.io.currentState := oldCounter
  btbCtrl.io.taken := actualTaken

  // Get the next state directly from the controller
  val newCounterOnHit = btbCtrl.io.nextState

  // THE NEW ALLOCATION STARTS IN WEAK BUT IN THE DIRECTION OF THE LAST OUTCOME
  val newCounterOnAlloc = Mux(actualTaken, 2.U, 1.U)

  val finalCounter = Mux(uHit, newCounterOnHit, newCounterOnAlloc)

  // IF HIT OVERWRITE THE MATCHING WAY. IF MISS REWRITE THE LRU IN THAT SET
  val writeWay = Mux(uHit, Mux(uHit0, 0.U, 1.U), lru(updIdx))

  when(io.update) {
    table(updIdx)(writeWay).valid   := true.B
    table(updIdx)(writeWay).tag     := updTag
    table(updIdx)(writeWay).target  := io.updateTarget
    table(updIdx)(writeWay).counter := finalCounter
    
    lru(updIdx) := ~writeWay  // The way just touched becomes MRU, the other becomes LRU
  }

  printf(" ================= BTB TABLE DUMP =================\n")
  for (s <- 0 until numSets) {
    for (w <- 0 until numWays) {
      val e = table(s)(w)
      printf(s"  set=$s way=$w  valid=%x  tag=%x  target=%x  counter=%x  predictTaken=%x\n",
        e.valid, e.tag, e.target, e.counter, e.counter(1))
    }
  }
  printf(p"  LRU bits per set: ${lru}\n")
  printf(" ====================================================\n")

}