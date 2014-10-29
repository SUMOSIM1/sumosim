package net.entelijan.sumo.commons

/**
 * Constants needed all over the simulation
 */
trait SimulationConstants {
  val minRobotsDist = 100
  val arenaRadius = 400
}

trait UpdatableMsg

case class SumoSimulationMessage(xpos1: Double, ypos1: Double, dir1: Double,
                                 xpos2: Double, ypos2: Double, dir2: Double, info: String) extends UpdatableMsg {
  override def toString = {
    "SumoSimulationMessage[r1:(%.2f|%.2f|%.2f) r2:(%.2f|%.2f|%.2f) '%s']" format (xpos1, ypos1, dir1, xpos2, ypos2, dir2, info)
  }
}
case class InfoMessage(info: String) extends UpdatableMsg
case object StartGameEventMessage extends UpdatableMsg
case class FinishedGameEventMessage(duration: Int, winnerName: String) extends UpdatableMsg
case object ColissionEventMessage extends UpdatableMsg

/*
 * Interfaces for sumo simulation that can be implemented by
 * sumo simulator extensions.
 * 
 * E.g. You could implement a more advanced renderer or your own robots
 * The sumo-sim module contains default implementations for these interfaces
 * 
 */
trait Updatable {
  def receive(msg: UpdatableMsg)
}

/**
 * Interface for robots used by simulations
 */
trait Robot {
  /**
   * X position. 0 is the center of the arena
   */
  def xpos: Double
  /**
   * Y position. 0 is the center of the arena
   */
  def ypos: Double
  /**
   * Direction of the robot. A value between 0 and 2*PI
   */
  def direction: Double
  /**
   * Moves and rotates a  robot to a certain position and direction
   * Must not be used by controllers
   */
  def adjust(xpos: Double, ypos: Double, direction: Double)
  /**
   * Lets the robot take one step.
   */
  def takeStep()

  /**
   * Indicates weather the robot is ready to take steps
   */
  def ready = false
  /**
   * Returns the name of the robot
   */
  def name: String
  /**
   * Resets the inner state of the robot
   */
  def reset() = {}
}

trait OpponentAwareRobot extends Robot {
  var opponent: Robot
}
 
