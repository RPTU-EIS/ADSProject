// ADS I Class Project
// Assignment 02: Arithmetic Logic Unit and UVM Testbench
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 09/21/2025 by Tharindu Samarakoon (gug75kex@rptu.de)
// File updated on 10/31/2025 by Tobias Jauch (tobias.jauch@rptu.de)

/* This file defines one ALU transaction for the UVM testbench.
  One transaction means: two operands, one operation, and one result.
  UVM will randomize the inputs and send them to the ALU.*/

`include "uvm_macros.svh"      // Include UVM macros like `uvm_object_utils_begin
import uvm_pkg::*;             // Import the UVM library
import alu_tb_config_pkg::*;   // Import DATA_WIDTH and the ALUOp enum

class alu_seq_item extends uvm_sequence_item;

  /*These are the values used in one ALU test. operandA, operandB, and operation are random because UVM will generate them.
    aluResult is not random because it stores the expected/check result. */

  rand bit [DATA_WIDTH-1:0] operandA;  // First random ALU input
  rand bit [DATA_WIDTH-1:0] operandB;  // Second random ALU input
  rand ALUOp operation;                // Random ALU operation
  bit [DATA_WIDTH-1:0] aluResult;      // Result value used for checking

  /*These macros register this item with UVM. They also tell UVM which fields belong to the transaction.
    This helps UVM print, copy, and compare the item automatically. */

  `uvm_object_utils_begin(alu_seq_item)
    `uvm_field_int(operandA, UVM_DEFAULT)                 // Register operandA
    `uvm_field_int(operandB, UVM_DEFAULT)                 // Register operandB
    `uvm_field_enum(ALUOp, operation, UVM_DEFAULT)        // Register operation as enum
    `uvm_field_int(aluResult, UVM_DEFAULT)                // Register aluResult
  `uvm_object_utils_end

  /* This constraint makes sure the random operation is valid. Without this, UVM could try an operation that the ALU does not support.*/
  constraint aluOp_constraint {
    operation inside {ADD, SUB, AND, OR, XOR, SLL, SRL, SRA, SLT, SLTU, PASSB};
  }

  /* This function prints the transaction in a readable way. It helps when checking the simulation log because we can see the operands,
the operation, and the result in one line.*/

  virtual function string convert2str();
    return $sformatf("operandA: 0x%08h, operandB: 0x%08h, operation: %s, aluResult: 0x%08h",
                     operandA, operandB, operation.name(), aluResult);
  endfunction

  /* This constructor creates the sequence item.super.new(name) connects it correctly with the UVM base class.*/

  function new(string name = "alu_seq_item");
    super.new(name);
  endfunction

endclass
