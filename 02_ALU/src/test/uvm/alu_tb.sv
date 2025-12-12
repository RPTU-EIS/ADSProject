// ADS I Class Project
// Assignment 02: Arithmetic Logic Unit and UVM Testbench
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 09/21/2025 by Tharindu Samarakoon (gug75kex@rptu.de)
// File updated on 10/31/2025 by Tobias Jauch (tobias.jauch@rptu.de)

`include "uvm_macros.svh"
import uvm_pkg::*;
import alu_tb_config_pkg::*;

module alu_tb();

timeunit 1ns;
timeprecision 1ns;

logic clk;
logic rst = 1'b0;

initial begin
    clk = 0;
    forever begin
        #(CLK_PERIOD/2);
        clk = ~clk;
    end
end

alu_if alu_if(clk);

ALU dut(
    // .clock(clk),
    // .reset(rst),
    .io_operation(alu_if.operation),
    .io_operandA(alu_if.operandA),
    .io_operandB(alu_if.operandB),
    .io_aluResult(alu_if.aluResult)
);

initial begin
    uvm_config_db #(virtual alu_if)::set(null, "uvm_test_top", "alu_if", alu_if);
    run_test("alu_test");
end

// dump waveform
initial begin
    $dumpfile("alu_dump.vcd");
    $dumpvars;
end

endmodule
