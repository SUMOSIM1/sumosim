package net.entelijan.sumo.robot

import net.entelijan.sumo.commons._
import net.entelijan.util._

/**
 * A sensor that gives information about the current distance of the
 * robot to the border of the arena.
 * The distance depends on the direction the sensor is orientation
 */
trait BorderDistanceSensor {

  def distance: Double

}

trait SelfRobotAware {
  def selfRobot: Robot 
}

trait OpponentRobotAware {
  def opponentRobot: Robot 
}

trait RobotsAware extends SelfRobotAware with OpponentRobotAware

trait ExtendedBorderDistanceSensor extends BorderDistanceSensor {

  def xpos: Double
  def ypos: Double
  def direction: Double
  
}

trait TeeBorderDistanceSensor {
  def leftDistance: Double
  def frontDistance: Double
  def rightDistance: Double
}

trait SimpleTeeBorderDistanceSensor extends 
	TeeBorderDistanceSensor with VecUtil with SimulationConstants with Randomizer with SelfRobotAware {
  def leftDistance: Double = distance("l", -scala.math.Pi / 2.0)
  def frontDistance: Double = distance("f", 0.0)
  def rightDistance: Double = distance("r",  scala.math.Pi / 2.0)
  private def distance(name: String, dirOffset: Double): Double = {
    val d = pointCircleDistance(arenaRadius, new Point2(selfRobot.xpos, selfRobot.ypos), selfRobot.direction + dirOffset)
    val re = d + randomAmount
    re
  }
}

trait SimpleBorderDistanceSensor extends 
		ExtendedBorderDistanceSensor with SimulationConstants with VecUtil with Randomizer with SelfRobotAware {

  def distance: Double = {
    pointCircleDistance(arenaRadius, new Point2(selfRobot.xpos, selfRobot.ypos), selfRobot.direction) + randomAmount
  }
  def xpos: Double = selfRobot.xpos
  def ypos: Double = selfRobot.ypos
  def direction:Double = selfRobot.direction


}

trait Randomizer {
  
  private val random = new java.util.Random
  
  def randomVariance: Double = 50.0

  def randomAmount: Double = {
    if (randomVariance == 0.0) {
      0.0
    }
    else {
      (random.nextDouble * 2.0 * randomVariance) - randomVariance
    }
  }
}

sealed trait SectorName
case object UNDEF extends SectorName
case object LEFT extends SectorName
case object CENTER extends SectorName
case object RIGHT extends SectorName

trait OpponentFanSensor extends RobotsAware {

    def opponentInSector: SectorName

}

trait CombiSensor extends OpponentFanSensorImpl with SimpleTeeBorderDistanceSensor 

trait OpponentFanSensorImpl extends OpponentFanSensor with VecUtil with TrigUtil {
  
  def fullOpeningAngle = toRad(30.0)

  private def opponentInSector(sectorDirection: Double): Boolean = {
    val selfRobotPoint = new Point2(selfRobot.xpos, selfRobot.ypos)
    val opponentRobotPoint = new Point2(opponentRobot.xpos, opponentRobot.ypos)
    val lookDirection = selfRobot.direction + sectorDirection
    isPointInSector(selfRobotPoint, lookDirection, fullOpeningAngle / 6.0, opponentRobotPoint)
  }
  
  def opponentInSector: SectorName = {
    if (opponentInSector(-fullOpeningAngle / 3.0)) RIGHT
    else if (opponentInSector(0)) CENTER
    else if (opponentInSector(fullOpeningAngle / 3.0)) LEFT
    else UNDEF
  }  

}
