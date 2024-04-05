package net.entelijan.sumo.robot

import net.entelijan.sumo.util.{Point2, TrigUtil, VecUtil}

class ForwardBackController(val name: String, val shortName: String)
    extends PosDirController {
  var time = 0
  private var inc = 10

  def takeStep(sensor: PosDirSensor): PosDir = {
    val current =
      new PosDir(new Point2(sensor.xpos, sensor.ypos), sensor.direction)
    time += 1
    if (time == 1) {
      new PosDir(new Point2(0, 350), current.dir)
    } else {
      if (time / 100 % 2 == 0) {
        if (current.pos.distance(new Point2(0, 0)) > 350.0) inc *= (-1)
        val newPos = new Point2(current.pos.xpos, current.pos.ypos + inc)
        new PosDir(newPos, current.dir + 0.1)
      } else {
        new PosDir(current.pos, current.dir + 0.17)
      }
    }
  }
}

class RightLeftController(val name: String, val shortName: String)
    extends PosDirController {
  var time = 0
  private var inc = 10

  def takeStep(sensor: PosDirSensor): PosDir = {
    val current =
      new PosDir(new Point2(sensor.xpos, sensor.ypos), sensor.direction)
    time += 1
    if (time == 1) {
      new PosDir(new Point2(350, 0), current.dir)
    } else {
      if (time / 100 % 2 == 1) {
        if (current.pos.distance(new Point2(0, 0)) > 350.0) inc *= (-1)
        val newPos = new Point2(current.pos.xpos + inc, current.pos.ypos)
        new PosDir(newPos, current.dir + 0.1)
      } else {
        new PosDir(current.pos, current.dir - 0.35)
      }
    }
  }
}

class NullSensor

class ModeController(
    val name: String = "Mode Controller",
    val shortName: String = "mode"
) extends DiffDriveController[CombiSensor] {

  override def describe: String =
    s"Different modes depending on sensor input"

  private class Mode

  private case object Forward extends Mode

  private case object Backward extends Mode

  private case object TurnRight extends Mode

  private case object TurnLeft extends Mode

  private var currentMode: Mode = Forward

  def takeStep(sensor: CombiSensor): DiffDriveValues = {
    val ran = new java.util.Random

    def p(percent: Int): Boolean = {
      val p = percent / 100.0
      ran.nextDouble < p
    }

    def border = 400

    def selectMode: Mode = {
      if (sensor.opponentInSector == CENTER) Forward
      else if (p(80)) currentMode
      else if (sensor.frontDistance < border)
        if (sensor.leftDistance > sensor.rightDistance) TurnLeft else TurnRight
      else if (p(10)) Backward
      else Forward
    }

    currentMode = selectMode

    selectMode match {
      case Forward =>
        val fw = 50.0 + ran.nextDouble * 5
        DiffDriveValues(fw, fw)
      case Backward =>
        val fw = 2.0 + ran.nextDouble * 5
        DiffDriveValues(-fw, -fw)
      case TurnRight => DiffDriveValues(50, 10)
      case TurnLeft  => DiffDriveValues(10, 50)
    }
  }
}

class Clever01Controller(
    val name: String = "Clever I",
    val shortName: String = "c1"
) extends DiffDriveController[CombiSensor] {

  override def describe: String =
    s"Goes straight ahead until near the border, then turn around"

  import scala.util.Random._

  private var nearBorder = TrueFor(0)
  private var turnDir = false

  def takeStep(sensor: CombiSensor): DiffDriveValues = {
    val ar = sensor.arenaRadius
    if (!nearBorder.isTrue && sensor.frontDistance / ar < 0.2) {
      nearBorder = TrueFor(between(20, 30))
      turnDir = nextBoolean()
    }
    if (nearBorder.isTrue) {
      // Stop and turn around
      if (turnDir) DiffDriveValues(6, -6)
      else DiffDriveValues(-6, 6)
    } else {
      val l = between(20, 30)
      val r = between(20, 30)
      DiffDriveValues(l, r)
    }
  }

  private case class TrueFor(n: Int) {
    private var cnt = n

    def isTrue: Boolean = {
      if (cnt > 0) {
        cnt -= 1; true
      } else false
    }
  }

  private def between(from: Int, to: Int): Int = from + nextInt(to - from)
}

class RotateController(
    val name: String = "Rotate",
    val shortName: String = "rot",
    initialPos: Point2,
    initialDir: Double
) extends PosDirController
    with VecUtil
    with TrigUtil {

  private var firstStep = true

  override def describe: String =
    "Turns 45 deg every step"

  def takeStep(current: PosDir): PosDir = {
    val nextPos = if (firstStep) initialPos else current.pos
    val nextDir = if (firstStep) initialDir else current.dir + toRad(45)
    val re = new PosDir(nextPos, normalize(nextDir))
    firstStep = false
    re
  }

  override def takeStep(sensor: PosDirSensor): PosDir = {
    val nextPos =
      if (firstStep) initialPos else new Point2(sensor.xpos, sensor.xpos)
    val nextDir = if (firstStep) initialDir else sensor.direction + toRad(45)
    val re = new PosDir(nextPos, normalize(nextDir))
    firstStep = false
    re

  }
}

/** A simple controller with three phases, A, B and C. Each phase represents a
  * certain behavior C ... Move backward B ... Move forward C ... Turn right
  */
class ForwardBackwardController(
    val name: String = "Forward Backward",
    val shortName: String = "fb"
) extends DiffDriveController[CombiSensor] {
  var time = 0

  override def describe: String =
    "Tumbles somehow forward backward"

  def takeStep(sensor: CombiSensor): DiffDriveValues = {
    time += 1
    val d = (time / 10.0).toInt
    if (d % 3 == 0) {
      DiffDriveValues(20, 22)
    } else if (d % 3 == 1) {
      DiffDriveValues(-33, -30)
    } else {
      DiffDriveValues(7, 2)
    }
  }
}

/** Does no moves
  */
class StandstillController(
    val name: String = "Standstill",
    val shortName: String = "stst"
) extends DiffDriveController[CombiSensor] {

  override def describe: String =
    "Does nothing"

  def takeStep(sensor: CombiSensor): DiffDriveValues =
    DiffDriveValues(0, 0)
}

/** A controller for testing. The speed of the right and left wheel is
  * controlled by trigonometric functions
  */
class RotatingController(
    val name: String = "Rotating",
    val shortName: String = "rot"
) extends DiffDriveController[CombiSensor] {

  import scala.math._

  override def describe: String =
    "Goes somehow in circles"

  var time = 0.0

  def takeStep(sensor: CombiSensor): DiffDriveValues = {
    time += 0.1
    val vr = (cos(time * 0.1) + 0.5) * 50
    val vl = (sin(time) - 0.5) * 60
    DiffDriveValues(vr, vl)
  }

  def writeInfo(pw: java.io.PrintWriter): Unit = {}
}

class ParkingController(pos: Point2) extends PosDirController {
  override def name = "Parking"

  override def shortName: String = "parking"

  override def takeStep(sensor: PosDirSensor): PosDir = {
    new PosDir(pos, 0)
  }
}
