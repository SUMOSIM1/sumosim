package net.entelijan.sumo.reinforcement

import net.entelijan.sumo.commons.{CollisionEventMessage, Robot, UpdatableMsg}
import net.entelijan.sumo.core.Simulation
import net.entelijan.sumo.robot.{
  CombiSensor,
  CombiSensorDiffDriveRobot,
  DiffDriveValues
}
import net.entelijan.sumo.util.Point2

import java.util.Locale

sealed trait ReceiveCommand
sealed trait SendCommand

case object StartCommand extends ReceiveCommand

case class SensorCommand(
    robot1Sensor: CombiSensor,
    robot2SSensor: CombiSensor
) extends SendCommand

case class DiffDriveCommand(
    robot1DiffDriveValues: DiffDriveValues,
    robot2DiffDriveValues: DiffDriveValues,
    stepsCount: Int
) extends ReceiveCommand

case class FinishedOkCommand(
    robot1Events: Seq[(String, String)], // List of key value pairs
    robot2Events: Seq[(String, String)] // List of key value pairs
) extends SendCommand
case class FinishedErrorCommand(
    message: String
) extends SendCommand

class NetConnector[C] {

  var simOpt: Option[NetSimulation[C]] = None

  def receive(data: String, conn: C, callback: (String, C) => Unit): Boolean = {
    try {
      val command = CommandParser.parse(data)
      println(s" -- received '$command'")
      val sendCommand: SendCommand = command match {
        case StartCommand =>
          simOpt
            .map { _ =>
              FinishedErrorCommand("Simulation is already running")
            }
            .getOrElse {
              val robot1 = new CombiSensorDiffDriveRobot() {
                override def name: String = "network 1"
              }
              val robot2 = new CombiSensorDiffDriveRobot() {
                override def name: String = "network 2"
              }
              val sim = new NetSimulation[C](robot1, robot2)
              robot1.opponentRobot = robot2
              robot2.opponentRobot = robot1
              println(s" --created a simulation '$sim'")
              simOpt = Some(sim)
              sim.start()
            }
        case DiffDriveCommand(v1, v2, cnt) =>
          simOpt
            .map { sim =>
              sim.diffDriveValues(v1, v2, cnt)
            }
            .getOrElse {
              FinishedErrorCommand("Simulation was stopped")
            }
      }
      val sendData = CommandFormatter.format(sendCommand)
      callback(sendData, conn)
      sendCommand match {
        case FinishedErrorCommand(_) =>
          println(s" --- finished error")
          simOpt = None
          true
        case FinishedOkCommand(_, _) =>
          println(s" --- finished OK")
          simOpt = None
          true
        case _ => true
      }
    } catch {
      case e: Throwable =>
        println(s" --- catched exc $e close sim")
        simOpt = None
        false
    }
  }
}

class NetSimulation[C](
    val robot1: Robot[CombiSensor, DiffDriveValues],
    val robot2: Robot[CombiSensor, DiffDriveValues]
) extends Simulation[
      CombiSensor,
      DiffDriveValues,
      CombiSensor,
      DiffDriveValues
    ] {

  def start(): SendCommand = {
    positionRobotsToStart()
    val s1 = robot1.sensor
    val s2 = robot2.sensor
    val cmd = SensorCommand(s1, s2)
    print(s" -- start created command '$cmd'")
    cmd
  }

  def diffDriveValues(
      r1: DiffDriveValues,
      r2: DiffDriveValues,
      cnt: Int
  ): SendCommand = {
    robot1.move(r1)
    robot2.move(r2)
    val prevPosRobot1 =
      Point2(robot1.xpos, robot1.ypos)
    val prevPosRobot2 =
      Point2(robot2.xpos, robot2.ypos)
    handleCollisions(prevPosRobot1, prevPosRobot2)
    checkForEnd(cnt) match {
      case Winner(winner) =>
        winner.addEvent("winner", "true")
        FinishedOkCommand(
          robot1Events = robot1.events.toSeq,
          robot2Events = robot2.events.toSeq
        )
      case Draw =>
        robot1.addEvent("draw", "true")
        robot2.addEvent("draw", "true")
        FinishedOkCommand(
          robot1Events = robot1.events.toSeq,
          robot2Events = robot2.events.toSeq
        )
      case Continue =>
        val s1 = robot1.sensor
        val s2 = robot2.sensor
        SensorCommand(s1, s2)
    }
  }

  override def sendUpdatableMessage(msg: UpdatableMsg): Unit = {
    msg match {
      case CollisionEventMessage =>
        println("## Received collision event message")
      case _ => // Nothing to do
    }
  }
}

object CommandParser {

  def parseDiffDriveValues(data: String): DiffDriveValues = {
    val r = data.split(";")(0).toDouble
    val l = data.split(";")(1).toDouble
    DiffDriveValues(r, l)
  }

  def parse(data: String): ReceiveCommand = {
    val split = data.split("\\|")
    if (split.isEmpty)
      throw new IllegalArgumentException(
        s"Received unknown command '$data'"
      )
    split(0) match {
      case "A" => StartCommand
      case "C" =>
        val r1Data = split(1).split("#")(0)
        val r2Data = split(1).split("#")(1)
        val cntStr = split(1).split("#")(2)
        DiffDriveCommand(
          parseDiffDriveValues(r1Data),
          parseDiffDriveValues(r2Data),
          cntStr.toInt
        )
      case _ =>
        throw new IllegalArgumentException(
          s"Received unknown command '$data'"
        )
    }
  }
}

object CommandFormatter {
  def format(sendCommand: SendCommand): String = {

    def fd(d: Double): String = {
      "%.4f".formatLocal(Locale.US, d)
    }

    def fcs(s: CombiSensor): String = {
      s"${fd(s.xpos)};${fd(s.ypos)};${fd(s.direction)};" +
        s"${fd(s.leftDistance)};" +
        s"${fd(s.frontDistance)};" +
        s"${fd(s.rightDistance)};" +
        s"${s.opponentInSector.toString}"
    }

    sendCommand match {
      case SensorCommand(r1, r2) =>
        s"B|${fcs(r1)}#${fcs(r2)}"
      case FinishedOkCommand(r1, r2) =>
        println(s"-- finished ok ${r1} ${r2}")
        val s1 = r1.map { case (n, v) => s"${n}!${v}" }.mkString(";")
        val s2 = r2.map { case (n, v) => s"${n}!${v}" }.mkString(";")
        s"D|${s1}#${s2}"
      case FinishedErrorCommand(message) =>
        s"E|$message"
    }
  }
}
