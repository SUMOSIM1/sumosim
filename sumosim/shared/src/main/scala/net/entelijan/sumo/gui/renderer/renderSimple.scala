package net.entelijan.sumo.gui.renderer

import net.entelijan.sumo.commons.*
import doctus.core.*
import doctus.core.color.*
import doctus.core.util.*
import doctus.core.util.DoctusVector
import net.entelijan.sumo.util.Point2

case class AffinePoliPoint(x: Double, y: Double) extends DoctusPoint {
  import scala.math._
  def scale(dx: Double, dy: Double): AffinePoliPoint =
    AffinePoliPoint((x * dx).toInt, (y * dy).toInt)
  def translate(dx: Double, dy: Double): AffinePoliPoint =
    AffinePoliPoint((x + dx).toInt, (y + dy).toInt)
  def rotate(angle: Double): AffinePoliPoint = {
    val r = sqrt(x * x + y * y)
    val a0 = atan2(y, x)
    val a1 = angle + a0
    AffinePoliPoint((r * cos(a1)).toInt, (r * sin(a1)).toInt)
  }

  // The following method should not be used
  def +(v: DoctusVector): DoctusPoint = throw IllegalStateException(
    "Should not be used"
  )
  def -(v: DoctusVector): DoctusPoint = throw IllegalStateException(
    "Should not be used"
  )
  def -(v: DoctusPoint): DoctusVector = throw IllegalStateException(
    "Should not be used"
  )

}

object SimpleUniverse {

  private def createUniverse(
      canvas: DoctusCanvas,
      color1: DoctusColor,
      color2: DoctusColor,
      util: DoctusUtil
  ): RenderUniverse = {
    new SimpleUniverse(canvas, color1, color2, util)
  }

  def createDefaultUniverse(
      canvas: DoctusCanvas,
      util: DoctusUtil
  ): RenderUniverse = {
    createUniverse(canvas, DoctusColorYellow, DoctusColorOrange, util)
  }

}

class SimpleUniverseRobot(
    xpos: Double,
    ypos: Double,
    dir: Double,
    val color: DoctusColor,
    val util: DoctusUtil
) {
  def pos: Point2 = Point2(xpos, ypos)

  def direction: Double = dir
}

class SimpleUniverse(
    canvas: DoctusCanvas,
    color1: DoctusColor,
    color2: DoctusColor,
    util: DoctusUtil
) extends RenderUniverse {

  private var info = ""
  private var desc1 = ""
  private var desc2 = ""
  private var robots: Seq[SimpleUniverseRobot] = Seq()

  def receive(umsg: UpdatableMsg): Unit = {
    umsg match {
      case msg: SumoSimulationMessage =>
        val r1 =
          new SimpleUniverseRobot(msg.xpos1, msg.ypos1, msg.dir1, color1, util)
        val r2 =
          new SimpleUniverseRobot(msg.xpos2, msg.ypos2, msg.dir2, color2, util)
        robots = Seq(r1, r2)
        info = msg.info
        canvas.repaint()
      case InfoMessage(msg) =>
        info = msg
        canvas.repaint()
      case StartGameEventMessage(msg1, msg2) =>
        println(s"## received Simple $msg1 $msg2")
        desc1 = msg1
        desc2 = msg2
        canvas.repaint()
      case net.entelijan.sumo.commons.FinishedGameEventMessage(_, _) =>
      case CollisionEventMessage                                     =>
    }

  }

  canvas.onRepaint((g: DoctusGraphics) => {

    val canvasWidth = canvas.width
    val canvasHeight = canvas.height
    Paint.paintSimple(
      g,
      canvasWidth,
      canvasHeight,
      util,
      robots,
      info,
      color1,
      color2,
      desc1,
      desc2
    )

  })

}
