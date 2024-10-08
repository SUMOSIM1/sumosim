package net.entelijan.sumo.core

import net.entelijan.sumo.commons.Robot
import net.entelijan.sumo.robot.*

trait Duel[S1, V1, S2, V2] {
  def robot1: ControlledRobot[S1, V1]
  def robot2: ControlledRobot[S2, V2]

  def name: String = s"${robot1.controller.name} vs ${robot2.controller.name}"
}

object Duels {

  def create[S1, V1, S2, V2](
      c1: Controller[S1, V1],
      r1: Robot[S1, V1],
      c2: Controller[S2, V2],
      r2: Robot[S2, V2]
  ): Duel[S1, V1, S2, V2] = {

    r2.opponentRobot = r1
    r1.opponentRobot = r2

    new Duel[S1, V1, S2, V2] {
      override def robot1 = ControlledRobot(c1, r1)
      override def robot2 = ControlledRobot(c2, r2)
    }

  }

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

    create(c1, r1, c2, r2)
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
    create(c1, r1, c2, r2)
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

    create(c1, r1, c2, r2)
  }

}
