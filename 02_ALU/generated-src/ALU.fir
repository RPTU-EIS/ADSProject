circuit ALU :
  module ALU :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip operandA : UInt<32>, flip operandB : UInt<32>, flip operation : UInt<4>, aluResult : UInt<32>}

    node _shift_amount_T = and(io.operandB, UInt<5>("h1f")) @[ALU.scala 39:35]
    node shift_amount = bits(_shift_amount_T, 4, 0) @[ALU.scala 39:42]
    io.aluResult <= UInt<1>("h0") @[ALU.scala 41:16]
    node _T = eq(io.operation, UInt<1>("h0")) @[ALU.scala 43:21]
    when _T : @[ALU.scala 43:36]
      node _io_aluResult_T = add(io.operandA, io.operandB) @[ALU.scala 44:33]
      node _io_aluResult_T_1 = tail(_io_aluResult_T, 1) @[ALU.scala 44:33]
      io.aluResult <= _io_aluResult_T_1 @[ALU.scala 44:18]
    else :
      node _T_1 = eq(io.operation, UInt<1>("h1")) @[ALU.scala 46:27]
      when _T_1 : @[ALU.scala 46:41]
        node _io_aluResult_T_2 = sub(io.operandA, io.operandB) @[ALU.scala 47:35]
        node _io_aluResult_T_3 = tail(_io_aluResult_T_2, 1) @[ALU.scala 47:35]
        io.aluResult <= _io_aluResult_T_3 @[ALU.scala 47:20]
      else :
        node _T_2 = eq(io.operation, UInt<2>("h2")) @[ALU.scala 49:27]
        when _T_2 : @[ALU.scala 49:41]
          node _io_aluResult_T_4 = and(io.operandA, io.operandB) @[ALU.scala 50:35]
          io.aluResult <= _io_aluResult_T_4 @[ALU.scala 50:20]
        else :
          node _T_3 = eq(io.operation, UInt<2>("h3")) @[ALU.scala 52:27]
          when _T_3 : @[ALU.scala 52:40]
            node _io_aluResult_T_5 = or(io.operandA, io.operandB) @[ALU.scala 53:35]
            io.aluResult <= _io_aluResult_T_5 @[ALU.scala 53:20]
          else :
            node _T_4 = eq(io.operation, UInt<3>("h4")) @[ALU.scala 55:27]
            when _T_4 : @[ALU.scala 55:41]
              node _io_aluResult_T_6 = xor(io.operandA, io.operandB) @[ALU.scala 56:35]
              io.aluResult <= _io_aluResult_T_6 @[ALU.scala 56:20]
            else :
              node _T_5 = eq(io.operation, UInt<3>("h5")) @[ALU.scala 58:27]
              when _T_5 : @[ALU.scala 58:41]
                node _io_aluResult_T_7 = dshl(io.operandA, shift_amount) @[ALU.scala 59:35]
                io.aluResult <= _io_aluResult_T_7 @[ALU.scala 59:20]
              else :
                node _T_6 = eq(io.operation, UInt<3>("h6")) @[ALU.scala 61:27]
                when _T_6 : @[ALU.scala 61:41]
                  node _io_aluResult_T_8 = dshr(io.operandA, shift_amount) @[ALU.scala 62:35]
                  io.aluResult <= _io_aluResult_T_8 @[ALU.scala 62:20]
                else :
                  node _T_7 = eq(io.operation, UInt<3>("h7")) @[ALU.scala 64:27]
                  when _T_7 : @[ALU.scala 64:41]
                    node _io_aluResult_T_9 = asSInt(io.operandA) @[ALU.scala 65:36]
                    node _io_aluResult_T_10 = dshr(_io_aluResult_T_9, shift_amount) @[ALU.scala 65:43]
                    node _io_aluResult_T_11 = asUInt(_io_aluResult_T_10) @[ALU.scala 65:60]
                    io.aluResult <= _io_aluResult_T_11 @[ALU.scala 65:20]
                  else :
                    node _T_8 = eq(io.operation, UInt<4>("h8")) @[ALU.scala 67:27]
                    when _T_8 : @[ALU.scala 67:41]
                      node _T_9 = asSInt(io.operandA) @[ALU.scala 68:24]
                      node _T_10 = asSInt(io.operandB) @[ALU.scala 68:45]
                      node _T_11 = gt(_T_9, _T_10) @[ALU.scala 68:31]
                      when _T_11 : @[ALU.scala 68:52]
                        io.aluResult <= UInt<1>("h1") @[ALU.scala 69:22]
                    else :
                      node _T_12 = eq(io.operation, UInt<4>("h9")) @[ALU.scala 72:27]
                      when _T_12 : @[ALU.scala 72:42]
                        node _T_13 = gt(io.operandA, io.operandB) @[ALU.scala 73:31]
                        when _T_13 : @[ALU.scala 73:52]
                          io.aluResult <= UInt<1>("h1") @[ALU.scala 74:22]
                      else :
                        node _T_14 = eq(io.operation, UInt<4>("ha")) @[ALU.scala 77:27]
                        when _T_14 : @[ALU.scala 77:43]
                          io.aluResult <= io.operandB @[ALU.scala 78:20]

