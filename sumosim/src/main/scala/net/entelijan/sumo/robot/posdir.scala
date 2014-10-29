package net.entelijan.sumo.robot

import net.entelijan.sumo.commons._
import net.entelijan.util._
class PosDirRobot(val control: PosDirController) extends 
		Robot with VecUtil with SimulationConstants {

	var pos = new Point2(0.0, 0.0)
	var dir = 0.0
	
	def xpos = pos.xpos
	def ypos = pos.ypos
	def direction = dir
    def adjust(xpos: Double, ypos: Double, direction: Double) {
	  pos = new Point2(xpos, ypos)
      dir = direction
    }

  override def ready = true

	def name = control.name
	
	def takeStep() {
		val dist = pointCircleDistance(arenaRadius, pos, dir)
		val posDir = control.takeStep(new PosDir(pos, dir), dist)
		dir = posDir.dir
		pos = posDir.pos
	}
	
	def writeInfo(pw: java.io.PrintWriter) = {
	  pw.printf("posdir '%s'%n", control.name)
	  pw.printf(" xpos=%.2f%n", double2Double(xpos))
	  pw.printf(" ypos=%.2f%n", double2Double(ypos))
	  pw.printf(" dir=%.2f%n", double2Double(dir))
	}
	
	override def toString = {"robot[%s, %s]" format(control.name, pos)}
	
}

class PosDir(val pos: Point2, val dir: Double)

trait PosDirController {
	
	def name: String
	def takeStep(currentPosDir: PosDir, distance: Double): PosDir

	override def toString = {"posdir[%s]" format name}
}
	

