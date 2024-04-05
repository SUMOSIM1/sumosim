package net.entelijan.sumo.sound

import net.entelijan.sumo.commons._
import net.entelijan.sumo.util.{Closeable, Pausable}

class SoundDesign00 extends Updatable with Pausable with Closeable {

  private val rand = new java.util.Random()

  private val bell = Synth()
  bell.changeInstrument(112)

  private val bump = Synth()
  bump.changeInstrument(22)

  def receive(umsg: UpdatableMsg): Unit = {
    umsg match {
      case StartGameEventMessage(_, _)    => bell.pressKey(48, 2000, 90)
      case FinishedGameEventMessage(_, _) => bell.pressKey(38, 2000, 90)
      case CollisionEventMessage =>
        bump.pressKey(25 + rand.nextInt(20), 100, 60)
      case SumoSimulationMessage(_, _, _, _, _, _, _) =>
      case InfoMessage(_)                             =>
    }

  }

  def close(): Unit = {
    bell.close()
    bump.close()
  }

}
