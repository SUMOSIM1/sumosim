package net.entelijan.sumo.gui.controller

import doctus.core.comp.{DoctusCard, DoctusSelect, SelectItemDescription}
import doctus.core.util.DoctusUtil
import doctus.core.{
  DoctusActivatable,
  DoctusCanvas,
  DoctusScheduler,
  DoctusSchedulerStopper
}
import net.entelijan.sumo.core.Duels
import net.entelijan.sumo.gui.example.{
  CodedDiffDriveControllers,
  DuelExample,
  SumoLayout
}
import net.entelijan.sumo.robot.CombiSensorDiffDriveRobot
import net.entelijan.sumo.sound.SoundDesign00

case class CodedController(
    card: DoctusCard,
    canvas: DoctusCanvas,
    homeButton: DoctusActivatable,
    startButton: DoctusActivatable,
    controllers1: DoctusSelect[String],
    controllers2: DoctusSelect[String],
    scheduler: DoctusScheduler,
    layout: Option[SumoLayout],
    util: DoctusUtil
) {
  var stopper: Option[DoctusSchedulerStopper] = None

  private val desc = SelectItemDescription(
    columnWidths = List(10),
    extractColumnString = (s: String, _: Int) => s
  )

  controllers1.setItems(CodedDiffDriveControllers.valid, desc)
  controllers2.setItems(CodedDiffDriveControllers.valid, desc)

  val sd = new SoundDesign00

  homeButton.onActivated { () =>
    stopper.foreach { _.stop() }
    card.show(Constants.CARD_HOME)
  }

  startButton.onActivated { () =>
    stopper.foreach(s => s.stop())
    val id1 = controllers1.selectedItem.get
    val id2 = controllers2.selectedItem.get
    val c1 = CodedDiffDriveControllers.controller(id1, "A")
    val c2 = CodedDiffDriveControllers.controller(id2, "B")

    val r1 = new CombiSensorDiffDriveRobot() {
      override def name: String = c1.name
    }
    val r2 = new CombiSensorDiffDriveRobot() {
      override def name: String = c2.name
    }

    val duel = Duels.create(c1, r1, c2, r2)
    val ex =
      new DuelExample(canvas, duel, Some(sd), scheduler, layout, util)
    stopper = Some(ex.start())
  }
}
