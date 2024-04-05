package net.entelijan.sumo.scalajs

import scala.scalajs.js.annotation.JSExport

import org.scalajs.dom

import doctus.core.DoctusCanvas
import doctus.core.util.DoctusUtilJs
import doctus.scalajs._
import net.entelijan.sumo.gui.example._

object ScalajsMain {

  // Comes here on every refresh (update)
  @JSExport(name = "ScalajsMain")
  def main(): Unit = {
    // GUI Components form the HTML-Page

    val canvas: dom.HTMLCanvasElement =
      dom.document.getElementById("canvas").asInstanceOf[dom.HTMLCanvasElement]

    dom.document.getElementById("contrAcc").asInstanceOf[dom.HTMLDivElement]
    dom.document.getElementById("contrTurn").asInstanceOf[dom.HTMLDivElement]

    val sc = DoctusSchedulerScalajs

    // Wrap the javascript components
    val dCanvas: DoctusCanvas = DoctusCanvasScalajs(canvas)
    val util = DoctusUtilJs()

    new RotatingVsForwardBackwardExample(dCanvas, None, sc, None, util).start()
  }

}
