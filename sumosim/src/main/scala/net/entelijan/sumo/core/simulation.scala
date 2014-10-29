package net.entelijan.sumo.core

import net.entelijan.sumo.commons._
import net.entelijan.util._
import scala.concurrent._
import scala.annotation.tailrec
import doctus.core._

class DemoSumoSim(val robot1: Robot, val robot2: Robot, pauseTime: Int, scheduler: DoctusScheduler)
  extends Pausable with RobotSimulation with UpdatablesHandler {


  def startRunning(): Unit = {
    var winner: Option[Robot] = None
    var cnt: Int = 0
    val states = Map(
      "before" -> List(
        TimelineEvent(0, Ext(positionRobotsToStart)),
        TimelineEvent(1000, Ext(() => sendUpdatableMessage(StartGameEventMessage))),
        TimelineEvent(1201, Transition(() => "run"))),
      "run" -> List(
        TimelineEvent(0, Ext(() => sendSumoSimulationMessage(""))),
        TimelineEvent(1, Ext(runOneCompetitionStep)),
        TimelineEvent(pauseTime, Transition(() => checkForWinner() match {
          case Some(r) => winner = Some(r); "end"
          case None => winner = None; cnt += 1; "run"
        }))),
      "end" -> List(
        TimelineEvent(0, Ext(() => winner.foreach(r => sendSumoSimulationMessage("'%s' is the winner" format r.name)))),
        TimelineEvent(0, Ext(() => winner.foreach(r => sendUpdatableMessage(FinishedGameEventMessage(cnt, r.name))))),
        TimelineEvent(1500, Ext(reset)),
        TimelineEvent(1501, Ext(positionRobotsToStart)),
        TimelineEvent(1502, Transition(() => "run"))))

    val tl = StatefulTimeline("before", states)
    scheduler.start(() => tl.exec(), 1)
  }

}

trait UpdatablesHandler {

  val updatables = scala.collection.mutable.ListBuffer[Updatable]()

  def sendUpdatableMessage(msg: UpdatableMsg) {
    updatables.foreach(u => u.receive(msg))
  }

  def addUpdatable(updatable: Updatable) {
    updatables += updatable
  }

}

trait Simulation extends UpdatablesHandler {
  def startRunning(): Unit
}


trait RobotSimulation extends Simulation with SimulationConstants {

  def robot1: Robot

  def robot2: Robot

  def reset() {
    robot1.reset()
    robot2.reset()
  }

  def sendUpdatableMessage(msg: UpdatableMsg)

  def sendSumoSimulationMessage(msg: String) {
    sendUpdatableMessage(new SumoSimulationMessage(
      robot1.xpos, robot1.ypos, robot1.direction,
      robot2.xpos, robot2.ypos, robot2.direction, msg))
  }

  def positionRobots() {
    robot1.adjust(0, 150, 0)
    robot2.adjust(0, -150, 0)
    sendSumoSimulationMessage("Robots positioned")
  }

  def positionRobotsToStart() {
    adjustRobot1ToStart()
    adjustRobot2ToStart()
    sendSumoSimulationMessage("Robots positioned for Start")
  }

  def adjustRobot1ToStart() {
    robot1.adjust(-80, 0, 3 * scala.math.Pi / 4)
  }

  def adjustRobot2ToStart() {
    robot2.adjust(80, 0, -scala.math.Pi / 4)
  }

  def runOneCompetitionStep() {
    val prevPosRobot1 = new Point2(robot1.xpos, robot1.ypos)
    val prevPosRobot2 = new Point2(robot2.xpos, robot2.ypos)
    robot1.takeStep()
    robot2.takeStep()
    handleCollisions(prevPosRobot1, prevPosRobot2)
  }

  import scala.math._

  def handleCollisions(prevPos1: Point2, prevPos2: Point2) {
    val pos1 = new Point2(robot1.xpos, robot1.ypos)
    val pos2 = new Point2(robot2.xpos, robot2.ypos)
    val dist = pos1.distance(pos2)
    if (dist < minRobotsDist) {
      val v1 = pos1.toVec(prevPos1)
      val v2 = pos2.toVec(prevPos2)
      val v = v1 + v2

      val npos1 = pos1 + v
      val npos2 = pos2 + v

      val corr = npos1 toVec npos2
      val norm = corr.norm
      val c1 = corr * ((minRobotsDist - dist) / 2 / norm)

      val cpos1 = npos1 + c1
      val cpos2 = npos2 - c1

      val v1n = v1.norm
      val v2n = v2.norm
      val adj1 = abs(v1.norm - v2.norm)
      val adj2 = abs(norm - 66.0)

      sendUpdatableMessage(ColissionEventMessage)

      robot1.adjust(cpos1.xpos, cpos1.ypos, robot1.direction)
      robot2.adjust(cpos2.xpos, cpos2.ypos, robot2.direction)

    }
  }

  def checkForWinner(): Option[Robot] = {
    val center = new Point2(0, 0)
    val d1 = new Point2(robot1.xpos, robot1.ypos) distance center
    val d2 = new Point2(robot2.xpos, robot2.ypos) distance center
    if (d1 > arenaRadius && d2 > arenaRadius) {
      if (d1 < d2) Some(robot1)
      else Some(robot2)
    } else if (d1 > arenaRadius) Some(robot2)
    else if (d2 > arenaRadius) Some(robot1)
    else None
  }

}  




