circuit BasicAdder :
  module BasicAdder :
    input clock : Clock
    input reset : UInt<1>
    input io_a : UInt<8>
    input io_b : UInt<8>
    output io_c : UInt<8>

    reg Reg : UInt<8>, clock with :
      reset => (UInt<1>("h0"), Reg) @[Basic_Adder.scala 20:20]
    node _Reg_T = add(io_a, io_b) @[Basic_Adder.scala 22:15]
    node _Reg_T_1 = tail(_Reg_T, 1) @[Basic_Adder.scala 22:15]
    io_c <= Reg @[Basic_Adder.scala 24:8]
    Reg <= mux(reset, UInt<8>("h0"), _Reg_T_1) @[Basic_Adder.scala 20:{20,20} 22:7]
