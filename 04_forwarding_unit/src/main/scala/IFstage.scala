package core_tile

import chisel3._
import chisel3.util.experimental.loadMemoryFromFile


// ---- IF STAGE (FETCH) ----
/*
WHY: This is the first stage of the pipeline.
This is where your Instruction Memory lives.
The IF stage has one simple job: read the next instruction from memory 
and send it to the rest of the pipeline.
It uses the PC (Program Counter) to know which instruction to read.
After reading, it moves the PC forward so the next cycle reads the next instruction.
Without this stage, the processor would not know what to do next.
*/
class IF (BinaryFile: String) extends Module {
  val io = IO(new Bundle {

    // ---- OUTPUT TO IF BARRIER ----
    /*
    WHY: We send the fetched instruction to the IF barrier.
    It is 32 bits long because every RISC-V instruction is 32 bits.
    The IF barrier will hold it for one cycle before ID decodes it.
    */
    val instr = Output(UInt(32.W))           // instruction going out, 32 bits
  })


  // ---- INSTRUCTION MEMORY ----
  /*
  WHY: This is the memory that stores the program.
  We make a memory with 4096 slots, each one 32 bits wide.
  4096 slots is enough to hold a small program.
  32 bits because every RISC-V instruction is 32 bits.
  We use loadMemoryFromFile to fill the memory with a binary file 
  before the processor starts running.
  BinaryFile is the path of that file, this giving when we build the processor.
  This way the same processor can run different programs.
  */
  val IMem = Mem(4096, UInt(32.W))           // memory: 4096 entries, 32 bits each
  loadMemoryFromFile(IMem, BinaryFile)       // load the program from the binary file


  // ---- PROGRAM COUNTER (PC) ----
  /*
  WHY: The PC is the address of the next instruction to fetch.
  It is 32 bits because RISC-V addresses are 32 bits.
  We start it at 0 so the first instruction comes from address 0.
  This is the very first instruction of the program.
  RegInit means: it is a register, and its initial value is 0.
  */
  val PC = RegInit(0.U(32.W))                // program counter, starts at address 0


  // ---- FETCH THE INSTRUCTION ----
  /*
  WHY: We use the PC to read from the Instruction Memory.
  But there is a trick: the PC counts in bytes, not in words.
  Each instruction is 4 bytes long, so we divide the PC by 4.
  Shifting right by 2 bits (PC >> 2) is the same as dividing by 4.
  This gives us the correct word index in the memory.
  For example: PC=0 reads word 0, PC=4 reads word 1, PC=8 reads word 2.
  */
  io.instr := IMem(PC >> 2)                  // read instruction at PC, word-aligned


  // ---- INCREMENT THE PC ----
  /*
  WHY: After fetching, we move the PC forward to the next instruction.
  We add 4 because each instruction is 4 bytes long.
  On the next clock cycle, the IF stage will fetch the next instruction.
  This is what makes the processor run continuously.
  Without this, the processor would fetch the same instruction forever.
  */
  PC := PC + 4.U                             // move PC to the next instruction
}