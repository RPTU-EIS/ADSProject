circuit ALU :
  module ALU :
    input clock : Clock
    input reset : UInt<1>
    input io_operandA : UInt<32>
    input io_operandB : UInt<32>
    input io_operation : UInt<3>
    output io_aluResult : UInt<32>

    node _T = eq(io_operation, UInt<1>("h0")) @[ALU.scala 35:21]
    node _io_aluResult_T = add(io_operandA, io_operandB) @[ALU.scala 36:33]
    node _io_aluResult_T_1 = tail(_io_aluResult_T, 1) @[ALU.scala 36:33]
    node _T_1 = eq(io_operation, UInt<1>("h1")) @[ALU.scala 38:25]
    node _io_aluResult_T_2 = sub(io_operandA, io_operandB) @[ALU.scala 39:33]
    node _io_aluResult_T_3 = tail(_io_aluResult_T_2, 1) @[ALU.scala 39:33]
    node _T_2 = eq(io_operation, UInt<2>("h2")) @[ALU.scala 41:25]
    node _io_aluResult_T_4 = and(io_operandA, io_operandB) @[ALU.scala 42:33]
    node _T_3 = eq(io_operation, UInt<2>("h3")) @[ALU.scala 44:25]
    node _io_aluResult_T_5 = or(io_operandA, io_operandB) @[ALU.scala 45:33]
    node _T_4 = eq(io_operation, UInt<3>("h4")) @[ALU.scala 47:25]
    node _io_aluResult_T_6 = xor(io_operandA, io_operandB) @[ALU.scala 48:33]
    node _GEN_0 = mux(_T_4, _io_aluResult_T_6, UInt<1>("h0")) @[ALU.scala 33:16 47:39 48:18]
    node _GEN_1 = mux(_T_3, _io_aluResult_T_5, _GEN_0) @[ALU.scala 44:38 45:18]
    node _GEN_2 = mux(_T_2, _io_aluResult_T_4, _GEN_1) @[ALU.scala 41:39 42:18]
    node _GEN_3 = mux(_T_1, _io_aluResult_T_3, _GEN_2) @[ALU.scala 38:39 39:18]
    node _GEN_4 = mux(_T, _io_aluResult_T_1, _GEN_3) @[ALU.scala 35:36 36:18]
    io_aluResult <= _GEN_4
