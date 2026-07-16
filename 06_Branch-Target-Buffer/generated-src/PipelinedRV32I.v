module IF(
  input         clock,
  input         reset,
  input         io_PCSrcE,
  input  [31:0] io_PCTargetE,
  output [31:0] io_InstrF,
  output [31:0] io_PCF,
  output [31:0] io_PCPlus4F,
  input  [31:0] io_BTBTarget,
  input         io_BTBPredictTaken,
  output        io_PredictTakenF
);
`ifdef RANDOMIZE_MEM_INIT
  reg [31:0] _RAND_0;
`endif // RANDOMIZE_MEM_INIT
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_1;
`endif // RANDOMIZE_REG_INIT
  reg [31:0] IMem [0:4095]; // @[IFstage.scala 59:17]
  wire  IMem_io_InstrF_MPORT_en; // @[IFstage.scala 59:17]
  wire [11:0] IMem_io_InstrF_MPORT_addr; // @[IFstage.scala 59:17]
  wire [31:0] IMem_io_InstrF_MPORT_data; // @[IFstage.scala 59:17]
  reg [31:0] PC; // @[IFstage.scala 62:19]
  wire [29:0] addr = PC[31:2]; // @[IFstage.scala 63:17]
  wire [31:0] PCPlus4 = PC + 32'h4; // @[IFstage.scala 66:20]
  wire  _T_1 = ~reset; // @[IFstage.scala 80:9]
  assign IMem_io_InstrF_MPORT_en = 1'h1;
  assign IMem_io_InstrF_MPORT_addr = addr[11:0];
  assign IMem_io_InstrF_MPORT_data = IMem[IMem_io_InstrF_MPORT_addr]; // @[IFstage.scala 59:17]
  assign io_InstrF = IMem_io_InstrF_MPORT_data; // @[IFstage.scala 64:13]
  assign io_PCF = PC; // @[IFstage.scala 74:15]
  assign io_PCPlus4F = PC + 32'h4; // @[IFstage.scala 66:20]
  assign io_PredictTakenF = io_BTBPredictTaken; // @[IFstage.scala 78:20]
  always @(posedge clock) begin
    if (reset) begin // @[IFstage.scala 62:19]
      PC <= 32'h0; // @[IFstage.scala 62:19]
    end else if (io_PCSrcE) begin // @[IFstage.scala 69:19]
      PC <= io_PCTargetE;
    end else if (io_BTBPredictTaken) begin // @[IFstage.scala 70:8]
      PC <= io_BTBTarget;
    end else begin
      PC <= PCPlus4;
    end
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (~reset) begin
          $fwrite(32'h80000002," IF STAGE SIGNALS\n"); // @[IFstage.scala 80:9]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"----------------------------------------------\n"); // @[IFstage.scala 81:9]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002," PCSrcE:       %x\n",io_PCSrcE); // @[IFstage.scala 82:9]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002," PredictTaken: %x\n\n",io_PredictTakenF); // @[IFstage.scala 83:9]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_MEM_INIT
  _RAND_0 = {1{`RANDOM}};
  for (initvar = 0; initvar < 4096; initvar = initvar+1)
    IMem[initvar] = _RAND_0[31:0];
`endif // RANDOMIZE_MEM_INIT
`ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  PC = _RAND_1[31:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module IFBarrier(
  input         clock,
  input         reset,
  input  [31:0] io_InstrF,
  input  [31:0] io_PCF,
  input  [31:0] io_PCPlus4F,
  input         io_CLR,
  output [31:0] io_InstrD,
  output [31:0] io_PCD,
  output [31:0] io_PCPlus4D,
  input         io_PredictTakenF,
  output        io_PredictTakenD
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
`endif // RANDOMIZE_REG_INIT
  reg [31:0] instrReg; // @[IFbarrier.scala 48:27]
  reg [31:0] pcReg; // @[IFbarrier.scala 49:27]
  reg [31:0] pcPlus4Reg; // @[IFbarrier.scala 50:27]
  reg  PredictTakenReg; // @[IFbarrier.scala 67:32]
  assign io_InstrD = instrReg; // @[IFbarrier.scala 62:15]
  assign io_PCD = pcReg; // @[IFbarrier.scala 63:15]
  assign io_PCPlus4D = pcPlus4Reg; // @[IFbarrier.scala 64:15]
  assign io_PredictTakenD = PredictTakenReg; // @[IFbarrier.scala 69:23]
  always @(posedge clock) begin
    if (reset) begin // @[IFbarrier.scala 48:27]
      instrReg <= 32'h0; // @[IFbarrier.scala 48:27]
    end else if (io_CLR) begin // @[IFbarrier.scala 52:16]
      instrReg <= 32'h13; // @[IFbarrier.scala 53:16]
    end else begin
      instrReg <= io_InstrF; // @[IFbarrier.scala 57:16]
    end
    if (reset) begin // @[IFbarrier.scala 49:27]
      pcReg <= 32'h0; // @[IFbarrier.scala 49:27]
    end else if (io_CLR) begin // @[IFbarrier.scala 52:16]
      pcReg <= 32'h0; // @[IFbarrier.scala 54:16]
    end else begin
      pcReg <= io_PCF; // @[IFbarrier.scala 58:16]
    end
    if (reset) begin // @[IFbarrier.scala 50:27]
      pcPlus4Reg <= 32'h0; // @[IFbarrier.scala 50:27]
    end else if (io_CLR) begin // @[IFbarrier.scala 52:16]
      pcPlus4Reg <= 32'h0; // @[IFbarrier.scala 55:16]
    end else begin
      pcPlus4Reg <= io_PCPlus4F; // @[IFbarrier.scala 59:16]
    end
    if (reset) begin // @[IFbarrier.scala 67:32]
      PredictTakenReg <= 1'h0; // @[IFbarrier.scala 67:32]
    end else begin
      PredictTakenReg <= io_PredictTakenF; // @[IFbarrier.scala 68:23]
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  instrReg = _RAND_0[31:0];
  _RAND_1 = {1{`RANDOM}};
  pcReg = _RAND_1[31:0];
  _RAND_2 = {1{`RANDOM}};
  pcPlus4Reg = _RAND_2[31:0];
  _RAND_3 = {1{`RANDOM}};
  PredictTakenReg = _RAND_3[0:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module regFile(
  input         clock,
  input         reset,
  input  [4:0]  io_req_1_addr,
  input  [4:0]  io_req_2_addr,
  input  [4:0]  io_req_3_addr,
  input  [31:0] io_req_3_data,
  input         io_req_3_w_en,
  output [31:0] io_resp_1_data,
  output [31:0] io_resp_2_data
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
  reg [31:0] _RAND_4;
  reg [31:0] _RAND_5;
  reg [31:0] _RAND_6;
  reg [31:0] _RAND_7;
  reg [31:0] _RAND_8;
  reg [31:0] _RAND_9;
  reg [31:0] _RAND_10;
  reg [31:0] _RAND_11;
  reg [31:0] _RAND_12;
  reg [31:0] _RAND_13;
  reg [31:0] _RAND_14;
  reg [31:0] _RAND_15;
  reg [31:0] _RAND_16;
  reg [31:0] _RAND_17;
  reg [31:0] _RAND_18;
  reg [31:0] _RAND_19;
  reg [31:0] _RAND_20;
  reg [31:0] _RAND_21;
  reg [31:0] _RAND_22;
  reg [31:0] _RAND_23;
  reg [31:0] _RAND_24;
  reg [31:0] _RAND_25;
  reg [31:0] _RAND_26;
  reg [31:0] _RAND_27;
  reg [31:0] _RAND_28;
  reg [31:0] _RAND_29;
  reg [31:0] _RAND_30;
  reg [31:0] _RAND_31;
`endif // RANDOMIZE_REG_INIT
  reg [31:0] regFile_0; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_1; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_2; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_3; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_4; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_5; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_6; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_7; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_8; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_9; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_10; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_11; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_12; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_13; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_14; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_15; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_16; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_17; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_18; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_19; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_20; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_21; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_22; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_23; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_24; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_25; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_26; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_27; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_28; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_29; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_30; // @[RegisterFile.scala 63:26]
  reg [31:0] regFile_31; // @[RegisterFile.scala 63:26]
  wire [31:0] _GEN_1 = 5'h1 == io_req_1_addr ? regFile_1 : regFile_0; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_2 = 5'h2 == io_req_1_addr ? regFile_2 : _GEN_1; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_3 = 5'h3 == io_req_1_addr ? regFile_3 : _GEN_2; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_4 = 5'h4 == io_req_1_addr ? regFile_4 : _GEN_3; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_5 = 5'h5 == io_req_1_addr ? regFile_5 : _GEN_4; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_6 = 5'h6 == io_req_1_addr ? regFile_6 : _GEN_5; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_7 = 5'h7 == io_req_1_addr ? regFile_7 : _GEN_6; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_8 = 5'h8 == io_req_1_addr ? regFile_8 : _GEN_7; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_9 = 5'h9 == io_req_1_addr ? regFile_9 : _GEN_8; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_10 = 5'ha == io_req_1_addr ? regFile_10 : _GEN_9; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_11 = 5'hb == io_req_1_addr ? regFile_11 : _GEN_10; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_12 = 5'hc == io_req_1_addr ? regFile_12 : _GEN_11; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_13 = 5'hd == io_req_1_addr ? regFile_13 : _GEN_12; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_14 = 5'he == io_req_1_addr ? regFile_14 : _GEN_13; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_15 = 5'hf == io_req_1_addr ? regFile_15 : _GEN_14; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_16 = 5'h10 == io_req_1_addr ? regFile_16 : _GEN_15; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_17 = 5'h11 == io_req_1_addr ? regFile_17 : _GEN_16; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_18 = 5'h12 == io_req_1_addr ? regFile_18 : _GEN_17; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_19 = 5'h13 == io_req_1_addr ? regFile_19 : _GEN_18; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_20 = 5'h14 == io_req_1_addr ? regFile_20 : _GEN_19; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_21 = 5'h15 == io_req_1_addr ? regFile_21 : _GEN_20; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_22 = 5'h16 == io_req_1_addr ? regFile_22 : _GEN_21; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_23 = 5'h17 == io_req_1_addr ? regFile_23 : _GEN_22; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_24 = 5'h18 == io_req_1_addr ? regFile_24 : _GEN_23; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_25 = 5'h19 == io_req_1_addr ? regFile_25 : _GEN_24; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_26 = 5'h1a == io_req_1_addr ? regFile_26 : _GEN_25; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_27 = 5'h1b == io_req_1_addr ? regFile_27 : _GEN_26; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_28 = 5'h1c == io_req_1_addr ? regFile_28 : _GEN_27; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_29 = 5'h1d == io_req_1_addr ? regFile_29 : _GEN_28; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_30 = 5'h1e == io_req_1_addr ? regFile_30 : _GEN_29; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_31 = 5'h1f == io_req_1_addr ? regFile_31 : _GEN_30; // @[RegisterFile.scala 65:{26,26}]
  wire [31:0] _GEN_33 = 5'h1 == io_req_2_addr ? regFile_1 : regFile_0; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_34 = 5'h2 == io_req_2_addr ? regFile_2 : _GEN_33; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_35 = 5'h3 == io_req_2_addr ? regFile_3 : _GEN_34; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_36 = 5'h4 == io_req_2_addr ? regFile_4 : _GEN_35; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_37 = 5'h5 == io_req_2_addr ? regFile_5 : _GEN_36; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_38 = 5'h6 == io_req_2_addr ? regFile_6 : _GEN_37; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_39 = 5'h7 == io_req_2_addr ? regFile_7 : _GEN_38; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_40 = 5'h8 == io_req_2_addr ? regFile_8 : _GEN_39; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_41 = 5'h9 == io_req_2_addr ? regFile_9 : _GEN_40; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_42 = 5'ha == io_req_2_addr ? regFile_10 : _GEN_41; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_43 = 5'hb == io_req_2_addr ? regFile_11 : _GEN_42; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_44 = 5'hc == io_req_2_addr ? regFile_12 : _GEN_43; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_45 = 5'hd == io_req_2_addr ? regFile_13 : _GEN_44; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_46 = 5'he == io_req_2_addr ? regFile_14 : _GEN_45; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_47 = 5'hf == io_req_2_addr ? regFile_15 : _GEN_46; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_48 = 5'h10 == io_req_2_addr ? regFile_16 : _GEN_47; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_49 = 5'h11 == io_req_2_addr ? regFile_17 : _GEN_48; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_50 = 5'h12 == io_req_2_addr ? regFile_18 : _GEN_49; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_51 = 5'h13 == io_req_2_addr ? regFile_19 : _GEN_50; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_52 = 5'h14 == io_req_2_addr ? regFile_20 : _GEN_51; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_53 = 5'h15 == io_req_2_addr ? regFile_21 : _GEN_52; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_54 = 5'h16 == io_req_2_addr ? regFile_22 : _GEN_53; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_55 = 5'h17 == io_req_2_addr ? regFile_23 : _GEN_54; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_56 = 5'h18 == io_req_2_addr ? regFile_24 : _GEN_55; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_57 = 5'h19 == io_req_2_addr ? regFile_25 : _GEN_56; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_58 = 5'h1a == io_req_2_addr ? regFile_26 : _GEN_57; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_59 = 5'h1b == io_req_2_addr ? regFile_27 : _GEN_58; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_60 = 5'h1c == io_req_2_addr ? regFile_28 : _GEN_59; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_61 = 5'h1d == io_req_2_addr ? regFile_29 : _GEN_60; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_62 = 5'h1e == io_req_2_addr ? regFile_30 : _GEN_61; // @[RegisterFile.scala 66:{26,26}]
  wire [31:0] _GEN_63 = 5'h1f == io_req_2_addr ? regFile_31 : _GEN_62; // @[RegisterFile.scala 66:{26,26}]
  assign io_resp_1_data = io_req_1_addr == 5'h0 ? 32'h0 : _GEN_31; // @[RegisterFile.scala 65:26]
  assign io_resp_2_data = io_req_2_addr == 5'h0 ? 32'h0 : _GEN_63; // @[RegisterFile.scala 66:26]
  always @(posedge clock) begin
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_0 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h0 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_0 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_1 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h1 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_1 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_2 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h2 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_2 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_3 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h3 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_3 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_4 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h4 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_4 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_5 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h5 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_5 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_6 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h6 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_6 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_7 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h7 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_7 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_8 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h8 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_8 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_9 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h9 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_9 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_10 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'ha == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_10 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_11 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'hb == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_11 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_12 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'hc == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_12 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_13 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'hd == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_13 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_14 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'he == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_14 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_15 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'hf == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_15 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_16 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h10 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_16 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_17 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h11 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_17 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_18 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h12 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_18 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_19 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h13 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_19 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_20 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h14 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_20 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_21 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h15 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_21 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_22 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h16 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_22 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_23 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h17 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_23 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_24 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h18 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_24 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_25 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h19 == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_25 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_26 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h1a == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_26 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_27 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h1b == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_27 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_28 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h1c == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_28 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_29 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h1d == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_29 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_30 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h1e == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_30 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
    if (reset) begin // @[RegisterFile.scala 63:26]
      regFile_31 <= 32'h0; // @[RegisterFile.scala 63:26]
    end else if (io_req_3_w_en & io_req_3_addr != 5'h0) begin // @[RegisterFile.scala 68:50]
      if (5'h1f == io_req_3_addr) begin // @[RegisterFile.scala 69:32]
        regFile_31 <= io_req_3_data; // @[RegisterFile.scala 69:32]
      end
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  regFile_0 = _RAND_0[31:0];
  _RAND_1 = {1{`RANDOM}};
  regFile_1 = _RAND_1[31:0];
  _RAND_2 = {1{`RANDOM}};
  regFile_2 = _RAND_2[31:0];
  _RAND_3 = {1{`RANDOM}};
  regFile_3 = _RAND_3[31:0];
  _RAND_4 = {1{`RANDOM}};
  regFile_4 = _RAND_4[31:0];
  _RAND_5 = {1{`RANDOM}};
  regFile_5 = _RAND_5[31:0];
  _RAND_6 = {1{`RANDOM}};
  regFile_6 = _RAND_6[31:0];
  _RAND_7 = {1{`RANDOM}};
  regFile_7 = _RAND_7[31:0];
  _RAND_8 = {1{`RANDOM}};
  regFile_8 = _RAND_8[31:0];
  _RAND_9 = {1{`RANDOM}};
  regFile_9 = _RAND_9[31:0];
  _RAND_10 = {1{`RANDOM}};
  regFile_10 = _RAND_10[31:0];
  _RAND_11 = {1{`RANDOM}};
  regFile_11 = _RAND_11[31:0];
  _RAND_12 = {1{`RANDOM}};
  regFile_12 = _RAND_12[31:0];
  _RAND_13 = {1{`RANDOM}};
  regFile_13 = _RAND_13[31:0];
  _RAND_14 = {1{`RANDOM}};
  regFile_14 = _RAND_14[31:0];
  _RAND_15 = {1{`RANDOM}};
  regFile_15 = _RAND_15[31:0];
  _RAND_16 = {1{`RANDOM}};
  regFile_16 = _RAND_16[31:0];
  _RAND_17 = {1{`RANDOM}};
  regFile_17 = _RAND_17[31:0];
  _RAND_18 = {1{`RANDOM}};
  regFile_18 = _RAND_18[31:0];
  _RAND_19 = {1{`RANDOM}};
  regFile_19 = _RAND_19[31:0];
  _RAND_20 = {1{`RANDOM}};
  regFile_20 = _RAND_20[31:0];
  _RAND_21 = {1{`RANDOM}};
  regFile_21 = _RAND_21[31:0];
  _RAND_22 = {1{`RANDOM}};
  regFile_22 = _RAND_22[31:0];
  _RAND_23 = {1{`RANDOM}};
  regFile_23 = _RAND_23[31:0];
  _RAND_24 = {1{`RANDOM}};
  regFile_24 = _RAND_24[31:0];
  _RAND_25 = {1{`RANDOM}};
  regFile_25 = _RAND_25[31:0];
  _RAND_26 = {1{`RANDOM}};
  regFile_26 = _RAND_26[31:0];
  _RAND_27 = {1{`RANDOM}};
  regFile_27 = _RAND_27[31:0];
  _RAND_28 = {1{`RANDOM}};
  regFile_28 = _RAND_28[31:0];
  _RAND_29 = {1{`RANDOM}};
  regFile_29 = _RAND_29[31:0];
  _RAND_30 = {1{`RANDOM}};
  regFile_30 = _RAND_30[31:0];
  _RAND_31 = {1{`RANDOM}};
  regFile_31 = _RAND_31[31:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module ControlUnit(
  input  [6:0] io_opcode,
  input  [2:0] io_funct3,
  input  [6:0] io_funct7,
  output [4:0] io_uop,
  output       io_ALUSrcD,
  output [1:0] io_immSel,
  output       io_BranchD,
  output       io_JumpD,
  output       io_WriteEnableD,
  output       io_XcptInvalid
);
  wire  _T_1 = io_funct7 == 7'h0; // @[IDstage.scala 80:28]
  wire  _T_2 = 3'h0 == io_funct3; // @[IDstage.scala 81:34]
  wire  _T_3 = 3'h1 == io_funct3; // @[IDstage.scala 81:34]
  wire  _T_4 = 3'h2 == io_funct3; // @[IDstage.scala 81:34]
  wire  _T_5 = 3'h3 == io_funct3; // @[IDstage.scala 81:34]
  wire  _T_6 = 3'h4 == io_funct3; // @[IDstage.scala 81:34]
  wire  _T_7 = 3'h5 == io_funct3; // @[IDstage.scala 81:34]
  wire  _T_8 = 3'h6 == io_funct3; // @[IDstage.scala 81:34]
  wire  _T_9 = 3'h7 == io_funct3; // @[IDstage.scala 81:34]
  wire [4:0] _GEN_0 = 3'h7 == io_funct3 ? 5'h2 : 5'h1c; // @[IDstage.scala 69:20 81:34 89:42]
  wire  _GEN_1 = 3'h7 == io_funct3 ? 1'h0 : 1'h1; // @[IDstage.scala 75:20 81:34 89:71]
  wire [4:0] _GEN_2 = 3'h6 == io_funct3 ? 5'h3 : _GEN_0; // @[IDstage.scala 81:34 88:42]
  wire  _GEN_3 = 3'h6 == io_funct3 ? 1'h0 : _GEN_1; // @[IDstage.scala 81:34 88:71]
  wire [4:0] _GEN_4 = 3'h5 == io_funct3 ? 5'h6 : _GEN_2; // @[IDstage.scala 81:34 87:42]
  wire  _GEN_5 = 3'h5 == io_funct3 ? 1'h0 : _GEN_3; // @[IDstage.scala 81:34 87:71]
  wire [4:0] _GEN_6 = 3'h4 == io_funct3 ? 5'h4 : _GEN_4; // @[IDstage.scala 81:34 86:42]
  wire  _GEN_7 = 3'h4 == io_funct3 ? 1'h0 : _GEN_5; // @[IDstage.scala 81:34 86:71]
  wire [4:0] _GEN_8 = 3'h3 == io_funct3 ? 5'h9 : _GEN_6; // @[IDstage.scala 81:34 85:42]
  wire  _GEN_9 = 3'h3 == io_funct3 ? 1'h0 : _GEN_7; // @[IDstage.scala 81:34 85:71]
  wire [4:0] _GEN_10 = 3'h2 == io_funct3 ? 5'h8 : _GEN_8; // @[IDstage.scala 81:34 84:42]
  wire  _GEN_11 = 3'h2 == io_funct3 ? 1'h0 : _GEN_9; // @[IDstage.scala 81:34 84:71]
  wire [4:0] _GEN_12 = 3'h1 == io_funct3 ? 5'h5 : _GEN_10; // @[IDstage.scala 81:34 83:42]
  wire  _GEN_13 = 3'h1 == io_funct3 ? 1'h0 : _GEN_11; // @[IDstage.scala 81:34 83:71]
  wire [4:0] _GEN_14 = 3'h0 == io_funct3 ? 5'h0 : _GEN_12; // @[IDstage.scala 81:34 82:42]
  wire  _GEN_15 = 3'h0 == io_funct3 ? 1'h0 : _GEN_13; // @[IDstage.scala 81:34 82:71]
  wire  _T_10 = io_funct7 == 7'h20; // @[IDstage.scala 91:34]
  wire [4:0] _GEN_16 = _T_7 ? 5'h7 : 5'h1c; // @[IDstage.scala 69:20 92:34 94:42]
  wire  _GEN_17 = _T_7 ? 1'h0 : 1'h1; // @[IDstage.scala 75:20 92:34 94:70]
  wire [4:0] _GEN_18 = _T_2 ? 5'h1 : _GEN_16; // @[IDstage.scala 92:34 93:42]
  wire  _GEN_19 = _T_2 ? 1'h0 : _GEN_17; // @[IDstage.scala 92:34 93:70]
  wire [4:0] _GEN_20 = io_funct7 == 7'h20 ? _GEN_18 : 5'h1c; // @[IDstage.scala 69:20 91:51]
  wire  _GEN_21 = io_funct7 == 7'h20 ? _GEN_19 : 1'h1; // @[IDstage.scala 75:20 91:51]
  wire [4:0] _GEN_22 = io_funct7 == 7'h0 ? _GEN_14 : _GEN_20; // @[IDstage.scala 80:45]
  wire  _GEN_23 = io_funct7 == 7'h0 ? _GEN_15 : _GEN_21; // @[IDstage.scala 80:45]
  wire [4:0] _GEN_24 = _T_1 ? 5'hf : 5'h1c; // @[IDstage.scala 109:53 110:32 69:20]
  wire  _GEN_26 = _T_1 ? 1'h0 : 1'h1; // @[IDstage.scala 109:53 110:79 75:20]
  wire [4:0] _GEN_27 = _T_10 ? 5'h12 : 5'h1c; // @[IDstage.scala 116:59 117:32 69:20]
  wire  _GEN_29 = _T_10 ? 1'h0 : 1'h1; // @[IDstage.scala 116:59 117:79 75:20]
  wire [4:0] _GEN_30 = _T_1 ? 5'h11 : _GEN_27; // @[IDstage.scala 114:53 115:32]
  wire  _GEN_31 = _T_1 | _T_10; // @[IDstage.scala 114:53 115:56]
  wire  _GEN_32 = _T_1 ? 1'h0 : _GEN_29; // @[IDstage.scala 114:53 115:79]
  wire [4:0] _GEN_33 = _T_7 ? _GEN_30 : 5'h1c; // @[IDstage.scala 101:30 69:20]
  wire  _GEN_34 = _T_7 & _GEN_31; // @[IDstage.scala 101:30 71:20]
  wire  _GEN_35 = _T_7 ? _GEN_32 : 1'h1; // @[IDstage.scala 101:30 75:20]
  wire [4:0] _GEN_36 = _T_3 ? _GEN_24 : _GEN_33; // @[IDstage.scala 101:30]
  wire  _GEN_37 = _T_3 ? _T_1 : _GEN_34; // @[IDstage.scala 101:30]
  wire  _GEN_38 = _T_3 ? _GEN_26 : _GEN_35; // @[IDstage.scala 101:30]
  wire [4:0] _GEN_39 = _T_9 ? 5'he : _GEN_36; // @[IDstage.scala 101:30 107:38]
  wire  _GEN_40 = _T_9 ? 1'h0 : _GEN_38; // @[IDstage.scala 101:30 107:68]
  wire  _GEN_41 = _T_9 ? 1'h0 : _GEN_37; // @[IDstage.scala 101:30 71:20]
  wire [4:0] _GEN_42 = _T_8 ? 5'hd : _GEN_39; // @[IDstage.scala 101:30 106:38]
  wire  _GEN_43 = _T_8 ? 1'h0 : _GEN_40; // @[IDstage.scala 101:30 106:68]
  wire  _GEN_44 = _T_8 ? 1'h0 : _GEN_41; // @[IDstage.scala 101:30 71:20]
  wire [4:0] _GEN_45 = _T_6 ? 5'hc : _GEN_42; // @[IDstage.scala 101:30 105:38]
  wire  _GEN_46 = _T_6 ? 1'h0 : _GEN_43; // @[IDstage.scala 101:30 105:68]
  wire  _GEN_47 = _T_6 ? 1'h0 : _GEN_44; // @[IDstage.scala 101:30 71:20]
  wire [4:0] _GEN_48 = _T_5 ? 5'h10 : _GEN_45; // @[IDstage.scala 101:30 104:38]
  wire  _GEN_49 = _T_5 ? 1'h0 : _GEN_46; // @[IDstage.scala 101:30 104:68]
  wire  _GEN_50 = _T_5 ? 1'h0 : _GEN_47; // @[IDstage.scala 101:30 71:20]
  wire [4:0] _GEN_51 = _T_4 ? 5'ha : _GEN_48; // @[IDstage.scala 101:30 103:38]
  wire  _GEN_52 = _T_4 ? 1'h0 : _GEN_49; // @[IDstage.scala 101:30 103:68]
  wire  _GEN_53 = _T_4 ? 1'h0 : _GEN_50; // @[IDstage.scala 101:30 71:20]
  wire [4:0] _GEN_54 = _T_2 ? 5'hb : _GEN_51; // @[IDstage.scala 101:30 102:38]
  wire  _GEN_55 = _T_2 ? 1'h0 : _GEN_52; // @[IDstage.scala 101:30 102:68]
  wire  _GEN_56 = _T_2 ? 1'h0 : _GEN_53; // @[IDstage.scala 101:30 71:20]
  wire [4:0] _GEN_57 = _T_9 ? 5'h19 : 5'h1c; // @[IDstage.scala 126:30 132:38 69:20]
  wire [4:0] _GEN_59 = _T_8 ? 5'h18 : _GEN_57; // @[IDstage.scala 126:30 131:38]
  wire [4:0] _GEN_61 = _T_7 ? 5'h17 : _GEN_59; // @[IDstage.scala 126:30 130:38]
  wire [4:0] _GEN_63 = _T_6 ? 5'h16 : _GEN_61; // @[IDstage.scala 126:30 129:38]
  wire [4:0] _GEN_65 = _T_3 ? 5'h15 : _GEN_63; // @[IDstage.scala 126:30 128:38]
  wire  _GEN_66 = _T_3 ? 1'h0 : _GEN_7; // @[IDstage.scala 126:30 128:67]
  wire [4:0] _GEN_67 = _T_2 ? 5'h14 : _GEN_65; // @[IDstage.scala 126:30 127:38]
  wire  _GEN_68 = _T_2 ? 1'h0 : _GEN_66; // @[IDstage.scala 126:30 127:67]
  wire [4:0] _GEN_71 = 7'h67 == io_opcode ? 5'h1b : 5'h1c; // @[IDstage.scala 77:22 149:28 69:20]
  wire  _GEN_72 = 7'h67 == io_opcode ? 1'h0 : 1'h1; // @[IDstage.scala 77:22 150:28 75:20]
  wire [1:0] _GEN_73 = 7'h6f == io_opcode ? 2'h2 : 2'h0; // @[IDstage.scala 77:22 136:28]
  wire  _GEN_74 = 7'h6f == io_opcode | 7'h67 == io_opcode; // @[IDstage.scala 77:22 137:28]
  wire [4:0] _GEN_75 = 7'h6f == io_opcode ? 5'h1a : _GEN_71; // @[IDstage.scala 77:22 139:28]
  wire  _GEN_76 = 7'h6f == io_opcode ? 1'h0 : _GEN_72; // @[IDstage.scala 77:22 140:28]
  wire  _GEN_77 = 7'h6f == io_opcode ? 1'h0 : 7'h67 == io_opcode; // @[IDstage.scala 70:20 77:22]
  wire [1:0] _GEN_78 = 7'h63 == io_opcode ? 2'h3 : _GEN_73; // @[IDstage.scala 77:22 123:24]
  wire  _GEN_79 = 7'h63 == io_opcode | _GEN_77; // @[IDstage.scala 77:22 124:24]
  wire [4:0] _GEN_80 = 7'h63 == io_opcode ? _GEN_67 : _GEN_75; // @[IDstage.scala 77:22]
  wire  _GEN_81 = 7'h63 == io_opcode ? _GEN_68 : _GEN_76; // @[IDstage.scala 77:22]
  wire  _GEN_82 = 7'h63 == io_opcode ? 1'h0 : _GEN_74; // @[IDstage.scala 73:20 77:22]
  wire  _GEN_83 = 7'h63 == io_opcode ? 1'h0 : _GEN_77; // @[IDstage.scala 70:20 77:22]
  wire  _GEN_84 = 7'h13 == io_opcode | _GEN_83; // @[IDstage.scala 77:22 99:26]
  wire  _GEN_85 = 7'h13 == io_opcode | _GEN_82; // @[IDstage.scala 77:22 100:29]
  wire [4:0] _GEN_86 = 7'h13 == io_opcode ? _GEN_54 : _GEN_80; // @[IDstage.scala 77:22]
  wire  _GEN_87 = 7'h13 == io_opcode ? _GEN_55 : _GEN_81; // @[IDstage.scala 77:22]
  wire [1:0] _GEN_88 = 7'h13 == io_opcode ? {{1'd0}, _GEN_56} : _GEN_78; // @[IDstage.scala 77:22]
  wire  _GEN_89 = 7'h13 == io_opcode ? 1'h0 : _GEN_79; // @[IDstage.scala 72:20 77:22]
  wire  _GEN_90 = 7'h13 == io_opcode ? 1'h0 : _GEN_82; // @[IDstage.scala 73:20 77:22]
  assign io_uop = 7'h33 == io_opcode ? _GEN_22 : _GEN_86; // @[IDstage.scala 77:22]
  assign io_ALUSrcD = 7'h33 == io_opcode ? 1'h0 : _GEN_84; // @[IDstage.scala 70:20 77:22]
  assign io_immSel = 7'h33 == io_opcode ? 2'h0 : _GEN_88; // @[IDstage.scala 71:20 77:22]
  assign io_BranchD = 7'h33 == io_opcode ? 1'h0 : _GEN_89; // @[IDstage.scala 72:20 77:22]
  assign io_JumpD = 7'h33 == io_opcode ? 1'h0 : _GEN_90; // @[IDstage.scala 73:20 77:22]
  assign io_WriteEnableD = 7'h33 == io_opcode | _GEN_85; // @[IDstage.scala 77:22 79:29]
  assign io_XcptInvalid = 7'h33 == io_opcode ? _GEN_23 : _GEN_87; // @[IDstage.scala 77:22]
endmodule
module SignExtend(
  input  [24:0] io_imm_in,
  input  [1:0]  io_sel,
  output [31:0] io_imm_out
);
  wire [19:0] _full_T_2 = io_imm_in[24] ? 20'hfffff : 20'h0; // @[Bitwise.scala 74:12]
  wire [31:0] full = {_full_T_2,io_imm_in[24:13]}; // @[Cat.scala 31:58]
  wire [31:0] shamt = {27'h0,io_imm_in[17:13]}; // @[Cat.scala 31:58]
  wire [19:0] jump_cat = {io_imm_in[24],io_imm_in[12:5],io_imm_in[13],io_imm_in[23:14]}; // @[Cat.scala 31:58]
  wire [10:0] _jump_imm_T_2 = jump_cat[19] ? 11'h7ff : 11'h0; // @[Bitwise.scala 74:12]
  wire [31:0] jump_imm = {_jump_imm_T_2,io_imm_in[24],io_imm_in[12:5],io_imm_in[13],io_imm_in[23:14],1'h0}; // @[Cat.scala 31:58]
  wire [18:0] _branch_imm_T_2 = io_imm_in[24] ? 19'h7ffff : 19'h0; // @[Bitwise.scala 74:12]
  wire [31:0] branch_imm = {_branch_imm_T_2,io_imm_in[24],io_imm_in[0],io_imm_in[23:18],io_imm_in[4:1],1'h0}; // @[Cat.scala 31:58]
  wire [31:0] _GEN_0 = 2'h3 == io_sel ? branch_imm : full; // @[IDstage.scala 168:16 170:19 174:28]
  wire [31:0] _GEN_1 = 2'h2 == io_sel ? jump_imm : _GEN_0; // @[IDstage.scala 170:19 173:28]
  wire [31:0] _GEN_2 = 2'h1 == io_sel ? shamt : _GEN_1; // @[IDstage.scala 170:19 172:28]
  assign io_imm_out = 2'h0 == io_sel ? full : _GEN_2; // @[IDstage.scala 170:19 171:28]
endmodule
module ID(
  input         clock,
  input         reset,
  input  [31:0] io_inst,
  input  [31:0] io_pcD,
  input  [31:0] io_pcPlus4D,
  input         io_WriteEnableW,
  input  [4:0]  io_rdW,
  input  [31:0] io_ResultW,
  output [4:0]  io_uop,
  output        io_WriteEnableD,
  output        io_ALUSrcD,
  output [31:0] io_ImmExtD,
  output        io_BranchD,
  output        io_JumpD,
  output        io_XcptInvalid,
  output [4:0]  io_rdD,
  output [31:0] io_RD1D,
  output [31:0] io_RD2D,
  output [31:0] io_pcD_out,
  output [31:0] io_pcPlus4D_out,
  input         io_PredictTakenF,
  output        io_PredictTakenD
);
  wire  rf_clock; // @[IDstage.scala 213:22]
  wire  rf_reset; // @[IDstage.scala 213:22]
  wire [4:0] rf_io_req_1_addr; // @[IDstage.scala 213:22]
  wire [4:0] rf_io_req_2_addr; // @[IDstage.scala 213:22]
  wire [4:0] rf_io_req_3_addr; // @[IDstage.scala 213:22]
  wire [31:0] rf_io_req_3_data; // @[IDstage.scala 213:22]
  wire  rf_io_req_3_w_en; // @[IDstage.scala 213:22]
  wire [31:0] rf_io_resp_1_data; // @[IDstage.scala 213:22]
  wire [31:0] rf_io_resp_2_data; // @[IDstage.scala 213:22]
  wire [6:0] cu_io_opcode; // @[IDstage.scala 214:22]
  wire [2:0] cu_io_funct3; // @[IDstage.scala 214:22]
  wire [6:0] cu_io_funct7; // @[IDstage.scala 214:22]
  wire [4:0] cu_io_uop; // @[IDstage.scala 214:22]
  wire  cu_io_ALUSrcD; // @[IDstage.scala 214:22]
  wire [1:0] cu_io_immSel; // @[IDstage.scala 214:22]
  wire  cu_io_BranchD; // @[IDstage.scala 214:22]
  wire  cu_io_JumpD; // @[IDstage.scala 214:22]
  wire  cu_io_WriteEnableD; // @[IDstage.scala 214:22]
  wire  cu_io_XcptInvalid; // @[IDstage.scala 214:22]
  wire [24:0] sigex_io_imm_in; // @[IDstage.scala 215:23]
  wire [1:0] sigex_io_sel; // @[IDstage.scala 215:23]
  wire [31:0] sigex_io_imm_out; // @[IDstage.scala 215:23]
  regFile rf ( // @[IDstage.scala 213:22]
    .clock(rf_clock),
    .reset(rf_reset),
    .io_req_1_addr(rf_io_req_1_addr),
    .io_req_2_addr(rf_io_req_2_addr),
    .io_req_3_addr(rf_io_req_3_addr),
    .io_req_3_data(rf_io_req_3_data),
    .io_req_3_w_en(rf_io_req_3_w_en),
    .io_resp_1_data(rf_io_resp_1_data),
    .io_resp_2_data(rf_io_resp_2_data)
  );
  ControlUnit cu ( // @[IDstage.scala 214:22]
    .io_opcode(cu_io_opcode),
    .io_funct3(cu_io_funct3),
    .io_funct7(cu_io_funct7),
    .io_uop(cu_io_uop),
    .io_ALUSrcD(cu_io_ALUSrcD),
    .io_immSel(cu_io_immSel),
    .io_BranchD(cu_io_BranchD),
    .io_JumpD(cu_io_JumpD),
    .io_WriteEnableD(cu_io_WriteEnableD),
    .io_XcptInvalid(cu_io_XcptInvalid)
  );
  SignExtend sigex ( // @[IDstage.scala 215:23]
    .io_imm_in(sigex_io_imm_in),
    .io_sel(sigex_io_sel),
    .io_imm_out(sigex_io_imm_out)
  );
  assign io_uop = cu_io_uop; // @[IDstage.scala 233:20]
  assign io_WriteEnableD = cu_io_WriteEnableD; // @[IDstage.scala 236:23]
  assign io_ALUSrcD = cu_io_ALUSrcD; // @[IDstage.scala 237:20]
  assign io_ImmExtD = sigex_io_imm_out; // @[IDstage.scala 235:20]
  assign io_BranchD = cu_io_BranchD; // @[IDstage.scala 238:20]
  assign io_JumpD = cu_io_JumpD; // @[IDstage.scala 239:20]
  assign io_XcptInvalid = cu_io_XcptInvalid; // @[IDstage.scala 234:20]
  assign io_rdD = io_inst[11:7]; // @[IDstage.scala 211:25]
  assign io_RD1D = rf_io_resp_1_data; // @[IDstage.scala 230:20]
  assign io_RD2D = rf_io_resp_2_data; // @[IDstage.scala 231:20]
  assign io_pcD_out = io_pcD; // @[IDstage.scala 240:21]
  assign io_pcPlus4D_out = io_pcPlus4D; // @[IDstage.scala 241:21]
  assign io_PredictTakenD = io_PredictTakenF; // @[IDstage.scala 244:22]
  assign rf_clock = clock;
  assign rf_reset = reset;
  assign rf_io_req_1_addr = io_inst[19:15]; // @[IDstage.scala 209:25]
  assign rf_io_req_2_addr = io_inst[24:20]; // @[IDstage.scala 210:25]
  assign rf_io_req_3_addr = io_rdW; // @[IDstage.scala 226:22]
  assign rf_io_req_3_data = io_ResultW; // @[IDstage.scala 228:22]
  assign rf_io_req_3_w_en = io_WriteEnableW; // @[IDstage.scala 227:22]
  assign cu_io_opcode = io_inst[6:0]; // @[IDstage.scala 206:25]
  assign cu_io_funct3 = io_inst[14:12]; // @[IDstage.scala 207:25]
  assign cu_io_funct7 = io_inst[31:25]; // @[IDstage.scala 208:25]
  assign sigex_io_imm_in = io_inst[31:7]; // @[IDstage.scala 221:31]
  assign sigex_io_sel = cu_io_immSel; // @[IDstage.scala 222:21]
endmodule
module IDBarrier(
  input         clock,
  input         reset,
  input  [4:0]  io_uopD,
  input  [4:0]  io_rdD,
  input  [31:0] io_RD1D,
  input  [31:0] io_RD2D,
  input         io_XcptInvalidD,
  input         io_WriteEnableD,
  input         io_ALUSrcD,
  input  [31:0] io_ImmExtD,
  input         io_BranchD,
  input         io_JumpD,
  input  [31:0] io_PCD,
  input  [31:0] io_PCPlus4D,
  input  [4:0]  io_rs1D,
  input  [4:0]  io_rs2D,
  input         io_CLR,
  output [4:0]  io_Rs1E,
  output [4:0]  io_Rs2E,
  output [4:0]  io_uopE,
  output [4:0]  io_rdE,
  output [31:0] io_RD1E,
  output [31:0] io_RD2E,
  output        io_XcptInvalidE,
  output        io_WriteEnableE,
  output        io_ALUSrcE,
  output [31:0] io_ImmExtE,
  output        io_BranchE,
  output        io_JumpE,
  output [31:0] io_PCE,
  output [31:0] io_PCPlus4E,
  input         io_PredictTakenD,
  output        io_PredictTakenE
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
  reg [31:0] _RAND_4;
  reg [31:0] _RAND_5;
  reg [31:0] _RAND_6;
  reg [31:0] _RAND_7;
  reg [31:0] _RAND_8;
  reg [31:0] _RAND_9;
  reg [31:0] _RAND_10;
  reg [31:0] _RAND_11;
  reg [31:0] _RAND_12;
  reg [31:0] _RAND_13;
  reg [31:0] _RAND_14;
`endif // RANDOMIZE_REG_INIT
  reg [4:0] uop; // @[IDbarrier.scala 82:31]
  reg  XcptInvalid; // @[IDbarrier.scala 83:31]
  reg [4:0] rd; // @[IDbarrier.scala 84:31]
  reg [31:0] RD1; // @[IDbarrier.scala 85:31]
  reg [31:0] RD2; // @[IDbarrier.scala 86:31]
  reg  WriteEnable; // @[IDbarrier.scala 87:34]
  reg  ALUSrc; // @[IDbarrier.scala 88:31]
  reg [31:0] ImmExt; // @[IDbarrier.scala 89:31]
  reg  Branch; // @[IDbarrier.scala 90:31]
  reg  Jump; // @[IDbarrier.scala 91:31]
  reg [31:0] PC; // @[IDbarrier.scala 92:31]
  reg [31:0] PCPlus4; // @[IDbarrier.scala 93:31]
  reg [4:0] rs1Addr; // @[IDbarrier.scala 94:31]
  reg [4:0] rs2Addr; // @[IDbarrier.scala 95:31]
  reg  PredictTakenD; // @[IDbarrier.scala 145:32]
  assign io_Rs1E = rs1Addr; // @[IDbarrier.scala 141:21]
  assign io_Rs2E = rs2Addr; // @[IDbarrier.scala 142:21]
  assign io_uopE = uop; // @[IDbarrier.scala 129:21]
  assign io_rdE = rd; // @[IDbarrier.scala 131:21]
  assign io_RD1E = RD1; // @[IDbarrier.scala 132:21]
  assign io_RD2E = RD2; // @[IDbarrier.scala 133:21]
  assign io_XcptInvalidE = XcptInvalid; // @[IDbarrier.scala 130:21]
  assign io_WriteEnableE = WriteEnable; // @[IDbarrier.scala 134:24]
  assign io_ALUSrcE = ALUSrc; // @[IDbarrier.scala 135:21]
  assign io_ImmExtE = ImmExt; // @[IDbarrier.scala 136:21]
  assign io_BranchE = Branch; // @[IDbarrier.scala 137:21]
  assign io_JumpE = Jump; // @[IDbarrier.scala 138:21]
  assign io_PCE = PC; // @[IDbarrier.scala 139:21]
  assign io_PCPlus4E = PCPlus4; // @[IDbarrier.scala 140:21]
  assign io_PredictTakenE = PredictTakenD; // @[IDbarrier.scala 147:22]
  always @(posedge clock) begin
    if (reset) begin // @[IDbarrier.scala 82:31]
      uop <= 5'h1c; // @[IDbarrier.scala 82:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 97:18]
      uop <= 5'h13; // @[IDbarrier.scala 98:21]
    end else begin
      uop <= io_uopD; // @[IDbarrier.scala 113:21]
    end
    if (reset) begin // @[IDbarrier.scala 83:31]
      XcptInvalid <= 1'h0; // @[IDbarrier.scala 83:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 97:18]
      XcptInvalid <= 1'h0; // @[IDbarrier.scala 99:21]
    end else begin
      XcptInvalid <= io_XcptInvalidD; // @[IDbarrier.scala 117:21]
    end
    if (reset) begin // @[IDbarrier.scala 84:31]
      rd <= 5'h0; // @[IDbarrier.scala 84:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 97:18]
      rd <= 5'h0; // @[IDbarrier.scala 100:21]
    end else begin
      rd <= io_rdD; // @[IDbarrier.scala 114:21]
    end
    if (reset) begin // @[IDbarrier.scala 85:31]
      RD1 <= 32'h0; // @[IDbarrier.scala 85:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 97:18]
      RD1 <= 32'h0; // @[IDbarrier.scala 101:21]
    end else begin
      RD1 <= io_RD1D; // @[IDbarrier.scala 115:21]
    end
    if (reset) begin // @[IDbarrier.scala 86:31]
      RD2 <= 32'h0; // @[IDbarrier.scala 86:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 97:18]
      RD2 <= 32'h0; // @[IDbarrier.scala 102:21]
    end else begin
      RD2 <= io_RD2D; // @[IDbarrier.scala 116:21]
    end
    if (reset) begin // @[IDbarrier.scala 87:34]
      WriteEnable <= 1'h0; // @[IDbarrier.scala 87:34]
    end else if (io_CLR) begin // @[IDbarrier.scala 97:18]
      WriteEnable <= 1'h0; // @[IDbarrier.scala 103:24]
    end else begin
      WriteEnable <= io_WriteEnableD; // @[IDbarrier.scala 118:24]
    end
    if (reset) begin // @[IDbarrier.scala 88:31]
      ALUSrc <= 1'h0; // @[IDbarrier.scala 88:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 97:18]
      ALUSrc <= 1'h0; // @[IDbarrier.scala 104:21]
    end else begin
      ALUSrc <= io_ALUSrcD; // @[IDbarrier.scala 119:21]
    end
    if (reset) begin // @[IDbarrier.scala 89:31]
      ImmExt <= 32'h0; // @[IDbarrier.scala 89:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 97:18]
      ImmExt <= 32'h0; // @[IDbarrier.scala 105:21]
    end else begin
      ImmExt <= io_ImmExtD; // @[IDbarrier.scala 120:21]
    end
    if (reset) begin // @[IDbarrier.scala 90:31]
      Branch <= 1'h0; // @[IDbarrier.scala 90:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 97:18]
      Branch <= 1'h0; // @[IDbarrier.scala 106:21]
    end else begin
      Branch <= io_BranchD; // @[IDbarrier.scala 121:21]
    end
    if (reset) begin // @[IDbarrier.scala 91:31]
      Jump <= 1'h0; // @[IDbarrier.scala 91:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 97:18]
      Jump <= 1'h0; // @[IDbarrier.scala 107:21]
    end else begin
      Jump <= io_JumpD; // @[IDbarrier.scala 122:21]
    end
    if (reset) begin // @[IDbarrier.scala 92:31]
      PC <= 32'h0; // @[IDbarrier.scala 92:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 97:18]
      PC <= 32'h0; // @[IDbarrier.scala 108:21]
    end else begin
      PC <= io_PCD; // @[IDbarrier.scala 123:21]
    end
    if (reset) begin // @[IDbarrier.scala 93:31]
      PCPlus4 <= 32'h0; // @[IDbarrier.scala 93:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 97:18]
      PCPlus4 <= 32'h0; // @[IDbarrier.scala 109:21]
    end else begin
      PCPlus4 <= io_PCPlus4D; // @[IDbarrier.scala 124:21]
    end
    if (reset) begin // @[IDbarrier.scala 94:31]
      rs1Addr <= 5'h0; // @[IDbarrier.scala 94:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 97:18]
      rs1Addr <= 5'h0; // @[IDbarrier.scala 110:21]
    end else begin
      rs1Addr <= io_rs1D; // @[IDbarrier.scala 125:21]
    end
    if (reset) begin // @[IDbarrier.scala 95:31]
      rs2Addr <= 5'h0; // @[IDbarrier.scala 95:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 97:18]
      rs2Addr <= 5'h0; // @[IDbarrier.scala 111:21]
    end else begin
      rs2Addr <= io_rs2D; // @[IDbarrier.scala 126:21]
    end
    if (reset) begin // @[IDbarrier.scala 145:32]
      PredictTakenD <= 1'h0; // @[IDbarrier.scala 145:32]
    end else begin
      PredictTakenD <= io_PredictTakenD; // @[IDbarrier.scala 146:19]
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  uop = _RAND_0[4:0];
  _RAND_1 = {1{`RANDOM}};
  XcptInvalid = _RAND_1[0:0];
  _RAND_2 = {1{`RANDOM}};
  rd = _RAND_2[4:0];
  _RAND_3 = {1{`RANDOM}};
  RD1 = _RAND_3[31:0];
  _RAND_4 = {1{`RANDOM}};
  RD2 = _RAND_4[31:0];
  _RAND_5 = {1{`RANDOM}};
  WriteEnable = _RAND_5[0:0];
  _RAND_6 = {1{`RANDOM}};
  ALUSrc = _RAND_6[0:0];
  _RAND_7 = {1{`RANDOM}};
  ImmExt = _RAND_7[31:0];
  _RAND_8 = {1{`RANDOM}};
  Branch = _RAND_8[0:0];
  _RAND_9 = {1{`RANDOM}};
  Jump = _RAND_9[0:0];
  _RAND_10 = {1{`RANDOM}};
  PC = _RAND_10[31:0];
  _RAND_11 = {1{`RANDOM}};
  PCPlus4 = _RAND_11[31:0];
  _RAND_12 = {1{`RANDOM}};
  rs1Addr = _RAND_12[4:0];
  _RAND_13 = {1{`RANDOM}};
  rs2Addr = _RAND_13[4:0];
  _RAND_14 = {1{`RANDOM}};
  PredictTakenD = _RAND_14[0:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module ALU(
  input  [31:0] io_operandA,
  input  [31:0] io_operandB,
  input  [3:0]  io_operation,
  output [31:0] io_aluResult,
  output        io_zero
);
  wire [4:0] shift_amount = io_operandB[4:0]; // @[ALU.scala 47:33]
  wire [31:0] _io_aluResult_T_1 = io_operandA + io_operandB; // @[ALU.scala 54:49]
  wire [31:0] _io_aluResult_T_3 = io_operandA - io_operandB; // @[ALU.scala 55:50]
  wire [31:0] _io_aluResult_T_4 = io_operandA & io_operandB; // @[ALU.scala 56:49]
  wire [31:0] _io_aluResult_T_5 = io_operandA | io_operandB; // @[ALU.scala 57:49]
  wire [31:0] _io_aluResult_T_6 = io_operandA ^ io_operandB; // @[ALU.scala 58:49]
  wire [62:0] _GEN_0 = {{31'd0}, io_operandA}; // @[ALU.scala 59:50]
  wire [62:0] _io_aluResult_T_7 = _GEN_0 << shift_amount; // @[ALU.scala 59:50]
  wire [31:0] _io_aluResult_T_8 = io_operandA >> shift_amount; // @[ALU.scala 60:49]
  wire [31:0] _io_aluResult_T_11 = $signed(io_operandA) >>> shift_amount; // @[ALU.scala 61:74]
  wire  _io_aluResult_T_14 = $signed(io_operandA) < $signed(io_operandB); // @[ALU.scala 62:57]
  wire  _io_aluResult_T_15 = io_operandA < io_operandB; // @[ALU.scala 63:50]
  wire  _GEN_1 = 4'hf == io_operation ? io_operandA >= io_operandB : io_aluResult == 32'h0; // @[ALU.scala 50:11 52:24 69:51]
  wire  _GEN_3 = 4'he == io_operation ? _io_aluResult_T_15 : _GEN_1; // @[ALU.scala 52:24 68:51]
  wire  _GEN_5 = 4'hd == io_operation ? $signed(io_operandA) >= $signed(io_operandB) : _GEN_3; // @[ALU.scala 52:24 67:51]
  wire  _GEN_7 = 4'hc == io_operation ? _io_aluResult_T_14 : _GEN_5; // @[ALU.scala 52:24 66:51]
  wire  _GEN_9 = 4'hb == io_operation ? io_operandA != io_operandB : _GEN_7; // @[ALU.scala 52:24 65:51]
  wire  _GEN_11 = 4'ha == io_operation ? io_operandA == io_operandB : _GEN_9; // @[ALU.scala 52:24 64:51]
  wire  _GEN_12 = 4'h9 == io_operation & io_operandA < io_operandB; // @[ALU.scala 52:24 63:34]
  wire  _GEN_13 = 4'h9 == io_operation ? io_aluResult == 32'h0 : _GEN_11; // @[ALU.scala 50:11 52:24]
  wire  _GEN_14 = 4'h8 == io_operation ? $signed(io_operandA) < $signed(io_operandB) : _GEN_12; // @[ALU.scala 52:24 62:34]
  wire  _GEN_15 = 4'h8 == io_operation ? io_aluResult == 32'h0 : _GEN_13; // @[ALU.scala 50:11 52:24]
  wire [31:0] _GEN_16 = 4'h7 == io_operation ? _io_aluResult_T_11 : {{31'd0}, _GEN_14}; // @[ALU.scala 52:24 61:34]
  wire  _GEN_17 = 4'h7 == io_operation ? io_aluResult == 32'h0 : _GEN_15; // @[ALU.scala 50:11 52:24]
  wire [31:0] _GEN_18 = 4'h6 == io_operation ? _io_aluResult_T_8 : _GEN_16; // @[ALU.scala 52:24 60:34]
  wire  _GEN_19 = 4'h6 == io_operation ? io_aluResult == 32'h0 : _GEN_17; // @[ALU.scala 50:11 52:24]
  wire [62:0] _GEN_20 = 4'h5 == io_operation ? _io_aluResult_T_7 : {{31'd0}, _GEN_18}; // @[ALU.scala 52:24 59:34]
  wire  _GEN_21 = 4'h5 == io_operation ? io_aluResult == 32'h0 : _GEN_19; // @[ALU.scala 50:11 52:24]
  wire [62:0] _GEN_22 = 4'h4 == io_operation ? {{31'd0}, _io_aluResult_T_6} : _GEN_20; // @[ALU.scala 52:24 58:34]
  wire  _GEN_23 = 4'h4 == io_operation ? io_aluResult == 32'h0 : _GEN_21; // @[ALU.scala 50:11 52:24]
  wire [62:0] _GEN_24 = 4'h3 == io_operation ? {{31'd0}, _io_aluResult_T_5} : _GEN_22; // @[ALU.scala 52:24 57:34]
  wire  _GEN_25 = 4'h3 == io_operation ? io_aluResult == 32'h0 : _GEN_23; // @[ALU.scala 50:11 52:24]
  wire [62:0] _GEN_26 = 4'h2 == io_operation ? {{31'd0}, _io_aluResult_T_4} : _GEN_24; // @[ALU.scala 52:24 56:34]
  wire  _GEN_27 = 4'h2 == io_operation ? io_aluResult == 32'h0 : _GEN_25; // @[ALU.scala 50:11 52:24]
  wire [62:0] _GEN_28 = 4'h1 == io_operation ? {{31'd0}, _io_aluResult_T_3} : _GEN_26; // @[ALU.scala 52:24 55:34]
  wire  _GEN_29 = 4'h1 == io_operation ? io_aluResult == 32'h0 : _GEN_27; // @[ALU.scala 50:11 52:24]
  wire [62:0] _GEN_30 = 4'h0 == io_operation ? {{31'd0}, _io_aluResult_T_1} : _GEN_28; // @[ALU.scala 52:24 54:34]
  assign io_aluResult = _GEN_30[31:0];
  assign io_zero = 4'h0 == io_operation ? io_aluResult == 32'h0 : _GEN_29; // @[ALU.scala 50:11 52:24]
endmodule
module ALUcontrol(
  input  [4:0] io_uop,
  output [3:0] io_mapped
);
  wire [3:0] _GEN_1 = 5'h19 == io_uop ? 4'hf : 4'h0; // @[EXstage.scala 50:18 66:31]
  wire [3:0] _GEN_2 = 5'h18 == io_uop ? 4'he : _GEN_1; // @[EXstage.scala 50:18 65:31]
  wire [3:0] _GEN_3 = 5'h17 == io_uop ? 4'hd : _GEN_2; // @[EXstage.scala 50:18 64:31]
  wire [3:0] _GEN_4 = 5'h16 == io_uop ? 4'hc : _GEN_3; // @[EXstage.scala 50:18 63:31]
  wire [3:0] _GEN_5 = 5'h15 == io_uop ? 4'hb : _GEN_4; // @[EXstage.scala 50:18 62:31]
  wire [3:0] _GEN_6 = 5'h14 == io_uop ? 4'ha : _GEN_5; // @[EXstage.scala 50:18 61:31]
  wire [3:0] _GEN_7 = 5'h9 == io_uop | 5'h10 == io_uop ? 4'h9 : _GEN_6; // @[EXstage.scala 50:18 60:43]
  wire [3:0] _GEN_8 = 5'h8 == io_uop | 5'ha == io_uop ? 4'h8 : _GEN_7; // @[EXstage.scala 50:18 59:43]
  wire [3:0] _GEN_9 = 5'h7 == io_uop | 5'h12 == io_uop ? 4'h7 : _GEN_8; // @[EXstage.scala 50:18 58:43]
  wire [3:0] _GEN_10 = 5'h6 == io_uop | 5'h11 == io_uop ? 4'h6 : _GEN_9; // @[EXstage.scala 50:18 57:43]
  wire [3:0] _GEN_11 = 5'h5 == io_uop | 5'hf == io_uop ? 4'h5 : _GEN_10; // @[EXstage.scala 50:18 56:43]
  wire [3:0] _GEN_12 = 5'h4 == io_uop | 5'hc == io_uop ? 4'h4 : _GEN_11; // @[EXstage.scala 50:18 55:43]
  wire [3:0] _GEN_13 = 5'h3 == io_uop | 5'hd == io_uop ? 4'h3 : _GEN_12; // @[EXstage.scala 50:18 54:43]
  wire [3:0] _GEN_14 = 5'h2 == io_uop | 5'he == io_uop ? 4'h2 : _GEN_13; // @[EXstage.scala 50:18 53:43]
  wire [3:0] _GEN_15 = 5'h1 == io_uop ? 4'h1 : _GEN_14; // @[EXstage.scala 50:18 52:43]
  assign io_mapped = 5'h0 == io_uop | 5'hb == io_uop ? 4'h0 : _GEN_15; // @[EXstage.scala 50:18 51:43]
endmodule
module EXstage(
  input  [31:0] io_RD1E,
  input  [31:0] io_RD2E,
  input  [31:0] io_ImmExtE,
  input         io_ALUSrcE,
  input  [4:0]  io_rdE,
  input  [4:0]  io_uopE,
  input         io_WriteEnableE,
  input         io_XcptInvalidE,
  input  [1:0]  io_ForwardAE,
  input  [1:0]  io_ForwardBE,
  input  [31:0] io_ResultW,
  input  [31:0] io_ALUResultM,
  input  [31:0] io_PCE,
  input  [31:0] io_PCPlus4E,
  input         io_BranchE,
  input         io_JumpE,
  output [31:0] io_ALUResultE,
  output        io_exceptionE,
  output [4:0]  io_rdOutE,
  output        io_WriteEnableOutE,
  output        io_PCSrcE,
  output [31:0] io_PCTargetE,
  output        io_FlushE,
  input         io_PredictTakenE,
  output        io_BTBUpdate,
  output [31:0] io_BTBUpdatePC,
  output [31:0] io_BTBUpdateTarget,
  output        io_BTBMispredicted
);
  wire [31:0] ALU_io_operandA; // @[EXstage.scala 105:26]
  wire [31:0] ALU_io_operandB; // @[EXstage.scala 105:26]
  wire [3:0] ALU_io_operation; // @[EXstage.scala 105:26]
  wire [31:0] ALU_io_aluResult; // @[EXstage.scala 105:26]
  wire  ALU_io_zero; // @[EXstage.scala 105:26]
  wire [4:0] ALUcontrol_io_uop; // @[EXstage.scala 106:26]
  wire [3:0] ALUcontrol_io_mapped; // @[EXstage.scala 106:26]
  wire [31:0] _GEN_0 = 2'h1 == io_ForwardAE ? io_ResultW : io_RD1E; // @[EXstage.scala 112:24 114:24 111:8]
  wire [31:0] _GEN_2 = 2'h1 == io_ForwardBE ? io_ResultW : io_RD2E; // @[EXstage.scala 117:24 119:24 116:8]
  wire [31:0] srcB = 2'h2 == io_ForwardBE ? io_ALUResultM : _GEN_2; // @[EXstage.scala 117:24 118:24]
  wire  jalr = io_JumpE & io_BranchE; // @[EXstage.scala 126:23]
  wire [31:0] pcTargetAdder = io_PCE + io_ImmExtE; // @[EXstage.scala 127:30]
  wire [31:0] _io_PCTargetE_T_1 = ALU_io_aluResult & 32'hfffffffe; // @[EXstage.scala 130:47]
  wire  _io_BTBUpdate_T = ~io_JumpE; // @[EXstage.scala 138:40]
  ALU ALU ( // @[EXstage.scala 105:26]
    .io_operandA(ALU_io_operandA),
    .io_operandB(ALU_io_operandB),
    .io_operation(ALU_io_operation),
    .io_aluResult(ALU_io_aluResult),
    .io_zero(ALU_io_zero)
  );
  ALUcontrol ALUcontrol ( // @[EXstage.scala 106:26]
    .io_uop(ALUcontrol_io_uop),
    .io_mapped(ALUcontrol_io_mapped)
  );
  assign io_ALUResultE = io_JumpE ? io_PCPlus4E : ALU_io_aluResult; // @[EXstage.scala 132:25]
  assign io_exceptionE = io_XcptInvalidE; // @[EXstage.scala 133:19]
  assign io_rdOutE = io_rdE; // @[EXstage.scala 134:19]
  assign io_WriteEnableOutE = io_WriteEnableE; // @[EXstage.scala 135:22]
  assign io_PCSrcE = io_BranchE & ALU_io_zero | io_JumpE; // @[EXstage.scala 129:47]
  assign io_PCTargetE = jalr ? _io_PCTargetE_T_1 : pcTargetAdder; // @[EXstage.scala 130:22]
  assign io_FlushE = io_JumpE | io_BTBMispredicted; // @[EXstage.scala 143:25]
  assign io_BTBUpdate = io_BranchE & ~io_JumpE; // @[EXstage.scala 138:37]
  assign io_BTBUpdatePC = io_PCE; // @[EXstage.scala 139:23]
  assign io_BTBUpdateTarget = io_PCTargetE; // @[EXstage.scala 140:23]
  assign io_BTBMispredicted = io_PCSrcE != io_PredictTakenE & io_BranchE & _io_BTBUpdate_T; // @[EXstage.scala 141:73]
  assign ALU_io_operandA = 2'h2 == io_ForwardAE ? io_ALUResultM : _GEN_0; // @[EXstage.scala 112:24 113:24]
  assign ALU_io_operandB = io_ALUSrcE ? io_ImmExtE : srcB; // @[EXstage.scala 123:26]
  assign ALU_io_operation = ALUcontrol_io_mapped; // @[EXstage.scala 124:20]
  assign ALUcontrol_io_uop = io_uopE; // @[EXstage.scala 107:21]
endmodule
module EXBarrier(
  input         clock,
  input         reset,
  input  [31:0] io_ALUResultE,
  input  [4:0]  io_rdE,
  input         io_XcptInvalidE,
  input         io_WriteEnableE,
  output [31:0] io_ALUResultM,
  output [4:0]  io_rdM,
  output        io_XcptInvalidM,
  output        io_WriteEnableM
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
`endif // RANDOMIZE_REG_INIT
  reg [31:0] ALUResult; // @[EXbarrier.scala 50:28]
  reg [4:0] rd; // @[EXbarrier.scala 51:28]
  reg  XcptInvalid; // @[EXbarrier.scala 52:28]
  reg  WriteEnable; // @[EXbarrier.scala 53:28]
  assign io_ALUResultM = ALUResult; // @[EXbarrier.scala 60:19]
  assign io_rdM = rd; // @[EXbarrier.scala 61:19]
  assign io_XcptInvalidM = XcptInvalid; // @[EXbarrier.scala 62:19]
  assign io_WriteEnableM = WriteEnable; // @[EXbarrier.scala 63:19]
  always @(posedge clock) begin
    if (reset) begin // @[EXbarrier.scala 50:28]
      ALUResult <= 32'h0; // @[EXbarrier.scala 50:28]
    end else begin
      ALUResult <= io_ALUResultE; // @[EXbarrier.scala 55:15]
    end
    if (reset) begin // @[EXbarrier.scala 51:28]
      rd <= 5'h0; // @[EXbarrier.scala 51:28]
    end else begin
      rd <= io_rdE; // @[EXbarrier.scala 56:15]
    end
    if (reset) begin // @[EXbarrier.scala 52:28]
      XcptInvalid <= 1'h0; // @[EXbarrier.scala 52:28]
    end else begin
      XcptInvalid <= io_XcptInvalidE; // @[EXbarrier.scala 57:15]
    end
    if (reset) begin // @[EXbarrier.scala 53:28]
      WriteEnable <= 1'h0; // @[EXbarrier.scala 53:28]
    end else begin
      WriteEnable <= io_WriteEnableE; // @[EXbarrier.scala 58:15]
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  ALUResult = _RAND_0[31:0];
  _RAND_1 = {1{`RANDOM}};
  rd = _RAND_1[4:0];
  _RAND_2 = {1{`RANDOM}};
  XcptInvalid = _RAND_2[0:0];
  _RAND_3 = {1{`RANDOM}};
  WriteEnable = _RAND_3[0:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module WBstage(
  input  [31:0] io_ALUResultW,
  input  [4:0]  io_rdW,
  input         io_WriteEnableW,
  output [4:0]  io_WriteEnableReq_addr,
  output [31:0] io_WriteEnableReq_data,
  output        io_WriteEnableReq_w_en,
  output [31:0] io_ResultW
);
  assign io_WriteEnableReq_addr = io_rdW; // @[WBstage.scala 58:26]
  assign io_WriteEnableReq_data = io_ALUResultW; // @[WBstage.scala 59:26]
  assign io_WriteEnableReq_w_en = io_WriteEnableW; // @[WBstage.scala 60:26]
  assign io_ResultW = io_ALUResultW; // @[WBstage.scala 61:24]
endmodule
module WBBarrier(
  input         clock,
  input         reset,
  input  [31:0] io_ResultW,
  input         io_XcptInvalidW,
  output [31:0] io_check_res,
  output        io_exception
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
`endif // RANDOMIZE_REG_INIT
  reg [31:0] check_res; // @[WBbarrier.scala 46:26]
  reg  isInvalid; // @[WBbarrier.scala 47:26]
  assign io_check_res = check_res; // @[WBbarrier.scala 52:16]
  assign io_exception = isInvalid; // @[WBbarrier.scala 53:16]
  always @(posedge clock) begin
    if (reset) begin // @[WBbarrier.scala 46:26]
      check_res <= 32'h0; // @[WBbarrier.scala 46:26]
    end else begin
      check_res <= io_ResultW; // @[WBbarrier.scala 49:13]
    end
    if (reset) begin // @[WBbarrier.scala 47:26]
      isInvalid <= 1'h0; // @[WBbarrier.scala 47:26]
    end else begin
      isInvalid <= io_XcptInvalidW; // @[WBbarrier.scala 50:13]
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  check_res = _RAND_0[31:0];
  _RAND_1 = {1{`RANDOM}};
  isInvalid = _RAND_1[0:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module ForwardingUnit(
  input  [31:0] io_rs1_EX,
  input  [31:0] io_rs2_EX,
  input  [31:0] io_rd_MEM,
  input  [31:0] io_rd_WB,
  input         io_wrEn_MEM,
  input         io_wrEn_WB,
  output [1:0]  io_forwardA,
  output [1:0]  io_forwardB
);
  wire  _T_2 = io_rs1_EX != 32'h0; // @[ForwardingUnit.scala 54:63]
  wire  _T_7 = io_rs1_EX == io_rd_WB & io_wrEn_WB & _T_2; // @[ForwardingUnit.scala 56:53]
  wire  _T_10 = io_rs2_EX != 32'h0; // @[ForwardingUnit.scala 63:63]
  wire  _T_15 = io_rs2_EX == io_rd_WB & io_wrEn_WB & _T_10; // @[ForwardingUnit.scala 65:53]
  assign io_forwardA = io_rs1_EX == io_rd_MEM & io_wrEn_MEM & io_rs1_EX != 32'h0 ? 2'h2 : {{1'd0}, _T_7}; // @[ForwardingUnit.scala 54:73 55:17]
  assign io_forwardB = io_rs2_EX == io_rd_MEM & io_wrEn_MEM & io_rs2_EX != 32'h0 ? 2'h2 : {{1'd0}, _T_15}; // @[ForwardingUnit.scala 63:73 64:17]
endmodule
module BTB_ctrl(
  input  [1:0] io_currentState,
  input        io_taken,
  output [1:0] io_nextState
);
  wire [1:0] _GEN_1 = io_taken ? 2'h2 : 2'h0; // @[BTB.scala 45:{21,26,49}]
  wire [1:0] _GEN_2 = io_taken ? 2'h3 : 2'h1; // @[BTB.scala 48:{21,26,49}]
  wire [1:0] _GEN_3 = io_taken ? 2'h3 : 2'h2; // @[BTB.scala 51:{21,26,49}]
  wire [1:0] _GEN_4 = 2'h3 == io_currentState ? _GEN_3 : 2'h0; // @[BTB.scala 40:26]
  wire [1:0] _GEN_5 = 2'h2 == io_currentState ? _GEN_2 : _GEN_4; // @[BTB.scala 40:26]
  wire [1:0] _GEN_6 = 2'h1 == io_currentState ? _GEN_1 : _GEN_5; // @[BTB.scala 40:26]
  wire [1:0] _GEN_7 = 2'h0 == io_currentState ? {{1'd0}, io_taken} : _GEN_6; // @[BTB.scala 40:26]
  wire  ns = _GEN_7[0];
  assign io_nextState = {{1'd0}, ns}; // @[BTB.scala 55:16]
endmodule
module BTB(
  input         clock,
  input         reset,
  input  [31:0] io_PC,
  input         io_update,
  input  [31:0] io_updatePC,
  input  [31:0] io_updateTarget,
  input         io_mispredicted,
  output [31:0] io_target,
  output        io_predictTaken
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
  reg [31:0] _RAND_4;
  reg [31:0] _RAND_5;
  reg [31:0] _RAND_6;
  reg [31:0] _RAND_7;
  reg [31:0] _RAND_8;
  reg [31:0] _RAND_9;
  reg [31:0] _RAND_10;
  reg [31:0] _RAND_11;
  reg [31:0] _RAND_12;
  reg [31:0] _RAND_13;
  reg [31:0] _RAND_14;
  reg [31:0] _RAND_15;
  reg [31:0] _RAND_16;
  reg [31:0] _RAND_17;
  reg [31:0] _RAND_18;
  reg [31:0] _RAND_19;
  reg [31:0] _RAND_20;
  reg [31:0] _RAND_21;
  reg [31:0] _RAND_22;
  reg [31:0] _RAND_23;
  reg [31:0] _RAND_24;
  reg [31:0] _RAND_25;
  reg [31:0] _RAND_26;
  reg [31:0] _RAND_27;
  reg [31:0] _RAND_28;
  reg [31:0] _RAND_29;
  reg [31:0] _RAND_30;
  reg [31:0] _RAND_31;
  reg [31:0] _RAND_32;
  reg [31:0] _RAND_33;
  reg [31:0] _RAND_34;
  reg [31:0] _RAND_35;
  reg [31:0] _RAND_36;
  reg [31:0] _RAND_37;
  reg [31:0] _RAND_38;
  reg [31:0] _RAND_39;
  reg [31:0] _RAND_40;
  reg [31:0] _RAND_41;
  reg [31:0] _RAND_42;
  reg [31:0] _RAND_43;
  reg [31:0] _RAND_44;
  reg [31:0] _RAND_45;
  reg [31:0] _RAND_46;
  reg [31:0] _RAND_47;
  reg [31:0] _RAND_48;
  reg [31:0] _RAND_49;
  reg [31:0] _RAND_50;
  reg [31:0] _RAND_51;
  reg [31:0] _RAND_52;
  reg [31:0] _RAND_53;
  reg [31:0] _RAND_54;
  reg [31:0] _RAND_55;
  reg [31:0] _RAND_56;
  reg [31:0] _RAND_57;
  reg [31:0] _RAND_58;
  reg [31:0] _RAND_59;
  reg [31:0] _RAND_60;
  reg [31:0] _RAND_61;
  reg [31:0] _RAND_62;
  reg [31:0] _RAND_63;
  reg [31:0] _RAND_64;
  reg [31:0] _RAND_65;
  reg [31:0] _RAND_66;
  reg [31:0] _RAND_67;
  reg [31:0] _RAND_68;
  reg [31:0] _RAND_69;
  reg [31:0] _RAND_70;
  reg [31:0] _RAND_71;
`endif // RANDOMIZE_REG_INIT
  wire [1:0] btbCtrl_io_currentState; // @[BTB.scala 122:23]
  wire  btbCtrl_io_taken; // @[BTB.scala 122:23]
  wire [1:0] btbCtrl_io_nextState; // @[BTB.scala 122:23]
  reg  table_0_0_valid; // @[BTB.scala 75:22]
  reg [26:0] table_0_0_tag; // @[BTB.scala 75:22]
  reg [31:0] table_0_0_target; // @[BTB.scala 75:22]
  reg [1:0] table_0_0_counter; // @[BTB.scala 75:22]
  reg  table_0_1_valid; // @[BTB.scala 75:22]
  reg [26:0] table_0_1_tag; // @[BTB.scala 75:22]
  reg [31:0] table_0_1_target; // @[BTB.scala 75:22]
  reg [1:0] table_0_1_counter; // @[BTB.scala 75:22]
  reg  table_1_0_valid; // @[BTB.scala 75:22]
  reg [26:0] table_1_0_tag; // @[BTB.scala 75:22]
  reg [31:0] table_1_0_target; // @[BTB.scala 75:22]
  reg [1:0] table_1_0_counter; // @[BTB.scala 75:22]
  reg  table_1_1_valid; // @[BTB.scala 75:22]
  reg [26:0] table_1_1_tag; // @[BTB.scala 75:22]
  reg [31:0] table_1_1_target; // @[BTB.scala 75:22]
  reg [1:0] table_1_1_counter; // @[BTB.scala 75:22]
  reg  table_2_0_valid; // @[BTB.scala 75:22]
  reg [26:0] table_2_0_tag; // @[BTB.scala 75:22]
  reg [31:0] table_2_0_target; // @[BTB.scala 75:22]
  reg [1:0] table_2_0_counter; // @[BTB.scala 75:22]
  reg  table_2_1_valid; // @[BTB.scala 75:22]
  reg [26:0] table_2_1_tag; // @[BTB.scala 75:22]
  reg [31:0] table_2_1_target; // @[BTB.scala 75:22]
  reg [1:0] table_2_1_counter; // @[BTB.scala 75:22]
  reg  table_3_0_valid; // @[BTB.scala 75:22]
  reg [26:0] table_3_0_tag; // @[BTB.scala 75:22]
  reg [31:0] table_3_0_target; // @[BTB.scala 75:22]
  reg [1:0] table_3_0_counter; // @[BTB.scala 75:22]
  reg  table_3_1_valid; // @[BTB.scala 75:22]
  reg [26:0] table_3_1_tag; // @[BTB.scala 75:22]
  reg [31:0] table_3_1_target; // @[BTB.scala 75:22]
  reg [1:0] table_3_1_counter; // @[BTB.scala 75:22]
  reg  table_4_0_valid; // @[BTB.scala 75:22]
  reg [26:0] table_4_0_tag; // @[BTB.scala 75:22]
  reg [31:0] table_4_0_target; // @[BTB.scala 75:22]
  reg [1:0] table_4_0_counter; // @[BTB.scala 75:22]
  reg  table_4_1_valid; // @[BTB.scala 75:22]
  reg [26:0] table_4_1_tag; // @[BTB.scala 75:22]
  reg [31:0] table_4_1_target; // @[BTB.scala 75:22]
  reg [1:0] table_4_1_counter; // @[BTB.scala 75:22]
  reg  table_5_0_valid; // @[BTB.scala 75:22]
  reg [26:0] table_5_0_tag; // @[BTB.scala 75:22]
  reg [31:0] table_5_0_target; // @[BTB.scala 75:22]
  reg [1:0] table_5_0_counter; // @[BTB.scala 75:22]
  reg  table_5_1_valid; // @[BTB.scala 75:22]
  reg [26:0] table_5_1_tag; // @[BTB.scala 75:22]
  reg [31:0] table_5_1_target; // @[BTB.scala 75:22]
  reg [1:0] table_5_1_counter; // @[BTB.scala 75:22]
  reg  table_6_0_valid; // @[BTB.scala 75:22]
  reg [26:0] table_6_0_tag; // @[BTB.scala 75:22]
  reg [31:0] table_6_0_target; // @[BTB.scala 75:22]
  reg [1:0] table_6_0_counter; // @[BTB.scala 75:22]
  reg  table_6_1_valid; // @[BTB.scala 75:22]
  reg [26:0] table_6_1_tag; // @[BTB.scala 75:22]
  reg [31:0] table_6_1_target; // @[BTB.scala 75:22]
  reg [1:0] table_6_1_counter; // @[BTB.scala 75:22]
  reg  table_7_0_valid; // @[BTB.scala 75:22]
  reg [26:0] table_7_0_tag; // @[BTB.scala 75:22]
  reg [31:0] table_7_0_target; // @[BTB.scala 75:22]
  reg [1:0] table_7_0_counter; // @[BTB.scala 75:22]
  reg  table_7_1_valid; // @[BTB.scala 75:22]
  reg [26:0] table_7_1_tag; // @[BTB.scala 75:22]
  reg [31:0] table_7_1_target; // @[BTB.scala 75:22]
  reg [1:0] table_7_1_counter; // @[BTB.scala 75:22]
  reg  lru_0; // @[BTB.scala 86:22]
  reg  lru_1; // @[BTB.scala 86:22]
  reg  lru_2; // @[BTB.scala 86:22]
  reg  lru_3; // @[BTB.scala 86:22]
  reg  lru_4; // @[BTB.scala 86:22]
  reg  lru_5; // @[BTB.scala 86:22]
  reg  lru_6; // @[BTB.scala 86:22]
  reg  lru_7; // @[BTB.scala 86:22]
  wire [2:0] lookupIdx = io_PC[4:2]; // @[BTB.scala 89:24]
  wire [26:0] lookupTag = io_PC[31:5]; // @[BTB.scala 90:24]
  wire [26:0] _GEN_1 = 3'h1 == lookupIdx ? table_1_0_tag : table_0_0_tag; // @[BTB.scala 94:{40,40}]
  wire [26:0] _GEN_2 = 3'h2 == lookupIdx ? table_2_0_tag : _GEN_1; // @[BTB.scala 94:{40,40}]
  wire [26:0] _GEN_3 = 3'h3 == lookupIdx ? table_3_0_tag : _GEN_2; // @[BTB.scala 94:{40,40}]
  wire [26:0] _GEN_4 = 3'h4 == lookupIdx ? table_4_0_tag : _GEN_3; // @[BTB.scala 94:{40,40}]
  wire [26:0] _GEN_5 = 3'h5 == lookupIdx ? table_5_0_tag : _GEN_4; // @[BTB.scala 94:{40,40}]
  wire [26:0] _GEN_6 = 3'h6 == lookupIdx ? table_6_0_tag : _GEN_5; // @[BTB.scala 94:{40,40}]
  wire [26:0] _GEN_7 = 3'h7 == lookupIdx ? table_7_0_tag : _GEN_6; // @[BTB.scala 94:{40,40}]
  wire  _GEN_9 = 3'h1 == lookupIdx ? table_1_0_valid : table_0_0_valid; // @[BTB.scala 94:{27,27}]
  wire  _GEN_10 = 3'h2 == lookupIdx ? table_2_0_valid : _GEN_9; // @[BTB.scala 94:{27,27}]
  wire  _GEN_11 = 3'h3 == lookupIdx ? table_3_0_valid : _GEN_10; // @[BTB.scala 94:{27,27}]
  wire  _GEN_12 = 3'h4 == lookupIdx ? table_4_0_valid : _GEN_11; // @[BTB.scala 94:{27,27}]
  wire  _GEN_13 = 3'h5 == lookupIdx ? table_5_0_valid : _GEN_12; // @[BTB.scala 94:{27,27}]
  wire  _GEN_14 = 3'h6 == lookupIdx ? table_6_0_valid : _GEN_13; // @[BTB.scala 94:{27,27}]
  wire  _GEN_15 = 3'h7 == lookupIdx ? table_7_0_valid : _GEN_14; // @[BTB.scala 94:{27,27}]
  wire  lHit0 = _GEN_15 & _GEN_7 == lookupTag; // @[BTB.scala 94:27]
  wire [26:0] _GEN_17 = 3'h1 == lookupIdx ? table_1_1_tag : table_0_1_tag; // @[BTB.scala 95:{40,40}]
  wire [26:0] _GEN_18 = 3'h2 == lookupIdx ? table_2_1_tag : _GEN_17; // @[BTB.scala 95:{40,40}]
  wire [26:0] _GEN_19 = 3'h3 == lookupIdx ? table_3_1_tag : _GEN_18; // @[BTB.scala 95:{40,40}]
  wire [26:0] _GEN_20 = 3'h4 == lookupIdx ? table_4_1_tag : _GEN_19; // @[BTB.scala 95:{40,40}]
  wire [26:0] _GEN_21 = 3'h5 == lookupIdx ? table_5_1_tag : _GEN_20; // @[BTB.scala 95:{40,40}]
  wire [26:0] _GEN_22 = 3'h6 == lookupIdx ? table_6_1_tag : _GEN_21; // @[BTB.scala 95:{40,40}]
  wire [26:0] _GEN_23 = 3'h7 == lookupIdx ? table_7_1_tag : _GEN_22; // @[BTB.scala 95:{40,40}]
  wire  _GEN_25 = 3'h1 == lookupIdx ? table_1_1_valid : table_0_1_valid; // @[BTB.scala 95:{27,27}]
  wire  _GEN_26 = 3'h2 == lookupIdx ? table_2_1_valid : _GEN_25; // @[BTB.scala 95:{27,27}]
  wire  _GEN_27 = 3'h3 == lookupIdx ? table_3_1_valid : _GEN_26; // @[BTB.scala 95:{27,27}]
  wire  _GEN_28 = 3'h4 == lookupIdx ? table_4_1_valid : _GEN_27; // @[BTB.scala 95:{27,27}]
  wire  _GEN_29 = 3'h5 == lookupIdx ? table_5_1_valid : _GEN_28; // @[BTB.scala 95:{27,27}]
  wire  _GEN_30 = 3'h6 == lookupIdx ? table_6_1_valid : _GEN_29; // @[BTB.scala 95:{27,27}]
  wire  _GEN_31 = 3'h7 == lookupIdx ? table_7_1_valid : _GEN_30; // @[BTB.scala 95:{27,27}]
  wire  lHit1 = _GEN_31 & _GEN_23 == lookupTag; // @[BTB.scala 95:27]
  wire  lHit = lHit0 | lHit1; // @[BTB.scala 96:21]
  wire [31:0] _GEN_34 = 3'h1 == lookupIdx ? table_1_0_target : table_0_0_target; // @[BTB.scala 97:{19,19}]
  wire [1:0] _GEN_35 = 3'h1 == lookupIdx ? table_1_0_counter : table_0_0_counter; // @[BTB.scala 97:{19,19}]
  wire [31:0] _GEN_36 = 3'h2 == lookupIdx ? table_2_0_target : _GEN_34; // @[BTB.scala 97:{19,19}]
  wire [1:0] _GEN_37 = 3'h2 == lookupIdx ? table_2_0_counter : _GEN_35; // @[BTB.scala 97:{19,19}]
  wire [31:0] _GEN_38 = 3'h3 == lookupIdx ? table_3_0_target : _GEN_36; // @[BTB.scala 97:{19,19}]
  wire [1:0] _GEN_39 = 3'h3 == lookupIdx ? table_3_0_counter : _GEN_37; // @[BTB.scala 97:{19,19}]
  wire [31:0] _GEN_40 = 3'h4 == lookupIdx ? table_4_0_target : _GEN_38; // @[BTB.scala 97:{19,19}]
  wire [1:0] _GEN_41 = 3'h4 == lookupIdx ? table_4_0_counter : _GEN_39; // @[BTB.scala 97:{19,19}]
  wire [31:0] _GEN_42 = 3'h5 == lookupIdx ? table_5_0_target : _GEN_40; // @[BTB.scala 97:{19,19}]
  wire [1:0] _GEN_43 = 3'h5 == lookupIdx ? table_5_0_counter : _GEN_41; // @[BTB.scala 97:{19,19}]
  wire [31:0] _GEN_44 = 3'h6 == lookupIdx ? table_6_0_target : _GEN_42; // @[BTB.scala 97:{19,19}]
  wire [1:0] _GEN_45 = 3'h6 == lookupIdx ? table_6_0_counter : _GEN_43; // @[BTB.scala 97:{19,19}]
  wire [31:0] _GEN_46 = 3'h7 == lookupIdx ? table_7_0_target : _GEN_44; // @[BTB.scala 97:{19,19}]
  wire [1:0] _GEN_47 = 3'h7 == lookupIdx ? table_7_0_counter : _GEN_45; // @[BTB.scala 97:{19,19}]
  wire [31:0] _GEN_50 = 3'h1 == lookupIdx ? table_1_1_target : table_0_1_target; // @[BTB.scala 97:{19,19}]
  wire [1:0] _GEN_51 = 3'h1 == lookupIdx ? table_1_1_counter : table_0_1_counter; // @[BTB.scala 97:{19,19}]
  wire [31:0] _GEN_52 = 3'h2 == lookupIdx ? table_2_1_target : _GEN_50; // @[BTB.scala 97:{19,19}]
  wire [1:0] _GEN_53 = 3'h2 == lookupIdx ? table_2_1_counter : _GEN_51; // @[BTB.scala 97:{19,19}]
  wire [31:0] _GEN_54 = 3'h3 == lookupIdx ? table_3_1_target : _GEN_52; // @[BTB.scala 97:{19,19}]
  wire [1:0] _GEN_55 = 3'h3 == lookupIdx ? table_3_1_counter : _GEN_53; // @[BTB.scala 97:{19,19}]
  wire [31:0] _GEN_56 = 3'h4 == lookupIdx ? table_4_1_target : _GEN_54; // @[BTB.scala 97:{19,19}]
  wire [1:0] _GEN_57 = 3'h4 == lookupIdx ? table_4_1_counter : _GEN_55; // @[BTB.scala 97:{19,19}]
  wire [31:0] _GEN_58 = 3'h5 == lookupIdx ? table_5_1_target : _GEN_56; // @[BTB.scala 97:{19,19}]
  wire [1:0] _GEN_59 = 3'h5 == lookupIdx ? table_5_1_counter : _GEN_57; // @[BTB.scala 97:{19,19}]
  wire [31:0] _GEN_60 = 3'h6 == lookupIdx ? table_6_1_target : _GEN_58; // @[BTB.scala 97:{19,19}]
  wire [1:0] _GEN_61 = 3'h6 == lookupIdx ? table_6_1_counter : _GEN_59; // @[BTB.scala 97:{19,19}]
  wire [31:0] _GEN_62 = 3'h7 == lookupIdx ? table_7_1_target : _GEN_60; // @[BTB.scala 97:{19,19}]
  wire [1:0] _GEN_63 = 3'h7 == lookupIdx ? table_7_1_counter : _GEN_61; // @[BTB.scala 97:{19,19}]
  wire [1:0] lEntry_counter = lHit0 ? _GEN_47 : _GEN_63; // @[BTB.scala 97:19]
  wire [2:0] updIdx = io_updatePC[4:2]; // @[BTB.scala 104:27]
  wire [26:0] updTag = io_updatePC[31:5]; // @[BTB.scala 105:27]
  wire [26:0] _GEN_65 = 3'h1 == updIdx ? table_1_0_tag : table_0_0_tag; // @[BTB.scala 109:{40,40}]
  wire [26:0] _GEN_66 = 3'h2 == updIdx ? table_2_0_tag : _GEN_65; // @[BTB.scala 109:{40,40}]
  wire [26:0] _GEN_67 = 3'h3 == updIdx ? table_3_0_tag : _GEN_66; // @[BTB.scala 109:{40,40}]
  wire [26:0] _GEN_68 = 3'h4 == updIdx ? table_4_0_tag : _GEN_67; // @[BTB.scala 109:{40,40}]
  wire [26:0] _GEN_69 = 3'h5 == updIdx ? table_5_0_tag : _GEN_68; // @[BTB.scala 109:{40,40}]
  wire [26:0] _GEN_70 = 3'h6 == updIdx ? table_6_0_tag : _GEN_69; // @[BTB.scala 109:{40,40}]
  wire [26:0] _GEN_71 = 3'h7 == updIdx ? table_7_0_tag : _GEN_70; // @[BTB.scala 109:{40,40}]
  wire  _GEN_73 = 3'h1 == updIdx ? table_1_0_valid : table_0_0_valid; // @[BTB.scala 109:{27,27}]
  wire  _GEN_74 = 3'h2 == updIdx ? table_2_0_valid : _GEN_73; // @[BTB.scala 109:{27,27}]
  wire  _GEN_75 = 3'h3 == updIdx ? table_3_0_valid : _GEN_74; // @[BTB.scala 109:{27,27}]
  wire  _GEN_76 = 3'h4 == updIdx ? table_4_0_valid : _GEN_75; // @[BTB.scala 109:{27,27}]
  wire  _GEN_77 = 3'h5 == updIdx ? table_5_0_valid : _GEN_76; // @[BTB.scala 109:{27,27}]
  wire  _GEN_78 = 3'h6 == updIdx ? table_6_0_valid : _GEN_77; // @[BTB.scala 109:{27,27}]
  wire  _GEN_79 = 3'h7 == updIdx ? table_7_0_valid : _GEN_78; // @[BTB.scala 109:{27,27}]
  wire  uHit0 = _GEN_79 & _GEN_71 == updTag; // @[BTB.scala 109:27]
  wire [26:0] _GEN_81 = 3'h1 == updIdx ? table_1_1_tag : table_0_1_tag; // @[BTB.scala 110:{40,40}]
  wire [26:0] _GEN_82 = 3'h2 == updIdx ? table_2_1_tag : _GEN_81; // @[BTB.scala 110:{40,40}]
  wire [26:0] _GEN_83 = 3'h3 == updIdx ? table_3_1_tag : _GEN_82; // @[BTB.scala 110:{40,40}]
  wire [26:0] _GEN_84 = 3'h4 == updIdx ? table_4_1_tag : _GEN_83; // @[BTB.scala 110:{40,40}]
  wire [26:0] _GEN_85 = 3'h5 == updIdx ? table_5_1_tag : _GEN_84; // @[BTB.scala 110:{40,40}]
  wire [26:0] _GEN_86 = 3'h6 == updIdx ? table_6_1_tag : _GEN_85; // @[BTB.scala 110:{40,40}]
  wire [26:0] _GEN_87 = 3'h7 == updIdx ? table_7_1_tag : _GEN_86; // @[BTB.scala 110:{40,40}]
  wire  _GEN_89 = 3'h1 == updIdx ? table_1_1_valid : table_0_1_valid; // @[BTB.scala 110:{27,27}]
  wire  _GEN_90 = 3'h2 == updIdx ? table_2_1_valid : _GEN_89; // @[BTB.scala 110:{27,27}]
  wire  _GEN_91 = 3'h3 == updIdx ? table_3_1_valid : _GEN_90; // @[BTB.scala 110:{27,27}]
  wire  _GEN_92 = 3'h4 == updIdx ? table_4_1_valid : _GEN_91; // @[BTB.scala 110:{27,27}]
  wire  _GEN_93 = 3'h5 == updIdx ? table_5_1_valid : _GEN_92; // @[BTB.scala 110:{27,27}]
  wire  _GEN_94 = 3'h6 == updIdx ? table_6_1_valid : _GEN_93; // @[BTB.scala 110:{27,27}]
  wire  _GEN_95 = 3'h7 == updIdx ? table_7_1_valid : _GEN_94; // @[BTB.scala 110:{27,27}]
  wire  uHit1 = _GEN_95 & _GEN_87 == updTag; // @[BTB.scala 110:27]
  wire  uHit = uHit0 | uHit1; // @[BTB.scala 111:21]
  wire [1:0] _GEN_97 = 3'h1 == updIdx ? table_1_0_counter : table_0_0_counter; // @[BTB.scala 115:{59,59}]
  wire [1:0] _GEN_98 = 3'h2 == updIdx ? table_2_0_counter : _GEN_97; // @[BTB.scala 115:{59,59}]
  wire [1:0] _GEN_99 = 3'h3 == updIdx ? table_3_0_counter : _GEN_98; // @[BTB.scala 115:{59,59}]
  wire [1:0] _GEN_100 = 3'h4 == updIdx ? table_4_0_counter : _GEN_99; // @[BTB.scala 115:{59,59}]
  wire [1:0] _GEN_101 = 3'h5 == updIdx ? table_5_0_counter : _GEN_100; // @[BTB.scala 115:{59,59}]
  wire [1:0] _GEN_102 = 3'h6 == updIdx ? table_6_0_counter : _GEN_101; // @[BTB.scala 115:{59,59}]
  wire [1:0] _GEN_103 = 3'h7 == updIdx ? table_7_0_counter : _GEN_102; // @[BTB.scala 115:{59,59}]
  wire [1:0] _GEN_105 = 3'h1 == updIdx ? table_1_1_counter : table_0_1_counter; // @[BTB.scala 115:{77,77}]
  wire [1:0] _GEN_106 = 3'h2 == updIdx ? table_2_1_counter : _GEN_105; // @[BTB.scala 115:{77,77}]
  wire [1:0] _GEN_107 = 3'h3 == updIdx ? table_3_1_counter : _GEN_106; // @[BTB.scala 115:{77,77}]
  wire [1:0] _GEN_108 = 3'h4 == updIdx ? table_4_1_counter : _GEN_107; // @[BTB.scala 115:{77,77}]
  wire [1:0] _GEN_109 = 3'h5 == updIdx ? table_5_1_counter : _GEN_108; // @[BTB.scala 115:{77,77}]
  wire [1:0] _GEN_110 = 3'h6 == updIdx ? table_6_1_counter : _GEN_109; // @[BTB.scala 115:{77,77}]
  wire [1:0] _GEN_111 = 3'h7 == updIdx ? table_7_1_counter : _GEN_110; // @[BTB.scala 115:{77,77}]
  wire  _oldPredictTaken_T_2 = uHit0 ? _GEN_103[1] : _GEN_111[1]; // @[BTB.scala 115:38]
  wire  oldPredictTaken = uHit & _oldPredictTaken_T_2; // @[BTB.scala 115:28]
  wire  actualTaken = oldPredictTaken != io_mispredicted; // @[BTB.scala 116:41]
  wire [1:0] _oldCounter_T = uHit0 ? _GEN_103 : _GEN_111; // @[BTB.scala 119:33]
  wire [1:0] newCounterOnAlloc = actualTaken ? 2'h2 : 2'h1; // @[BTB.scala 130:30]
  wire  _writeWay_T = uHit0 ? 1'h0 : 1'h1; // @[BTB.scala 135:31]
  wire  _GEN_113 = 3'h1 == updIdx ? lru_1 : lru_0; // @[BTB.scala 135:{21,21}]
  wire  _GEN_114 = 3'h2 == updIdx ? lru_2 : _GEN_113; // @[BTB.scala 135:{21,21}]
  wire  _GEN_115 = 3'h3 == updIdx ? lru_3 : _GEN_114; // @[BTB.scala 135:{21,21}]
  wire  _GEN_116 = 3'h4 == updIdx ? lru_4 : _GEN_115; // @[BTB.scala 135:{21,21}]
  wire  _GEN_117 = 3'h5 == updIdx ? lru_5 : _GEN_116; // @[BTB.scala 135:{21,21}]
  wire  _GEN_118 = 3'h6 == updIdx ? lru_6 : _GEN_117; // @[BTB.scala 135:{21,21}]
  wire  _GEN_119 = 3'h7 == updIdx ? lru_7 : _GEN_118; // @[BTB.scala 135:{21,21}]
  wire  writeWay = uHit ? _writeWay_T : _GEN_119; // @[BTB.scala 135:21]
  wire  _GEN_264 = 3'h0 == updIdx; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_265 = ~writeWay; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_120 = 3'h0 == updIdx & ~writeWay | table_0_0_valid; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_121 = 3'h0 == updIdx & writeWay | table_0_1_valid; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_269 = 3'h1 == updIdx; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_122 = 3'h1 == updIdx & ~writeWay | table_1_0_valid; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_123 = 3'h1 == updIdx & writeWay | table_1_1_valid; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_274 = 3'h2 == updIdx; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_124 = 3'h2 == updIdx & ~writeWay | table_2_0_valid; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_125 = 3'h2 == updIdx & writeWay | table_2_1_valid; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_279 = 3'h3 == updIdx; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_126 = 3'h3 == updIdx & ~writeWay | table_3_0_valid; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_127 = 3'h3 == updIdx & writeWay | table_3_1_valid; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_284 = 3'h4 == updIdx; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_128 = 3'h4 == updIdx & ~writeWay | table_4_0_valid; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_129 = 3'h4 == updIdx & writeWay | table_4_1_valid; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_289 = 3'h5 == updIdx; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_130 = 3'h5 == updIdx & ~writeWay | table_5_0_valid; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_131 = 3'h5 == updIdx & writeWay | table_5_1_valid; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_294 = 3'h6 == updIdx; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_132 = 3'h6 == updIdx & ~writeWay | table_6_0_valid; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_133 = 3'h6 == updIdx & writeWay | table_6_1_valid; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_299 = 3'h7 == updIdx; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_134 = 3'h7 == updIdx & ~writeWay | table_7_0_valid; // @[BTB.scala 138:{37,37} 75:22]
  wire  _GEN_135 = 3'h7 == updIdx & writeWay | table_7_1_valid; // @[BTB.scala 138:{37,37} 75:22]
  wire  _T_1 = ~reset; // @[BTB.scala 146:9]
  BTB_ctrl btbCtrl ( // @[BTB.scala 122:23]
    .io_currentState(btbCtrl_io_currentState),
    .io_taken(btbCtrl_io_taken),
    .io_nextState(btbCtrl_io_nextState)
  );
  assign io_target = lHit0 ? _GEN_46 : _GEN_62; // @[BTB.scala 97:19]
  assign io_predictTaken = lHit & lEntry_counter[1]; // @[BTB.scala 101:27]
  assign btbCtrl_io_currentState = uHit ? _oldCounter_T : 2'h0; // @[BTB.scala 119:23]
  assign btbCtrl_io_taken = oldPredictTaken != io_mispredicted; // @[BTB.scala 116:41]
  always @(posedge clock) begin
    if (reset) begin // @[BTB.scala 75:22]
      table_0_0_valid <= 1'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      table_0_0_valid <= _GEN_120;
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_0_0_tag <= 27'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_264 & _GEN_265) begin // @[BTB.scala 139:37]
        table_0_0_tag <= updTag; // @[BTB.scala 139:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_0_0_target <= 32'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_264 & _GEN_265) begin // @[BTB.scala 140:37]
        table_0_0_target <= io_updateTarget; // @[BTB.scala 140:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_0_0_counter <= 2'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_264 & _GEN_265) begin // @[BTB.scala 141:37]
        if (uHit) begin // @[BTB.scala 132:25]
          table_0_0_counter <= btbCtrl_io_nextState;
        end else begin
          table_0_0_counter <= newCounterOnAlloc;
        end
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_0_1_valid <= 1'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      table_0_1_valid <= _GEN_121;
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_0_1_tag <= 27'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_264 & writeWay) begin // @[BTB.scala 139:37]
        table_0_1_tag <= updTag; // @[BTB.scala 139:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_0_1_target <= 32'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_264 & writeWay) begin // @[BTB.scala 140:37]
        table_0_1_target <= io_updateTarget; // @[BTB.scala 140:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_0_1_counter <= 2'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_264 & writeWay) begin // @[BTB.scala 141:37]
        if (uHit) begin // @[BTB.scala 132:25]
          table_0_1_counter <= btbCtrl_io_nextState;
        end else begin
          table_0_1_counter <= newCounterOnAlloc;
        end
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_1_0_valid <= 1'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      table_1_0_valid <= _GEN_122;
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_1_0_tag <= 27'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_269 & _GEN_265) begin // @[BTB.scala 139:37]
        table_1_0_tag <= updTag; // @[BTB.scala 139:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_1_0_target <= 32'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_269 & _GEN_265) begin // @[BTB.scala 140:37]
        table_1_0_target <= io_updateTarget; // @[BTB.scala 140:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_1_0_counter <= 2'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_269 & _GEN_265) begin // @[BTB.scala 141:37]
        if (uHit) begin // @[BTB.scala 132:25]
          table_1_0_counter <= btbCtrl_io_nextState;
        end else begin
          table_1_0_counter <= newCounterOnAlloc;
        end
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_1_1_valid <= 1'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      table_1_1_valid <= _GEN_123;
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_1_1_tag <= 27'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_269 & writeWay) begin // @[BTB.scala 139:37]
        table_1_1_tag <= updTag; // @[BTB.scala 139:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_1_1_target <= 32'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_269 & writeWay) begin // @[BTB.scala 140:37]
        table_1_1_target <= io_updateTarget; // @[BTB.scala 140:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_1_1_counter <= 2'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_269 & writeWay) begin // @[BTB.scala 141:37]
        if (uHit) begin // @[BTB.scala 132:25]
          table_1_1_counter <= btbCtrl_io_nextState;
        end else begin
          table_1_1_counter <= newCounterOnAlloc;
        end
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_2_0_valid <= 1'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      table_2_0_valid <= _GEN_124;
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_2_0_tag <= 27'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_274 & _GEN_265) begin // @[BTB.scala 139:37]
        table_2_0_tag <= updTag; // @[BTB.scala 139:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_2_0_target <= 32'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_274 & _GEN_265) begin // @[BTB.scala 140:37]
        table_2_0_target <= io_updateTarget; // @[BTB.scala 140:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_2_0_counter <= 2'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_274 & _GEN_265) begin // @[BTB.scala 141:37]
        if (uHit) begin // @[BTB.scala 132:25]
          table_2_0_counter <= btbCtrl_io_nextState;
        end else begin
          table_2_0_counter <= newCounterOnAlloc;
        end
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_2_1_valid <= 1'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      table_2_1_valid <= _GEN_125;
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_2_1_tag <= 27'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_274 & writeWay) begin // @[BTB.scala 139:37]
        table_2_1_tag <= updTag; // @[BTB.scala 139:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_2_1_target <= 32'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_274 & writeWay) begin // @[BTB.scala 140:37]
        table_2_1_target <= io_updateTarget; // @[BTB.scala 140:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_2_1_counter <= 2'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_274 & writeWay) begin // @[BTB.scala 141:37]
        if (uHit) begin // @[BTB.scala 132:25]
          table_2_1_counter <= btbCtrl_io_nextState;
        end else begin
          table_2_1_counter <= newCounterOnAlloc;
        end
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_3_0_valid <= 1'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      table_3_0_valid <= _GEN_126;
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_3_0_tag <= 27'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_279 & _GEN_265) begin // @[BTB.scala 139:37]
        table_3_0_tag <= updTag; // @[BTB.scala 139:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_3_0_target <= 32'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_279 & _GEN_265) begin // @[BTB.scala 140:37]
        table_3_0_target <= io_updateTarget; // @[BTB.scala 140:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_3_0_counter <= 2'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_279 & _GEN_265) begin // @[BTB.scala 141:37]
        if (uHit) begin // @[BTB.scala 132:25]
          table_3_0_counter <= btbCtrl_io_nextState;
        end else begin
          table_3_0_counter <= newCounterOnAlloc;
        end
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_3_1_valid <= 1'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      table_3_1_valid <= _GEN_127;
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_3_1_tag <= 27'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_279 & writeWay) begin // @[BTB.scala 139:37]
        table_3_1_tag <= updTag; // @[BTB.scala 139:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_3_1_target <= 32'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_279 & writeWay) begin // @[BTB.scala 140:37]
        table_3_1_target <= io_updateTarget; // @[BTB.scala 140:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_3_1_counter <= 2'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_279 & writeWay) begin // @[BTB.scala 141:37]
        if (uHit) begin // @[BTB.scala 132:25]
          table_3_1_counter <= btbCtrl_io_nextState;
        end else begin
          table_3_1_counter <= newCounterOnAlloc;
        end
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_4_0_valid <= 1'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      table_4_0_valid <= _GEN_128;
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_4_0_tag <= 27'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_284 & _GEN_265) begin // @[BTB.scala 139:37]
        table_4_0_tag <= updTag; // @[BTB.scala 139:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_4_0_target <= 32'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_284 & _GEN_265) begin // @[BTB.scala 140:37]
        table_4_0_target <= io_updateTarget; // @[BTB.scala 140:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_4_0_counter <= 2'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_284 & _GEN_265) begin // @[BTB.scala 141:37]
        if (uHit) begin // @[BTB.scala 132:25]
          table_4_0_counter <= btbCtrl_io_nextState;
        end else begin
          table_4_0_counter <= newCounterOnAlloc;
        end
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_4_1_valid <= 1'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      table_4_1_valid <= _GEN_129;
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_4_1_tag <= 27'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_284 & writeWay) begin // @[BTB.scala 139:37]
        table_4_1_tag <= updTag; // @[BTB.scala 139:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_4_1_target <= 32'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_284 & writeWay) begin // @[BTB.scala 140:37]
        table_4_1_target <= io_updateTarget; // @[BTB.scala 140:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_4_1_counter <= 2'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_284 & writeWay) begin // @[BTB.scala 141:37]
        if (uHit) begin // @[BTB.scala 132:25]
          table_4_1_counter <= btbCtrl_io_nextState;
        end else begin
          table_4_1_counter <= newCounterOnAlloc;
        end
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_5_0_valid <= 1'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      table_5_0_valid <= _GEN_130;
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_5_0_tag <= 27'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_289 & _GEN_265) begin // @[BTB.scala 139:37]
        table_5_0_tag <= updTag; // @[BTB.scala 139:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_5_0_target <= 32'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_289 & _GEN_265) begin // @[BTB.scala 140:37]
        table_5_0_target <= io_updateTarget; // @[BTB.scala 140:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_5_0_counter <= 2'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_289 & _GEN_265) begin // @[BTB.scala 141:37]
        if (uHit) begin // @[BTB.scala 132:25]
          table_5_0_counter <= btbCtrl_io_nextState;
        end else begin
          table_5_0_counter <= newCounterOnAlloc;
        end
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_5_1_valid <= 1'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      table_5_1_valid <= _GEN_131;
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_5_1_tag <= 27'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_289 & writeWay) begin // @[BTB.scala 139:37]
        table_5_1_tag <= updTag; // @[BTB.scala 139:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_5_1_target <= 32'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_289 & writeWay) begin // @[BTB.scala 140:37]
        table_5_1_target <= io_updateTarget; // @[BTB.scala 140:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_5_1_counter <= 2'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_289 & writeWay) begin // @[BTB.scala 141:37]
        if (uHit) begin // @[BTB.scala 132:25]
          table_5_1_counter <= btbCtrl_io_nextState;
        end else begin
          table_5_1_counter <= newCounterOnAlloc;
        end
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_6_0_valid <= 1'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      table_6_0_valid <= _GEN_132;
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_6_0_tag <= 27'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_294 & _GEN_265) begin // @[BTB.scala 139:37]
        table_6_0_tag <= updTag; // @[BTB.scala 139:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_6_0_target <= 32'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_294 & _GEN_265) begin // @[BTB.scala 140:37]
        table_6_0_target <= io_updateTarget; // @[BTB.scala 140:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_6_0_counter <= 2'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_294 & _GEN_265) begin // @[BTB.scala 141:37]
        if (uHit) begin // @[BTB.scala 132:25]
          table_6_0_counter <= btbCtrl_io_nextState;
        end else begin
          table_6_0_counter <= newCounterOnAlloc;
        end
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_6_1_valid <= 1'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      table_6_1_valid <= _GEN_133;
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_6_1_tag <= 27'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_294 & writeWay) begin // @[BTB.scala 139:37]
        table_6_1_tag <= updTag; // @[BTB.scala 139:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_6_1_target <= 32'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_294 & writeWay) begin // @[BTB.scala 140:37]
        table_6_1_target <= io_updateTarget; // @[BTB.scala 140:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_6_1_counter <= 2'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_294 & writeWay) begin // @[BTB.scala 141:37]
        if (uHit) begin // @[BTB.scala 132:25]
          table_6_1_counter <= btbCtrl_io_nextState;
        end else begin
          table_6_1_counter <= newCounterOnAlloc;
        end
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_7_0_valid <= 1'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      table_7_0_valid <= _GEN_134;
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_7_0_tag <= 27'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_299 & _GEN_265) begin // @[BTB.scala 139:37]
        table_7_0_tag <= updTag; // @[BTB.scala 139:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_7_0_target <= 32'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_299 & _GEN_265) begin // @[BTB.scala 140:37]
        table_7_0_target <= io_updateTarget; // @[BTB.scala 140:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_7_0_counter <= 2'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_299 & _GEN_265) begin // @[BTB.scala 141:37]
        if (uHit) begin // @[BTB.scala 132:25]
          table_7_0_counter <= btbCtrl_io_nextState;
        end else begin
          table_7_0_counter <= newCounterOnAlloc;
        end
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_7_1_valid <= 1'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      table_7_1_valid <= _GEN_135;
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_7_1_tag <= 27'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_299 & writeWay) begin // @[BTB.scala 139:37]
        table_7_1_tag <= updTag; // @[BTB.scala 139:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_7_1_target <= 32'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_299 & writeWay) begin // @[BTB.scala 140:37]
        table_7_1_target <= io_updateTarget; // @[BTB.scala 140:37]
      end
    end
    if (reset) begin // @[BTB.scala 75:22]
      table_7_1_counter <= 2'h0; // @[BTB.scala 75:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (_GEN_299 & writeWay) begin // @[BTB.scala 141:37]
        if (uHit) begin // @[BTB.scala 132:25]
          table_7_1_counter <= btbCtrl_io_nextState;
        end else begin
          table_7_1_counter <= newCounterOnAlloc;
        end
      end
    end
    if (reset) begin // @[BTB.scala 86:22]
      lru_0 <= 1'h0; // @[BTB.scala 86:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (3'h0 == updIdx) begin // @[BTB.scala 143:17]
        lru_0 <= _GEN_265; // @[BTB.scala 143:17]
      end
    end
    if (reset) begin // @[BTB.scala 86:22]
      lru_1 <= 1'h0; // @[BTB.scala 86:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (3'h1 == updIdx) begin // @[BTB.scala 143:17]
        lru_1 <= _GEN_265; // @[BTB.scala 143:17]
      end
    end
    if (reset) begin // @[BTB.scala 86:22]
      lru_2 <= 1'h0; // @[BTB.scala 86:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (3'h2 == updIdx) begin // @[BTB.scala 143:17]
        lru_2 <= _GEN_265; // @[BTB.scala 143:17]
      end
    end
    if (reset) begin // @[BTB.scala 86:22]
      lru_3 <= 1'h0; // @[BTB.scala 86:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (3'h3 == updIdx) begin // @[BTB.scala 143:17]
        lru_3 <= _GEN_265; // @[BTB.scala 143:17]
      end
    end
    if (reset) begin // @[BTB.scala 86:22]
      lru_4 <= 1'h0; // @[BTB.scala 86:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (3'h4 == updIdx) begin // @[BTB.scala 143:17]
        lru_4 <= _GEN_265; // @[BTB.scala 143:17]
      end
    end
    if (reset) begin // @[BTB.scala 86:22]
      lru_5 <= 1'h0; // @[BTB.scala 86:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (3'h5 == updIdx) begin // @[BTB.scala 143:17]
        lru_5 <= _GEN_265; // @[BTB.scala 143:17]
      end
    end
    if (reset) begin // @[BTB.scala 86:22]
      lru_6 <= 1'h0; // @[BTB.scala 86:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (3'h6 == updIdx) begin // @[BTB.scala 143:17]
        lru_6 <= _GEN_265; // @[BTB.scala 143:17]
      end
    end
    if (reset) begin // @[BTB.scala 86:22]
      lru_7 <= 1'h0; // @[BTB.scala 86:22]
    end else if (io_update) begin // @[BTB.scala 137:19]
      if (3'h7 == updIdx) begin // @[BTB.scala 143:17]
        lru_7 <= _GEN_265; // @[BTB.scala 143:17]
      end
    end
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (~reset) begin
          $fwrite(32'h80000002," ================= BTB TABLE DUMP =================\n"); // @[BTB.scala 146:9]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"  set=0 way=0  valid=%x  tag=%x  target=%x  counter=%x  predictTaken=%x\n",
            table_0_0_valid,table_0_0_tag,table_0_0_target,table_0_0_counter,table_0_0_counter[1]); // @[BTB.scala 150:13]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"  set=0 way=1  valid=%x  tag=%x  target=%x  counter=%x  predictTaken=%x\n",
            table_0_1_valid,table_0_1_tag,table_0_1_target,table_0_1_counter,table_0_1_counter[1]); // @[BTB.scala 150:13]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"  set=1 way=0  valid=%x  tag=%x  target=%x  counter=%x  predictTaken=%x\n",
            table_1_0_valid,table_1_0_tag,table_1_0_target,table_1_0_counter,table_1_0_counter[1]); // @[BTB.scala 150:13]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"  set=1 way=1  valid=%x  tag=%x  target=%x  counter=%x  predictTaken=%x\n",
            table_1_1_valid,table_1_1_tag,table_1_1_target,table_1_1_counter,table_1_1_counter[1]); // @[BTB.scala 150:13]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"  set=2 way=0  valid=%x  tag=%x  target=%x  counter=%x  predictTaken=%x\n",
            table_2_0_valid,table_2_0_tag,table_2_0_target,table_2_0_counter,table_2_0_counter[1]); // @[BTB.scala 150:13]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"  set=2 way=1  valid=%x  tag=%x  target=%x  counter=%x  predictTaken=%x\n",
            table_2_1_valid,table_2_1_tag,table_2_1_target,table_2_1_counter,table_2_1_counter[1]); // @[BTB.scala 150:13]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"  set=3 way=0  valid=%x  tag=%x  target=%x  counter=%x  predictTaken=%x\n",
            table_3_0_valid,table_3_0_tag,table_3_0_target,table_3_0_counter,table_3_0_counter[1]); // @[BTB.scala 150:13]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"  set=3 way=1  valid=%x  tag=%x  target=%x  counter=%x  predictTaken=%x\n",
            table_3_1_valid,table_3_1_tag,table_3_1_target,table_3_1_counter,table_3_1_counter[1]); // @[BTB.scala 150:13]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"  set=4 way=0  valid=%x  tag=%x  target=%x  counter=%x  predictTaken=%x\n",
            table_4_0_valid,table_4_0_tag,table_4_0_target,table_4_0_counter,table_4_0_counter[1]); // @[BTB.scala 150:13]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"  set=4 way=1  valid=%x  tag=%x  target=%x  counter=%x  predictTaken=%x\n",
            table_4_1_valid,table_4_1_tag,table_4_1_target,table_4_1_counter,table_4_1_counter[1]); // @[BTB.scala 150:13]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"  set=5 way=0  valid=%x  tag=%x  target=%x  counter=%x  predictTaken=%x\n",
            table_5_0_valid,table_5_0_tag,table_5_0_target,table_5_0_counter,table_5_0_counter[1]); // @[BTB.scala 150:13]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"  set=5 way=1  valid=%x  tag=%x  target=%x  counter=%x  predictTaken=%x\n",
            table_5_1_valid,table_5_1_tag,table_5_1_target,table_5_1_counter,table_5_1_counter[1]); // @[BTB.scala 150:13]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"  set=6 way=0  valid=%x  tag=%x  target=%x  counter=%x  predictTaken=%x\n",
            table_6_0_valid,table_6_0_tag,table_6_0_target,table_6_0_counter,table_6_0_counter[1]); // @[BTB.scala 150:13]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"  set=6 way=1  valid=%x  tag=%x  target=%x  counter=%x  predictTaken=%x\n",
            table_6_1_valid,table_6_1_tag,table_6_1_target,table_6_1_counter,table_6_1_counter[1]); // @[BTB.scala 150:13]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"  set=7 way=0  valid=%x  tag=%x  target=%x  counter=%x  predictTaken=%x\n",
            table_7_0_valid,table_7_0_tag,table_7_0_target,table_7_0_counter,table_7_0_counter[1]); // @[BTB.scala 150:13]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"  set=7 way=1  valid=%x  tag=%x  target=%x  counter=%x  predictTaken=%x\n",
            table_7_1_valid,table_7_1_tag,table_7_1_target,table_7_1_counter,table_7_1_counter[1]); // @[BTB.scala 150:13]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"  LRU bits per set: Vec(%d, %d, %d, %d, %d, %d, %d, %d)\n",lru_0,lru_1,lru_2,lru_3,lru_4
            ,lru_5,lru_6,lru_7); // @[BTB.scala 154:9]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002," ====================================================\n"); // @[BTB.scala 155:9]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  table_0_0_valid = _RAND_0[0:0];
  _RAND_1 = {1{`RANDOM}};
  table_0_0_tag = _RAND_1[26:0];
  _RAND_2 = {1{`RANDOM}};
  table_0_0_target = _RAND_2[31:0];
  _RAND_3 = {1{`RANDOM}};
  table_0_0_counter = _RAND_3[1:0];
  _RAND_4 = {1{`RANDOM}};
  table_0_1_valid = _RAND_4[0:0];
  _RAND_5 = {1{`RANDOM}};
  table_0_1_tag = _RAND_5[26:0];
  _RAND_6 = {1{`RANDOM}};
  table_0_1_target = _RAND_6[31:0];
  _RAND_7 = {1{`RANDOM}};
  table_0_1_counter = _RAND_7[1:0];
  _RAND_8 = {1{`RANDOM}};
  table_1_0_valid = _RAND_8[0:0];
  _RAND_9 = {1{`RANDOM}};
  table_1_0_tag = _RAND_9[26:0];
  _RAND_10 = {1{`RANDOM}};
  table_1_0_target = _RAND_10[31:0];
  _RAND_11 = {1{`RANDOM}};
  table_1_0_counter = _RAND_11[1:0];
  _RAND_12 = {1{`RANDOM}};
  table_1_1_valid = _RAND_12[0:0];
  _RAND_13 = {1{`RANDOM}};
  table_1_1_tag = _RAND_13[26:0];
  _RAND_14 = {1{`RANDOM}};
  table_1_1_target = _RAND_14[31:0];
  _RAND_15 = {1{`RANDOM}};
  table_1_1_counter = _RAND_15[1:0];
  _RAND_16 = {1{`RANDOM}};
  table_2_0_valid = _RAND_16[0:0];
  _RAND_17 = {1{`RANDOM}};
  table_2_0_tag = _RAND_17[26:0];
  _RAND_18 = {1{`RANDOM}};
  table_2_0_target = _RAND_18[31:0];
  _RAND_19 = {1{`RANDOM}};
  table_2_0_counter = _RAND_19[1:0];
  _RAND_20 = {1{`RANDOM}};
  table_2_1_valid = _RAND_20[0:0];
  _RAND_21 = {1{`RANDOM}};
  table_2_1_tag = _RAND_21[26:0];
  _RAND_22 = {1{`RANDOM}};
  table_2_1_target = _RAND_22[31:0];
  _RAND_23 = {1{`RANDOM}};
  table_2_1_counter = _RAND_23[1:0];
  _RAND_24 = {1{`RANDOM}};
  table_3_0_valid = _RAND_24[0:0];
  _RAND_25 = {1{`RANDOM}};
  table_3_0_tag = _RAND_25[26:0];
  _RAND_26 = {1{`RANDOM}};
  table_3_0_target = _RAND_26[31:0];
  _RAND_27 = {1{`RANDOM}};
  table_3_0_counter = _RAND_27[1:0];
  _RAND_28 = {1{`RANDOM}};
  table_3_1_valid = _RAND_28[0:0];
  _RAND_29 = {1{`RANDOM}};
  table_3_1_tag = _RAND_29[26:0];
  _RAND_30 = {1{`RANDOM}};
  table_3_1_target = _RAND_30[31:0];
  _RAND_31 = {1{`RANDOM}};
  table_3_1_counter = _RAND_31[1:0];
  _RAND_32 = {1{`RANDOM}};
  table_4_0_valid = _RAND_32[0:0];
  _RAND_33 = {1{`RANDOM}};
  table_4_0_tag = _RAND_33[26:0];
  _RAND_34 = {1{`RANDOM}};
  table_4_0_target = _RAND_34[31:0];
  _RAND_35 = {1{`RANDOM}};
  table_4_0_counter = _RAND_35[1:0];
  _RAND_36 = {1{`RANDOM}};
  table_4_1_valid = _RAND_36[0:0];
  _RAND_37 = {1{`RANDOM}};
  table_4_1_tag = _RAND_37[26:0];
  _RAND_38 = {1{`RANDOM}};
  table_4_1_target = _RAND_38[31:0];
  _RAND_39 = {1{`RANDOM}};
  table_4_1_counter = _RAND_39[1:0];
  _RAND_40 = {1{`RANDOM}};
  table_5_0_valid = _RAND_40[0:0];
  _RAND_41 = {1{`RANDOM}};
  table_5_0_tag = _RAND_41[26:0];
  _RAND_42 = {1{`RANDOM}};
  table_5_0_target = _RAND_42[31:0];
  _RAND_43 = {1{`RANDOM}};
  table_5_0_counter = _RAND_43[1:0];
  _RAND_44 = {1{`RANDOM}};
  table_5_1_valid = _RAND_44[0:0];
  _RAND_45 = {1{`RANDOM}};
  table_5_1_tag = _RAND_45[26:0];
  _RAND_46 = {1{`RANDOM}};
  table_5_1_target = _RAND_46[31:0];
  _RAND_47 = {1{`RANDOM}};
  table_5_1_counter = _RAND_47[1:0];
  _RAND_48 = {1{`RANDOM}};
  table_6_0_valid = _RAND_48[0:0];
  _RAND_49 = {1{`RANDOM}};
  table_6_0_tag = _RAND_49[26:0];
  _RAND_50 = {1{`RANDOM}};
  table_6_0_target = _RAND_50[31:0];
  _RAND_51 = {1{`RANDOM}};
  table_6_0_counter = _RAND_51[1:0];
  _RAND_52 = {1{`RANDOM}};
  table_6_1_valid = _RAND_52[0:0];
  _RAND_53 = {1{`RANDOM}};
  table_6_1_tag = _RAND_53[26:0];
  _RAND_54 = {1{`RANDOM}};
  table_6_1_target = _RAND_54[31:0];
  _RAND_55 = {1{`RANDOM}};
  table_6_1_counter = _RAND_55[1:0];
  _RAND_56 = {1{`RANDOM}};
  table_7_0_valid = _RAND_56[0:0];
  _RAND_57 = {1{`RANDOM}};
  table_7_0_tag = _RAND_57[26:0];
  _RAND_58 = {1{`RANDOM}};
  table_7_0_target = _RAND_58[31:0];
  _RAND_59 = {1{`RANDOM}};
  table_7_0_counter = _RAND_59[1:0];
  _RAND_60 = {1{`RANDOM}};
  table_7_1_valid = _RAND_60[0:0];
  _RAND_61 = {1{`RANDOM}};
  table_7_1_tag = _RAND_61[26:0];
  _RAND_62 = {1{`RANDOM}};
  table_7_1_target = _RAND_62[31:0];
  _RAND_63 = {1{`RANDOM}};
  table_7_1_counter = _RAND_63[1:0];
  _RAND_64 = {1{`RANDOM}};
  lru_0 = _RAND_64[0:0];
  _RAND_65 = {1{`RANDOM}};
  lru_1 = _RAND_65[0:0];
  _RAND_66 = {1{`RANDOM}};
  lru_2 = _RAND_66[0:0];
  _RAND_67 = {1{`RANDOM}};
  lru_3 = _RAND_67[0:0];
  _RAND_68 = {1{`RANDOM}};
  lru_4 = _RAND_68[0:0];
  _RAND_69 = {1{`RANDOM}};
  lru_5 = _RAND_69[0:0];
  _RAND_70 = {1{`RANDOM}};
  lru_6 = _RAND_70[0:0];
  _RAND_71 = {1{`RANDOM}};
  lru_7 = _RAND_71[0:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module PipelinedRV32Icore(
  input         clock,
  input         reset,
  output [31:0] io_check_res,
  output        io_exception,
  output [31:0] io_PCdebug,
  output [31:0] io_InstrDdebug,
  output [31:0] io_PCEdebug,
  output [31:0] io_RS1Edebug,
  output [31:0] io_RS2Edebug,
  output        io_BranchEdebug,
  output        io_JumpEdebug,
  output        io_PCSrcEdebug,
  output [31:0] io_PCTargetEdebug,
  output        io_WriteEnableWdebug,
  output [4:0]  io_rdWdebug,
  output        io_PCSrcE_debug
);
  wire  IFstage_clock; // @[core.scala 84:23]
  wire  IFstage_reset; // @[core.scala 84:23]
  wire  IFstage_io_PCSrcE; // @[core.scala 84:23]
  wire [31:0] IFstage_io_PCTargetE; // @[core.scala 84:23]
  wire [31:0] IFstage_io_InstrF; // @[core.scala 84:23]
  wire [31:0] IFstage_io_PCF; // @[core.scala 84:23]
  wire [31:0] IFstage_io_PCPlus4F; // @[core.scala 84:23]
  wire [31:0] IFstage_io_BTBTarget; // @[core.scala 84:23]
  wire  IFstage_io_BTBPredictTaken; // @[core.scala 84:23]
  wire  IFstage_io_PredictTakenF; // @[core.scala 84:23]
  wire  IFBarrier_clock; // @[core.scala 85:25]
  wire  IFBarrier_reset; // @[core.scala 85:25]
  wire [31:0] IFBarrier_io_InstrF; // @[core.scala 85:25]
  wire [31:0] IFBarrier_io_PCF; // @[core.scala 85:25]
  wire [31:0] IFBarrier_io_PCPlus4F; // @[core.scala 85:25]
  wire  IFBarrier_io_CLR; // @[core.scala 85:25]
  wire [31:0] IFBarrier_io_InstrD; // @[core.scala 85:25]
  wire [31:0] IFBarrier_io_PCD; // @[core.scala 85:25]
  wire [31:0] IFBarrier_io_PCPlus4D; // @[core.scala 85:25]
  wire  IFBarrier_io_PredictTakenF; // @[core.scala 85:25]
  wire  IFBarrier_io_PredictTakenD; // @[core.scala 85:25]
  wire  IDstage_clock; // @[core.scala 87:23]
  wire  IDstage_reset; // @[core.scala 87:23]
  wire [31:0] IDstage_io_inst; // @[core.scala 87:23]
  wire [31:0] IDstage_io_pcD; // @[core.scala 87:23]
  wire [31:0] IDstage_io_pcPlus4D; // @[core.scala 87:23]
  wire  IDstage_io_WriteEnableW; // @[core.scala 87:23]
  wire [4:0] IDstage_io_rdW; // @[core.scala 87:23]
  wire [31:0] IDstage_io_ResultW; // @[core.scala 87:23]
  wire [4:0] IDstage_io_uop; // @[core.scala 87:23]
  wire  IDstage_io_WriteEnableD; // @[core.scala 87:23]
  wire  IDstage_io_ALUSrcD; // @[core.scala 87:23]
  wire [31:0] IDstage_io_ImmExtD; // @[core.scala 87:23]
  wire  IDstage_io_BranchD; // @[core.scala 87:23]
  wire  IDstage_io_JumpD; // @[core.scala 87:23]
  wire  IDstage_io_XcptInvalid; // @[core.scala 87:23]
  wire [4:0] IDstage_io_rdD; // @[core.scala 87:23]
  wire [31:0] IDstage_io_RD1D; // @[core.scala 87:23]
  wire [31:0] IDstage_io_RD2D; // @[core.scala 87:23]
  wire [31:0] IDstage_io_pcD_out; // @[core.scala 87:23]
  wire [31:0] IDstage_io_pcPlus4D_out; // @[core.scala 87:23]
  wire  IDstage_io_PredictTakenF; // @[core.scala 87:23]
  wire  IDstage_io_PredictTakenD; // @[core.scala 87:23]
  wire  IDBarrier_clock; // @[core.scala 88:25]
  wire  IDBarrier_reset; // @[core.scala 88:25]
  wire [4:0] IDBarrier_io_uopD; // @[core.scala 88:25]
  wire [4:0] IDBarrier_io_rdD; // @[core.scala 88:25]
  wire [31:0] IDBarrier_io_RD1D; // @[core.scala 88:25]
  wire [31:0] IDBarrier_io_RD2D; // @[core.scala 88:25]
  wire  IDBarrier_io_XcptInvalidD; // @[core.scala 88:25]
  wire  IDBarrier_io_WriteEnableD; // @[core.scala 88:25]
  wire  IDBarrier_io_ALUSrcD; // @[core.scala 88:25]
  wire [31:0] IDBarrier_io_ImmExtD; // @[core.scala 88:25]
  wire  IDBarrier_io_BranchD; // @[core.scala 88:25]
  wire  IDBarrier_io_JumpD; // @[core.scala 88:25]
  wire [31:0] IDBarrier_io_PCD; // @[core.scala 88:25]
  wire [31:0] IDBarrier_io_PCPlus4D; // @[core.scala 88:25]
  wire [4:0] IDBarrier_io_rs1D; // @[core.scala 88:25]
  wire [4:0] IDBarrier_io_rs2D; // @[core.scala 88:25]
  wire  IDBarrier_io_CLR; // @[core.scala 88:25]
  wire [4:0] IDBarrier_io_Rs1E; // @[core.scala 88:25]
  wire [4:0] IDBarrier_io_Rs2E; // @[core.scala 88:25]
  wire [4:0] IDBarrier_io_uopE; // @[core.scala 88:25]
  wire [4:0] IDBarrier_io_rdE; // @[core.scala 88:25]
  wire [31:0] IDBarrier_io_RD1E; // @[core.scala 88:25]
  wire [31:0] IDBarrier_io_RD2E; // @[core.scala 88:25]
  wire  IDBarrier_io_XcptInvalidE; // @[core.scala 88:25]
  wire  IDBarrier_io_WriteEnableE; // @[core.scala 88:25]
  wire  IDBarrier_io_ALUSrcE; // @[core.scala 88:25]
  wire [31:0] IDBarrier_io_ImmExtE; // @[core.scala 88:25]
  wire  IDBarrier_io_BranchE; // @[core.scala 88:25]
  wire  IDBarrier_io_JumpE; // @[core.scala 88:25]
  wire [31:0] IDBarrier_io_PCE; // @[core.scala 88:25]
  wire [31:0] IDBarrier_io_PCPlus4E; // @[core.scala 88:25]
  wire  IDBarrier_io_PredictTakenD; // @[core.scala 88:25]
  wire  IDBarrier_io_PredictTakenE; // @[core.scala 88:25]
  wire [31:0] EXstage_io_RD1E; // @[core.scala 90:23]
  wire [31:0] EXstage_io_RD2E; // @[core.scala 90:23]
  wire [31:0] EXstage_io_ImmExtE; // @[core.scala 90:23]
  wire  EXstage_io_ALUSrcE; // @[core.scala 90:23]
  wire [4:0] EXstage_io_rdE; // @[core.scala 90:23]
  wire [4:0] EXstage_io_uopE; // @[core.scala 90:23]
  wire  EXstage_io_WriteEnableE; // @[core.scala 90:23]
  wire  EXstage_io_XcptInvalidE; // @[core.scala 90:23]
  wire [1:0] EXstage_io_ForwardAE; // @[core.scala 90:23]
  wire [1:0] EXstage_io_ForwardBE; // @[core.scala 90:23]
  wire [31:0] EXstage_io_ResultW; // @[core.scala 90:23]
  wire [31:0] EXstage_io_ALUResultM; // @[core.scala 90:23]
  wire [31:0] EXstage_io_PCE; // @[core.scala 90:23]
  wire [31:0] EXstage_io_PCPlus4E; // @[core.scala 90:23]
  wire  EXstage_io_BranchE; // @[core.scala 90:23]
  wire  EXstage_io_JumpE; // @[core.scala 90:23]
  wire [31:0] EXstage_io_ALUResultE; // @[core.scala 90:23]
  wire  EXstage_io_exceptionE; // @[core.scala 90:23]
  wire [4:0] EXstage_io_rdOutE; // @[core.scala 90:23]
  wire  EXstage_io_WriteEnableOutE; // @[core.scala 90:23]
  wire  EXstage_io_PCSrcE; // @[core.scala 90:23]
  wire [31:0] EXstage_io_PCTargetE; // @[core.scala 90:23]
  wire  EXstage_io_FlushE; // @[core.scala 90:23]
  wire  EXstage_io_PredictTakenE; // @[core.scala 90:23]
  wire  EXstage_io_BTBUpdate; // @[core.scala 90:23]
  wire [31:0] EXstage_io_BTBUpdatePC; // @[core.scala 90:23]
  wire [31:0] EXstage_io_BTBUpdateTarget; // @[core.scala 90:23]
  wire  EXstage_io_BTBMispredicted; // @[core.scala 90:23]
  wire  EXBarrier_clock; // @[core.scala 91:25]
  wire  EXBarrier_reset; // @[core.scala 91:25]
  wire [31:0] EXBarrier_io_ALUResultE; // @[core.scala 91:25]
  wire [4:0] EXBarrier_io_rdE; // @[core.scala 91:25]
  wire  EXBarrier_io_XcptInvalidE; // @[core.scala 91:25]
  wire  EXBarrier_io_WriteEnableE; // @[core.scala 91:25]
  wire [31:0] EXBarrier_io_ALUResultM; // @[core.scala 91:25]
  wire [4:0] EXBarrier_io_rdM; // @[core.scala 91:25]
  wire  EXBarrier_io_XcptInvalidM; // @[core.scala 91:25]
  wire  EXBarrier_io_WriteEnableM; // @[core.scala 91:25]
  wire  MEMBarrier_clock; // @[core.scala 94:26]
  wire  MEMBarrier_reset; // @[core.scala 94:26]
  wire [31:0] MEMBarrier_io_ALUResultE; // @[core.scala 94:26]
  wire [4:0] MEMBarrier_io_rdE; // @[core.scala 94:26]
  wire  MEMBarrier_io_XcptInvalidE; // @[core.scala 94:26]
  wire  MEMBarrier_io_WriteEnableE; // @[core.scala 94:26]
  wire [31:0] MEMBarrier_io_ALUResultM; // @[core.scala 94:26]
  wire [4:0] MEMBarrier_io_rdM; // @[core.scala 94:26]
  wire  MEMBarrier_io_XcptInvalidM; // @[core.scala 94:26]
  wire  MEMBarrier_io_WriteEnableM; // @[core.scala 94:26]
  wire [31:0] WBstage_io_ALUResultW; // @[core.scala 96:23]
  wire [4:0] WBstage_io_rdW; // @[core.scala 96:23]
  wire  WBstage_io_WriteEnableW; // @[core.scala 96:23]
  wire [4:0] WBstage_io_WriteEnableReq_addr; // @[core.scala 96:23]
  wire [31:0] WBstage_io_WriteEnableReq_data; // @[core.scala 96:23]
  wire  WBstage_io_WriteEnableReq_w_en; // @[core.scala 96:23]
  wire [31:0] WBstage_io_ResultW; // @[core.scala 96:23]
  wire  WBBarrier_clock; // @[core.scala 97:25]
  wire  WBBarrier_reset; // @[core.scala 97:25]
  wire [31:0] WBBarrier_io_ResultW; // @[core.scala 97:25]
  wire  WBBarrier_io_XcptInvalidW; // @[core.scala 97:25]
  wire [31:0] WBBarrier_io_check_res; // @[core.scala 97:25]
  wire  WBBarrier_io_exception; // @[core.scala 97:25]
  wire [31:0] ForwardingUnit_io_rs1_EX; // @[core.scala 99:30]
  wire [31:0] ForwardingUnit_io_rs2_EX; // @[core.scala 99:30]
  wire [31:0] ForwardingUnit_io_rd_MEM; // @[core.scala 99:30]
  wire [31:0] ForwardingUnit_io_rd_WB; // @[core.scala 99:30]
  wire  ForwardingUnit_io_wrEn_MEM; // @[core.scala 99:30]
  wire  ForwardingUnit_io_wrEn_WB; // @[core.scala 99:30]
  wire [1:0] ForwardingUnit_io_forwardA; // @[core.scala 99:30]
  wire [1:0] ForwardingUnit_io_forwardB; // @[core.scala 99:30]
  wire  BTB_clock; // @[core.scala 101:19]
  wire  BTB_reset; // @[core.scala 101:19]
  wire [31:0] BTB_io_PC; // @[core.scala 101:19]
  wire  BTB_io_update; // @[core.scala 101:19]
  wire [31:0] BTB_io_updatePC; // @[core.scala 101:19]
  wire [31:0] BTB_io_updateTarget; // @[core.scala 101:19]
  wire  BTB_io_mispredicted; // @[core.scala 101:19]
  wire [31:0] BTB_io_target; // @[core.scala 101:19]
  wire  BTB_io_predictTaken; // @[core.scala 101:19]
  wire  _T_1 = ~reset; // @[core.scala 222:9]
  IF IFstage ( // @[core.scala 84:23]
    .clock(IFstage_clock),
    .reset(IFstage_reset),
    .io_PCSrcE(IFstage_io_PCSrcE),
    .io_PCTargetE(IFstage_io_PCTargetE),
    .io_InstrF(IFstage_io_InstrF),
    .io_PCF(IFstage_io_PCF),
    .io_PCPlus4F(IFstage_io_PCPlus4F),
    .io_BTBTarget(IFstage_io_BTBTarget),
    .io_BTBPredictTaken(IFstage_io_BTBPredictTaken),
    .io_PredictTakenF(IFstage_io_PredictTakenF)
  );
  IFBarrier IFBarrier ( // @[core.scala 85:25]
    .clock(IFBarrier_clock),
    .reset(IFBarrier_reset),
    .io_InstrF(IFBarrier_io_InstrF),
    .io_PCF(IFBarrier_io_PCF),
    .io_PCPlus4F(IFBarrier_io_PCPlus4F),
    .io_CLR(IFBarrier_io_CLR),
    .io_InstrD(IFBarrier_io_InstrD),
    .io_PCD(IFBarrier_io_PCD),
    .io_PCPlus4D(IFBarrier_io_PCPlus4D),
    .io_PredictTakenF(IFBarrier_io_PredictTakenF),
    .io_PredictTakenD(IFBarrier_io_PredictTakenD)
  );
  ID IDstage ( // @[core.scala 87:23]
    .clock(IDstage_clock),
    .reset(IDstage_reset),
    .io_inst(IDstage_io_inst),
    .io_pcD(IDstage_io_pcD),
    .io_pcPlus4D(IDstage_io_pcPlus4D),
    .io_WriteEnableW(IDstage_io_WriteEnableW),
    .io_rdW(IDstage_io_rdW),
    .io_ResultW(IDstage_io_ResultW),
    .io_uop(IDstage_io_uop),
    .io_WriteEnableD(IDstage_io_WriteEnableD),
    .io_ALUSrcD(IDstage_io_ALUSrcD),
    .io_ImmExtD(IDstage_io_ImmExtD),
    .io_BranchD(IDstage_io_BranchD),
    .io_JumpD(IDstage_io_JumpD),
    .io_XcptInvalid(IDstage_io_XcptInvalid),
    .io_rdD(IDstage_io_rdD),
    .io_RD1D(IDstage_io_RD1D),
    .io_RD2D(IDstage_io_RD2D),
    .io_pcD_out(IDstage_io_pcD_out),
    .io_pcPlus4D_out(IDstage_io_pcPlus4D_out),
    .io_PredictTakenF(IDstage_io_PredictTakenF),
    .io_PredictTakenD(IDstage_io_PredictTakenD)
  );
  IDBarrier IDBarrier ( // @[core.scala 88:25]
    .clock(IDBarrier_clock),
    .reset(IDBarrier_reset),
    .io_uopD(IDBarrier_io_uopD),
    .io_rdD(IDBarrier_io_rdD),
    .io_RD1D(IDBarrier_io_RD1D),
    .io_RD2D(IDBarrier_io_RD2D),
    .io_XcptInvalidD(IDBarrier_io_XcptInvalidD),
    .io_WriteEnableD(IDBarrier_io_WriteEnableD),
    .io_ALUSrcD(IDBarrier_io_ALUSrcD),
    .io_ImmExtD(IDBarrier_io_ImmExtD),
    .io_BranchD(IDBarrier_io_BranchD),
    .io_JumpD(IDBarrier_io_JumpD),
    .io_PCD(IDBarrier_io_PCD),
    .io_PCPlus4D(IDBarrier_io_PCPlus4D),
    .io_rs1D(IDBarrier_io_rs1D),
    .io_rs2D(IDBarrier_io_rs2D),
    .io_CLR(IDBarrier_io_CLR),
    .io_Rs1E(IDBarrier_io_Rs1E),
    .io_Rs2E(IDBarrier_io_Rs2E),
    .io_uopE(IDBarrier_io_uopE),
    .io_rdE(IDBarrier_io_rdE),
    .io_RD1E(IDBarrier_io_RD1E),
    .io_RD2E(IDBarrier_io_RD2E),
    .io_XcptInvalidE(IDBarrier_io_XcptInvalidE),
    .io_WriteEnableE(IDBarrier_io_WriteEnableE),
    .io_ALUSrcE(IDBarrier_io_ALUSrcE),
    .io_ImmExtE(IDBarrier_io_ImmExtE),
    .io_BranchE(IDBarrier_io_BranchE),
    .io_JumpE(IDBarrier_io_JumpE),
    .io_PCE(IDBarrier_io_PCE),
    .io_PCPlus4E(IDBarrier_io_PCPlus4E),
    .io_PredictTakenD(IDBarrier_io_PredictTakenD),
    .io_PredictTakenE(IDBarrier_io_PredictTakenE)
  );
  EXstage EXstage ( // @[core.scala 90:23]
    .io_RD1E(EXstage_io_RD1E),
    .io_RD2E(EXstage_io_RD2E),
    .io_ImmExtE(EXstage_io_ImmExtE),
    .io_ALUSrcE(EXstage_io_ALUSrcE),
    .io_rdE(EXstage_io_rdE),
    .io_uopE(EXstage_io_uopE),
    .io_WriteEnableE(EXstage_io_WriteEnableE),
    .io_XcptInvalidE(EXstage_io_XcptInvalidE),
    .io_ForwardAE(EXstage_io_ForwardAE),
    .io_ForwardBE(EXstage_io_ForwardBE),
    .io_ResultW(EXstage_io_ResultW),
    .io_ALUResultM(EXstage_io_ALUResultM),
    .io_PCE(EXstage_io_PCE),
    .io_PCPlus4E(EXstage_io_PCPlus4E),
    .io_BranchE(EXstage_io_BranchE),
    .io_JumpE(EXstage_io_JumpE),
    .io_ALUResultE(EXstage_io_ALUResultE),
    .io_exceptionE(EXstage_io_exceptionE),
    .io_rdOutE(EXstage_io_rdOutE),
    .io_WriteEnableOutE(EXstage_io_WriteEnableOutE),
    .io_PCSrcE(EXstage_io_PCSrcE),
    .io_PCTargetE(EXstage_io_PCTargetE),
    .io_FlushE(EXstage_io_FlushE),
    .io_PredictTakenE(EXstage_io_PredictTakenE),
    .io_BTBUpdate(EXstage_io_BTBUpdate),
    .io_BTBUpdatePC(EXstage_io_BTBUpdatePC),
    .io_BTBUpdateTarget(EXstage_io_BTBUpdateTarget),
    .io_BTBMispredicted(EXstage_io_BTBMispredicted)
  );
  EXBarrier EXBarrier ( // @[core.scala 91:25]
    .clock(EXBarrier_clock),
    .reset(EXBarrier_reset),
    .io_ALUResultE(EXBarrier_io_ALUResultE),
    .io_rdE(EXBarrier_io_rdE),
    .io_XcptInvalidE(EXBarrier_io_XcptInvalidE),
    .io_WriteEnableE(EXBarrier_io_WriteEnableE),
    .io_ALUResultM(EXBarrier_io_ALUResultM),
    .io_rdM(EXBarrier_io_rdM),
    .io_XcptInvalidM(EXBarrier_io_XcptInvalidM),
    .io_WriteEnableM(EXBarrier_io_WriteEnableM)
  );
  EXBarrier MEMBarrier ( // @[core.scala 94:26]
    .clock(MEMBarrier_clock),
    .reset(MEMBarrier_reset),
    .io_ALUResultE(MEMBarrier_io_ALUResultE),
    .io_rdE(MEMBarrier_io_rdE),
    .io_XcptInvalidE(MEMBarrier_io_XcptInvalidE),
    .io_WriteEnableE(MEMBarrier_io_WriteEnableE),
    .io_ALUResultM(MEMBarrier_io_ALUResultM),
    .io_rdM(MEMBarrier_io_rdM),
    .io_XcptInvalidM(MEMBarrier_io_XcptInvalidM),
    .io_WriteEnableM(MEMBarrier_io_WriteEnableM)
  );
  WBstage WBstage ( // @[core.scala 96:23]
    .io_ALUResultW(WBstage_io_ALUResultW),
    .io_rdW(WBstage_io_rdW),
    .io_WriteEnableW(WBstage_io_WriteEnableW),
    .io_WriteEnableReq_addr(WBstage_io_WriteEnableReq_addr),
    .io_WriteEnableReq_data(WBstage_io_WriteEnableReq_data),
    .io_WriteEnableReq_w_en(WBstage_io_WriteEnableReq_w_en),
    .io_ResultW(WBstage_io_ResultW)
  );
  WBBarrier WBBarrier ( // @[core.scala 97:25]
    .clock(WBBarrier_clock),
    .reset(WBBarrier_reset),
    .io_ResultW(WBBarrier_io_ResultW),
    .io_XcptInvalidW(WBBarrier_io_XcptInvalidW),
    .io_check_res(WBBarrier_io_check_res),
    .io_exception(WBBarrier_io_exception)
  );
  ForwardingUnit ForwardingUnit ( // @[core.scala 99:30]
    .io_rs1_EX(ForwardingUnit_io_rs1_EX),
    .io_rs2_EX(ForwardingUnit_io_rs2_EX),
    .io_rd_MEM(ForwardingUnit_io_rd_MEM),
    .io_rd_WB(ForwardingUnit_io_rd_WB),
    .io_wrEn_MEM(ForwardingUnit_io_wrEn_MEM),
    .io_wrEn_WB(ForwardingUnit_io_wrEn_WB),
    .io_forwardA(ForwardingUnit_io_forwardA),
    .io_forwardB(ForwardingUnit_io_forwardB)
  );
  BTB BTB ( // @[core.scala 101:19]
    .clock(BTB_clock),
    .reset(BTB_reset),
    .io_PC(BTB_io_PC),
    .io_update(BTB_io_update),
    .io_updatePC(BTB_io_updatePC),
    .io_updateTarget(BTB_io_updateTarget),
    .io_mispredicted(BTB_io_mispredicted),
    .io_target(BTB_io_target),
    .io_predictTaken(BTB_io_predictTaken)
  );
  assign io_check_res = WBBarrier_io_check_res; // @[core.scala 189:16]
  assign io_exception = WBBarrier_io_exception; // @[core.scala 190:16]
  assign io_PCdebug = IFstage_io_PCF; // @[core.scala 193:21]
  assign io_InstrDdebug = IFBarrier_io_InstrD; // @[core.scala 194:21]
  assign io_PCEdebug = IDBarrier_io_PCE; // @[core.scala 195:21]
  assign io_RS1Edebug = IDBarrier_io_RD1E; // @[core.scala 196:21]
  assign io_RS2Edebug = IDBarrier_io_RD2E; // @[core.scala 197:21]
  assign io_BranchEdebug = IDBarrier_io_BranchE; // @[core.scala 198:21]
  assign io_JumpEdebug = IDBarrier_io_JumpE; // @[core.scala 199:21]
  assign io_PCSrcEdebug = EXstage_io_PCSrcE; // @[core.scala 200:21]
  assign io_PCTargetEdebug = EXstage_io_PCTargetE; // @[core.scala 201:21]
  assign io_WriteEnableWdebug = MEMBarrier_io_WriteEnableM; // @[core.scala 202:24]
  assign io_rdWdebug = MEMBarrier_io_rdM; // @[core.scala 203:21]
  assign io_PCSrcE_debug = EXstage_io_PCSrcE; // @[core.scala 205:24]
  assign IFstage_clock = clock;
  assign IFstage_reset = reset;
  assign IFstage_io_PCSrcE = EXstage_io_PCSrcE; // @[core.scala 104:24]
  assign IFstage_io_PCTargetE = EXstage_io_PCTargetE; // @[core.scala 105:24]
  assign IFstage_io_BTBTarget = BTB_io_target; // @[core.scala 214:31]
  assign IFstage_io_BTBPredictTaken = BTB_io_predictTaken; // @[core.scala 215:31]
  assign IFBarrier_clock = clock;
  assign IFBarrier_reset = reset;
  assign IFBarrier_io_InstrF = IFstage_io_InstrF; // @[core.scala 108:25]
  assign IFBarrier_io_PCF = IFstage_io_PCF; // @[core.scala 109:25]
  assign IFBarrier_io_PCPlus4F = IFstage_io_PCPlus4F; // @[core.scala 110:25]
  assign IFBarrier_io_CLR = EXstage_io_FlushE; // @[core.scala 111:25]
  assign IFBarrier_io_PredictTakenF = IFstage_io_PredictTakenF; // @[core.scala 217:34]
  assign IDstage_clock = clock;
  assign IDstage_reset = reset;
  assign IDstage_io_inst = IFBarrier_io_InstrD; // @[core.scala 114:26]
  assign IDstage_io_pcD = IFBarrier_io_PCD; // @[core.scala 115:26]
  assign IDstage_io_pcPlus4D = IFBarrier_io_PCPlus4D; // @[core.scala 116:26]
  assign IDstage_io_WriteEnableW = WBstage_io_WriteEnableReq_w_en; // @[core.scala 117:26]
  assign IDstage_io_rdW = WBstage_io_WriteEnableReq_addr; // @[core.scala 118:26]
  assign IDstage_io_ResultW = WBstage_io_WriteEnableReq_data; // @[core.scala 119:26]
  assign IDstage_io_PredictTakenF = IFBarrier_io_PredictTakenD; // @[core.scala 218:34]
  assign IDBarrier_clock = clock;
  assign IDBarrier_reset = reset;
  assign IDBarrier_io_uopD = IDstage_io_uop; // @[core.scala 122:29]
  assign IDBarrier_io_rdD = IDstage_io_rdD; // @[core.scala 123:29]
  assign IDBarrier_io_RD1D = IDstage_io_RD1D; // @[core.scala 124:29]
  assign IDBarrier_io_RD2D = IDstage_io_RD2D; // @[core.scala 125:29]
  assign IDBarrier_io_XcptInvalidD = IDstage_io_XcptInvalid; // @[core.scala 126:29]
  assign IDBarrier_io_WriteEnableD = IDstage_io_WriteEnableD; // @[core.scala 127:32]
  assign IDBarrier_io_ALUSrcD = IDstage_io_ALUSrcD; // @[core.scala 128:29]
  assign IDBarrier_io_ImmExtD = IDstage_io_ImmExtD; // @[core.scala 129:29]
  assign IDBarrier_io_BranchD = IDstage_io_BranchD; // @[core.scala 130:29]
  assign IDBarrier_io_JumpD = IDstage_io_JumpD; // @[core.scala 131:29]
  assign IDBarrier_io_PCD = IDstage_io_pcD_out; // @[core.scala 132:29]
  assign IDBarrier_io_PCPlus4D = IDstage_io_pcPlus4D_out; // @[core.scala 133:29]
  assign IDBarrier_io_rs1D = IFBarrier_io_InstrD[19:15]; // @[core.scala 134:51]
  assign IDBarrier_io_rs2D = IFBarrier_io_InstrD[24:20]; // @[core.scala 135:51]
  assign IDBarrier_io_CLR = EXstage_io_FlushE; // @[core.scala 136:29]
  assign IDBarrier_io_PredictTakenD = IDstage_io_PredictTakenD; // @[core.scala 219:34]
  assign EXstage_io_RD1E = IDBarrier_io_RD1E; // @[core.scala 139:27]
  assign EXstage_io_RD2E = IDBarrier_io_RD2E; // @[core.scala 140:27]
  assign EXstage_io_ImmExtE = IDBarrier_io_ImmExtE; // @[core.scala 141:27]
  assign EXstage_io_ALUSrcE = IDBarrier_io_ALUSrcE; // @[core.scala 142:27]
  assign EXstage_io_rdE = IDBarrier_io_rdE; // @[core.scala 143:27]
  assign EXstage_io_uopE = IDBarrier_io_uopE; // @[core.scala 144:27]
  assign EXstage_io_WriteEnableE = IDBarrier_io_WriteEnableE; // @[core.scala 145:30]
  assign EXstage_io_XcptInvalidE = IDBarrier_io_XcptInvalidE; // @[core.scala 146:27]
  assign EXstage_io_ForwardAE = ForwardingUnit_io_forwardA; // @[core.scala 183:25]
  assign EXstage_io_ForwardBE = ForwardingUnit_io_forwardB; // @[core.scala 184:25]
  assign EXstage_io_ResultW = WBstage_io_ResultW; // @[core.scala 186:25]
  assign EXstage_io_ALUResultM = EXBarrier_io_ALUResultM; // @[core.scala 185:25]
  assign EXstage_io_PCE = IDBarrier_io_PCE; // @[core.scala 147:27]
  assign EXstage_io_PCPlus4E = IDBarrier_io_PCPlus4E; // @[core.scala 148:27]
  assign EXstage_io_BranchE = IDBarrier_io_BranchE; // @[core.scala 149:27]
  assign EXstage_io_JumpE = IDBarrier_io_JumpE; // @[core.scala 150:27]
  assign EXstage_io_PredictTakenE = IDBarrier_io_PredictTakenE; // @[core.scala 220:34]
  assign EXBarrier_clock = clock;
  assign EXBarrier_reset = reset;
  assign EXBarrier_io_ALUResultE = EXstage_io_ALUResultE; // @[core.scala 153:29]
  assign EXBarrier_io_rdE = EXstage_io_rdOutE; // @[core.scala 154:29]
  assign EXBarrier_io_XcptInvalidE = EXstage_io_exceptionE; // @[core.scala 155:29]
  assign EXBarrier_io_WriteEnableE = EXstage_io_WriteEnableOutE; // @[core.scala 156:32]
  assign MEMBarrier_clock = clock;
  assign MEMBarrier_reset = reset;
  assign MEMBarrier_io_ALUResultE = EXBarrier_io_ALUResultM; // @[core.scala 159:30]
  assign MEMBarrier_io_rdE = EXBarrier_io_rdM; // @[core.scala 160:30]
  assign MEMBarrier_io_XcptInvalidE = EXBarrier_io_XcptInvalidM; // @[core.scala 161:30]
  assign MEMBarrier_io_WriteEnableE = EXBarrier_io_WriteEnableM; // @[core.scala 162:33]
  assign WBstage_io_ALUResultW = MEMBarrier_io_ALUResultM; // @[core.scala 165:25]
  assign WBstage_io_rdW = MEMBarrier_io_rdM; // @[core.scala 166:25]
  assign WBstage_io_WriteEnableW = MEMBarrier_io_WriteEnableM; // @[core.scala 167:28]
  assign WBBarrier_clock = clock;
  assign WBBarrier_reset = reset;
  assign WBBarrier_io_ResultW = WBstage_io_ResultW; // @[core.scala 170:29]
  assign WBBarrier_io_XcptInvalidW = MEMBarrier_io_XcptInvalidM; // @[core.scala 171:29]
  assign ForwardingUnit_io_rs1_EX = {{27'd0}, IDBarrier_io_Rs1E}; // @[core.scala 174:30]
  assign ForwardingUnit_io_rs2_EX = {{27'd0}, IDBarrier_io_Rs2E}; // @[core.scala 175:30]
  assign ForwardingUnit_io_rd_MEM = {{27'd0}, EXBarrier_io_rdM}; // @[core.scala 177:30]
  assign ForwardingUnit_io_rd_WB = {{27'd0}, MEMBarrier_io_rdM}; // @[core.scala 180:30]
  assign ForwardingUnit_io_wrEn_MEM = EXBarrier_io_WriteEnableM; // @[core.scala 178:30]
  assign ForwardingUnit_io_wrEn_WB = MEMBarrier_io_WriteEnableM; // @[core.scala 181:30]
  assign BTB_clock = clock;
  assign BTB_reset = reset;
  assign BTB_io_PC = IFstage_io_PCF; // @[core.scala 208:25]
  assign BTB_io_update = EXstage_io_BTBUpdate; // @[core.scala 209:25]
  assign BTB_io_updatePC = EXstage_io_BTBUpdatePC; // @[core.scala 210:25]
  assign BTB_io_updateTarget = EXstage_io_BTBUpdateTarget; // @[core.scala 211:25]
  assign BTB_io_mispredicted = EXstage_io_BTBMispredicted; // @[core.scala 212:25]
  always @(posedge clock) begin
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (~reset) begin
          $fwrite(32'h80000002," \n\n\nCORE PRINT\n"); // @[core.scala 222:9]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"----------------------------------------------\n"); // @[core.scala 223:9]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002," BTB UPDATE: %x",BTB_io_update); // @[core.scala 224:9]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (_T_1) begin
          $fwrite(32'h80000002,"\n----------------------------------------------\n"); // @[core.scala 225:9]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
  end
endmodule
module PipelinedRV32I(
  input         clock,
  input         reset,
  output [31:0] io_result,
  output        io_exception,
  output [31:0] io_PCdebug,
  output [31:0] io_InstrDdebug,
  output [31:0] io_PCEdebug,
  output [31:0] io_RS1Edebug,
  output [31:0] io_RS2Edebug,
  output        io_BranchEdebug,
  output        io_JumpEdebug,
  output        io_PCSrcEdebug,
  output [31:0] io_PCTargetEdebug,
  output        io_WriteEnableWdebug,
  output [4:0]  io_rdWdebug,
  output        io_PCSrcE_debug
);
  wire  core_clock; // @[PipelinedRISCV32I.scala 38:20]
  wire  core_reset; // @[PipelinedRISCV32I.scala 38:20]
  wire [31:0] core_io_check_res; // @[PipelinedRISCV32I.scala 38:20]
  wire  core_io_exception; // @[PipelinedRISCV32I.scala 38:20]
  wire [31:0] core_io_PCdebug; // @[PipelinedRISCV32I.scala 38:20]
  wire [31:0] core_io_InstrDdebug; // @[PipelinedRISCV32I.scala 38:20]
  wire [31:0] core_io_PCEdebug; // @[PipelinedRISCV32I.scala 38:20]
  wire [31:0] core_io_RS1Edebug; // @[PipelinedRISCV32I.scala 38:20]
  wire [31:0] core_io_RS2Edebug; // @[PipelinedRISCV32I.scala 38:20]
  wire  core_io_BranchEdebug; // @[PipelinedRISCV32I.scala 38:20]
  wire  core_io_JumpEdebug; // @[PipelinedRISCV32I.scala 38:20]
  wire  core_io_PCSrcEdebug; // @[PipelinedRISCV32I.scala 38:20]
  wire [31:0] core_io_PCTargetEdebug; // @[PipelinedRISCV32I.scala 38:20]
  wire  core_io_WriteEnableWdebug; // @[PipelinedRISCV32I.scala 38:20]
  wire [4:0] core_io_rdWdebug; // @[PipelinedRISCV32I.scala 38:20]
  wire  core_io_PCSrcE_debug; // @[PipelinedRISCV32I.scala 38:20]
  PipelinedRV32Icore core ( // @[PipelinedRISCV32I.scala 38:20]
    .clock(core_clock),
    .reset(core_reset),
    .io_check_res(core_io_check_res),
    .io_exception(core_io_exception),
    .io_PCdebug(core_io_PCdebug),
    .io_InstrDdebug(core_io_InstrDdebug),
    .io_PCEdebug(core_io_PCEdebug),
    .io_RS1Edebug(core_io_RS1Edebug),
    .io_RS2Edebug(core_io_RS2Edebug),
    .io_BranchEdebug(core_io_BranchEdebug),
    .io_JumpEdebug(core_io_JumpEdebug),
    .io_PCSrcEdebug(core_io_PCSrcEdebug),
    .io_PCTargetEdebug(core_io_PCTargetEdebug),
    .io_WriteEnableWdebug(core_io_WriteEnableWdebug),
    .io_rdWdebug(core_io_rdWdebug),
    .io_PCSrcE_debug(core_io_PCSrcE_debug)
  );
  assign io_result = core_io_check_res; // @[PipelinedRISCV32I.scala 40:21]
  assign io_exception = core_io_exception; // @[PipelinedRISCV32I.scala 41:21]
  assign io_PCdebug = core_io_PCdebug; // @[PipelinedRISCV32I.scala 42:21]
  assign io_InstrDdebug = core_io_InstrDdebug; // @[PipelinedRISCV32I.scala 43:21]
  assign io_PCEdebug = core_io_PCEdebug; // @[PipelinedRISCV32I.scala 44:21]
  assign io_RS1Edebug = core_io_RS1Edebug; // @[PipelinedRISCV32I.scala 45:21]
  assign io_RS2Edebug = core_io_RS2Edebug; // @[PipelinedRISCV32I.scala 46:21]
  assign io_BranchEdebug = core_io_BranchEdebug; // @[PipelinedRISCV32I.scala 47:21]
  assign io_JumpEdebug = core_io_JumpEdebug; // @[PipelinedRISCV32I.scala 48:21]
  assign io_PCSrcEdebug = core_io_PCSrcEdebug; // @[PipelinedRISCV32I.scala 49:21]
  assign io_PCTargetEdebug = core_io_PCTargetEdebug; // @[PipelinedRISCV32I.scala 50:21]
  assign io_WriteEnableWdebug = core_io_WriteEnableWdebug; // @[PipelinedRISCV32I.scala 51:24]
  assign io_rdWdebug = core_io_rdWdebug; // @[PipelinedRISCV32I.scala 52:21]
  assign io_PCSrcE_debug = core_io_PCSrcE_debug; // @[PipelinedRISCV32I.scala 53:21]
  assign core_clock = clock;
  assign core_reset = reset;
endmodule
