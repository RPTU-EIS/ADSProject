# Assignment 03: Pipelined RV32I Core

This is an implementation of a 5-stage pipelined RISC-V 32-bit Integer (RV32I) processor core, designed in Chisel HDL. The processor executes the R-type and I-type subset of the RV32I instruction set without hazard detection or resolution.

## Processor Architecture Overview

### 5-Stage Pipeline

The processor implements a classic 5-stage pipeline architecture:

1. **Instruction Fetch (IF)**: Fetch instruction from instruction memory and increment program counter
2. **Instruction Decode (ID)**: Decode instruction, extract operands from register file, generate immediate values
3. **Execute (EX)**: Execute ALU operations
4. **Memory (MEM)**: Load/Store operations on data memory
5. **Write-Back (WB)**: Write results back to register file

### Key Features

- **Full RV32I ISA Support**: Supports the following RV32I instructions
  - R-type arithmetic and logical operations
  - I-type immediate operations

- **Comprehensive ALU**: All 11 RV32I ALU operations with exception detection

- **Memory Interface**: Configurable instruction and data memory with word-addressed access

## Project Structure

### Source Code (`src/main/scala/`)

- **`core.scala`**: Top-level processor core module instantiating all pipeline stages and connecting interfaces
- **`PipelinedRISCV32I.scala`**: Wrapper module for testbench integration
- **`IFstage.scala`**: Instruction Fetch stage - PC management and instruction retrieval
- **`IDstage.scala`**: Instruction Decode stage - instruction decoding, register file reads, immediate generation
- **`EXstage.scala`**: Execute stage - ALU operations, branch condition evaluation, address calculation
- **`MEMstage.scala`**: Memory stage - load/store operations, data memory interface
- **`WBstage.scala`**: Write-Back stage - result selection and register file writes
- **`IFbarrier.scala`, `IDbarrier.scala`, `EXbarrier.scala`, `MEMbarrier.scala`, `WBbarrier.scala`**: Pipeline registers holding stage outputs
- **`ALU.scala`**: 32-bit ALU supporting all 11 RV32I operations with exception handling
- **`RegisterFile.scala`**: 32×32-bit register file with 2 read ports and 1 write port
- **`common.scala`**: Common enums and control signals (ALU operations, opcodes, control types)
- **`MakeVerilog.scala`**: Verilog generation driver

### Test Files (`src/test/`)

- **`scala/`**: Chisel testbench with test programs and verification harness
  - `PipelinedRISCV32I_tb.scala`: Chisel testbench for processor verification
  - Test programs in assembly format

- **`programs/`**: Binary instruction files
  - `BinaryFile`: Compiled test program loaded into instruction memory

## RV32I Instruction Set Coverage

### Instruction Categories

- **Arithmetic** 
- **Comparison** 
- **Logical**
- **Shift**
- **Total** 

### Not Implemented

- **Load** LW, LH, LHU, LB, LBU
- **Store** SW, SH, SB
- **Branch** BEQ, BNE, BLT, BGE, BLTU, BGEU
- **Jump** JAL, JALR
- **Upper Immediate** LUI, AUIPC
- **System Instructions**: ECALL, EBREAK, FENCE, FENCE.I
- **Privileged Instructions**: CSRRW, CSRRS, CSRRC, CSRRWI, CSRRSI, CSRRCI
- **Specialized**: UNIMP (test marker only)

## Requirements

- **Build Tools**:
  - Scala CLI or SBT (Scala Build Tool)
  - Chisel 3.5+

- **Optional Visualization**:
  - GTKWave or similar for waveform viewing
  - Browser-based tools like [Surfer](https://app.surfer-project.org/)

## Usage Instructions

### Compile the Chisel Project

Generate Verilog RTL from Chisel:

```bash
cd 03_RV32I-pipeline
sbt run
```

This creates the synthesizable Verilog in `generated-src/PipelinedRV32I.v`.

### Run Chisel Testbench

Execute the Chisel testbench with test program:

```bash
sbt test
```

This:
- Loads test program from `src/test/programs/BinaryFile`
- Runs simulation for specified number of cycles
- Verifies expected register file values at each stage
- Generates waveforms in `test_run_dir/`

**Expected Output** (successful test):
```
[info] PipelinedRISCV32ITest: PipelinedRV32I_Tester should work
[info] Run completed successfully.
```

### View Waveforms

- **Chisel tests**: VCD files in `test_run_dir/<Test_Name>/PipelinedRV32I.vcd`
- Open with GTKWave:
  ```bash
  gtkwave test_run_dir/*/PipelinedRV32I.vcd
  ```

## Key Implementation Features

### Processor Design

- **5-Stage Pipeline**: Classical MIPS-style pipeline with pipeline registers between stages
- **Register File**: Full register file with x0 hard-wired to zero

### Instruction Decoding

- **Comprehensive Control Unit**: RV32I instruction subset with:
  - R-type instruction recognition and funct3/funct7 decoding
  - I-type immediate decoding with sign extension


### ALU Implementation

ALU design used from previous assignment.


## Hazard Detection and Resolution

### Data Hazards
No solution implemented in Assignment03


## Test Coverage

The testbench verifies:
- ✓ R-type and I-type arithmetic/logical operations
- ✓ Immediate value extraction and sign extension

## References

- [RISC-V ISA Manual - Volume I: Unprivileged ISA](https://docs.riscv.org/reference/isa/_attachments/riscv-unprivileged.pdf)
- [Chisel Documentation](https://www.chisel-lang.org/)
- Computer Architecture: A Quantitative Approach (John L. Hennessy and David A. Patterson, Morgan Kaufmann, 2011)
- [Architecture of Digital Systems I](https://eit.rptu.de/fgs/eis/teaching/85-571) Lecture Notes