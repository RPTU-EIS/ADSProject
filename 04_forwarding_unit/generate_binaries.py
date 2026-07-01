#!/usr/bin/env python3
"""
RISC-V Binary Generator for Task 04 Test Programs
Generates hex binaries for various hazard detection and forwarding test cases
"""

def encode_i_type(opcode, rd, funct3, rs1, imm):
    """Encode I-type instruction (ADDI, etc.)"""
    # Handle sign extension for negative immediates
    imm = imm & 0xFFF  # 12-bit immediate
    inst = imm << 20 | rs1 << 15 | funct3 << 12 | rd << 7 | opcode
    return inst & 0xFFFFFFFF

def encode_r_type(opcode, rd, funct3, rs1, rs2, funct7):
    """Encode R-type instruction (ADD, SUB, etc.)"""
    inst = funct7 << 25 | rs2 << 20 | rs1 << 15 | funct3 << 12 | rd << 7 | opcode
    return inst & 0xFFFFFFFF

# Opcodes
OPCODE_I = 0x13  # ADDI
OPCODE_R = 0x33  # ADD, SUB, etc.

# Funct3 codes
FUNCT3_ADD = 0x0
FUNCT3_AND = 0x7
FUNCT3_OR  = 0x6
FUNCT3_XOR = 0x4
FUNCT3_SLL = 0x1
FUNCT3_SRL = 0x5
FUNCT3_SLT = 0x2

# Funct7 codes
FUNCT7_ADD = 0x00
FUNCT7_SUB = 0x20
FUNCT7_SRA = 0x20

def write_binary_file(filename, instructions):
    """Write instructions to binary file in hex format"""
    with open(filename, 'w') as f:
        for inst in instructions:
            # Write as 8-digit hex (32 bits)
            f.write(f"{inst:08x}\n")

# Test 1: RAW hazard - MEM stage forwarding
# ADDI x1, x0, 10    -> x1 = 10
# ADD x2, x1, x0     -> x2 = 10 + 0 = 10 (x1 forwarded from MEM)
prg1 = [
    encode_i_type(OPCODE_I, 1, FUNCT3_ADD, 0, 10),    # ADDI x1, x0, 10
    encode_r_type(OPCODE_R, 2, FUNCT3_ADD, 1, 0, FUNCT7_ADD),  # ADD x2, x1, x0
]
write_binary_file('src/test/programs/BinaryFile_forwarding_raw_mem', prg1)

# Test 2: RAW hazard - WB stage forwarding
# ADDI x1, x0, 15    -> x1 = 15
# ADDI x2, x0, 8     -> x2 = 8
# ADD x3, x1, x2     -> x3 = 15 + 8 = 23 (both forwarded)
prg2 = [
    encode_i_type(OPCODE_I, 1, FUNCT3_ADD, 0, 15),     # ADDI x1, x0, 15
    encode_i_type(OPCODE_I, 2, FUNCT3_ADD, 0, 8),      # ADDI x2, x0, 8
    encode_r_type(OPCODE_R, 3, FUNCT3_ADD, 1, 2, FUNCT7_ADD),  # ADD x3, x1, x2
]
write_binary_file('src/test/programs/BinaryFile_forwarding_raw_wb', prg2)

# Test 3: Both operands from MEM
# ADDI x1, x0, 7     -> x1 = 7
# ADD x2, x1, x1     -> x2 = 7 + 7 = 14 (both operands same, forwarded from MEM)
prg3 = [
    encode_i_type(OPCODE_I, 1, FUNCT3_ADD, 0, 7),      # ADDI x1, x0, 7
    encode_r_type(OPCODE_R, 2, FUNCT3_ADD, 1, 1, FUNCT7_ADD),  # ADD x2, x1, x1
]
write_binary_file('src/test/programs/BinaryFile_forwarding_both_mem', prg3)

# Test 4: Chain of dependencies
# ADDI x1, x0, 3     -> x1 = 3
# ADD x2, x1, x1     -> x2 = 6 (forwarded from MEM)
# ADD x3, x2, x0     -> x3 = 6 (forwarded from WB)
# ADD x4, x3, x0     -> x4 = 6 (forwarded from WB)
prg4 = [
    encode_i_type(OPCODE_I, 1, FUNCT3_ADD, 0, 3),      # ADDI x1, x0, 3
    encode_r_type(OPCODE_R, 2, FUNCT3_ADD, 1, 1, FUNCT7_ADD),  # ADD x2, x1, x1
    encode_r_type(OPCODE_R, 3, FUNCT3_ADD, 2, 0, FUNCT7_ADD),  # ADD x3, x2, x0
    encode_r_type(OPCODE_R, 4, FUNCT3_ADD, 3, 0, FUNCT7_ADD),  # ADD x4, x3, x0
]
write_binary_file('src/test/programs/BinaryFile_forwarding_chain', prg4)

# Test 5: Mixed forwarding sources
# ADDI x1, x0, 20    -> x1 = 20
# ADDI x2, x0, 5     -> x2 = 5
# SUB x3, x1, x2     -> x3 = 20 - 5 = 15 (x1 from MEM, x2 from WB)
prg5 = [
    encode_i_type(OPCODE_I, 1, FUNCT3_ADD, 0, 20),     # ADDI x1, x0, 20
    encode_i_type(OPCODE_I, 2, FUNCT3_ADD, 0, 5),      # ADDI x2, x0, 5
    encode_r_type(OPCODE_R, 3, FUNCT3_ADD, 1, 2, FUNCT7_SUB),  # SUB x3, x1, x2
]
write_binary_file('src/test/programs/BinaryFile_forwarding_mixed', prg5)

# Test 6: Logical operations
# ADDI x1, x0, 0xFF  -> x1 = 255
# ADDI x2, x0, 0x0F  -> x2 = 15
# AND x3, x1, x2     -> x3 = 0x0F = 15
# OR x4, x1, x2      -> x4 = 0xFF = 255
# XOR x5, x1, x2     -> x5 = 0xF0 = 240
prg6 = [
    encode_i_type(OPCODE_I, 1, FUNCT3_ADD, 0, 0xFF),    # ADDI x1, x0, 255
    encode_i_type(OPCODE_I, 2, FUNCT3_ADD, 0, 0x0F),    # ADDI x2, x0, 15
    encode_r_type(OPCODE_R, 3, FUNCT3_AND, 1, 2, FUNCT7_ADD),  # AND x3, x1, x2
    encode_r_type(OPCODE_R, 4, FUNCT3_OR,  1, 2, FUNCT7_ADD),  # OR x4, x1, x2
    encode_r_type(OPCODE_R, 5, FUNCT3_XOR, 1, 2, FUNCT7_ADD),  # XOR x5, x1, x2
]
write_binary_file('src/test/programs/BinaryFile_forwarding_logical', prg6)

# Test 7: Shift operations
# ADDI x1, x0, 4     -> x1 = 4
# ADDI x2, x0, 2     -> x2 = 2
# SLL x3, x1, x2     -> x3 = 4 << 2 = 16
# SRL x4, x3, x2     -> x4 = 16 >> 2 = 4
prg7 = [
    encode_i_type(OPCODE_I, 1, FUNCT3_ADD, 0, 4),      # ADDI x1, x0, 4
    encode_i_type(OPCODE_I, 2, FUNCT3_ADD, 0, 2),      # ADDI x2, x0, 2
    encode_r_type(OPCODE_R, 3, FUNCT3_SLL, 1, 2, FUNCT7_ADD),  # SLL x3, x1, x2
    encode_r_type(OPCODE_R, 4, FUNCT3_SRL, 3, 2, FUNCT7_ADD),  # SRL x4, x3, x2
]
write_binary_file('src/test/programs/BinaryFile_forwarding_shift', prg7)

# Test 8: Comparison operations
# ADDI x1, x0, 10    -> x1 = 10
# ADDI x2, x0, 20    -> x2 = 20
# SLT x3, x1, x2     -> x3 = (10 < 20) = 1
# SLT x4, x2, x1     -> x4 = (20 < 10) = 0
prg8 = [
    encode_i_type(OPCODE_I, 1, FUNCT3_ADD, 0, 10),     # ADDI x1, x0, 10
    encode_i_type(OPCODE_I, 2, FUNCT3_ADD, 0, 20),     # ADDI x2, x0, 20
    encode_r_type(OPCODE_R, 3, FUNCT3_SLT, 1, 2, FUNCT7_ADD),  # SLT x3, x1, x2
    encode_r_type(OPCODE_R, 4, FUNCT3_SLT, 2, 1, FUNCT7_ADD),  # SLT x4, x2, x1
]
write_binary_file('src/test/programs/BinaryFile_forwarding_cmp', prg8)

# Test 9: No hazards (independent instructions)
# ADDI x1, x0, 100   -> x1 = 100
# ADDI x2, x0, 200   -> x2 = 200
# ADDI x3, x0, 300   -> x3 = 300
prg9 = [
    encode_i_type(OPCODE_I, 1, FUNCT3_ADD, 0, 100),    # ADDI x1, x0, 100
    encode_i_type(OPCODE_I, 2, FUNCT3_ADD, 0, 200),    # ADDI x2, x0, 200
    encode_i_type(OPCODE_I, 3, FUNCT3_ADD, 0, 300),    # ADDI x3, x0, 300
]
write_binary_file('src/test/programs/BinaryFile_no_hazard', prg9)

# Test 10: Multiple independent chains
# ADDI x1, x0, 5     -> x1 = 5
# ADDI x4, x0, 3     -> x4 = 3
# ADD x2, x1, x1     -> x2 = 10
# ADD x5, x4, x4     -> x5 = 6
# ADD x3, x2, x0     -> x3 = 10 (x2 from WB)
# ADD x6, x5, x0     -> x6 = 6 (x5 from WB)
prg10 = [
    encode_i_type(OPCODE_I, 1, FUNCT3_ADD, 0, 5),      # ADDI x1, x0, 5
    encode_i_type(OPCODE_I, 4, FUNCT3_ADD, 0, 3),      # ADDI x4, x0, 3
    encode_r_type(OPCODE_R, 2, FUNCT3_ADD, 1, 1, FUNCT7_ADD),  # ADD x2, x1, x1
    encode_r_type(OPCODE_R, 5, FUNCT3_ADD, 4, 4, FUNCT7_ADD),  # ADD x5, x4, x4
    encode_r_type(OPCODE_R, 3, FUNCT3_ADD, 2, 0, FUNCT7_ADD),  # ADD x3, x2, x0
    encode_r_type(OPCODE_R, 6, FUNCT3_ADD, 5, 0, FUNCT7_ADD),  # ADD x6, x5, x0
]
write_binary_file('src/test/programs/BinaryFile_multi_chains', prg10)

print("All binary test files generated successfully!")
