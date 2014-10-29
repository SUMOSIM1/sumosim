package net.entelijan.sumo.gui.renderer

import net.entelijan.sumo.commons._
import net.entelijan.util._
import doctus.core.DoctusCanvas
import doctus.core.DoctusGraphics
import doctus.core.color._
import doctus.core.DoctusImage
import doctus.core.util.DoctusPoint
import doctus.core.text.DoctusFontMonospace

class R2DRobot(xpos: Double, ypos: Double, dir: Double, val imgProv: ImageProvider) {
  def pos = new Point2(xpos, ypos)
  def direction = dir
}


trait OctalImageProvider extends ImageProvider with VecUtil with TrigUtil {

  private lazy val scaledImages = (0 to 7) map (i => inputProvider(i))

  def scale: Double

  def image(direction: Double): Img = {
    val index: Int = octal(direction)
    inputProvider(index)
  }

  protected def inputProvider(index: Int): Img
}



trait ImageProviderImpl extends OctalImageProvider {
  def width: Int
  def height: Int 
  def xoff: Double
  def yoff: Double
  def scale: Double
  protected def inputProvider(index: Int): Img = Img(doctusImage(index), width, height, scale)
  def doctusImage(index: Int): DoctusImage
}



abstract class DefaultImageProviderImpl(val width: Int, val height: Int, val xoff: Double, val yoff: Double, val scale: Double) extends ImageProviderImpl

abstract class SumoViolet extends DefaultImageProviderImpl(200, 247, 0.5, 0.68, 1.0)
abstract class SumoBlue extends DefaultImageProviderImpl(243, 309, 0.5, 0.70, 0.8)
abstract class RoboBlack extends DefaultImageProviderImpl(150, 146, 0.5, 0.7, 1.0)
abstract class RoboRed extends DefaultImageProviderImpl(150, 188, 0.5, 0.8, 0.9)

class R2DUniverse(canvas: DoctusCanvas, fillFactor: Double, val provider1: ImageProvider, val provider2: ImageProvider, bgImg: DoctusImage)
  extends RenderUniverse(canvas) {

  var scaleFactor = 1.0
  var robots: Seq[R2DRobot] = Seq()
  var info = "Initial Info String"

  def receive(umsg: UpdatableMsg) {
    umsg match {
      case msg: SumoSimulationMessage =>
        val r1 = new R2DRobot(msg.xpos1, msg.ypos1, msg.dir1, provider1)
        val r2 = new R2DRobot(msg.xpos2, msg.ypos2, msg.dir2, provider2)
        robots = Seq(r1, r2)
        info = msg.info
        canvas.repaint()
      case InfoMessage(msg) =>
        info = msg
        canvas.repaint()
      case any: UpdatableMsg => ()
    }
  }

  canvas.onRepaint((g: DoctusGraphics) => {

    val cw = canvas.width.toDouble
    val ch = canvas.height.toDouble
    val ratio = ch / cw
    scaleFactor = fillFactor * (if (ratio < 0.7) ch / (810 * 0.7) else cw / 810)

    def calculateScreenPosition(pos: Point2) = {
      // 0.7 in respect to the pad.png
      val x = pos.xpos * 0.7 * scaleFactor
      val y = pos.ypos * scaleFactor
      val xoff = canvas.height * 0.5
      val yoff = canvas.width * 0.5
      new Point2(y + yoff, x + xoff)
    }

    def paintBackground() {
      g.fill(DoctusColorBlack, 255)
      g.rect(0, 0, canvas.width, canvas.height)
      val centerScreenPos = calculateScreenPosition(new Point2(0, 0))
      val x = centerScreenPos.xpos - 430 * scaleFactor
      val y = centerScreenPos.ypos - 310 * scaleFactor
      val sf = scaleFactor * 0.89
      g.image(bgImg.scale(sf), x.toInt, y.toInt)
    }

    def paintRobot(robot: R2DRobot) {
      val p = calculateScreenPosition(robot.pos)
      val img = robot.imgProv.image(robot.direction)
      val w = img.width * scaleFactor * img.scaleFact
      val h = img.height * scaleFactor * img.scaleFact
      val x = p.xpos - (w * robot.imgProv.xoff)
      val y = p.ypos - (h * robot.imgProv.yoff)
      val sf = scaleFactor * img.scaleFact
      g.image(img.img.scale(sf), x.toInt, y.toInt)
    }

    def sortRobots = {
      def cameraDistance(r: R2DRobot) = r.pos.xpos - 3000
      robots sortWith ((r1, r2) => cameraDistance(r1) < cameraDistance(r2))
    }

    paintBackground()
    sortRobots.foreach(r => paintRobot(r))
    g.fill(DoctusColorWhite, 255)
    g.textSize(16)
    g.textFont(DoctusFontMonospace)
    g.text(info, DoctusPoint(10, 30), 0)

  })
}

/**
 * Scale images and buffer them for quick access
 */
case class Img(img: DoctusImage, width: Int, height: Int, scaleFact: Double)

/**
 * Define the interface for image providers
 */
trait ImageProvider {
  /**
   * Returns a scaled image for a direction.
   * @param direction The direction in RAD
   */
  def image(direction: Double): Img
  /**
   * The relative offset of the turning point of the provided images.
   * 0.5 means the turning point is in the middle
   */
  def xoff: Double
  /**
   * The relative offset of the turning point of the provided images.
   * 1.0 means the turning point is at the bottom line of the image
   */
  def yoff: Double
}



