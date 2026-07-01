# Assignment 5: Branch and Jump Instructions Implementation

## Task 5.1 Preparation - Questions and Answers

### 1. Where can you find the encoding of the branch and jump instructions in RISC-V?

The encoding of branch and jump instructions can be found in the RISC-V Instruction Set Manual, specifically in the "Base Integer Instruction Set" section. The relevant instruction types are:
- **B-type (Branch)**: Specified in Section 2.4 of the RISC-V spec, with opcode 0x63
- **J-type (Jump)**: Specified in Section 2.4 of the RISC-V spec, with opcodes 0x6f (JAL) and 0x67 (JALR)

### 2. What new instruction format do you need to support for the branch and jump instructions?

Two new instruction formats were added:

- **B-type (Branch Instructions)**: 
  - Format: [31:25] funct7, [24:20] rs2, [19:15] rs1, [14:12] funct3, [11:7] imm[4:1|11], [6:0] opcode
  - Opcode: 0x63
  - Instructions: BEQ, BNE, BLT, BGE, BLTU, BGEU
  - Immediate encoding: 12-bit sign-extended immediate with bits [12|10:5|4:1|11] distributed across different fields

- **J-type (Jump Instructions)**:
  - Format: [31:12] imm[20|10:1|11|19:12], [11:7] rd, [6:0] opcode
  - JAL (opcode 0x6f): Unconditional jump with link (saves return address in rd)
  - JALR (opcode 0x67): Indirect jump with link (uses register as target address)
  - Immediate encoding: 20-bit sign-extended immediate

### 3. What stages of your pipeline do you need to modify to support the branch and jump instructions?

The following pipeline stages were modified:

1. **IF (Instruction Fetch) Stage**:
   - Added input for branch target address
   - Added flush signal to redirect PC to branch target
   - Implements simple static branch prediction

2. **ID (Instruction Decode) Stage**:
   - Added decoding logic for B-type and J-type instructions
   - Added immediate extraction for B-type and J-type immediates
   - Added PC passing through for return address calculation (JAL)

3. **IDBarrier**:
   - Added PC output to pass through pipeline for JAL return address

4. **EX (Execution) Stage**:
   - Added branch condition evaluation (BEQ, BNE, BLT, BGE, BLTU, BGEU)
   - Added branch target calculation (PC + immediate for branches)
   - Added jump target calculation (PC + immediate for JAL, rs1 + immediate for JALR)
   - Added return address computation (PC + 4 for JAL/JALR)
   - Output flush signal and branch target to pipeline

5. **EXBarrier, MEMBarrier, WBBarrier**:
   - Added flush and branchTarget signal passing

6. **core.scala (Pipeline Controller)**:
   - Wired flush and branchTarget signals from WB back to IF
   - Implements pipeline flushing on branch taken

### 4. What new types of hazards can arise with branch and jump instructions, and how can they be resolved?

**New Hazard Types:**

1. **Control Hazards (Branch Misprediction)**:
   - When a conditional branch is predicted as "not taken" but actually is taken, instructions in the IF, ID, and EX stages are incorrectly fetched and need to be discarded
   - **Resolution**: Simple static branch prediction (always not taken for conditional branches) with pipeline flushing when branch is actually taken. The WB stage signals a flush when a branch is taken, causing the IF stage to redirect the PC to the correct branch target.

2. **Data Hazards with Branch/Jump Instructions**:
   - The operands for branch condition evaluation (rs1, rs2) may not be ready when the branch instruction is in the EX stage
   - **Resolution**: The forwarding unit was extended to forward results from MEM and WB stages to the EX stage for branch condition evaluation, just like for other ALU instructions.

3. **Return Address Hazards (JAL/JALR)**:
   - The return address (PC + 4) needs to be available in the WB stage to write to rd
   - **Resolution**: The return address is computed in the EX stage and passed through the barriers to the WB stage.

## Implementation Details

### Instruction Decoding (IDstage.scala)

Branch instructions are decoded in the ID stage by checking the opcode (0x63) and funct3 field:
- BEQ: funct3 = 0x0
- BNE: funct3 = 0x1
- BLT: funct3 = 0x4
- BGE: funct3 = 0x5
- BLTU: funct3 = 0x6
- BGEU: funct3 = 0x7

Jump instructions are decoded using their unique opcodes:
- JAL: opcode = 0x6f
- JALR: opcode = 0x67 with funct3 = 0x0

### Immediate Extraction

- **B-type immediates**: Extracted from bits [31:25], [7], [30:25], [11:8] and sign-extended to 32 bits
- **J-type immediates**: Extracted from bits [31], [19:12], [20], [30:21] and sign-extended to 32 bits
- **JALR immediates**: Extracted as I-type immediates from bits [31:20]

### Branch Condition Evaluation (EXstage.scala)

The EX stage evaluates branch conditions using the forwarded operands:
- **BEQ**: rs1 == rs2
- **BNE**: rs1 != rs2
- **BLT**: rs1 < rs2 (signed)
- **BGE**: rs1 >= rs2 (signed)
- **BLTU**: rs1 < rs2 (unsigned)
- **BGEU**: rs1 >= rs2 (unsigned)

### Branch Target Calculation

- **Conditional branches (BEQ, BNE, BLT, BGE, BLTU, BGEU)**: target = PC + immediate (if taken)
- **JAL**: target = PC + immediate, return address = PC + 4
- **JALR**: target = (rs1 + immediate) & ~1, return address = PC + 4

### Pipeline Flushing

When a branch is taken (branchTaken = true in EX stage), the flush signal propagates through the barriers to the WB stage. In the WB stage, the flush signal is fed back to the IF stage, causing:
1. The PC to be redirected to the branch target
2. Instructions in IF, ID, and EX stages (which were based on incorrect path assumption) are effectively discarded

### Static Branch Prediction

The implementation uses a simple static branch prediction scheme:
- **Conditional branches**: Always predict "not taken" (fetch next sequential instruction)
- **Unconditional jumps (JAL, JALR)**: Always predict "taken" (always flush and redirect)

This approach minimizes the penalty for unconditional jumps but may have higher branch misprediction penalty for conditional branches.

## Testing

### Test Program Structure

Test programs are provided in hex format (text files with 8-character hex values, one per line) in the `src/test/programs/` directory:
- `BinaryFile_pipelined`: Original test program from Assignment 4 (validates baseline functionality)
- `BinaryFile_branches`: Branch instruction test program (validates branch functionality)

### Test Coverage

The implementation has been validated with:
1. **R-type and I-type instructions**: All arithmetic, logical, and immediate instructions work correctly with proper forwarding
2. **Branch prediction and flushing**: Conditional branches are correctly predicted as not-taken, and pipeline is flushed when prediction is incorrect
3. **Jump instructions**: Both JAL and JALR correctly compute targets and return addresses

## Files Modified/Created

1. **common.scala**: Added uopc enum values for B-type and J-type instructions
2. **IDstage.scala**: Added B-type and J-type instruction decoding and immediate extraction
3. **IDbarrier.scala**: Added PC signal passing
4. **IFstage.scala**: Added branch target and flush signal inputs
5. **IFbarrier.scala**: Added PC signal passing
6. **EXstage.scala**: Added branch condition evaluation and target calculation
7. **EXbarrier.scala**: Added flush and branchTarget signal passing
8. **MEMbarrier.scala**: Added flush and branchTarget signal passing
9. **WBbarrier.scala**: Added flush and branchTarget signal passing
10. **core.scala**: Added branch control signal wiring
11. **RegisterFile.scala**: Implemented with write-through forwarding
12. **ALU.scala**: Copied from Assignment 2
13. **WBstage.scala**: Implemented writeback stage
14. **MakeVerilog.scala**: Verilog generation script

## Known Limitations

1. The implementation uses static branch prediction (always not-taken for conditional branches), which can lead to high branch misprediction penalty
2. No branch predictor is implemented - a more sophisticated dynamic predictor could improve performance
3. Pipeline depth is fixed at 5 stages - no variable-length pipelines are supported

## Future Enhancements

1. Implement a branch predictor (e.g., 2-bit pattern history predictor)
2. Add support for store/load instructions with memory access control
3. Implement advanced branch prediction techniques (branch target buffer, return address stack)
4. Add support for exceptions and interrupts
