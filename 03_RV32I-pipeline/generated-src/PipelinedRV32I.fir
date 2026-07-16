circuit PipelinedRV32I :
  module IF :
    input clock : Clock
    input reset : Reset
    output io : { inst : UInt<32>}

    cmem IMem : UInt<32> [4096] @[IFstage.scala 48:19]
    reg PC : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[IFstage.scala 51:21]
    node addr = shr(PC, 2) @[IFstage.scala 53:19]
    node _io_inst_T = bits(addr, 11, 0) @[IFstage.scala 55:25]
    infer mport io_inst_MPORT = IMem[_io_inst_T], clock @[IFstage.scala 55:20]
    io.inst <= io_inst_MPORT @[IFstage.scala 55:13]
    node _PC_T = add(PC, UInt<3>("h4")) @[IFstage.scala 57:14]
    node _PC_T_1 = tail(_PC_T, 1) @[IFstage.scala 57:14]
    PC <= _PC_T_1 @[IFstage.scala 57:8]

  module IFBarrier :
    input clock : Clock
    input reset : Reset
    output io : { flip inInstr : UInt<32>, outInstr : UInt<32>}

    reg instrReg : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[IFbarrier.scala 38:25]
    instrReg <= io.inInstr @[IFbarrier.scala 40:15]
    io.outInstr <= instrReg @[IFbarrier.scala 42:15]

  module regFile :
    input clock : Clock
    input reset : Reset
    output io : { flip req_1 : { addr : UInt<5>}, flip req_2 : { addr : UInt<5>}, flip req_3 : { addr : UInt<5>, data : UInt<32>, w_en : UInt<1>}, resp_1 : { data : UInt<32>}, resp_2 : { data : UInt<32>}}

    wire _regFile_WIRE : UInt<32>[32] @[RegisterFile.scala 63:34]
    _regFile_WIRE[0] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[1] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[2] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[3] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[4] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[5] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[6] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[7] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[8] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[9] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[10] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[11] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[12] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[13] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[14] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[15] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[16] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[17] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[18] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[19] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[20] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[21] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[22] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[23] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[24] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[25] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[26] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[27] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[28] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[29] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[30] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    _regFile_WIRE[31] <= UInt<32>("h0") @[RegisterFile.scala 63:34]
    reg regFile : UInt<32>[32], clock with :
      reset => (reset, _regFile_WIRE) @[RegisterFile.scala 63:26]
    node _io_resp_1_data_T = eq(io.req_1.addr, UInt<1>("h0")) @[RegisterFile.scala 65:41]
    node _io_resp_1_data_T_1 = mux(_io_resp_1_data_T, UInt<1>("h0"), regFile[io.req_1.addr]) @[RegisterFile.scala 65:26]
    io.resp_1.data <= _io_resp_1_data_T_1 @[RegisterFile.scala 65:20]
    node _io_resp_2_data_T = eq(io.req_2.addr, UInt<1>("h0")) @[RegisterFile.scala 66:41]
    node _io_resp_2_data_T_1 = mux(_io_resp_2_data_T, UInt<1>("h0"), regFile[io.req_2.addr]) @[RegisterFile.scala 66:26]
    io.resp_2.data <= _io_resp_2_data_T_1 @[RegisterFile.scala 66:20]
    node _T = neq(io.req_3.addr, UInt<1>("h0")) @[RegisterFile.scala 68:41]
    node _T_1 = and(io.req_3.w_en, _T) @[RegisterFile.scala 68:24]
    when _T_1 : @[RegisterFile.scala 68:50]
      regFile[io.req_3.addr] <= io.req_3.data @[RegisterFile.scala 69:32]

  module ControlUnit :
    input clock : Clock
    input reset : Reset
    output io : { flip opcode : UInt<7>, flip funct3 : UInt<3>, flip funct7 : UInt<7>, uop : UInt<5>, ALUsrc : UInt<1>, immSel : UInt<1>, XcptInvalid : UInt<1>}

    io.uop <= UInt<5>("h14") @[IDstage.scala 63:20]
    io.ALUsrc <= UInt<1>("h0") @[IDstage.scala 64:20]
    io.immSel <= UInt<1>("h0") @[IDstage.scala 65:20]
    io.XcptInvalid <= UInt<1>("h1") @[IDstage.scala 66:20]
    node _T = eq(UInt<6>("h33"), io.opcode) @[IDstage.scala 68:22]
    when _T : @[IDstage.scala 68:22]
      node _T_1 = eq(io.funct7, UInt<1>("h0")) @[IDstage.scala 70:28]
      when _T_1 : @[IDstage.scala 70:45]
        node _T_2 = eq(UInt<1>("h0"), io.funct3) @[IDstage.scala 71:34]
        when _T_2 : @[IDstage.scala 71:34]
          io.uop <= UInt<1>("h0") @[IDstage.scala 72:42]
          io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 72:71]
        else :
          node _T_3 = eq(UInt<1>("h1"), io.funct3) @[IDstage.scala 71:34]
          when _T_3 : @[IDstage.scala 71:34]
            io.uop <= UInt<3>("h5") @[IDstage.scala 73:42]
            io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 73:71]
          else :
            node _T_4 = eq(UInt<2>("h2"), io.funct3) @[IDstage.scala 71:34]
            when _T_4 : @[IDstage.scala 71:34]
              io.uop <= UInt<4>("h8") @[IDstage.scala 74:42]
              io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 74:71]
            else :
              node _T_5 = eq(UInt<2>("h3"), io.funct3) @[IDstage.scala 71:34]
              when _T_5 : @[IDstage.scala 71:34]
                io.uop <= UInt<4>("h9") @[IDstage.scala 75:42]
                io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 75:71]
              else :
                node _T_6 = eq(UInt<3>("h4"), io.funct3) @[IDstage.scala 71:34]
                when _T_6 : @[IDstage.scala 71:34]
                  io.uop <= UInt<3>("h4") @[IDstage.scala 76:42]
                  io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 76:71]
                else :
                  node _T_7 = eq(UInt<3>("h5"), io.funct3) @[IDstage.scala 71:34]
                  when _T_7 : @[IDstage.scala 71:34]
                    io.uop <= UInt<3>("h6") @[IDstage.scala 77:42]
                    io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 77:71]
                  else :
                    node _T_8 = eq(UInt<3>("h6"), io.funct3) @[IDstage.scala 71:34]
                    when _T_8 : @[IDstage.scala 71:34]
                      io.uop <= UInt<2>("h3") @[IDstage.scala 78:42]
                      io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 78:71]
                    else :
                      node _T_9 = eq(UInt<3>("h7"), io.funct3) @[IDstage.scala 71:34]
                      when _T_9 : @[IDstage.scala 71:34]
                        io.uop <= UInt<2>("h2") @[IDstage.scala 79:42]
                        io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 79:71]
      else :
        node _T_10 = eq(io.funct7, UInt<6>("h20")) @[IDstage.scala 81:34]
        when _T_10 : @[IDstage.scala 81:51]
          node _T_11 = eq(UInt<1>("h0"), io.funct3) @[IDstage.scala 82:34]
          when _T_11 : @[IDstage.scala 82:34]
            io.uop <= UInt<1>("h1") @[IDstage.scala 83:42]
            io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 83:70]
          else :
            node _T_12 = eq(UInt<3>("h5"), io.funct3) @[IDstage.scala 82:34]
            when _T_12 : @[IDstage.scala 82:34]
              io.uop <= UInt<3>("h7") @[IDstage.scala 84:42]
              io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 84:70]
    else :
      node _T_13 = eq(UInt<5>("h13"), io.opcode) @[IDstage.scala 68:22]
      when _T_13 : @[IDstage.scala 68:22]
        io.ALUsrc <= UInt<1>("h1") @[IDstage.scala 89:23]
        node _T_14 = eq(UInt<1>("h0"), io.funct3) @[IDstage.scala 90:30]
        when _T_14 : @[IDstage.scala 90:30]
          io.uop <= UInt<4>("hb") @[IDstage.scala 91:38]
          io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 91:68]
        else :
          node _T_15 = eq(UInt<2>("h2"), io.funct3) @[IDstage.scala 90:30]
          when _T_15 : @[IDstage.scala 90:30]
            io.uop <= UInt<4>("ha") @[IDstage.scala 92:38]
            io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 92:68]
          else :
            node _T_16 = eq(UInt<2>("h3"), io.funct3) @[IDstage.scala 90:30]
            when _T_16 : @[IDstage.scala 90:30]
              io.uop <= UInt<5>("h10") @[IDstage.scala 93:38]
              io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 93:68]
            else :
              node _T_17 = eq(UInt<3>("h4"), io.funct3) @[IDstage.scala 90:30]
              when _T_17 : @[IDstage.scala 90:30]
                io.uop <= UInt<4>("hc") @[IDstage.scala 94:38]
                io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 94:68]
              else :
                node _T_18 = eq(UInt<3>("h6"), io.funct3) @[IDstage.scala 90:30]
                when _T_18 : @[IDstage.scala 90:30]
                  io.uop <= UInt<4>("hd") @[IDstage.scala 95:38]
                  io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 95:68]
                else :
                  node _T_19 = eq(UInt<3>("h7"), io.funct3) @[IDstage.scala 90:30]
                  when _T_19 : @[IDstage.scala 90:30]
                    io.uop <= UInt<4>("he") @[IDstage.scala 96:38]
                    io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 96:68]
                  else :
                    node _T_20 = eq(UInt<1>("h1"), io.funct3) @[IDstage.scala 90:30]
                    when _T_20 : @[IDstage.scala 90:30]
                      node _T_21 = eq(io.funct7, UInt<1>("h0")) @[IDstage.scala 98:36]
                      when _T_21 : @[IDstage.scala 98:53]
                        io.uop <= UInt<4>("hf") @[IDstage.scala 99:32]
                        io.immSel <= UInt<1>("h1") @[IDstage.scala 99:56]
                        io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 99:82]
                    else :
                      node _T_22 = eq(UInt<3>("h5"), io.funct3) @[IDstage.scala 90:30]
                      when _T_22 : @[IDstage.scala 90:30]
                        node _T_23 = eq(io.funct7, UInt<1>("h0")) @[IDstage.scala 103:36]
                        when _T_23 : @[IDstage.scala 103:53]
                          io.uop <= UInt<5>("h11") @[IDstage.scala 104:32]
                          io.immSel <= UInt<1>("h1") @[IDstage.scala 104:56]
                          io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 104:82]
                        else :
                          node _T_24 = eq(io.funct7, UInt<6>("h20")) @[IDstage.scala 105:42]
                          when _T_24 : @[IDstage.scala 105:59]
                            io.uop <= UInt<5>("h12") @[IDstage.scala 106:32]
                            io.immSel <= UInt<1>("h1") @[IDstage.scala 106:56]
                            io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 106:82]

  module SignExtend :
    input clock : Clock
    input reset : Reset
    output io : { flip imm_in : UInt<12>, flip sel : UInt<1>, imm_out : UInt<32>}

    node _full_T = bits(io.imm_in, 11, 11) @[IDstage.scala 120:39]
    node _full_T_1 = bits(_full_T, 0, 0) @[Bitwise.scala 74:15]
    node _full_T_2 = mux(_full_T_1, UInt<20>("hfffff"), UInt<20>("h0")) @[Bitwise.scala 74:12]
    node full = cat(_full_T_2, io.imm_in) @[Cat.scala 31:58]
    node _shamt_T = bits(io.imm_in, 4, 0) @[IDstage.scala 121:41]
    node shamt = cat(UInt<27>("h0"), _shamt_T) @[Cat.scala 31:58]
    node _io_imm_out_T = mux(io.sel, shamt, full) @[IDstage.scala 122:22]
    io.imm_out <= _io_imm_out_T @[IDstage.scala 122:16]

  module ID :
    input clock : Clock
    input reset : Reset
    output io : { flip inst : UInt<32>, flip w_en : UInt<1>, flip rd_in : UInt<5>, flip write_data : UInt<32>, uop : UInt<5>, wrten : UInt<1>, ALUsrc : UInt<1>, immExtnd : UInt<32>, XcptInvalid : UInt<1>, rd_out : UInt<5>, operandA : UInt<32>, operandB : UInt<32>}

    node opcode = bits(io.inst, 6, 0) @[IDstage.scala 143:25]
    node funct3 = bits(io.inst, 14, 12) @[IDstage.scala 144:25]
    node funct7 = bits(io.inst, 31, 25) @[IDstage.scala 145:25]
    node rs1 = bits(io.inst, 19, 15) @[IDstage.scala 146:25]
    node rs2 = bits(io.inst, 24, 20) @[IDstage.scala 147:25]
    node rd = bits(io.inst, 11, 7) @[IDstage.scala 148:25]
    node imm12 = bits(io.inst, 31, 20) @[IDstage.scala 149:25]
    inst rf of regFile @[IDstage.scala 151:22]
    rf.clock <= clock
    rf.reset <= reset
    inst cu of ControlUnit @[IDstage.scala 152:22]
    cu.clock <= clock
    cu.reset <= reset
    inst sigex of SignExtend @[IDstage.scala 153:23]
    sigex.clock <= clock
    sigex.reset <= reset
    cu.io.opcode <= opcode @[IDstage.scala 156:18]
    cu.io.funct3 <= funct3 @[IDstage.scala 157:18]
    cu.io.funct7 <= funct7 @[IDstage.scala 158:18]
    sigex.io.imm_in <= imm12 @[IDstage.scala 161:21]
    sigex.io.sel <= cu.io.immSel @[IDstage.scala 162:21]
    rf.io.req_1.addr <= rs1 @[IDstage.scala 165:22]
    rf.io.req_2.addr <= rs2 @[IDstage.scala 166:22]
    rf.io.req_3.addr <= io.rd_in @[IDstage.scala 167:22]
    rf.io.req_3.w_en <= io.w_en @[IDstage.scala 168:22]
    rf.io.req_3.data <= io.write_data @[IDstage.scala 169:22]
    io.operandA <= rf.io.resp_1.data @[IDstage.scala 172:20]
    io.operandB <= rf.io.resp_2.data @[IDstage.scala 173:20]
    io.rd_out <= rd @[IDstage.scala 174:20]
    io.uop <= cu.io.uop @[IDstage.scala 175:20]
    io.XcptInvalid <= cu.io.XcptInvalid @[IDstage.scala 176:20]
    io.immExtnd <= sigex.io.imm_out @[IDstage.scala 177:20]
    io.wrten <= UInt<1>("h1") @[IDstage.scala 178:20]
    io.ALUsrc <= cu.io.ALUsrc @[IDstage.scala 179:21]

  module IDBarrier :
    input clock : Clock
    input reset : Reset
    output io : { flip inUOP : UInt<5>, flip inRD : UInt<5>, flip inOperandA : UInt<32>, flip inOperandB : UInt<32>, flip inXcptInvalid : UInt<1>, flip inWrten : UInt<1>, flip inALUsrc : UInt<1>, flip inImmExtnd : UInt<32>, outUOP : UInt<5>, outRD : UInt<5>, outOperandA : UInt<32>, outOperandB : UInt<32>, outXcptInvalid : UInt<1>, outWrten : UInt<1>, outALUsrc : UInt<1>, outImmExtnd : UInt<32>}

    reg uop : UInt<5>, clock with :
      reset => (reset, UInt<5>("h14")) @[IDbarrier.scala 65:27]
    reg XcptInvalid : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[IDbarrier.scala 66:31]
    reg rd : UInt<5>, clock with :
      reset => (reset, UInt<5>("h0")) @[IDbarrier.scala 67:27]
    reg operandA : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[IDbarrier.scala 68:27]
    reg operandB : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[IDbarrier.scala 69:27]
    reg wrten : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[IDbarrier.scala 70:30]
    reg ALUsrc : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[IDbarrier.scala 71:30]
    reg immExtnd : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[IDbarrier.scala 72:30]
    uop <= io.inUOP @[IDbarrier.scala 74:17]
    rd <= io.inRD @[IDbarrier.scala 75:17]
    operandA <= io.inOperandA @[IDbarrier.scala 76:17]
    operandB <= io.inOperandB @[IDbarrier.scala 77:17]
    XcptInvalid <= io.inXcptInvalid @[IDbarrier.scala 78:17]
    wrten <= io.inWrten @[IDbarrier.scala 79:17]
    ALUsrc <= io.inALUsrc @[IDbarrier.scala 80:17]
    immExtnd <= io.inImmExtnd @[IDbarrier.scala 81:17]
    io.outUOP <= uop @[IDbarrier.scala 83:24]
    io.outXcptInvalid <= XcptInvalid @[IDbarrier.scala 84:24]
    io.outRD <= rd @[IDbarrier.scala 85:24]
    io.outOperandA <= operandA @[IDbarrier.scala 86:24]
    io.outOperandB <= operandB @[IDbarrier.scala 87:24]
    io.outWrten <= wrten @[IDbarrier.scala 88:24]
    io.outALUsrc <= ALUsrc @[IDbarrier.scala 89:24]
    io.outImmExtnd <= immExtnd @[IDbarrier.scala 90:24]

  module ALU :
    input clock : Clock
    input reset : Reset
    output io : { flip operandA : UInt<32>, flip operandB : UInt<32>, flip operation : UInt<4>, aluResult : UInt<32>}

    node shift_amount = bits(io.operandB, 4, 0) @[ALU.scala 42:33]
    io.aluResult <= UInt<1>("h0") @[ALU.scala 44:16]
    node _T = asUInt(UInt<1>("h0")) @[ALU.scala 48:24]
    node _T_1 = asUInt(io.operation) @[ALU.scala 48:24]
    node _T_2 = eq(_T, _T_1) @[ALU.scala 48:24]
    when _T_2 : @[ALU.scala 48:24]
      node _io_aluResult_T = add(io.operandA, io.operandB) @[ALU.scala 52:35]
      node _io_aluResult_T_1 = tail(_io_aluResult_T, 1) @[ALU.scala 52:35]
      io.aluResult <= _io_aluResult_T_1 @[ALU.scala 52:20]
    else :
      node _T_3 = asUInt(UInt<1>("h1")) @[ALU.scala 48:24]
      node _T_4 = asUInt(io.operation) @[ALU.scala 48:24]
      node _T_5 = eq(_T_3, _T_4) @[ALU.scala 48:24]
      when _T_5 : @[ALU.scala 48:24]
        node _io_aluResult_T_2 = sub(io.operandA, io.operandB) @[ALU.scala 59:36]
        node _io_aluResult_T_3 = tail(_io_aluResult_T_2, 1) @[ALU.scala 59:36]
        io.aluResult <= _io_aluResult_T_3 @[ALU.scala 59:20]
      else :
        node _T_6 = asUInt(UInt<2>("h2")) @[ALU.scala 48:24]
        node _T_7 = asUInt(io.operation) @[ALU.scala 48:24]
        node _T_8 = eq(_T_6, _T_7) @[ALU.scala 48:24]
        when _T_8 : @[ALU.scala 48:24]
          node _io_aluResult_T_4 = and(io.operandA, io.operandB) @[ALU.scala 64:35]
          io.aluResult <= _io_aluResult_T_4 @[ALU.scala 64:20]
        else :
          node _T_9 = asUInt(UInt<2>("h3")) @[ALU.scala 48:24]
          node _T_10 = asUInt(io.operation) @[ALU.scala 48:24]
          node _T_11 = eq(_T_9, _T_10) @[ALU.scala 48:24]
          when _T_11 : @[ALU.scala 48:24]
            node _io_aluResult_T_5 = or(io.operandA, io.operandB) @[ALU.scala 69:35]
            io.aluResult <= _io_aluResult_T_5 @[ALU.scala 69:20]
          else :
            node _T_12 = asUInt(UInt<3>("h4")) @[ALU.scala 48:24]
            node _T_13 = asUInt(io.operation) @[ALU.scala 48:24]
            node _T_14 = eq(_T_12, _T_13) @[ALU.scala 48:24]
            when _T_14 : @[ALU.scala 48:24]
              node _io_aluResult_T_6 = xor(io.operandA, io.operandB) @[ALU.scala 74:35]
              io.aluResult <= _io_aluResult_T_6 @[ALU.scala 74:20]
            else :
              node _T_15 = asUInt(UInt<3>("h5")) @[ALU.scala 48:24]
              node _T_16 = asUInt(io.operation) @[ALU.scala 48:24]
              node _T_17 = eq(_T_15, _T_16) @[ALU.scala 48:24]
              when _T_17 : @[ALU.scala 48:24]
                node _io_aluResult_T_7 = dshl(io.operandA, shift_amount) @[ALU.scala 79:36]
                io.aluResult <= _io_aluResult_T_7 @[ALU.scala 79:20]
              else :
                node _T_18 = asUInt(UInt<3>("h6")) @[ALU.scala 48:24]
                node _T_19 = asUInt(io.operation) @[ALU.scala 48:24]
                node _T_20 = eq(_T_18, _T_19) @[ALU.scala 48:24]
                when _T_20 : @[ALU.scala 48:24]
                  node _io_aluResult_T_8 = dshr(io.operandA, shift_amount) @[ALU.scala 84:35]
                  io.aluResult <= _io_aluResult_T_8 @[ALU.scala 84:20]
                else :
                  node _T_21 = asUInt(UInt<3>("h7")) @[ALU.scala 48:24]
                  node _T_22 = asUInt(io.operation) @[ALU.scala 48:24]
                  node _T_23 = eq(_T_21, _T_22) @[ALU.scala 48:24]
                  when _T_23 : @[ALU.scala 48:24]
                    node _io_aluResult_T_9 = asSInt(io.operandA) @[ALU.scala 89:36]
                    node _io_aluResult_T_10 = dshr(_io_aluResult_T_9, shift_amount) @[ALU.scala 89:43]
                    node _io_aluResult_T_11 = asUInt(_io_aluResult_T_10) @[ALU.scala 89:60]
                    io.aluResult <= _io_aluResult_T_11 @[ALU.scala 89:20]
                  else :
                    node _T_24 = asUInt(UInt<4>("h8")) @[ALU.scala 48:24]
                    node _T_25 = asUInt(io.operation) @[ALU.scala 48:24]
                    node _T_26 = eq(_T_24, _T_25) @[ALU.scala 48:24]
                    when _T_26 : @[ALU.scala 48:24]
                      node _io_aluResult_T_12 = asSInt(io.operandA) @[ALU.scala 94:36]
                      node _io_aluResult_T_13 = asSInt(io.operandB) @[ALU.scala 94:57]
                      node _io_aluResult_T_14 = lt(_io_aluResult_T_12, _io_aluResult_T_13) @[ALU.scala 94:43]
                      io.aluResult <= _io_aluResult_T_14 @[ALU.scala 94:20]
                    else :
                      node _T_27 = asUInt(UInt<4>("h9")) @[ALU.scala 48:24]
                      node _T_28 = asUInt(io.operation) @[ALU.scala 48:24]
                      node _T_29 = eq(_T_27, _T_28) @[ALU.scala 48:24]
                      when _T_29 : @[ALU.scala 48:24]
                        node _io_aluResult_T_15 = lt(io.operandA, io.operandB) @[ALU.scala 99:36]
                        io.aluResult <= _io_aluResult_T_15 @[ALU.scala 99:20]

  module ALUcontrol :
    input clock : Clock
    input reset : Reset
    output io : { flip uop : UInt<5>, mapped : UInt<4>}

    io.mapped <= UInt<1>("h0") @[EXstage.scala 50:15]
    node _T = asUInt(UInt<1>("h0")) @[EXstage.scala 52:18]
    node _T_1 = asUInt(io.uop) @[EXstage.scala 52:18]
    node _T_2 = eq(_T, _T_1) @[EXstage.scala 52:18]
    node _T_3 = asUInt(UInt<4>("hb")) @[EXstage.scala 52:18]
    node _T_4 = asUInt(io.uop) @[EXstage.scala 52:18]
    node _T_5 = eq(_T_3, _T_4) @[EXstage.scala 52:18]
    node _T_6 = or(_T_2, _T_5) @[EXstage.scala 52:18]
    when _T_6 : @[EXstage.scala 52:18]
      io.mapped <= UInt<1>("h0") @[EXstage.scala 53:43]
    else :
      node _T_7 = asUInt(UInt<1>("h1")) @[EXstage.scala 52:18]
      node _T_8 = asUInt(io.uop) @[EXstage.scala 52:18]
      node _T_9 = eq(_T_7, _T_8) @[EXstage.scala 52:18]
      when _T_9 : @[EXstage.scala 52:18]
        io.mapped <= UInt<1>("h1") @[EXstage.scala 54:43]
      else :
        node _T_10 = asUInt(UInt<2>("h2")) @[EXstage.scala 52:18]
        node _T_11 = asUInt(io.uop) @[EXstage.scala 52:18]
        node _T_12 = eq(_T_10, _T_11) @[EXstage.scala 52:18]
        node _T_13 = asUInt(UInt<4>("he")) @[EXstage.scala 52:18]
        node _T_14 = asUInt(io.uop) @[EXstage.scala 52:18]
        node _T_15 = eq(_T_13, _T_14) @[EXstage.scala 52:18]
        node _T_16 = or(_T_12, _T_15) @[EXstage.scala 52:18]
        when _T_16 : @[EXstage.scala 52:18]
          io.mapped <= UInt<2>("h2") @[EXstage.scala 55:43]
        else :
          node _T_17 = asUInt(UInt<2>("h3")) @[EXstage.scala 52:18]
          node _T_18 = asUInt(io.uop) @[EXstage.scala 52:18]
          node _T_19 = eq(_T_17, _T_18) @[EXstage.scala 52:18]
          node _T_20 = asUInt(UInt<4>("hd")) @[EXstage.scala 52:18]
          node _T_21 = asUInt(io.uop) @[EXstage.scala 52:18]
          node _T_22 = eq(_T_20, _T_21) @[EXstage.scala 52:18]
          node _T_23 = or(_T_19, _T_22) @[EXstage.scala 52:18]
          when _T_23 : @[EXstage.scala 52:18]
            io.mapped <= UInt<2>("h3") @[EXstage.scala 56:43]
          else :
            node _T_24 = asUInt(UInt<3>("h4")) @[EXstage.scala 52:18]
            node _T_25 = asUInt(io.uop) @[EXstage.scala 52:18]
            node _T_26 = eq(_T_24, _T_25) @[EXstage.scala 52:18]
            node _T_27 = asUInt(UInt<4>("hc")) @[EXstage.scala 52:18]
            node _T_28 = asUInt(io.uop) @[EXstage.scala 52:18]
            node _T_29 = eq(_T_27, _T_28) @[EXstage.scala 52:18]
            node _T_30 = or(_T_26, _T_29) @[EXstage.scala 52:18]
            when _T_30 : @[EXstage.scala 52:18]
              io.mapped <= UInt<3>("h4") @[EXstage.scala 57:43]
            else :
              node _T_31 = asUInt(UInt<3>("h5")) @[EXstage.scala 52:18]
              node _T_32 = asUInt(io.uop) @[EXstage.scala 52:18]
              node _T_33 = eq(_T_31, _T_32) @[EXstage.scala 52:18]
              node _T_34 = asUInt(UInt<4>("hf")) @[EXstage.scala 52:18]
              node _T_35 = asUInt(io.uop) @[EXstage.scala 52:18]
              node _T_36 = eq(_T_34, _T_35) @[EXstage.scala 52:18]
              node _T_37 = or(_T_33, _T_36) @[EXstage.scala 52:18]
              when _T_37 : @[EXstage.scala 52:18]
                io.mapped <= UInt<3>("h5") @[EXstage.scala 58:43]
              else :
                node _T_38 = asUInt(UInt<3>("h6")) @[EXstage.scala 52:18]
                node _T_39 = asUInt(io.uop) @[EXstage.scala 52:18]
                node _T_40 = eq(_T_38, _T_39) @[EXstage.scala 52:18]
                node _T_41 = asUInt(UInt<5>("h11")) @[EXstage.scala 52:18]
                node _T_42 = asUInt(io.uop) @[EXstage.scala 52:18]
                node _T_43 = eq(_T_41, _T_42) @[EXstage.scala 52:18]
                node _T_44 = or(_T_40, _T_43) @[EXstage.scala 52:18]
                when _T_44 : @[EXstage.scala 52:18]
                  io.mapped <= UInt<3>("h6") @[EXstage.scala 59:43]
                else :
                  node _T_45 = asUInt(UInt<3>("h7")) @[EXstage.scala 52:18]
                  node _T_46 = asUInt(io.uop) @[EXstage.scala 52:18]
                  node _T_47 = eq(_T_45, _T_46) @[EXstage.scala 52:18]
                  node _T_48 = asUInt(UInt<5>("h12")) @[EXstage.scala 52:18]
                  node _T_49 = asUInt(io.uop) @[EXstage.scala 52:18]
                  node _T_50 = eq(_T_48, _T_49) @[EXstage.scala 52:18]
                  node _T_51 = or(_T_47, _T_50) @[EXstage.scala 52:18]
                  when _T_51 : @[EXstage.scala 52:18]
                    io.mapped <= UInt<3>("h7") @[EXstage.scala 60:43]
                  else :
                    node _T_52 = asUInt(UInt<4>("h8")) @[EXstage.scala 52:18]
                    node _T_53 = asUInt(io.uop) @[EXstage.scala 52:18]
                    node _T_54 = eq(_T_52, _T_53) @[EXstage.scala 52:18]
                    node _T_55 = asUInt(UInt<4>("ha")) @[EXstage.scala 52:18]
                    node _T_56 = asUInt(io.uop) @[EXstage.scala 52:18]
                    node _T_57 = eq(_T_55, _T_56) @[EXstage.scala 52:18]
                    node _T_58 = or(_T_54, _T_57) @[EXstage.scala 52:18]
                    when _T_58 : @[EXstage.scala 52:18]
                      io.mapped <= UInt<4>("h8") @[EXstage.scala 61:43]
                    else :
                      node _T_59 = asUInt(UInt<4>("h9")) @[EXstage.scala 52:18]
                      node _T_60 = asUInt(io.uop) @[EXstage.scala 52:18]
                      node _T_61 = eq(_T_59, _T_60) @[EXstage.scala 52:18]
                      node _T_62 = asUInt(UInt<5>("h10")) @[EXstage.scala 52:18]
                      node _T_63 = asUInt(io.uop) @[EXstage.scala 52:18]
                      node _T_64 = eq(_T_62, _T_63) @[EXstage.scala 52:18]
                      node _T_65 = or(_T_61, _T_64) @[EXstage.scala 52:18]
                      when _T_65 : @[EXstage.scala 52:18]
                        io.mapped <= UInt<4>("h9") @[EXstage.scala 62:43]

  module EXstage :
    input clock : Clock
    input reset : Reset
    output io : { flip operandA : UInt<32>, flip operandB : UInt<32>, flip immExtnd : UInt<32>, flip ALUsrc : UInt<1>, flip rd_in : UInt<5>, flip uop : UInt<5>, flip wrten_in : UInt<1>, flip XcptInvalid : UInt<1>, aluResult : UInt<32>, exception : UInt<1>, rd : UInt<5>, wrten : UInt<1>}

    inst ALU of ALU @[EXstage.scala 83:26]
    ALU.clock <= clock
    ALU.reset <= reset
    inst ALUcontrol of ALUcontrol @[EXstage.scala 84:26]
    ALUcontrol.clock <= clock
    ALUcontrol.reset <= reset
    ALUcontrol.io.uop <= io.uop @[EXstage.scala 86:21]
    ALU.io.operandA <= io.operandA @[EXstage.scala 88:20]
    node _ALU_io_operandB_T = mux(io.ALUsrc, io.immExtnd, io.operandB) @[EXstage.scala 89:26]
    ALU.io.operandB <= _ALU_io_operandB_T @[EXstage.scala 89:20]
    ALU.io.operation <= ALUcontrol.io.mapped @[EXstage.scala 90:20]
    io.aluResult <= ALU.io.aluResult @[EXstage.scala 92:16]
    io.exception <= io.XcptInvalid @[EXstage.scala 93:16]
    io.rd <= io.rd_in @[EXstage.scala 94:16]
    io.wrten <= io.wrten_in @[EXstage.scala 95:16]

  module EXBarrier :
    input clock : Clock
    input reset : Reset
    output io : { flip inAluResult : UInt<32>, flip inRD : UInt<5>, flip inXcptInvalid : UInt<1>, flip inWrten : UInt<1>, outAluResult : UInt<32>, outRD : UInt<5>, outXcptInvalid : UInt<1>, outWrten : UInt<1>}

    reg aluResult : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[EXbarrier.scala 50:28]
    reg RD : UInt<5>, clock with :
      reset => (reset, UInt<5>("h0")) @[EXbarrier.scala 51:28]
    reg XcptInvalid : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[EXbarrier.scala 52:28]
    reg wrten : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[EXbarrier.scala 53:28]
    aluResult <= io.inAluResult @[EXbarrier.scala 55:15]
    RD <= io.inRD @[EXbarrier.scala 56:15]
    XcptInvalid <= io.inXcptInvalid @[EXbarrier.scala 57:15]
    wrten <= io.inWrten @[EXbarrier.scala 58:15]
    io.outAluResult <= aluResult @[EXbarrier.scala 60:23]
    io.outRD <= RD @[EXbarrier.scala 61:23]
    io.outXcptInvalid <= XcptInvalid @[EXbarrier.scala 62:23]
    io.outWrten <= wrten @[EXbarrier.scala 63:23]

  module MEMBarrier :
    input clock : Clock
    input reset : Reset
    output io : { flip inALUResult : UInt<32>, flip inRD : UInt<5>, flip inException : UInt<1>, flip inWrten : UInt<1>, outALUResult : UInt<32>, outRD : UInt<5>, outException : UInt<1>, outWrten : UInt<1>}

    reg aluResult : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[MEMbarrier.scala 50:28]
    reg rd : UInt<5>, clock with :
      reset => (reset, UInt<5>("h0")) @[MEMbarrier.scala 51:28]
    reg exception : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[MEMbarrier.scala 52:28]
    reg wrten : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[MEMbarrier.scala 53:28]
    aluResult <= io.inALUResult @[MEMbarrier.scala 55:21]
    rd <= io.inRD @[MEMbarrier.scala 56:21]
    exception <= io.inException @[MEMbarrier.scala 57:21]
    wrten <= io.inWrten @[MEMbarrier.scala 58:21]
    io.outALUResult <= aluResult @[MEMbarrier.scala 60:21]
    io.outRD <= rd @[MEMbarrier.scala 61:21]
    io.outException <= exception @[MEMbarrier.scala 62:21]
    io.outWrten <= wrten @[MEMbarrier.scala 63:21]

  module WBstage :
    input clock : Clock
    input reset : Reset
    output io : { flip aluResult : UInt<32>, flip rd : UInt<5>, flip wrten : UInt<1>, regFileReq : { addr : UInt<5>, data : UInt<32>, w_en : UInt<1>}, check_res : UInt<32>}

    io.regFileReq.addr <= io.rd @[WBstage.scala 58:22]
    io.regFileReq.data <= io.aluResult @[WBstage.scala 59:22]
    io.regFileReq.w_en <= io.wrten @[WBstage.scala 60:22]
    io.check_res <= io.aluResult @[WBstage.scala 61:22]

  module WBBarrier :
    input clock : Clock
    input reset : Reset
    output io : { flip inCheckRes : UInt<32>, flip inXcptInvalid : UInt<1>, outCheckRes : UInt<32>, outXcptInvalid : UInt<1>}

    reg check_res : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[WBbarrier.scala 46:26]
    reg isInvalid : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[WBbarrier.scala 47:26]
    check_res <= io.inCheckRes @[WBbarrier.scala 49:13]
    isInvalid <= io.inXcptInvalid @[WBbarrier.scala 50:13]
    io.outCheckRes <= check_res @[WBbarrier.scala 52:18]
    io.outXcptInvalid <= isInvalid @[WBbarrier.scala 53:21]

  module PipelinedRV32Icore :
    input clock : Clock
    input reset : Reset
    output io : { check_res : UInt<32>, exception : UInt<1>}

    inst IFstage of IF @[core.scala 65:25]
    IFstage.clock <= clock
    IFstage.reset <= reset
    inst IFBarrier of IFBarrier @[core.scala 66:25]
    IFBarrier.clock <= clock
    IFBarrier.reset <= reset
    inst IDstage of ID @[core.scala 68:25]
    IDstage.clock <= clock
    IDstage.reset <= reset
    inst IDBarrier of IDBarrier @[core.scala 69:25]
    IDBarrier.clock <= clock
    IDBarrier.reset <= reset
    inst EXstage of EXstage @[core.scala 71:25]
    EXstage.clock <= clock
    EXstage.reset <= reset
    inst EXBarrier of EXBarrier @[core.scala 72:25]
    EXBarrier.clock <= clock
    EXBarrier.reset <= reset
    inst MEMBarrier of MEMBarrier @[core.scala 75:26]
    MEMBarrier.clock <= clock
    MEMBarrier.reset <= reset
    inst WBstage of WBstage @[core.scala 77:25]
    WBstage.clock <= clock
    WBstage.reset <= reset
    inst WBBarrier of WBBarrier @[core.scala 78:25]
    WBBarrier.clock <= clock
    WBBarrier.reset <= reset
    IFBarrier.io.inInstr <= IFstage.io.inst @[core.scala 81:27]
    IDstage.io.inst <= IFBarrier.io.outInstr @[core.scala 84:27]
    IDstage.io.w_en <= WBstage.io.regFileReq.w_en @[core.scala 85:27]
    IDstage.io.rd_in <= WBstage.io.regFileReq.addr @[core.scala 86:27]
    IDstage.io.write_data <= WBstage.io.regFileReq.data @[core.scala 87:27]
    IDBarrier.io.inUOP <= IDstage.io.uop @[core.scala 90:31]
    IDBarrier.io.inRD <= IDstage.io.rd_out @[core.scala 91:31]
    IDBarrier.io.inXcptInvalid <= IDstage.io.XcptInvalid @[core.scala 92:31]
    IDBarrier.io.inOperandA <= IDstage.io.operandA @[core.scala 93:31]
    IDBarrier.io.inOperandB <= IDstage.io.operandB @[core.scala 94:31]
    IDBarrier.io.inWrten <= IDstage.io.wrten @[core.scala 95:31]
    IDBarrier.io.inALUsrc <= IDstage.io.ALUsrc @[core.scala 96:31]
    IDBarrier.io.inImmExtnd <= IDstage.io.immExtnd @[core.scala 97:31]
    EXstage.io.uop <= IDBarrier.io.outUOP @[core.scala 100:27]
    EXstage.io.rd_in <= IDBarrier.io.outRD @[core.scala 101:27]
    EXstage.io.operandA <= IDBarrier.io.outOperandA @[core.scala 102:27]
    EXstage.io.operandB <= IDBarrier.io.outOperandB @[core.scala 103:27]
    EXstage.io.XcptInvalid <= IDBarrier.io.outXcptInvalid @[core.scala 104:27]
    EXstage.io.wrten_in <= IDBarrier.io.outWrten @[core.scala 105:27]
    EXstage.io.ALUsrc <= IDBarrier.io.outALUsrc @[core.scala 106:27]
    EXstage.io.immExtnd <= IDBarrier.io.outImmExtnd @[core.scala 107:27]
    EXBarrier.io.inAluResult <= EXstage.io.aluResult @[core.scala 110:31]
    EXBarrier.io.inRD <= EXstage.io.rd @[core.scala 111:31]
    EXBarrier.io.inXcptInvalid <= EXstage.io.exception @[core.scala 112:31]
    EXBarrier.io.inWrten <= EXstage.io.wrten @[core.scala 113:31]
    MEMBarrier.io.inALUResult <= EXBarrier.io.outAluResult @[core.scala 116:29]
    MEMBarrier.io.inRD <= EXBarrier.io.outRD @[core.scala 117:29]
    MEMBarrier.io.inException <= EXBarrier.io.outXcptInvalid @[core.scala 118:29]
    MEMBarrier.io.inWrten <= EXBarrier.io.outWrten @[core.scala 119:29]
    WBstage.io.aluResult <= MEMBarrier.io.outALUResult @[core.scala 122:24]
    WBstage.io.rd <= MEMBarrier.io.outRD @[core.scala 123:24]
    WBstage.io.wrten <= MEMBarrier.io.outWrten @[core.scala 124:24]
    WBBarrier.io.inCheckRes <= WBstage.io.aluResult @[core.scala 127:31]
    WBBarrier.io.inXcptInvalid <= MEMBarrier.io.outException @[core.scala 128:31]
    io.check_res <= WBBarrier.io.outCheckRes @[core.scala 130:16]
    io.exception <= WBBarrier.io.outXcptInvalid @[core.scala 131:16]

  module PipelinedRV32I :
    input clock : Clock
    input reset : UInt<1>
    output io : { result : UInt<32>, exception : UInt<1>}

    inst core of PipelinedRV32Icore @[PipelinedRISCV32I.scala 25:20]
    core.clock <= clock
    core.reset <= reset
    io.result <= core.io.check_res @[PipelinedRISCV32I.scala 27:16]
    io.exception <= core.io.exception @[PipelinedRISCV32I.scala 28:16]

