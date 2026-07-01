# Task 04: Hazard Detection and Forwarding Unit - Implementation Summary

## Overview
Successfully implemented a forwarding unit that detects and resolves data hazards (RAW - Read-After-Write) in the 5-stage pipelined RISC-V processor without requiring pipeline stalls.

## Architecture

### Forwarding Unit Design
The forwarding unit operates by:
1. **Hazard Detection**: Compares source registers (rs1, rs2) in the EX stage with destination registers (rd) in both MEM and WB stages
2. **Forwarding Control**: Generates multiplexer control signals to select correct ALU operand sources:
   - **0**: Original operand from ID stage
   - **1**: Forwarded value from MEM stage (exBarrier)
   - **2**: Forwarded value from WB stage (memBarrier)
3. **Priority**: MEM stage forwarding has priority over WB stage forwarding

### Pipeline Barriers Enhanced
- **EXBarrier (EX/MEM)**: Added `outWriteEn` signal for forwarding control
- **IDBarrier (ID/EX)**: Added `rs1` and `rs2` outputs to pass register addresses to forwarding logic
- **MEMBarrier (MEM/WB)**: Added `outWriteEn` signal
- **WBBarrier (WB)**: Added `outWriteEn` and `outRD` for forwarding tracking

### Register File Enhancement
Implemented write-through forwarding: when reading and writing the same register in the same cycle, the ALU input immediately receives the written value.

## Test Cases Implemented

### 1. **Forwarding_RAW_Hazard_MEM** ✓
**Tests**: Data hazard resolved by forwarding from MEM stage
```
ADDI x1, x0, 10  →  x1 = 10
ADD x2, x1, x0   →  x2 = 10 (x1 forwarded from MEM)
```
**Result**: Both instructions execute correctly with 1-cycle dependency resolved

### 2. **Forwarding_RAW_Hazard_WB** ✓
**Tests**: Data hazard resolved by forwarding from WB stage
```
ADDI x1, x0, 15  →  x1 = 15
ADDI x2, x0, 8   →  x2 = 8
ADD x3, x1, x2   →  x3 = 23 (both x1 and x2 forwarded)
```
**Result**: 2-cycle dependency with dual operand forwarding works correctly

### 3. **Forwarding_Both_Operands_MEM** ✓
**Tests**: Both ALU operands from same register, forwarded from MEM
```
ADDI x1, x0, 7   →  x1 = 7
ADD x2, x1, x1   →  x2 = 14 (x1 forwarded to both inputs)
```
**Result**: Same register on both operands doesn't interfere with forwarding

### 4. **Forwarding_Chain_Dependencies** ✓
**Tests**: Chain of dependent instructions (dependency propagation)
```
ADDI x1, x0, 3   →  x1 = 3
ADD x2, x1, x1   →  x2 = 6
ADD x3, x2, x0   →  x3 = 6
ADD x4, x3, x0   →  x4 = 6
```
**Result**: Long dependency chains handled correctly through cascading forwards

### 5. **Forwarding_Mixed_Sources** ✓
**Tests**: One operand from MEM, one from WB stage
```
ADDI x1, x0, 20  →  x1 = 20
ADDI x2, x0, 5   →  x2 = 5
SUB x3, x1, x2   →  x3 = 15 (x1 from MEM, x2 from WB)
```
**Result**: Mixed source priorities work correctly

### 6. **Forwarding_Logical_Operations** ✓
**Tests**: Forwarding with AND, OR, XOR operations
```
ADDI x1, x0, 0xFF   →  x1 = 255
ADDI x2, x0, 0x0F   →  x2 = 15
AND x3, x1, x2      →  x3 = 15
OR x4, x1, x2       →  x4 = 255
XOR x5, x1, x2      →  x5 = 240
```
**Result**: Logical operations with forwarded operands execute correctly

### 7. **Forwarding_Shift_Operations** ✓
**Tests**: Forwarding with shift instructions (SLL, SRL)
```
ADDI x1, x0, 4   →  x1 = 4
ADDI x2, x0, 2   →  x2 = 2
SLL x3, x1, x2   →  x3 = 16 (4 << 2)
SRL x4, x3, x2   →  x4 = 4 (16 >> 2)
```
**Result**: Shift operations correctly forward both value and shift amount

### 8. **Forwarding_Comparison_Operations** ✓
**Tests**: Forwarding with comparison operations (SLT)
```
ADDI x1, x0, 10  →  x1 = 10
ADDI x2, x0, 20  →  x2 = 20
SLT x3, x1, x2   →  x3 = 1 (10 < 20)
SLT x4, x2, x1   →  x4 = 0 (20 < 10)
```
**Result**: Comparison operations work correctly with forwarded values

### 9. **Forwarding_No_Hazard** ✓
**Tests**: Independent instructions without hazards (baseline)
```
ADDI x1, x0, 100  →  x1 = 100
ADDI x2, x0, 200  →  x2 = 200
ADDI x3, x0, 300  →  x3 = 300
```
**Result**: Non-dependent instructions execute without forwarding interference

### 10. **Forwarding_Multiple_Chains** ✓
**Tests**: Multiple independent dependency chains in parallel
```
Chain 1:            Chain 2:
ADDI x1, x0, 5      ADDI x4, x0, 3
ADD x2, x1, x1      ADD x5, x4, x4
ADD x3, x2, x0      ADD x6, x5, x0
```
**Result**: Multiple chains processed in parallel without interference

## Key Implementation Details

### Forwarding Logic (EX Stage)
```
forwardA = MEM if (rs1 == rd_MEM and wrEn_MEM and rs1 != x0) else
           WB  if (rs1 == rd_WB and wrEn_WB and rs1 != x0) else
           ID
```

### Hardware Optimizations
1. **Zero Register Exclusion**: x0 never forwarded (always returns 0)
2. **Exception Handling**: Invalid instructions block forwarding (wrEn=false)
3. **Write-Through Register File**: Simultaneous read/write returns new value
4. **Pipeline Depth Maintained**: No stalling required - all dependencies resolved via forwarding

## Performance Impact
- **No pipeline stalls** for resolved hazards
- **Full execution throughput** maintained
- **Minimal hardware overhead**: Only comparison logic and 2-to-1 muxes per operand

## Hazards NOT Requiring Forwarding
1. **Load-Use Hazards**: Load instructions not implemented in RV32I subset
2. **Branch Hazards**: Branch instructions not yet implemented
3. **Store Conflicts**: Store instructions not implemented

## Supported Instruction Types with Forwarding
- **R-type**: ADD, SUB, AND, OR, XOR, SLL, SRL, SRA, SLT, SLTU
- **I-type**: ADDI, ANDI, ORI, XORI, SLLI, SRLI, SRAI, SLTI, SLTIU

## Test Results
```
Total Tests: 10
Passed:      10 ✓
Failed:      0
Success Rate: 100%
```

## Conclusion
The forwarding unit successfully resolves all RAW data hazards in the pipelined processor without pipeline stalls. The implementation correctly handles:
- Single-cycle dependencies (MEM forwarding)
- Multi-cycle dependencies (WB forwarding)
- Dual operand forwarding from mixed sources
- Dependency chains
- All supported instruction types
