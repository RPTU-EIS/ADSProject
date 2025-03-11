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
        // Here I define mispredicted signal when reset a way with a new address as below
        // 1. mispredicted == 0 : branch taken = 1
        // 2. mispredicted == 1 : branch taken = 0
        when(io.mispredicted === 1.U){
            stateReg := predictorStates.strongTaken
        }
        .otherwise{
            stateReg := predictorStates.strongNotTaken
        }
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
        assert((!((io.reset_state === 1.U) && (io.mispredicted === 0.U)) || (RegNext(stateReg) === predictorStates.strongNotTaken)), "(io.reset_state && ~io.mispredicted) => NextState = strongNotTaken.")
        assert((!((io.reset_state === 1.U) && (io.mispredicted === 1.U)) || (RegNext(stateReg) === predictorStates.strongTaken)), "(io.reset_state && io.mispredicted) => NextState = strongTaken.")
    }

    // set prediction based on state
    when((stateReg === predictorStates.strongNotTaken) || (stateReg === predictorStates.weakNotTaken)) {io.prediction := 0.U}
    .otherwise {io.prediction := 1.U} //((stateReg === predictorStates.strongTaken) || (stateReg === predictorStates.weakTaken))

}

// BTB is a 2-way set associative cache.
// For the implementation we can consider each way as a separate cache. 
// Each cache line has these fields. [valid_bit | tag | target | prediction]
// Infact we can consider each of these fields as separate small caches. We use same index to access (read / write) each of these fields simultaneously 

class BTB_way (NSETS: Int) extends Module { // NSETS: number of sets
    val BYTE_OFFSET = 2
    val INDEX_WIDTH = log2Ceil(NSETS)
    val TAG_WIDTH = 32 - INDEX_WIDTH - BYTE_OFFSET // ADDR_WIDTH - INDEX_WIDTH - BYTE_OFFSET

    val io = IO(new Bundle{
        val PC = Input(UInt(32.W))
        val update = Input(UInt(1.W))
        val updatePC = Input(UInt(32.W))
        val updateTarget = Input(UInt(32.W))
        val mispredicted = Input(UInt(1.W))

        val valid = Output(UInt(1.W))
        val tag = Output(UInt(TAG_WIDTH.W))
        val target = Output(UInt(32.W))
        val predictedTaken = Output(UInt(1.W))
        val updatePC_available = Output(UInt(1.W)) // used to decide which way will be updated
    })

    val validMem   = RegInit(VecInit(Seq.fill(NSETS)(0.U(1.W))))
    val tagMem     = RegInit(VecInit(Seq.fill(NSETS)(0.U(TAG_WIDTH.W)))) // PC[31:5]
    val targetMem  = RegInit(VecInit(Seq.fill(NSETS)(0.U(32.W))))
    val predictors = (0 until NSETS).map(x => Module(new TwoBitPredictor)).toList

    val read_index = Wire(UInt(INDEX_WIDTH.W))
    val write_index = Wire(UInt(INDEX_WIDTH.W))
    val predictedTaken_vec = Wire(Vec(NSETS, UInt(1.W)))

    read_index := io.PC(INDEX_WIDTH+BYTE_OFFSET-1,BYTE_OFFSET)
    write_index := io.updatePC(INDEX_WIDTH+BYTE_OFFSET-1,BYTE_OFFSET)

    // outputs
    io.valid := validMem(read_index)
    io.tag := tagMem(read_index)
    io.target := targetMem(read_index)
    io.predictedTaken := predictedTaken_vec(read_index)

    val updatePC_available_wire = Wire(UInt(1.W))
    updatePC_available_wire := validMem(write_index) & (io.updatePC(31,32-TAG_WIDTH) === tagMem(write_index))
    io.updatePC_available := updatePC_available_wire

    // update memories
    when(io.update === 1.U){
        validMem(write_index) := 1.U
        tagMem(write_index) := io.updatePC(31,32-TAG_WIDTH)
        targetMem(write_index) := io.updateTarget
    }

    // update predictor
    for (i <- 0 until NSETS){
        predictors(i).io.update := (io.update === 1.U) & (write_index === i.U)
        predictors(i).io.mispredicted := (io.mispredicted === 1.U) & (write_index === i.U)
        predictors(i).io.reset_state := (io.update === 1.U) & (write_index === i.U) & ~updatePC_available_wire
        predictedTaken_vec(i) := predictors(i).io.prediction
    }

}

class TwoWayLRU (NSETS: Int) extends  Module{
    val io = IO(new Bundle{
        val readSet = Input(UInt(log2Ceil(NSETS).W)) // used to update LRU when read hit
        val readHitWay = Input(UInt(2.W)) // which way was read hit
        val writeSet = Input(UInt(log2Ceil(NSETS).W)) // used to decide which way is going to update
        val LRU_way = Output(UInt(1.W)) //The way should be replaced (when BTB miss) 0: way0, 1: way1
    })

    // resets to 0 => if both are ways are available, write to way0
    val LRUMem   = RegInit(VecInit(Seq.fill(NSETS)(0.U(1.W))))

    io.LRU_way := LRUMem(io.writeSet)

    switch(io.readHitWay){
        is(1.U){ // read way0
            LRUMem(io.readSet) := 1.U // LRU way = way1
        }
        is(2.U){ // read way1
            LRUMem(io.readSet) := 0.U // LRU way = way0
        }
    }
}

class TwoWayBTB (NSETS: Int) extends Module{
    val NWAYS = 2
    val BYTE_OFFSET = 2
    val INDEX_WIDTH = log2Ceil(NSETS)
    val TAG_WIDTH = 32 - INDEX_WIDTH - BYTE_OFFSET // ADDR_WIDTH - INDEX_WIDTH - BYTE_OFFSET
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

    val w_update = Wire(Vec(NWAYS, UInt(1.W)))
    val w_updatePC_available = Wire(Vec(NWAYS, UInt(1.W)))
    val w_valid = Wire(Vec(NWAYS, UInt(1.W)))
    val w_tag = Wire(Vec(NWAYS, UInt(TAG_WIDTH.W)))
    val w_target = Wire(Vec(NWAYS, UInt(32.W)))
    val w_predictedTaken = Wire(Vec(NWAYS, UInt(1.W)))

    val readHit = Wire(Vec(NWAYS, UInt(1.W)))
    val writeWay = Wire(UInt(1.W))

    //instantiate ways
    val ways = (0 until NWAYS).map(x => Module(new BTB_way(NSETS = NSETS))).toList
    for (i <- 0 until NWAYS){
        ways(i).io.PC := io.PC
        ways(i).io.update := w_update(i)
        ways(i).io.updatePC := io.updatePC
        ways(i).io.updateTarget := io.updateTarget
        ways(i).io.mispredicted := io.mispredicted

        w_valid(i) := ways(i).io.valid
        w_tag(i) := ways(i).io.tag
        w_target(i) := ways(i).io.target
        w_predictedTaken(i) := ways(i).io.predictedTaken
        w_updatePC_available(i) := ways(i).io.updatePC_available
    }

    // instantiate LRU unit
    val TwoWayLRU_inst = Module(new TwoWayLRU(NSETS = NSETS))
    TwoWayLRU_inst.io.writeSet := io.updatePC(INDEX_WIDTH+BYTE_OFFSET-1,BYTE_OFFSET)
    TwoWayLRU_inst.io.readSet := io.PC(INDEX_WIDTH+BYTE_OFFSET-1,BYTE_OFFSET)
    TwoWayLRU_inst.io.readHitWay := readHit.asUInt
    writeWay := TwoWayLRU_inst.io.LRU_way

    for (i <- 0 until NWAYS){
        // value should be updated in following cases. (should be updated when the instruction in the EX stage)
        // 1. after 2 cycles from read miss. (no info)
        // 2. after 2 cycles from read hit. (wrong prediction)
        w_update(i) := io.update & (w_updatePC_available(i) | ((i.U === writeWay) & !w_updatePC_available(~i.B))) // select the way if it already has the line or (LRU select it and other way does not have it)
        readHit(i) := ways(i).io.valid & (w_tag(i) === io.PC(31,BYTE_OFFSET+INDEX_WIDTH))
    }
    assert(!((readHit(0)===1.U) && (readHit(1) === 1.U)), f"Both ways have same address. PC: ${io.PC(31,BYTE_OFFSET+INDEX_WIDTH)}")

    io.valid := readHit.reduce(_ | _)
    io.target := Mux((readHit(0)===1.U), w_target(0), Mux((readHit(1)===1.U), w_target(1), 0.U))
    io.predictedTaken := Mux((readHit(0)===1.U), w_predictedTaken(0), Mux((readHit(1)===1.U), w_predictedTaken(1), 0.U))
    
}