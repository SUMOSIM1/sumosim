package net.entelijan.sumo.reinforcement

import doctus.core.DoctusSchedulerStopper
import net.entelijan.sumo.commons.{SelfRobot, UpdatableMsg}
import net.entelijan.sumo.core.{ControlledRobot, Duel, RobotSimulation}
import net.entelijan.sumo.gui.example.CodedDiffDriveControllers
import net.entelijan.sumo.robot.*
import net.entelijan.sumo.util.Helper

object Analyse {

  def runValueCollectingSimulation(controllerIds: String): String = {

    val (c1, c2) = {
      val cs = controllerIds.split("-")
      val id1 = cs(0)
      val id2 = cs(1)
      (
        CodedDiffDriveControllers.controller(id1, "A"),
        CodedDiffDriveControllers.controller(id2, "B"),
      )
    }

    val collector = new WrapperCollector(
      keys = List(
        c1.shortName,
        c2.shortName
      ),
      names = List(
        "distLeft",
        "distCenter",
        "distRight",
        "opponent",
        "rotRight",
        "rotLeft",
        "xpos",
        "ypos",
        "direction"
      )
    )
    val w1 = new ControllerWrapper(c1, collector)
    val r1 = new CombiSensorDiffDriveRobot() {
      override def name: String = w1.name
    }

    val r2 = new CombiSensorDiffDriveRobot() {
      override def name: String = c2.name
    }

    r2.opponentRobot = r1
    r1.opponentRobot = r2

    val myDuel = Duel(ControlledRobot(w1, r1), ControlledRobot(w1, r2))

    val sim = new RobotSimulation[
      CombiSensor,
      DiffDriveValues,
      CombiSensor,
      DiffDriveValues
    ] {

      override def sendUpdatableMessage(msg: UpdatableMsg): Unit = {
        throw new RuntimeException("Should never be called")
      }

      override def duel
          : Duel[CombiSensor, DiffDriveValues, CombiSensor, DiffDriveValues] =
        myDuel

      println(s"starting simulation - ${myDuel.name}")
      println(s"robot1: ${describeRobot(myDuel.robot1.robot)}")

      private def describeRobot(robo: SelfRobot) = {
        s"${robo.name}"
      }

      println(s"robot2: ${describeRobot(myDuel.robot2.robot)}")

      override def startRunning(): DoctusSchedulerStopper = {

        def checkContinue(cnt: Int): Boolean = {
          checkForEnd(cnt) match {
            case Continue => true
            case _        => false
          }
        }

        positionRobotsToStart()
        var cnt = 0
        val dur = Helper.measureTimeMillis {
          while (checkContinue(cnt)) {
            runOneCompetitionStep()
            cnt += 1
          }
        }
        val durPerStep = dur / cnt
        checkForEnd(cnt) match {
          case Winner(w) =>
            println(
              f"Got a winner ${w.name} after $cnt steps. Duration per step $durPerStep%.4f ms"
            )
          case Draw =>
            println(
              f"Got no winner after $cnt steps (Max number of steps exceeded). Duration per step $durPerStep%.4f ms"
            )
          case Continue =>
            println(
              f"No winner after $cnt steps. Duration per step $durPerStep%.4f ms"
            )
        }
        () => ()
      }
    }

    sim.startRunning()
    println("finished simulation")
    collector.transpose().mkString("\n")
  }

}
