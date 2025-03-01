package btb_pkg

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum
import os.stat

object predictorStates extends ChiselEnum {
    val strongNotTaken, weakNotTaken, strongTaken, weakTaken = Value
}

import predictorStates._

class TwoBitPredictor extends Module {
    val io = IO(new Bundle {
        val update = Input(UInt(1.W))
        val mispredicted = Input(UInt(1.W))
        val reset_state = Input(UInt(1.W))
        val prediction = Output(UInt(1.W)) // 0: branch not taken, 1: branch taken
    })

    val stateReg = RegInit(predictorStates.strongNotTaken)

    stateReg := stateReg // default case
    when (io.reset_state === 1.U){
        stateReg := predictorStates.strongNotTaken
    }
    .otherwise{
        switch (stateReg){
            is (predictorStates.strongNotTaken){
                when((io.update === 1.U) && (io.mispredicted === 1.U)){
                    stateReg := predictorStates.weakNotTaken
                }
            }
            is (predictorStates.weakNotTaken){
                when((io.update === 1.U) && (io.mispredicted === 1.U)){
                    stateReg := predictorStates.strongTaken
                }
                .elsewhen((io.update === 1.U) && (io.mispredicted === 0.U)){
                    stateReg := predictorStates.strongNotTaken
                }
            }
            is (predictorStates.strongTaken){
                when((io.update === 1.U) && (io.mispredicted === 1.U)){
                    stateReg := predictorStates.weakTaken
                }
            }
            is (predictorStates.weakTaken){
                when((io.update === 1.U) && (io.mispredicted === 1.U)){
                    stateReg := predictorStates.strongNotTaken
                }
                .elsewhen((io.update === 1.U) && (io.mispredicted === 0.U)){
                    stateReg := predictorStates.strongTaken
                }
            }
        }
        
        // assertions
        assert(((stateReg === RegNext(stateReg)) || (io.update =/= 1.U)), "io.update == 0.U => state should not be changed.")
        assert(((io.reset_state =/= 1.U) || (RegNext(stateReg) === predictorStates.strongNotTaken)), "io.reset_state => NextState = strongNotTaken.")
    }

    // set prediction based on state
    when((stateReg === predictorStates.strongNotTaken) || (stateReg === predictorStates.weakNotTaken)) {io.prediction := 0.U}
    .otherwise {io.prediction := 1.U} //((stateReg === predictorStates.strongTaken) || (stateReg === predictorStates.weakTaken))

}

// BTB is a 2-way set associative cache.
// For the implementation we can consider each way as a separate cache. 
// Each cache line has these fields. [valid_bit | tag | target | prediction]
// Infact we can consider each of these fields as separate small caches. We use same index to access (read / write) each of these fields simultaneously 

class BTB_way (N: Int) extends Module { // N: number of sets
    val io = IO(new Bundle{
        val PC = Input(UInt(32.W))
        val update = Input(UInt(1.W))
        val updatePC = Input(UInt(32.W))
        val updateTarget = Input(UInt(32.W))
        val mispredicted = Input(UInt(1.W))

        val valid = Output(UInt(1.W))
        val tag = Output(UInt(27.W))
        val target = Output(UInt(32.W))
        val predictedTaken = Output(UInt(1.W))
    })

    val validMem   = RegInit(VecInit(Seq.fill(N)(0.U(1.W))))
    val tagMem     = RegInit(VecInit(Seq.fill(N)(0.U(27.W)))) // PC[31:5]
    val targetMem  = RegInit(VecInit(Seq.fill(N)(0.U(32.W))))
    val predictors = Vec(N, Module(new TwoBitPredictor).io)

    // outputs
    io.valid := validMem(io.PC(4,2))
    io.tag := tagMem((io.PC(4,2)))
    io.target := targetMem((io.PC(4,2)))
    io.predictedTaken := predictors(io.PC(4,2)).prediction

    // update memories
    when(io.update === 1.U){
        validMem(io.PC(4,2)) := 1.U
        tagMem(io.PC(4,2)) := io.updatePC(31,5)
        targetMem(io.PC(4,2)) := io.updateTarget
    }

    // update predictor
    predictors(io.PC(4,2)).update := io.update
    predictors(io.PC(4,2)).mispredicted := io.mispredicted
    predictors(io.PC(4,2)).reset_state := io.update
}

class TwoWayLRU (SETS: Int) extends  Module{
    val io = IO(new Bundle{
        val set = Input(UInt(log2Ceil(SETS).W))
        val readHitWay = Input(UInt(2.W)) // which way was read hit
        val LRU_way = Output(UInt(1.W)) //0: way0, 1: way1
    })

    // resets to 0 => if both are ways are available, write to way0
    val LRUMem   = RegInit(VecInit(Seq.fill(SETS)(0.U(1.W))))

    io.LRU_way := LRUMem(io.set)

    switch(io.readHitWay){
        is(1.U){ // read way0
            LRUMem(io.set) := 1.U // LRU way = way1
        }
        is(2.U){ // read way1
            LRUMem(io.set) := 0.U // LRU way = way0
        }
    }
}

class TwoWayBTB (SETS: Int) extends Module{
    val io = IO(new Bundle{
        val PC = Input(UInt(32.W))
        val update = Input(UInt(1.W))
        val updatePC = Input(UInt(32.W))
        val updateTarget = Input(UInt(32.W))
        val mispredicted = Input(UInt(1.W))

        val valid = Output(UInt(1.W))
        val target = Output(UInt(32.W))
        val predictedTaken = Output(UInt(1.W))
    })

    val NWAYS = 2

    val w_mispredicted = Wire(UInt(NWAYS.W))
    val w_update = Wire(UInt(NWAYS.W))
    val w_valid = Wire(UInt(NWAYS.W))
    val w_tag = (0 until NWAYS).map(x => Wire(UInt(27.W)))
    val w_target = (0 until NWAYS).map(x => Wire(UInt(32.W)))
    val w_predictedTaken = Wire(UInt(NWAYS.W))

    val readHit = Wire(UInt(NWAYS.W))
    val writeWay = Wire(UInt(1.W))

    //instantiate ways
    val ways = (0 until NWAYS).map(x => Module(new BTB_way(N = SETS))).toList
    for (i <- 0 until NWAYS){
        ways(i).io.PC := io.PC
        ways(i).io.update := w_update(i)
        ways(i).io.updatePC := io.updatePC
        ways(i).io.updateTarget := io.updateTarget
        ways(i).io.mispredicted := w_mispredicted(i)

        w_valid(i) := ways(i).io.valid
        w_tag(i) := ways(i).io.tag
        w_target(i) := ways(i).io.target
        w_predictedTaken(i) := ways(i).io.predictedTaken
    }

    // instantiate LRU unit
    val TwoWayLRU_inst = Module(new TwoWayLRU(SETS = SETS))
    TwoWayLRU_inst.io.set := io.PC(4,2)
    TwoWayLRU_inst.io.readHitWay := readHit
    writeWay := TwoWayLRU_inst.io.LRU_way

    for (i <- 0 until NWAYS){
        // value should be updated in following cases. (should be updated when the instruction in the EX stage)
        // 1. after 2 cycles from read miss. (no info)
        // 2. after 2 cycles from read hit. (wrong prediction)
        w_update(i) := io.update & (i.U === writeWay)
        readHit(i) := ways(i).io.valid & (w_tag(i) === io.PC(31,5))
    }

    io.valid := w_valid.orR
    io.target := Mux((readHit(0)===1.U), w_target(0), Mux((readHit(1)===1.U), w_target(1), 0.U))
    io.predictedTaken := Mux((readHit(0)===1.U), w_predictedTaken(0), Mux((readHit(1)===1.U), w_predictedTaken(1), 0.U))
    
}