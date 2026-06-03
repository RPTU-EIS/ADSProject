module ALU(
  input         clock,
  input         reset,
  input  [31:0] io_operandA,
  input  [31:0] io_operandB,
  input  [3:0]  io_operation,
  output [31:0] io_aluResult
);
  wire [31:0] _shift_amount_T = io_operandB & 32'h1f; // @[ALU.scala 37:35]
  wire [4:0] shift_amount = _shift_amount_T[4:0]; // @[ALU.scala 37:42]
  wire [31:0] _io_aluResult_T_1 = io_operandA + io_operandB; // @[ALU.scala 42:33]
  wire [31:0] _io_aluResult_T_3 = io_operandA - io_operandB; // @[ALU.scala 45:33]
  wire [31:0] _io_aluResult_T_4 = io_operandA & io_operandB; // @[ALU.scala 48:33]
  wire [31:0] _io_aluResult_T_5 = io_operandA | io_operandB; // @[ALU.scala 51:33]
  wire [31:0] _io_aluResult_T_6 = io_operandA ^ io_operandB; // @[ALU.scala 54:33]
  wire [62:0] _GEN_0 = {{31'd0}, io_operandA}; // @[ALU.scala 57:33]
  wire [62:0] _io_aluResult_T_7 = _GEN_0 << shift_amount; // @[ALU.scala 57:33]
  wire [31:0] _io_aluResult_T_8 = io_operandA >> shift_amount; // @[ALU.scala 60:33]
  wire [31:0] _io_aluResult_T_11 = $signed(io_operandA) >>> shift_amount; // @[ALU.scala 63:58]
  wire  _T_11 = $signed(io_operandA) > $signed(io_operandB); // @[ALU.scala 66:29]
  wire  _GEN_1 = io_operation == 4'h8 & _T_11; // @[ALU.scala 39:16 65:39]
  wire [31:0] _GEN_2 = io_operation == 4'h7 ? _io_aluResult_T_11 : {{31'd0}, _GEN_1}; // @[ALU.scala 62:39 63:18]
  wire [31:0] _GEN_3 = io_operation == 4'h6 ? _io_aluResult_T_8 : _GEN_2; // @[ALU.scala 59:39 60:18]
  wire [62:0] _GEN_4 = io_operation == 4'h5 ? _io_aluResult_T_7 : {{31'd0}, _GEN_3}; // @[ALU.scala 56:39 57:18]
  wire [62:0] _GEN_5 = io_operation == 4'h4 ? {{31'd0}, _io_aluResult_T_6} : _GEN_4; // @[ALU.scala 53:39 54:18]
  wire [62:0] _GEN_6 = io_operation == 4'h3 ? {{31'd0}, _io_aluResult_T_5} : _GEN_5; // @[ALU.scala 50:38 51:18]
  wire [62:0] _GEN_7 = io_operation == 4'h2 ? {{31'd0}, _io_aluResult_T_4} : _GEN_6; // @[ALU.scala 47:39 48:18]
  wire [62:0] _GEN_8 = io_operation == 4'h1 ? {{31'd0}, _io_aluResult_T_3} : _GEN_7; // @[ALU.scala 44:39 45:18]
  wire [62:0] _GEN_9 = io_operation == 4'h0 ? {{31'd0}, _io_aluResult_T_1} : _GEN_8; // @[ALU.scala 41:36 42:18]
  assign io_aluResult = _GEN_9[31:0];
endmodule
