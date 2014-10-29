package net.entelijan.sumo.commons

import net.entelijan.sumo.commons._
import net.entelijan.util._
import scala.math._
import org.scalatest.FunSuite
import org.scalatest.Matchers

class VecmathSuite extends FunSuite with VecUtil with TrigUtil with Matchers {


  test("testDirection001") {
    val v1 = new Vec2(1.0, 1.0)
    toRad(45) shouldBe v1.direction +- 0.00001
  }

  test("testDirection002") {
    val v1 = new Vec2(1.0, 2.0)
    toRad(63.43494882292201) shouldBe v1.direction +- 0.00001
  }

  test("testDirection003") {
    val v1 = new Vec2(0, 1.0)
    toRad(90) shouldBe v1.direction +- 0.0001
  }

  test("testDirection003a") {
    val v1 = new Vec2(0, -1.0)
    toRad(-90) shouldBe v1.direction +- 0.0001
  }

  test("testDirection004") {
    val v1 = new Vec2(-1.0, 1.0)
    toRad(135)  shouldBe v1.direction +- 0.00001
  }

  test("testDirection005") {
    val v1 = new Vec2(1.0, 0.0)
    toRad(0) shouldBe v1.direction +- 0.0001
  }

  test("testDirection006") {
    val v1 = new Vec2(-1.0, -1.0)
    toRad(-135) shouldBe v1.direction +- 0.0001
  }

  test("testDirection007") {
    val v1 = new Vec2(1.0, -1.0)
    toRad(-45) shouldBe v1.direction +- 0.0001
  }

  test("testRotate001") {
    val v1 = new Vec2(1.0, 1.0)
    90.0 shouldBe toDeg(v1.rot(toRad(45)).direction) +- 0.00001
  }

  test("testRotate002") {
    val v1 = new Vec2(1.0, 1.0)
    0.0 shouldBe testRound(v1.rot(toRad(-45)).direction) +- 0.00001
  }

  test("testRotate003") {
    val v1 = new Vec2(1.0, 1.0)
    135.0 shouldBe toDeg(v1.rot(toRad(90)).direction) +- 0.00001
  }

  test("testRotate004") {
    val v1 = new Vec2(1.0, 1.0)
    -135.0 shouldBe toDeg(v1.rot(toRad(180)).direction) +- 0.00001
  }
  test("testRotate005") {
    val v1 = new Vec2(1.0, 1.0)
    -45.0 shouldBe testRound(toDeg(v1.rot(toRad(270)).direction)) +- 0.0001
  }

  test("testEquals001") {
    val v1 = new Vec2(1.0, 1.0)
    val v2 = new Vec2(1.0, 1.0)
    v1 shouldBe v2
    v1 shouldBe v1
    v2 shouldBe v2
  }

  test("testEquals002") {
    val v1 = new Vec2(1.0, 1.0)
    val v2 = new Vec2(1.1, 1.0)
    assert(v1 != v2)
  }

  test("testEquals001a") {
    val v1 = new Point2(1.0, 1.0)
    val v2 = new Point2(1.0, 1.0)
    v1 shouldBe v2
    v1 shouldBe v1
    v2 shouldBe v2
  }

  test("testEquals002b") {
    val v1 = new Point2(1.0, 1.0)
    val v2 = new Point2(1.1, 1.0)
    assert(v1 != v2)
  }

  test("testNorm") {
    90 shouldBe normDeg(90)
    180 shouldBe normDeg(180)
    -170 shouldBe normDeg(190)
    170 shouldBe normDeg(-190)
    -45 shouldBe normDeg(-45)
    -90 shouldBe normDeg(270)
  }

  private def normDeg(deg: Int): Int = {
    round(toDeg(normalize(toRad(deg)))).asInstanceOf[Int]
  }

  private def smallDouble = 0.0001

  test("testOctal0") {
    0 shouldBe octal(0.0)
    0 shouldBe octal(Pi / 8 - smallDouble)
    0 shouldBe octal(-Pi / 8 + smallDouble)
  }

  test("testOctal1") {
    1 shouldBe octal(Pi / 4)
    1 shouldBe octal(3 * Pi / 8 - smallDouble)
    1 shouldBe octal(Pi / 8 + smallDouble)
  }

  test("testOctal2") {
    2 shouldBe octal(Pi / 2)
    2 shouldBe octal(3 * Pi / 8 + smallDouble)
    val x1 = 5 * Pi / 8
    val x2 = x1 - smallDouble
    2 shouldBe octal(x2)
  }

  test("testOctal3") {
    3 shouldBe octal(3 * Pi / 4)
    3 shouldBe octal(7 * Pi / 8 - smallDouble)
    3 shouldBe octal(5 * Pi / 8 + smallDouble)
  }

  test("testOctal4") {
    4 shouldBe octal(Pi)
    4 shouldBe octal(-Pi)
    4 shouldBe octal(-7 * Pi / 8 - smallDouble)
    4 shouldBe octal(7 * Pi / 8 + smallDouble)
  }

  test("testOctal5") {
    5 shouldBe octal(-3 * Pi / 4)
    5 shouldBe octal(-5 * Pi / 8 - smallDouble)
    5 shouldBe octal(-7 * Pi / 8 + smallDouble)
  }

  test("testOctal6") {
    6 shouldBe octal(-Pi / 2)
    6 shouldBe octal(-3 * Pi / 8 - smallDouble)
    6 shouldBe octal(-5 * Pi / 8 + smallDouble)
  }

  test("testOctal7") {
	  7 shouldBe octal(-Pi / 4)
    7 shouldBe octal(-3 * Pi / 8 + smallDouble)
    7 shouldBe octal(-Pi / 8 - smallDouble)
  }

  private def equals(v1: Vec2, v2: Vec2) = {
    val a = new Vec2(testRound(v1.x), testRound(v1.y))
    val b = new Vec2(testRound(v2.x), testRound(v2.y))
    a == b
  }

  private def testRound(v: Double) = {
    val r = 100000000.0
    round(v * r) / r
  }
  test("isPointInSector_01") {
    assert(isPointInSector(new Point2(0, 0), toRad(0), toRad(20), new Point2(500, 100)))
  }
  test("isPointInSector_02") {
    assert(isPointInSector(new Point2(0, 0), toRad(0), toRad(20), new Point2(500, -100)))
  }
  test("isPointInSector_03") {
    assert(!isPointInSector(new Point2(0, 0), toRad(0), toRad(20), new Point2(500, 200)))
  }
  test("isPointInSector_04") {
    assert(!isPointInSector(new Point2(0, 0), toRad(0), toRad(20), new Point2(500, -200)))
  }
  test("isPointInSector_05") {
    assert(!isPointInSector(new Point2(0, 0), toRad(0), toRad(20), new Point2(500, 300)))
  }
  test("isPointInSector_06") {
    assert(!isPointInSector(new Point2(0, 0), toRad(0), toRad(20), new Point2(500, -300)))
  }

  test("isPointInSector_10") {
    assert(!isPointInSector(new Point2(-10, 5), toRad(45), toRad(20), new Point2(10, 10)))
  }
  test("isPointInSector_11") {
    assert(isPointInSector(new Point2(-10, 5), toRad(45), toRad(20), new Point2(10, 20)))
  }
  test("isPointInSector_12") {
    assert(isPointInSector(new Point2(-10, 5), toRad(45), toRad(20), new Point2(10, 30)))
  }
  test("isPointInSector_13") {
    assert(isPointInSector(new Point2(-10, 5), toRad(45), toRad(20), new Point2(10, 40)))
  }
  test("isPointInSector_14") {
    assert(!isPointInSector(new Point2(-10, 5), toRad(45), toRad(20), new Point2(10, 50)))
  }
  test("isPointInSector_15") {
    assert(!isPointInSector(new Point2(-10, 5), toRad(45), toRad(20), new Point2(10, 60)))
  }

  test("isPointInSector_20") {
    assert(!isPointInSector(new Point2(-10, 5), toRad(45), toRad(20), new Point2(-35, 0)))
  }
  test("isPointInSector_21") {
    assert(!isPointInSector(new Point2(-10, 5), toRad(45), toRad(20), new Point2(-35, -10)))
  }
  test("isPointInSector_22") {
    assert(!isPointInSector(new Point2(-10, 5), toRad(45), toRad(20), new Point2(-35, -20)))
  }
  test("isPointInSector_23") {
    assert(!isPointInSector(new Point2(-10, 5), toRad(45), toRad(20), new Point2(-35, -30)))
  }
  test("isPointInSector_24") {
    assert(!isPointInSector(new Point2(-10, 5), toRad(45), toRad(20), new Point2(-35, -40)))
  }
  test("isPointInSector_25") {
    assert(!isPointInSector(new Point2(-10, 5), toRad(45), toRad(20), new Point2(-35, -50)))
  }

  test("isPointInSector_30") {
    assert(!isPointInSector(new Point2(-10, -15), toRad(-10), toRad(20), new Point2(30, 0)))
  }
  test("isPointInSector_31") {
    assert(isPointInSector(new Point2(-10, -15), toRad(-10), toRad(20), new Point2(30, -10)))
  }
  test("isPointInSector_32") {
    assert(isPointInSector(new Point2(-10, -15), toRad(-10), toRad(20), new Point2(30, -20)))
  }
  test("isPointInSector_33") {
    assert(isPointInSector(new Point2(-10, -15), toRad(-10), toRad(20), new Point2(30, -30)))
  }
  test("isPointInSector_34") {
    assert(!isPointInSector(new Point2(-10, -15), toRad(-10), toRad(20), new Point2(30, -40)))
  }
  test("isPointInSector_35") {
    assert(!isPointInSector(new Point2(-10, -15), toRad(-10), toRad(20), new Point2(30, -40)))
  }

  test("isPointInSector_40") {
    assert(!isPointInSector(new Point2(-10, -15), toRad(-10), toRad(20), new Point2(-50, 10)))
  }
  test("isPointInSector_41") {
    assert(!isPointInSector(new Point2(-10, -15), toRad(-10), toRad(20), new Point2(-50, 0)))
  }
  test("isPointInSector_42") {
    assert(!isPointInSector(new Point2(-10, -15), toRad(-10), toRad(20), new Point2(-50, -10)))
  }
  test("isPointInSector_43") {
    assert(!isPointInSector(new Point2(-10, -15), toRad(-10), toRad(20), new Point2(-50, -20)))
  }
  test("isPointInSector_44") {
    assert(!isPointInSector(new Point2(-10, -15), toRad(-10), toRad(20), new Point2(-50, -30)))
  }
  test("isPointInSector_45") {
    assert(!isPointInSector(new Point2(-10, -15), toRad(-10), toRad(20), new Point2(-50, -40)))
  }
  test("isPointInSector_46") {
    assert(!isPointInSector(new Point2(-10, -15), toRad(-10), toRad(20), new Point2(-50, -50)))
  }

  test("isPointInSector_50") {
    assert(!isPointInSector(new Point2(0, 0), toRad(10), toRad(20), new Point2(5, -5)))
  }

  test("dotProduct_00") {
    11.0 shouldBe (new Vec2(1, 2)dotProduct new Vec2(3, 4)) +- 0.00001
  }

  test("dotProduct_01") {
    4.0 shouldBe (new Vec2(4, -2)dotProduct new Vec2(3, 4)) +- 0.000001
  }

  test("enclosedAngle_00") {
    45.0 shouldBe toDeg(normalize(new Vec2(10, 0).enclosedAngle(new Vec2(10, 10)))) +- 0.000001
  }
  test("enclosedAngle_01") {
    -135.0 shouldBe toDeg(normalize(new Vec2(10, 0).enclosedAngle(new Vec2(-10, -10)))) +- 0.000001
  }
  test("enclosedAngle_03") {
    -45.0 shouldBe toDeg(normalize(new Vec2(10, 0).enclosedAngle(new Vec2(10, -10)))) +- 0.000001
  }
  test("enclosedAngle_04") {
    135.0 shouldBe toDeg(normalize(new Vec2(10, 0).enclosedAngle(new Vec2(-10, 10)))) +- 0.000001
  }

  test("enclosedAngle_05") {
    -120.96375653207352 shouldBe toDeg(normalize(new Vec2(20, -5).enclosedAngle(new Vec2(-20, -20)))) +- 0.000001
  }
  test("enclosedAngle_06") {
    120.96375653207352 shouldBe toDeg(normalize(new Vec2(-20, -20).enclosedAngle(new Vec2(20, -5)))) +- 0.000001
  }

  test("enclosedAngle_07") {
    30.51023740611555 shouldBe toDeg(normalize(new Vec2(-15, -55).enclosedAngle(new Vec2(15, -55)))) +- 0.000001
  }
  test("enclosedAngle_08") {
    -30.51023740611555 shouldBe toDeg(normalize(new Vec2(15, -55).enclosedAngle(new Vec2(-15, -55)))) +- 0.000001
  }

  import scala.math._
  
  test("pointCircleDistance_A02") {
    1.0 shouldBe pointCircleDistance(1.0, new Point2(0, 0), toRad(45)) +- 0.000001
  }

  test("pointCircleDistance_A03") {
    1.0 shouldBe pointCircleDistance(1.0, new Point2(0, 0), toRad(90)) +- 0.000001
  }

  test("pointCircleDistance_A04") {
    1.0 shouldBe pointCircleDistance(1.0, new Point2(0, 0), toRad(136)) +- 0.000001
  }

  test("pointCircleDistance_A05") {
    1.0 shouldBe pointCircleDistance(1.0, new Point2(0, 0), toRad(180)) +- 0.000001
  }

  test("pointCircleDistance_A06") {
    1.0 shouldBe pointCircleDistance(1.0, new Point2(0, 0), toRad(200)) +- 0.000001
  }

  test("pointCircleDistance_A07") {
    1.0 shouldBe pointCircleDistance(1.0, new Point2(0, 0), toRad(270)) +- 0.000001
  }

  test("pointCircleDistance_A08") {
    1.0 shouldBe pointCircleDistance(1.0, new Point2(0, 0), toRad(300)) +- 0.000001
  }

  test("pointCircleDistance_BI01") {
    0.5 * sqrt(2) shouldBe pointCircleDistance(1.0, new Point2(0.5, 0.5), toRad(135)) +- 0.000001
  }

  test("pointCircleDistance_BI02") {
    sqrt(3.0/4.0) - 0.5 shouldBe pointCircleDistance(1.0, new Point2(0.5, 0.5), toRad(90)) +- 0.000001
  }

  test("pointCircleDistance_BI03") {
    val r = 1.0
    r - r / 2 * sqrt(2) shouldBe pointCircleDistance(r, new Point2(r / 2, r / 2), toRad(45)) +- 0.000001
  }

  test("pointCircleDistance_BI03a") {
    val r = 2.0
    r - r / 2 * sqrt(2) shouldBe pointCircleDistance(r, new Point2(r / 2, r / 2), toRad(45)) +- 0.000001
  }

  test("pointCircleDistance_BI03c") {
    val r = 2.34
    r - r / 2 * sqrt(2) shouldBe pointCircleDistance(r, new Point2(r / 2, r / 2), toRad(45)) +- 0.000001
  }

  test("pointCircleDistance_BI04") {
    sqrt(3.0/4.0) - 0.5 shouldBe pointCircleDistance(1.0, new Point2(0.5, 0.5), toRad(0)) +- 0.000001
  }

  test("pointCircleDistance_BI05") {
    0.5 * sqrt(2) shouldBe pointCircleDistance(1.0, new Point2(0.5, 0.5), toRad(-45)) +- 0.000001
  }

  test("pointCircleDistance_BII01") {
    0.5 * sqrt(2) shouldBe pointCircleDistance(1.0, new Point2(-0.5, 0.5), toRad(45)) +- 0.000001
  }

  test("pointCircleDistance_BII02") {
    sqrt(3.0/4.0) - 0.5 shouldBe pointCircleDistance(1.0, new Point2(-0.5, 0.5), toRad(90)) +- 0.000001
  }

  test("pointCircleDistance_BII03") {
    val r = 1.0
    r - r / 2 * sqrt(2) shouldBe pointCircleDistance(r, new Point2(-r / 2, r / 2), toRad(135)) +- 0.000001
  }

  test("pointCircleDistance_BII03a") {
    val r = 2.0
    r - r / 2 * sqrt(2) shouldBe pointCircleDistance(r, new Point2(-r / 2, r / 2), toRad(135)) +- 0.000001
  }

  test("pointCircleDistance_BII03c") {
    val r = 2.34
    r - r / 2 * sqrt(2) shouldBe pointCircleDistance(r, new Point2(-r / 2, r / 2), toRad(135)) +- 0.000001
  }

  test("pointCircleDistance_BII04") {
    sqrt(3.0/4.0) - 0.5 shouldBe pointCircleDistance(1.0, new Point2(-0.5, 0.5), toRad(180)) +- 0.000001
  }

  test("pointCircleDistance_BII05") {
    0.5 * sqrt(2) shouldBe pointCircleDistance(1.0, new Point2(-0.5, 0.5), toRad(225)) +- 0.000001
  }

  test("pointCircleDistance_BIII01") {
    0.5 * sqrt(2) shouldBe pointCircleDistance(1.0, new Point2(-0.5, -0.5), toRad(-45)) +- 0.000001
  }

  test("pointCircleDistance_BIII02") {
    sqrt(3.0/4.0) - 0.5 shouldBe pointCircleDistance(1.0, new Point2(-0.5, -0.5), toRad(-90)) +- 0.000001
  }

  test("pointCircleDistance_BIII03") {
    val r = 1.0
    r - r / 2 * sqrt(2) shouldBe pointCircleDistance(r, new Point2(-r / 2, -r / 2), toRad(-135)) +- 0.000001
  }

  test("pointCircleDistance_BIII04") {
    sqrt(3.0/4.0) - 0.5 shouldBe pointCircleDistance(1.0, new Point2(-0.5, -0.5), toRad(180)) +- 0.000001
  }

  test("pointCircleDistance_BIII05") {
    0.5 * sqrt(2) shouldBe pointCircleDistance(1.0, new Point2(-0.5, -0.5), toRad(135)) +- 0.000001
  }

  test("pointCircleDistance_BIV01") {
    0.5 * sqrt(2) shouldBe pointCircleDistance(1.0, new Point2(0.5, -0.5), toRad(45)) +- 0.000001
  }

  test("pointCircleDistance_BIV02") {
    sqrt(3.0/4.0) - 0.5 shouldBe pointCircleDistance(1.0, new Point2(0.5, -0.5), toRad(0)) +- 0.000001
  }

  test("pointCircleDistance_BIV03") {
    val r = 1.0
    r - r / 2 * sqrt(2) shouldBe pointCircleDistance(r, new Point2(r / 2, -r / 2), toRad(-45)) +- 0.000001
  }

  test("pointCircleDistance_BIV04") {
    sqrt(3.0/4.0) - 0.5 shouldBe pointCircleDistance(1.0, new Point2(0.5, -0.5), toRad(-90)) +- 0.000001
  }

  test("pointCircleDistance_BIV05") {
    0.5 * sqrt(2) shouldBe pointCircleDistance(1.0, new Point2(0.5, -0.5), toRad(-135)) +- 0.000001
  }

  test("pointCircleDistance_CI01") {
    sqrt(3.0/4.0) + 0.5 shouldBe pointCircleDistance(1.0, new Point2(0.5, 0.5), toRad(180)) +- 0.000001
  }

  test("pointCircleDistance_CI02") {
    1 + 0.5 * sqrt(2) shouldBe pointCircleDistance(1.0, new Point2(0.5, 0.5), toRad(-135)) +- 0.000001
  }

  test("pointCircleDistance_CI03") {
    sqrt(3.0/4.0) + 0.5 shouldBe pointCircleDistance(1.0, new Point2(0.5, 0.5), toRad(-90)) +- 0.000001
  }

  test("pointCircleDistance_CII01") {
    sqrt(3.0/4.0) + 0.5 shouldBe pointCircleDistance(1.0, new Point2(-0.5, 0.5), toRad(0)) +- 0.000001
  }

  test("pointCircleDistance_CII02") {
    1 + 0.5 * sqrt(2) shouldBe pointCircleDistance(1.0, new Point2(-0.5, 0.5), toRad(-45)) +- 0.000001
  }

  test("pointCircleDistance_CII03") {
    sqrt(3.0/4.0) + 0.5 shouldBe pointCircleDistance(1.0, new Point2(-0.5, 0.5), toRad(-90)) +- 0.000001
  }

  test("pointCircleDistance_CIII01") {
   sqrt(3.0/4.0) + 0.5 shouldBe pointCircleDistance(1.0, new Point2(-0.5, -0.5), toRad(0)) +- 0.000001
  }

  test("pointCircleDistance_CIII02") {
    1 + 0.5 * sqrt(2) shouldBe pointCircleDistance(1.0, new Point2(-0.5, -0.5), toRad(45)) +- 0.000001
  }

  test("pointCircleDistance_CIII03") {
    sqrt(3.0/4.0) + 0.5 shouldBe pointCircleDistance(1.0, new Point2(-0.5, -0.5), toRad(90)) +- 0.000001
  }

  test("pointCircleDistance_CIV01") {
    sqrt(3.0/4.0) + 0.5 shouldBe pointCircleDistance(1.0, new Point2(0.5, -0.5), toRad(90)) +- 0.000001
  }

  test("pointCircleDistance_CIV02") {
    1 + 0.5 * sqrt(2) shouldBe pointCircleDistance(1.0, new Point2(0.5, -0.5), toRad(135)) +- 0.000001
  }

  test("pointCircleDistance_CIV03") {
    sqrt(3.0/4.0) + 0.5 shouldBe pointCircleDistance(1.0, new Point2(0.5, -0.5), toRad(180)) +- 0.000001
  }

  test("isQuartI_00") {
    assert(new Point2(10, 0).isQuartI)
  }
  test("isQuartI_01") {
    assert(new Point2(10, 10).isQuartI)
  }
  test("isQuartII_00") {
    assert(new Point2(0, 10).isQuartII)
  }
  test("isQuartII_01") {
    assert(new Point2(-10, 10).isQuartII)
  }
  test("isQuartIII_00") {
    assert(new Point2(-10, 0).isQuartIII)
  }
  test("isQuartIII_01") {
    assert(new Point2(-10, -10).isQuartIII)
  }
  test("isQuartIV_00") {
    assert(new Point2(0, -10).isQuartIV)
  }
  test("isQuartIV_01") {
    assert(new Point2(10, -10).isQuartIV)
  }

}

