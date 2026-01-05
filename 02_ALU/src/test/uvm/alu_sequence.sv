// ADS I Class Project
// Assignment 02: Arithmetic Logic Unit and UVM Testbench
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 09/22/2025 by Tharindu Samarakoon (gug75kex@rptu.de)

`include "uvm_macros.svh"
import uvm_pkg::*;
import alu_tb_config_pkg::*;

class alu_sequence extends uvm_sequence;
    `uvm_object_utils(alu_sequence)

    function new(string name = "alu_sequence");
        super.new(name);
    endfunction

    rand int iteration_count;

    constraint iteration_count_c{
        soft iteration_count inside {[20:40]};
    }

    virtual task body();
        for (int i=0; i<iteration_count; i++) begin
            alu_seq_item item = alu_seq_item::type_id::create("item");
            start_item(item);
            item.randomize();
            `uvm_info(get_type_name(), $sformatf("Generated new item: \n%0s", item.sprint()), UVM_MEDIUM)
            finish_item(item);
        end
        `uvm_info(get_type_name(), $sformatf("Done generation of %0d items", iteration_count), UVM_MEDIUM)
    endtask

endclass
