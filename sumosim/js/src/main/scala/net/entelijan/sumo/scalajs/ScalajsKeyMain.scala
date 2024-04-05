package net.entelijan.sumo.scalajs

import doctus.core.DoctusCanvas
import doctus.core.util.{DoctusUtil, DoctusUtilJs}
import doctus.scalajs.*
import net.entelijan.sumo.gui.example.ManualVsStandstillExample
import net.entelijan.sumo.robot.UpDownLeftRight
import org.scalajs.dom

import scala.scalajs.js.annotation.JSExport

object ScalajsKeyMain {

  // Comes here on every refresh (update)
  @JSExport(name = "ScalajsKeyMain")
  def main(): Unit = {
    // GUI Components form the HTML-Page

    val canvas: dom.HTMLCanvasElement =
      dom.document.getElementById("canvas").asInstanceOf[dom.HTMLCanvasElement]

    val dsc = DoctusSchedulerScalajs

    // Wrap the javascript components
    val dCanvas: DoctusCanvas = DoctusCanvasScalajs(canvas)

    val dup = DoctusKeyScalajs(dom.document.body)
    val ddown = DoctusKeyScalajs(dom.document.body)
    val dleft = DoctusKeyScalajs(dom.document.body)
    val dright = DoctusKeyScalajs(dom.document.body)

    val dUpDownLeftRight = UpDownLeftRight(dup, ddown, dleft, dright, dsc)

    val util: DoctusUtil = new DoctusUtilJs()

    import ImageProviderScalajs._

    new ManualVsStandstillExample(
      dCanvas,
      dUpDownLeftRight,
      None,
      dsc,
      roboBlack,
      roboRed,
      background,
      util
    ).start()
  }

}
