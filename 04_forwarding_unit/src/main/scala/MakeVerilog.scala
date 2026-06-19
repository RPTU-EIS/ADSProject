package makeverilog

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

import PipelinedRV32I._


// ---- VERILOG GENERATOR ----
/*
WHY: This file is not part of the processor itself.
It is a helper tool.
Its job is to take our Chisel design and convert it to Verilog.
Verilog is the language that real hardware tools understand.
Chisel is nice for designing, but FPGAs and chips need Verilog.
So we use this tool to translate one to the other.
We only run this when we want to generate the final hardware files.
*/
object Verilog_Gen extends App {

  // ---- GENERATE VERILOG ----
  /*
  WHY: emitVerilog takes our processor and writes the Verilog files.
  We pass the BinaryFile path so the Instruction Memory knows what program to load.
  The "--target-dir" tells the tool where to save the generated files.
  In this case, the Verilog files go to a folder called "generated-src".
  After running this, we can take those files and use them in real hardware tools.
  */
  emitVerilog(
    new PipelinedRV32I("src/test/programs/BinaryFile"),   // create the processor with the binary
    Array("--target-dir", "generated-src")                // save the output here
  )
}