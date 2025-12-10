# Assignment 02: Arithmetic Logic Unit (ALU) with UVM Testbench

This is an implementation of a 32-bit Arithmetic Logic Unit (ALU) for an RV32I RISC-V processor, designed in Chisel HDL with a comprehensive UVM testbench for verification.

## ALU Operations Supported

The ALU supports all 11 RV32I operations:
- **ADD**: Addition with two's-complement wraparound
- **SUB**: Subtraction with two's-complement wraparound
- **AND**: Logical bitwise AND
- **OR**: Logical bitwise OR
- **XOR**: Logical bitwise XOR
- **SLL**: Shift Left Logical (uses lower 5 bits of operandB)
- **SRL**: Shift Right Logical (uses lower 5 bits of operandB)
- **SRA**: Shift Right Arithmetic (uses lower 5 bits of operandB)
- **SLT**: Set Less Than (signed comparison)
- **SLTU**: Set Less Than Unsigned (unsigned comparison)
- **PASSB**: Pass operandB unchanged to output

## Project Structure

- **`src/main/scala/`**: Chisel HDL implementation
  - `ALU.scala`: ALU module implementation
  - `MakeVerilog.scala`: Verilog code generation

- **`src/test/scala/`**: Chisel testbench
  - `alu_tb.scala`: Test-driven development testbench with separate test classes for each operation

- **`src/test/uvm/`**: SystemVerilog UVM verification environment
  - `alu_seq_item.sv`: Sequence item (atomic stimulus unit)
  - `alu_sequence.sv`: Sequence (generates transaction streams)
  - `alu_driver.sv`: Driver (converts transactions to pin-level signals)
  - `alu_monitor.sv`: Monitor (observes and records transactions)
  - `alu_agent.sv`: Agent (bundles sequencer, driver, monitor)
  - `alu_scoreboard.sv`: Scoreboard (reference model and result checking)
  - `alu_env.sv`: Environment (instantiates and connects UVM components)
  - `alu_test.sv`: Test (top-level configuration and test execution)
  - `alu_if.sv`: Interface (carries DUT signals and clocking blocks)
  - `alu_tb_config_pkg.sv`: Configuration package (parameters, enums)
  - `alu_tb.sv`: Top module (DUT instantiation and testbench harness)
  - `alu_sim.tcl`: Tcl script for simulation in Vivado

- **`generated-src/`**: Generated RTL
  - `ALU.v`: Generated Verilog from Chisel compilation

## Requirements

- **Chisel/Scala**: 
  - Scala CLI ([Installation Guide](https://www.chisel-lang.org/docs/installation))
  - sbt (Scala Build Tool)

- **Simulation**:
  - Xilinx Vivado Design Suite (tested on version 2020.1)
  - or any SystemVerilog simulator supporting UVM (ModelSim, VCS, Verilator)

## Usage Instructions

### Compile the Chisel Project

Generate Verilog RTL from Chisel:
```bash
sbt run
```
This creates the synthesizable Verilog in `generated-src/ALU.v`.

### Run Chisel Testbench

Execute the test-driven development testbench:
```bash
sbt test
```
This runs all test classes and generates waveforms in `test_run_dir/`.

**Note**: Each operation has a dedicated test class with comprehensive edge cases (wraparound, zero values, boundary conditions, etc.).

### Run UVM Testbench in Vivado

1. First, ensure the Chisel project is compiled:
   ```bash
   sbt run
   ```

2. Open Vivado and navigate to the project directory:
   ```bash
   cd 02_ALU
   ```

3. In the Vivado Tcl Console, run the simulation script:
   ```tcl
   source ./alu_sim.tcl
   ```

4. Monitor the simulation output for test results. Expected output:
   ```
   Scoreboard summary: Passes: 1000, Fails: 0, Total: 1000, Pass rate: 100.00 %
   ```

### Alternative: Run with Other SystemVerilog Simulators

For ModelSim, VCS, or Verilator, compile the UVM environment:
```bash
# Add all files from src/test/uvm/ and generated-src/ALU.v to your simulator project
# Ensure UVM libraries are available
```

### View Waveforms

- **Chisel tests**: VCD files in `test_run_dir/*/ALU.vcd`
- **UVM simulation**: VCD file at `build/alu_dump.vcd` (in Vivado)

### Run SystemVerilog Testbench (Legacy)

For direct SystemVerilog simulation without UVM:
```bash
# In Vivado:
# 1. Create RTL project (Default Part)
# 2. Add design source: generated-src/ALU.v
# 3. Add simulation source: src/test/uvm/alu_tb.sv (if available)
# 4. Run simulation
```

## Key Implementation Features

### ALU Design (Chisel)
- **Purely combinational**: No state, all operations completed within one clock cycle
- **RV32I compliant**: Follows RISC-V 32-bit ISA specification
- **Exception handling**: Includes `exception` output for detecting unsupported operations
- **Two's-complement arithmetic**: ADD/SUB operations with modulo-2³² wraparound
- **Proper shift semantics**: SLL/SRL/SRA use only lower 5 bits of operandB for shift amount

### Chisel Testbench (TDD)
- **Modular test classes**: Separate test class for each operation
- **Comprehensive coverage**: Edge cases including:
  - Zero and maximum values
  - Wraparound conditions
  - Signed vs. unsigned comparisons
  - Boundary shift amounts (0, 31)
- **Automated VCD generation**: Waveforms for debugging

## Test Coverage Summary

| Operation | Tests | Corner Cases Covered |
|-----------|-------|---------------------|
| ADD | 5 | Zero, max, wraparound, positive/negative |
| SUB | 5 | Zero, underflow, negative to positive transition |
| AND | 5 | All-zeros, all-ones, alternating bits |
| OR | 5 | All-zeros, all-ones, alternating bits |
| XOR | 4 | All-zeros, all-ones, alternating bits |
| SLL | 3 | Shift by 0, 1, 31 |
| SRL | 3 | Shift by 0, 1, 31 (logical) |
| SRA | 3 | Shift by 0, 1, 31 (arithmetic sign extension) |
| SLT | 3 | Signed comparison, equal values, negative vs. positive |
| SLTU | 4 | Unsigned comparison, max value, zero |
| PASSB | 3 | Pass zero, max, arbitrary value |

### UVM Testbench
- **Constrained-random stimulus**: Automatically generates valid ALU operations and operands
- **Transaction-level verification**: Reusable sequence items and sequences
- **Scoreboard comparison**: Reference model validates all results
- **Coverage-driven**: Supports functional coverage collection and reporting
- **Scalable architecture**: Modular design enables extension to larger systems

## Troubleshooting

### Issue: "UVM not found" in Vivado
- Ensure UVM libraries are installed in Vivado
- Check Vivado version (2020.1 or 2022.2 recommended)

### Issue: Test failures in UVM simulation
- Verify Chisel compilation: `sbt run` should complete without errors
- Check `generated-src/ALU.v` exists and is valid
- Review `vivado.log` for detailed error messages

### Issue: Waveform not generated
- Ensure `$dumpfile()` and `$dumpvars` are in `alu_tb.sv`
- Check simulation directory permissions
- Increase dump file size limit if needed

## References

- [RISC-V ISA Manual](https://docs.riscv.org/reference/isa/_attachments/riscv-unprivileged.pdf) (pp. 23-40 for RV32I)
- [Chisel Documentation](https://www.chisel-lang.org/)
- [SystemVerilog UVM Guide](https://verificationacademy.com/uvm)
- [Xilinx Vivado Simulator Guide](https://docs.xilinx.com/)
