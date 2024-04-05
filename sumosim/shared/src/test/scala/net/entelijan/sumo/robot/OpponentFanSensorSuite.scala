package net.entelijan.sumo.robot

import net.entelijan.sumo.commons._
import net.entelijan.sumo.util.{TrigUtil, VecUtil}
import utest._

object OpponentFanSensorSuite extends TestSuite with VecUtil with TrigUtil {

  private def createRobot(
      x: Double,
      y: Double,
      dir: Double
  ): Robot[NullSensor, Unit] = {
    new Robot[NullSensor, Unit] {
      def xpos: Double = x

      def ypos: Double = y

      def direction: Double = dir

      def adjust(xpos: Double, ypos: Double, direction: Double): Unit = {}

      /** Moves the robot according to the values created by the Controller
        */
      override def move(value: Unit): Unit = ???

      /** Returns the sensor of the current robot containing the values
        * according to the robots current position and the position of the
        * opponent
        */
      override def sensor: NullSensor = ???

      override def name: String = ???
    }
  }

  private def createSensor(
      x: Double,
      y: Double,
      dir: Double,
      oppx: Double,
      oppy: Double
  ): OpponentFanSensor = {
    new OpponentFanSensorImpl with TrigUtil {
      override def fullOpeningAngle: Double = toRad(20.0)

      def selfRobot: Robot[NullSensor, Unit] = createRobot(x, y, dir)

      def opponentRobot: Robot[NullSensor, Unit] = createRobot(oppx, oppy, 0.0)
    }
  }

  def tests: Tests = utest.Tests {
    test("opponentInSector_01") {
      assert(UNDEF == createSensor(0, 0, toRad(10.0), 5, -5).opponentInSector)
    }
    test("opponentInSector_02") {
      assert(
        RIGHT == createSensor(0, 0, toRad(10.0), 60, 0.001).opponentInSector
      )
    }
    test("opponentInSector_03") {
      assert(RIGHT == createSensor(0, 0, toRad(10.0), 55, 5).opponentInSector)
    }
    test("opponentInSector_04") {
      assert(CENTER == createSensor(0, 0, toRad(10.0), 65, 10).opponentInSector)
    }
    test("opponentInSector_05") {
      assert(CENTER == createSensor(0, 0, toRad(10.0), 30, 5).opponentInSector)
    }
    test("opponentInSector_06") {
      assert(LEFT == createSensor(0, 0, toRad(10.0), 45, 15).opponentInSector)
    }
    test("opponentInSector_07") {
      assert(LEFT == createSensor(0, 0, toRad(10.0), 60, 20).opponentInSector)
    }
    test("opponentInSector_08") {
      assert(UNDEF == createSensor(0, 0, toRad(10.0), 60, 25).opponentInSector)
    }
    test("opponentInSector_09") {
      assert(UNDEF == createSensor(0, 0, toRad(10.0), -10, 10).opponentInSector)
    }
    test("opponentInSector_10") {
      assert(
        UNDEF == createSensor(0, 0, toRad(10.0), -10, -10).opponentInSector
      )
    }
    test("opponentInSector_11") {
      assert(UNDEF == createSensor(0, 0, toRad(10.0), -55, -5).opponentInSector)
    }
    test("opponentInSector_12") {
      assert(
        UNDEF == createSensor(0, 0, toRad(10.0), -45, -15).opponentInSector
      )
    }
  }
}
