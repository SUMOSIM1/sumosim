package doctus.core.util

trait DoctusUtil {

  private val shrinkFactor = 0.04

  // Calculates the optimal font size in px, based on the current screen resolution.
  def fontSize: Double = screenResolution.toDouble * 3.5 * shrinkFactor

  // Calculate the optimal border with for components, based in the screen resolution
  def borderWidth: Double = screenResolution.toDouble * 2.0 * shrinkFactor

  // Adapt a length based on the current screen resolution
  def adaptLength(length: Int): Int =
    (screenResolution * shrinkFactor * length).toInt

  // Returns the curren screen resolution in DPI
  def screenResolution: Int

}
