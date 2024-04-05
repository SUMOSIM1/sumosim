package net.entelijan.sumo.gui.renderer

import net.entelijan.sumo.commons._
import doctus.core._
import doctus.core.color._
import doctus.core.util._
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

  private val fieldPadding = 0.1

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
    import scala.math._

    val canvasWidth = canvas.width
    val canvasHeight = canvas.height
    val canvasSmallerSide = min(canvasWidth, canvasHeight)

    val scaleFact = canvasSmallerSide.asInstanceOf[Double] / 1000.0
    val robotSize = (100 * scaleFact).asInstanceOf[Int]

    val backColor = DoctusColorRgb(100, 120, 110)
    val fieldColor = DoctusColorRgb(50, 80, 60)

    def drawBackground(): Unit = {
      g.fill(backColor, 255)
      g.rect(0, 0, canvasWidth, canvasHeight)
      val mw = min(canvasWidth, canvasHeight)
      val padding = (mw * fieldPadding).toInt
      val ow = mw - padding * 2
      val or = ow / 2
      g.fill(fieldColor, 255)
      g.stroke(DoctusColorBlack, 255)
      g.strokeWeight(1)
      if (mw == canvasWidth) {
        val ox = (padding + ow / 2.0).toInt
        val oy = (padding + (canvasHeight - mw) / 2.0 + ow / 2.0).toInt
        g.ellipse(ox, oy, or, or)
      } else {
        val ox = (padding + (canvasWidth - mw) / 2.0 + ow / 2.0).toInt
        val oy = (padding + ow / 2.0).toInt
        g.ellipse(ox, oy, or, or)
      }
    }
    def drawRobot(robot: SimpleUniverseRobot): Unit = {

      def transformShape(
          shape: List[AffinePoliPoint],
          robot: SimpleUniverseRobot
      ): List[AffinePoliPoint] = {
        val a = shape.map(p => p.translate(-robotSize / 2, -robotSize / 2))
        val b = a.mapConserve(p => p.rotate(-robot.direction))
        b.map(p =>
          p.translate(
            robot.pos.ypos * scaleFact + canvasWidth / 2,
            robot.pos.xpos * scaleFact + canvasHeight / 2
          )
        )
      }

      val rectBase = {
        val s = (100.0 * scaleFact).toInt
        val a = s / 3.0
        val b = 2.0 * s / 3.0
        List(
          AffinePoliPoint(0, a),
          AffinePoliPoint(0, b),
          AffinePoliPoint(a, s),
          AffinePoliPoint(b, s),
          AffinePoliPoint(s, b),
          AffinePoliPoint(s, a),
          AffinePoliPoint(b, 0),
          AffinePoliPoint(a, 0),
        )
      }
      val rect: List[DoctusPoint] = transformShape(rectBase, robot)
      g.fill(robot.color, 255)
      g.stroke(DoctusColorBlack, 255)
      g.poli(rect)

      val arrowBase = {
        val shape = List(
          AffinePoliPoint(1, 0),
          AffinePoliPoint(1, 2),
          AffinePoliPoint(0, 1),
          AffinePoliPoint(2, 1),
          AffinePoliPoint(1, 2)
        )
        val sf = 50 * scaleFact
        shape.map(p => p.scale(sf, sf))
      }
      val arrow = transformShape(arrowBase, robot)
      g.poli(arrow)

    }
    val fontSize = util.fontSize
    // println(s"### text render Simple :'${info}'")
    drawBackground()
    robots.foreach(r => drawRobot(r))
    RenderUtil.writeText(
      g,
      fontSize,
      info,
      RobotRenderInfo(color1, desc1),
      RobotRenderInfo(color2, desc2)
    )

  })

}
