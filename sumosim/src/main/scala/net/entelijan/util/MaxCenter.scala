package net.entelijan.util

/**
 * Center an inner rectangle into an outer rectangle.
 */

trait MaxCenterRect {
  def width: Int
  def height: Int
}

case class MaxCenterResult(offsetX: Int, offsetY: Int, scale: Double)

object MaxCenter {

  def calc(outer: MaxCenterRect, inner: MaxCenterRect): MaxCenterResult = {
    val outerRatio = outer.height.toDouble / outer.width
    val innerRatio = inner.height.toDouble / inner.width
    if (outerRatio < innerRatio) {
      val scale = outer.height.toDouble / inner.height
      val off = ((outer.width - (inner.width * scale)) / 2.0).toInt
      MaxCenterResult(off, 0, scale)
    } else {
      val scale = outer.width.toDouble / inner.width
      val off = ((outer.height - (inner.height * scale)) / 2.0).toInt
      MaxCenterResult(0, off, scale)
    }
  }

}
