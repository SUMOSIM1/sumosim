package net.entelijan.sumo.scalajs

import doctus.core.DoctusCanvas
import doctus.scalajs._
import net.entelijan.sumo.gui.example.ManualVsStandstillExample
import net.entelijan.sumo.robot.{ ControlerValue, UpDownLeftRight }
import org.scalajs.dom
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.raw.HTMLCanvasElement
import net.entelijan.sumo.gui.renderer.SumoBlue
import net.entelijan.sumo.gui.renderer.SumoViolet
import doctus.core.DoctusImage

@JSExport(name = "ScalajsKeyMain")
object ScalajsKeyMain {

  // Comes here on every refresh (update)
  @JSExport
  def main(): Unit = {
    // GUI Components form the HTML-Page

    val canvas: HTMLCanvasElement = dom.document.getElementById("canvas").asInstanceOf[HTMLCanvasElement]

    val dsc = DoctusSchedulerScalajs

    // Wrap the javascript components
    val dcanvas: DoctusCanvas = DoctusCanvasScalajs(canvas)

    val dup = DoctusActivatableScalajsKey(dom.document.body, KeyCode.up)
    val ddown = DoctusActivatableScalajsKey(dom.document.body, KeyCode.down)
    val dleft = DoctusActivatableScalajsKey(dom.document.body, KeyCode.left)
    val dright = DoctusActivatableScalajsKey(dom.document.body, KeyCode.right)

    val d = UpDownLeftRight(dup, ddown, dleft, dright, dsc)

    import ImageProviderScalajs._

    new ManualVsStandstillExample(dcanvas, d, None, dsc, roboBlack , roboRed, background).start()
  }

}

