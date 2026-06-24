// ADS I Class Project
// Assignment 03: Pipelined RISC-V Core
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 11/01/2025

/*
  This file creates the Immediate Generator.
  The immediate is the constant value that comes inside some RISC-V instructions.
  The difficult part is that each instruction format puts the immediate bits
  in different positions, so this module puts them together again.
*/

package core_tile

import chisel3._       // Import the basic Chisel hardware types
import chisel3.util._  // Import switch, is, Cat, and Fill

// -----------------------------------------
// Immediate Generator
// -----------------------------------------

class ImmediateGenerator extends Module {
  val io = IO(new Bundle {
    val instruction = Input(UInt(32.W))   // Get the full 32-bit instruction
    val immSel      = Input(ImmSel())     // Select which immediate format is needed
    val immediate   = Output(UInt(32.W))  // Send the final 32-bit immediate
  })

  io.immediate := 0.U  // Default immediate is zero, useful for R-type instructions

  switch(io.immSel) {  // Check which immediate format needs to be generated

    is(ImmSel.I) {
      io.immediate := Cat(
        Fill(20, io.instruction(31)),  // Copy sign bit to the upper 20 bits
        io.instruction(31, 20)         // Take immediate bits from instruction
      )
    }

    is(ImmSel.S) {
      io.immediate := Cat(
        Fill(20, io.instruction(31)),  // Copy sign bit to the upper 20 bits
        io.instruction(31, 25),        // Take upper immediate bits
        io.instruction(11, 7)          // Take lower immediate bits
      )
    }

    is(ImmSel.B) {
      io.immediate := Cat(
        Fill(19, io.instruction(31)),  // Copy sign bit to the upper 19 bits
        io.instruction(31),            // Take immediate bit 12
        io.instruction(7),             // Take immediate bit 11
        io.instruction(30, 25),        // Take immediate bits 10 to 5
        io.instruction(11, 8),         // Take immediate bits 4 to 1
        0.U(1.W)                       // Add zero because branch targets are aligned
      )
    }

    is(ImmSel.U) {
      io.immediate := Cat(
        io.instruction(31, 12),  // Take upper 20 immediate bits
        0.U(12.W)                // Lower 12 bits are zero
      )
    }

    is(ImmSel.J) {
      io.immediate := Cat(
        Fill(11, io.instruction(31)),  // Copy sign bit to the upper 11 bits
        io.instruction(31),            // Take immediate bit 20
        io.instruction(19, 12),        // Take immediate bits 19 to 12
        io.instruction(20),            // Take immediate bit 11
        io.instruction(30, 21),        // Take immediate bits 10 to 1
        0.U(1.W)                       // Add zero because jump targets are aligned
      )
    }

    is(ImmSel.R) {
      io.immediate := 0.U  // R-type has no immediate
    }
  }
}
