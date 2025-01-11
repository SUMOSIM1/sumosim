package net.entelijan.sumo.gui.renderer

import doctus.core._
import doctus.core.color._
import doctus.core.util._
import math.*

object Paint {

  private val fieldPadding = 0.1

  def paintSimple(
      graphics: DoctusGraphics,
      width: Int,
      height: Int,
      util: DoctusUtil,
      robots: Seq[SimpleUniverseRobot],
      info: String,
      color1: DoctusColor,
      color2: DoctusColor,
      desc1: String,
      desc2: String
  ): Unit = {
    val canvasSmallerSide = min(width, height)

    val scaleFact = canvasSmallerSide.asInstanceOf[Double] / 1000.0
    val robotSize = (100 * scaleFact).asInstanceOf[Int]

    val backColor = DoctusColorRgb(100, 120, 110)
    val fieldColor = DoctusColorRgb(50, 80, 60)

    def drawBackground(): Unit = {
      graphics.fill(backColor, 255)
      graphics.rect(0, 0, width, height)
      val mw = min(width, height)
      val padding = (mw * fieldPadding).toInt
      val ow = mw - padding * 2
      val or = ow / 2
      graphics.fill(fieldColor, 255)
      graphics.stroke(DoctusColorBlack, 255)
      graphics.strokeWeight(1)
      if (mw == width) {
        val ox = (padding + ow / 2.0).toInt
        val oy = (padding + (height - mw) / 2.0 + ow / 2.0).toInt
        graphics.ellipse(ox, oy, or, or)
      } else {
        val ox = (padding + (width - mw) / 2.0 + ow / 2.0).toInt
        val oy = (padding + ow / 2.0).toInt
        graphics.ellipse(ox, oy, or, or)
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
            robot.pos.ypos * scaleFact + width / 2,
            robot.pos.xpos * scaleFact + height / 2
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
          AffinePoliPoint(a, 0)
        )
      }
      val rect: List[DoctusPoint] = transformShape(rectBase, robot)
      graphics.fill(robot.color, 255)
      graphics.stroke(DoctusColorBlack, 255)
      graphics.poli(rect)

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
      graphics.poli(arrow)

    }

    val fontSize = util.fontSize
    // println(s"### text render Simple :'${info}'")
    drawBackground()
    robots.foreach(r => drawRobot(r))
    RenderUtil.writeText(
      graphics,
      fontSize,
      info,
      RobotRenderInfo(color1, desc1),
      RobotRenderInfo(color2, desc2)
    )
  }

}
