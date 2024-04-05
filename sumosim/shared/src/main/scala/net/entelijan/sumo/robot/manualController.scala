package net.entelijan.sumo.robot

import doctus.core._

case class UpDownLeftRight(
    up: DoctusKey,
    down: DoctusKey,
    left: DoctusKey,
    right: DoctusKey,
    sched: DoctusScheduler
) {
  def upControllerValue: ControllerValue = ControllerValue(up, sched)

  def downControllerValue: ControllerValue = ControllerValue(down, sched)

  def leftControllerValue: ControllerValue = ControllerValue(left, sched)

  def rightControllerValue: ControllerValue = ControllerValue(right, sched)
}

case class ControllerValue(activatable: DoctusKey, sched: DoctusScheduler)

class ManualController(
    val name: String = "Manual",
    val shortName: String = "man",
    comp: UpDownLeftRight
) extends DiffDriveController[NullSensor] {

  private var left = 0.0
  private var right = 0.0
  private var forw = 0.0
  private var backw = 0.0

  override def describe: String = "Differential drive with no sensor"

  private trait KeyListener {

    private trait State

    private case object Idle extends State

    private case object Increase extends State

    private case object Decrease extends State

    def sched: DoctusScheduler

    private var value = 0.0
    private var state: State = Idle

    private def maxValue = 1.0

    private def minValue = 0.0

    private def incValue = 0.01

    private def decValue = 0.008

    private def pauseTime = 5 // ms

    def valueChanged(value: Double): Unit

    sched.start(
      () => {
        if (state == Increase && value < maxValue) {
          value = scala.math.min(value + incValue, maxValue)
          valueChanged(value)
        }
        if (state == Decrease && value > minValue) {
          value = scala.math.max(value - decValue, minValue)
          valueChanged(value)
        }
      },
      pauseTime
    )

    def startIncreaseValue(): Unit = {
      state = Increase
    }

    def startDecreaseValue(): Unit = {
      state = Decrease
    }
  }

  private val leftKeyListener = new KeyListener {
    def sched: DoctusScheduler = comp.leftControllerValue.sched

    def valueChanged(value: Double): Unit = {
      left = value
    }
  }
  private val rightKeyListener = new KeyListener {
    def sched: DoctusScheduler = comp.rightControllerValue.sched

    def valueChanged(value: Double): Unit = {
      right = value
    }
  }
  private val forwKeyListener = new KeyListener {
    def sched: DoctusScheduler = comp.upControllerValue.sched

    def valueChanged(value: Double): Unit = {
      forw = value
    }
  }
  private val backwKeyListener = new KeyListener {
    def sched: DoctusScheduler = comp.downControllerValue.sched

    def valueChanged(value: Double): Unit = {
      backw = value
    }
  }

  comp.leftControllerValue.activatable.onKeyPressed { keyCode =>
    if (keyCode == DKC_Left) {
      leftKeyListener.startIncreaseValue()
    }
  }
  comp.leftControllerValue.activatable.onKeyReleased { keyCode =>
    if (keyCode == DKC_Left) {
      leftKeyListener.startDecreaseValue()
    }
    comp.rightControllerValue.activatable.onKeyPressed { keyCode =>
      if (keyCode == DKC_Right) {
        rightKeyListener.startIncreaseValue()
      }
    }
    comp.rightControllerValue.activatable.onKeyReleased { keyCode =>
      if (keyCode == DKC_Right) {
        rightKeyListener.startDecreaseValue()
      }
    }
    comp.upControllerValue.activatable.onKeyPressed { keyCode =>
      if (keyCode == DKC_Up) {
        forwKeyListener.startIncreaseValue()
      }
    }
    comp.upControllerValue.activatable.onKeyReleased { keyCode =>
      if (keyCode == DKC_Up) {
        forwKeyListener.startDecreaseValue()
      }
    }
    comp.downControllerValue.activatable.onKeyPressed { keyCode =>
      if (keyCode == DKC_Down) {
        backwKeyListener.startIncreaseValue()
      }
    }
    comp.downControllerValue.activatable.onKeyReleased { keyCode =>
      if (keyCode == DKC_Down) {
        backwKeyListener.startDecreaseValue()
      }
    }
  }

  // Decrease that value if the controller reacts to sharp
  def forwardFactor = 1.0

  // Decrease that value if the controller reacts to sharp
  def rotFactor = 1.0

  def takeStep(sensor: NullSensor): DiffDriveValues = {
    val rightWheel =
      (left - right) * 7 * rotFactor + (forw - backw) * 15.0 * forwardFactor
    val leftWheel =
      (right - left) * 7 * rotFactor + (forw - backw) * 15.0 * forwardFactor
    DiffDriveValues(leftWheel, rightWheel)
  }

}
