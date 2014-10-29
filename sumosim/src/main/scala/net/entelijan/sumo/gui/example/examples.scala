package net.entelijan.sumo.gui.example

import doctus.core.{ DoctusCanvas, DoctusScheduler }
import net.entelijan.sumo.commons.Updatable
import net.entelijan.sumo.core.DemoSumoSim
import net.entelijan.sumo.gui.renderer._
import net.entelijan.sumo.robot._
import doctus.core.DoctusImage

class RotatingVsForwardBackwardExample(val canv: DoctusCanvas, val soundDevice: Option[Updatable],
                                       scheduler: DoctusScheduler) extends SumoGuiExample {

  def name = "SumoSim Forward/Backward vs Rotating"
  val sim = {
    val c1 = new ForwardBackwardController("FB")
    val r1 = new BorderDistanceDiffDriveRobot(c1)

    val c2 = new RotatingController("R")
    val r2 = new ManualDiffDriveRobot(c2)

    new DemoSumoSim(r1, r2, 30, scheduler)
  }

  val univ = {
    // R2DUniverse.createRoboUniverse(0.8)
    SimpleUniverse.createDefaultUniverse(canv)
  }

  sim.addUpdatable(univ)
  soundDevice.foreach(sim.addUpdatable)

  def start(): Unit = sim.startRunning()

}

class CleverVsForwardBackwardExample(
  val canv: DoctusCanvas,
  val soundDevice: Option[Updatable],
  val scheduler: DoctusScheduler,
  val imgProv1: ImageProvider,
  val imgProv2: ImageProvider,
  val bgImg: DoctusImage) extends SumoGuiExample {

  def name = "Forward/Backward vs Clever"
  val sim = {
    val c1 = new ForwardBackwardController("FB")
    val r1 = new BorderDistanceDiffDriveRobot(c1)

    val c2 = new Clever01Controller("C")
    val r2 = new CombiSensorDiffDriveRobot(c2)

    r2.opponent = r1
    new DemoSumoSim(r1, r2, 30, scheduler)
  }

  val univ = {
    new R2DUniverse(canv, 0.8, imgProv1, imgProv2, bgImg)
    //SimpleUniverse.createDefaultUniverse(canv)
  }

  sim.addUpdatable(univ)
  soundDevice.foreach(sim.addUpdatable)

  def start(): Unit = sim.startRunning()

}

class CleverVsCleverExample(
  val canv: DoctusCanvas,
  val soundDevice: Option[Updatable],
  val scheduler: DoctusScheduler,
  val imgProv1: ImageProvider,
  val imgProv2: ImageProvider,
  val bgImg: DoctusImage) extends SumoGuiExample {

  def name = "Forward/Backward vs Clever"
  val sim = {
    val c1 = new Clever01Controller("Clever 1")
    val r1 = new CombiSensorDiffDriveRobot(c1)

    val c2 = new Clever01Controller("Clever 2")
    val r2 = new CombiSensorDiffDriveRobot(c2)

    r2.opponent = r1
    r1.opponent = r2
    new DemoSumoSim(r1, r2, 30, scheduler)
  }

  val univ = {
    new R2DUniverse(canv, 0.8, imgProv1, imgProv2, bgImg)
    //SimpleUniverse.createDefaultUniverse(canv)
  }

  sim.addUpdatable(univ)
  soundDevice.foreach(sim.addUpdatable)

  def start(): Unit = sim.startRunning()

}

class ManualVsForwardBackwardExample(
  val canv: DoctusCanvas,
  val comp: UpDownLeftRight,
  val soundDevice: Option[Updatable],
  val scheduler: DoctusScheduler,
  val imgProv1: ImageProvider,
  val imgProv2: ImageProvider,
  val bgImg: DoctusImage) extends SumoGuiExample {

  def name = "You vs Forward/Backward"
  val sim = {
    val c1 = new ForwardBackwardController("FB")
    val r1 = new BorderDistanceDiffDriveRobot(c1)

    val r2 = {
      val c0 = new ManualController("You", comp) {
        override def forwardFactor = 0.7
        override def rotFactor = 0.5
      }
      new ManualDiffDriveRobot(c0) {
        override def maxAccelerationFactor = 0.7
        override def maxSpeedFactor = 0.5
      }
    }

    new DemoSumoSim(r1, r2, 30, scheduler)
  }

  val univ = {
    new R2DUniverse(canv, 0.8, imgProv1, imgProv2, bgImg)
    // SimpleUniverse.createDefaultUniverse(canv)
    // R2DUniverse.createSumosUniverse(canv, 0.5)
  }

  sim.addUpdatable(univ)
  soundDevice.foreach(sim.addUpdatable)

  def start(): Unit = sim.startRunning()

}

class ManualVsStandstillExample(
  val canv: DoctusCanvas,
  val comp: UpDownLeftRight,
  val soundDevice: Option[Updatable],
  val scheduler: DoctusScheduler,
  val imgProv1: ImageProvider,
  val imgProv2: ImageProvider,
  val bgImg: DoctusImage) extends SumoGuiExample {

  def name = "You vs Standstill"
  val sim = {
    val c1 = new StandstillController("Standstill")
    val r1 = new BorderDistanceDiffDriveRobot(c1)

    val r2 = {
      val c0 = new ManualController("You", comp) {
        override def forwardFactor = 0.7
        override def rotFactor = 0.5
      }
      new ManualDiffDriveRobot(c0) {
        override def maxAccelerationFactor = 0.7
        override def maxSpeedFactor = 0.5
      }
    }

    new DemoSumoSim(r1, r2, 30, scheduler)
  }

  val univ = {
    new R2DUniverse(canv, 0.8, imgProv1, imgProv2, bgImg)
    // SimpleUniverse.createDefaultUniverse(canv)
  }

  sim.addUpdatable(univ)
  soundDevice.foreach(sim.addUpdatable)

  def start(): Unit = sim.startRunning()

}


