package net.entelijan.sumo.core
import doctus.core._
import net.entelijan.sumo.commons._
import net.entelijan.sumo.robot.Controller
import net.entelijan.sumo.util._

import scala.math._

case class ControlledRobot[S, V](
    controller: Controller[S, V],
    robot: Robot[S, V]
)

case class Duel[S1, V1, S2, V2](
    robot1: ControlledRobot[S1, V1],
    robot2: ControlledRobot[S2, V2]
) {
  def name: String = s"${robot1.controller.name} vs ${robot2.controller.name}"
}

class DemoSumoSim[S1, V1, S2, V2](
    val duel: Duel[S1, V1, S2, V2],
    pauseTime: Int,
    scheduler: DoctusScheduler
) extends Pausable
    with RobotSimulation[S1, V1, S2, V2]
    with UpdatablesHandler {

  override def startRunning(): DoctusSchedulerStopper = {
    var winner: Option[SelfRobot] = None
    var cnt: Int = 0
    val states: Map[State, List[TimelineEvent]] = Map(
      State_BEFORE -> List(
        TimelineEvent(0, Ext(() => positionRobotsToStart())),
        TimelineEvent(
          1000,
          Ext(() =>
            sendUpdatableMessage(
              StartGameEventMessage(
                duel.robot1.controller.name,
                duel.robot2.controller.name
              )
            )
          )
        ),
        TimelineEvent(1201, Transition(() => State_RUN))
      ),
      State_RUN -> List(
        TimelineEvent(0, Ext(() => sendSumoSimulationMessage(""))),
        TimelineEvent(1, Ext(() => runOneCompetitionStep())),
        TimelineEvent(
          pauseTime,
          Transition(() =>
            checkForEnd(cnt) match {
              case Winner(r) => winner = Some(r); State_END
              case Draw      => winner = None; State_END
              case Continue  => winner = None; cnt += 1; State_RUN
            }
          )
        )
      ),
      State_END -> List(
        TimelineEvent(
          0,
          Ext(() =>
            winner.foreach(r =>
              sendSumoSimulationMessage(
                "'%s' is the winner" format r.name
              )
            )
          )
        ),
        TimelineEvent(
          0,
          Ext(() =>
            winner.foreach(r =>
              sendUpdatableMessage(
                FinishedGameEventMessage(cnt, r.name)
              )
            )
          )
        ),
        TimelineEvent(1500, Ext(() => reset())),
        TimelineEvent(1501, Ext(() => positionRobotsToStart())),
        TimelineEvent(1502, Transition(() => State_RUN))
      )
    )

    val tl = StatefulTimeline(State_BEFORE, states)
    scheduler.start(() => tl.exec(), 1)
  }

}

trait UpdatablesHandler {

  private val updatables = scala.collection.mutable.ListBuffer[Updatable]()

  def sendUpdatableMessage(msg: UpdatableMsg): Unit = {
    updatables.foreach(u => u.receive(msg))
  }

  def addUpdatable(updatable: Updatable): Unit = {
    updatables += updatable
  }

}

trait RobotSimulation[S1, V1, S2, V2] extends Simulation[S1, V1, S2, V2] {

  def duel: Duel[S1, V1, S2, V2]

  def startRunning(): DoctusSchedulerStopper

  override def robot1: Robot[S1, V1] = duel.robot1.robot

  override def robot2: Robot[S2, V2] = duel.robot2.robot

  def runOneCompetitionStep(): Unit = {
    val prevPosRobot1 =
      Point2(duel.robot1.robot.xpos, duel.robot1.robot.ypos)
    val prevPosRobot2 =
      Point2(duel.robot2.robot.xpos, duel.robot2.robot.ypos)

    val sensor1 = duel.robot1.robot.sensor
    val value1 = duel.robot1.controller.takeStep(sensor1)
    duel.robot1.robot.move(value1)

    val sensor2 = duel.robot2.robot.sensor
    val value2 = duel.robot2.controller.takeStep(sensor2)
    duel.robot2.robot.move(value2)

    handleCollisions(prevPosRobot1, prevPosRobot2)
  }

}

trait Simulation[S1, V1, S2, V2]
    extends UpdatablesHandler
    with SimulationConstants {

  def robot1: Robot[S1, V1]
  def robot2: Robot[S2, V2]

  def reset(): Unit = {
    robot1.reset()
    robot2.reset()
  }

  def sendUpdatableMessage(msg: UpdatableMsg): Unit

  def sendSumoSimulationMessage(msg: String): Unit = {
    sendUpdatableMessage(
      SumoSimulationMessage(
        robot1.xpos,
        robot1.ypos,
        robot1.direction,
        robot2.xpos,
        robot2.ypos,
        robot2.direction,
        msg
      )
    )
  }

  def positionRobots(): Unit = {
    robot1.adjust(0, 150, 0)
    robot2.adjust(0, -150, 0)
    sendSumoSimulationMessage("Robots positioned")
  }

  def positionRobotsToStart(): Unit = {
    adjustRobot1ToStart()
    adjustRobot2ToStart()
    sendSumoSimulationMessage("Robots positioned for Start")
  }

  private def adjustRobot1ToStart(): Unit = {
    robot1.adjust(-80, 0, 3 * scala.math.Pi / 4)
  }

  private def adjustRobot2ToStart(): Unit = {
    robot2.adjust(80, 0, -scala.math.Pi / 4)
  }

  def handleCollisions(prevPos1: Point2, prevPos2: Point2): Unit = {
    val pos1 = Point2(robot1.xpos, robot1.ypos)
    val pos2 = Point2(robot2.xpos, robot2.ypos)
    val dist = pos1.distance(pos2)
    if (dist < minRobotsDist) {
      val v1 = pos1.toVec(prevPos1)
      val v2 = pos2.toVec(prevPos2)
      val v = v1 + v2

      val npos1 = pos1 + v
      val npos2 = pos2 + v

      val corr = npos1.toVec(npos2)
      val norm = corr.norm
      val c1 = corr * ((minRobotsDist - dist) / 2 / norm)

      val cpos1 = npos1 + c1
      val cpos2 = npos2 - c1

      v1.norm
      v2.norm
      abs(v1.norm - v2.norm)
      abs(norm - 66.0)

      sendUpdatableMessage(CollisionEventMessage)

      robot1.adjust(
        cpos1.xpos,
        cpos1.ypos,
        robot1.direction
      )
      robot2.adjust(
        cpos2.xpos,
        cpos2.ypos,
        robot2.direction
      )

    }
  }

  val maxNumberOfSteps = 1000

  sealed trait EndReason

  case object Continue extends EndReason

  case object Draw extends EndReason

  case class Winner(winner: SelfRobot) extends EndReason

  def checkForEnd(numberOfSteps: Int): EndReason = {
    if (numberOfSteps >= maxNumberOfSteps) Draw
    else {
      val center = Point2(0, 0)
      val d1 =
        Point2(robot1.xpos, robot1.ypos).distance(center)
      val d2 =
        Point2(robot2.xpos, robot2.ypos).distance(center)
      if (d1 > arenaRadius && d2 > arenaRadius) {
        if (d1 < d2) Winner(robot1)
        else Winner(robot2)
      } else if (d1 > arenaRadius) Winner(robot2)
      else if (d2 > arenaRadius) Winner(robot1)
      else Continue
    }
  }

}
