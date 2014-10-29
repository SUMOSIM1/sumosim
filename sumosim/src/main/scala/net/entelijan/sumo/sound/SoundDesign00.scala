package net.entelijan.sumo.sound

import net.entelijan.sumo.commons.ColissionEventMessage
import net.entelijan.sumo.commons.FinishedGameEventMessage
import net.entelijan.sumo.commons.StartGameEventMessage
import net.entelijan.sumo.commons.Updatable
import net.entelijan.sumo.commons.UpdatableMsg
import net.entelijan.util.Pausable
import net.entelijan.util.sound.Synth
import net.entelijan.util.Closeable

class SoundDesign00 extends Updatable with Pausable with Closeable {
  
  private val rand = new java.util.Random()

  private val bell = Synth()
  bell.changeInstrument(112)

  private val bump = Synth()
  bump.changeInstrument(22)

  def receive(umsg: UpdatableMsg) {
    import scala.math._
    umsg match {
      case StartGameEventMessage => bell.pressKey(48, 2000, 90)
      case FinishedGameEventMessage(_, _) => bell.pressKey(38, 2000, 90)
      case ColissionEventMessage =>
        bump.pressKey(25 + rand.nextInt(20), 100, 60)
      case msg: Any =>
        // info("received: '%s'" format msg)
    }

  }
  def close() {
    bell.close()
    bump.close()
  }

}

