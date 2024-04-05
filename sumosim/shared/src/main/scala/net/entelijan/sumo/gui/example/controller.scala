package net.entelijan.sumo.gui.example

import doctus.core.*
import doctus.core.util.DoctusUtil
import net.entelijan.sumo.gui.renderer.ImageProvider
import net.entelijan.sumo.robot.*

/** Controls multiple example simulations
  */
case class MultiController(
    a: DoctusActivatable,
    b: DoctusActivatable,
    c: DoctusActivatable,
    d: DoctusActivatable,
    canv: DoctusCanvas,
    comp: UpDownLeftRight,
    sched: DoctusScheduler,
    sumoViolet: ImageProvider,
    sumoBlue: ImageProvider,
    robotBlack: ImageProvider,
    robotRed: ImageProvider,
    bgImage: DoctusImage,
    util: DoctusUtil
) {

  private val ssched = StopperCollectionScheduler(sched)

  new CleverVsForwardBackwardExample(
    canv,
    None,
    ssched,
    sumoViolet,
    sumoBlue,
    bgImage,
    util
  ).start()
  a.onDeactivated(() => {
    ssched.stopAll()
    new CleverVsCleverExample(
      canv,
      None,
      ssched,
      sumoViolet,
      sumoBlue,
      bgImage,
      util
    )
      .start()
  })
  b.onDeactivated(() => {
    ssched.stopAll()
    new RotatingVsForwardBackwardExample(canv, None, ssched, None, util).start()
  })
  c.onDeactivated(() => {
    ssched.stopAll()
    new ManualVsStandstillExample(
      canv,
      comp,
      None,
      ssched,
      robotRed,
      robotBlack,
      bgImage,
      util
    ).start()
  })
  d.onDeactivated(() => {
    ssched.stopAll()
    new ManualVsForwardBackwardExample(
      canv,
      comp,
      None,
      ssched,
      robotRed,
      robotBlack,
      bgImage,
      util
    ).start()
  })
}

/** Collects the stoppers of all scheduler threads created by the scheduler
  */
case class StopperCollectionScheduler(sched: DoctusScheduler)
    extends DoctusScheduler {

  private var stoppers = List.empty[DoctusSchedulerStopper]

  override def start(
      f: () => Unit,
      duration: Int,
      initial: Int
  ): DoctusSchedulerStopper = {
    synchronized {
      val stopper = sched.start(f, duration)
      stoppers ::= stopper
      stopper
    }
  }

  /** Stops all schedulers threads
    */
  def stopAll(): Unit = {
    stoppers.foreach(_.stop())
    stoppers = List.empty[DoctusSchedulerStopper]
  }
}

object CodedDiffDriveControllers {

  val valid: List[String] = List(
    "clever",
    "mode",
    "fwbw",
    "stand",
    "rotating"
  )

  private val validIDs = valid.mkString(", ")

  /** Creates a controller
    * @param id:
    *   Defines the behaviour
    * @param prefix
    *   : Prefix for the name
    * @return
    *   Diff drive controller with combi sensor
    */
  def controller(
      id: String,
      prefix: String
  ): DiffDriveController[CombiSensor] = {
    id match {
      case "clever" =>
        new Clever01Controller(
          shortName = s"$prefix-C",
          name = s"$prefix Clever I"
        )
      case "mode" =>
        new ModeController(shortName = s"$prefix-M", name = s"$prefix Mode")
      case "fwbw" =>
        new ForwardBackwardController(
          shortName = s"$prefix-FB",
          name = s"$prefix Forward Backward"
        )
      case "stand" =>
        new StandstillController(
          shortName = s"$prefix-S",
          name = s"$prefix Standstill"
        )
      case "rotating" =>
        new RotatingController(
          shortName = s"$prefix-R",
          name = s"$prefix Rotating"
        )
      case _ =>
        throw new IllegalArgumentException(
          s"Unknown RController id $id. Valid: $validIDs"
        )
    }
  }
}
