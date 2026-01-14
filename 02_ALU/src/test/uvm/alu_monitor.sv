// ADS I Class Project
// Assignment 02: Arithmetic Logic Unit and UVM Testbench
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 09/21/2025 by Tharindu Samarakoon (gug75kex@rptu.de)
// File updated on 10/31/2025 by Tobias Jauch (tobias.jauch@rptu.de)

`include "uvm_macros.svh"
import uvm_pkg::*;
import alu_tb_config_pkg::*;

class alu_monitor extends uvm_monitor;
    `uvm_component_utils(alu_monitor)

    function new(string name="alu_monitor", uvm_component parent=null);
        super.new(name, parent);
    endfunction

    uvm_analysis_port #(alu_seq_item) mon_analysis_port;
    virtual alu_if vif;

    virtual function void build_phase(uvm_phase phase);
        super.build_phase(phase);
        if(!uvm_config_db#(virtual alu_if)::get(this, "", "alu_if", vif))begin
            `uvm_fatal(get_type_name(), "Could not get vif")
        end
        mon_analysis_port = new("mon_analysis_port", this);
    endfunction

    virtual task run_phase(uvm_phase phase);
        alu_seq_item item, item_clone;
        super.run_phase(phase);
        
        forever begin
            @(posedge vif.clk);
            item = new;
            item.operandA = vif.operandA;
            item.operandB = vif.operandB;
            item.operation    = vif.operation;
            item.aluResult= vif.aluResult;
            $cast(item_clone, item.clone());
            `uvm_info(get_type_name(), $sformatf("Monnitor read packet %0s", item_clone.convert2str()), UVM_DEBUG)

            mon_analysis_port.write(item_clone);
        end

    endtask

endclass
