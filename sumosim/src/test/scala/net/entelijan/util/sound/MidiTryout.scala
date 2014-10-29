package net.entelijan.util.sound
import net.entelijan.util.Pausable

object MidiTryout extends App with Pausable {

  val s1 = Synth()
  s1.changeInstrument(3)

  val s2 = Synth()
  s2.changeInstrument(50)
 
  try {
    s1.pressKey(47, 5000, 100)
    pause(500)
    s2.pressKey(47, 3000, 100)
    s2.pressKey(45, 3000, 100)
    pause(500)
    s1.pressKey(49, 100, 100)
    pause(500)
    s1.pressKey(52, 500, 100)
    pause(500)

    s1.changeInstrument(1)
    s1.pressKey(47, 5000, 100)
    pause(500)
    s1.pressKey(49, 4500, 100)
    pause(500)
    s1.pressKey(52, 4000, 100)

    pause(5000)
  } finally {
    s1.close()
    s2.close()
  }

}

object MidiInstrumentDemo extends App with Pausable {
  val s1 = Synth()
  s1.changeInstrument(3)

  try {

    def demo(id: Int) {
      println("--- Instrument id=%d" format id)
      s1.changeInstrument(id)
      s1.pressKey(45, 500, 127)
      pause(100)
      s1.pressKey(39, 500, 100)
      pause(100)
      s1.pressKey(51, 500, 10)
      pause(500)
    }

    (0 to 127) foreach (id => demo(id))
  } finally {
    s1.close()
  }

}

object MidiVelocityDemo extends App with Pausable {

  val s1 = Synth()
  val s2 = Synth()

  try {
    s1.changeInstrument(0)
    s2.changeInstrument(10)

    def demo(velocity: Int) {
      val v1 = velocity
      val v2 = 127 - velocity
      println("v1 v2 %d %d" format (v1, v2))
      s1.pressKey(45, 100, v1)
      pause(100)
      s2.pressKey(39, 100, v2)
      pause(100)
    }

    (0 to (120, 10)) foreach (v => demo(v))
  } finally {
    s1.close()
    s2.close()
  }
}

object MidiReverbDemo extends App with Pausable {

  val s1 = Synth()

  try {
    s1.changeInstrument(115)

    def demo(r: Int) {
      println("reverb %d" format r)
      s1.changeReverb(r)
      s1.pressKey(45, 1000, 80)
      pause(1500)
    }

    (0 to (127, 20)) foreach (v => demo(v))
  } finally {
    s1.close()
  }
}

object MidiManyDemo extends App with Pausable {

  val s1 = Synth()

  try {
    s1.changeInstrument(33)

    def demo() {
      s1.pressKey(45, 800, 80)
      pause(100)
    }

    (0 to 1000) foreach (v => demo())
  } finally {
    s1.close()
  }
}