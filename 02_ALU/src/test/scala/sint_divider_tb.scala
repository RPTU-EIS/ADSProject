package divider

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class SIntDividerTest extends AnyFlatSpec with ChiselScalatestTester {
  "SignCalc" should "handle two negative operands" in {
    test(new SignCalc) { dut =>
      dut.io.a.poke(-8.S)
      dut.io.b.poke(-2.S)
      dut.io.q_sign.expect(false.B)
    }
  }

  it should "use the dividend sign for the remainder" in {
    test(new SignCalc) { dut =>
      dut.io.a.poke(-8.S)
      dut.io.b.poke(2.S)
      dut.io.r_sign.expect(true.B)
    }
  }

  "ToUnsigned" should "convert a negative value" in {
    test(new ToUnsigned) { dut =>
      dut.io.valueIn.poke(-5.S)
      dut.io.valueOut.expect(5.U)
    }
  }

  "ToSigned" should "output negative sign" in {
    test(new ToSigned) { dut =>
      dut.io.valueIn.poke(10.U)
      dut.io.sign.poke(true.B)
      dut.io.valueOut.expect(-10.S)
    }
  }

  "DivisorRegister" should "load the upper half" in {
    test(new DivisorRegister) { dut =>
      dut.io.load.poke(true.B)
      dut.io.dataLoad.poke("h1234".U)
      dut.clock.step()
      dut.io.dataOut.expect("h12340000".U)
    }
  }

  "QuotientRegister" should "retain the top bit" in {
    test(new QuotientRegister) { dut =>
      dut.io.dataIn.poke(true.B)
      dut.clock.step()
      dut.io.dataIn.poke(false.B)
      dut.clock.step(15)
      dut.io.dataOut.expect("h8000".U)
    }
  }

  "RemainderRegister" should "write the selected value" in {
    test(new RemainderRegister) { dut =>
      dut.io.write.poke(true.B)
      dut.io.dataWrite.poke(123.U)
      dut.io.dataIn.poke(456.U)
      dut.clock.step()
      dut.io.dataOut.expect(123.U)
    }
  }

  "Controller" should "assert valid after 18 cycles" in {
    test(new Controller) { dut =>
      dut.io.a.poke(0.U)
      dut.io.b.poke(1.U)
      dut.io.rst.poke(true.B)
      dut.clock.step()
      dut.io.rst.poke(false.B)
      dut.clock.step(17)
      dut.io.valid.expect(false.B)
      dut.clock.step()
      dut.io.valid.expect(true.B)
    }
  }

  "SIntDivider" should "cover some signed division edge cases" in {
    test(new SIntDivider) { dut =>
      dut.io.rst.poke(true.B)
      dut.clock.step(1)
      dut.io.rst.poke(false.B)

      dut.io.a.poke(-32768.S)
      dut.io.b.poke(-1.S)
      dut.clock.step(18)
      dut.io.valid.expect(true.B)
      dut.io.q.expect(-32768.S)
      dut.io.r.expect(0.S)

      dut.io.a.poke(-32768.S)
      dut.io.b.poke(3.S)
      dut.clock.step(18)
      dut.io.valid.expect(true.B)
      dut.io.q.expect(-10922.S)
      dut.io.r.expect(-2.S)

      dut.io.a.poke(32767.S)
      dut.io.b.poke(1.S)
      dut.clock.step(18)
      dut.io.valid.expect(true.B)
      dut.io.q.expect(32767.S)
      dut.io.r.expect(0.S)

      dut.io.a.poke(17.S)
      dut.io.b.poke(-5.S)
      dut.clock.step(18)
      dut.io.valid.expect(true.B)
      dut.io.q.expect(-3.S)
      dut.io.r.expect(2.S)

      dut.io.a.poke(0.S)
      dut.io.b.poke(0.S)
      dut.clock.step(18)
      dut.io.valid.expect(true.B)
      dut.io.q.expect(-1.S)
      dut.io.r.expect(0.S)
    }
  }
}
