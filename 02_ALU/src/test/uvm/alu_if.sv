// ADS I Class Project
// Assignment 02: Arithmetic Logic Unit and UVM Testbench
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 09/21/2025 by Tharindu Samarakoon (gug75kex@rptu.de)
// File updated on 10/31/2025 by Tobias Jauch (tobias.jauch@rptu.de)

import alu_tb_config_pkg::*;

interface alu_if (input clk);
    logic [DATA_WIDTH-1:0] operandA;
    logic [DATA_WIDTH-1:0] operandB;
    ALUOp operation;
    logic [DATA_WIDTH-1:0] aluResult;

    clocking cb @(posedge clk);
        default input #3ns output #2ns;
        input operandA, operandB, operation;
        output aluResult;
    endclocking 
endinterface
