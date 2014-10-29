package net.entelijan.sumo.scalajs

import doctus.core.DoctusCanvas
import doctus.scalajs._
import net.entelijan.sumo.gui.example.{ MultiController, ManualVsStandstillExample }
import net.entelijan.sumo.robot.{ ControlerValue, UpDownLeftRight }
import org.scalajs.dom
import org.scalajs.jquery.jQuery
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.raw.HTMLCanvasElement
import org.scalajs.dom.raw.HTMLElement
import net.entelijan.sumo.gui.renderer._
import doctus.core.DoctusImage

@JSExport(name = "ScalajsMultiMain")
object ScalajsMultiMain {

  // Comes here on every refresh (update)
  @JSExport
  def main(): Unit = {
    // Create a scheduler
    val dsc = DoctusSchedulerScalajs

    // GUI Components form the HTML-Page
    val canvas: HTMLCanvasElement = dom.document.getElementById("canvas").asInstanceOf[HTMLCanvasElement]
    val dcanvas: DoctusCanvas = DoctusCanvasScalajs(canvas)
    val dup = DoctusActivatableScalajsKey(dom.document.body, KeyCode.up)
    val ddown = DoctusActivatableScalajsKey(dom.document.body, KeyCode.down)
    val dleft = DoctusActivatableScalajsKey(dom.document.body, KeyCode.left)
    val dright = DoctusActivatableScalajsKey(dom.document.body, KeyCode.right)

    val da = DoctusActivatableScalajs(dom.document.getElementById("a").asInstanceOf[HTMLElement])
    val db = DoctusActivatableScalajs(dom.document.getElementById("b").asInstanceOf[HTMLElement])
    val dc = DoctusActivatableScalajs(dom.document.getElementById("c").asInstanceOf[HTMLElement])
    val dd = DoctusActivatableScalajs(dom.document.getElementById("d").asInstanceOf[HTMLElement])

    val d = new UpDownLeftRight(dup, ddown, dleft, dright, dsc)

    import ImageProviderScalajs._
    
    MultiController(da, db, dc, dd, dcanvas, d, dsc, sumoBlue, sumoViolet, roboRed, roboBlack, background)
  }

}

object ImageProviderScalajs {

  def sumoBlue = new SumoBlue {                   
    def doctusImage(index: Int): DoctusImage = DoctusImageScalajs("target/scala-2.11/classes/robot-2d/sumo-blue/img%d.png" format index)
  }
  def sumoViolet = new SumoViolet {
    def doctusImage(index: Int): DoctusImage = DoctusImageScalajs("target/scala-2.11/classes/robot-2d/sumo-violet/img%d.png" format index)
  }
  def roboRed = new RoboRed {
    def doctusImage(index: Int): DoctusImage = DoctusImageScalajs("target/scala-2.11/classes/robot-2d/robo2/img%d.png" format index)
  }
  def roboBlack = new RoboBlack {                                  
    def doctusImage(index: Int): DoctusImage = DoctusImageScalajs("target/scala-2.11/classes/robot-2d/robo1/img%d.png" format index)
  }
  val background = new DoctusImageScalajs("target/scala-2.11/classes/robot-2d/bg/pad.png")

}


