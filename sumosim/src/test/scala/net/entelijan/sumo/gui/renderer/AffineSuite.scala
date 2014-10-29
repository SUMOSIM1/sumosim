package net.entelijan.sumo.gui.renderer

import org.scalatest.FunSuite

class AffineSuite extends FunSuite {

  test("translate only x") {
    val p = AffinePoliPoint(0, 10)
    val p1 = p.translate(10, 0)

    assert(p1 === AffinePoliPoint(10, 10))
  }

  test("translate only y") {
    val p = AffinePoliPoint(0, 10)
    val p1 = p.translate(0, -20)

    assert(p1 === AffinePoliPoint(0, -10))
  }

  test("translate x and y") {
    val p = AffinePoliPoint(0, 10)
    val p1 = p.translate(-5, 20)

    assert(p1 === AffinePoliPoint(-5, 30))
  }

  test("scale only x") {
    val p = AffinePoliPoint(0, 10)
    val p1 = p.scale(-5, 1)

    assert(p1 === AffinePoliPoint(0, 10))
  }

  test("scale only x, x ne 0") {
    val p = AffinePoliPoint(2, 10)
    val p1 = p.scale(-5, 1)

    assert(p1 === AffinePoliPoint(-10, 10))
  }

  test("scale only y") {
    val p = AffinePoliPoint(0, 10)
    val p1 = p.scale(1, 22)

    assert(p1 === AffinePoliPoint(0, 220))
  }

  test("scale x and y") {
    val p = AffinePoliPoint(0, 10)
    val p1 = p.scale(11, 22)

    assert(p1 === AffinePoliPoint(0, 220))
  }

  test("scale x and y, x ne 0") {
    val p = AffinePoliPoint(-1, 10)
    val p1 = p.scale(11, 22)

    assert(p1 === AffinePoliPoint(-11, 220))
  }

  import scala.math._

  test("rotate center +90deg") {
    val p = AffinePoliPoint(10, 0)
    val p1 = p.rotate(Pi / 2.0)

    assert(p1 === AffinePoliPoint(0, 10))
  }

  test("rotate center -90deg") {
    val p = AffinePoliPoint(10, 0)
    val p1 = p.rotate(-Pi / 2.0)

    assert(p1 === AffinePoliPoint(0, -10))
  }

  test("rotate center 180deg") {
    val p = AffinePoliPoint(10, 0)
    val p1 = p.rotate(Pi)

    assert(p1 === AffinePoliPoint(-10, 0))
  }

}