module BindsTo_0_IF(
  input         clock,
  input         reset,
  output [31:0] io_inst
);

initial begin
  $readmemh("src/test/programs/BinaryFile", IF.IMem);
end
                      endmodule

bind IF BindsTo_0_IF BindsTo_0_IF_Inst(.*);