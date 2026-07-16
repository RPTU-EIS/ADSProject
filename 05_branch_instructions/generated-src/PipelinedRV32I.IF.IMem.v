module BindsTo_0_IF(
  input         clock,
  input         reset,
  input         io_PCSrcE,
  input  [31:0] io_PCTargetE,
  output [31:0] io_InstrF,
  output [31:0] io_PCF,
  output [31:0] io_PCPlus4F
);

initial begin
  $readmemh("src/test/programs/BinaryFile", IF.IMem);
end
                      endmodule

bind IF BindsTo_0_IF BindsTo_0_IF_Inst(.*);