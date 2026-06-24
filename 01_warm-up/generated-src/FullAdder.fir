circuit FullAdder :
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

  module FullAdder :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip a : UInt<1>, flip b : UInt<1>, flip c_i : UInt<1>, s : UInt<1>, c_o : UInt<1>}

    inst HA1 of HalfAdder @[Adder.scala 35:19]
    HA1.clock <= clock
    HA1.reset <= reset
    inst HA2 of HalfAdder_1 @[Adder.scala 36:19]
    HA2.clock <= clock
    HA2.reset <= reset
    HA1.io.a <= io.a @[Adder.scala 38:12]
    HA1.io.b <= io.b @[Adder.scala 39:12]
    HA2.io.a <= HA1.io.s @[Adder.scala 41:12]
    HA2.io.b <= io.c_i @[Adder.scala 42:12]
    io.s <= HA2.io.s @[Adder.scala 44:9]
    node _io_c_o_T = xor(HA1.io.c_o, HA2.io.c_o) @[Adder.scala 45:24]
    io.c_o <= _io_c_o_T @[Adder.scala 45:10]

