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
module FourBitAdder(
  input        clock,
  input        reset,
  input  [3:0] io_a,
  input  [3:0] io_b,
  output [3:0] io_s,
  output       io_c_o
);
  wire  ha0_io_a; // @[Adder.scala 60:19]
  wire  ha0_io_b; // @[Adder.scala 60:19]
  wire  ha0_io_s; // @[Adder.scala 60:19]
  wire  ha0_io_c_o; // @[Adder.scala 60:19]
  wire  fa1_io_a; // @[Adder.scala 61:19]
  wire  fa1_io_b; // @[Adder.scala 61:19]
  wire  fa1_io_c_i; // @[Adder.scala 61:19]
  wire  fa1_io_s; // @[Adder.scala 61:19]
  wire  fa1_io_c_o; // @[Adder.scala 61:19]
  wire  fa2_io_a; // @[Adder.scala 62:19]
  wire  fa2_io_b; // @[Adder.scala 62:19]
  wire  fa2_io_c_i; // @[Adder.scala 62:19]
  wire  fa2_io_s; // @[Adder.scala 62:19]
  wire  fa2_io_c_o; // @[Adder.scala 62:19]
  wire  fa3_io_a; // @[Adder.scala 63:19]
  wire  fa3_io_b; // @[Adder.scala 63:19]
  wire  fa3_io_c_i; // @[Adder.scala 63:19]
  wire  fa3_io_s; // @[Adder.scala 63:19]
  wire  fa3_io_c_o; // @[Adder.scala 63:19]
  wire [1:0] io_s_lo = {fa1_io_s,ha0_io_s}; // @[Cat.scala 31:58]
  wire [1:0] io_s_hi = {fa3_io_s,fa2_io_s}; // @[Cat.scala 31:58]
  HalfAdder ha0 ( // @[Adder.scala 60:19]
    .io_a(ha0_io_a),
    .io_b(ha0_io_b),
    .io_s(ha0_io_s),
    .io_c_o(ha0_io_c_o)
  );
  FullAdder fa1 ( // @[Adder.scala 61:19]
    .io_a(fa1_io_a),
    .io_b(fa1_io_b),
    .io_c_i(fa1_io_c_i),
    .io_s(fa1_io_s),
    .io_c_o(fa1_io_c_o)
  );
  FullAdder fa2 ( // @[Adder.scala 62:19]
    .io_a(fa2_io_a),
    .io_b(fa2_io_b),
    .io_c_i(fa2_io_c_i),
    .io_s(fa2_io_s),
    .io_c_o(fa2_io_c_o)
  );
  FullAdder fa3 ( // @[Adder.scala 63:19]
    .io_a(fa3_io_a),
    .io_b(fa3_io_b),
    .io_c_i(fa3_io_c_i),
    .io_s(fa3_io_s),
    .io_c_o(fa3_io_c_o)
  );
  assign io_s = {io_s_hi,io_s_lo}; // @[Cat.scala 31:58]
  assign io_c_o = fa3_io_c_o; // @[Adder.scala 85:10]
  assign ha0_io_a = io_a[0]; // @[Adder.scala 66:19]
  assign ha0_io_b = io_b[0]; // @[Adder.scala 67:19]
  assign fa1_io_a = io_a[1]; // @[Adder.scala 70:19]
  assign fa1_io_b = io_b[1]; // @[Adder.scala 71:19]
  assign fa1_io_c_i = ha0_io_c_o; // @[Adder.scala 72:14]
  assign fa2_io_a = io_a[2]; // @[Adder.scala 75:19]
  assign fa2_io_b = io_b[2]; // @[Adder.scala 76:19]
  assign fa2_io_c_i = fa1_io_c_o; // @[Adder.scala 77:14]
  assign fa3_io_a = io_a[3]; // @[Adder.scala 80:19]
  assign fa3_io_b = io_b[3]; // @[Adder.scala 81:19]
  assign fa3_io_c_i = fa2_io_c_o; // @[Adder.scala 82:14]
endmodule
