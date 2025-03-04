class Way_model #(int NSETS = 8);
    localparam SET_BITS = $clog2(NSETS);
    localparam BYTE_OFFSET = 2;

    bit valid_array[NSETS-1:0];
    bit [31-SET_BITS-BYTE_OFFSET:0]tag_array[NSETS-1:0];
    bit [31:0]target_array[NSETS-1:0];
    bit [1:0]prediction_array[NSETS-1:0];

    function new();
        valid_array = '{default: 0};
        tag_array = '{default: 0};
        target_array = '{default: 0};
        prediction_array = '{default: 0}; // 0: strongTaken, 1: weakTaken, 2: strongNotTaken, 3: weakNotTaken
    endfunction

    function automatic void read(input int PC, ref bit valid, ref bit predicate, ref int target, ref bit readHit);
        bit [26:0]tag = PC[31:(SET_BITS+BYTE_OFFSET)];
        bit [2:0]index = PC[$clog2(NSETS)+1:2];
        valid = valid_array[index];
        predicate = prediction_array[index];
        target = target_array[index];
        readHit = ((tag_array[index] == tag) && (valid == 1'b1));
    endfunction

    // Two types of writes
    //  1. Write a new value (replace)
    //  2. Update prediction of existing value
    function automatic void write(input int updatePC, input int updateTarget, input bit mispredicted);
        bit [26:0]tag = updatePC[31:5];
        bit [2:0]index = updatePC[$clog2(NSETS)+1:2];

        if ((tag_array[index] == tag) && (valid_array[index] == 1'b1)) begin // value available, need to update the prediction
            updatePrediction(prediction_array[index], mispredicted);
        end
        else begin // replace existing value with a new value
            valid_array[index] = 1'b1;
            tag_array[index] = tag;
            target_array[index] = updateTarget;
            prediction_array[index] = '0; // reset prediction            
        end
        
        // $display("Updated way index: %d, tag: 0x%0x, target: 0x%0x", index, tag, updateTarget);
        
    endfunction

    function automatic void updatePrediction(ref bit[1:0]currentPrediction, input bit mispredicted);
        bit [1:0] newPrediction; // 0: strongTaken, 1: weakTaken, 2: strongNotTaken, 3: weakNotTaken

        case(currentPrediction)
            2'd0, 2'd2: newPrediction = (mispredicted)? currentPrediction + 1'b1: currentPrediction;
            2'd1, 2'd3: newPrediction = (mispredicted)? currentPrediction + 1'b1: currentPrediction-1;
        endcase

        currentPrediction = newPrediction;
    endfunction

endclass

class LRU_model #(int NSETS = 8);
    bit [NSETS-1:0]lru_counter;

    function new();
        lru_counter = '0;
    endfunction

    function automatic void update_LRU_array(input bit way, input int set); // update after read hit or write 
        // $display("update LRU way: %0d, set: %0d", way, set);
        // $display("before update LRU: %b", lru_counter[set]);
        lru_counter[set] = (way==1'b0)? 1'b1 : 1'b0;
        // $display("after update LRU: %b", lru_counter[set]);
    endfunction

    function automatic bit get_LRU_value(input int set);
        $display("get LRU set: %0d, value: %d", set, lru_counter[set]);
        return lru_counter[set];
    endfunction

endclass

class TwoWayBTB_model #(int NSETS = 8);
    localparam SET_BITS = $clog2(NSETS);
    localparam BYTE_OFFSET = 2;

    Way_model way[2];
    LRU_model lru;

    function new();
        for(int i=0; i<2; i++) begin
            way[i] = new();
        end
        this.lru  = new();
    endfunction

    function automatic void read(input int PC, ref bit valid, ref int target, ref bit predictTaken);
        bit w_valid[1:0];
        bit w_predicate[1:0];
        int w_target[1:0];
        bit w_readHit[1:0];

        for(int i=0; i<2; i++) begin
            way[i].read(.PC(PC), .valid(w_valid[i]), .predicate(w_predicate[i]), .target(w_target[i]), .readHit(w_readHit[i]));
            // $display("Read way%0d pc: 0x%0x, valid: %0d, predicate: %0d, target: 0x%0x, hit: %0d", i, PC, w_valid[i], w_predicate[i], w_target[i], w_readHit[i]);
        end

        if (w_readHit[0] && w_readHit[1]) $fatal("Both ways have same address 0x%0x", PC);
        else if (w_readHit[0]) begin
            valid = 1'b1;
            target = w_target[0];
            predictTaken = w_predicate[0];
            lru.update_LRU_array(.way(0), .set(PC[SET_BITS+BYTE_OFFSET-1:BYTE_OFFSET]));
        end
        else if (w_readHit[1]) begin
            valid = 1'b1;
            target = w_target[1];
            predictTaken = w_predicate[1];
            lru.update_LRU_array(.way(1), .set(PC[SET_BITS+BYTE_OFFSET-1:BYTE_OFFSET]));
        end
        else begin
            valid = 1'b0;
            target = '0;
            predictTaken = 1'b0;
        end
    endfunction

    function automatic bit check_hit(input int PC, output bit wayID);
        bit w_valid[1:0];
        bit w_predicate[1:0];
        int w_target[1:0];
        bit w_readHit[1:0];

        for(int i=0; i<2; i++) begin
            way[i].read(.PC(PC), .valid(w_valid[i]), .predicate(w_predicate[i]), .target(w_target[i]), .readHit(w_readHit[i]));
        end
        wayID = (w_readHit[1] == 1'b1)? 1'b1: 1'b0;
        // $display("Hit wayID: %0p", w_readHit);
        return ((w_readHit[0] == 1'b1) || (w_readHit[1] == 1'b1))? 1'b1 : 1'b0;
    endfunction

    function automatic void write(input bit update, input int updatePC, input int updateTarget, input bit mispredicted);
        bit replace_way;
        if(update) begin
            if (!(check_hit(.PC(updatePC), .wayID(replace_way)))) begin // check if already available in any of the ways
                replace_way = lru.get_LRU_value(updatePC[SET_BITS+BYTE_OFFSET-1:BYTE_OFFSET]); // else get the LRU way to replace
            end
            way[replace_way].write(.updatePC(updatePC), .updateTarget(updateTarget), .mispredicted(mispredicted));
            print_BTB();
        end
    endfunction

    function print_BTB;
        $display("set | valid |  tag  | target| prediction || valid |  tag  | target| prediction |");
        for(int i=0; i<NSETS; i++) begin
            $display("  %0d |   %0d   | 0x%3x | 0x%3x |     %0d      ||   %0d   | 0x%3x | 0x%3x |     %0d      |", i, way[0].valid_array[i], way[0].tag_array[i], way[0].target_array[i], way[0].prediction_array[i], way[1].valid_array[i], way[1].tag_array[i], way[1].target_array[i], way[1].prediction_array[i]);
        end

    endfunction

endclass

class TwoWayBTB_driver;
    localparam NUM_INSTRUCTIONS = 40;

    localparam BRANCH_INS = 2'd0;
    localparam JUMP_INS   = 2'd1;
    localparam OTHER_INS  = 2'd2;

    rand bit[1:0] instructions[0: NUM_INSTRUCTIONS-1];
    rand int target_addresses[0: NUM_INSTRUCTIONS-1];
    rand bit branch_or_not;

    int current_PC = 0;

    // buffer BTB outputs until EX stage
    localparam FIFO_DEPTH = 3;
    int PC_fifo[$:FIFO_DEPTH-1];
    bit valid_fifo[$:FIFO_DEPTH-1];
    int target_fifo[$:FIFO_DEPTH-1];
    int predictTaken_fifo[$:FIFO_DEPTH-1];

    constraint instruction_c{
        foreach (instructions[j]) {
            instructions[j] dist {BRANCH_INS := 1, JUMP_INS := 1, OTHER_INS:= 20}; 
        }
        instructions[NUM_INSTRUCTIONS-1] == JUMP_INS; // last instruction should jump back to 0th instruction
        foreach(target_addresses[i]){
            target_addresses[i] inside {[0:NUM_INSTRUCTIONS-1]};
        }
        target_addresses[NUM_INSTRUCTIONS-1] == 0; // last instruction should jump back to 0th instruction
    }
    
    constraint three_jumps_single_btb_set_c{ // 0->1->2->9->10->17->18->0
        // instructions[0]  == OTHER_INS;
        // instructions[1]  == OTHER_INS;
        // instructions[2]  == JUMP_INS;
        // instructions[3]  == OTHER_INS;
        // instructions[4]  == OTHER_INS;
        // instructions[9]  == OTHER_INS;
        // instructions[10] == JUMP_INS;
        // instructions[17] == OTHER_INS;
        // instructions[18] == JUMP_INS;

        // temporarily add till driver is corrected to flush wrong predictions
        // instructions[3] == OTHER_INS;
        // instructions[4] == OTHER_INS;

        // target_addresses[2]  == 9;
        // target_addresses[10] == 17;
        // target_addresses[18] == 0;

        instructions[0]  == BRANCH_INS;
        instructions[4]  == BRANCH_INS;
        instructions[8]  == BRANCH_INS;
        instructions[16]  == JUMP_INS;

        target_addresses[0]  == 4;
        target_addresses[4] == 8;
        target_addresses[8] == 16;
        target_addresses[16] == 0;

    }

    function new();
        // initialize fifos
        for(int i=0; i<FIFO_DEPTH; i++) begin
            PC_fifo.push_front('0);
            valid_fifo.push_front(1'b0);
            target_fifo.push_front('0);
            predictTaken_fifo.push_front(1'b0);
        end
        assert(this.randomize());
        // $display("target_addresses: %0p", target_addresses);
    endfunction

    function automatic capture_BTB(input bit valid, input int target, input bit predictTaken);

        // update fifos
        PC_fifo.push_front(current_PC);
        valid_fifo.push_front(valid);
        target_fifo.push_front(target);
        predictTaken_fifo.push_front(predictTaken);

        // $display("Captured BTB output: PC: 0x%0x valid: %0d, target: 0x%0x, predictTaken: %0d", current_PC*4, valid, target, predictTaken);
        // $display("PC_fifo: %0p", PC_fifo);
    endfunction

    int flush_counter; //used to ignore instructions in fifos when they are flushed

    // next PC is generated mainly by execution stage. However, BTB will decide the next PC if execution stage has no special requirement
    function automatic void generate_next_pc(output int PC, output bit[1:0]instruction);

        // variables in execution stage
        int old_PC = PC_fifo.pop_back();
        bit old_valid = valid_fifo.pop_back();
        int old_target = target_fifo.pop_back();
        bit old_predictTaken = predictTaken_fifo.pop_back();
        int old_instruction = instructions[old_PC];

        // variables in fetch stage
        int new_instruction = instructions[current_PC];
        bit new_valid = valid_fifo[0];
        int new_target = target_fifo[0];
        bit new_predictTaken = predictTaken_fifo[0];

        bit new_speculatively_branched = (new_valid == 1'b1) && (new_predictTaken == 1'b1);
        bit old_speculatively_branched = (old_valid == 1'b1) && (old_predictTaken == 1'b1);

        // $display("fetch stage: PC: 0x%0x, Ins: %0d, valid: %0d, target: 0x%0x, predictTaken: %0d", current_PC*4, new_instruction, new_valid, new_target, new_predictTaken);
        // $display("ex stage:    PC: 0x%0x, Ins: %0d, valid: %0d, target: 0x%0x, predictTaken: %0d", old_PC*4, old_instruction, old_valid, old_target, old_predictTaken);

        if (flush_counter > 0) begin // already inside a flushed instruction. Neglect values in execution stage
            if(new_speculatively_branched) current_PC = new_target;
            else current_PC = current_PC + 1;
        end
        else if(old_instruction == JUMP_INS) begin
            if (!old_speculatively_branched) begin // need to flush next 2 instructions
                current_PC = target_addresses[old_PC];
                flush_counter = 2;
                // $display("old_PC: %0d, current_PC: %0d", old_PC, current_PC);
            end
            else begin
                if(new_speculatively_branched) current_PC = new_target;
                else current_PC = current_PC + 1;
            end
        end
        else if(old_instruction == BRANCH_INS) begin
            assert(std::randomize(branch_or_not));
            if((!old_speculatively_branched) && (branch_or_not == 1'b1)) begin
                current_PC = target_addresses[old_PC];
                flush_counter = 2;
            end
            else if (old_speculatively_branched && (branch_or_not == 1'b0)) begin // should not branch, but already speculatively branched
                current_PC = current_PC + 1;
                flush_counter = 2;
            end
            else begin // either already correctly speculated branch or some other instruction
                if(new_speculatively_branched) current_PC = new_target;
                else current_PC = current_PC + 1;
            end
        end
        else begin
            if(new_speculatively_branched) current_PC = new_target;
            else current_PC = current_PC + 1;
        end

        if(flush_counter > 0) flush_counter = flush_counter - 1;

        PC = current_PC*4;
        instruction = instructions[current_PC];
        // $display("PC: 0x%0x, instruction: 0x%0x", PC, instruction);
    endfunction

    // BTB should be updated in these 3 cases for branch and jump instructions in execution stage
    //  1. when valid entry is not available
    //  2. when valid entry is available but the prediction was wrong
    //  3. when valid entry is available and the prediction was correct (used to update prediction state machine)
    function automatic void generate_BTB_update(output bit update, output int updatePC, output int updateTarget, output bit mispredicted);
        int pc = PC_fifo[FIFO_DEPTH-2];
        bit valid = valid_fifo[FIFO_DEPTH-2];
        int target = target_fifo[FIFO_DEPTH-2];
        bit predictTaken = predictTaken_fifo[FIFO_DEPTH-2];
        int instruction = instructions[pc];
        int real_target = target_addresses[pc];

        // $display("generate BTB fifos: PC: %0p, valid: %0p, target: %0p, predictTaken: %0p, instruction: %0d", PC_fifo, valid_fifo, target_fifo, predictTaken_fifo, instruction);
        // $display("PC_fifo: %0p", PC_fifo);
        $display("ex stage PC: 0x%0x, instruction: %0d, valid: %d, target: 0x%0x", pc*4, instruction, valid, real_target);

        if ((instruction == BRANCH_INS)||(instruction == JUMP_INS)) begin
            if (valid == 1'b0) begin // entry ins not available in BTB
                update = 1'b1;
                updatePC = pc*4;
                updateTarget = real_target*4;
                mispredicted = 1'b0;
                // $display("should update BTB miss");
            end
            else begin // entry available (valid == 1'b1)
                assert(target == real_target); // compare the actual target and target from BTB
                update = 1'b1;
                updatePC = pc*4;
                updateTarget = real_target*4;
                mispredicted = (branch_or_not == predictTaken) ? 1'b0: 1'b1; // prediction is true or false
                // $display("should update BTB hit");
            end
        end
        else begin // no update required
            update = 1'b0;
            updatePC = '0;
            updateTarget = '0;
            mispredicted = 1'b0;
        end

    endfunction

endclass

module TwoWayBTB_tb();
    timeunit 1ns;
    timeprecision 1ps;

    localparam ITERATIONS = 15;

    localparam real CLK_FREQ = 100; // MHz
    localparam real CLK_PERIOD = 1000/CLK_FREQ;

    TwoWayBTB_model BTB_model;
    TwoWayBTB_driver BTB_driver;
    
    bit clk, reset;

    int PC;
    bit update;
    int updatePC;
    int updateTarget;
    bit mispredicted;

    bit valid;
    int target;
    bit predictTaken;

    bit [1:0]instruction;

    bit debugWire;

    initial begin
        clk = 1'b0;
        forever begin
            #(CLK_PERIOD/2);
            clk = ~clk;
        end
    end

    initial begin
        BTB_model = new();
        BTB_driver = new();
    end

    initial begin
        reset = 1'b1;
        #(CLK_PERIOD*2);
        reset = 1'b0;

        for (int i=0;i<ITERATIONS;i++) begin
            @(posedge clk);
            BTB_driver.generate_BTB_update(update, updatePC, updateTarget, mispredicted);
            BTB_model.write(update, updatePC, updateTarget, mispredicted);
            BTB_model.read(PC, valid, target, predictTaken);
            BTB_driver.capture_BTB(valid, target, predictTaken); //(ref bit clk, ref bit valid, input int target, input bit predictTaken)
            BTB_driver.generate_next_pc(PC, instruction);
        end
        // forever begin
        //     @(posedge clk);
        //     fork
        //         begin
        //             BTB_driver.generate_next_pc(PC, instruction);
        //             BTB_model.read(PC, valid, target, predictTaken);
        //             BTB_driver.generate_BTB_update(update, updatePC, updateTarget, mispredicted);
        //             BTB_model.write(updatePC, updateTarget, mispredicted);
        //         end
        //         BTB_driver.capture_BTB(clk, valid, target, predictTaken);
        //     join
        // end
    end

endmodule