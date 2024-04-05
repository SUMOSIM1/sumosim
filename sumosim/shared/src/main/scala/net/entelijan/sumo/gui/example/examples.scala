package net.entelijan.sumo.gui.example

import doctus.core.util.DoctusUtil
import doctus.core.{
  DoctusCanvas,
  DoctusImage,
  DoctusScheduler,
  DoctusSchedulerStopper
}
import net.entelijan.sumo.commons.Updatable
import net.entelijan.sumo.core.{ControlledRobot, DemoSumoSim, Duel, Duels}
import net.entelijan.sumo.gui.controller.Util.createUniverse
import net.entelijan.sumo.gui.renderer.*
import net.entelijan.sumo.robot.*

trait SumoLayout {
  def robot1: ImageProvider

  def robot2: ImageProvider

  def background: DoctusImage

  def util: DoctusUtil
}

class RotatingVsForwardBackwardExample(
    val canv: DoctusCanvas,
    val soundDevice: Option[Updatable],
    scheduler: DoctusScheduler,
    images: Option[SumoLayout],
    util: DoctusUtil
) extends SumoGuiExample {

  private val duel = Duels.cleverVsClever
  override def name: String = duel.name
  private val sim = {
    new DemoSumoSim(duel, 30, scheduler)
  }

  private val univ = createUniverse(canv, images, util)

  sim.addUpdatable(univ)
  soundDevice.foreach(sim.addUpdatable)

  def start(): DoctusSchedulerStopper = sim.startRunning()

}

class DuelExample[S1, V1, S2, V2](
    val canv: DoctusCanvas,
    val duel: Duel[S1, V1, S2, V2],
    val soundDevice: Option[Updatable],
    scheduler: DoctusScheduler,
    layout: Option[SumoLayout],
    util: DoctusUtil
) extends SumoGuiExample {

  def name: String = duel.name
  private val sim = {
    new DemoSumoSim(duel, 30, scheduler)
  }

  private val univ = createUniverse(canv, layout, util)

  sim.addUpdatable(univ)
  soundDevice.foreach(sim.addUpdatable)

  def start(): DoctusSchedulerStopper = {
    sim.startRunning()
  }

}

class CleverVsForwardBackwardExample(
    val canv: DoctusCanvas,
    val soundDevice: Option[Updatable],
    val scheduler: DoctusScheduler,
    val imgProv1: ImageProvider,
    val imgProv2: ImageProvider,
    val bgImg: DoctusImage,
    val util: DoctusUtil
) extends SumoGuiExample {

  def name = "Forward/Backward vs Clever"

  private val sim = {

    val duel = {
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

    new DemoSumoSim(duel, 30, scheduler)
  }

  private val univ = {
    new R2DUniverse(canv, 0.8, imgProv1, imgProv2, bgImg, util)
    // SimpleUniverse.createDefaultUniverse(canv)
  }

  sim.addUpdatable(univ)
  soundDevice.foreach(sim.addUpdatable)

  def start(): DoctusSchedulerStopper = sim.startRunning()

}

class CleverVsCleverExample(
    val canv: DoctusCanvas,
    val soundDevice: Option[Updatable],
    val scheduler: DoctusScheduler,
    val imgProv1: ImageProvider,
    val imgProv2: ImageProvider,
    val bgImg: DoctusImage,
    val util: DoctusUtil
) extends SumoGuiExample {

  def name = "Forward/Backward vs Clever"

  private val sim = {

    val duel = {
      val c1 = new Clever01Controller()
      val r1 = new CombiSensorDiffDriveRobot() {
        override def name: String = c1.name
      }

      val c2 = new Clever01Controller()
      val r2 = new CombiSensorDiffDriveRobot() {
        override def name: String = c2.name
      }

      r2.opponentRobot = r1
      r1.opponentRobot = r2

      Duel(ControlledRobot(c1, r1), ControlledRobot(c2, r2))
    }

    new DemoSumoSim(duel, 30, scheduler)
  }

  private val univ = {
    new R2DUniverse(canv, 0.8, imgProv1, imgProv2, bgImg, util)
    // SimpleUniverse.createDefaultUniverse(canv)
  }

  sim.addUpdatable(univ)
  soundDevice.foreach(sim.addUpdatable)

  def start(): DoctusSchedulerStopper = sim.startRunning()

}

class ManualVsForwardBackwardExample(
    val canvas: DoctusCanvas,
    val comp: UpDownLeftRight,
    val soundDevice: Option[Updatable],
    val scheduler: DoctusScheduler,
    val imgProv1: ImageProvider,
    val imgProv2: ImageProvider,
    val bgImg: DoctusImage,
    val util: DoctusUtil
) extends SumoGuiExample {

  def name = "You vs Forward/Backward"

  private val sim = {

    val duel = {
      val c1 = new ForwardBackwardController()
      val r1 = new CombiSensorDiffDriveRobot() {
        override def name: String = c1.name
      }

      val c2 = new ManualController(name = "You", comp = comp) {
        override def forwardFactor = 0.7

        override def rotFactor = 0.5
      }
      val r2 = {
        new ManualDiffDriveRobot() {
          override def maxAccelerationFactor = 0.7

          override def maxSpeedFactor = 0.5

          override def name: String = c2.name

        }
      }
      Duel(
        ControlledRobot(c1, r1),
        ControlledRobot(c2, r2)
      )
    }

    new DemoSumoSim(duel, 30, scheduler)
  }

  private val univ = {
    new R2DUniverse(canvas, 0.8, imgProv1, imgProv2, bgImg, util)
    // SimpleUniverse.createDefaultUniverse(canv)
    // R2DUniverse.createSumosUniverse(canv, 0.5)
  }

  sim.addUpdatable(univ)
  soundDevice.foreach(sim.addUpdatable)

  def start(): DoctusSchedulerStopper = sim.startRunning()

}

class ManualVsStandstillExample(
    val canv: DoctusCanvas,
    val comp: UpDownLeftRight,
    val soundDevice: Option[Updatable],
    val scheduler: DoctusScheduler,
    val imgProv1: ImageProvider,
    val imgProv2: ImageProvider,
    val bgImg: DoctusImage,
    val util: DoctusUtil
) extends SumoGuiExample {

  def name = "You vs Standstill"

  private val sim = {

    val duel = {
      val c1 = new StandstillController()
      val r1 = new CombiSensorDiffDriveRobot() {
        override def name: String = c1.name
      }

      val c2 = new ManualController(name = "You", comp = comp) {
        override def forwardFactor = 0.7

        override def rotFactor = 0.5
      }
      val r2 = {
        new ManualDiffDriveRobot() {
          override def maxAccelerationFactor = 0.7

          override def maxSpeedFactor = 0.5

          override def name: String = c2.name
        }
      }
      Duel(ControlledRobot(c1, r1), ControlledRobot(c2, r2))
    }

    new DemoSumoSim(duel, 30, scheduler)
  }

  private val univ = {
    new R2DUniverse(canv, 0.8, imgProv1, imgProv2, bgImg, util)
    // SimpleUniverse.createDefaultUniverse(canv)
  }

  sim.addUpdatable(univ)
  soundDevice.foreach(sim.addUpdatable)

  def start(): DoctusSchedulerStopper = sim.startRunning()

}
