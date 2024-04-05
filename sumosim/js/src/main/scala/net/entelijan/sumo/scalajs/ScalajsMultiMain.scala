package net.entelijan.sumo.scalajs

import doctus.core.color.*
import doctus.core.util.{DoctusUtil, DoctusUtilJs}
import doctus.core.{DoctusCanvas, DoctusColor, DoctusImage}
import doctus.scalajs.*
import net.entelijan.sumo.gui.example.MultiController
import net.entelijan.sumo.gui.renderer.*
import net.entelijan.sumo.robot.UpDownLeftRight
import org.scalajs.dom

import scala.scalajs.js.annotation.JSExport

object ScalajsMultiMain {

  // Comes here on every refresh (update)
  @JSExport(name = "ScalajsMultiMain")
  def main(): Unit = {
    // Create a scheduler
    val dsc = DoctusSchedulerScalajs

    // GUI Components form the HTML-Page
    val canvas: dom.HTMLCanvasElement =
      dom.document.getElementById("canvas").asInstanceOf[dom.HTMLCanvasElement]
    val dcanvas: DoctusCanvas = DoctusCanvasScalajs(canvas)
    val dup = DoctusKeyScalajs(dom.document.body)
    val ddown = DoctusKeyScalajs(dom.document.body)
    val dleft = DoctusKeyScalajs(dom.document.body)
    val dright = DoctusKeyScalajs(dom.document.body)

    val da = DoctusActivatableScalajs(
      dom.document.getElementById("a").asInstanceOf[dom.HTMLElement]
    )
    val db = DoctusActivatableScalajs(
      dom.document.getElementById("b").asInstanceOf[dom.HTMLElement]
    )
    val dc = DoctusActivatableScalajs(
      dom.document.getElementById("c").asInstanceOf[dom.HTMLElement]
    )
    val dd = DoctusActivatableScalajs(
      dom.document.getElementById("d").asInstanceOf[dom.HTMLElement]
    )

    val dUpDownLeftRight = UpDownLeftRight(dup, ddown, dleft, dright, dsc)

    val util: DoctusUtil = new DoctusUtilJs()

    import ImageProviderScalajs._

    MultiController(
      da,
      db,
      dc,
      dd,
      dcanvas,
      dUpDownLeftRight,
      dsc,
      sumoBlue,
      sumoViolet,
      roboRed,
      roboBlack,
      background,
      util
    )
  }

}

object ImageProviderScalajs {

  def sumoBlue: ImageProvider = new SumoBlue {
    def doctusImage(index: Int): DoctusImage = DoctusImageScalajs(
      "target/scala-2.11/classes/robot-2d/sumo-blue/img%d.png" format index
    )

    override def mainColor: DoctusColor = DoctusColorBlue
  }

  def sumoViolet: ImageProvider = new SumoViolet {
    def doctusImage(index: Int): DoctusImage = DoctusImageScalajs(
      "target/scala-2.11/classes/robot-2d/sumo-violet/img%d.png" format index
    )

    override def mainColor: DoctusColor = DoctusColorMagenta
  }

  def roboRed: ImageProvider = new RoboRed {
    def doctusImage(index: Int): DoctusImage = DoctusImageScalajs(
      "target/scala-2.11/classes/robot-2d/robo2/img%d.png" format index
    )

    override def mainColor: DoctusColor = DoctusColorRed
  }

  def roboBlack: ImageProvider = new RoboBlack {
    def doctusImage(index: Int): DoctusImage = DoctusImageScalajs(
      "target/scala-2.11/classes/robot-2d/robo1/img%d.png" format index
    )

    override def mainColor: DoctusColor = DoctusColorBlack
  }

  val background: DoctusImage = DoctusImageScalajs(
    "target/scala-2.11/classes/robot-2d/bg/pad.png"
  )

}
