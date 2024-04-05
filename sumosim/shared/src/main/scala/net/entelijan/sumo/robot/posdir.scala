package net.entelijan.sumo.robot

import net.entelijan.sumo.commons._
import net.entelijan.sumo.util.{Point2, VecUtil}

import java.io.PrintWriter

trait Controller[S, V] {

  def name: String

  def shortName: String

  def describe: String = "Undefined controller"

  def takeStep(sensor: S): V

}

class PosDirRobot(val name: String)
    extends Robot[NullSensor, PosDir]
    with VecUtil
    with SimulationConstants {

  var pos = new Point2(0.0, 0.0)
  var dir = 0.0

  def xpos: Double = pos.xpos

  def ypos: Double = pos.ypos

  def direction: Double = dir

  def adjust(xpos: Double, ypos: Double, direction: Double): Unit = {
    pos = new Point2(xpos, ypos)
    dir = direction
  }

  override def ready = true

  def writeInfo(pw: java.io.PrintWriter): PrintWriter = {
    pw.printf("posdir '%s'%n", name)
    pw.printf(" xpos=%.2f%n", double2Double(xpos))
    pw.printf(" ypos=%.2f%n", double2Double(ypos))
    pw.printf(" dir=%.2f%n", double2Double(dir))
  }

  override def toString: String = {
    "robot[%s, %s]" format (name, pos)
  }

  /** Moves the robot according to the values created by the Controller
    */
  override def sensor: NullSensor = new NullSensor()

  /** Moves the robot according to the values created by the Controller
    */
  override def move(value: PosDir): Unit = {
    dir = value.dir
    pos = value.pos
  }

}

case class PosDir(val pos: Point2, val dir: Double)

trait PosDirController extends Controller[PosDirSensor, PosDir] {

  override def toString: String = {
    "posdir[%s]" format name
  }
}
