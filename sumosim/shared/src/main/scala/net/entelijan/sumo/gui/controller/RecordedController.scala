package net.entelijan.sumo.gui.controller

import doctus.core.comp.DoctusCard
import doctus.core.util.DoctusUtil
import doctus.core.{
  DoctusActivatable,
  DoctusCanvas,
  DoctusScheduler,
  DoctusSchedulerStopper
}
import net.entelijan.sumo.commons.{
  InfoMessage,
  StartGameEventMessage,
  SumoSimulationMessage
}
import net.entelijan.sumo.gui.controller.Util.createUniverse
import net.entelijan.sumo.gui.example.SumoLayout
import net.entelijan.sumo.reinforcement.db.{SimulationDetail, SimulationState}

case class RecordedController(
    card: DoctusCard,
    canvas: DoctusCanvas,
    runForwardButton: DoctusActivatable,
    runFastForwardButton: DoctusActivatable,
    pauseButton: DoctusActivatable,
    runBackwardButton: DoctusActivatable,
    runFastBackwardButton: DoctusActivatable,
    homeButton: DoctusActivatable,
    backButton: DoctusActivatable,
    scheduler: DoctusScheduler,
    layout: Option[SumoLayout],
    util: DoctusUtil
) {

  private val normalDiffVal = 1
  private val normalDelayVal = 10

  private val fastDiffVal = 5
  private val fastDelayVal = 1

  private val univ = createUniverse(canvas, layout, util)

  private var time = 0

  private var stopper: Option[DoctusSchedulerStopper] = None

  sealed trait State

  private case class Error(msg: String)

  private case class Ok(sim: SimulationDetail, maxIndex: Int)

  private var simulation: Option[SimulationDetail] = None

  def init(sim: SimulationDetail): Unit = {
    simulation = Some(sim)
    time = 0
    univ.receive(StartGameEventMessage(sim.robot1Name, sim.robot2Name))
    displayCurrentState(sim, sim.states.size - 1)
  }

  private def state() = {
    simulation
      .map { sim =>
        if (sim.states.isEmpty) {
          Error(
            "Simulation contains no states. Check why simulations have no states"
          )
        }
        Ok(sim, sim.states.size - 1)
      }
      .getOrElse(
        Error(
          "Recorded controller contains no simulation. Check why the store is empty"
        )
      )
  }

  def stop(): Unit = {
    stopper.foreach(_.stop())
  }

  private def currentState(
      sim: SimulationDetail,
      maxIndex: Int
  ): SimulationState = {
    val index = math.max(0, math.min(time, maxIndex))
    sim.states(index)
  }

  private def displayCurrentState(
      sim: SimulationDetail,
      maxIndex: Int
  ): Unit = {
    val stage = currentState(sim, maxIndex)
    val msg = SumoSimulationMessage(
      xpos1 = stage.robot1.pos.xpos,
      ypos1 = stage.robot1.pos.ypos,
      dir1 = stage.robot1.dir,
      xpos2 = stage.robot2.pos.xpos,
      ypos2 = stage.robot2.pos.ypos,
      dir2 = stage.robot2.dir,
      info = ""
    )
    univ.receive(msg)
  }

  private def displayInfo(info: String): Unit = {
    val msg = InfoMessage(
      info = info
    )
    univ.receive(msg)
  }

  private def createErrorMessage(action: String, error: String): String = {
    s"ERROR: '$action' not possible. $error"
  }

  homeButton.onDeactivated { () =>
    stop()
    card.show(Constants.CARD_HOME)
  }

  backButton.onDeactivated { () =>
    stop()
    card.show(Constants.CARD_RECORDED_CONTROL)
  }

  private def incTimeAndCheckOverflow(diff: Int, maxIndex: Int): Unit = {
    val newTime = time + diff
    if (newTime >= maxIndex) {
      stop()
    } else if (newTime < 0) {
      stop()
    } else {
      time = math.max(math.min(newTime, maxIndex), 0)
    }
  }

  def start(
      delay: Int,
      diff: Int,
      sim: SimulationDetail,
      maxIndex: Int
  ): Unit = {
    stop()
    univ.receive(StartGameEventMessage(sim.robot1Name, sim.robot2Name))
    val s = scheduler.start(
      () => {
        displayCurrentState(sim, maxIndex)
        incTimeAndCheckOverflow(diff, maxIndex)
      },
      delay,
      0
    )
    stopper = Some(s)
  }

  runForwardButton.onDeactivated { () =>
    run(normalDelayVal, normalDiffVal, "forward")
  }

  runFastForwardButton.onDeactivated { () =>
    run(fastDelayVal, fastDiffVal, "fast forward")
  }

  pauseButton.onDeactivated { () =>
    stop()
  }

  runBackwardButton.onDeactivated { () =>
    run(normalDelayVal, -normalDiffVal, "backward")
  }

  runFastBackwardButton.onDeactivated { () =>
    run(fastDelayVal, -fastDiffVal, "fast backward")
  }

  private def run(delay: Int, diff: Int, action: String): Unit = {
    state() match {
      case Ok(sim, maxIndex) =>
        start(delay, diff, sim, maxIndex)
      case Error(msg) =>
        val errorMessage = createErrorMessage(action, msg)
        displayInfo(errorMessage)
    }
  }

}
