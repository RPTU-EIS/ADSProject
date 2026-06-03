circuit ALU :
  module ALU :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip operandA : UInt<32>, flip operandB : UInt<32>, flip operation : UInt<4>, aluResult : UInt<32>}

    node _shift_amount_T = and(io.operandB, UInt<5>("h1f")) @[ALU.scala 37:35]
    node shift_amount = bits(_shift_amount_T, 4, 0) @[ALU.scala 37:42]
    io.aluResult <= UInt<1>("h0") @[ALU.scala 39:16]
    node _T = eq(io.operation, UInt<1>("h0")) @[ALU.scala 41:21]
    when _T : @[ALU.scala 41:36]
      node _io_aluResult_T = add(io.operandA, io.operandB) @[ALU.scala 42:33]
      node _io_aluResult_T_1 = tail(_io_aluResult_T, 1) @[ALU.scala 42:33]
      io.aluResult <= _io_aluResult_T_1 @[ALU.scala 42:18]
    else :
      node _T_1 = eq(io.operation, UInt<1>("h1")) @[ALU.scala 44:25]
      when _T_1 : @[ALU.scala 44:39]
        node _io_aluResult_T_2 = sub(io.operandA, io.operandB) @[ALU.scala 45:33]
        node _io_aluResult_T_3 = tail(_io_aluResult_T_2, 1) @[ALU.scala 45:33]
        io.aluResult <= _io_aluResult_T_3 @[ALU.scala 45:18]
      else :
        node _T_2 = eq(io.operation, UInt<2>("h2")) @[ALU.scala 47:25]
        when _T_2 : @[ALU.scala 47:39]
          node _io_aluResult_T_4 = and(io.operandA, io.operandB) @[ALU.scala 48:33]
          io.aluResult <= _io_aluResult_T_4 @[ALU.scala 48:18]
        else :
          node _T_3 = eq(io.operation, UInt<2>("h3")) @[ALU.scala 50:25]
          when _T_3 : @[ALU.scala 50:38]
            node _io_aluResult_T_5 = or(io.operandA, io.operandB) @[ALU.scala 51:33]
            io.aluResult <= _io_aluResult_T_5 @[ALU.scala 51:18]
          else :
            node _T_4 = eq(io.operation, UInt<3>("h4")) @[ALU.scala 53:25]
            when _T_4 : @[ALU.scala 53:39]
              node _io_aluResult_T_6 = xor(io.operandA, io.operandB) @[ALU.scala 54:33]
              io.aluResult <= _io_aluResult_T_6 @[ALU.scala 54:18]
            else :
              node _T_5 = eq(io.operation, UInt<3>("h5")) @[ALU.scala 56:25]
              when _T_5 : @[ALU.scala 56:39]
                node _io_aluResult_T_7 = dshl(io.operandA, shift_amount) @[ALU.scala 57:33]
                io.aluResult <= _io_aluResult_T_7 @[ALU.scala 57:18]
              else :
                node _T_6 = eq(io.operation, UInt<3>("h6")) @[ALU.scala 59:25]
                when _T_6 : @[ALU.scala 59:39]
                  node _io_aluResult_T_8 = dshr(io.operandA, shift_amount) @[ALU.scala 60:33]
                  io.aluResult <= _io_aluResult_T_8 @[ALU.scala 60:18]
                else :
                  node _T_7 = eq(io.operation, UInt<3>("h7")) @[ALU.scala 62:25]
                  when _T_7 : @[ALU.scala 62:39]
                    node _io_aluResult_T_9 = asSInt(io.operandA) @[ALU.scala 63:34]
                    node _io_aluResult_T_10 = dshr(_io_aluResult_T_9, shift_amount) @[ALU.scala 63:41]
                    node _io_aluResult_T_11 = asUInt(_io_aluResult_T_10) @[ALU.scala 63:58]
                    io.aluResult <= _io_aluResult_T_11 @[ALU.scala 63:18]
                  else :
                    node _T_8 = eq(io.operation, UInt<4>("h8")) @[ALU.scala 65:25]
                    when _T_8 : @[ALU.scala 65:39]
                      node _T_9 = asSInt(io.operandA) @[ALU.scala 66:22]
                      node _T_10 = asSInt(io.operandB) @[ALU.scala 66:43]
                      node _T_11 = gt(_T_9, _T_10) @[ALU.scala 66:29]
                      when _T_11 : @[ALU.scala 66:50]
                        io.aluResult <= UInt<1>("h1") @[ALU.scala 67:20]

