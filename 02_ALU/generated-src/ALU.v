module ALU(
  input         clock,
  input         reset,
  input  [31:0] io_operandA,
  input  [31:0] io_operandB,
  input  [3:0]  io_operation,
  output [31:0] io_aluResult
);
  wire [31:0] _io_aluResult_T_1 = io_operandA + io_operandB; // @[ALU.scala 30:49]
  wire [31:0] _io_aluResult_T_3 = io_operandA - io_operandB; // @[ALU.scala 31:49]
  wire [31:0] _io_aluResult_T_4 = io_operandA & io_operandB; // @[ALU.scala 32:49]
  wire [31:0] _io_aluResult_T_5 = io_operandA | io_operandB; // @[ALU.scala 33:48]
  wire [31:0] _io_aluResult_T_6 = io_operandA ^ io_operandB; // @[ALU.scala 34:49]
  wire [62:0] _GEN_11 = {{31'd0}, io_operandA}; // @[ALU.scala 35:49]
  wire [62:0] _io_aluResult_T_8 = _GEN_11 << io_operandB[4:0]; // @[ALU.scala 35:49]
  wire [31:0] _io_aluResult_T_10 = io_operandA >> io_operandB[4:0]; // @[ALU.scala 36:49]
  wire [31:0] _io_aluResult_T_11 = io_operandA; // @[ALU.scala 37:50]
  wire [31:0] _io_aluResult_T_14 = $signed(io_operandA) >>> io_operandB[4:0]; // @[ALU.scala 37:78]
  wire [31:0] _io_aluResult_T_16 = io_operandB; // @[ALU.scala 38:71]
  wire [31:0] _GEN_0 = 4'ha == io_operation ? io_operandB : 32'h0; // @[ALU.scala 27:16 29:24 40:36]
  wire [31:0] _GEN_1 = 4'h9 == io_operation ? {{31'd0}, io_operandA < io_operandB} : _GEN_0; // @[ALU.scala 29:24 39:35]
  wire [31:0] _GEN_2 = 4'h8 == io_operation ? {{31'd0}, $signed(_io_aluResult_T_11) < $signed(_io_aluResult_T_16)} :
    _GEN_1; // @[ALU.scala 29:24 38:34]
  wire [31:0] _GEN_3 = 4'h7 == io_operation ? _io_aluResult_T_14 : _GEN_2; // @[ALU.scala 29:24 37:34]
  wire [31:0] _GEN_4 = 4'h6 == io_operation ? _io_aluResult_T_10 : _GEN_3; // @[ALU.scala 29:24 36:34]
  wire [62:0] _GEN_5 = 4'h5 == io_operation ? _io_aluResult_T_8 : {{31'd0}, _GEN_4}; // @[ALU.scala 29:24 35:34]
  wire [62:0] _GEN_6 = 4'h4 == io_operation ? {{31'd0}, _io_aluResult_T_6} : _GEN_5; // @[ALU.scala 29:24 34:34]
  wire [62:0] _GEN_7 = 4'h3 == io_operation ? {{31'd0}, _io_aluResult_T_5} : _GEN_6; // @[ALU.scala 29:24 33:33]
  wire [62:0] _GEN_8 = 4'h2 == io_operation ? {{31'd0}, _io_aluResult_T_4} : _GEN_7; // @[ALU.scala 29:24 32:34]
  wire [62:0] _GEN_9 = 4'h1 == io_operation ? {{31'd0}, _io_aluResult_T_3} : _GEN_8; // @[ALU.scala 29:24 31:34]
  wire [62:0] _GEN_10 = 4'h0 == io_operation ? {{31'd0}, _io_aluResult_T_1} : _GEN_9; // @[ALU.scala 29:24 30:34]
  assign io_aluResult = _GEN_10[31:0];
endmodule
