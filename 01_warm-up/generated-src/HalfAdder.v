module HalfAdder(
  input   clock,
  input   reset,
  input   io_a,
  input   io_b,
  output  io_s,
  output  io_c_o
);
  assign io_s = io_a ^ io_b; // @[Adder.scala 18:17]
  assign io_c_o = io_a & io_b; // @[Adder.scala 19:18]
endmodule
