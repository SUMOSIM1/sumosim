package net.entelijan.sumo.sensor

import net.entelijan.sumo.commons.Robot
import net.entelijan.sumo.robot.{NullSensor, SimpleTeeBorderDistanceSensor}
import utest._

object SensorTests extends TestSuite {
  val tests: Tests = Tests {
    test("tee border") {
      test("01") {
        val sensor: SimpleTeeBorderDistanceSensor =
          new SimpleTeeBorderDistanceSensor() {
            override def selfRobot: TestRobot = TestRobot(0.0, 200.0, 0.0)

            override def randomAmount: Double = 0.0
          }
        val l = sensor.leftDistance.toInt
        val f = sensor.frontDistance.toInt
        val r = sensor.rightDistance.toInt
        assert(around(l, 200))
        assert(around(f, 370))
        assert(around(r, 600))
      }
    }
  }

  private def around(x: Double, p: Double): Boolean = {
    val e = 80
    x > (p - e) && x < (p + e)
  }

  case class TestRobot(
      xpos: Double,
      ypos: Double,
      direction: Double
  ) extends Robot[NullSensor, Unit] {

    /** Moves and rotates a robot to a certain position and direction Must not
      * be used by controllers
      */
    override def adjust(xpos: Double, ypos: Double, direction: Double): Unit = {
      // Nothing to do
    }

    /** Moves the robot according to the values created by the Controller
      */
    override def move(value: Unit): Unit = ???

    /** Returns the sensor of the current robot containing the values according
      * to the robots current position and the position of the opponent
      */
    override def sensor: NullSensor = ???

    override def name: String = ???
  }
}
