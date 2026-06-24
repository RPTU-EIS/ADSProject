module HalfAdder(
  input   io_a,
  input   io_b,
  output  io_s,
  output  io_c_o
);
  assign io_s = io_a ^ io_b; // @[Adder.scala 18:17]
  assign io_c_o = io_a & io_b; // @[Adder.scala 19:18]
endmodule
module FullAdder(
  input   clock,
  input   reset,
  input   io_a,
  input   io_b,
  input   io_c_i,
  output  io_s,
  output  io_c_o
);
  wire  HA1_io_a; // @[Adder.scala 35:19]
  wire  HA1_io_b; // @[Adder.scala 35:19]
  wire  HA1_io_s; // @[Adder.scala 35:19]
  wire  HA1_io_c_o; // @[Adder.scala 35:19]
  wire  HA2_io_a; // @[Adder.scala 36:19]
  wire  HA2_io_b; // @[Adder.scala 36:19]
  wire  HA2_io_s; // @[Adder.scala 36:19]
  wire  HA2_io_c_o; // @[Adder.scala 36:19]
  HalfAdder HA1 ( // @[Adder.scala 35:19]
    .io_a(HA1_io_a),
    .io_b(HA1_io_b),
    .io_s(HA1_io_s),
    .io_c_o(HA1_io_c_o)
  );
  HalfAdder HA2 ( // @[Adder.scala 36:19]
    .io_a(HA2_io_a),
    .io_b(HA2_io_b),
    .io_s(HA2_io_s),
    .io_c_o(HA2_io_c_o)
  );
  assign io_s = HA2_io_s; // @[Adder.scala 44:9]
  assign io_c_o = HA1_io_c_o ^ HA2_io_c_o; // @[Adder.scala 45:24]
  assign HA1_io_a = io_a; // @[Adder.scala 38:12]
  assign HA1_io_b = io_b; // @[Adder.scala 39:12]
  assign HA2_io_a = HA1_io_s; // @[Adder.scala 41:12]
  assign HA2_io_b = io_c_i; // @[Adder.scala 42:12]
endmodule
