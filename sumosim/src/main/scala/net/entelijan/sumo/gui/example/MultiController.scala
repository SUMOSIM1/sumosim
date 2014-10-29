package net.entelijan.sumo.gui.example

import doctus.core.DoctusScheduler.Stopper
import net.entelijan.sumo.robot.UpDownLeftRight
import doctus.core.DoctusActivatable
import doctus.core.DoctusCanvas
import doctus.core.DoctusScheduler
import net.entelijan.sumo.gui.renderer.ImageProvider
import doctus.core.DoctusImage

/**
 * Controls multiple example simulations
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
  bgImage: DoctusImage) {

  val ssched = StopperCollectionScheduler(sched)

  new CleverVsForwardBackwardExample(canv, None, ssched, sumoViolet, sumoBlue, bgImage).start()
  a.onDeactivated(() => {
    ssched.stopAll()
    new CleverVsCleverExample(canv, None, ssched, sumoViolet, sumoBlue, bgImage).start()
  })
  b.onDeactivated(() => {
    ssched.stopAll()
    new RotatingVsForwardBackwardExample(canv, None, ssched).start()
  })
  c.onDeactivated(() => {
    ssched.stopAll()
    new ManualVsStandstillExample(canv, comp, None, ssched, robotRed, robotBlack, bgImage).start()
  })
  d.onDeactivated(() => {
    ssched.stopAll()
    new ManualVsForwardBackwardExample(canv, comp, None, ssched, robotRed, robotBlack, bgImage).start()
  })
}

/**
 * Collects the stoppers of all scheduler threads created by the scheduler
 */
case class StopperCollectionScheduler(sched: DoctusScheduler) extends DoctusScheduler {

  private var stoppers = List.empty[DoctusScheduler.Stopper]

  override def start(f: () => Unit, duration: Int): Stopper = {
    synchronized {
      val stopper = sched.start(f, duration)
      stoppers ::= stopper
      stopper
    }
  }

  /**
   * Stops all schedulers threads
   */
  def stopAll(): Unit = {
    stoppers.foreach(_.stop)
    stoppers = List.empty[DoctusScheduler.Stopper]
  }
}

