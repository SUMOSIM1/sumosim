package net.entelijan.sumo.robot

import scala.math.cos
import scala.math.sin

import net.entelijan.sumo.commons.OpponentAwareRobot
import net.entelijan.sumo.commons.Robot
import net.entelijan.util.Point2
import net.entelijan.util.TrigUtil
import net.entelijan.util.VecUtil

abstract class DiffDriveRobot[S](cntrl: DiffDriveController[S]) extends Robot with VecUtil with TrigUtil {

  def maxAccelerationFactor = 1.0
  def maxSpeedFactor = 1.0
  
  // Max velocity
  private val maxVelo = 10
  private val maxAccel = 5
  // Wheel distance
  private val wheelDistance = 50.0

  // Angle
  private var phi = 0.0
  // Actual position
  private var pos = new Point2(0, 0)

  private val leftWheel = new WheelMotor {
    override def maxWheelAccelerationFactor = maxAccelerationFactor
  }
  private val rightWheel = new WheelMotor

  def xpos = pos.xpos
  def ypos = pos.ypos
  def direction = normalize(phi)
  def adjust(xpos: Double, ypos: Double, direction: Double) {
    pos = new Point2(xpos, ypos)
    phi = direction
  }
  def name = cntrl.name

  def createSensor: S

  def takeStep() {

    import scala.math._

    val values = cntrl.takeStep(createSensor)
    rightWheel.set(values.rightVelo)
    leftWheel.set(values.leftVelo)

    // Calculate the new angle(phi) and position using kinematic equations
    val vr = rightWheel.get
    val vl = leftWheel.get
    if (vr == vl) {
      val x = pos.xpos + vr * cos(phi)
      val y = pos.ypos + vl * sin(phi)
      pos = new Point2(x, y)
    } else {
      val d = (wheelDistance * (vr + vl)) / (2 * (vr - vl))
      val b = sin((vr - vl) / wheelDistance + phi) - sin(phi)
      val a = cos((vr - vl) / wheelDistance + phi) - cos(phi)
      val x = pos.xpos + d * b
      val y = pos.ypos - d * a
      phi = phi + (vl - vr) / wheelDistance
      pos = new Point2(x, y)
    }
  }

  override def ready = cntrl.ready

}

trait DiffDriveController[S] {

  def name: String
  def takeStep(sensor: S): DiffDriveValues
  def ready = true

}

case class DiffDriveValues(rightVelo: Double, leftVelo: Double)

/**
 * A robot with a BorderDistanceSensor
 */
class BorderDistanceDiffDriveRobot(controller: DiffDriveController[BorderDistanceSensor]) extends DiffDriveRobot[BorderDistanceSensor](controller) {
  def createSensor: BorderDistanceSensor = new SimpleBorderDistanceSensor() {
    def selfRobot = BorderDistanceDiffDriveRobot.this
  }
}
class ExtendedBorderDistanceDiffDriveRobot(controller: DiffDriveController[ExtendedBorderDistanceSensor]) extends DiffDriveRobot[ExtendedBorderDistanceSensor](controller) {
  def createSensor: ExtendedBorderDistanceSensor = new SimpleBorderDistanceSensor() {
    def selfRobot = ExtendedBorderDistanceDiffDriveRobot.this
  }
}

class TeeBorderDistanceDiffDriveRobot(controller: DiffDriveController[TeeBorderDistanceSensor]) extends DiffDriveRobot[TeeBorderDistanceSensor](controller) {
  def createSensor: SimpleTeeBorderDistanceSensor = new SimpleTeeBorderDistanceSensor() {
    def selfRobot = TeeBorderDistanceDiffDriveRobot.this
  }
}

class ManualDiffDriveRobot(controller: DiffDriveController[NullSensor]) extends DiffDriveRobot[NullSensor](controller) {
	def createSensor = new NullSensor
}

class CombiSensorDiffDriveRobot(controller: DiffDriveController[CombiSensor]) extends DiffDriveRobot[CombiSensor](controller) with OpponentAwareRobot {
  var opponent: Robot = new Robot {
    def takeStep() = throw new IllegalStateException("You must set the opponent before you can run a CombiSensorDiffDriveRobot")
    def xpos = throw new IllegalStateException("You must set the opponent before you can run a CombiSensorDiffDriveRobot")
    def ypos = throw new IllegalStateException("You must set the opponent before you can run a CombiSensorDiffDriveRobot")
    def direction = throw new IllegalStateException("You must set the opponent before you can run a CombiSensorDiffDriveRobot")
    def adjust(xpos: Double, ypos: Double, direction: Double) = throw new IllegalStateException("You must set the opponent before you can run a CombiSensorDiffDriveRobot")
    def name = throw new IllegalStateException("You must set the opponent before you can run a CombiSensorDiffDriveRobot")
  }
  def sensorfullOpeningAngleDeg: Double = 30
	
  def createSensor: CombiSensor = new CombiSensor {
    override def fullOpeningAngle = toRad(sensorfullOpeningAngleDeg)

    def selfRobot = CombiSensorDiffDriveRobot.this
    def opponentRobot = opponent
  }
}

// TODO make it somehow stackable
class WheelMotor {

  import scala.math._

  private def maxSpeed = 30 * maxWheelSpeedFactor
  private def maxSpeedDiff = 5.0 * maxWheelAccelerationFactor
  
  def maxWheelAccelerationFactor = 1.0
  def maxWheelSpeedFactor = 1.0

  private var _speed = 0.0

  private def minmaxSpeed(speed: Double): Double = {
    if (speed > maxSpeed) maxSpeed
    else if (speed < -maxSpeed) -maxSpeed
    else speed
  }

  private def minmaxDiff(speed: Double, currentSpeed: Double): Double = {
    if (speed > currentSpeed) {
      val diff = speed - currentSpeed
      if (diff > maxSpeedDiff) currentSpeed + maxSpeedDiff
      else currentSpeed + diff
    } else {
      val diff = currentSpeed - speed
      if (diff > maxSpeedDiff) currentSpeed - maxSpeedDiff
      else currentSpeed - diff
    }
  }

  def set(intendedSpeed: Double): Unit = {
    val v1 = minmaxDiff(intendedSpeed, _speed)
    _speed = minmaxSpeed(intendedSpeed)
  }
  def get: Double = {
    _speed
  }
}

/**
 * Testrobot for diff drive. Has no sensor
 */
class TestDiffDriveRobot(controller: DiffDriveController[Unit]) extends DiffDriveRobot[Unit](controller) {
  def createSensor: Unit = Unit
}
  
