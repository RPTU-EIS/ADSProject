module ALU(
  input         clock,
  input         reset,
  input  [31:0] io_operandA,
  input  [31:0] io_operandB,
  input  [3:0]  io_operation,
  output [31:0] io_aluResult
);
  wire [31:0] _shift_amount_T = io_operandB & 32'h1f; // @[ALU.scala 39:35]
  wire [4:0] shift_amount = _shift_amount_T[4:0]; // @[ALU.scala 39:42]
  wire [31:0] _io_aluResult_T_1 = io_operandA + io_operandB; // @[ALU.scala 44:33]
  wire [31:0] _io_aluResult_T_3 = io_operandA - io_operandB; // @[ALU.scala 47:35]
  wire [31:0] _io_aluResult_T_4 = io_operandA & io_operandB; // @[ALU.scala 50:35]
  wire [31:0] _io_aluResult_T_5 = io_operandA | io_operandB; // @[ALU.scala 53:35]
  wire [31:0] _io_aluResult_T_6 = io_operandA ^ io_operandB; // @[ALU.scala 56:35]
  wire [62:0] _GEN_0 = {{31'd0}, io_operandA}; // @[ALU.scala 59:35]
  wire [62:0] _io_aluResult_T_7 = _GEN_0 << shift_amount; // @[ALU.scala 59:35]
  wire [31:0] _io_aluResult_T_8 = io_operandA >> shift_amount; // @[ALU.scala 62:35]
  wire [31:0] _io_aluResult_T_11 = $signed(io_operandA) >>> shift_amount; // @[ALU.scala 65:60]
  wire  _T_11 = $signed(io_operandA) > $signed(io_operandB); // @[ALU.scala 68:31]
  wire  _T_13 = io_operandA > io_operandB; // @[ALU.scala 73:31]
  wire [31:0] _GEN_2 = io_operation == 4'ha ? io_operandB : 32'h0; // @[ALU.scala 41:16 77:43 78:20]
  wire [31:0] _GEN_3 = io_operation == 4'h9 ? {{31'd0}, _T_13} : _GEN_2; // @[ALU.scala 72:42]
  wire [31:0] _GEN_4 = io_operation == 4'h8 ? {{31'd0}, _T_11} : _GEN_3; // @[ALU.scala 67:41]
  wire [31:0] _GEN_5 = io_operation == 4'h7 ? _io_aluResult_T_11 : _GEN_4; // @[ALU.scala 64:41 65:20]
  wire [31:0] _GEN_6 = io_operation == 4'h6 ? _io_aluResult_T_8 : _GEN_5; // @[ALU.scala 61:41 62:20]
  wire [62:0] _GEN_7 = io_operation == 4'h5 ? _io_aluResult_T_7 : {{31'd0}, _GEN_6}; // @[ALU.scala 58:41 59:20]
  wire [62:0] _GEN_8 = io_operation == 4'h4 ? {{31'd0}, _io_aluResult_T_6} : _GEN_7; // @[ALU.scala 55:41 56:20]
  wire [62:0] _GEN_9 = io_operation == 4'h3 ? {{31'd0}, _io_aluResult_T_5} : _GEN_8; // @[ALU.scala 52:40 53:20]
  wire [62:0] _GEN_10 = io_operation == 4'h2 ? {{31'd0}, _io_aluResult_T_4} : _GEN_9; // @[ALU.scala 49:41 50:20]
  wire [62:0] _GEN_11 = io_operation == 4'h1 ? {{31'd0}, _io_aluResult_T_3} : _GEN_10; // @[ALU.scala 46:41 47:20]
  wire [62:0] _GEN_12 = io_operation == 4'h0 ? {{31'd0}, _io_aluResult_T_1} : _GEN_11; // @[ALU.scala 43:36 44:18]
  assign io_aluResult = _GEN_12[31:0];
endmodule
