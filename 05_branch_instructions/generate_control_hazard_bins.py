#!/usr/bin/env python3
"""
RISC-V Binary Generator for Control Hazard Test Programs (Assignment 05)
Each program places wrong-path instructions immediately after a branch (no NOPs),
so the hardware flush mechanism must suppress their register writes.
"""

OPCODE_I      = 0x13  # ADDI
OPCODE_R      = 0x33  # ADD, SUB, etc.
OPCODE_BRANCH = 0x63
OPCODE_JAL    = 0x6F
OPCODE_JALR   = 0x67
FUNCT3_ADD    = 0x0
FUNCT7_ADD    = 0x00

def addi(rd, rs1, imm):
    imm = imm & 0xFFF
    return (imm << 20 | rs1 << 15 | FUNCT3_ADD << 12 | rd << 7 | OPCODE_I) & 0xFFFFFFFF

def add(rd, rs1, rs2):
    return (FUNCT7_ADD << 25 | rs2 << 20 | rs1 << 15 | FUNCT3_ADD << 12 | rd << 7 | OPCODE_R) & 0xFFFFFFFF

def branch(funct3, rs1, rs2, imm):
    # B-type: imm is a signed byte offset (bit 0 always 0)
    imm = imm & 0x1FFE
    inst = (((imm >> 12) & 1) << 31 |
            ((imm >> 5)  & 0x3F) << 25 |
            (rs2 & 0x1F) << 20 |
            (rs1 & 0x1F) << 15 |
            (funct3 & 0x7) << 12 |
            ((imm >> 1) & 0xF) << 8 |
            ((imm >> 11) & 1) << 7 |
            OPCODE_BRANCH)
    return inst & 0xFFFFFFFF

def beq(rs1, rs2, imm):  return branch(0b000, rs1, rs2, imm)
def bne(rs1, rs2, imm):  return branch(0b001, rs1, rs2, imm)
def blt(rs1, rs2, imm):  return branch(0b100, rs1, rs2, imm)
def bge(rs1, rs2, imm):  return branch(0b101, rs1, rs2, imm)
def bgeu(rs1, rs2, imm): return branch(0b111, rs1, rs2, imm)

def jal(rd, imm):
    # J-type
    imm = imm & 0x1FFFFE
    inst = (((imm >> 20) & 1)    << 31 |
            ((imm >> 1)  & 0x3FF) << 21 |
            ((imm >> 11) & 1)     << 20 |
            ((imm >> 12) & 0xFF)  << 12 |
            (rd & 0x1F)           << 7  |
            OPCODE_JAL)
    return inst & 0xFFFFFFFF

def jalr(rd, rs1, imm):
    imm = imm & 0xFFF
    return (imm << 20 | rs1 << 15 | 0 << 12 | rd << 7 | OPCODE_JALR) & 0xFFFFFFFF

NOP = addi(0, 0, 0)  # ADDI x0, x0, 0

def write(filename, instructions):
    with open(filename, 'w') as f:
        for inst in instructions:
            f.write(f"{inst:08x}\n")

# ──────────────────────────────────────────────────────────────
# Test 1: BEQ taken — 2 wrong-path writes to x10/x11 suppressed
# Sequence (bytes): ADDI x1,5 | ADDI x2,3 | BEQ x0,x0,+12 | ADDI x10,99* | ADDI x11,98* | ADD x3,x10,x11
# check_res WB order: 5, 3, 0, 99, 98, 0   (* write suppressed → x10=0, x11=0 → ADD=0)
# ──────────────────────────────────────────────────────────────
write('src/test/programs/BinaryFile_ch_beq_taken', [
    addi(1,  0,  5),   # x1 = 5
    addi(2,  0,  3),   # x2 = 3
    beq(0, 0, 12),     # taken (0==0) → jump to +12
    addi(10, 0, 99),   # WRONG PATH
    addi(11, 0, 98),   # WRONG PATH
    add(3, 10, 11),    # x3 = x10+x11 = 0+0 = 0 if flush worked
])

# ──────────────────────────────────────────────────────────────
# Test 2: BEQ not taken — instructions after branch execute normally
# check_res: 10, 20, 30, 7, 8, 15
# ──────────────────────────────────────────────────────────────
write('src/test/programs/BinaryFile_ch_beq_not_taken', [
    addi(1,  0, 10),   # x1 = 10
    addi(2,  0, 20),   # x2 = 20
    beq(1, 2, 12),     # NOT taken (10 ≠ 20); ALU=10+20=30 written to "rd"=x12
    addi(3,  0,  7),   # executes, x3 = 7
    addi(4,  0,  8),   # executes, x4 = 8
    add(5, 3, 4),      # x5 = 7+8 = 15
])

# ──────────────────────────────────────────────────────────────
# Test 3: BNE taken — unequal values, 2 wrong-path writes suppressed
# check_res: 4, 7, 11, 55, 44, 0
# ──────────────────────────────────────────────────────────────
write('src/test/programs/BinaryFile_ch_bne_taken', [
    addi(1,  0,  4),   # x1 = 4
    addi(2,  0,  7),   # x2 = 7
    bne(1, 2, 12),     # taken (4 ≠ 7); ALU=4+7=11
    addi(10, 0, 55),   # WRONG PATH
    addi(11, 0, 44),   # WRONG PATH
    add(3, 10, 11),    # x3 = 0+0 = 0 if flush worked
])

# ──────────────────────────────────────────────────────────────
# Test 4: BNE not taken — equal values, instructions execute
# check_res: 5, 5, 10, 11, 22, 33
# ──────────────────────────────────────────────────────────────
write('src/test/programs/BinaryFile_ch_bne_not_taken', [
    addi(1,  0,  5),   # x1 = 5
    addi(2,  0,  5),   # x2 = 5
    bne(1, 2, 12),     # NOT taken (5 == 5); ALU=5+5=10
    addi(3,  0, 11),   # executes, x3 = 11
    addi(4,  0, 22),   # executes, x4 = 22
    add(5, 3, 4),      # x5 = 33
])

# ──────────────────────────────────────────────────────────────
# Test 5: BLT taken — signed less-than, 2 wrong-path writes suppressed
# check_res: 3, 9, 12, 77, 66, 0
# ──────────────────────────────────────────────────────────────
write('src/test/programs/BinaryFile_ch_blt_taken', [
    addi(1,  0,  3),   # x1 = 3
    addi(2,  0,  9),   # x2 = 9
    blt(1, 2, 12),     # taken (3 < 9 signed); ALU=3+9=12
    addi(10, 0, 77),   # WRONG PATH
    addi(11, 0, 66),   # WRONG PATH
    add(3, 10, 11),    # x3 = 0+0 = 0 if flush worked
])

# ──────────────────────────────────────────────────────────────
# Test 6: BGE taken — signed greater-or-equal, 2 wrong-path writes suppressed
# check_res: 9, 3, 12, 33, 22, 0
# ──────────────────────────────────────────────────────────────
write('src/test/programs/BinaryFile_ch_bge_taken', [
    addi(1,  0,  9),   # x1 = 9
    addi(2,  0,  3),   # x2 = 3
    bge(1, 2, 12),     # taken (9 >= 3 signed); ALU=9+3=12
    addi(10, 0, 33),   # WRONG PATH
    addi(11, 0, 22),   # WRONG PATH
    add(3, 10, 11),    # x3 = 0+0 = 0 if flush worked
])

# ──────────────────────────────────────────────────────────────
# Test 7: BGEU taken — unsigned greater-or-equal
# check_res: 5, 2, 7, 55, 44, 0
# ──────────────────────────────────────────────────────────────
write('src/test/programs/BinaryFile_ch_bgeu_taken', [
    addi(1,  0,  5),   # x1 = 5
    addi(2,  0,  2),   # x2 = 2
    bgeu(1, 2, 12),    # taken (5 >= 2 unsigned); ALU=5+2=7
    addi(10, 0, 55),   # WRONG PATH
    addi(11, 0, 44),   # WRONG PATH
    add(3, 10, 11),    # x3 = 0+0 = 0 if flush worked
])

# ──────────────────────────────────────────────────────────────
# Test 8: JAL — unconditional jump, writes return addr to x3
# JAL at PC=8 → x3 = 12 (PC+4), jump to PC=20
# check_res: 5, 3, 12, 99, 88, 0
# ──────────────────────────────────────────────────────────────
write('src/test/programs/BinaryFile_ch_jal', [
    addi(1,  0,  5),   # x1 = 5
    addi(2,  0,  3),   # x2 = 3
    jal(3, 12),        # jump to PC+12=20; x3 = PC+4 = 12
    addi(10, 0, 99),   # WRONG PATH
    addi(11, 0, 88),   # WRONG PATH
    add(4, 10, 11),    # x4 = 0+0 = 0 if flush worked
])

# ──────────────────────────────────────────────────────────────
# Test 9: JALR — jump to register+offset, writes return addr
# JALR x3, x0, 20 → target = (0+20)&~1 = 20; x3 = PC+4 = 12
# check_res: 5, 3, 12, 99, 88, 0
# ──────────────────────────────────────────────────────────────
write('src/test/programs/BinaryFile_ch_jalr', [
    addi(1,  0,  5),   # x1 = 5
    addi(2,  0,  3),   # x2 = 3
    jalr(3, 0, 20),    # jump to x0+20=20; x3 = 12
    addi(10, 0, 99),   # WRONG PATH
    addi(11, 0, 88),   # WRONG PATH
    add(4, 10, 11),    # x4 = 0+0 = 0 if flush worked
])

# ──────────────────────────────────────────────────────────────
# Test 10: Forwarding + branch hazard
# BEQ uses values forwarded from EXBarrier/MEMBarrier, then flushes wrong-path.
# x1 and x2 are set immediately before BEQ — their values are forwarded into
# the branch comparison unit in EX stage.
# check_res: 10, 10, 20, 55, 44, 42, 42
# ──────────────────────────────────────────────────────────────
write('src/test/programs/BinaryFile_ch_fwd_branch', [
    addi(1,  0, 10),   # x1 = 10  (forwarded from MEMBarrier when BEQ in EX)
    addi(2,  0, 10),   # x2 = 10  (forwarded from EXBarrier  when BEQ in EX)
    beq(1, 2, 12),     # taken (10==10 via forwarding); ALU=10+10=20
    addi(10, 0, 55),   # WRONG PATH
    addi(11, 0, 44),   # WRONG PATH
    addi(3,  0, 42),   # x3 = 42 (at branch target)
    add(4, 3, 10),     # x4 = 42+0 = 42 if x10 was correctly suppressed
])

print("All control hazard binary files generated successfully!")
