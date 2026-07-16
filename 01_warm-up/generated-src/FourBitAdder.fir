circuit FourBitAdder :
  module HalfAdder :
    input clock : Clock
    input reset : Reset
    output io : { flip a : UInt<1>, flip b : UInt<1>, s : UInt<1>, c_o : UInt<1>}

    node _io_s_T = xor(io.a, io.b) @[Adder.scala 18:17]
    io.s <= _io_s_T @[Adder.scala 18:9]
    node _io_c_o_T = and(io.a, io.b) @[Adder.scala 19:18]
    io.c_o <= _io_c_o_T @[Adder.scala 19:10]

  module HalfAdder_1 :
    input clock : Clock
    input reset : Reset
    output io : { flip a : UInt<1>, flip b : UInt<1>, s : UInt<1>, c_o : UInt<1>}

    node _io_s_T = xor(io.a, io.b) @[Adder.scala 18:17]
    io.s <= _io_s_T @[Adder.scala 18:9]
    node _io_c_o_T = and(io.a, io.b) @[Adder.scala 19:18]
    io.c_o <= _io_c_o_T @[Adder.scala 19:10]

  module HalfAdder_2 :
    input clock : Clock
    input reset : Reset
    output io : { flip a : UInt<1>, flip b : UInt<1>, s : UInt<1>, c_o : UInt<1>}

    node _io_s_T = xor(io.a, io.b) @[Adder.scala 18:17]
    io.s <= _io_s_T @[Adder.scala 18:9]
    node _io_c_o_T = and(io.a, io.b) @[Adder.scala 19:18]
    io.c_o <= _io_c_o_T @[Adder.scala 19:10]

  module FullAdder :
    input clock : Clock
    input reset : Reset
    output io : { flip a : UInt<1>, flip b : UInt<1>, flip c_i : UInt<1>, s : UInt<1>, c_o : UInt<1>}

    inst HA1 of HalfAdder_1 @[Adder.scala 35:19]
    HA1.clock <= clock
    HA1.reset <= reset
    inst HA2 of HalfAdder_2 @[Adder.scala 36:19]
    HA2.clock <= clock
    HA2.reset <= reset
    HA1.io.a <= io.a @[Adder.scala 38:12]
    HA1.io.b <= io.b @[Adder.scala 39:12]
    HA2.io.a <= HA1.io.s @[Adder.scala 41:12]
    HA2.io.b <= io.c_i @[Adder.scala 42:12]
    io.s <= HA2.io.s @[Adder.scala 44:9]
    node _io_c_o_T = xor(HA1.io.c_o, HA2.io.c_o) @[Adder.scala 45:24]
    io.c_o <= _io_c_o_T @[Adder.scala 45:10]

  module HalfAdder_3 :
    input clock : Clock
    input reset : Reset
    output io : { flip a : UInt<1>, flip b : UInt<1>, s : UInt<1>, c_o : UInt<1>}

    node _io_s_T = xor(io.a, io.b) @[Adder.scala 18:17]
    io.s <= _io_s_T @[Adder.scala 18:9]
    node _io_c_o_T = and(io.a, io.b) @[Adder.scala 19:18]
    io.c_o <= _io_c_o_T @[Adder.scala 19:10]

  module HalfAdder_4 :
    input clock : Clock
    input reset : Reset
    output io : { flip a : UInt<1>, flip b : UInt<1>, s : UInt<1>, c_o : UInt<1>}

    node _io_s_T = xor(io.a, io.b) @[Adder.scala 18:17]
    io.s <= _io_s_T @[Adder.scala 18:9]
    node _io_c_o_T = and(io.a, io.b) @[Adder.scala 19:18]
    io.c_o <= _io_c_o_T @[Adder.scala 19:10]

  module FullAdder_1 :
    input clock : Clock
    input reset : Reset
    output io : { flip a : UInt<1>, flip b : UInt<1>, flip c_i : UInt<1>, s : UInt<1>, c_o : UInt<1>}

    inst HA1 of HalfAdder_3 @[Adder.scala 35:19]
    HA1.clock <= clock
    HA1.reset <= reset
    inst HA2 of HalfAdder_4 @[Adder.scala 36:19]
    HA2.clock <= clock
    HA2.reset <= reset
    HA1.io.a <= io.a @[Adder.scala 38:12]
    HA1.io.b <= io.b @[Adder.scala 39:12]
    HA2.io.a <= HA1.io.s @[Adder.scala 41:12]
    HA2.io.b <= io.c_i @[Adder.scala 42:12]
    io.s <= HA2.io.s @[Adder.scala 44:9]
    node _io_c_o_T = xor(HA1.io.c_o, HA2.io.c_o) @[Adder.scala 45:24]
    io.c_o <= _io_c_o_T @[Adder.scala 45:10]

  module HalfAdder_5 :
    input clock : Clock
    input reset : Reset
    output io : { flip a : UInt<1>, flip b : UInt<1>, s : UInt<1>, c_o : UInt<1>}

    node _io_s_T = xor(io.a, io.b) @[Adder.scala 18:17]
    io.s <= _io_s_T @[Adder.scala 18:9]
    node _io_c_o_T = and(io.a, io.b) @[Adder.scala 19:18]
    io.c_o <= _io_c_o_T @[Adder.scala 19:10]

  module HalfAdder_6 :
    input clock : Clock
    input reset : Reset
    output io : { flip a : UInt<1>, flip b : UInt<1>, s : UInt<1>, c_o : UInt<1>}

    node _io_s_T = xor(io.a, io.b) @[Adder.scala 18:17]
    io.s <= _io_s_T @[Adder.scala 18:9]
    node _io_c_o_T = and(io.a, io.b) @[Adder.scala 19:18]
    io.c_o <= _io_c_o_T @[Adder.scala 19:10]

  module FullAdder_2 :
    input clock : Clock
    input reset : Reset
    output io : { flip a : UInt<1>, flip b : UInt<1>, flip c_i : UInt<1>, s : UInt<1>, c_o : UInt<1>}

    inst HA1 of HalfAdder_5 @[Adder.scala 35:19]
    HA1.clock <= clock
    HA1.reset <= reset
    inst HA2 of HalfAdder_6 @[Adder.scala 36:19]
    HA2.clock <= clock
    HA2.reset <= reset
    HA1.io.a <= io.a @[Adder.scala 38:12]
    HA1.io.b <= io.b @[Adder.scala 39:12]
    HA2.io.a <= HA1.io.s @[Adder.scala 41:12]
    HA2.io.b <= io.c_i @[Adder.scala 42:12]
    io.s <= HA2.io.s @[Adder.scala 44:9]
    node _io_c_o_T = xor(HA1.io.c_o, HA2.io.c_o) @[Adder.scala 45:24]
    io.c_o <= _io_c_o_T @[Adder.scala 45:10]

  module FourBitAdder :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip a : UInt<4>, flip b : UInt<4>, s : UInt<4>, c_o : UInt<1>}

    inst ha0 of HalfAdder @[Adder.scala 60:19]
    ha0.clock <= clock
    ha0.reset <= reset
    inst fa1 of FullAdder @[Adder.scala 61:19]
    fa1.clock <= clock
    fa1.reset <= reset
    inst fa2 of FullAdder_1 @[Adder.scala 62:19]
    fa2.clock <= clock
    fa2.reset <= reset
    inst fa3 of FullAdder_2 @[Adder.scala 63:19]
    fa3.clock <= clock
    fa3.reset <= reset
    node _ha0_io_a_T = bits(io.a, 0, 0) @[Adder.scala 66:19]
    ha0.io.a <= _ha0_io_a_T @[Adder.scala 66:12]
    node _ha0_io_b_T = bits(io.b, 0, 0) @[Adder.scala 67:19]
    ha0.io.b <= _ha0_io_b_T @[Adder.scala 67:12]
    node _fa1_io_a_T = bits(io.a, 1, 1) @[Adder.scala 70:19]
    fa1.io.a <= _fa1_io_a_T @[Adder.scala 70:12]
    node _fa1_io_b_T = bits(io.b, 1, 1) @[Adder.scala 71:19]
    fa1.io.b <= _fa1_io_b_T @[Adder.scala 71:12]
    fa1.io.c_i <= ha0.io.c_o @[Adder.scala 72:14]
    node _fa2_io_a_T = bits(io.a, 2, 2) @[Adder.scala 75:19]
    fa2.io.a <= _fa2_io_a_T @[Adder.scala 75:12]
    node _fa2_io_b_T = bits(io.b, 2, 2) @[Adder.scala 76:19]
    fa2.io.b <= _fa2_io_b_T @[Adder.scala 76:12]
    fa2.io.c_i <= fa1.io.c_o @[Adder.scala 77:14]
    node _fa3_io_a_T = bits(io.a, 3, 3) @[Adder.scala 80:19]
    fa3.io.a <= _fa3_io_a_T @[Adder.scala 80:12]
    node _fa3_io_b_T = bits(io.b, 3, 3) @[Adder.scala 81:19]
    fa3.io.b <= _fa3_io_b_T @[Adder.scala 81:12]
    fa3.io.c_i <= fa2.io.c_o @[Adder.scala 82:14]
    node io_s_lo = cat(fa1.io.s, ha0.io.s) @[Cat.scala 31:58]
    node io_s_hi = cat(fa3.io.s, fa2.io.s) @[Cat.scala 31:58]
    node _io_s_T = cat(io_s_hi, io_s_lo) @[Cat.scala 31:58]
    io.s <= _io_s_T @[Adder.scala 84:8]
    io.c_o <= fa3.io.c_o @[Adder.scala 85:10]

