package net.entelijan.sumo.reinforcement.db

import net.entelijan.sumo.robot.PosDir

case class SimulationOverview(
    id: String,
    startedAt: String,
    simulationName: String,
    robot1Name: String,
    robot2Name: String,
    reward1: String,
    reward2: String,
    stepcount: String,
    rewardhandler: String
)

case class SimulationState(
    robot1: PosDir,
    robot2: PosDir
)

case class SimulationDetail(
    id: String,
    startedAt: String,
    simulationName: String,
    robot1Name: String,
    robot2Name: String,
    states: Seq[SimulationState]
)

trait DatabaseClient {

  def overviews: Seq[SimulationOverview]

  def detail(id: String): SimulationDetail

  def close(): Unit

}
