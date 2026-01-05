// ADS I Class Project
// Assignment 02: Arithmetic Logic Unit and UVM Testbench
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 09/21/2025 by Tharindu Samarakoon (gug75kex@rptu.de)
// File updated on 11/10/2025 by Tobias Jauch (tobias.jauch@rptu.de)

`include "uvm_macros.svh"
import uvm_pkg::*;
import alu_tb_config_pkg::*;

class alu_agent extends uvm_agent;
    `uvm_component_utils(alu_agent)

    function new(string name = "alu_agent", uvm_component parent = null);
        super.new(name, parent);
    endfunction

    alu_driver driver;
    alu_monitor monitor;
    uvm_sequencer #(alu_seq_item) sequencer;

    virtual function void build_phase(uvm_phase phase);
        super.build_phase(phase);
        driver = alu_driver::type_id::create("driver", this);
        monitor = alu_monitor::type_id::create("monitor", this);
        sequencer = uvm_sequencer#(alu_seq_item)::type_id::create("sequencer", this);
    endfunction

    virtual function void connect_phase(uvm_phase phase);
        super.connect_phase(phase);
        driver.seq_item_port.connect(sequencer.seq_item_export);
    endfunction

endclass
