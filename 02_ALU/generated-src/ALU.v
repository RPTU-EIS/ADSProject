module ALU(
  input         clock,
  input         reset,
  input  [31:0] io_operandA,
  input  [31:0] io_operandB,
  input  [3:0]  io_operation,
  output [31:0] io_aluResult
);
  wire [4:0] shift_amount = io_operandB[4:0]; // @[ALU.scala 39:33]
  wire [31:0] _io_aluResult_T_1 = io_operandA + io_operandB; // @[ALU.scala 45:35]
  wire [31:0] _io_aluResult_T_3 = io_operandA - io_operandB; // @[ALU.scala 48:35]
  wire [31:0] _io_aluResult_T_4 = io_operandA & io_operandB; // @[ALU.scala 51:35]
  wire [31:0] _io_aluResult_T_5 = io_operandA | io_operandB; // @[ALU.scala 54:35]
  wire [31:0] _io_aluResult_T_6 = io_operandA ^ io_operandB; // @[ALU.scala 57:35]
  wire [62:0] _GEN_11 = {{31'd0}, io_operandA}; // @[ALU.scala 60:35]
  wire [62:0] _io_aluResult_T_7 = _GEN_11 << shift_amount; // @[ALU.scala 60:35]
  wire [31:0] _io_aluResult_T_8 = io_operandA >> shift_amount; // @[ALU.scala 63:35]
  wire [31:0] _io_aluResult_T_9 = io_operandA; // @[ALU.scala 66:36]
  wire [31:0] _io_aluResult_T_11 = $signed(io_operandA) >>> shift_amount; // @[ALU.scala 66:60]
  wire [31:0] _io_aluResult_T_13 = io_operandB; // @[ALU.scala 69:57]
  wire [31:0] _GEN_0 = 4'ha == io_operation ? io_operandB : 32'h0; // @[ALU.scala 41:16 43:24 75:20]
  wire [31:0] _GEN_1 = 4'h9 == io_operation ? {{31'd0}, io_operandA < io_operandB} : _GEN_0; // @[ALU.scala 43:24 72:20]
  wire [31:0] _GEN_2 = 4'h8 == io_operation ? {{31'd0}, $signed(_io_aluResult_T_9) < $signed(_io_aluResult_T_13)} :
    _GEN_1; // @[ALU.scala 43:24 69:20]
  wire [31:0] _GEN_3 = 4'h7 == io_operation ? _io_aluResult_T_11 : _GEN_2; // @[ALU.scala 43:24 66:20]
  wire [31:0] _GEN_4 = 4'h6 == io_operation ? _io_aluResult_T_8 : _GEN_3; // @[ALU.scala 43:24 63:20]
  wire [62:0] _GEN_5 = 4'h5 == io_operation ? _io_aluResult_T_7 : {{31'd0}, _GEN_4}; // @[ALU.scala 43:24 60:20]
  wire [62:0] _GEN_6 = 4'h4 == io_operation ? {{31'd0}, _io_aluResult_T_6} : _GEN_5; // @[ALU.scala 43:24 57:20]
  wire [62:0] _GEN_7 = 4'h3 == io_operation ? {{31'd0}, _io_aluResult_T_5} : _GEN_6; // @[ALU.scala 43:24 54:20]
  wire [62:0] _GEN_8 = 4'h2 == io_operation ? {{31'd0}, _io_aluResult_T_4} : _GEN_7; // @[ALU.scala 43:24 51:20]
  wire [62:0] _GEN_9 = 4'h1 == io_operation ? {{31'd0}, _io_aluResult_T_3} : _GEN_8; // @[ALU.scala 43:24 48:20]
  wire [62:0] _GEN_10 = 4'h0 == io_operation ? {{31'd0}, _io_aluResult_T_1} : _GEN_9; // @[ALU.scala 43:24 45:20]
  assign io_aluResult = _GEN_10[31:0];
endmodule
