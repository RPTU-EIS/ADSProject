// ADS I Class Project
// Assignment 02: Arithmetic Logic Unit and UVM Testbench
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 09/21/2025 by Tharindu Samarakoon (gug75kex@rptu.de)
// File updated on 10/31/2025 by Tobias Jauch (tobias.jauch@rptu.de)

`include "uvm_macros.svh"
import uvm_pkg::*;
import alu_tb_config_pkg::*;

class alu_scoreboard extends uvm_scoreboard;
    `uvm_component_utils(alu_scoreboard)

    function new(string name = "alu_scoreboard", uvm_component parent = null);
        super.new(name, parent);
    endfunction

    uvm_analysis_imp #(alu_seq_item, alu_scoreboard) m_analysis_imp;
    int unsigned pass_count;
    int unsigned fail_count;

    virtual function void build_phase(uvm_phase phase);
        super.build_phase(phase);
        m_analysis_imp = new("m_analysis_imp", this);
        pass_count = 0;
        fail_count = 0;
    endfunction

    virtual function void write(alu_seq_item item);
        ALUOp operation;
        bit signed [DATA_WIDTH-1:0]operandA, operandB, aluResult;
        operation = item.operation;
        operandA = item.operandA;
        operandB = item.operandB;

        case (operation)
            ADD  : aluResult = operandA + operandB;
            SUB  : aluResult = operandA - operandB;
            XOR  : aluResult = operandA ^ operandB;
            OR   : aluResult = operandA | operandB;
            AND  : aluResult = operandA & operandB;
            SLL  : aluResult = operandA << operandB[4:0];
            SRL  : aluResult = operandA >> operandB[4:0];
            SRA  : aluResult = operandA >>> operandB[4:0];
            SLT  : aluResult = (operandA < operandB)? 1'b1 : 1'b0;
            SLTU : aluResult = ($unsigned(operandA) < $unsigned(operandB))? 1'b1 : 1'b0;
            PASSB: aluResult = operandB;
            default: aluResult = 32'h0000_0000;
        endcase
        if(aluResult != item.aluResult) begin
            fail_count++;
            `uvm_error(get_type_name(), $sformatf("DUT result: 0x%0x, expected result: 0x%0x, operation: %0p, opA: 0x%0x, opB: 0x%0x", item.aluResult, aluResult, operation, operandA, operandB))
        end
        else begin
            pass_count++;
            `uvm_info(get_type_name(), $sformatf("Pass: DUT result: 0x%0x, expected result: 0x%0x, operation: %0p, opA: 0x%0x, opB: 0x%0x", item.aluResult, aluResult, operation, operandA, operandB), UVM_LOW)
        end
    endfunction

    virtual function void report_phase(uvm_phase phase);
        int unsigned total_count;
        real pass_rate;
        string msg;

        super.report_phase(phase);

        total_count = pass_count + fail_count;
        pass_rate = (total_count == 0) ? 0.0 : (pass_count * 100.0) / total_count;
        msg = $sformatf("Scoreboard summary: Passes: %0d, Fails: %0d, Total: %0d, Pass rate: %0.2f %%", pass_count, fail_count, total_count, pass_rate);

        uvm_report_info("SCOREBOARD_SUMMARY", msg, UVM_MEDIUM);

        if (fail_count != 0) begin
            uvm_report_fatal("SCOREBOARD_FAIL", $sformatf("Total failures detected by scoreboard: %0d", fail_count));
        end
    endfunction

endclass
