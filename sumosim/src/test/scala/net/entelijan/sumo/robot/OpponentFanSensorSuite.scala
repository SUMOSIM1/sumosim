package net.entelijan.sumo.robot

import net.entelijan.util._
import net.entelijan.sumo.core._
import net.entelijan.sumo.commons._
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Matchers

class OpponentFanSensorSuite extends FunSuite with Matchers with VecUtil with TrigUtil {

  private def createRobot(x: Double, y: Double, dir: Double): Robot = {
    new Robot {
      def xpos = x
      def ypos = y
      def direction = dir
      def adjust(xpos: Double, ypos: Double, direction: Double) = {}
      def takeStep() = {}
      def name = "Dummy"
    }
  }

  private def createSensor(x: Double, y: Double, dir: Double, oppx: Double, oppy: Double): OpponentFanSensor = {
    new OpponentFanSensorImpl with TrigUtil {
      override def fullOpeningAngle = toRad(20.0)
      def selfRobot = createRobot(x, y, dir)
      def opponentRobot = createRobot(oppx, oppy, 0.0)
    }
  }

  test("opponentInSector_01") {
    UNDEF shouldBe createSensor(0, 0, toRad(10.0), 5, -5).opponentInSector
  }
  test("opponentInSector_02") {
    RIGHT shouldBe createSensor(0, 0, toRad(10.0), 60, 0.001).opponentInSector
  }
  test("opponentInSector_03") {
    RIGHT shouldBe createSensor(0, 0, toRad(10.0), 55, 5).opponentInSector
  }
  test("opponentInSector_04") {
    CENTER shouldBe createSensor(0, 0, toRad(10.0), 65, 10).opponentInSector
  }
  test("opponentInSector_05") {
    CENTER shouldBe createSensor(0, 0, toRad(10.0), 30, 5).opponentInSector
  }
  test("opponentInSector_06") {
    LEFT shouldBe createSensor(0, 0, toRad(10.0), 45, 15).opponentInSector
  }
  test("opponentInSector_07") {
    LEFT shouldBe createSensor(0, 0, toRad(10.0), 60, 20).opponentInSector
  }
  test("opponentInSector_08") {
    UNDEF shouldBe createSensor(0, 0, toRad(10.0), 60, 25).opponentInSector
  }
  test("opponentInSector_09") {
    UNDEF shouldBe createSensor(0, 0, toRad(10.0), -10, 10).opponentInSector
  }
  test("opponentInSector_10") {
    UNDEF shouldBe createSensor(0, 0, toRad(10.0), -10, -10).opponentInSector
  }
  test("opponentInSector_11") {
    UNDEF shouldBe createSensor(0, 0, toRad(10.0), -55, -5).opponentInSector
  }
  test("opponentInSector_12") {
    UNDEF shouldBe createSensor(0, 0, toRad(10.0), -45, -15).opponentInSector
  }


  
  
}

