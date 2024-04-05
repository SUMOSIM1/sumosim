package net.entelijan.sumo.sound

import net.entelijan.sumo.util.{
  Pausable,
  Requester,
  ResourcePool,
  SynchronizedResourcePool
}

import javax.sound.midi._

trait Synth {

  /** Press a key of the synthesizer keyId can be a value between 24-94 duration
    * is im milliseconds
    */
  def pressKey(keyId: Int, duration: Int, velocity: Int): Unit

  /** Change the instrument of the synthesizer. The id can be a value between 0
    * \- 127
    */
  def changeInstrument(instrumentId: Int): Unit

  /** Changes the reverb of the synthesizer reverb must be between 0-127
    */
  def changeReverb(reverb: Int): Unit

  /** Releases all resources of the synthesizer
    */
  def close(): Unit
}

/** Create a new instance of a Synth. If an error occurred during initialisation
  * a dummy Synth is created
  */
object Synth {

  def apply(): Synth = {
    var synthOption: Option[Synthesizer] = None
    try {
      val synthesizer = MidiSystem.getSynthesizer
      synthesizer.open()
      synthOption = Some(synthesizer)
      val midiChannels = synthesizer.getChannels
      val sb = synthesizer.getDefaultSoundbank
      // TODO sb can be null. find a more elegant solution
      val instruments: Array[Instrument] = sb.getInstruments
      new DefaultSynth(synthesizer, midiChannels, instruments)
    } catch {
      case _: MidiUnavailableException =>
        new DummySynth(synthOption)
      case _: Throwable =>
        new DummySynth(synthOption)
    }
  }

  private class DummySynth(synthesizer: Option[Synthesizer]) extends Synth {
    def pressKey(keyId: Int, duration: Int, velocity: Int): Unit = {}
    def close(): Unit = {
      synthesizer match {
        case Some(s) => s.close()
        case None    =>
      }
    }
    def changeInstrument(instrumentId: Int): Unit = {}
    def changeReverb(reverb: Int): Unit = {}
  }

  private class DefaultSynth(
      synthesizer: Synthesizer,
      midiChannels: Array[MidiChannel],
      instruments: Array[Instrument]
  ) extends Synth
      with Pausable {

    private val REVERB_ID = 91

    private val channelsPool: ResourcePool[MidiChannel] = new MidiChannelPool()

    def pressKey(keyId: Int, duration: Int, velocity: Int): Unit = {
      if (outOfRange(keyId))
        throw new IllegalArgumentException("keyId must be in 0-127")
      if (outOfRange(velocity))
        throw new IllegalArgumentException(
          "velocity must be in 0-127 %d" format velocity
        )
      val req = new MidiRequester(keyId, duration, velocity)
      channelsPool.addRequester(req)
      pause(100)
    }

    def close(): Unit = {
      synthesizer.close()
      channelsPool.stop()
    }

    def changeInstrument(instrumentId: Int): Unit = {
      if (outOfRange(instrumentId))
        throw new IllegalArgumentException(
          "instrumentId must be in 0-127 %d" format instrumentId
        )
      val inst = instruments(instrumentId)
      inst.getName
      synthesizer.loadInstrument(inst)
      midiChannels foreach (c => c.programChange(instrumentId))
    }
    def changeReverb(reverb: Int): Unit = {
      if (outOfRange(reverb))
        throw new IllegalArgumentException(
          "reverb must be in 0-127 %d" format reverb
        )
      midiChannels foreach (c => c.controlChange(REVERB_ID, reverb))
    }

    private def outOfRange(value: Int): Boolean = {
      value < 0 || value >= 128
    }

    private class MidiRequester(kNum: Int, duration: Int, velocity: Int)
        extends Requester[MidiChannel]
        with Pausable {

      def useResource(channel: MidiChannel): Unit = {
        channel.noteOn(kNum, velocity)
        pause(duration)
        channel.noteOff(kNum, velocity)
      }
    }

    private class MidiChannelPool
        extends SynchronizedResourcePool[MidiChannel]() {

      def resources = midiChannels.toList

      def stop(): Unit = {
        // Nothing to be done here
      }

    }
  }
}
