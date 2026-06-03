circuit ALU :
  module ALU :
    input clock : Clock
    input reset : UInt<1>
    input io_operandA : UInt<32>
    input io_operandB : UInt<32>
    input io_operation : UInt<4>
    output io_aluResult : UInt<32>

    node _shift_amount_T = and(io_operandB, UInt<5>("h1f")) @[ALU.scala 37:35]
    node shift_amount = bits(_shift_amount_T, 4, 0) @[ALU.scala 37:42]
    node _T = eq(io_operation, UInt<1>("h0")) @[ALU.scala 41:21]
    node _io_aluResult_T = add(io_operandA, io_operandB) @[ALU.scala 42:33]
    node _io_aluResult_T_1 = tail(_io_aluResult_T, 1) @[ALU.scala 42:33]
    node _T_1 = eq(io_operation, UInt<1>("h1")) @[ALU.scala 44:25]
    node _io_aluResult_T_2 = sub(io_operandA, io_operandB) @[ALU.scala 45:33]
    node _io_aluResult_T_3 = tail(_io_aluResult_T_2, 1) @[ALU.scala 45:33]
    node _T_2 = eq(io_operation, UInt<2>("h2")) @[ALU.scala 47:25]
    node _io_aluResult_T_4 = and(io_operandA, io_operandB) @[ALU.scala 48:33]
    node _T_3 = eq(io_operation, UInt<2>("h3")) @[ALU.scala 50:25]
    node _io_aluResult_T_5 = or(io_operandA, io_operandB) @[ALU.scala 51:33]
    node _T_4 = eq(io_operation, UInt<3>("h4")) @[ALU.scala 53:25]
    node _io_aluResult_T_6 = xor(io_operandA, io_operandB) @[ALU.scala 54:33]
    node _T_5 = eq(io_operation, UInt<3>("h5")) @[ALU.scala 56:25]
    node _io_aluResult_T_7 = dshl(io_operandA, shift_amount) @[ALU.scala 57:33]
    node _T_6 = eq(io_operation, UInt<3>("h6")) @[ALU.scala 59:25]
    node _io_aluResult_T_8 = dshr(io_operandA, shift_amount) @[ALU.scala 60:33]
    node _T_7 = eq(io_operation, UInt<3>("h7")) @[ALU.scala 62:25]
    node _io_aluResult_T_9 = asSInt(io_operandA) @[ALU.scala 63:34]
    node _io_aluResult_T_10 = dshr(_io_aluResult_T_9, shift_amount) @[ALU.scala 63:41]
    node _io_aluResult_T_11 = asUInt(_io_aluResult_T_10) @[ALU.scala 63:58]
    node _T_8 = eq(io_operation, UInt<4>("h8")) @[ALU.scala 65:25]
    node _T_9 = asSInt(io_operandA) @[ALU.scala 66:22]
    node _T_10 = asSInt(io_operandB) @[ALU.scala 66:43]
    node _T_11 = gt(_T_9, _T_10) @[ALU.scala 66:29]
    node _GEN_0 = mux(_T_11, UInt<1>("h1"), UInt<1>("h0")) @[ALU.scala 39:16 66:50 67:20]
    node _GEN_1 = mux(_T_8, _GEN_0, UInt<1>("h0")) @[ALU.scala 39:16 65:39]
    node _GEN_2 = mux(_T_7, _io_aluResult_T_11, _GEN_1) @[ALU.scala 62:39 63:18]
    node _GEN_3 = mux(_T_6, _io_aluResult_T_8, _GEN_2) @[ALU.scala 59:39 60:18]
    node _GEN_4 = mux(_T_5, _io_aluResult_T_7, _GEN_3) @[ALU.scala 56:39 57:18]
    node _GEN_5 = mux(_T_4, _io_aluResult_T_6, _GEN_4) @[ALU.scala 53:39 54:18]
    node _GEN_6 = mux(_T_3, _io_aluResult_T_5, _GEN_5) @[ALU.scala 50:38 51:18]
    node _GEN_7 = mux(_T_2, _io_aluResult_T_4, _GEN_6) @[ALU.scala 47:39 48:18]
    node _GEN_8 = mux(_T_1, _io_aluResult_T_3, _GEN_7) @[ALU.scala 44:39 45:18]
    node _GEN_9 = mux(_T, _io_aluResult_T_1, _GEN_8) @[ALU.scala 41:36 42:18]
    io_aluResult <= bits(_GEN_9, 31, 0)
