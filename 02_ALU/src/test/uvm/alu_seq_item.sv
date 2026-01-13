// ADS I Class Project
// Assignment 02: Arithmetic Logic Unit and UVM Testbench
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 09/21/2025 by Tharindu Samarakoon (gug75kex@rptu.de)
// File updated on 10/31/2025 by Tobias Jauch (tobias.jauch@rptu.de)

`include "uvm_macros.svh"
import uvm_pkg::*;
import alu_tb_config_pkg::*;

class alu_seq_item extends uvm_sequence_item;

    rand logic [31:0] operandA;
    rand logic [31:0] operandB;
    rand ALUOp operation;

    logic [31:0] aluResult;

     `uvm_object_utils(alu_seq_item)

     function new(string name = "alu_seq_item");
        super.new(name);
     endfunction

    constraint aluOp_constraint {
        operation inside {
            ADD, SUB, AND, OR, XOR, SLL, SRL, SRA, SLT, SLTU, PASSB
        };
    }

    virtual function string convert2str();
        return $sformatf(
            "operandA: 0x%0x, operandB: 0x%0x, operation: %0p, aluResult: 0x%0x",
            operandA,
            operandB,
            operation,
            aluResult
        );
    endfunction

    function new(string name = "alu_seq_item");
        super.new(name);
    endfunction

endclass
