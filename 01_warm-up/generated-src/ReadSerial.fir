circuit ReadSerial :
  module Controller :
    input clock : Clock
    input reset : Reset
    output io : { flip rst : UInt<1>, flip rxd : UInt<1>, flip cnt_s : UInt<1>, cnt_en : UInt<1>, valid : UInt<1>, ps_t : UInt<2>}

    reg ps : UInt<2>, clock with :
      reset => (reset, UInt<2>("h0")) @[ReadSerial.scala 26:19]
    wire ns : UInt
    ns <= ps
    reg valid : UInt<1>, clock with :
      reset => (reset, UInt<1>("h0")) @[ReadSerial.scala 28:22]
    io.cnt_en <= UInt<1>("h0") @[ReadSerial.scala 30:13]
    io.valid <= valid @[ReadSerial.scala 31:12]
    node _T = eq(io.rst, UInt<1>("h1")) @[ReadSerial.scala 35:15]
    when _T : @[ReadSerial.scala 35:24]
      ns <= UInt<1>("h0") @[ReadSerial.scala 36:8]
    else :
      node _T_1 = eq(UInt<1>("h0"), ps) @[ReadSerial.scala 39:18]
      when _T_1 : @[ReadSerial.scala 39:18]
        valid <= UInt<1>("h0") @[ReadSerial.scala 42:17]
        node _T_2 = eq(io.rxd, UInt<1>("h1")) @[ReadSerial.scala 43:23]
        when _T_2 : @[ReadSerial.scala 43:32]
          ns <= UInt<1>("h0") @[ReadSerial.scala 44:16]
        else :
          node _T_3 = eq(io.rxd, UInt<1>("h0")) @[ReadSerial.scala 46:27]
          when _T_3 : @[ReadSerial.scala 46:36]
            ns <= UInt<1>("h1") @[ReadSerial.scala 47:16]
            node _io_cnt_en_T = eq(io.rxd, UInt<1>("h0")) @[ReadSerial.scala 48:26]
            io.cnt_en <= _io_cnt_en_T @[ReadSerial.scala 48:23]
      else :
        node _T_4 = eq(UInt<1>("h1"), ps) @[ReadSerial.scala 39:18]
        when _T_4 : @[ReadSerial.scala 39:18]
          node _T_5 = eq(io.cnt_s, UInt<1>("h0")) @[ReadSerial.scala 53:25]
          when _T_5 : @[ReadSerial.scala 53:34]
            io.cnt_en <= UInt<1>("h1") @[ReadSerial.scala 54:23]
            ns <= UInt<1>("h1") @[ReadSerial.scala 55:16]
          else :
            node _T_6 = eq(io.cnt_s, UInt<1>("h1")) @[ReadSerial.scala 57:29]
            when _T_6 : @[ReadSerial.scala 57:38]
              ns <= UInt<1>("h0") @[ReadSerial.scala 58:16]
              valid <= UInt<1>("h1") @[ReadSerial.scala 59:19]
    ps <= ns @[ReadSerial.scala 70:6]
    io.ps_t <= ps @[ReadSerial.scala 71:11]

  module ShiftRegister :
    input clock : Clock
    input reset : Reset
    output io : { flip rxd : UInt<1>, data : UInt<8>}

    reg reg : UInt<8>, clock with :
      reset => (reset, UInt<8>("h0")) @[ReadSerial.scala 112:20]
    node _reg_T = bits(reg, 6, 0) @[ReadSerial.scala 115:17]
    node _reg_T_1 = cat(_reg_T, io.rxd) @[Cat.scala 31:58]
    reg <= _reg_T_1 @[ReadSerial.scala 115:7]
    io.data <= reg @[ReadSerial.scala 116:11]

  module Counter :
    input clock : Clock
    input reset : Reset
    output io : { flip rst : UInt<1>, flip cnt_en : UInt<1>, cnt_s : UInt<1>, count_t : UInt<3>}

    reg count : UInt<3>, clock with :
      reset => (reset, UInt<3>("h0")) @[ReadSerial.scala 86:22]
    io.cnt_s <= UInt<1>("h0") @[ReadSerial.scala 88:12]
    node _T = eq(io.rst, UInt<1>("h1")) @[ReadSerial.scala 90:15]
    node _T_1 = eq(io.cnt_s, UInt<1>("h1")) @[ReadSerial.scala 90:35]
    node _T_2 = or(_T, _T_1) @[ReadSerial.scala 90:23]
    when _T_2 : @[ReadSerial.scala 90:44]
      count <= UInt<1>("h0") @[ReadSerial.scala 91:11]
    else :
      node _T_3 = eq(io.cnt_en, UInt<1>("h1")) @[ReadSerial.scala 92:24]
      when _T_3 : @[ReadSerial.scala 92:33]
        node _count_T = add(count, UInt<1>("h1")) @[ReadSerial.scala 93:20]
        node _count_T_1 = tail(_count_T, 1) @[ReadSerial.scala 93:20]
        count <= _count_T_1 @[ReadSerial.scala 93:11]
    node _T_4 = eq(count, UInt<3>("h7")) @[ReadSerial.scala 96:14]
    when _T_4 : @[ReadSerial.scala 96:23]
      io.cnt_s <= UInt<1>("h1") @[ReadSerial.scala 97:14]
    io.count_t <= count @[ReadSerial.scala 100:14]

  module ReadSerial :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip reset_n : UInt<1>, flip rxd : UInt<1>, valid : UInt<1>, data : UInt<8>, debug_ps : UInt<2>, debug_cnt_s : UInt<1>, debug_count : UInt<3>}

    inst CU of Controller @[ReadSerial.scala 144:18]
    CU.clock <= clock
    CU.reset <= reset
    inst SR of ShiftRegister @[ReadSerial.scala 145:18]
    SR.clock <= clock
    SR.reset <= reset
    inst CNT of Counter @[ReadSerial.scala 146:19]
    CNT.clock <= clock
    CNT.reset <= reset
    CU.io.rxd <= io.rxd @[ReadSerial.scala 149:13]
    CU.io.rst <= io.reset_n @[ReadSerial.scala 150:13]
    CU.io.cnt_s <= CNT.io.cnt_s @[ReadSerial.scala 151:15]
    CNT.io.rst <= io.reset_n @[ReadSerial.scala 153:14]
    CNT.io.cnt_en <= CU.io.cnt_en @[ReadSerial.scala 154:17]
    SR.io.rxd <= io.rxd @[ReadSerial.scala 157:13]
    io.valid <= CU.io.valid @[ReadSerial.scala 161:12]
    io.data <= SR.io.data @[ReadSerial.scala 162:11]
    io.debug_ps <= CU.io.ps_t @[ReadSerial.scala 163:15]
    io.debug_cnt_s <= CU.io.cnt_s @[ReadSerial.scala 164:18]
    io.debug_count <= CNT.io.count_t @[ReadSerial.scala 165:18]

