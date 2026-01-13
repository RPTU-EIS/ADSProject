circuit BasicAdder :
  module BasicAdder :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip a : UInt<8>, flip b : UInt<8>, c : UInt<8>}

    reg Reg : UInt<8>, clock with :
      reset => (reset, UInt<8>("h0")) @[Basic_Adder.scala 22:20]
    node _Reg_T = add(io.a, io.b) @[Basic_Adder.scala 24:15]
    node _Reg_T_1 = tail(_Reg_T, 1) @[Basic_Adder.scala 24:15]
    Reg <= _Reg_T_1 @[Basic_Adder.scala 24:7]
    io.c <= Reg @[Basic_Adder.scala 26:8]

