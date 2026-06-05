module ALU(
  input         clock,
  input         reset,
  input  [31:0] io_operandA,
  input  [31:0] io_operandB,
  input  [3:0]  io_operation,
  output [31:0] io_aluResult,
  output        io_negativeNum
);
  wire [4:0] shift_amount = io_operandB[4:0]; // @[ALU.scala 42:33]
  wire [31:0] _io_aluResult_T_1 = io_operandA + io_operandB; // @[ALU.scala 53:35]
  wire [31:0] _io_aluResult_T_3 = io_operandA - io_operandB; // @[ALU.scala 60:36]
  wire  _T_6 = io_operandA < io_operandB; // @[ALU.scala 61:24]
  wire [31:0] _io_aluResult_T_4 = io_operandA & io_operandB; // @[ALU.scala 67:35]
  wire [31:0] _io_aluResult_T_5 = io_operandA | io_operandB; // @[ALU.scala 72:35]
  wire [31:0] _io_aluResult_T_6 = io_operandA ^ io_operandB; // @[ALU.scala 77:35]
  wire [62:0] _GEN_0 = {{31'd0}, io_operandA}; // @[ALU.scala 81:35]
  wire [62:0] _io_aluResult_T_7 = _GEN_0 << shift_amount; // @[ALU.scala 81:35]
  wire [31:0] _io_aluResult_T_8 = io_operandA >> shift_amount; // @[ALU.scala 84:35]
  wire [31:0] _io_aluResult_T_9 = io_operandA; // @[ALU.scala 87:36]
  wire [31:0] _io_aluResult_T_11 = $signed(io_operandA) >>> shift_amount; // @[ALU.scala 87:60]
  wire [31:0] _io_aluResult_T_13 = io_operandB; // @[ALU.scala 90:57]
  wire [31:0] _GEN_1 = 4'ha == io_operation ? io_operandB : 32'h0; // @[ALU.scala 44:16 49:24 96:20]
  wire [31:0] _GEN_2 = 4'h9 == io_operation ? {{31'd0}, _T_6} : _GEN_1; // @[ALU.scala 49:24 93:20]
  wire [31:0] _GEN_3 = 4'h8 == io_operation ? {{31'd0}, $signed(_io_aluResult_T_9) < $signed(_io_aluResult_T_13)} :
    _GEN_2; // @[ALU.scala 49:24 90:20]
  wire [31:0] _GEN_4 = 4'h7 == io_operation ? _io_aluResult_T_11 : _GEN_3; // @[ALU.scala 49:24 87:20]
  wire [31:0] _GEN_5 = 4'h6 == io_operation ? _io_aluResult_T_8 : _GEN_4; // @[ALU.scala 49:24 84:20]
  wire [62:0] _GEN_6 = 4'h5 == io_operation ? _io_aluResult_T_7 : {{31'd0}, _GEN_5}; // @[ALU.scala 49:24 81:20]
  wire [62:0] _GEN_7 = 4'h4 == io_operation ? {{31'd0}, _io_aluResult_T_6} : _GEN_6; // @[ALU.scala 49:24 77:20]
  wire [62:0] _GEN_8 = 4'h3 == io_operation ? {{31'd0}, _io_aluResult_T_5} : _GEN_7; // @[ALU.scala 49:24 72:20]
  wire [62:0] _GEN_9 = 4'h2 == io_operation ? {{31'd0}, _io_aluResult_T_4} : _GEN_8; // @[ALU.scala 49:24 67:20]
  wire [62:0] _GEN_10 = 4'h1 == io_operation ? {{31'd0}, _io_aluResult_T_3} : _GEN_9; // @[ALU.scala 49:24 60:20]
  wire [62:0] _GEN_12 = 4'h0 == io_operation ? {{31'd0}, _io_aluResult_T_1} : _GEN_10; // @[ALU.scala 49:24 53:20]
  assign io_aluResult = _GEN_12[31:0];
  assign io_negativeNum = 4'h0 == io_operation ? 1'h0 : 4'h1 == io_operation & _T_6; // @[ALU.scala 45:18 49:24]
endmodule
