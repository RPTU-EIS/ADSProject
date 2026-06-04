circuit HalfAdder :
  module HalfAdder :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip a : UInt<1>, flip b : UInt<1>, s : UInt<1>, c_o : UInt<1>}

    node _io_s_T = xor(io.a, io.b) @[Adder.scala 18:17]
    io.s <= _io_s_T @[Adder.scala 18:9]
    node _io_c_o_T = and(io.a, io.b) @[Adder.scala 19:18]
    io.c_o <= _io_c_o_T @[Adder.scala 19:10]

