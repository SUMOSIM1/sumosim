package net.entelijan.sumo.scalajs

import scala.scalajs.js.annotation.JSExport

import org.scalajs.dom
import org.scalajs.dom.raw.HTMLCanvasElement
import org.scalajs.dom.raw.HTMLDivElement

import doctus.core.DoctusCanvas
import doctus.scalajs._
import net.entelijan.sumo.gui.example._

@JSExport(name="ScalajsMain")
object ScalajsMain {

  // Comes here on every refresh (update)
  @JSExport
  def main(): Unit = {
    // GUI Components form the HTML-Page

    val canvas: HTMLCanvasElement = dom.document.getElementById("canvas").asInstanceOf[HTMLCanvasElement]

    val contrAcc: HTMLDivElement = dom.document.getElementById("contrAcc").asInstanceOf[HTMLDivElement]
    val contrTurn: HTMLDivElement = dom.document.getElementById("contrTurn").asInstanceOf[HTMLDivElement]

    val sc = DoctusSchedulerScalajs

    // Wrap the javascript components
    val dcanvas: DoctusCanvas = DoctusCanvasScalajs(canvas)

    new RotatingVsForwardBackwardExample(dcanvas, None, sc).start()
  }

}

