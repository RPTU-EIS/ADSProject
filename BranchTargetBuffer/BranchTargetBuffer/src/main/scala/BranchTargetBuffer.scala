package BranchTargetBuffer


import chisel3._
import chisel3.util._

class BTBEntry extends Bundle {
  val valid = Bool()
  val tag = UInt(27.W)
  val target = UInt(32.W)
  val predictor = UInt(2.W) // 2-bit predictor FSM
}

class BranchTargetBuffer extends Module {
  
  val io = IO(new Bundle {
    val pc = Input(UInt(32.W))
    val update = Input(Bool())
    val updatePC = Input(UInt(32.W))
    val updateTarget = Input(UInt(32.W))
    val mispredicted = Input(Bool())
    val valid = Output(Bool())
    val target = Output(UInt(32.W))
    val predictedTaken = Output(Bool())
  })

  val numSets = 8
  val numWays = 2
  
  // BTB storage
  val btb = RegInit(VecInit(Seq.fill(numSets)(
    VecInit(Seq.fill(numWays)(0.U.asTypeOf(new BTBEntry))))))
  
  // LRU bits (1 bit per set)
  val lru = RegInit(VecInit(Seq.fill(numSets)(false.B)))
  
  // Index and tag extraction
  val index = io.pc(4, 2) // Extract bits [4:2] for indexing
  val tag = io.pc(31, 5)  // Extract remaining bits as tag

  // Lookup logic

  val wayHits = Wire(Vec(numWays, Bool())) // Create an output vector of Booleans
  // Loop through each way in the set
  val hit = Wire(Bool())
  val hitWay = Wire(UInt(1.W))
  hitWay := 0.U
  hit := false.B
for (i <- 0 until numWays) {
  when(btb(index)(i).valid && btb(index)(i).tag === tag) {
    wayHits(i) := true.B // no se usa si tampoco se usa el mux de wayhits
    hit := true.B
    hitWay := i.asUInt(1.W)
  }.otherwise {
    wayHits(i) := false.B // no se usa si tampoco se usa el mux de wayhits
  }
}


  //val hitWay = Mux(wayHits(0), 0.U, 1.U) usar si no funciona hitway en el for
  
  // Outputs
  io.valid := hit
  io.target := Mux(hit, btb(index)(hitWay).target, 0.U)
  io.predictedTaken := Mux(hit, btb(index)(hitWay).predictor(1), false.B) // MSB of 2-bit predictor

  when(io.update) {
    val updateIndex = io.updatePC(4, 2)
    val updateTag = io.updatePC(31, 5)
    val updateWay = Mux(lru(updateIndex), 1.U, 0.U)
    
    // Update BTB entry
    btb(updateIndex)(updateWay).valid := true.B
    btb(updateIndex)(updateWay).tag := updateTag
    btb(updateIndex)(updateWay).target := io.updateTarget
    btb(updateIndex)(updateWay).predictor := 2.U // Initialize predictor to weakTaken
    
    // Update LRU
    lru(updateIndex) := !updateWay
  }
/*
The 2-bit predictor is a simple saturating counter:

"00" → Strongly Not Taken
"01" → Weakly Not Taken
"10" → Weakly Taken
"11" → Strongly Taken
*/
  // Predictor FSM update
  when(hit) { // ** Is io.update necesary?
    val predState = btb(index)(hitWay).predictor
    when(io.mispredicted) {
      when(predState === 3.U) { btb(index)(hitWay).predictor := 2.U }
      .elsewhen(predState === 2.U) { btb(index)(hitWay).predictor := 0.U }
      .elsewhen(predState === 1.U) { btb(index)(hitWay).predictor := 3.U }
      .otherwise { btb(index)(hitWay).predictor := 1.U }
    }.otherwise {
      when(predState === 0.U) { btb(index)(hitWay).predictor := 0.U }
      .elsewhen(predState === 1.U) { btb(index)(hitWay).predictor := 0.U }
      .elsewhen(predState === 2.U) { btb(index)(hitWay).predictor := 3.U }
      .otherwise { btb(index)(hitWay).predictor := 3.U }
    }
  }
  printf(p"btb(index)(hitWay).predictor: ${btb(index)(hitWay).predictor}\n")
}
