package net.entelijan.sumo.robot

import net.entelijan.util._

class VorwardBackControler(val name: String) extends PosDirController {
  var time = 0
  var inc = 10

  def takeStep(current: PosDir, dist: Double): PosDir = {
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

class RightLeftControler(val name: String) extends PosDirController {
  var time = 0
  var inc = 10
  def takeStep(current: PosDir, dist: Double): PosDir = {
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

class ModeController(val name: String) extends DiffDriveController[BorderDistanceSensor] {

  class Mode
  case object Forward extends Mode
  case object Backward extends Mode
  case object TurnRight extends Mode
  case object TurnLeft extends Mode

  def takeStep(sensor: BorderDistanceSensor): DiffDriveValues = {
    val ran = new java.util.Random
    def randomBoolean = ran.nextDouble > 0.5
    def border = 200 + ran.nextInt(100) - 50
    def selectMode: Mode = {
      if (sensor.distance > border) Forward
      else if (randomBoolean) Backward
      else if (randomBoolean) TurnRight
      else TurnLeft
    }
    selectMode match {
      case Forward =>
        val fw = 10.0 + ran.nextDouble * 20
        new DiffDriveValues(fw, fw)
      case Backward =>
        val fw = 10.0 + ran.nextDouble * 20
        new DiffDriveValues(-fw, -fw)
      case TurnRight => new DiffDriveValues(5, 3)
      case TurnLeft => new DiffDriveValues(3, 5)
    }
  }
}

class Clever01Controller(val name: String) extends DiffDriveController[CombiSensor] {
  var nearBorder = TrueFor(0)
  import scala.util.Random._
  var turnDir = false
  
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
  case class TrueFor(n: Int) {
    var cnt = n
    def isTrue: Boolean = {
      if (cnt > 0) { cnt -= 1; true }
      else false
    }
  }
  def between(from: Int, to: Int): Int = from + nextInt(to - from)
}

class RotateControler(val name: String, initialPos: Point2, initialDir: Double) extends PosDirController with VecUtil with TrigUtil {

  private var firstStep = true

  def takeStep(current: PosDir, distance: Double): PosDir = {
    val nextPos = if (firstStep) initialPos else current.pos
    val nextDir = if (firstStep) initialDir else current.dir + toRad(45)
    val re = new PosDir(nextPos, normalize(nextDir))
    firstStep = false
    re
  }
}

/**
 * A simple controller with three phases, A, B and C.
 * Each phase represents a certain behavior
 * C ... Move backward
 * B ... Move forward
 * C ... Turn right
 */
class ForwardBackwardController(val name: String) extends DiffDriveController[BorderDistanceSensor] {
  var time = 0
  def takeStep(sensor: BorderDistanceSensor): DiffDriveValues = {
    time += 1
    val d = (time / 10.0).toInt
    if (d % 3 == 0) {
      new DiffDriveValues(20, 22)
    } else if (d % 3 == 1) {
      new DiffDriveValues(-33, -30)
    } else {
      new DiffDriveValues(7, 2)
    }
  }
}

/**
 * Does no moves
 */
class StandstillController(val name: String) extends DiffDriveController[BorderDistanceSensor] {
  def takeStep(sensor: BorderDistanceSensor): DiffDriveValues = new DiffDriveValues(0, 0)
}

/**
 * A controller for testing. The speed of the right and left wheel
 * is controlled by trigonometric functions
 */
class RotatingController(val name: String) extends DiffDriveController[NullSensor] {
  import scala.math._
  var time = 0.0
  def takeStep(sensor: NullSensor): DiffDriveValues = {
    time += 0.1
    val vr = (cos(time * 0.1) + 0.5) * 50
    val vl = (sin(time) - 0.5) * 60
    new DiffDriveValues(vr, vl)
  }
  def writeInfo(pw: java.io.PrintWriter) {}
}

class ParkingControler(pos: Point2) extends PosDirController {
  def name = "Parking"
  def takeStep(current: PosDir, dist: Double): PosDir = {
    new PosDir(pos, 0)
  }
}



