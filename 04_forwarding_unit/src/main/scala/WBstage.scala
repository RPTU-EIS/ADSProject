// ADS I Class Project
// Pipelined RISC-V Core - WB Stage
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)


/*
Writeback (WB) Stage: result storage and register file updates

Register File Interface:
    regFileReq: write request bundle
        regFileReq.addr: destination register index
        regFileReq.data: result value to write
        regFileReq.wr_en: write enable signal

Inputs:
    aluResult: computation result from pipeline
    rd: destination register address

Internal Signals:
    Result forwarding paths
    Write enable control

Functionality:
    Forward aluResult to register file write port
    Set write address to rd
    Assert wr_en = true for all R-type and I-type instructions
    Output result on check_res for verification and debugging

Outputs:
    check_res: result value for verification
*/

package core_tile

import chisel3._

// -----------------------------------------
// Writeback Stage
// -----------------------------------------

//ToDo: Add your implementation according to the specification above here 

class WBStage extends Module {
    val io = IO(new Bundle {
        // Inputs from MEMbarrier
        val inAluResult   = Input(UInt(32.W))
        val inRD          = Input(UInt(5.W))
        val inXcptInvalid = Input(Bool())


        // Register File Interface Output 
        // (Uses the regFileWriteReq Bundle defined in register file source)
        val regFileReq = Output(new regFileWriteReq)



        //outputs to the WB-barrier for external observation
        val check_res      = Output(UInt(32.W))
        val outXcptInvalid = Output(Bool())
    })

    //Result forwarding
    //the alu result is passed directly to the write data port of the register file

    io.regFileReq.data := io.inAluResult

    //Write Enable Control
    io.regFileReq.wr_en := !io.inXcptInvalid && (io.inRD =/= 0.U)   //every valid instruction that reaches WB and has a non‑zero destination register will write to the register file

    // see it again 
    io.regFileReq.addr := io.inRD

    //Verificaton Outputs
    io.check_res      := io.inAluResult
    io.outXcptInvalid := io.inXcptInvalid
}