package net.entelijan.sumo.robot

import net.entelijan.sumo.commons.{OpponentRobot, Robot}
import net.entelijan.sumo.util.{Point2, TrigUtil, VecUtil}

abstract class DiffDriveRobot[S]
    extends Robot[S, DiffDriveValues]
    with VecUtil
    with TrigUtil {

  def maxAccelerationFactor = 1.0
  def maxSpeedFactor = 1.0

  // Max velocity

  // Wheel distance
  private val wheelDistance = 50.0

  // Angle
  private var phi = 0.0
  // Actual position
  private var pos = Point2(0, 0)

  private val leftWheel = new WheelMotor {
    override def maxWheelAccelerationFactor: Double = maxAccelerationFactor
  }
  private val rightWheel = new WheelMotor

  def xpos: Double = pos.xpos
  def ypos: Double = pos.ypos
  def direction: Double = normalize(phi)
  def adjust(xpos: Double, ypos: Double, direction: Double): Unit = {
    pos = Point2(xpos, ypos)
    phi = direction
  }
  def name: String

  override def sensor: S

  override def move(values: DiffDriveValues): Unit = {

    import scala.math._

    rightWheel.set(values.rightVelo)
    leftWheel.set(values.leftVelo)

    // Calculate the new angle(phi) and position using kinematic equations
    val vr = rightWheel.get
    val vl = leftWheel.get
    if (vr == vl) {
      val x = pos.xpos + vr * cos(phi)
      val y = pos.ypos + vl * sin(phi)
      pos = Point2(x, y)
    } else {
      val d = (wheelDistance * (vr + vl)) / (2 * (vr - vl))
      val b = sin((vr - vl) / wheelDistance + phi) - sin(phi)
      val a = cos((vr - vl) / wheelDistance + phi) - cos(phi)
      val x = pos.xpos + d * b
      val y = pos.ypos - d * a
      phi = phi + (vl - vr) / wheelDistance
      pos = Point2(x, y)
    }
  }

}

trait DiffDriveController[S] extends Controller[S, DiffDriveValues] {

  def name: String
  def ready = true

}

case class DiffDriveValues(rightVelo: Double, leftVelo: Double)

/** A robot with a BorderDistanceSensor
  */
class BorderDistanceDiffDriveRobot(
    val randomizer: Randomizer = RandomizerImpl()
) extends DiffDriveRobot[BorderDistanceSensor] {
  def sensor: BorderDistanceSensor =
    new SimpleExtendedBorderDistanceSensor() {
      def selfRobot: BorderDistanceDiffDriveRobot =
        BorderDistanceDiffDriveRobot.this

      override def randomAmount: Double = randomizer.randomAmount
    }

  override def name: String = "Border distance diff drive"
}
class ExtendedBorderDistanceDiffDriveRobot(
    val randomizer: Randomizer
) extends DiffDriveRobot[ExtendedBorderDistanceSensor] {
  def sensor: ExtendedBorderDistanceSensor =
    new SimpleExtendedBorderDistanceSensor() {
      def selfRobot: ExtendedBorderDistanceDiffDriveRobot =
        ExtendedBorderDistanceDiffDriveRobot.this
      override def randomAmount: Double = randomizer.randomAmount
    }

  override def name: String = "Extended border distance diff drive"
}

class TeeBorderDistanceDiffDriveRobot(
    val randomizer: Randomizer = RandomizerImpl()
) extends DiffDriveRobot[TeeBorderDistanceSensor] {
  def sensor: SimpleTeeBorderDistanceSensor =
    new SimpleTeeBorderDistanceSensor() {
      def selfRobot: TeeBorderDistanceDiffDriveRobot =
        TeeBorderDistanceDiffDriveRobot.this
      override def randomAmount: Double = randomizer.randomAmount
    }

  override def name: String = "Tee border diff drive"
}

abstract class ManualDiffDriveRobot extends DiffDriveRobot[NullSensor] {
  override def sensor = new NullSensor

}

abstract class CombiSensorDiffDriveRobot(
    val randomizer: Randomizer = RandomizerImpl()
) extends DiffDriveRobot[CombiSensor]
    with OpponentAware {

  private var opponent = Option.empty[OpponentRobot]

  override def opponentRobot_=(opponentRobot: OpponentRobot) = opponent = Some(
    opponentRobot
  )

  override def opponentRobot: OpponentRobot = {
    opponent.getOrElse(throw IllegalStateException("No opponent defined"))
  }

  private def sensorOpeningAngleDeg: Double = 30

  def sensor: CombiSensor = new CombiSensor {
    override def fullOpeningAngle: Double = toRad(sensorOpeningAngleDeg)

    def opponentRobot: OpponentRobot = selfRobot.opponentRobot
    def selfRobot: CombiSensorDiffDriveRobot = CombiSensorDiffDriveRobot.this

    def direction: Double = selfRobot.direction
    def xpos: Double = selfRobot.xpos
    def ypos: Double = selfRobot.ypos

    override def randomAmount: Double = randomizer.randomAmount
  }

}

// TODO make it somehow stackable
class WheelMotor {

  private def maxSpeed = 30 * maxWheelSpeedFactor
  private def maxSpeedDiff = 5.0 * maxWheelAccelerationFactor

  def maxWheelAccelerationFactor = 1.0
  private def maxWheelSpeedFactor = 1.0

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
    minmaxDiff(intendedSpeed, _speed)
    _speed = minmaxSpeed(intendedSpeed)
  }
  def get: Double = {
    _speed
  }
}

/** Test robot for diff drive. Has no sensor
  */
class TestDiffDriveRobot extends DiffDriveRobot[Unit]() {
  def sensor: Unit = ()

  override def name: String = "Test robot"
}
