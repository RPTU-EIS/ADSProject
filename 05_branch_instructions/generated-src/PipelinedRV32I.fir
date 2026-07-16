circuit PipelinedRV32I :
  module IF :
    input clock : Clock
    input reset : Reset
    output io : { flip PCSrcE : UInt<1>, flip PCTargetE : UInt<32>, InstrF : UInt<32>, PCF : UInt<32>, PCPlus4F : UInt<32>}

    cmem IMem : UInt<32> [4096] @[IFstage.scala 54:17]
    reg PC : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[IFstage.scala 57:19]
    node addr = shr(PC, 2) @[IFstage.scala 59:17]
    node _io_InstrF_T = bits(addr, 11, 0) @[IFstage.scala 61:25]
    infer mport io_InstrF_MPORT = IMem[_io_InstrF_T], clock @[IFstage.scala 61:20]
    io.InstrF <= io_InstrF_MPORT @[IFstage.scala 61:13]
    node _PCPlus4_T = add(PC, UInt<3>("h4")) @[IFstage.scala 63:20]
    node PCPlus4 = tail(_PCPlus4_T, 1) @[IFstage.scala 63:20]
    node PCNext = mux(io.PCSrcE, io.PCTargetE, PCPlus4) @[IFstage.scala 64:20]
    PC <= PCNext @[IFstage.scala 66:6]
    io.PCF <= PC @[IFstage.scala 68:15]
    io.PCPlus4F <= PCPlus4 @[IFstage.scala 69:15]

  module IFBarrier :
    input clock : Clock
    input reset : Reset
    output io : { flip InstrF : UInt<32>, flip PCF : UInt<32>, flip PCPlus4F : UInt<32>, flip CLR : UInt<1>, InstrD : UInt<32>, PCD : UInt<32>, PCPlus4D : UInt<32>}

    reg instrReg : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[IFbarrier.scala 44:27]
    reg pcReg : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[IFbarrier.scala 45:27]
    reg pcPlus4Reg : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[IFbarrier.scala 46:27]
    when io.CLR : @[IFbarrier.scala 48:16]
      instrReg <= UInt<5>("h13") @[IFbarrier.scala 49:16]
      pcReg <= UInt<1>("h0") @[IFbarrier.scala 50:16]
      pcPlus4Reg <= UInt<1>("h0") @[IFbarrier.scala 51:16]
    else :
      instrReg <= io.InstrF @[IFbarrier.scala 53:16]
      pcReg <= io.PCF @[IFbarrier.scala 54:16]
      pcPlus4Reg <= io.PCPlus4F @[IFbarrier.scala 55:16]
    io.InstrD <= instrReg @[IFbarrier.scala 58:15]
    io.PCD <= pcReg @[IFbarrier.scala 59:15]
    io.PCPlus4D <= pcPlus4Reg @[IFbarrier.scala 60:15]

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
    output io : { flip opcode : UInt<7>, flip funct3 : UInt<3>, flip funct7 : UInt<7>, uop : UInt<5>, ALUSrcD : UInt<1>, immSel : UInt<2>, BranchD : UInt<1>, JumpD : UInt<1>, RegWriteD : UInt<1>, XcptInvalid : UInt<1>}

    io.uop <= UInt<5>("h1c") @[IDstage.scala 69:20]
    io.ALUSrcD <= UInt<1>("h0") @[IDstage.scala 70:20]
    io.immSel <= UInt<1>("h0") @[IDstage.scala 71:20]
    io.BranchD <= UInt<1>("h0") @[IDstage.scala 72:20]
    io.JumpD <= UInt<1>("h0") @[IDstage.scala 73:20]
    io.RegWriteD <= UInt<1>("h0") @[IDstage.scala 74:20]
    io.XcptInvalid <= UInt<1>("h1") @[IDstage.scala 75:20]
    node _T = eq(UInt<6>("h33"), io.opcode) @[IDstage.scala 77:22]
    when _T : @[IDstage.scala 77:22]
      io.RegWriteD <= UInt<1>("h1") @[IDstage.scala 79:26]
      node _T_1 = eq(io.funct7, UInt<1>("h0")) @[IDstage.scala 80:28]
      when _T_1 : @[IDstage.scala 80:45]
        node _T_2 = eq(UInt<1>("h0"), io.funct3) @[IDstage.scala 81:34]
        when _T_2 : @[IDstage.scala 81:34]
          io.uop <= UInt<1>("h0") @[IDstage.scala 82:42]
          io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 82:71]
        else :
          node _T_3 = eq(UInt<1>("h1"), io.funct3) @[IDstage.scala 81:34]
          when _T_3 : @[IDstage.scala 81:34]
            io.uop <= UInt<3>("h5") @[IDstage.scala 83:42]
            io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 83:71]
          else :
            node _T_4 = eq(UInt<2>("h2"), io.funct3) @[IDstage.scala 81:34]
            when _T_4 : @[IDstage.scala 81:34]
              io.uop <= UInt<4>("h8") @[IDstage.scala 84:42]
              io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 84:71]
            else :
              node _T_5 = eq(UInt<2>("h3"), io.funct3) @[IDstage.scala 81:34]
              when _T_5 : @[IDstage.scala 81:34]
                io.uop <= UInt<4>("h9") @[IDstage.scala 85:42]
                io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 85:71]
              else :
                node _T_6 = eq(UInt<3>("h4"), io.funct3) @[IDstage.scala 81:34]
                when _T_6 : @[IDstage.scala 81:34]
                  io.uop <= UInt<3>("h4") @[IDstage.scala 86:42]
                  io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 86:71]
                else :
                  node _T_7 = eq(UInt<3>("h5"), io.funct3) @[IDstage.scala 81:34]
                  when _T_7 : @[IDstage.scala 81:34]
                    io.uop <= UInt<3>("h6") @[IDstage.scala 87:42]
                    io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 87:71]
                  else :
                    node _T_8 = eq(UInt<3>("h6"), io.funct3) @[IDstage.scala 81:34]
                    when _T_8 : @[IDstage.scala 81:34]
                      io.uop <= UInt<2>("h3") @[IDstage.scala 88:42]
                      io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 88:71]
                    else :
                      node _T_9 = eq(UInt<3>("h7"), io.funct3) @[IDstage.scala 81:34]
                      when _T_9 : @[IDstage.scala 81:34]
                        io.uop <= UInt<2>("h2") @[IDstage.scala 89:42]
                        io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 89:71]
      else :
        node _T_10 = eq(io.funct7, UInt<6>("h20")) @[IDstage.scala 91:34]
        when _T_10 : @[IDstage.scala 91:51]
          node _T_11 = eq(UInt<1>("h0"), io.funct3) @[IDstage.scala 92:34]
          when _T_11 : @[IDstage.scala 92:34]
            io.uop <= UInt<1>("h1") @[IDstage.scala 93:42]
            io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 93:70]
          else :
            node _T_12 = eq(UInt<3>("h5"), io.funct3) @[IDstage.scala 92:34]
            when _T_12 : @[IDstage.scala 92:34]
              io.uop <= UInt<3>("h7") @[IDstage.scala 94:42]
              io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 94:70]
    else :
      node _T_13 = eq(UInt<5>("h13"), io.opcode) @[IDstage.scala 77:22]
      when _T_13 : @[IDstage.scala 77:22]
        io.ALUSrcD <= UInt<1>("h1") @[IDstage.scala 99:26]
        io.RegWriteD <= UInt<1>("h1") @[IDstage.scala 100:26]
        node _T_14 = eq(UInt<1>("h0"), io.funct3) @[IDstage.scala 101:30]
        when _T_14 : @[IDstage.scala 101:30]
          io.uop <= UInt<4>("hb") @[IDstage.scala 102:38]
          io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 102:68]
        else :
          node _T_15 = eq(UInt<2>("h2"), io.funct3) @[IDstage.scala 101:30]
          when _T_15 : @[IDstage.scala 101:30]
            io.uop <= UInt<4>("ha") @[IDstage.scala 103:38]
            io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 103:68]
          else :
            node _T_16 = eq(UInt<2>("h3"), io.funct3) @[IDstage.scala 101:30]
            when _T_16 : @[IDstage.scala 101:30]
              io.uop <= UInt<5>("h10") @[IDstage.scala 104:38]
              io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 104:68]
            else :
              node _T_17 = eq(UInt<3>("h4"), io.funct3) @[IDstage.scala 101:30]
              when _T_17 : @[IDstage.scala 101:30]
                io.uop <= UInt<4>("hc") @[IDstage.scala 105:38]
                io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 105:68]
              else :
                node _T_18 = eq(UInt<3>("h6"), io.funct3) @[IDstage.scala 101:30]
                when _T_18 : @[IDstage.scala 101:30]
                  io.uop <= UInt<4>("hd") @[IDstage.scala 106:38]
                  io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 106:68]
                else :
                  node _T_19 = eq(UInt<3>("h7"), io.funct3) @[IDstage.scala 101:30]
                  when _T_19 : @[IDstage.scala 101:30]
                    io.uop <= UInt<4>("he") @[IDstage.scala 107:38]
                    io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 107:68]
                  else :
                    node _T_20 = eq(UInt<1>("h1"), io.funct3) @[IDstage.scala 101:30]
                    when _T_20 : @[IDstage.scala 101:30]
                      node _T_21 = eq(io.funct7, UInt<1>("h0")) @[IDstage.scala 109:36]
                      when _T_21 : @[IDstage.scala 109:53]
                        io.uop <= UInt<4>("hf") @[IDstage.scala 110:32]
                        io.immSel <= UInt<1>("h1") @[IDstage.scala 110:56]
                        io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 110:79]
                    else :
                      node _T_22 = eq(UInt<3>("h5"), io.funct3) @[IDstage.scala 101:30]
                      when _T_22 : @[IDstage.scala 101:30]
                        node _T_23 = eq(io.funct7, UInt<1>("h0")) @[IDstage.scala 114:36]
                        when _T_23 : @[IDstage.scala 114:53]
                          io.uop <= UInt<5>("h11") @[IDstage.scala 115:32]
                          io.immSel <= UInt<1>("h1") @[IDstage.scala 115:56]
                          io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 115:79]
                        else :
                          node _T_24 = eq(io.funct7, UInt<6>("h20")) @[IDstage.scala 116:42]
                          when _T_24 : @[IDstage.scala 116:59]
                            io.uop <= UInt<5>("h12") @[IDstage.scala 117:32]
                            io.immSel <= UInt<1>("h1") @[IDstage.scala 117:56]
                            io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 117:79]
      else :
        node _T_25 = eq(UInt<7>("h63"), io.opcode) @[IDstage.scala 77:22]
        when _T_25 : @[IDstage.scala 77:22]
          io.immSel <= UInt<2>("h3") @[IDstage.scala 123:24]
          io.BranchD <= UInt<1>("h1") @[IDstage.scala 124:24]
          node _T_26 = eq(UInt<1>("h0"), io.funct3) @[IDstage.scala 126:30]
          when _T_26 : @[IDstage.scala 126:30]
            io.uop <= UInt<5>("h14") @[IDstage.scala 127:38]
            io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 127:67]
          else :
            node _T_27 = eq(UInt<1>("h1"), io.funct3) @[IDstage.scala 126:30]
            when _T_27 : @[IDstage.scala 126:30]
              io.uop <= UInt<5>("h15") @[IDstage.scala 128:38]
              io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 128:67]
            else :
              node _T_28 = eq(UInt<3>("h4"), io.funct3) @[IDstage.scala 126:30]
              when _T_28 : @[IDstage.scala 126:30]
                io.uop <= UInt<5>("h16") @[IDstage.scala 129:38]
                io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 129:67]
              else :
                node _T_29 = eq(UInt<3>("h5"), io.funct3) @[IDstage.scala 126:30]
                when _T_29 : @[IDstage.scala 126:30]
                  io.uop <= UInt<5>("h17") @[IDstage.scala 130:38]
                  io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 130:67]
                else :
                  node _T_30 = eq(UInt<3>("h6"), io.funct3) @[IDstage.scala 126:30]
                  when _T_30 : @[IDstage.scala 126:30]
                    io.uop <= UInt<5>("h18") @[IDstage.scala 131:38]
                    io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 131:67]
                  else :
                    node _T_31 = eq(UInt<3>("h7"), io.funct3) @[IDstage.scala 126:30]
                    when _T_31 : @[IDstage.scala 126:30]
                      io.uop <= UInt<5>("h19") @[IDstage.scala 132:38]
                      io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 132:67]
        else :
          node _T_32 = eq(UInt<7>("h6f"), io.opcode) @[IDstage.scala 77:22]
          when _T_32 : @[IDstage.scala 77:22]
            io.immSel <= UInt<2>("h2") @[IDstage.scala 136:28]
            io.JumpD <= UInt<1>("h1") @[IDstage.scala 137:28]
            io.RegWriteD <= UInt<1>("h1") @[IDstage.scala 138:28]
            io.uop <= UInt<5>("h1a") @[IDstage.scala 139:28]
            io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 140:28]
          else :
            node _T_33 = eq(UInt<7>("h67"), io.opcode) @[IDstage.scala 77:22]
            when _T_33 : @[IDstage.scala 77:22]
              io.ALUSrcD <= UInt<1>("h1") @[IDstage.scala 144:28]
              io.JumpD <= UInt<1>("h1") @[IDstage.scala 145:28]
              io.BranchD <= UInt<1>("h1") @[IDstage.scala 146:28]
              io.RegWriteD <= UInt<1>("h1") @[IDstage.scala 147:28]
              io.uop <= UInt<5>("h1b") @[IDstage.scala 148:28]
              io.XcptInvalid <= UInt<1>("h0") @[IDstage.scala 149:28]

  module SignExtend :
    input clock : Clock
    input reset : Reset
    output io : { flip imm_in : UInt<25>, flip sel : UInt<2>, imm_out : UInt<32>}

    node _full_T = bits(io.imm_in, 24, 24) @[IDstage.scala 161:44]
    node _full_T_1 = bits(_full_T, 0, 0) @[Bitwise.scala 74:15]
    node _full_T_2 = mux(_full_T_1, UInt<20>("hfffff"), UInt<20>("h0")) @[Bitwise.scala 74:12]
    node _full_T_3 = bits(io.imm_in, 24, 13) @[IDstage.scala 161:60]
    node full = cat(_full_T_2, _full_T_3) @[Cat.scala 31:58]
    node _shamt_T = bits(io.imm_in, 17, 13) @[IDstage.scala 162:46]
    node shamt = cat(UInt<27>("h0"), _shamt_T) @[Cat.scala 31:58]
    node _jump_cat_T = bits(io.imm_in, 24, 24) @[IDstage.scala 163:35]
    node _jump_cat_T_1 = bits(io.imm_in, 12, 5) @[IDstage.scala 163:50]
    node _jump_cat_T_2 = bits(io.imm_in, 13, 13) @[IDstage.scala 163:67]
    node _jump_cat_T_3 = bits(io.imm_in, 23, 14) @[IDstage.scala 163:82]
    node jump_cat_lo = cat(_jump_cat_T_2, _jump_cat_T_3) @[Cat.scala 31:58]
    node jump_cat_hi = cat(_jump_cat_T, _jump_cat_T_1) @[Cat.scala 31:58]
    node jump_cat = cat(jump_cat_hi, jump_cat_lo) @[Cat.scala 31:58]
    node _jump_imm_T = bits(jump_cat, 19, 19) @[IDstage.scala 164:43]
    node _jump_imm_T_1 = bits(_jump_imm_T, 0, 0) @[Bitwise.scala 74:15]
    node _jump_imm_T_2 = mux(_jump_imm_T_1, UInt<11>("h7ff"), UInt<11>("h0")) @[Bitwise.scala 74:12]
    node jump_imm_hi = cat(_jump_imm_T_2, jump_cat) @[Cat.scala 31:58]
    node jump_imm = cat(jump_imm_hi, UInt<1>("h0")) @[Cat.scala 31:58]
    node _branch_imm_T = bits(io.imm_in, 24, 24) @[IDstage.scala 165:44]
    node _branch_imm_T_1 = bits(_branch_imm_T, 0, 0) @[Bitwise.scala 74:15]
    node _branch_imm_T_2 = mux(_branch_imm_T_1, UInt<19>("h7ffff"), UInt<19>("h0")) @[Bitwise.scala 74:12]
    node _branch_imm_T_3 = bits(io.imm_in, 24, 24) @[IDstage.scala 165:60]
    node _branch_imm_T_4 = bits(io.imm_in, 0, 0) @[IDstage.scala 165:75]
    node _branch_imm_T_5 = bits(io.imm_in, 23, 18) @[IDstage.scala 165:89]
    node _branch_imm_T_6 = bits(io.imm_in, 4, 1) @[IDstage.scala 165:107]
    node branch_imm_lo_hi = cat(_branch_imm_T_5, _branch_imm_T_6) @[Cat.scala 31:58]
    node branch_imm_lo = cat(branch_imm_lo_hi, UInt<1>("h0")) @[Cat.scala 31:58]
    node branch_imm_hi_hi = cat(_branch_imm_T_2, _branch_imm_T_3) @[Cat.scala 31:58]
    node branch_imm_hi = cat(branch_imm_hi_hi, _branch_imm_T_4) @[Cat.scala 31:58]
    node branch_imm = cat(branch_imm_hi, branch_imm_lo) @[Cat.scala 31:58]
    io.imm_out <= full @[IDstage.scala 167:16]
    node _T = eq(UInt<1>("h0"), io.sel) @[IDstage.scala 169:19]
    when _T : @[IDstage.scala 169:19]
      io.imm_out <= full @[IDstage.scala 170:28]
    else :
      node _T_1 = eq(UInt<1>("h1"), io.sel) @[IDstage.scala 169:19]
      when _T_1 : @[IDstage.scala 169:19]
        io.imm_out <= shamt @[IDstage.scala 171:28]
      else :
        node _T_2 = eq(UInt<2>("h2"), io.sel) @[IDstage.scala 169:19]
        when _T_2 : @[IDstage.scala 169:19]
          io.imm_out <= jump_imm @[IDstage.scala 172:28]
        else :
          node _T_3 = eq(UInt<2>("h3"), io.sel) @[IDstage.scala 169:19]
          when _T_3 : @[IDstage.scala 169:19]
            io.imm_out <= branch_imm @[IDstage.scala 173:28]

  module ID :
    input clock : Clock
    input reset : Reset
    output io : { flip inst : UInt<32>, flip pcD : UInt<32>, flip pcPlus4D : UInt<32>, flip RegWriteW : UInt<1>, flip rdW : UInt<5>, flip ResultW : UInt<32>, uop : UInt<5>, RegWriteD : UInt<1>, ALUSrcD : UInt<1>, ImmExtD : UInt<32>, BranchD : UInt<1>, JumpD : UInt<1>, XcptInvalid : UInt<1>, rdD : UInt<5>, RD1D : UInt<32>, RD2D : UInt<32>, pcD_out : UInt<32>, pcPlus4D_out : UInt<32>}

    node opcode = bits(io.inst, 6, 0) @[IDstage.scala 201:25]
    node funct3 = bits(io.inst, 14, 12) @[IDstage.scala 202:25]
    node funct7 = bits(io.inst, 31, 25) @[IDstage.scala 203:25]
    node rs1 = bits(io.inst, 19, 15) @[IDstage.scala 204:25]
    node rs2 = bits(io.inst, 24, 20) @[IDstage.scala 205:25]
    node rd = bits(io.inst, 11, 7) @[IDstage.scala 206:25]
    inst rf of regFile @[IDstage.scala 208:22]
    rf.clock <= clock
    rf.reset <= reset
    inst cu of ControlUnit @[IDstage.scala 209:22]
    cu.clock <= clock
    cu.reset <= reset
    inst sigex of SignExtend @[IDstage.scala 210:23]
    sigex.clock <= clock
    sigex.reset <= reset
    cu.io.opcode <= opcode @[IDstage.scala 212:18]
    cu.io.funct3 <= funct3 @[IDstage.scala 213:18]
    cu.io.funct7 <= funct7 @[IDstage.scala 214:18]
    node _sigex_io_imm_in_T = bits(io.inst, 31, 7) @[IDstage.scala 216:31]
    sigex.io.imm_in <= _sigex_io_imm_in_T @[IDstage.scala 216:21]
    sigex.io.sel <= cu.io.immSel @[IDstage.scala 217:21]
    rf.io.req_1.addr <= rs1 @[IDstage.scala 219:22]
    rf.io.req_2.addr <= rs2 @[IDstage.scala 220:22]
    rf.io.req_3.addr <= io.rdW @[IDstage.scala 221:22]
    rf.io.req_3.w_en <= io.RegWriteW @[IDstage.scala 222:22]
    rf.io.req_3.data <= io.ResultW @[IDstage.scala 223:22]
    io.RD1D <= rf.io.resp_1.data @[IDstage.scala 225:20]
    io.RD2D <= rf.io.resp_2.data @[IDstage.scala 226:20]
    io.rdD <= rd @[IDstage.scala 227:20]
    io.uop <= cu.io.uop @[IDstage.scala 228:20]
    io.XcptInvalid <= cu.io.XcptInvalid @[IDstage.scala 229:20]
    io.ImmExtD <= sigex.io.imm_out @[IDstage.scala 230:20]
    io.RegWriteD <= cu.io.RegWriteD @[IDstage.scala 231:20]
    io.ALUSrcD <= cu.io.ALUSrcD @[IDstage.scala 232:20]
    io.BranchD <= cu.io.BranchD @[IDstage.scala 233:20]
    io.JumpD <= cu.io.JumpD @[IDstage.scala 234:20]
    io.pcD_out <= io.pcD @[IDstage.scala 235:21]
    io.pcPlus4D_out <= io.pcPlus4D @[IDstage.scala 236:21]

  module IDBarrier :
    input clock : Clock
    input reset : Reset
    output io : { flip uopD : UInt<5>, flip rdD : UInt<5>, flip RD1D : UInt<32>, flip RD2D : UInt<32>, flip XcptInvalidD : UInt<1>, flip RegWriteD : UInt<1>, flip ALUSrcD : UInt<1>, flip ImmExtD : UInt<32>, flip BranchD : UInt<1>, flip JumpD : UInt<1>, flip PCD : UInt<32>, flip PCPlus4D : UInt<32>, flip rs1D : UInt<5>, flip rs2D : UInt<5>, flip CLR : UInt<1>, Rs1E : UInt<5>, Rs2E : UInt<5>, uopE : UInt<5>, rdE : UInt<5>, RD1E : UInt<32>, RD2E : UInt<32>, XcptInvalidE : UInt<1>, RegWriteE : UInt<1>, ALUSrcE : UInt<1>, ImmExtE : UInt<32>, BranchE : UInt<1>, JumpE : UInt<1>, PCE : UInt<32>, PCPlus4E : UInt<32>}

    reg uop : UInt<5>, clock with :
      reset => (reset, UInt<5>("h1c")) @[IDbarrier.scala 78:31]
    reg XcptInvalid : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[IDbarrier.scala 79:31]
    reg rd : UInt<5>, clock with :
      reset => (reset, UInt<5>("h0")) @[IDbarrier.scala 80:31]
    reg RD1 : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[IDbarrier.scala 81:31]
    reg RD2 : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[IDbarrier.scala 82:31]
    reg RegWrite : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[IDbarrier.scala 83:31]
    reg ALUSrc : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[IDbarrier.scala 84:31]
    reg ImmExt : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[IDbarrier.scala 85:31]
    reg Branch : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[IDbarrier.scala 86:31]
    reg Jump : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[IDbarrier.scala 87:31]
    reg PC : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[IDbarrier.scala 88:31]
    reg PCPlus4 : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[IDbarrier.scala 89:31]
    reg rs1Addr : UInt<5>, clock with :
      reset => (reset, UInt<5>("h0")) @[IDbarrier.scala 90:31]
    reg rs2Addr : UInt<5>, clock with :
      reset => (reset, UInt<5>("h0")) @[IDbarrier.scala 91:31]
    when io.CLR : @[IDbarrier.scala 93:18]
      uop <= UInt<5>("h13") @[IDbarrier.scala 94:21]
      XcptInvalid <= UInt<1>("h0") @[IDbarrier.scala 95:21]
      rd <= UInt<1>("h0") @[IDbarrier.scala 96:21]
      RD1 <= UInt<1>("h0") @[IDbarrier.scala 97:21]
      RD2 <= UInt<1>("h0") @[IDbarrier.scala 98:21]
      RegWrite <= UInt<1>("h0") @[IDbarrier.scala 99:21]
      ALUSrc <= UInt<1>("h0") @[IDbarrier.scala 100:21]
      ImmExt <= UInt<1>("h0") @[IDbarrier.scala 101:21]
      Branch <= UInt<1>("h0") @[IDbarrier.scala 102:21]
      Jump <= UInt<1>("h0") @[IDbarrier.scala 103:21]
      PC <= UInt<1>("h0") @[IDbarrier.scala 104:21]
      PCPlus4 <= UInt<1>("h0") @[IDbarrier.scala 105:21]
      rs1Addr <= UInt<1>("h0") @[IDbarrier.scala 106:21]
      rs2Addr <= UInt<1>("h0") @[IDbarrier.scala 107:21]
    else :
      uop <= io.uopD @[IDbarrier.scala 109:21]
      rd <= io.rdD @[IDbarrier.scala 110:21]
      RD1 <= io.RD1D @[IDbarrier.scala 111:21]
      RD2 <= io.RD2D @[IDbarrier.scala 112:21]
      XcptInvalid <= io.XcptInvalidD @[IDbarrier.scala 113:21]
      RegWrite <= io.RegWriteD @[IDbarrier.scala 114:21]
      ALUSrc <= io.ALUSrcD @[IDbarrier.scala 115:21]
      ImmExt <= io.ImmExtD @[IDbarrier.scala 116:21]
      Branch <= io.BranchD @[IDbarrier.scala 117:21]
      Jump <= io.JumpD @[IDbarrier.scala 118:21]
      PC <= io.PCD @[IDbarrier.scala 119:21]
      PCPlus4 <= io.PCPlus4D @[IDbarrier.scala 120:21]
      rs1Addr <= io.rs1D @[IDbarrier.scala 121:21]
      rs2Addr <= io.rs2D @[IDbarrier.scala 122:21]
    io.uopE <= uop @[IDbarrier.scala 125:21]
    io.XcptInvalidE <= XcptInvalid @[IDbarrier.scala 126:21]
    io.rdE <= rd @[IDbarrier.scala 127:21]
    io.RD1E <= RD1 @[IDbarrier.scala 128:21]
    io.RD2E <= RD2 @[IDbarrier.scala 129:21]
    io.RegWriteE <= RegWrite @[IDbarrier.scala 130:21]
    io.ALUSrcE <= ALUSrc @[IDbarrier.scala 131:21]
    io.ImmExtE <= ImmExt @[IDbarrier.scala 132:21]
    io.BranchE <= Branch @[IDbarrier.scala 133:21]
    io.JumpE <= Jump @[IDbarrier.scala 134:21]
    io.PCE <= PC @[IDbarrier.scala 135:21]
    io.PCPlus4E <= PCPlus4 @[IDbarrier.scala 136:21]
    io.Rs1E <= rs1Addr @[IDbarrier.scala 137:21]
    io.Rs2E <= rs2Addr @[IDbarrier.scala 138:21]

  module ALU :
    input clock : Clock
    input reset : Reset
    output io : { flip operandA : UInt<32>, flip operandB : UInt<32>, flip operation : UInt<4>, aluResult : UInt<32>, zero : UInt<1>}

    node shift_amount = bits(io.operandB, 4, 0) @[ALU.scala 47:33]
    io.aluResult <= UInt<1>("h0") @[ALU.scala 49:16]
    node _io_zero_T = eq(io.aluResult, UInt<1>("h0")) @[ALU.scala 50:27]
    io.zero <= _io_zero_T @[ALU.scala 50:11]
    node _T = asUInt(UInt<1>("h0")) @[ALU.scala 52:24]
    node _T_1 = asUInt(io.operation) @[ALU.scala 52:24]
    node _T_2 = eq(_T, _T_1) @[ALU.scala 52:24]
    when _T_2 : @[ALU.scala 52:24]
      node _io_aluResult_T = add(io.operandA, io.operandB) @[ALU.scala 54:49]
      node _io_aluResult_T_1 = tail(_io_aluResult_T, 1) @[ALU.scala 54:49]
      io.aluResult <= _io_aluResult_T_1 @[ALU.scala 54:34]
    else :
      node _T_3 = asUInt(UInt<1>("h1")) @[ALU.scala 52:24]
      node _T_4 = asUInt(io.operation) @[ALU.scala 52:24]
      node _T_5 = eq(_T_3, _T_4) @[ALU.scala 52:24]
      when _T_5 : @[ALU.scala 52:24]
        node _io_aluResult_T_2 = sub(io.operandA, io.operandB) @[ALU.scala 55:50]
        node _io_aluResult_T_3 = tail(_io_aluResult_T_2, 1) @[ALU.scala 55:50]
        io.aluResult <= _io_aluResult_T_3 @[ALU.scala 55:34]
      else :
        node _T_6 = asUInt(UInt<2>("h2")) @[ALU.scala 52:24]
        node _T_7 = asUInt(io.operation) @[ALU.scala 52:24]
        node _T_8 = eq(_T_6, _T_7) @[ALU.scala 52:24]
        when _T_8 : @[ALU.scala 52:24]
          node _io_aluResult_T_4 = and(io.operandA, io.operandB) @[ALU.scala 56:49]
          io.aluResult <= _io_aluResult_T_4 @[ALU.scala 56:34]
        else :
          node _T_9 = asUInt(UInt<2>("h3")) @[ALU.scala 52:24]
          node _T_10 = asUInt(io.operation) @[ALU.scala 52:24]
          node _T_11 = eq(_T_9, _T_10) @[ALU.scala 52:24]
          when _T_11 : @[ALU.scala 52:24]
            node _io_aluResult_T_5 = or(io.operandA, io.operandB) @[ALU.scala 57:49]
            io.aluResult <= _io_aluResult_T_5 @[ALU.scala 57:34]
          else :
            node _T_12 = asUInt(UInt<3>("h4")) @[ALU.scala 52:24]
            node _T_13 = asUInt(io.operation) @[ALU.scala 52:24]
            node _T_14 = eq(_T_12, _T_13) @[ALU.scala 52:24]
            when _T_14 : @[ALU.scala 52:24]
              node _io_aluResult_T_6 = xor(io.operandA, io.operandB) @[ALU.scala 58:49]
              io.aluResult <= _io_aluResult_T_6 @[ALU.scala 58:34]
            else :
              node _T_15 = asUInt(UInt<3>("h5")) @[ALU.scala 52:24]
              node _T_16 = asUInt(io.operation) @[ALU.scala 52:24]
              node _T_17 = eq(_T_15, _T_16) @[ALU.scala 52:24]
              when _T_17 : @[ALU.scala 52:24]
                node _io_aluResult_T_7 = dshl(io.operandA, shift_amount) @[ALU.scala 59:50]
                io.aluResult <= _io_aluResult_T_7 @[ALU.scala 59:34]
              else :
                node _T_18 = asUInt(UInt<3>("h6")) @[ALU.scala 52:24]
                node _T_19 = asUInt(io.operation) @[ALU.scala 52:24]
                node _T_20 = eq(_T_18, _T_19) @[ALU.scala 52:24]
                when _T_20 : @[ALU.scala 52:24]
                  node _io_aluResult_T_8 = dshr(io.operandA, shift_amount) @[ALU.scala 60:49]
                  io.aluResult <= _io_aluResult_T_8 @[ALU.scala 60:34]
                else :
                  node _T_21 = asUInt(UInt<3>("h7")) @[ALU.scala 52:24]
                  node _T_22 = asUInt(io.operation) @[ALU.scala 52:24]
                  node _T_23 = eq(_T_21, _T_22) @[ALU.scala 52:24]
                  when _T_23 : @[ALU.scala 52:24]
                    node _io_aluResult_T_9 = asSInt(io.operandA) @[ALU.scala 61:50]
                    node _io_aluResult_T_10 = dshr(_io_aluResult_T_9, shift_amount) @[ALU.scala 61:57]
                    node _io_aluResult_T_11 = asUInt(_io_aluResult_T_10) @[ALU.scala 61:74]
                    io.aluResult <= _io_aluResult_T_11 @[ALU.scala 61:34]
                  else :
                    node _T_24 = asUInt(UInt<4>("h8")) @[ALU.scala 52:24]
                    node _T_25 = asUInt(io.operation) @[ALU.scala 52:24]
                    node _T_26 = eq(_T_24, _T_25) @[ALU.scala 52:24]
                    when _T_26 : @[ALU.scala 52:24]
                      node _io_aluResult_T_12 = asSInt(io.operandA) @[ALU.scala 62:50]
                      node _io_aluResult_T_13 = asSInt(io.operandB) @[ALU.scala 62:71]
                      node _io_aluResult_T_14 = lt(_io_aluResult_T_12, _io_aluResult_T_13) @[ALU.scala 62:57]
                      io.aluResult <= _io_aluResult_T_14 @[ALU.scala 62:34]
                    else :
                      node _T_27 = asUInt(UInt<4>("h9")) @[ALU.scala 52:24]
                      node _T_28 = asUInt(io.operation) @[ALU.scala 52:24]
                      node _T_29 = eq(_T_27, _T_28) @[ALU.scala 52:24]
                      when _T_29 : @[ALU.scala 52:24]
                        node _io_aluResult_T_15 = lt(io.operandA, io.operandB) @[ALU.scala 63:50]
                        io.aluResult <= _io_aluResult_T_15 @[ALU.scala 63:34]
                      else :
                        node _T_30 = asUInt(UInt<4>("ha")) @[ALU.scala 52:24]
                        node _T_31 = asUInt(io.operation) @[ALU.scala 52:24]
                        node _T_32 = eq(_T_30, _T_31) @[ALU.scala 52:24]
                        when _T_32 : @[ALU.scala 52:24]
                          io.aluResult <= UInt<1>("h0") @[ALU.scala 64:35]
                          node _io_zero_T_1 = eq(io.operandA, io.operandB) @[ALU.scala 64:66]
                          io.zero <= _io_zero_T_1 @[ALU.scala 64:51]
                        else :
                          node _T_33 = asUInt(UInt<4>("hb")) @[ALU.scala 52:24]
                          node _T_34 = asUInt(io.operation) @[ALU.scala 52:24]
                          node _T_35 = eq(_T_33, _T_34) @[ALU.scala 52:24]
                          when _T_35 : @[ALU.scala 52:24]
                            io.aluResult <= UInt<1>("h0") @[ALU.scala 65:35]
                            node _io_zero_T_2 = neq(io.operandA, io.operandB) @[ALU.scala 65:66]
                            io.zero <= _io_zero_T_2 @[ALU.scala 65:51]
                          else :
                            node _T_36 = asUInt(UInt<4>("hc")) @[ALU.scala 52:24]
                            node _T_37 = asUInt(io.operation) @[ALU.scala 52:24]
                            node _T_38 = eq(_T_36, _T_37) @[ALU.scala 52:24]
                            when _T_38 : @[ALU.scala 52:24]
                              io.aluResult <= UInt<1>("h0") @[ALU.scala 66:35]
                              node _io_zero_T_3 = asSInt(io.operandA) @[ALU.scala 66:66]
                              node _io_zero_T_4 = asSInt(io.operandB) @[ALU.scala 66:87]
                              node _io_zero_T_5 = lt(_io_zero_T_3, _io_zero_T_4) @[ALU.scala 66:73]
                              io.zero <= _io_zero_T_5 @[ALU.scala 66:51]
                            else :
                              node _T_39 = asUInt(UInt<4>("hd")) @[ALU.scala 52:24]
                              node _T_40 = asUInt(io.operation) @[ALU.scala 52:24]
                              node _T_41 = eq(_T_39, _T_40) @[ALU.scala 52:24]
                              when _T_41 : @[ALU.scala 52:24]
                                io.aluResult <= UInt<1>("h0") @[ALU.scala 67:35]
                                node _io_zero_T_6 = asSInt(io.operandA) @[ALU.scala 67:66]
                                node _io_zero_T_7 = asSInt(io.operandB) @[ALU.scala 67:88]
                                node _io_zero_T_8 = geq(_io_zero_T_6, _io_zero_T_7) @[ALU.scala 67:73]
                                io.zero <= _io_zero_T_8 @[ALU.scala 67:51]
                              else :
                                node _T_42 = asUInt(UInt<4>("he")) @[ALU.scala 52:24]
                                node _T_43 = asUInt(io.operation) @[ALU.scala 52:24]
                                node _T_44 = eq(_T_42, _T_43) @[ALU.scala 52:24]
                                when _T_44 : @[ALU.scala 52:24]
                                  io.aluResult <= UInt<1>("h0") @[ALU.scala 68:35]
                                  node _io_zero_T_9 = lt(io.operandA, io.operandB) @[ALU.scala 68:66]
                                  io.zero <= _io_zero_T_9 @[ALU.scala 68:51]
                                else :
                                  node _T_45 = asUInt(UInt<4>("hf")) @[ALU.scala 52:24]
                                  node _T_46 = asUInt(io.operation) @[ALU.scala 52:24]
                                  node _T_47 = eq(_T_45, _T_46) @[ALU.scala 52:24]
                                  when _T_47 : @[ALU.scala 52:24]
                                    io.aluResult <= UInt<1>("h0") @[ALU.scala 69:35]
                                    node _io_zero_T_10 = geq(io.operandA, io.operandB) @[ALU.scala 69:66]
                                    io.zero <= _io_zero_T_10 @[ALU.scala 69:51]

  module ALUcontrol :
    input clock : Clock
    input reset : Reset
    output io : { flip uop : UInt<5>, mapped : UInt<4>}

    io.mapped <= UInt<1>("h0") @[EXstage.scala 49:13]
    node _T = asUInt(UInt<1>("h0")) @[EXstage.scala 50:18]
    node _T_1 = asUInt(io.uop) @[EXstage.scala 50:18]
    node _T_2 = eq(_T, _T_1) @[EXstage.scala 50:18]
    node _T_3 = asUInt(UInt<4>("hb")) @[EXstage.scala 50:18]
    node _T_4 = asUInt(io.uop) @[EXstage.scala 50:18]
    node _T_5 = eq(_T_3, _T_4) @[EXstage.scala 50:18]
    node _T_6 = or(_T_2, _T_5) @[EXstage.scala 50:18]
    when _T_6 : @[EXstage.scala 50:18]
      io.mapped <= UInt<1>("h0") @[EXstage.scala 51:43]
    else :
      node _T_7 = asUInt(UInt<1>("h1")) @[EXstage.scala 50:18]
      node _T_8 = asUInt(io.uop) @[EXstage.scala 50:18]
      node _T_9 = eq(_T_7, _T_8) @[EXstage.scala 50:18]
      when _T_9 : @[EXstage.scala 50:18]
        io.mapped <= UInt<1>("h1") @[EXstage.scala 52:43]
      else :
        node _T_10 = asUInt(UInt<2>("h2")) @[EXstage.scala 50:18]
        node _T_11 = asUInt(io.uop) @[EXstage.scala 50:18]
        node _T_12 = eq(_T_10, _T_11) @[EXstage.scala 50:18]
        node _T_13 = asUInt(UInt<4>("he")) @[EXstage.scala 50:18]
        node _T_14 = asUInt(io.uop) @[EXstage.scala 50:18]
        node _T_15 = eq(_T_13, _T_14) @[EXstage.scala 50:18]
        node _T_16 = or(_T_12, _T_15) @[EXstage.scala 50:18]
        when _T_16 : @[EXstage.scala 50:18]
          io.mapped <= UInt<2>("h2") @[EXstage.scala 53:43]
        else :
          node _T_17 = asUInt(UInt<2>("h3")) @[EXstage.scala 50:18]
          node _T_18 = asUInt(io.uop) @[EXstage.scala 50:18]
          node _T_19 = eq(_T_17, _T_18) @[EXstage.scala 50:18]
          node _T_20 = asUInt(UInt<4>("hd")) @[EXstage.scala 50:18]
          node _T_21 = asUInt(io.uop) @[EXstage.scala 50:18]
          node _T_22 = eq(_T_20, _T_21) @[EXstage.scala 50:18]
          node _T_23 = or(_T_19, _T_22) @[EXstage.scala 50:18]
          when _T_23 : @[EXstage.scala 50:18]
            io.mapped <= UInt<2>("h3") @[EXstage.scala 54:43]
          else :
            node _T_24 = asUInt(UInt<3>("h4")) @[EXstage.scala 50:18]
            node _T_25 = asUInt(io.uop) @[EXstage.scala 50:18]
            node _T_26 = eq(_T_24, _T_25) @[EXstage.scala 50:18]
            node _T_27 = asUInt(UInt<4>("hc")) @[EXstage.scala 50:18]
            node _T_28 = asUInt(io.uop) @[EXstage.scala 50:18]
            node _T_29 = eq(_T_27, _T_28) @[EXstage.scala 50:18]
            node _T_30 = or(_T_26, _T_29) @[EXstage.scala 50:18]
            when _T_30 : @[EXstage.scala 50:18]
              io.mapped <= UInt<3>("h4") @[EXstage.scala 55:43]
            else :
              node _T_31 = asUInt(UInt<3>("h5")) @[EXstage.scala 50:18]
              node _T_32 = asUInt(io.uop) @[EXstage.scala 50:18]
              node _T_33 = eq(_T_31, _T_32) @[EXstage.scala 50:18]
              node _T_34 = asUInt(UInt<4>("hf")) @[EXstage.scala 50:18]
              node _T_35 = asUInt(io.uop) @[EXstage.scala 50:18]
              node _T_36 = eq(_T_34, _T_35) @[EXstage.scala 50:18]
              node _T_37 = or(_T_33, _T_36) @[EXstage.scala 50:18]
              when _T_37 : @[EXstage.scala 50:18]
                io.mapped <= UInt<3>("h5") @[EXstage.scala 56:43]
              else :
                node _T_38 = asUInt(UInt<3>("h6")) @[EXstage.scala 50:18]
                node _T_39 = asUInt(io.uop) @[EXstage.scala 50:18]
                node _T_40 = eq(_T_38, _T_39) @[EXstage.scala 50:18]
                node _T_41 = asUInt(UInt<5>("h11")) @[EXstage.scala 50:18]
                node _T_42 = asUInt(io.uop) @[EXstage.scala 50:18]
                node _T_43 = eq(_T_41, _T_42) @[EXstage.scala 50:18]
                node _T_44 = or(_T_40, _T_43) @[EXstage.scala 50:18]
                when _T_44 : @[EXstage.scala 50:18]
                  io.mapped <= UInt<3>("h6") @[EXstage.scala 57:43]
                else :
                  node _T_45 = asUInt(UInt<3>("h7")) @[EXstage.scala 50:18]
                  node _T_46 = asUInt(io.uop) @[EXstage.scala 50:18]
                  node _T_47 = eq(_T_45, _T_46) @[EXstage.scala 50:18]
                  node _T_48 = asUInt(UInt<5>("h12")) @[EXstage.scala 50:18]
                  node _T_49 = asUInt(io.uop) @[EXstage.scala 50:18]
                  node _T_50 = eq(_T_48, _T_49) @[EXstage.scala 50:18]
                  node _T_51 = or(_T_47, _T_50) @[EXstage.scala 50:18]
                  when _T_51 : @[EXstage.scala 50:18]
                    io.mapped <= UInt<3>("h7") @[EXstage.scala 58:43]
                  else :
                    node _T_52 = asUInt(UInt<4>("h8")) @[EXstage.scala 50:18]
                    node _T_53 = asUInt(io.uop) @[EXstage.scala 50:18]
                    node _T_54 = eq(_T_52, _T_53) @[EXstage.scala 50:18]
                    node _T_55 = asUInt(UInt<4>("ha")) @[EXstage.scala 50:18]
                    node _T_56 = asUInt(io.uop) @[EXstage.scala 50:18]
                    node _T_57 = eq(_T_55, _T_56) @[EXstage.scala 50:18]
                    node _T_58 = or(_T_54, _T_57) @[EXstage.scala 50:18]
                    when _T_58 : @[EXstage.scala 50:18]
                      io.mapped <= UInt<4>("h8") @[EXstage.scala 59:43]
                    else :
                      node _T_59 = asUInt(UInt<4>("h9")) @[EXstage.scala 50:18]
                      node _T_60 = asUInt(io.uop) @[EXstage.scala 50:18]
                      node _T_61 = eq(_T_59, _T_60) @[EXstage.scala 50:18]
                      node _T_62 = asUInt(UInt<5>("h10")) @[EXstage.scala 50:18]
                      node _T_63 = asUInt(io.uop) @[EXstage.scala 50:18]
                      node _T_64 = eq(_T_62, _T_63) @[EXstage.scala 50:18]
                      node _T_65 = or(_T_61, _T_64) @[EXstage.scala 50:18]
                      when _T_65 : @[EXstage.scala 50:18]
                        io.mapped <= UInt<4>("h9") @[EXstage.scala 60:43]
                      else :
                        node _T_66 = asUInt(UInt<5>("h14")) @[EXstage.scala 50:18]
                        node _T_67 = asUInt(io.uop) @[EXstage.scala 50:18]
                        node _T_68 = eq(_T_66, _T_67) @[EXstage.scala 50:18]
                        when _T_68 : @[EXstage.scala 50:18]
                          io.mapped <= UInt<4>("ha") @[EXstage.scala 61:31]
                        else :
                          node _T_69 = asUInt(UInt<5>("h15")) @[EXstage.scala 50:18]
                          node _T_70 = asUInt(io.uop) @[EXstage.scala 50:18]
                          node _T_71 = eq(_T_69, _T_70) @[EXstage.scala 50:18]
                          when _T_71 : @[EXstage.scala 50:18]
                            io.mapped <= UInt<4>("hb") @[EXstage.scala 62:31]
                          else :
                            node _T_72 = asUInt(UInt<5>("h16")) @[EXstage.scala 50:18]
                            node _T_73 = asUInt(io.uop) @[EXstage.scala 50:18]
                            node _T_74 = eq(_T_72, _T_73) @[EXstage.scala 50:18]
                            when _T_74 : @[EXstage.scala 50:18]
                              io.mapped <= UInt<4>("hc") @[EXstage.scala 63:31]
                            else :
                              node _T_75 = asUInt(UInt<5>("h17")) @[EXstage.scala 50:18]
                              node _T_76 = asUInt(io.uop) @[EXstage.scala 50:18]
                              node _T_77 = eq(_T_75, _T_76) @[EXstage.scala 50:18]
                              when _T_77 : @[EXstage.scala 50:18]
                                io.mapped <= UInt<4>("hd") @[EXstage.scala 64:31]
                              else :
                                node _T_78 = asUInt(UInt<5>("h18")) @[EXstage.scala 50:18]
                                node _T_79 = asUInt(io.uop) @[EXstage.scala 50:18]
                                node _T_80 = eq(_T_78, _T_79) @[EXstage.scala 50:18]
                                when _T_80 : @[EXstage.scala 50:18]
                                  io.mapped <= UInt<4>("he") @[EXstage.scala 65:31]
                                else :
                                  node _T_81 = asUInt(UInt<5>("h19")) @[EXstage.scala 50:18]
                                  node _T_82 = asUInt(io.uop) @[EXstage.scala 50:18]
                                  node _T_83 = eq(_T_81, _T_82) @[EXstage.scala 50:18]
                                  when _T_83 : @[EXstage.scala 50:18]
                                    io.mapped <= UInt<4>("hf") @[EXstage.scala 66:31]
                                  else :
                                    node _T_84 = asUInt(UInt<5>("h1a")) @[EXstage.scala 50:18]
                                    node _T_85 = asUInt(io.uop) @[EXstage.scala 50:18]
                                    node _T_86 = eq(_T_84, _T_85) @[EXstage.scala 50:18]
                                    node _T_87 = asUInt(UInt<5>("h1b")) @[EXstage.scala 50:18]
                                    node _T_88 = asUInt(io.uop) @[EXstage.scala 50:18]
                                    node _T_89 = eq(_T_87, _T_88) @[EXstage.scala 50:18]
                                    node _T_90 = or(_T_86, _T_89) @[EXstage.scala 50:18]
                                    when _T_90 : @[EXstage.scala 50:18]
                                      io.mapped <= UInt<1>("h0") @[EXstage.scala 67:43]

  module EXstage :
    input clock : Clock
    input reset : Reset
    output io : { flip RD1E : UInt<32>, flip RD2E : UInt<32>, flip ImmExtE : UInt<32>, flip ALUSrcE : UInt<1>, flip rdE : UInt<5>, flip uopE : UInt<5>, flip RegWriteE : UInt<1>, flip XcptInvalidE : UInt<1>, flip ForwardAE : UInt<2>, flip ForwardBE : UInt<2>, flip ResultW : UInt<32>, flip ALUResultM : UInt<32>, flip PCE : UInt<32>, flip PCPlus4E : UInt<32>, flip BranchE : UInt<1>, flip JumpE : UInt<1>, ALUResultE : UInt<32>, exceptionE : UInt<1>, rdOutE : UInt<5>, RegWriteOutE : UInt<1>, PCSrcE : UInt<1>, PCTargetE : UInt<32>}

    inst ALU of ALU @[EXstage.scala 96:26]
    ALU.clock <= clock
    ALU.reset <= reset
    inst ALUcontrol of ALUcontrol @[EXstage.scala 97:26]
    ALUcontrol.clock <= clock
    ALUcontrol.reset <= reset
    ALUcontrol.io.uop <= io.uopE @[EXstage.scala 98:21]
    wire srcA : UInt<32> @[EXstage.scala 100:18]
    wire srcB : UInt<32> @[EXstage.scala 101:18]
    srcA <= io.RD1E @[EXstage.scala 102:8]
    node _T = eq(UInt<2>("h2"), io.ForwardAE) @[EXstage.scala 103:24]
    when _T : @[EXstage.scala 103:24]
      srcA <= io.ALUResultM @[EXstage.scala 104:24]
    else :
      node _T_1 = eq(UInt<1>("h1"), io.ForwardAE) @[EXstage.scala 103:24]
      when _T_1 : @[EXstage.scala 103:24]
        srcA <= io.ResultW @[EXstage.scala 105:24]
    srcB <= io.RD2E @[EXstage.scala 107:8]
    node _T_2 = eq(UInt<2>("h2"), io.ForwardBE) @[EXstage.scala 108:24]
    when _T_2 : @[EXstage.scala 108:24]
      srcB <= io.ALUResultM @[EXstage.scala 109:24]
    else :
      node _T_3 = eq(UInt<1>("h1"), io.ForwardBE) @[EXstage.scala 108:24]
      when _T_3 : @[EXstage.scala 108:24]
        srcB <= io.ResultW @[EXstage.scala 110:24]
    ALU.io.operandA <= srcA @[EXstage.scala 113:20]
    node _ALU_io_operandB_T = mux(io.ALUSrcE, io.ImmExtE, srcB) @[EXstage.scala 114:26]
    ALU.io.operandB <= _ALU_io_operandB_T @[EXstage.scala 114:20]
    ALU.io.operation <= ALUcontrol.io.mapped @[EXstage.scala 115:20]
    node jalr = and(io.JumpE, io.BranchE) @[EXstage.scala 117:23]
    node _pcTargetAdder_T = add(io.PCE, io.ImmExtE) @[EXstage.scala 118:30]
    node pcTargetAdder = tail(_pcTargetAdder_T, 1) @[EXstage.scala 118:30]
    node _io_PCSrcE_T = and(io.BranchE, ALU.io.zero) @[EXstage.scala 120:31]
    node _io_PCSrcE_T_1 = or(_io_PCSrcE_T, io.JumpE) @[EXstage.scala 120:47]
    io.PCSrcE <= _io_PCSrcE_T_1 @[EXstage.scala 120:16]
    node _io_PCTargetE_T = not(UInt<32>("h1")) @[EXstage.scala 121:49]
    node _io_PCTargetE_T_1 = and(ALU.io.aluResult, _io_PCTargetE_T) @[EXstage.scala 121:47]
    node _io_PCTargetE_T_2 = mux(jalr, _io_PCTargetE_T_1, pcTargetAdder) @[EXstage.scala 121:22]
    io.PCTargetE <= _io_PCTargetE_T_2 @[EXstage.scala 121:16]
    node _io_ALUResultE_T = mux(io.JumpE, io.PCPlus4E, ALU.io.aluResult) @[EXstage.scala 123:25]
    io.ALUResultE <= _io_ALUResultE_T @[EXstage.scala 123:19]
    io.exceptionE <= io.XcptInvalidE @[EXstage.scala 124:19]
    io.rdOutE <= io.rdE @[EXstage.scala 125:19]
    io.RegWriteOutE <= io.RegWriteE @[EXstage.scala 126:19]

  module EXBarrier :
    input clock : Clock
    input reset : Reset
    output io : { flip ALUResultE : UInt<32>, flip rdE : UInt<5>, flip XcptInvalidE : UInt<1>, flip RegWriteE : UInt<1>, ALUResultM : UInt<32>, rdM : UInt<5>, XcptInvalidM : UInt<1>, RegWriteM : UInt<1>}

    reg ALUResult : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[EXbarrier.scala 50:28]
    reg rd : UInt<5>, clock with :
      reset => (reset, UInt<5>("h0")) @[EXbarrier.scala 51:28]
    reg XcptInvalid : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[EXbarrier.scala 52:28]
    reg RegWrite : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[EXbarrier.scala 53:28]
    ALUResult <= io.ALUResultE @[EXbarrier.scala 55:15]
    rd <= io.rdE @[EXbarrier.scala 56:15]
    XcptInvalid <= io.XcptInvalidE @[EXbarrier.scala 57:15]
    RegWrite <= io.RegWriteE @[EXbarrier.scala 58:15]
    io.ALUResultM <= ALUResult @[EXbarrier.scala 60:19]
    io.rdM <= rd @[EXbarrier.scala 61:19]
    io.XcptInvalidM <= XcptInvalid @[EXbarrier.scala 62:19]
    io.RegWriteM <= RegWrite @[EXbarrier.scala 63:19]

  module MEMBarrier :
    input clock : Clock
    input reset : Reset
    output io : { flip ALUResultM : UInt<32>, flip rdM : UInt<5>, flip XcptInvalidM : UInt<1>, flip RegWriteM : UInt<1>, ALUResultW : UInt<32>, rdW : UInt<5>, XcptInvalidW : UInt<1>, RegWriteW : UInt<1>}

    reg ALUResult : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[MEMbarrier.scala 50:30]
    reg rd : UInt<5>, clock with :
      reset => (reset, UInt<5>("h0")) @[MEMbarrier.scala 51:30]
    reg XcptInvalid : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[MEMbarrier.scala 52:30]
    reg RegWrite : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[MEMbarrier.scala 53:30]
    ALUResult <= io.ALUResultM @[MEMbarrier.scala 55:17]
    rd <= io.rdM @[MEMbarrier.scala 56:17]
    XcptInvalid <= io.XcptInvalidM @[MEMbarrier.scala 57:17]
    RegWrite <= io.RegWriteM @[MEMbarrier.scala 58:17]
    io.ALUResultW <= ALUResult @[MEMbarrier.scala 60:21]
    io.rdW <= rd @[MEMbarrier.scala 61:21]
    io.XcptInvalidW <= XcptInvalid @[MEMbarrier.scala 62:21]
    io.RegWriteW <= RegWrite @[MEMbarrier.scala 63:21]

  module WBstage :
    input clock : Clock
    input reset : Reset
    output io : { flip ALUResultW : UInt<32>, flip rdW : UInt<5>, flip RegWriteW : UInt<1>, RegWriteReq : { addr : UInt<5>, data : UInt<32>, w_en : UInt<1>}, ResultW : UInt<32>}

    io.RegWriteReq.addr <= io.rdW @[WBstage.scala 58:23]
    io.RegWriteReq.data <= io.ALUResultW @[WBstage.scala 59:23]
    io.RegWriteReq.w_en <= io.RegWriteW @[WBstage.scala 60:23]
    io.ResultW <= io.ALUResultW @[WBstage.scala 61:24]

  module WBBarrier :
    input clock : Clock
    input reset : Reset
    output io : { flip ResultW : UInt<32>, flip XcptInvalidW : UInt<1>, check_res : UInt<32>, exception : UInt<1>}

    reg check_res : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[WBbarrier.scala 46:26]
    reg isInvalid : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[WBbarrier.scala 47:26]
    check_res <= io.ResultW @[WBbarrier.scala 49:13]
    isInvalid <= io.XcptInvalidW @[WBbarrier.scala 50:13]
    io.check_res <= check_res @[WBbarrier.scala 52:16]
    io.exception <= isInvalid @[WBbarrier.scala 53:16]

  module ForwardingUnit :
    input clock : Clock
    input reset : Reset
    output io : { flip rs1_EX : UInt<32>, flip rs2_EX : UInt<32>, flip rd_MEM : UInt<32>, flip rd_WB : UInt<32>, flip wrEn_MEM : UInt<1>, flip wrEn_WB : UInt<1>, forwardA : UInt<2>, forwardB : UInt<2>}

    node _T = eq(io.rs1_EX, io.rd_MEM) @[ForwardingUnit.scala 54:19]
    node _T_1 = and(_T, io.wrEn_MEM) @[ForwardingUnit.scala 54:34]
    node _T_2 = neq(io.rs1_EX, UInt<1>("h0")) @[ForwardingUnit.scala 54:63]
    node _T_3 = and(_T_1, _T_2) @[ForwardingUnit.scala 54:49]
    when _T_3 : @[ForwardingUnit.scala 54:73]
      io.forwardA <= UInt<2>("h2") @[ForwardingUnit.scala 55:17]
    else :
      node _T_4 = eq(io.rs1_EX, io.rd_WB) @[ForwardingUnit.scala 56:25]
      node _T_5 = and(_T_4, io.wrEn_WB) @[ForwardingUnit.scala 56:39]
      node _T_6 = neq(io.rs1_EX, UInt<1>("h0")) @[ForwardingUnit.scala 56:67]
      node _T_7 = and(_T_5, _T_6) @[ForwardingUnit.scala 56:53]
      when _T_7 : @[ForwardingUnit.scala 56:77]
        io.forwardA <= UInt<1>("h1") @[ForwardingUnit.scala 57:17]
      else :
        io.forwardA <= UInt<1>("h0") @[ForwardingUnit.scala 59:17]
    node _T_8 = eq(io.rs2_EX, io.rd_MEM) @[ForwardingUnit.scala 63:19]
    node _T_9 = and(_T_8, io.wrEn_MEM) @[ForwardingUnit.scala 63:34]
    node _T_10 = neq(io.rs2_EX, UInt<1>("h0")) @[ForwardingUnit.scala 63:63]
    node _T_11 = and(_T_9, _T_10) @[ForwardingUnit.scala 63:49]
    when _T_11 : @[ForwardingUnit.scala 63:73]
      io.forwardB <= UInt<2>("h2") @[ForwardingUnit.scala 64:17]
    else :
      node _T_12 = eq(io.rs2_EX, io.rd_WB) @[ForwardingUnit.scala 65:25]
      node _T_13 = and(_T_12, io.wrEn_WB) @[ForwardingUnit.scala 65:39]
      node _T_14 = neq(io.rs2_EX, UInt<1>("h0")) @[ForwardingUnit.scala 65:67]
      node _T_15 = and(_T_13, _T_14) @[ForwardingUnit.scala 65:53]
      when _T_15 : @[ForwardingUnit.scala 65:77]
        io.forwardB <= UInt<1>("h1") @[ForwardingUnit.scala 66:17]
      else :
        io.forwardB <= UInt<1>("h0") @[ForwardingUnit.scala 68:17]

  module PipelinedRV32Icore :
    input clock : Clock
    input reset : Reset
    output io : { check_res : UInt<32>, exception : UInt<1>, PCdebug : UInt<32>, InstrDdebug : UInt<32>, PCEdebug : UInt<32>, RS1Edebug : UInt<32>, RS2Edebug : UInt<32>, BranchEdebug : UInt<1>, JumpEdebug : UInt<1>, PCSrcEdebug : UInt<1>, PCTargetEdebug : UInt<32>, RegWriteWdebug : UInt<1>, rdWdebug : UInt<5>, PCSrcE_debug : UInt<1>}

    inst IFstage of IF @[core.scala 84:25]
    IFstage.clock <= clock
    IFstage.reset <= reset
    inst IFBarrier of IFBarrier @[core.scala 85:25]
    IFBarrier.clock <= clock
    IFBarrier.reset <= reset
    inst IDstage of ID @[core.scala 87:25]
    IDstage.clock <= clock
    IDstage.reset <= reset
    inst IDBarrier of IDBarrier @[core.scala 88:25]
    IDBarrier.clock <= clock
    IDBarrier.reset <= reset
    inst EXstage of EXstage @[core.scala 90:25]
    EXstage.clock <= clock
    EXstage.reset <= reset
    inst EXBarrier of EXBarrier @[core.scala 91:25]
    EXBarrier.clock <= clock
    EXBarrier.reset <= reset
    inst MEMBarrier of MEMBarrier @[core.scala 94:26]
    MEMBarrier.clock <= clock
    MEMBarrier.reset <= reset
    inst WBstage of WBstage @[core.scala 96:25]
    WBstage.clock <= clock
    WBstage.reset <= reset
    inst WBBarrier of WBBarrier @[core.scala 97:25]
    WBBarrier.clock <= clock
    WBBarrier.reset <= reset
    inst ForwardingUnit of ForwardingUnit @[core.scala 99:30]
    ForwardingUnit.clock <= clock
    ForwardingUnit.reset <= reset
    IFstage.io.PCSrcE <= EXstage.io.PCSrcE @[core.scala 102:24]
    IFstage.io.PCTargetE <= EXstage.io.PCTargetE @[core.scala 103:24]
    IFBarrier.io.InstrF <= IFstage.io.InstrF @[core.scala 106:25]
    IFBarrier.io.PCF <= IFstage.io.PCF @[core.scala 107:25]
    IFBarrier.io.PCPlus4F <= IFstage.io.PCPlus4F @[core.scala 108:25]
    IFBarrier.io.CLR <= EXstage.io.PCSrcE @[core.scala 109:25]
    IDstage.io.inst <= IFBarrier.io.InstrD @[core.scala 112:26]
    IDstage.io.pcD <= IFBarrier.io.PCD @[core.scala 113:26]
    IDstage.io.pcPlus4D <= IFBarrier.io.PCPlus4D @[core.scala 114:26]
    IDstage.io.RegWriteW <= WBstage.io.RegWriteReq.w_en @[core.scala 115:26]
    IDstage.io.rdW <= WBstage.io.RegWriteReq.addr @[core.scala 116:26]
    IDstage.io.ResultW <= WBstage.io.RegWriteReq.data @[core.scala 117:26]
    IDBarrier.io.uopD <= IDstage.io.uop @[core.scala 120:29]
    IDBarrier.io.rdD <= IDstage.io.rdD @[core.scala 121:29]
    IDBarrier.io.RD1D <= IDstage.io.RD1D @[core.scala 122:29]
    IDBarrier.io.RD2D <= IDstage.io.RD2D @[core.scala 123:29]
    IDBarrier.io.XcptInvalidD <= IDstage.io.XcptInvalid @[core.scala 124:29]
    IDBarrier.io.RegWriteD <= IDstage.io.RegWriteD @[core.scala 125:29]
    IDBarrier.io.ALUSrcD <= IDstage.io.ALUSrcD @[core.scala 126:29]
    IDBarrier.io.ImmExtD <= IDstage.io.ImmExtD @[core.scala 127:29]
    IDBarrier.io.BranchD <= IDstage.io.BranchD @[core.scala 128:29]
    IDBarrier.io.JumpD <= IDstage.io.JumpD @[core.scala 129:29]
    IDBarrier.io.PCD <= IDstage.io.pcD_out @[core.scala 130:29]
    IDBarrier.io.PCPlus4D <= IDstage.io.pcPlus4D_out @[core.scala 131:29]
    node _IDBarrier_io_rs1D_T = bits(IFBarrier.io.InstrD, 19, 15) @[core.scala 132:51]
    IDBarrier.io.rs1D <= _IDBarrier_io_rs1D_T @[core.scala 132:29]
    node _IDBarrier_io_rs2D_T = bits(IFBarrier.io.InstrD, 24, 20) @[core.scala 133:51]
    IDBarrier.io.rs2D <= _IDBarrier_io_rs2D_T @[core.scala 133:29]
    IDBarrier.io.CLR <= EXstage.io.PCSrcE @[core.scala 134:29]
    EXstage.io.RD1E <= IDBarrier.io.RD1E @[core.scala 137:27]
    EXstage.io.RD2E <= IDBarrier.io.RD2E @[core.scala 138:27]
    EXstage.io.ImmExtE <= IDBarrier.io.ImmExtE @[core.scala 139:27]
    EXstage.io.ALUSrcE <= IDBarrier.io.ALUSrcE @[core.scala 140:27]
    EXstage.io.rdE <= IDBarrier.io.rdE @[core.scala 141:27]
    EXstage.io.uopE <= IDBarrier.io.uopE @[core.scala 142:27]
    EXstage.io.RegWriteE <= IDBarrier.io.RegWriteE @[core.scala 143:27]
    EXstage.io.XcptInvalidE <= IDBarrier.io.XcptInvalidE @[core.scala 144:27]
    EXstage.io.PCE <= IDBarrier.io.PCE @[core.scala 145:27]
    EXstage.io.PCPlus4E <= IDBarrier.io.PCPlus4E @[core.scala 146:27]
    EXstage.io.BranchE <= IDBarrier.io.BranchE @[core.scala 147:27]
    EXstage.io.JumpE <= IDBarrier.io.JumpE @[core.scala 148:27]
    EXBarrier.io.ALUResultE <= EXstage.io.ALUResultE @[core.scala 151:29]
    EXBarrier.io.rdE <= EXstage.io.rdOutE @[core.scala 152:29]
    EXBarrier.io.XcptInvalidE <= EXstage.io.exceptionE @[core.scala 153:29]
    EXBarrier.io.RegWriteE <= EXstage.io.RegWriteOutE @[core.scala 154:29]
    MEMBarrier.io.ALUResultM <= EXBarrier.io.ALUResultM @[core.scala 157:30]
    MEMBarrier.io.rdM <= EXBarrier.io.rdM @[core.scala 158:30]
    MEMBarrier.io.XcptInvalidM <= EXBarrier.io.XcptInvalidM @[core.scala 159:30]
    MEMBarrier.io.RegWriteM <= EXBarrier.io.RegWriteM @[core.scala 160:30]
    WBstage.io.ALUResultW <= MEMBarrier.io.ALUResultW @[core.scala 163:25]
    WBstage.io.rdW <= MEMBarrier.io.rdW @[core.scala 164:25]
    WBstage.io.RegWriteW <= MEMBarrier.io.RegWriteW @[core.scala 165:25]
    WBBarrier.io.ResultW <= WBstage.io.ResultW @[core.scala 168:29]
    WBBarrier.io.XcptInvalidW <= MEMBarrier.io.XcptInvalidW @[core.scala 169:29]
    ForwardingUnit.io.rs1_EX <= IDBarrier.io.Rs1E @[core.scala 172:30]
    ForwardingUnit.io.rs2_EX <= IDBarrier.io.Rs2E @[core.scala 173:30]
    ForwardingUnit.io.rd_MEM <= EXBarrier.io.rdM @[core.scala 175:30]
    ForwardingUnit.io.wrEn_MEM <= EXBarrier.io.RegWriteM @[core.scala 176:30]
    ForwardingUnit.io.rd_WB <= MEMBarrier.io.rdW @[core.scala 178:30]
    ForwardingUnit.io.wrEn_WB <= MEMBarrier.io.RegWriteW @[core.scala 179:30]
    EXstage.io.ForwardAE <= ForwardingUnit.io.forwardA @[core.scala 181:25]
    EXstage.io.ForwardBE <= ForwardingUnit.io.forwardB @[core.scala 182:25]
    EXstage.io.ALUResultM <= EXBarrier.io.ALUResultM @[core.scala 183:25]
    EXstage.io.ResultW <= WBstage.io.ResultW @[core.scala 184:25]
    io.check_res <= WBBarrier.io.check_res @[core.scala 187:16]
    io.exception <= WBBarrier.io.exception @[core.scala 188:16]
    io.PCdebug <= IFstage.io.PCF @[core.scala 191:21]
    io.InstrDdebug <= IFBarrier.io.InstrD @[core.scala 192:21]
    io.PCEdebug <= IDBarrier.io.PCE @[core.scala 193:21]
    io.RS1Edebug <= IDBarrier.io.RD1E @[core.scala 194:21]
    io.RS2Edebug <= IDBarrier.io.RD2E @[core.scala 195:21]
    io.BranchEdebug <= IDBarrier.io.BranchE @[core.scala 196:21]
    io.JumpEdebug <= IDBarrier.io.JumpE @[core.scala 197:21]
    io.PCSrcEdebug <= EXstage.io.PCSrcE @[core.scala 198:21]
    io.PCTargetEdebug <= EXstage.io.PCTargetE @[core.scala 199:21]
    io.RegWriteWdebug <= MEMBarrier.io.RegWriteW @[core.scala 200:21]
    io.rdWdebug <= MEMBarrier.io.rdW @[core.scala 201:21]
    io.PCSrcE_debug <= EXstage.io.PCSrcE @[core.scala 203:24]

  module PipelinedRV32I :
    input clock : Clock
    input reset : UInt<1>
    output io : { result : UInt<32>, exception : UInt<1>, PCdebug : UInt<32>, InstrDdebug : UInt<32>, PCEdebug : UInt<32>, RS1Edebug : UInt<32>, RS2Edebug : UInt<32>, BranchEdebug : UInt<1>, JumpEdebug : UInt<1>, PCSrcEdebug : UInt<1>, PCTargetEdebug : UInt<32>, RegWriteWdebug : UInt<1>, rdWdebug : UInt<5>, PCSrcE_debug : UInt<1>}

    inst core of PipelinedRV32Icore @[PipelinedRISCV32I.scala 38:20]
    core.clock <= clock
    core.reset <= reset
    io.result <= core.io.check_res @[PipelinedRISCV32I.scala 40:21]
    io.exception <= core.io.exception @[PipelinedRISCV32I.scala 41:21]
    io.PCdebug <= core.io.PCdebug @[PipelinedRISCV32I.scala 42:21]
    io.InstrDdebug <= core.io.InstrDdebug @[PipelinedRISCV32I.scala 43:21]
    io.PCEdebug <= core.io.PCEdebug @[PipelinedRISCV32I.scala 44:21]
    io.RS1Edebug <= core.io.RS1Edebug @[PipelinedRISCV32I.scala 45:21]
    io.RS2Edebug <= core.io.RS2Edebug @[PipelinedRISCV32I.scala 46:21]
    io.BranchEdebug <= core.io.BranchEdebug @[PipelinedRISCV32I.scala 47:21]
    io.JumpEdebug <= core.io.JumpEdebug @[PipelinedRISCV32I.scala 48:21]
    io.PCSrcEdebug <= core.io.PCSrcEdebug @[PipelinedRISCV32I.scala 49:21]
    io.PCTargetEdebug <= core.io.PCTargetEdebug @[PipelinedRISCV32I.scala 50:21]
    io.RegWriteWdebug <= core.io.RegWriteWdebug @[PipelinedRISCV32I.scala 51:21]
    io.rdWdebug <= core.io.rdWdebug @[PipelinedRISCV32I.scala 52:21]
    io.PCSrcE_debug <= core.io.PCSrcE_debug @[PipelinedRISCV32I.scala 53:21]

