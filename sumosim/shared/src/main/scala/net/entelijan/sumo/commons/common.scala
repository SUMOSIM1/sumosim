package net.entelijan.sumo.commons

/** Constants needed all over the simulation
  */
trait SimulationConstants {
  val minRobotsDist = 100
  val arenaRadius = 400
}

sealed trait UpdatableMsg

case class SumoSimulationMessage(
    xpos1: Double,
    ypos1: Double,
    dir1: Double,
    xpos2: Double,
    ypos2: Double,
    dir2: Double,
    info: String
) extends UpdatableMsg {
  override def toString: String = {
    "SumoSimulationMessage[r1:(%.2f|%.2f|%.2f) r2:(%.2f|%.2f|%.2f) '%s']" format (
      xpos1,
      ypos1,
      dir1,
      xpos2,
      ypos2,
      dir2,
      info
    )
  }
}

case class InfoMessage(info: String) extends UpdatableMsg

case class StartGameEventMessage(robot1Desc: String, robot2Desc: String)
    extends UpdatableMsg

case class FinishedGameEventMessage(duration: Int, winnerName: String)
    extends UpdatableMsg

case object CollisionEventMessage extends UpdatableMsg

/*
 * Interfaces for sumo simulation that can be implemented by
 * sumo simulator extensions.
 *
 * E.g. You could implement a more advanced renderer or your own robots
 * The sumo-sim module contains default implementations for these interfaces
 *
 */
trait Updatable {
  def receive(msg: UpdatableMsg): Unit
}

trait OpponentRobot {

  /** X position. 0 is the center of the arena
    */
  def xpos: Double

  /** Y position. 0 is the center of the arena
    */
  def ypos: Double

}

trait SelfRobot extends OpponentRobot with RobotEventsCollector {

  def name: String

  /** Direction of the robot. A value between 0 and 2*PI
    */
  def direction: Double

}

case class RobotEvent(
    name: String,
    value: String
)

trait RobotEventsCollector {

  private var _events = Seq.empty[(String, String)]

  def addEvent(name: String, value: String): Unit = _events =
    _events :+ (name, value)
  def events: Iterable[(String, String)] = _events

}

/** Interface for robots used by simulations
  * @tparam S
  *   Sensor
  * @tparam V
  *   Indicates the robot how to move depending on the robots implementation
  */
trait Robot[S, V] extends SelfRobot {

  // TODO is this a correct defined setter ???
  def opponentRobot_=(opponentRobot: OpponentRobot) = {
    // ignore an opponent set
  }

  def opponentRobot: OpponentRobot = {
    throw IllegalStateException("No opponent defined")
  }

  /** Moves and rotates a robot to a certain position and direction Must not be
    * used by controllers
    */
  def adjust(xpos: Double, ypos: Double, direction: Double): Unit

  /** Indicates weather the robot is ready to take steps
    */
  def ready = false

  /** Resets the inner state of the robot
    */
  def reset(): Unit = {}

  /** Moves the robot according to the values created by the Controller
    */
  def move(value: V): Unit

  /** Returns the sensor of the current robot containing the values according to
    * the robots current position and the position of the opponent
    */
  def sensor: S
}
