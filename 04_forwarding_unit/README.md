# Assignment 04: Hazard Detection and Forwarding Unit


## Processor Architecture Overview

### 5-Stage Pipeline with Hazard Detection and Forwarding

This task is based on the classic 5-stage pipeline architecture implemented in assignment 3:

1. **Instruction Fetch (IF)**: Fetch instruction from instruction memory and increment program counter
2. **Instruction Decode (ID)**: Decode instruction, extract operands from register file, generate immediate values
3. **Execute (EX)**: Execute ALU operations
4. **Memory (MEM)**: Load/Store operations on data memory (left empty)
5. **Write-Back (WB)**: Write results back to register file

### Key Features to be added in this task

Add a Forwarding Unit to your RISC-V pipeline from assignment 03 that detects data hazards and resolves them by controlling input multiplexers in the EX stage. 
Connect the Forwarding Unit and the input signals to the multiplexers accordingly in core.scala.
 
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
- **`ForwardingUnit.scala`**: Forwarding Unit to control the input muxes in the EX stage
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

| Category | Instructions | Count |
|----------|--------------|-------|
| **Arithmetic** | ADD, ADDI, SUB | 3 |
| **Comparison** | SLT, SLTI, SLTU, SLTIU | 4 |
| **Logical** | AND, ANDI, OR, ORI, XOR, XORI | 6 |
| **Shift** | SLL, SLLI, SRL, SRLI, SRA, SRAI | 6 |
| **Total** | | **19** |

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
cd 04_forwarding_unit
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



## Test Coverage

Test all possible cases of potential data hazards in this pipeline and check whether all of them are resolved by your implemented forwarding unit.

## References

- [RISC-V ISA Manual - Volume I: Unprivileged ISA](https://docs.riscv.org/reference/isa/_attachments/riscv-unprivileged.pdf)
- [Chisel Documentation](https://www.chisel-lang.org/)
- Computer Architecture: A Quantitative Approach (John L. Hennessy and David A. Patterson, Morgan Kaufmann, 2011)
- [Architecture of Digital Systems I](https://eit.rptu.de/fgs/eis/teaching/85-571) Lecture Notes
