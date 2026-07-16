module IF(
  input         clock,
  input         reset,
  input         io_PCSrcE,
  input  [31:0] io_PCTargetE,
  output [31:0] io_InstrF,
  output [31:0] io_PCF,
  output [31:0] io_PCPlus4F
);
`ifdef RANDOMIZE_MEM_INIT
  reg [31:0] _RAND_0;
`endif // RANDOMIZE_MEM_INIT
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_1;
`endif // RANDOMIZE_REG_INIT
  reg [31:0] IMem [0:4095]; // @[IFstage.scala 54:17]
  wire  IMem_io_InstrF_MPORT_en; // @[IFstage.scala 54:17]
  wire [11:0] IMem_io_InstrF_MPORT_addr; // @[IFstage.scala 54:17]
  wire [31:0] IMem_io_InstrF_MPORT_data; // @[IFstage.scala 54:17]
  reg [31:0] PC; // @[IFstage.scala 57:19]
  wire [29:0] addr = PC[31:2]; // @[IFstage.scala 59:17]
  wire [31:0] PCPlus4 = PC + 32'h4; // @[IFstage.scala 63:20]
  assign IMem_io_InstrF_MPORT_en = 1'h1;
  assign IMem_io_InstrF_MPORT_addr = addr[11:0];
  assign IMem_io_InstrF_MPORT_data = IMem[IMem_io_InstrF_MPORT_addr]; // @[IFstage.scala 54:17]
  assign io_InstrF = IMem_io_InstrF_MPORT_data; // @[IFstage.scala 61:13]
  assign io_PCF = PC; // @[IFstage.scala 68:15]
  assign io_PCPlus4F = PC + 32'h4; // @[IFstage.scala 63:20]
  always @(posedge clock) begin
    if (reset) begin // @[IFstage.scala 57:19]
      PC <= 32'h0; // @[IFstage.scala 57:19]
    end else if (io_PCSrcE) begin // @[IFstage.scala 64:20]
      PC <= io_PCTargetE;
    end else begin
      PC <= PCPlus4;
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
  output [31:0] io_PCPlus4D
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
`endif // RANDOMIZE_REG_INIT
  reg [31:0] instrReg; // @[IFbarrier.scala 44:27]
  reg [31:0] pcReg; // @[IFbarrier.scala 45:27]
  reg [31:0] pcPlus4Reg; // @[IFbarrier.scala 46:27]
  assign io_InstrD = instrReg; // @[IFbarrier.scala 58:15]
  assign io_PCD = pcReg; // @[IFbarrier.scala 59:15]
  assign io_PCPlus4D = pcPlus4Reg; // @[IFbarrier.scala 60:15]
  always @(posedge clock) begin
    if (reset) begin // @[IFbarrier.scala 44:27]
      instrReg <= 32'h0; // @[IFbarrier.scala 44:27]
    end else if (io_CLR) begin // @[IFbarrier.scala 48:16]
      instrReg <= 32'h13; // @[IFbarrier.scala 49:16]
    end else begin
      instrReg <= io_InstrF; // @[IFbarrier.scala 53:16]
    end
    if (reset) begin // @[IFbarrier.scala 45:27]
      pcReg <= 32'h0; // @[IFbarrier.scala 45:27]
    end else if (io_CLR) begin // @[IFbarrier.scala 48:16]
      pcReg <= 32'h0; // @[IFbarrier.scala 50:16]
    end else begin
      pcReg <= io_PCF; // @[IFbarrier.scala 54:16]
    end
    if (reset) begin // @[IFbarrier.scala 46:27]
      pcPlus4Reg <= 32'h0; // @[IFbarrier.scala 46:27]
    end else if (io_CLR) begin // @[IFbarrier.scala 48:16]
      pcPlus4Reg <= 32'h0; // @[IFbarrier.scala 51:16]
    end else begin
      pcPlus4Reg <= io_PCPlus4F; // @[IFbarrier.scala 55:16]
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
  output       io_RegWriteD,
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
  wire [4:0] _GEN_70 = 7'h67 == io_opcode ? 5'h1b : 5'h1c; // @[IDstage.scala 77:22 148:28 69:20]
  wire  _GEN_71 = 7'h67 == io_opcode ? 1'h0 : 1'h1; // @[IDstage.scala 77:22 149:28 75:20]
  wire [1:0] _GEN_72 = 7'h6f == io_opcode ? 2'h2 : 2'h0; // @[IDstage.scala 77:22 136:28 71:20]
  wire  _GEN_73 = 7'h6f == io_opcode | 7'h67 == io_opcode; // @[IDstage.scala 77:22 137:28]
  wire [4:0] _GEN_74 = 7'h6f == io_opcode ? 5'h1a : _GEN_70; // @[IDstage.scala 77:22 139:28]
  wire  _GEN_75 = 7'h6f == io_opcode ? 1'h0 : _GEN_71; // @[IDstage.scala 77:22 140:28]
  wire  _GEN_76 = 7'h6f == io_opcode ? 1'h0 : 7'h67 == io_opcode; // @[IDstage.scala 70:20 77:22]
  wire [1:0] _GEN_77 = 7'h63 == io_opcode ? 2'h3 : _GEN_72; // @[IDstage.scala 77:22 123:24]
  wire  _GEN_78 = 7'h63 == io_opcode | _GEN_76; // @[IDstage.scala 77:22 124:24]
  wire [4:0] _GEN_79 = 7'h63 == io_opcode ? _GEN_67 : _GEN_74; // @[IDstage.scala 77:22]
  wire  _GEN_80 = 7'h63 == io_opcode ? _GEN_68 : _GEN_75; // @[IDstage.scala 77:22]
  wire  _GEN_81 = 7'h63 == io_opcode ? 1'h0 : _GEN_73; // @[IDstage.scala 73:20 77:22]
  wire  _GEN_82 = 7'h63 == io_opcode ? 1'h0 : _GEN_76; // @[IDstage.scala 70:20 77:22]
  wire  _GEN_83 = 7'h13 == io_opcode | _GEN_82; // @[IDstage.scala 77:22 99:26]
  wire  _GEN_84 = 7'h13 == io_opcode | _GEN_81; // @[IDstage.scala 77:22 100:26]
  wire [4:0] _GEN_85 = 7'h13 == io_opcode ? _GEN_54 : _GEN_79; // @[IDstage.scala 77:22]
  wire  _GEN_86 = 7'h13 == io_opcode ? _GEN_55 : _GEN_80; // @[IDstage.scala 77:22]
  wire [1:0] _GEN_87 = 7'h13 == io_opcode ? {{1'd0}, _GEN_56} : _GEN_77; // @[IDstage.scala 77:22]
  wire  _GEN_88 = 7'h13 == io_opcode ? 1'h0 : _GEN_78; // @[IDstage.scala 72:20 77:22]
  wire  _GEN_89 = 7'h13 == io_opcode ? 1'h0 : _GEN_81; // @[IDstage.scala 73:20 77:22]
  assign io_uop = 7'h33 == io_opcode ? _GEN_22 : _GEN_85; // @[IDstage.scala 77:22]
  assign io_ALUSrcD = 7'h33 == io_opcode ? 1'h0 : _GEN_83; // @[IDstage.scala 70:20 77:22]
  assign io_immSel = 7'h33 == io_opcode ? 2'h0 : _GEN_87; // @[IDstage.scala 71:20 77:22]
  assign io_BranchD = 7'h33 == io_opcode ? 1'h0 : _GEN_88; // @[IDstage.scala 72:20 77:22]
  assign io_JumpD = 7'h33 == io_opcode ? 1'h0 : _GEN_89; // @[IDstage.scala 73:20 77:22]
  assign io_RegWriteD = 7'h33 == io_opcode | _GEN_84; // @[IDstage.scala 77:22 79:26]
  assign io_XcptInvalid = 7'h33 == io_opcode ? _GEN_23 : _GEN_86; // @[IDstage.scala 77:22]
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
  wire [31:0] _GEN_0 = 2'h3 == io_sel ? branch_imm : full; // @[IDstage.scala 167:16 169:19 173:28]
  wire [31:0] _GEN_1 = 2'h2 == io_sel ? jump_imm : _GEN_0; // @[IDstage.scala 169:19 172:28]
  wire [31:0] _GEN_2 = 2'h1 == io_sel ? shamt : _GEN_1; // @[IDstage.scala 169:19 171:28]
  assign io_imm_out = 2'h0 == io_sel ? full : _GEN_2; // @[IDstage.scala 169:19 170:28]
endmodule
module ID(
  input         clock,
  input         reset,
  input  [31:0] io_inst,
  input  [31:0] io_pcD,
  input  [31:0] io_pcPlus4D,
  input         io_RegWriteW,
  input  [4:0]  io_rdW,
  input  [31:0] io_ResultW,
  output [4:0]  io_uop,
  output        io_RegWriteD,
  output        io_ALUSrcD,
  output [31:0] io_ImmExtD,
  output        io_BranchD,
  output        io_JumpD,
  output        io_XcptInvalid,
  output [4:0]  io_rdD,
  output [31:0] io_RD1D,
  output [31:0] io_RD2D,
  output [31:0] io_pcD_out,
  output [31:0] io_pcPlus4D_out
);
  wire  rf_clock; // @[IDstage.scala 208:22]
  wire  rf_reset; // @[IDstage.scala 208:22]
  wire [4:0] rf_io_req_1_addr; // @[IDstage.scala 208:22]
  wire [4:0] rf_io_req_2_addr; // @[IDstage.scala 208:22]
  wire [4:0] rf_io_req_3_addr; // @[IDstage.scala 208:22]
  wire [31:0] rf_io_req_3_data; // @[IDstage.scala 208:22]
  wire  rf_io_req_3_w_en; // @[IDstage.scala 208:22]
  wire [31:0] rf_io_resp_1_data; // @[IDstage.scala 208:22]
  wire [31:0] rf_io_resp_2_data; // @[IDstage.scala 208:22]
  wire [6:0] cu_io_opcode; // @[IDstage.scala 209:22]
  wire [2:0] cu_io_funct3; // @[IDstage.scala 209:22]
  wire [6:0] cu_io_funct7; // @[IDstage.scala 209:22]
  wire [4:0] cu_io_uop; // @[IDstage.scala 209:22]
  wire  cu_io_ALUSrcD; // @[IDstage.scala 209:22]
  wire [1:0] cu_io_immSel; // @[IDstage.scala 209:22]
  wire  cu_io_BranchD; // @[IDstage.scala 209:22]
  wire  cu_io_JumpD; // @[IDstage.scala 209:22]
  wire  cu_io_RegWriteD; // @[IDstage.scala 209:22]
  wire  cu_io_XcptInvalid; // @[IDstage.scala 209:22]
  wire [24:0] sigex_io_imm_in; // @[IDstage.scala 210:23]
  wire [1:0] sigex_io_sel; // @[IDstage.scala 210:23]
  wire [31:0] sigex_io_imm_out; // @[IDstage.scala 210:23]
  regFile rf ( // @[IDstage.scala 208:22]
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
  ControlUnit cu ( // @[IDstage.scala 209:22]
    .io_opcode(cu_io_opcode),
    .io_funct3(cu_io_funct3),
    .io_funct7(cu_io_funct7),
    .io_uop(cu_io_uop),
    .io_ALUSrcD(cu_io_ALUSrcD),
    .io_immSel(cu_io_immSel),
    .io_BranchD(cu_io_BranchD),
    .io_JumpD(cu_io_JumpD),
    .io_RegWriteD(cu_io_RegWriteD),
    .io_XcptInvalid(cu_io_XcptInvalid)
  );
  SignExtend sigex ( // @[IDstage.scala 210:23]
    .io_imm_in(sigex_io_imm_in),
    .io_sel(sigex_io_sel),
    .io_imm_out(sigex_io_imm_out)
  );
  assign io_uop = cu_io_uop; // @[IDstage.scala 228:20]
  assign io_RegWriteD = cu_io_RegWriteD; // @[IDstage.scala 231:20]
  assign io_ALUSrcD = cu_io_ALUSrcD; // @[IDstage.scala 232:20]
  assign io_ImmExtD = sigex_io_imm_out; // @[IDstage.scala 230:20]
  assign io_BranchD = cu_io_BranchD; // @[IDstage.scala 233:20]
  assign io_JumpD = cu_io_JumpD; // @[IDstage.scala 234:20]
  assign io_XcptInvalid = cu_io_XcptInvalid; // @[IDstage.scala 229:20]
  assign io_rdD = io_inst[11:7]; // @[IDstage.scala 206:25]
  assign io_RD1D = rf_io_resp_1_data; // @[IDstage.scala 225:20]
  assign io_RD2D = rf_io_resp_2_data; // @[IDstage.scala 226:20]
  assign io_pcD_out = io_pcD; // @[IDstage.scala 235:21]
  assign io_pcPlus4D_out = io_pcPlus4D; // @[IDstage.scala 236:21]
  assign rf_clock = clock;
  assign rf_reset = reset;
  assign rf_io_req_1_addr = io_inst[19:15]; // @[IDstage.scala 204:25]
  assign rf_io_req_2_addr = io_inst[24:20]; // @[IDstage.scala 205:25]
  assign rf_io_req_3_addr = io_rdW; // @[IDstage.scala 221:22]
  assign rf_io_req_3_data = io_ResultW; // @[IDstage.scala 223:22]
  assign rf_io_req_3_w_en = io_RegWriteW; // @[IDstage.scala 222:22]
  assign cu_io_opcode = io_inst[6:0]; // @[IDstage.scala 201:25]
  assign cu_io_funct3 = io_inst[14:12]; // @[IDstage.scala 202:25]
  assign cu_io_funct7 = io_inst[31:25]; // @[IDstage.scala 203:25]
  assign sigex_io_imm_in = io_inst[31:7]; // @[IDstage.scala 216:31]
  assign sigex_io_sel = cu_io_immSel; // @[IDstage.scala 217:21]
endmodule
module IDBarrier(
  input         clock,
  input         reset,
  input  [4:0]  io_uopD,
  input  [4:0]  io_rdD,
  input  [31:0] io_RD1D,
  input  [31:0] io_RD2D,
  input         io_XcptInvalidD,
  input         io_RegWriteD,
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
  output        io_RegWriteE,
  output        io_ALUSrcE,
  output [31:0] io_ImmExtE,
  output        io_BranchE,
  output        io_JumpE,
  output [31:0] io_PCE,
  output [31:0] io_PCPlus4E
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
`endif // RANDOMIZE_REG_INIT
  reg [4:0] uop; // @[IDbarrier.scala 78:31]
  reg  XcptInvalid; // @[IDbarrier.scala 79:31]
  reg [4:0] rd; // @[IDbarrier.scala 80:31]
  reg [31:0] RD1; // @[IDbarrier.scala 81:31]
  reg [31:0] RD2; // @[IDbarrier.scala 82:31]
  reg  RegWrite; // @[IDbarrier.scala 83:31]
  reg  ALUSrc; // @[IDbarrier.scala 84:31]
  reg [31:0] ImmExt; // @[IDbarrier.scala 85:31]
  reg  Branch; // @[IDbarrier.scala 86:31]
  reg  Jump; // @[IDbarrier.scala 87:31]
  reg [31:0] PC; // @[IDbarrier.scala 88:31]
  reg [31:0] PCPlus4; // @[IDbarrier.scala 89:31]
  reg [4:0] rs1Addr; // @[IDbarrier.scala 90:31]
  reg [4:0] rs2Addr; // @[IDbarrier.scala 91:31]
  assign io_Rs1E = rs1Addr; // @[IDbarrier.scala 137:21]
  assign io_Rs2E = rs2Addr; // @[IDbarrier.scala 138:21]
  assign io_uopE = uop; // @[IDbarrier.scala 125:21]
  assign io_rdE = rd; // @[IDbarrier.scala 127:21]
  assign io_RD1E = RD1; // @[IDbarrier.scala 128:21]
  assign io_RD2E = RD2; // @[IDbarrier.scala 129:21]
  assign io_XcptInvalidE = XcptInvalid; // @[IDbarrier.scala 126:21]
  assign io_RegWriteE = RegWrite; // @[IDbarrier.scala 130:21]
  assign io_ALUSrcE = ALUSrc; // @[IDbarrier.scala 131:21]
  assign io_ImmExtE = ImmExt; // @[IDbarrier.scala 132:21]
  assign io_BranchE = Branch; // @[IDbarrier.scala 133:21]
  assign io_JumpE = Jump; // @[IDbarrier.scala 134:21]
  assign io_PCE = PC; // @[IDbarrier.scala 135:21]
  assign io_PCPlus4E = PCPlus4; // @[IDbarrier.scala 136:21]
  always @(posedge clock) begin
    if (reset) begin // @[IDbarrier.scala 78:31]
      uop <= 5'h1c; // @[IDbarrier.scala 78:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 93:18]
      uop <= 5'h13; // @[IDbarrier.scala 94:21]
    end else begin
      uop <= io_uopD; // @[IDbarrier.scala 109:21]
    end
    if (reset) begin // @[IDbarrier.scala 79:31]
      XcptInvalid <= 1'h0; // @[IDbarrier.scala 79:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 93:18]
      XcptInvalid <= 1'h0; // @[IDbarrier.scala 95:21]
    end else begin
      XcptInvalid <= io_XcptInvalidD; // @[IDbarrier.scala 113:21]
    end
    if (reset) begin // @[IDbarrier.scala 80:31]
      rd <= 5'h0; // @[IDbarrier.scala 80:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 93:18]
      rd <= 5'h0; // @[IDbarrier.scala 96:21]
    end else begin
      rd <= io_rdD; // @[IDbarrier.scala 110:21]
    end
    if (reset) begin // @[IDbarrier.scala 81:31]
      RD1 <= 32'h0; // @[IDbarrier.scala 81:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 93:18]
      RD1 <= 32'h0; // @[IDbarrier.scala 97:21]
    end else begin
      RD1 <= io_RD1D; // @[IDbarrier.scala 111:21]
    end
    if (reset) begin // @[IDbarrier.scala 82:31]
      RD2 <= 32'h0; // @[IDbarrier.scala 82:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 93:18]
      RD2 <= 32'h0; // @[IDbarrier.scala 98:21]
    end else begin
      RD2 <= io_RD2D; // @[IDbarrier.scala 112:21]
    end
    if (reset) begin // @[IDbarrier.scala 83:31]
      RegWrite <= 1'h0; // @[IDbarrier.scala 83:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 93:18]
      RegWrite <= 1'h0; // @[IDbarrier.scala 99:21]
    end else begin
      RegWrite <= io_RegWriteD; // @[IDbarrier.scala 114:21]
    end
    if (reset) begin // @[IDbarrier.scala 84:31]
      ALUSrc <= 1'h0; // @[IDbarrier.scala 84:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 93:18]
      ALUSrc <= 1'h0; // @[IDbarrier.scala 100:21]
    end else begin
      ALUSrc <= io_ALUSrcD; // @[IDbarrier.scala 115:21]
    end
    if (reset) begin // @[IDbarrier.scala 85:31]
      ImmExt <= 32'h0; // @[IDbarrier.scala 85:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 93:18]
      ImmExt <= 32'h0; // @[IDbarrier.scala 101:21]
    end else begin
      ImmExt <= io_ImmExtD; // @[IDbarrier.scala 116:21]
    end
    if (reset) begin // @[IDbarrier.scala 86:31]
      Branch <= 1'h0; // @[IDbarrier.scala 86:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 93:18]
      Branch <= 1'h0; // @[IDbarrier.scala 102:21]
    end else begin
      Branch <= io_BranchD; // @[IDbarrier.scala 117:21]
    end
    if (reset) begin // @[IDbarrier.scala 87:31]
      Jump <= 1'h0; // @[IDbarrier.scala 87:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 93:18]
      Jump <= 1'h0; // @[IDbarrier.scala 103:21]
    end else begin
      Jump <= io_JumpD; // @[IDbarrier.scala 118:21]
    end
    if (reset) begin // @[IDbarrier.scala 88:31]
      PC <= 32'h0; // @[IDbarrier.scala 88:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 93:18]
      PC <= 32'h0; // @[IDbarrier.scala 104:21]
    end else begin
      PC <= io_PCD; // @[IDbarrier.scala 119:21]
    end
    if (reset) begin // @[IDbarrier.scala 89:31]
      PCPlus4 <= 32'h0; // @[IDbarrier.scala 89:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 93:18]
      PCPlus4 <= 32'h0; // @[IDbarrier.scala 105:21]
    end else begin
      PCPlus4 <= io_PCPlus4D; // @[IDbarrier.scala 120:21]
    end
    if (reset) begin // @[IDbarrier.scala 90:31]
      rs1Addr <= 5'h0; // @[IDbarrier.scala 90:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 93:18]
      rs1Addr <= 5'h0; // @[IDbarrier.scala 106:21]
    end else begin
      rs1Addr <= io_rs1D; // @[IDbarrier.scala 121:21]
    end
    if (reset) begin // @[IDbarrier.scala 91:31]
      rs2Addr <= 5'h0; // @[IDbarrier.scala 91:31]
    end else if (io_CLR) begin // @[IDbarrier.scala 93:18]
      rs2Addr <= 5'h0; // @[IDbarrier.scala 107:21]
    end else begin
      rs2Addr <= io_rs2D; // @[IDbarrier.scala 122:21]
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
  RegWrite = _RAND_5[0:0];
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
  input         io_RegWriteE,
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
  output        io_RegWriteOutE,
  output        io_PCSrcE,
  output [31:0] io_PCTargetE
);
  wire [31:0] ALU_io_operandA; // @[EXstage.scala 96:26]
  wire [31:0] ALU_io_operandB; // @[EXstage.scala 96:26]
  wire [3:0] ALU_io_operation; // @[EXstage.scala 96:26]
  wire [31:0] ALU_io_aluResult; // @[EXstage.scala 96:26]
  wire  ALU_io_zero; // @[EXstage.scala 96:26]
  wire [4:0] ALUcontrol_io_uop; // @[EXstage.scala 97:26]
  wire [3:0] ALUcontrol_io_mapped; // @[EXstage.scala 97:26]
  wire [31:0] _GEN_0 = 2'h1 == io_ForwardAE ? io_ResultW : io_RD1E; // @[EXstage.scala 103:24 105:24 102:8]
  wire [31:0] _GEN_2 = 2'h1 == io_ForwardBE ? io_ResultW : io_RD2E; // @[EXstage.scala 108:24 110:24 107:8]
  wire [31:0] srcB = 2'h2 == io_ForwardBE ? io_ALUResultM : _GEN_2; // @[EXstage.scala 108:24 109:24]
  wire  jalr = io_JumpE & io_BranchE; // @[EXstage.scala 117:23]
  wire [31:0] pcTargetAdder = io_PCE + io_ImmExtE; // @[EXstage.scala 118:30]
  wire [31:0] _io_PCTargetE_T_1 = ALU_io_aluResult & 32'hfffffffe; // @[EXstage.scala 121:47]
  ALU ALU ( // @[EXstage.scala 96:26]
    .io_operandA(ALU_io_operandA),
    .io_operandB(ALU_io_operandB),
    .io_operation(ALU_io_operation),
    .io_aluResult(ALU_io_aluResult),
    .io_zero(ALU_io_zero)
  );
  ALUcontrol ALUcontrol ( // @[EXstage.scala 97:26]
    .io_uop(ALUcontrol_io_uop),
    .io_mapped(ALUcontrol_io_mapped)
  );
  assign io_ALUResultE = io_JumpE ? io_PCPlus4E : ALU_io_aluResult; // @[EXstage.scala 123:25]
  assign io_exceptionE = io_XcptInvalidE; // @[EXstage.scala 124:19]
  assign io_rdOutE = io_rdE; // @[EXstage.scala 125:19]
  assign io_RegWriteOutE = io_RegWriteE; // @[EXstage.scala 126:19]
  assign io_PCSrcE = io_BranchE & ALU_io_zero | io_JumpE; // @[EXstage.scala 120:47]
  assign io_PCTargetE = jalr ? _io_PCTargetE_T_1 : pcTargetAdder; // @[EXstage.scala 121:22]
  assign ALU_io_operandA = 2'h2 == io_ForwardAE ? io_ALUResultM : _GEN_0; // @[EXstage.scala 103:24 104:24]
  assign ALU_io_operandB = io_ALUSrcE ? io_ImmExtE : srcB; // @[EXstage.scala 114:26]
  assign ALU_io_operation = ALUcontrol_io_mapped; // @[EXstage.scala 115:20]
  assign ALUcontrol_io_uop = io_uopE; // @[EXstage.scala 98:21]
endmodule
module EXBarrier(
  input         clock,
  input         reset,
  input  [31:0] io_ALUResultE,
  input  [4:0]  io_rdE,
  input         io_XcptInvalidE,
  input         io_RegWriteE,
  output [31:0] io_ALUResultM,
  output [4:0]  io_rdM,
  output        io_XcptInvalidM,
  output        io_RegWriteM
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
  reg  RegWrite; // @[EXbarrier.scala 53:28]
  assign io_ALUResultM = ALUResult; // @[EXbarrier.scala 60:19]
  assign io_rdM = rd; // @[EXbarrier.scala 61:19]
  assign io_XcptInvalidM = XcptInvalid; // @[EXbarrier.scala 62:19]
  assign io_RegWriteM = RegWrite; // @[EXbarrier.scala 63:19]
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
      RegWrite <= 1'h0; // @[EXbarrier.scala 53:28]
    end else begin
      RegWrite <= io_RegWriteE; // @[EXbarrier.scala 58:15]
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
  RegWrite = _RAND_3[0:0];
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
  input         io_RegWriteW,
  output [4:0]  io_RegWriteReq_addr,
  output [31:0] io_RegWriteReq_data,
  output        io_RegWriteReq_w_en,
  output [31:0] io_ResultW
);
  assign io_RegWriteReq_addr = io_rdW; // @[WBstage.scala 58:23]
  assign io_RegWriteReq_data = io_ALUResultW; // @[WBstage.scala 59:23]
  assign io_RegWriteReq_w_en = io_RegWriteW; // @[WBstage.scala 60:23]
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
  output        io_RegWriteWdebug,
  output [4:0]  io_rdWdebug,
  output        io_PCSrcE_debug
);
  wire  IFstage_clock; // @[core.scala 84:25]
  wire  IFstage_reset; // @[core.scala 84:25]
  wire  IFstage_io_PCSrcE; // @[core.scala 84:25]
  wire [31:0] IFstage_io_PCTargetE; // @[core.scala 84:25]
  wire [31:0] IFstage_io_InstrF; // @[core.scala 84:25]
  wire [31:0] IFstage_io_PCF; // @[core.scala 84:25]
  wire [31:0] IFstage_io_PCPlus4F; // @[core.scala 84:25]
  wire  IFBarrier_clock; // @[core.scala 85:25]
  wire  IFBarrier_reset; // @[core.scala 85:25]
  wire [31:0] IFBarrier_io_InstrF; // @[core.scala 85:25]
  wire [31:0] IFBarrier_io_PCF; // @[core.scala 85:25]
  wire [31:0] IFBarrier_io_PCPlus4F; // @[core.scala 85:25]
  wire  IFBarrier_io_CLR; // @[core.scala 85:25]
  wire [31:0] IFBarrier_io_InstrD; // @[core.scala 85:25]
  wire [31:0] IFBarrier_io_PCD; // @[core.scala 85:25]
  wire [31:0] IFBarrier_io_PCPlus4D; // @[core.scala 85:25]
  wire  IDstage_clock; // @[core.scala 87:25]
  wire  IDstage_reset; // @[core.scala 87:25]
  wire [31:0] IDstage_io_inst; // @[core.scala 87:25]
  wire [31:0] IDstage_io_pcD; // @[core.scala 87:25]
  wire [31:0] IDstage_io_pcPlus4D; // @[core.scala 87:25]
  wire  IDstage_io_RegWriteW; // @[core.scala 87:25]
  wire [4:0] IDstage_io_rdW; // @[core.scala 87:25]
  wire [31:0] IDstage_io_ResultW; // @[core.scala 87:25]
  wire [4:0] IDstage_io_uop; // @[core.scala 87:25]
  wire  IDstage_io_RegWriteD; // @[core.scala 87:25]
  wire  IDstage_io_ALUSrcD; // @[core.scala 87:25]
  wire [31:0] IDstage_io_ImmExtD; // @[core.scala 87:25]
  wire  IDstage_io_BranchD; // @[core.scala 87:25]
  wire  IDstage_io_JumpD; // @[core.scala 87:25]
  wire  IDstage_io_XcptInvalid; // @[core.scala 87:25]
  wire [4:0] IDstage_io_rdD; // @[core.scala 87:25]
  wire [31:0] IDstage_io_RD1D; // @[core.scala 87:25]
  wire [31:0] IDstage_io_RD2D; // @[core.scala 87:25]
  wire [31:0] IDstage_io_pcD_out; // @[core.scala 87:25]
  wire [31:0] IDstage_io_pcPlus4D_out; // @[core.scala 87:25]
  wire  IDBarrier_clock; // @[core.scala 88:25]
  wire  IDBarrier_reset; // @[core.scala 88:25]
  wire [4:0] IDBarrier_io_uopD; // @[core.scala 88:25]
  wire [4:0] IDBarrier_io_rdD; // @[core.scala 88:25]
  wire [31:0] IDBarrier_io_RD1D; // @[core.scala 88:25]
  wire [31:0] IDBarrier_io_RD2D; // @[core.scala 88:25]
  wire  IDBarrier_io_XcptInvalidD; // @[core.scala 88:25]
  wire  IDBarrier_io_RegWriteD; // @[core.scala 88:25]
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
  wire  IDBarrier_io_RegWriteE; // @[core.scala 88:25]
  wire  IDBarrier_io_ALUSrcE; // @[core.scala 88:25]
  wire [31:0] IDBarrier_io_ImmExtE; // @[core.scala 88:25]
  wire  IDBarrier_io_BranchE; // @[core.scala 88:25]
  wire  IDBarrier_io_JumpE; // @[core.scala 88:25]
  wire [31:0] IDBarrier_io_PCE; // @[core.scala 88:25]
  wire [31:0] IDBarrier_io_PCPlus4E; // @[core.scala 88:25]
  wire [31:0] EXstage_io_RD1E; // @[core.scala 90:25]
  wire [31:0] EXstage_io_RD2E; // @[core.scala 90:25]
  wire [31:0] EXstage_io_ImmExtE; // @[core.scala 90:25]
  wire  EXstage_io_ALUSrcE; // @[core.scala 90:25]
  wire [4:0] EXstage_io_rdE; // @[core.scala 90:25]
  wire [4:0] EXstage_io_uopE; // @[core.scala 90:25]
  wire  EXstage_io_RegWriteE; // @[core.scala 90:25]
  wire  EXstage_io_XcptInvalidE; // @[core.scala 90:25]
  wire [1:0] EXstage_io_ForwardAE; // @[core.scala 90:25]
  wire [1:0] EXstage_io_ForwardBE; // @[core.scala 90:25]
  wire [31:0] EXstage_io_ResultW; // @[core.scala 90:25]
  wire [31:0] EXstage_io_ALUResultM; // @[core.scala 90:25]
  wire [31:0] EXstage_io_PCE; // @[core.scala 90:25]
  wire [31:0] EXstage_io_PCPlus4E; // @[core.scala 90:25]
  wire  EXstage_io_BranchE; // @[core.scala 90:25]
  wire  EXstage_io_JumpE; // @[core.scala 90:25]
  wire [31:0] EXstage_io_ALUResultE; // @[core.scala 90:25]
  wire  EXstage_io_exceptionE; // @[core.scala 90:25]
  wire [4:0] EXstage_io_rdOutE; // @[core.scala 90:25]
  wire  EXstage_io_RegWriteOutE; // @[core.scala 90:25]
  wire  EXstage_io_PCSrcE; // @[core.scala 90:25]
  wire [31:0] EXstage_io_PCTargetE; // @[core.scala 90:25]
  wire  EXBarrier_clock; // @[core.scala 91:25]
  wire  EXBarrier_reset; // @[core.scala 91:25]
  wire [31:0] EXBarrier_io_ALUResultE; // @[core.scala 91:25]
  wire [4:0] EXBarrier_io_rdE; // @[core.scala 91:25]
  wire  EXBarrier_io_XcptInvalidE; // @[core.scala 91:25]
  wire  EXBarrier_io_RegWriteE; // @[core.scala 91:25]
  wire [31:0] EXBarrier_io_ALUResultM; // @[core.scala 91:25]
  wire [4:0] EXBarrier_io_rdM; // @[core.scala 91:25]
  wire  EXBarrier_io_XcptInvalidM; // @[core.scala 91:25]
  wire  EXBarrier_io_RegWriteM; // @[core.scala 91:25]
  wire  MEMBarrier_clock; // @[core.scala 94:26]
  wire  MEMBarrier_reset; // @[core.scala 94:26]
  wire [31:0] MEMBarrier_io_ALUResultE; // @[core.scala 94:26]
  wire [4:0] MEMBarrier_io_rdE; // @[core.scala 94:26]
  wire  MEMBarrier_io_XcptInvalidE; // @[core.scala 94:26]
  wire  MEMBarrier_io_RegWriteE; // @[core.scala 94:26]
  wire [31:0] MEMBarrier_io_ALUResultM; // @[core.scala 94:26]
  wire [4:0] MEMBarrier_io_rdM; // @[core.scala 94:26]
  wire  MEMBarrier_io_XcptInvalidM; // @[core.scala 94:26]
  wire  MEMBarrier_io_RegWriteM; // @[core.scala 94:26]
  wire [31:0] WBstage_io_ALUResultW; // @[core.scala 96:25]
  wire [4:0] WBstage_io_rdW; // @[core.scala 96:25]
  wire  WBstage_io_RegWriteW; // @[core.scala 96:25]
  wire [4:0] WBstage_io_RegWriteReq_addr; // @[core.scala 96:25]
  wire [31:0] WBstage_io_RegWriteReq_data; // @[core.scala 96:25]
  wire  WBstage_io_RegWriteReq_w_en; // @[core.scala 96:25]
  wire [31:0] WBstage_io_ResultW; // @[core.scala 96:25]
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
  IF IFstage ( // @[core.scala 84:25]
    .clock(IFstage_clock),
    .reset(IFstage_reset),
    .io_PCSrcE(IFstage_io_PCSrcE),
    .io_PCTargetE(IFstage_io_PCTargetE),
    .io_InstrF(IFstage_io_InstrF),
    .io_PCF(IFstage_io_PCF),
    .io_PCPlus4F(IFstage_io_PCPlus4F)
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
    .io_PCPlus4D(IFBarrier_io_PCPlus4D)
  );
  ID IDstage ( // @[core.scala 87:25]
    .clock(IDstage_clock),
    .reset(IDstage_reset),
    .io_inst(IDstage_io_inst),
    .io_pcD(IDstage_io_pcD),
    .io_pcPlus4D(IDstage_io_pcPlus4D),
    .io_RegWriteW(IDstage_io_RegWriteW),
    .io_rdW(IDstage_io_rdW),
    .io_ResultW(IDstage_io_ResultW),
    .io_uop(IDstage_io_uop),
    .io_RegWriteD(IDstage_io_RegWriteD),
    .io_ALUSrcD(IDstage_io_ALUSrcD),
    .io_ImmExtD(IDstage_io_ImmExtD),
    .io_BranchD(IDstage_io_BranchD),
    .io_JumpD(IDstage_io_JumpD),
    .io_XcptInvalid(IDstage_io_XcptInvalid),
    .io_rdD(IDstage_io_rdD),
    .io_RD1D(IDstage_io_RD1D),
    .io_RD2D(IDstage_io_RD2D),
    .io_pcD_out(IDstage_io_pcD_out),
    .io_pcPlus4D_out(IDstage_io_pcPlus4D_out)
  );
  IDBarrier IDBarrier ( // @[core.scala 88:25]
    .clock(IDBarrier_clock),
    .reset(IDBarrier_reset),
    .io_uopD(IDBarrier_io_uopD),
    .io_rdD(IDBarrier_io_rdD),
    .io_RD1D(IDBarrier_io_RD1D),
    .io_RD2D(IDBarrier_io_RD2D),
    .io_XcptInvalidD(IDBarrier_io_XcptInvalidD),
    .io_RegWriteD(IDBarrier_io_RegWriteD),
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
    .io_RegWriteE(IDBarrier_io_RegWriteE),
    .io_ALUSrcE(IDBarrier_io_ALUSrcE),
    .io_ImmExtE(IDBarrier_io_ImmExtE),
    .io_BranchE(IDBarrier_io_BranchE),
    .io_JumpE(IDBarrier_io_JumpE),
    .io_PCE(IDBarrier_io_PCE),
    .io_PCPlus4E(IDBarrier_io_PCPlus4E)
  );
  EXstage EXstage ( // @[core.scala 90:25]
    .io_RD1E(EXstage_io_RD1E),
    .io_RD2E(EXstage_io_RD2E),
    .io_ImmExtE(EXstage_io_ImmExtE),
    .io_ALUSrcE(EXstage_io_ALUSrcE),
    .io_rdE(EXstage_io_rdE),
    .io_uopE(EXstage_io_uopE),
    .io_RegWriteE(EXstage_io_RegWriteE),
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
    .io_RegWriteOutE(EXstage_io_RegWriteOutE),
    .io_PCSrcE(EXstage_io_PCSrcE),
    .io_PCTargetE(EXstage_io_PCTargetE)
  );
  EXBarrier EXBarrier ( // @[core.scala 91:25]
    .clock(EXBarrier_clock),
    .reset(EXBarrier_reset),
    .io_ALUResultE(EXBarrier_io_ALUResultE),
    .io_rdE(EXBarrier_io_rdE),
    .io_XcptInvalidE(EXBarrier_io_XcptInvalidE),
    .io_RegWriteE(EXBarrier_io_RegWriteE),
    .io_ALUResultM(EXBarrier_io_ALUResultM),
    .io_rdM(EXBarrier_io_rdM),
    .io_XcptInvalidM(EXBarrier_io_XcptInvalidM),
    .io_RegWriteM(EXBarrier_io_RegWriteM)
  );
  EXBarrier MEMBarrier ( // @[core.scala 94:26]
    .clock(MEMBarrier_clock),
    .reset(MEMBarrier_reset),
    .io_ALUResultE(MEMBarrier_io_ALUResultE),
    .io_rdE(MEMBarrier_io_rdE),
    .io_XcptInvalidE(MEMBarrier_io_XcptInvalidE),
    .io_RegWriteE(MEMBarrier_io_RegWriteE),
    .io_ALUResultM(MEMBarrier_io_ALUResultM),
    .io_rdM(MEMBarrier_io_rdM),
    .io_XcptInvalidM(MEMBarrier_io_XcptInvalidM),
    .io_RegWriteM(MEMBarrier_io_RegWriteM)
  );
  WBstage WBstage ( // @[core.scala 96:25]
    .io_ALUResultW(WBstage_io_ALUResultW),
    .io_rdW(WBstage_io_rdW),
    .io_RegWriteW(WBstage_io_RegWriteW),
    .io_RegWriteReq_addr(WBstage_io_RegWriteReq_addr),
    .io_RegWriteReq_data(WBstage_io_RegWriteReq_data),
    .io_RegWriteReq_w_en(WBstage_io_RegWriteReq_w_en),
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
  assign io_check_res = WBBarrier_io_check_res; // @[core.scala 187:16]
  assign io_exception = WBBarrier_io_exception; // @[core.scala 188:16]
  assign io_PCdebug = IFstage_io_PCF; // @[core.scala 191:21]
  assign io_InstrDdebug = IFBarrier_io_InstrD; // @[core.scala 192:21]
  assign io_PCEdebug = IDBarrier_io_PCE; // @[core.scala 193:21]
  assign io_RS1Edebug = IDBarrier_io_RD1E; // @[core.scala 194:21]
  assign io_RS2Edebug = IDBarrier_io_RD2E; // @[core.scala 195:21]
  assign io_BranchEdebug = IDBarrier_io_BranchE; // @[core.scala 196:21]
  assign io_JumpEdebug = IDBarrier_io_JumpE; // @[core.scala 197:21]
  assign io_PCSrcEdebug = EXstage_io_PCSrcE; // @[core.scala 198:21]
  assign io_PCTargetEdebug = EXstage_io_PCTargetE; // @[core.scala 199:21]
  assign io_RegWriteWdebug = MEMBarrier_io_RegWriteM; // @[core.scala 200:21]
  assign io_rdWdebug = MEMBarrier_io_rdM; // @[core.scala 201:21]
  assign io_PCSrcE_debug = EXstage_io_PCSrcE; // @[core.scala 203:24]
  assign IFstage_clock = clock;
  assign IFstage_reset = reset;
  assign IFstage_io_PCSrcE = EXstage_io_PCSrcE; // @[core.scala 102:24]
  assign IFstage_io_PCTargetE = EXstage_io_PCTargetE; // @[core.scala 103:24]
  assign IFBarrier_clock = clock;
  assign IFBarrier_reset = reset;
  assign IFBarrier_io_InstrF = IFstage_io_InstrF; // @[core.scala 106:25]
  assign IFBarrier_io_PCF = IFstage_io_PCF; // @[core.scala 107:25]
  assign IFBarrier_io_PCPlus4F = IFstage_io_PCPlus4F; // @[core.scala 108:25]
  assign IFBarrier_io_CLR = EXstage_io_PCSrcE; // @[core.scala 109:25]
  assign IDstage_clock = clock;
  assign IDstage_reset = reset;
  assign IDstage_io_inst = IFBarrier_io_InstrD; // @[core.scala 112:26]
  assign IDstage_io_pcD = IFBarrier_io_PCD; // @[core.scala 113:26]
  assign IDstage_io_pcPlus4D = IFBarrier_io_PCPlus4D; // @[core.scala 114:26]
  assign IDstage_io_RegWriteW = WBstage_io_RegWriteReq_w_en; // @[core.scala 115:26]
  assign IDstage_io_rdW = WBstage_io_RegWriteReq_addr; // @[core.scala 116:26]
  assign IDstage_io_ResultW = WBstage_io_RegWriteReq_data; // @[core.scala 117:26]
  assign IDBarrier_clock = clock;
  assign IDBarrier_reset = reset;
  assign IDBarrier_io_uopD = IDstage_io_uop; // @[core.scala 120:29]
  assign IDBarrier_io_rdD = IDstage_io_rdD; // @[core.scala 121:29]
  assign IDBarrier_io_RD1D = IDstage_io_RD1D; // @[core.scala 122:29]
  assign IDBarrier_io_RD2D = IDstage_io_RD2D; // @[core.scala 123:29]
  assign IDBarrier_io_XcptInvalidD = IDstage_io_XcptInvalid; // @[core.scala 124:29]
  assign IDBarrier_io_RegWriteD = IDstage_io_RegWriteD; // @[core.scala 125:29]
  assign IDBarrier_io_ALUSrcD = IDstage_io_ALUSrcD; // @[core.scala 126:29]
  assign IDBarrier_io_ImmExtD = IDstage_io_ImmExtD; // @[core.scala 127:29]
  assign IDBarrier_io_BranchD = IDstage_io_BranchD; // @[core.scala 128:29]
  assign IDBarrier_io_JumpD = IDstage_io_JumpD; // @[core.scala 129:29]
  assign IDBarrier_io_PCD = IDstage_io_pcD_out; // @[core.scala 130:29]
  assign IDBarrier_io_PCPlus4D = IDstage_io_pcPlus4D_out; // @[core.scala 131:29]
  assign IDBarrier_io_rs1D = IFBarrier_io_InstrD[19:15]; // @[core.scala 132:51]
  assign IDBarrier_io_rs2D = IFBarrier_io_InstrD[24:20]; // @[core.scala 133:51]
  assign IDBarrier_io_CLR = EXstage_io_PCSrcE; // @[core.scala 134:29]
  assign EXstage_io_RD1E = IDBarrier_io_RD1E; // @[core.scala 137:27]
  assign EXstage_io_RD2E = IDBarrier_io_RD2E; // @[core.scala 138:27]
  assign EXstage_io_ImmExtE = IDBarrier_io_ImmExtE; // @[core.scala 139:27]
  assign EXstage_io_ALUSrcE = IDBarrier_io_ALUSrcE; // @[core.scala 140:27]
  assign EXstage_io_rdE = IDBarrier_io_rdE; // @[core.scala 141:27]
  assign EXstage_io_uopE = IDBarrier_io_uopE; // @[core.scala 142:27]
  assign EXstage_io_RegWriteE = IDBarrier_io_RegWriteE; // @[core.scala 143:27]
  assign EXstage_io_XcptInvalidE = IDBarrier_io_XcptInvalidE; // @[core.scala 144:27]
  assign EXstage_io_ForwardAE = ForwardingUnit_io_forwardA; // @[core.scala 181:25]
  assign EXstage_io_ForwardBE = ForwardingUnit_io_forwardB; // @[core.scala 182:25]
  assign EXstage_io_ResultW = WBstage_io_ResultW; // @[core.scala 184:25]
  assign EXstage_io_ALUResultM = EXBarrier_io_ALUResultM; // @[core.scala 183:25]
  assign EXstage_io_PCE = IDBarrier_io_PCE; // @[core.scala 145:27]
  assign EXstage_io_PCPlus4E = IDBarrier_io_PCPlus4E; // @[core.scala 146:27]
  assign EXstage_io_BranchE = IDBarrier_io_BranchE; // @[core.scala 147:27]
  assign EXstage_io_JumpE = IDBarrier_io_JumpE; // @[core.scala 148:27]
  assign EXBarrier_clock = clock;
  assign EXBarrier_reset = reset;
  assign EXBarrier_io_ALUResultE = EXstage_io_ALUResultE; // @[core.scala 151:29]
  assign EXBarrier_io_rdE = EXstage_io_rdOutE; // @[core.scala 152:29]
  assign EXBarrier_io_XcptInvalidE = EXstage_io_exceptionE; // @[core.scala 153:29]
  assign EXBarrier_io_RegWriteE = EXstage_io_RegWriteOutE; // @[core.scala 154:29]
  assign MEMBarrier_clock = clock;
  assign MEMBarrier_reset = reset;
  assign MEMBarrier_io_ALUResultE = EXBarrier_io_ALUResultM; // @[core.scala 157:30]
  assign MEMBarrier_io_rdE = EXBarrier_io_rdM; // @[core.scala 158:30]
  assign MEMBarrier_io_XcptInvalidE = EXBarrier_io_XcptInvalidM; // @[core.scala 159:30]
  assign MEMBarrier_io_RegWriteE = EXBarrier_io_RegWriteM; // @[core.scala 160:30]
  assign WBstage_io_ALUResultW = MEMBarrier_io_ALUResultM; // @[core.scala 163:25]
  assign WBstage_io_rdW = MEMBarrier_io_rdM; // @[core.scala 164:25]
  assign WBstage_io_RegWriteW = MEMBarrier_io_RegWriteM; // @[core.scala 165:25]
  assign WBBarrier_clock = clock;
  assign WBBarrier_reset = reset;
  assign WBBarrier_io_ResultW = WBstage_io_ResultW; // @[core.scala 168:29]
  assign WBBarrier_io_XcptInvalidW = MEMBarrier_io_XcptInvalidM; // @[core.scala 169:29]
  assign ForwardingUnit_io_rs1_EX = {{27'd0}, IDBarrier_io_Rs1E}; // @[core.scala 172:30]
  assign ForwardingUnit_io_rs2_EX = {{27'd0}, IDBarrier_io_Rs2E}; // @[core.scala 173:30]
  assign ForwardingUnit_io_rd_MEM = {{27'd0}, EXBarrier_io_rdM}; // @[core.scala 175:30]
  assign ForwardingUnit_io_rd_WB = {{27'd0}, MEMBarrier_io_rdM}; // @[core.scala 178:30]
  assign ForwardingUnit_io_wrEn_MEM = EXBarrier_io_RegWriteM; // @[core.scala 176:30]
  assign ForwardingUnit_io_wrEn_WB = MEMBarrier_io_RegWriteM; // @[core.scala 179:30]
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
  output        io_RegWriteWdebug,
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
  wire  core_io_RegWriteWdebug; // @[PipelinedRISCV32I.scala 38:20]
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
    .io_RegWriteWdebug(core_io_RegWriteWdebug),
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
  assign io_RegWriteWdebug = core_io_RegWriteWdebug; // @[PipelinedRISCV32I.scala 51:21]
  assign io_rdWdebug = core_io_rdWdebug; // @[PipelinedRISCV32I.scala 52:21]
  assign io_PCSrcE_debug = core_io_PCSrcE_debug; // @[PipelinedRISCV32I.scala 53:21]
  assign core_clock = clock;
  assign core_reset = reset;
endmodule
