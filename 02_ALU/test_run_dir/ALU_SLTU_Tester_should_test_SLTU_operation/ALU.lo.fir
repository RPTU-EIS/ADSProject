circuit ALU :
  module ALU :
    input clock : Clock
    input reset : UInt<1>
    input io_operandA : UInt<32>
    input io_operandB : UInt<32>
    input io_operation : UInt<4>
    output io_aluResult : UInt<32>

    node _shift_amount_T = and(io_operandB, UInt<5>("h1f")) @[ALU.scala 39:35]
    node shift_amount = bits(_shift_amount_T, 4, 0) @[ALU.scala 39:42]
    node _T = eq(io_operation, UInt<1>("h0")) @[ALU.scala 43:21]
    node _io_aluResult_T = add(io_operandA, io_operandB) @[ALU.scala 44:33]
    node _io_aluResult_T_1 = tail(_io_aluResult_T, 1) @[ALU.scala 44:33]
    node _T_1 = eq(io_operation, UInt<1>("h1")) @[ALU.scala 46:25]
    node _io_aluResult_T_2 = sub(io_operandA, io_operandB) @[ALU.scala 47:33]
    node _io_aluResult_T_3 = tail(_io_aluResult_T_2, 1) @[ALU.scala 47:33]
    node _T_2 = eq(io_operation, UInt<2>("h2")) @[ALU.scala 49:25]
    node _io_aluResult_T_4 = and(io_operandA, io_operandB) @[ALU.scala 50:33]
    node _T_3 = eq(io_operation, UInt<2>("h3")) @[ALU.scala 52:25]
    node _io_aluResult_T_5 = or(io_operandA, io_operandB) @[ALU.scala 53:33]
    node _T_4 = eq(io_operation, UInt<3>("h4")) @[ALU.scala 55:25]
    node _io_aluResult_T_6 = xor(io_operandA, io_operandB) @[ALU.scala 56:33]
    node _T_5 = eq(io_operation, UInt<3>("h5")) @[ALU.scala 58:25]
    node _io_aluResult_T_7 = dshl(io_operandA, shift_amount) @[ALU.scala 59:33]
    node _T_6 = eq(io_operation, UInt<3>("h6")) @[ALU.scala 61:25]
    node _io_aluResult_T_8 = dshr(io_operandA, shift_amount) @[ALU.scala 62:33]
    node _T_7 = eq(io_operation, UInt<3>("h7")) @[ALU.scala 64:25]
    node _io_aluResult_T_9 = asSInt(io_operandA) @[ALU.scala 65:34]
    node _io_aluResult_T_10 = dshr(_io_aluResult_T_9, shift_amount) @[ALU.scala 65:41]
    node _io_aluResult_T_11 = asUInt(_io_aluResult_T_10) @[ALU.scala 65:58]
    node _T_8 = eq(io_operation, UInt<4>("h8")) @[ALU.scala 67:25]
    node _T_9 = asSInt(io_operandA) @[ALU.scala 68:22]
    node _T_10 = asSInt(io_operandB) @[ALU.scala 68:43]
    node _T_11 = gt(_T_9, _T_10) @[ALU.scala 68:29]
    node _GEN_0 = mux(_T_11, UInt<1>("h1"), UInt<1>("h0")) @[ALU.scala 41:16 68:50 69:20]
    node _T_12 = eq(io_operation, UInt<4>("h9")) @[ALU.scala 72:25]
    node _T_13 = gt(io_operandA, io_operandB) @[ALU.scala 73:29]
    node _GEN_1 = mux(_T_13, UInt<1>("h1"), UInt<1>("h0")) @[ALU.scala 41:16 73:50 74:20]
    node _T_14 = eq(io_operation, UInt<4>("ha")) @[ALU.scala 77:25]
    node _GEN_2 = mux(_T_14, io_operandB, UInt<1>("h0")) @[ALU.scala 41:16 77:41 78:18]
    node _GEN_3 = mux(_T_12, _GEN_1, _GEN_2) @[ALU.scala 72:40]
    node _GEN_4 = mux(_T_8, _GEN_0, _GEN_3) @[ALU.scala 67:39]
    node _GEN_5 = mux(_T_7, _io_aluResult_T_11, _GEN_4) @[ALU.scala 64:39 65:18]
    node _GEN_6 = mux(_T_6, _io_aluResult_T_8, _GEN_5) @[ALU.scala 61:39 62:18]
    node _GEN_7 = mux(_T_5, _io_aluResult_T_7, _GEN_6) @[ALU.scala 58:39 59:18]
    node _GEN_8 = mux(_T_4, _io_aluResult_T_6, _GEN_7) @[ALU.scala 55:39 56:18]
    node _GEN_9 = mux(_T_3, _io_aluResult_T_5, _GEN_8) @[ALU.scala 52:38 53:18]
    node _GEN_10 = mux(_T_2, _io_aluResult_T_4, _GEN_9) @[ALU.scala 49:39 50:18]
    node _GEN_11 = mux(_T_1, _io_aluResult_T_3, _GEN_10) @[ALU.scala 46:39 47:18]
    node _GEN_12 = mux(_T, _io_aluResult_T_1, _GEN_11) @[ALU.scala 43:36 44:18]
    io_aluResult <= bits(_GEN_12, 31, 0)
