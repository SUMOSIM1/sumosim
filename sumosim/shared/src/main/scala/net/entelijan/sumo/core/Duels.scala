package net.entelijan.sumo.core

import net.entelijan.sumo.robot._

object Duels {
  val cleverVsClever
      : Duel[CombiSensor, DiffDriveValues, CombiSensor, DiffDriveValues] = {
    val c1 = new Clever01Controller(name = "Clever A", shortName = "A")
    val r1 = new CombiSensorDiffDriveRobot() {
      override def name: String = c1.name
    }

    val c2 = new Clever01Controller(name = "Clever B", shortName = "B")
    val r2 = new CombiSensorDiffDriveRobot() {
      override def name: String = c2.name
    }

    r2.opponentRobot = r1
    r1.opponentRobot = r2

    Duel(
      ControlledRobot(c1, r1),
      ControlledRobot(c2, r2)
    )
  }
  val forwardBackwardVsClever
      : Duel[CombiSensor, DiffDriveValues, CombiSensor, DiffDriveValues] = {
    val c1 = new ForwardBackwardController()
    val r1 = new CombiSensorDiffDriveRobot() {
      override def name: String = c1.name
    }

    val c2 = new Clever01Controller()
    val r2 = new CombiSensorDiffDriveRobot() {
      override def name: String = c2.name
    }
    r2.opponentRobot = r1

    Duel(ControlledRobot(c1, r1), ControlledRobot(c2, r2))
  }

  val forwardBackwardVsRotating
      : Duel[CombiSensor, DiffDriveValues, CombiSensor, DiffDriveValues] = {
    val c1 = new ForwardBackwardController()
    val r1 = new CombiSensorDiffDriveRobot() {
      override def name: String = c1.name
    }

    val c2 = new RotatingController()
    val r2 = new CombiSensorDiffDriveRobot() {
      override def name: String = c1.name
    }

    Duel(ControlledRobot(c1, r1), ControlledRobot(c2, r2))
  }

}
