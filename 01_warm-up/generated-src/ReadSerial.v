module Controller(
  input        clock,
  input        reset,
  input        io_rst,
  input        io_rxd,
  input        io_cnt_s,
  output       io_cnt_en,
  output       io_valid,
  output [1:0] io_ps_t
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
`endif // RANDOMIZE_REG_INIT
  reg [1:0] ps; // @[ReadSerial.scala 26:19]
  reg  valid; // @[ReadSerial.scala 28:22]
  wire [1:0] _GEN_0 = ~io_rxd ? 2'h1 : ps; // @[ReadSerial.scala 46:36 47:16]
  wire  _GEN_1 = ~io_rxd & ~io_rxd; // @[ReadSerial.scala 30:13 46:36 48:23]
  wire  _GEN_3 = io_rxd ? 1'h0 : _GEN_1; // @[ReadSerial.scala 30:13 43:32]
  wire  _T_5 = ~io_cnt_s; // @[ReadSerial.scala 53:25]
  wire [1:0] _GEN_4 = io_cnt_s ? 2'h0 : ps; // @[ReadSerial.scala 57:38 58:16]
  wire  _GEN_5 = io_cnt_s | valid; // @[ReadSerial.scala 57:38 59:19 28:22]
  wire [1:0] _GEN_7 = ~io_cnt_s ? 2'h1 : _GEN_4; // @[ReadSerial.scala 53:34 55:16]
  wire  _GEN_8 = ~io_cnt_s ? valid : _GEN_5; // @[ReadSerial.scala 28:22 53:34]
  wire  _GEN_9 = 2'h1 == ps & _T_5; // @[ReadSerial.scala 30:13 39:18]
  wire  _GEN_14 = 2'h0 == ps ? _GEN_3 : _GEN_9; // @[ReadSerial.scala 39:18]
  assign io_cnt_en = io_rst ? 1'h0 : _GEN_14; // @[ReadSerial.scala 30:13 35:24]
  assign io_valid = valid; // @[ReadSerial.scala 31:12]
  assign io_ps_t = ps; // @[ReadSerial.scala 71:11]
  always @(posedge clock) begin
    if (reset) begin // @[ReadSerial.scala 26:19]
      ps <= 2'h0; // @[ReadSerial.scala 26:19]
    end else if (io_rst) begin // @[ReadSerial.scala 35:24]
      ps <= 2'h0; // @[ReadSerial.scala 36:8]
    end else if (2'h0 == ps) begin // @[ReadSerial.scala 39:18]
      if (io_rxd) begin // @[ReadSerial.scala 43:32]
        ps <= 2'h0; // @[ReadSerial.scala 44:16]
      end else begin
        ps <= _GEN_0;
      end
    end else if (2'h1 == ps) begin // @[ReadSerial.scala 39:18]
      ps <= _GEN_7;
    end
    if (reset) begin // @[ReadSerial.scala 28:22]
      valid <= 1'h0; // @[ReadSerial.scala 28:22]
    end else if (!(io_rst)) begin // @[ReadSerial.scala 35:24]
      if (2'h0 == ps) begin // @[ReadSerial.scala 39:18]
        valid <= 1'h0; // @[ReadSerial.scala 42:17]
      end else if (2'h1 == ps) begin // @[ReadSerial.scala 39:18]
        valid <= _GEN_8;
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
  ps = _RAND_0[1:0];
  _RAND_1 = {1{`RANDOM}};
  valid = _RAND_1[0:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module ShiftRegister(
  input        clock,
  input        reset,
  input        io_rxd,
  output [7:0] io_data
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
`endif // RANDOMIZE_REG_INIT
  reg [7:0] reg_; // @[ReadSerial.scala 112:20]
  wire [7:0] _reg_T_1 = {reg_[6:0],io_rxd}; // @[Cat.scala 31:58]
  assign io_data = reg_; // @[ReadSerial.scala 116:11]
  always @(posedge clock) begin
    if (reset) begin // @[ReadSerial.scala 112:20]
      reg_ <= 8'h0; // @[ReadSerial.scala 112:20]
    end else begin
      reg_ <= _reg_T_1; // @[ReadSerial.scala 115:7]
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
  reg_ = _RAND_0[7:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module Counter(
  input        clock,
  input        reset,
  input        io_rst,
  input        io_cnt_en,
  output       io_cnt_s,
  output [2:0] io_count_t
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
`endif // RANDOMIZE_REG_INIT
  reg [2:0] count; // @[ReadSerial.scala 86:22]
  wire [2:0] _count_T_1 = count + 3'h1; // @[ReadSerial.scala 93:20]
  assign io_cnt_s = count == 3'h7; // @[ReadSerial.scala 96:14]
  assign io_count_t = count; // @[ReadSerial.scala 100:14]
  always @(posedge clock) begin
    if (reset) begin // @[ReadSerial.scala 86:22]
      count <= 3'h0; // @[ReadSerial.scala 86:22]
    end else if (io_rst | io_cnt_s) begin // @[ReadSerial.scala 90:44]
      count <= 3'h0; // @[ReadSerial.scala 91:11]
    end else if (io_cnt_en) begin // @[ReadSerial.scala 92:33]
      count <= _count_T_1; // @[ReadSerial.scala 93:11]
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
  count = _RAND_0[2:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module ReadSerial(
  input        clock,
  input        reset,
  input        io_reset_n,
  input        io_rxd,
  output       io_valid,
  output [7:0] io_data,
  output [1:0] io_debug_ps,
  output       io_debug_cnt_s,
  output [2:0] io_debug_count
);
  wire  CU_clock; // @[ReadSerial.scala 144:18]
  wire  CU_reset; // @[ReadSerial.scala 144:18]
  wire  CU_io_rst; // @[ReadSerial.scala 144:18]
  wire  CU_io_rxd; // @[ReadSerial.scala 144:18]
  wire  CU_io_cnt_s; // @[ReadSerial.scala 144:18]
  wire  CU_io_cnt_en; // @[ReadSerial.scala 144:18]
  wire  CU_io_valid; // @[ReadSerial.scala 144:18]
  wire [1:0] CU_io_ps_t; // @[ReadSerial.scala 144:18]
  wire  SR_clock; // @[ReadSerial.scala 145:18]
  wire  SR_reset; // @[ReadSerial.scala 145:18]
  wire  SR_io_rxd; // @[ReadSerial.scala 145:18]
  wire [7:0] SR_io_data; // @[ReadSerial.scala 145:18]
  wire  CNT_clock; // @[ReadSerial.scala 146:19]
  wire  CNT_reset; // @[ReadSerial.scala 146:19]
  wire  CNT_io_rst; // @[ReadSerial.scala 146:19]
  wire  CNT_io_cnt_en; // @[ReadSerial.scala 146:19]
  wire  CNT_io_cnt_s; // @[ReadSerial.scala 146:19]
  wire [2:0] CNT_io_count_t; // @[ReadSerial.scala 146:19]
  Controller CU ( // @[ReadSerial.scala 144:18]
    .clock(CU_clock),
    .reset(CU_reset),
    .io_rst(CU_io_rst),
    .io_rxd(CU_io_rxd),
    .io_cnt_s(CU_io_cnt_s),
    .io_cnt_en(CU_io_cnt_en),
    .io_valid(CU_io_valid),
    .io_ps_t(CU_io_ps_t)
  );
  ShiftRegister SR ( // @[ReadSerial.scala 145:18]
    .clock(SR_clock),
    .reset(SR_reset),
    .io_rxd(SR_io_rxd),
    .io_data(SR_io_data)
  );
  Counter CNT ( // @[ReadSerial.scala 146:19]
    .clock(CNT_clock),
    .reset(CNT_reset),
    .io_rst(CNT_io_rst),
    .io_cnt_en(CNT_io_cnt_en),
    .io_cnt_s(CNT_io_cnt_s),
    .io_count_t(CNT_io_count_t)
  );
  assign io_valid = CU_io_valid; // @[ReadSerial.scala 161:12]
  assign io_data = SR_io_data; // @[ReadSerial.scala 162:11]
  assign io_debug_ps = CU_io_ps_t; // @[ReadSerial.scala 163:15]
  assign io_debug_cnt_s = CU_io_cnt_s; // @[ReadSerial.scala 164:18]
  assign io_debug_count = CNT_io_count_t; // @[ReadSerial.scala 165:18]
  assign CU_clock = clock;
  assign CU_reset = reset;
  assign CU_io_rst = io_reset_n; // @[ReadSerial.scala 150:13]
  assign CU_io_rxd = io_rxd; // @[ReadSerial.scala 149:13]
  assign CU_io_cnt_s = CNT_io_cnt_s; // @[ReadSerial.scala 151:15]
  assign SR_clock = clock;
  assign SR_reset = reset;
  assign SR_io_rxd = io_rxd; // @[ReadSerial.scala 157:13]
  assign CNT_clock = clock;
  assign CNT_reset = reset;
  assign CNT_io_rst = io_reset_n; // @[ReadSerial.scala 153:14]
  assign CNT_io_cnt_en = CU_io_cnt_en; // @[ReadSerial.scala 154:17]
endmodule
