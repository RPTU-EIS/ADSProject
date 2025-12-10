// ADS I Class Project
// Assignment 02: Arithmetic Logic Unit and UVM Testbench
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 09/21/2025 by Tharindu Samarakoon (gug75kex@rptu.de)
// File updated on 12/10/2025 by Tobias Jauch (tobias.jauch@rptu.de)

import alu_tb_config_pkg::*;

class alu_test extends uvm_test;
    `uvm_component_utils(alu_test)

    function new(string name = "alu_test", uvm_component parent = null);
        super.new(name, parent);
    endfunction

    alu_env env;
    virtual alu_if vif;

    virtual function void build_phase(uvm_phase phase);
        super.build_phase(phase);
        env = alu_env::type_id::create("env", this);
        if(!uvm_config_db#(virtual alu_if)::get(this, "", "alu_if", vif)) begin
            `uvm_fatal(get_type_name(), "Did not get vif")
        end

        uvm_config_db#(virtual alu_if)::set(this, "env.agent.*", "alu_if", vif);
    endfunction

    virtual function void end_of_elaboration_phase (uvm_phase phase);
        uvm_top.print_topology();
    endfunction

    virtual task run_phase(uvm_phase phase);
        alu_sequence seq = alu_sequence::type_id::create("seq");
        phase.raise_objection(this);
        
        // set deterministic iteration count:
        seq.iteration_count = 1000;
        
        // Instead, you can also set a randomized iteration count:
        //seq.randomize() with { iteration_count inside {[500:1500]}; };


        seq.start(env.agent.sequencer);
        phase.drop_objection(this);
    endtask
endclass