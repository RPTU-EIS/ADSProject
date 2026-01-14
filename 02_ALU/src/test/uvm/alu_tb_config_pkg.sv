// ADS I Class Project
// Assignment 02: Arithmetic Logic Unit and UVM Testbench
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 09/21/2025 by Tharindu Samarakoon (gug75kex@rptu.de)
// File updated on 10/31/2025 by Tobias Jauch (tobias.jauch@rptu.de)

package alu_tb_config_pkg;

    localparam real CLK_FREQ = 100; //MHz
    localparam real CLK_PERIOD = 1000 / CLK_FREQ; // ns
 
    parameter int DATA_WIDTH = 32;
    
    typedef enum logic [7:0] { 
        ADD   = 8'h0,
        SUB   = 8'h1,
        AND   = 8'h2,
        OR    = 8'h3,
        XOR   = 8'h4,
        SLL   = 8'h5,
        SRL   = 8'h6,
        SRA   = 8'h7,
        SLT   = 8'h8,
        SLTU  = 8'h9,
        PASSB = 8'hA
     } ALUOp;

endpackage
