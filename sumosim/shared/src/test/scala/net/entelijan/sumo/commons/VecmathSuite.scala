package net.entelijan.sumo.commons

import net.entelijan.sumo.util.{Point2, TrigUtil, Vec2, VecUtil}

import scala.math._
import utest._

object VecmathSuite extends TestSuite with VecUtil with TrigUtil {

  def tests: Tests = utest.Tests {

    "testDirection001" - {
      val v1 = new Vec2(1.0, 1.0)
      assert(equals(toRad(45), v1.direction))
    }

    "testDirection002" - {
      val v1 = new Vec2(1.0, 2.0)
      assert(equals(toRad(63.43494882292201), v1.direction))
    }

    "testDirection003" - {
      val v1 = new Vec2(0, 1.0)
      assert(equals(toRad(90), v1.direction))
    }

    "testDirection003a" - {
      val v1 = new Vec2(0, -1.0)
      assert(equals(toRad(-90), v1.direction))
    }

    "testDirection004" - {
      val v1 = new Vec2(-1.0, 1.0)
      assert(equals(toRad(135), v1.direction))
    }

    "testDirection005" - {
      val v1 = new Vec2(1.0, 0.0)
      assert(equals(toRad(0), v1.direction))
    }

    "testDirection006" - {
      val v1 = new Vec2(-1.0, -1.0)
      assert(equals(toRad(-135), v1.direction))
    }

    "testDirection007" - {
      val v1 = new Vec2(1.0, -1.0)
      assert(toRad(-45).equals(v1.direction))
    }

    "testRotate001" - {
      val v1 = new Vec2(1.0, 1.0)
      assert(90.0.equals(toDeg(v1.rot(toRad(45)).direction)))
    }

    "testRotate002" - {
      val v1 = new Vec2(1.0, 1.0)
      assert(equals(0.0, testRound(v1.rot(toRad(-45)).direction)))
    }

    "testRotate003" - {
      val v1 = new Vec2(1.0, 1.0)
      assert(equals(135.0, toDeg(v1.rot(toRad(90)).direction)))
    }

    "testRotate004" - {
      val v1 = new Vec2(1.0, 1.0)
      assert(equals(-135.0, toDeg(v1.rot(toRad(180)).direction)))
    }
    "testRotate005" - {
      val v1 = new Vec2(1.0, 1.0)
      assert(equals(testRound(-45.0), toDeg(v1.rot(toRad(270)).direction)))
    }

    "testEquals001" - {
      val v1 = new Vec2(1.0, 1.0)
      val v2 = new Vec2(1.0, 1.0)
      assert(equals(v1, v2))
      assert(equals(v1, v1))
      assert(equals(v2, v2))
    }

    "testEquals002" - {
      val v1 = new Vec2(1.0, 1.0)
      val v2 = new Vec2(1.1, 1.0)
      assert(!equals(v1, v2))
    }

    "testEquals001a" - {
      val v1 = new Point2(1.0, 1.0)
      val v2 = new Point2(1.0, 1.0)
      assert(equals(v1, v2))
      assert(equals(v1, v1))
      assert(equals(v2, v2))
    }

    "testEquals002b" - {
      val v1 = new Point2(1.0, 1.0)
      val v2 = new Point2(1.1, 1.0)
      assert(!equals(v1, v2))
    }

    "testNorm" - {
      assert(equals(90, normDeg(90)))
      assert(equals(180, normDeg(180)))
      assert(equals(-170, normDeg(190)))
      assert(equals(170, normDeg(-190)))
      assert(equals(-45, normDeg(-45)))
      assert(equals(-90, normDeg(270)))
    }

    "testOctal0" - {
      assert(equals(0, octal(0.0)))
      assert(equals(0, octal(Pi / 8 - smallDouble)))
      assert(equals(0, octal(-Pi / 8 + smallDouble)))
    }

    "testOctal1" - {
      assert(equals(1, octal(Pi / 4)))
      assert(equals(1, octal(3 * Pi / 8 - smallDouble)))
      assert(equals(1, octal(Pi / 8 + smallDouble)))
    }

    "testOctal2" - {
      assert(equals(2, octal(Pi / 2)))
      assert(equals(2, octal(3 * Pi / 8 + smallDouble)))
      val x1 = 5 * Pi / 8
      val x2 = x1 - smallDouble
      assert(equals(2, octal(x2)))
    }

    "testOctal3" - {
      assert(equals(3, octal(3 * Pi / 4)))
      assert(equals(3, octal(7 * Pi / 8 - smallDouble)))
      assert(equals(3, octal(5 * Pi / 8 + smallDouble)))
    }

    "testOctal4" - {
      assert(equals(4, octal(Pi)))
      assert(equals(4, octal(-Pi)))
      assert(equals(4, octal(-7 * Pi / 8 - smallDouble)))
      assert(equals(4, octal(7 * Pi / 8 + smallDouble)))
    }

    "testOctal5" - {
      assert(equals(5, octal(-3 * Pi / 4)))
      assert(equals(5, octal(-5 * Pi / 8 - smallDouble)))
      assert(equals(5, octal(-7 * Pi / 8 + smallDouble)))
    }

    "testOctal6" - {
      assert(equals(6, octal(-Pi / 2)))
      assert(equals(6, octal(-3 * Pi / 8 - smallDouble)))
      assert(equals(6, octal(-5 * Pi / 8 + smallDouble)))
    }

    "testOctal7" - {
      assert(equals(7, octal(-Pi / 4)))
      assert(equals(7, octal(-3 * Pi / 8 + smallDouble)))
      assert(equals(7, octal(-Pi / 8 - smallDouble)))
    }

    "isPointInSector_01" - {
      assert(
        isPointInSector(
          new Point2(0, 0),
          toRad(0),
          toRad(20),
          new Point2(500, 100)
        )
      )
    }
    "isPointInSector_02" - {
      assert(
        isPointInSector(
          new Point2(0, 0),
          toRad(0),
          toRad(20),
          new Point2(500, -100)
        )
      )
    }
    "isPointInSector_03" - {
      assert(
        !isPointInSector(
          new Point2(0, 0),
          toRad(0),
          toRad(20),
          new Point2(500, 200)
        )
      )
    }
    "isPointInSector_04" - {
      assert(
        !isPointInSector(
          new Point2(0, 0),
          toRad(0),
          toRad(20),
          new Point2(500, -200)
        )
      )
    }
    "isPointInSector_05" - {
      assert(
        !isPointInSector(
          new Point2(0, 0),
          toRad(0),
          toRad(20),
          new Point2(500, 300)
        )
      )
    }
    "isPointInSector_06" - {
      assert(
        !isPointInSector(
          new Point2(0, 0),
          toRad(0),
          toRad(20),
          new Point2(500, -300)
        )
      )
    }

    "isPointInSector_10" - {
      assert(
        !isPointInSector(
          new Point2(-10, 5),
          toRad(45),
          toRad(20),
          new Point2(10, 10)
        )
      )
    }
    "isPointInSector_11" - {
      assert(
        isPointInSector(
          new Point2(-10, 5),
          toRad(45),
          toRad(20),
          new Point2(10, 20)
        )
      )
    }
    "isPointInSector_12" - {
      assert(
        isPointInSector(
          new Point2(-10, 5),
          toRad(45),
          toRad(20),
          new Point2(10, 30)
        )
      )
    }
    "isPointInSector_13" - {
      assert(
        isPointInSector(
          new Point2(-10, 5),
          toRad(45),
          toRad(20),
          new Point2(10, 40)
        )
      )
    }
    "isPointInSector_14" - {
      assert(
        !isPointInSector(
          new Point2(-10, 5),
          toRad(45),
          toRad(20),
          new Point2(10, 50)
        )
      )
    }
    "isPointInSector_15" - {
      assert(
        !isPointInSector(
          new Point2(-10, 5),
          toRad(45),
          toRad(20),
          new Point2(10, 60)
        )
      )
    }

    "isPointInSector_20" - {
      assert(
        !isPointInSector(
          new Point2(-10, 5),
          toRad(45),
          toRad(20),
          new Point2(-35, 0)
        )
      )
    }
    "isPointInSector_21" - {
      assert(
        !isPointInSector(
          new Point2(-10, 5),
          toRad(45),
          toRad(20),
          new Point2(-35, -10)
        )
      )
    }
    "isPointInSector_22" - {
      assert(
        !isPointInSector(
          new Point2(-10, 5),
          toRad(45),
          toRad(20),
          new Point2(-35, -20)
        )
      )
    }
    "isPointInSector_23" - {
      assert(
        !isPointInSector(
          new Point2(-10, 5),
          toRad(45),
          toRad(20),
          new Point2(-35, -30)
        )
      )
    }
    "isPointInSector_24" - {
      assert(
        !isPointInSector(
          new Point2(-10, 5),
          toRad(45),
          toRad(20),
          new Point2(-35, -40)
        )
      )
    }
    "isPointInSector_25" - {
      assert(
        !isPointInSector(
          new Point2(-10, 5),
          toRad(45),
          toRad(20),
          new Point2(-35, -50)
        )
      )
    }

    "isPointInSector_30" - {
      assert(
        !isPointInSector(
          new Point2(-10, -15),
          toRad(-10),
          toRad(20),
          new Point2(30, 0)
        )
      )
    }
    "isPointInSector_31" - {
      assert(
        isPointInSector(
          new Point2(-10, -15),
          toRad(-10),
          toRad(20),
          new Point2(30, -10)
        )
      )
    }
    "isPointInSector_32" - {
      assert(
        isPointInSector(
          new Point2(-10, -15),
          toRad(-10),
          toRad(20),
          new Point2(30, -20)
        )
      )
    }
    "isPointInSector_33" - {
      assert(
        isPointInSector(
          new Point2(-10, -15),
          toRad(-10),
          toRad(20),
          new Point2(30, -30)
        )
      )
    }
    "isPointInSector_34" - {
      assert(
        !isPointInSector(
          new Point2(-10, -15),
          toRad(-10),
          toRad(20),
          new Point2(30, -40)
        )
      )
    }
    "isPointInSector_35" - {
      assert(
        !isPointInSector(
          new Point2(-10, -15),
          toRad(-10),
          toRad(20),
          new Point2(30, -40)
        )
      )
    }

    "isPointInSector_40" - {
      assert(
        !isPointInSector(
          new Point2(-10, -15),
          toRad(-10),
          toRad(20),
          new Point2(-50, 10)
        )
      )
    }
    "isPointInSector_41" - {
      assert(
        !isPointInSector(
          new Point2(-10, -15),
          toRad(-10),
          toRad(20),
          new Point2(-50, 0)
        )
      )
    }
    "isPointInSector_42" - {
      assert(
        !isPointInSector(
          new Point2(-10, -15),
          toRad(-10),
          toRad(20),
          new Point2(-50, -10)
        )
      )
    }
    "isPointInSector_43" - {
      assert(
        !isPointInSector(
          new Point2(-10, -15),
          toRad(-10),
          toRad(20),
          new Point2(-50, -20)
        )
      )
    }
    "isPointInSector_44" - {
      assert(
        !isPointInSector(
          new Point2(-10, -15),
          toRad(-10),
          toRad(20),
          new Point2(-50, -30)
        )
      )
    }
    "isPointInSector_45" - {
      assert(
        !isPointInSector(
          new Point2(-10, -15),
          toRad(-10),
          toRad(20),
          new Point2(-50, -40)
        )
      )
    }
    "isPointInSector_46" - {
      assert(
        !isPointInSector(
          new Point2(-10, -15),
          toRad(-10),
          toRad(20),
          new Point2(-50, -50)
        )
      )
    }

    "isPointInSector_50" - {
      assert(
        !isPointInSector(
          new Point2(0, 0),
          toRad(10),
          toRad(20),
          new Point2(5, -5)
        )
      )
    }

    "dotProduct_00" - {
      assert(equals(11.0, new Vec2(1, 2).dotProduct(new Vec2(3, 4))))
    }

    "dotProduct_01" - {
      assert(equals(4.0, new Vec2(4, -2).dotProduct(new Vec2(3, 4))))
    }

    "enclosedAngle_00" - {
      assert(
        equals(
          45.0,
          toDeg(normalize(new Vec2(10, 0).enclosedAngle(new Vec2(10, 10))))
        )
      )
    }
    "enclosedAngle_01" - {
      assert(
        equals(
          -135.0,
          toDeg(normalize(new Vec2(10, 0).enclosedAngle(new Vec2(-10, -10))))
        )
      )
    }
    "enclosedAngle_03" - {
      assert(
        equals(
          -45.0,
          toDeg(normalize(new Vec2(10, 0).enclosedAngle(new Vec2(10, -10))))
        )
      )
    }
    "enclosedAngle_04" - {
      assert(
        equals(
          135.0,
          toDeg(normalize(new Vec2(10, 0).enclosedAngle(new Vec2(-10, 10))))
        )
      )
    }

    "enclosedAngle_05" - {
      assert(
        equals(
          -120.96375653207352,
          toDeg(normalize(new Vec2(20, -5).enclosedAngle(new Vec2(-20, -20))))
        )
      )
    }
    "enclosedAngle_06" - {
      assert(
        equals(
          120.96375653207352,
          toDeg(normalize(new Vec2(-20, -20).enclosedAngle(new Vec2(20, -5))))
        )
      )
    }

    "enclosedAngle_07" - {
      assert(
        equals(
          30.51023740611555,
          toDeg(normalize(new Vec2(-15, -55).enclosedAngle(new Vec2(15, -55))))
        )
      )
    }
    "enclosedAngle_08" - {
      assert(
        equals(
          -30.51023740611555,
          toDeg(normalize(new Vec2(15, -55).enclosedAngle(new Vec2(-15, -55))))
        )
      )
    }

    "pointCircleDistance_A02" - {
      assert(equals(1.0, pointCircleDistance(1.0, new Point2(0, 0), toRad(45))))
    }

    "pointCircleDistance_A03" - {
      assert(equals(1.0, pointCircleDistance(1.0, new Point2(0, 0), toRad(90))))
    }

    "pointCircleDistance_A04" - {
      assert(
        equals(1.0, pointCircleDistance(1.0, new Point2(0, 0), toRad(136)))
      )
    }

    "pointCircleDistance_A05" - {
      assert(
        equals(1.0, pointCircleDistance(1.0, new Point2(0, 0), toRad(180)))
      )
    }

    "pointCircleDistance_A06" - {
      assert(
        equals(1.0, pointCircleDistance(1.0, new Point2(0, 0), toRad(200)))
      )
    }

    "pointCircleDistance_A07" - {
      assert(
        equals(1.0, pointCircleDistance(1.0, new Point2(0, 0), toRad(270)))
      )
    }

    "pointCircleDistance_A08" - {
      assert(
        equals(1.0, pointCircleDistance(1.0, new Point2(0, 0), toRad(300)))
      )
    }

    "pointCircleDistance_BI01" - {
      assert(
        equals(
          0.5 * sqrt(2),
          pointCircleDistance(1.0, new Point2(0.5, 0.5), toRad(135))
        )
      )
    }

    "pointCircleDistance_BI02" - {
      assert(
        equals(
          sqrt(3.0 / 4.0) - 0.5,
          pointCircleDistance(1.0, new Point2(0.5, 0.5), toRad(90))
        )
      )
    }

    "pointCircleDistance_BI03" - {
      val r = 1.0
      assert(
        equals(
          r - r / 2 * sqrt(2),
          pointCircleDistance(r, new Point2(r / 2, r / 2), toRad(45))
        )
      )
    }

    "pointCircleDistance_BI03a" - {
      val r = 2.0
      assert(
        equals(
          r - r / 2 * sqrt(2),
          pointCircleDistance(r, new Point2(r / 2, r / 2), toRad(45))
        )
      )
    }

    "pointCircleDistance_BI03c" - {
      val r = 2.34
      assert(
        equals(
          r - r / 2 * sqrt(2),
          pointCircleDistance(r, new Point2(r / 2, r / 2), toRad(45))
        )
      )
    }

    "pointCircleDistance_BI04" - {
      assert(
        equals(
          sqrt(3.0 / 4.0) - 0.5,
          pointCircleDistance(1.0, new Point2(0.5, 0.5), toRad(0))
        )
      )
    }

    "pointCircleDistance_BI05" - {
      assert(
        equals(
          0.5 * sqrt(2),
          pointCircleDistance(1.0, new Point2(0.5, 0.5), toRad(-45))
        )
      )
    }

    "pointCircleDistance_BII01" - {
      assert(
        equals(
          0.5 * sqrt(2),
          pointCircleDistance(1.0, new Point2(-0.5, 0.5), toRad(45))
        )
      )
    }

    "pointCircleDistance_BII02" - {
      assert(
        equals(
          sqrt(3.0 / 4.0) - 0.5,
          pointCircleDistance(1.0, new Point2(-0.5, 0.5), toRad(90))
        )
      )
    }

    "pointCircleDistance_BII03" - {
      val r = 1.0
      assert(
        equals(
          r - r / 2 * sqrt(2),
          pointCircleDistance(r, new Point2(-r / 2, r / 2), toRad(135))
        )
      )
    }

    "pointCircleDistance_BII03a" - {
      val r = 2.0
      assert(
        equals(
          r - r / 2 * sqrt(2),
          pointCircleDistance(r, new Point2(-r / 2, r / 2), toRad(135))
        )
      )
    }

    "pointCircleDistance_BII03c" - {
      val r = 2.34
      assert(
        equals(
          r - r / 2 * sqrt(2),
          pointCircleDistance(r, new Point2(-r / 2, r / 2), toRad(135))
        )
      )
    }

    "pointCircleDistance_BII04" - {
      assert(
        equals(
          sqrt(3.0 / 4.0) - 0.5,
          pointCircleDistance(1.0, new Point2(-0.5, 0.5), toRad(180))
        )
      )
    }

    "pointCircleDistance_BII05" - {
      assert(
        equals(
          0.5 * sqrt(2),
          pointCircleDistance(1.0, new Point2(-0.5, 0.5), toRad(225))
        )
      )
    }

    "pointCircleDistance_BIII01" - {
      assert(
        equals(
          0.5 * sqrt(2),
          pointCircleDistance(1.0, new Point2(-0.5, -0.5), toRad(-45))
        )
      )
    }

    "pointCircleDistance_BIII02" - {
      assert(
        equals(
          sqrt(3.0 / 4.0) - 0.5,
          pointCircleDistance(1.0, new Point2(-0.5, -0.5), toRad(-90))
        )
      )
    }

    "pointCircleDistance_BIII03" - {
      val r = 1.0
      assert(
        equals(
          r - r / 2 * sqrt(2),
          pointCircleDistance(r, new Point2(-r / 2, -r / 2), toRad(-135))
        )
      )
    }

    "pointCircleDistance_BIII04" - {
      assert(
        equals(
          sqrt(3.0 / 4.0) - 0.5,
          pointCircleDistance(1.0, new Point2(-0.5, -0.5), toRad(180))
        )
      )
    }

    "pointCircleDistance_BIII05" - {
      assert(
        equals(
          0.5 * sqrt(2),
          pointCircleDistance(1.0, new Point2(-0.5, -0.5), toRad(135))
        )
      )
    }

    "pointCircleDistance_BIV01" - {
      assert(
        equals(
          0.5 * sqrt(2),
          pointCircleDistance(1.0, new Point2(0.5, -0.5), toRad(45))
        )
      )
    }

    "pointCircleDistance_BIV02" - {
      assert(
        equals(
          sqrt(3.0 / 4.0) - 0.5,
          pointCircleDistance(1.0, new Point2(0.5, -0.5), toRad(0))
        )
      )
    }

    "pointCircleDistance_BIV03" - {
      val r = 1.0
      assert(
        equals(
          r - r / 2 * sqrt(2),
          pointCircleDistance(r, new Point2(r / 2, -r / 2), toRad(-45))
        )
      )
    }

    "pointCircleDistance_BIV04" - {
      assert(
        equals(
          sqrt(3.0 / 4.0) - 0.5,
          pointCircleDistance(1.0, new Point2(0.5, -0.5), toRad(-90))
        )
      )
    }

    "pointCircleDistance_BIV05" - {
      assert(
        equals(
          0.5 * sqrt(2),
          pointCircleDistance(1.0, new Point2(0.5, -0.5), toRad(-135))
        )
      )
    }

    "pointCircleDistance_CI01" - {
      assert(
        equals(
          sqrt(3.0 / 4.0) + 0.5,
          pointCircleDistance(1.0, new Point2(0.5, 0.5), toRad(180))
        )
      )
    }

    "pointCircleDistance_CI02" - {
      assert(
        equals(
          1 + 0.5 * sqrt(2),
          pointCircleDistance(1.0, new Point2(0.5, 0.5), toRad(-135))
        )
      )
    }

    "pointCircleDistance_CI03" - {
      assert(
        equals(
          sqrt(3.0 / 4.0) + 0.5,
          pointCircleDistance(1.0, new Point2(0.5, 0.5), toRad(-90))
        )
      )
    }

    "pointCircleDistance_CII01" - {
      assert(
        equals(
          sqrt(3.0 / 4.0) + 0.5,
          pointCircleDistance(1.0, new Point2(-0.5, 0.5), toRad(0))
        )
      )
    }

    "pointCircleDistance_CII02" - {
      assert(
        equals(
          1 + 0.5 * sqrt(2),
          pointCircleDistance(1.0, new Point2(-0.5, 0.5), toRad(-45))
        )
      )
    }

    "pointCircleDistance_CII03" - {
      assert(
        equals(
          sqrt(3.0 / 4.0) + 0.5,
          pointCircleDistance(1.0, new Point2(-0.5, 0.5), toRad(-90))
        )
      )
    }

    "pointCircleDistance_CIII01" - {
      assert(
        equals(
          sqrt(3.0 / 4.0) + 0.5,
          pointCircleDistance(1.0, new Point2(-0.5, -0.5), toRad(0))
        )
      )
    }

    "pointCircleDistance_CIII02" - {
      assert(
        equals(
          1 + 0.5 * sqrt(2),
          pointCircleDistance(1.0, new Point2(-0.5, -0.5), toRad(45))
        )
      )
    }

    "pointCircleDistance_CIII03" - {
      assert(
        equals(
          sqrt(3.0 / 4.0) + 0.5,
          pointCircleDistance(1.0, new Point2(-0.5, -0.5), toRad(90))
        )
      )
    }

    "pointCircleDistance_CIV01" - {
      assert(
        equals(
          sqrt(3.0 / 4.0) + 0.5,
          pointCircleDistance(1.0, new Point2(0.5, -0.5), toRad(90))
        )
      )
    }

    "pointCircleDistance_CIV02" - {
      assert(
        equals(
          1 + 0.5 * sqrt(2),
          pointCircleDistance(1.0, new Point2(0.5, -0.5), toRad(135))
        )
      )
    }

    "pointCircleDistance_CIV03" - {
      assert(
        equals(
          sqrt(3.0 / 4.0) + 0.5,
          pointCircleDistance(1.0, new Point2(0.5, -0.5), toRad(180))
        )
      )
    }

    "isQuartI_00" - {
      assert(new Point2(10, 0).isQuartI)
    }
    "isQuartI_01" - {
      assert(new Point2(10, 10).isQuartI)
    }
    "isQuartII_00" - {
      assert(new Point2(0, 10).isQuartII)
    }
    "isQuartII_01" - {
      assert(new Point2(-10, 10).isQuartII)
    }
    "isQuartIII_00" - {
      assert(new Point2(-10, 0).isQuartIII)
    }
    "isQuartIII_01" - {
      assert(new Point2(-10, -10).isQuartIII)
    }
    "isQuartIV_00" - {
      assert(new Point2(0, -10).isQuartIV)
    }
    "isQuartIV_01" - {
      assert(new Point2(10, -10).isQuartIV)
    }
  }

  private def normDeg(deg: Int): Int = {
    round(toDeg(normalize(toRad(deg)))).asInstanceOf[Int]
  }

  private def smallDouble = 0.0001

  def equals(v1: Vec2, v2: Vec2): Boolean = {
    val a = new Vec2(testRound(v1.x), testRound(v1.y))
    val b = new Vec2(testRound(v2.x), testRound(v2.y))
    a == b
  }

  def equals(p1: Point2, p2: Point2): Boolean = {
    val a = new Point2(testRound(p1.xpos), testRound(p1.ypos))
    val b = new Point2(testRound(p2.xpos), testRound(p2.ypos))
    a == b
  }

  def equals(a: Double, b: Double): Boolean = {
    testRound(a) == testRound(b)
  }

  def testRound(v: Double): Double = {
    val r = 100000000.0
    round(v * r) / r
  }

}
