package net.entelijan.sumo.gui.renderer

import doctus.core.color.DoctusColorWhite
import doctus.core.text.DoctusFontMonospace
import doctus.core.util.DoctusPoint
import doctus.core.{DoctusColor, DoctusGraphics, DoctusSchedulerStopper}
import net.entelijan.sumo.commons.Updatable

// TODO Make it somehow scalable. Eventually not here but in implementations
abstract class RenderUniverse extends Updatable

trait SumoGuiExample {
  def name: String
  def start(): DoctusSchedulerStopper
}

case class RobotRenderInfo(
    mainColor: DoctusColor,
    desc: String
)

object RenderUtil {

  def writeText(
      g: DoctusGraphics,
      fontSize: Double,
      info: String,
      robotRenderInfo1: RobotRenderInfo,
      robotRenderInfo2: RobotRenderInfo
  ): Unit = {
    g.fill(DoctusColorWhite, 255)
    g.textSize(fontSize)
    g.textFont(DoctusFontMonospace)

    g.text(info, DoctusPoint(10, fontSize), 0)
    g.text(robotRenderInfo1.desc, DoctusPoint(30, 2 * fontSize), 0)
    g.text(robotRenderInfo2.desc, DoctusPoint(30, 3 * fontSize), 0)

    g.fill(robotRenderInfo1.mainColor, 255)
    g.ellipse(13, 1.7 * fontSize, 6, 6)

    g.fill(robotRenderInfo2.mainColor, 255)
    g.ellipse(13, 2.7 * fontSize, 6, 6)

  }

}
