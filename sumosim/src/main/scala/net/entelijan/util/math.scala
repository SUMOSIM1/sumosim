package net.entelijan.util

import scala.math.{ sqrt, Pi, atan, cos, sin, acos, abs }


/**
 * Point in a two dimensional space 
 */
class Point2(val xpos: Double, val ypos: Double) {
  /**
   * Adds a vector to a point. That results in a new Point
   */
  def +(v: Vec2) = new Point2(v.x + xpos, v.y + ypos)
  /**
   * Subtracts a vector to a point. That results in a new Point
   */
  def -(v: Vec2) = new Point2(xpos - v.x, ypos - v.y)
  /**
   * Calculates the distance between two Points
   */
  def distance(p: Point2) = {
    val dx = xpos - p.xpos
    val dy = ypos - p.ypos
    sqrt(dx * dx + dy * dy)
  }
  /**
   * Creates a Vector2 from two points
   */
  def toVec(base: Point2): Vec2 = new Vec2(xpos - base.xpos, ypos - base.ypos)

  def isQuartI: Boolean = {
    val d = toVec(new Point2(0,0)).direction
    d >= 0.0 && d < Pi / 2 
  }
  def isQuartII: Boolean = {
    val d = toVec(new Point2(0,0)).direction
    d >= Pi/2 && d < Pi 
  }
  def isQuartIII: Boolean = {
    val d = toVec(new Point2(0,0)).direction
    (d < -Pi/2 && d >= -Pi) || d ==  Pi 
  }
  def isQuartIV: Boolean = {
    val d = toVec(new Point2(0,0)).direction
    d < 0.0 && d >= -Pi / 2 
  }
  

  override def toString = { "[%.2f, %.2f]" format (xpos, ypos) }
  override def equals(that: Any): Boolean = {
    that match {
      case other: Point2 => xpos == other.xpos && ypos == other.ypos
      case _ => false
    }
  }
  override def hashCode = xpos.hashCode + ypos.hashCode
}

/**
 * Vector in a two dimensional space 
 */
class Vec2(val x: Double, val y: Double) extends VecUtil with TrigUtil {
  /**
   * The norm of a vector is its length
   */
  def norm: Double = sqrt(x * x + y * y)
  /**
   * The direction of the vector in rad
   */
  def direction = {
    if (x > 0) normalize(atan(y / x))
    else if (x < 0) normalize(atan(y / x) + Pi)
    else if (y > 0) Pi / 2
    else if (y < 0) -Pi / 2
    else throw new IllegalArgumentException("direction not defined for (%.2f %.2f)" format (x, y))
  }
  
  /**
   * Rotates the angle for a certain angle in rad
   * Returns a new (immutable) vector
   */
  def rot(rad: Double) = {
    val a = x * cos(rad) - y * sin(rad)
    val b = y * cos(rad) + x * sin(rad)
    new Vec2(a, b)
  }
  /**
   * Multiplication of a scalar with the vector
   * Returns a new (immutable) vector
   */
  def *(v: Double) = new Vec2(v * x, v * y)
  /**
   * Addition of a scalar to the vector
   * Returns a new immutable vector
   */
  def +(v: Vec2) = new Vec2(v.x + x, v.y + y)
  /**
   * Returns the enclosed angle of two vectors
   * The angle starts always from the original vector
   * It is always smaller than pi 
   */
  def enclosedAngle(v: Vec2): Double = {
    val dp = this.dotProduct(v)
    val n1 = norm
    val n2 = v.norm
    val a = acos(dp / (n1 * n2))
    val b = 
      if (v.y < 0) if (x > 0) -a else a
      else if (x < 0) -a else a
    val aDeg = toDeg(b)
    b
  }
  def *(v: Vec2): Double = {
    this.dotProduct(v)
  }
  def dotProduct(v: Vec2): Double = {
    (this.x * v.x) + (this.y * v.y)
  }
  override def toString = { "[%.2f, %.2f]" format (x, y) }

  override def equals(that: Any): Boolean = {
    that match {
      case other: Vec2 => x == other.x && y == other.y
      case _ => false
    }
  }
  override def hashCode = x.hashCode + y.hashCode
}

/**
 * Trigonometric utilities
 */
trait TrigUtil {
  
  /**
   * Brings an angle represented in rad
   * in the intervals [0, pi] or [0, -pi]
   */
  def normalize(rad: Double) = {
    val mod = rad % (2 * Pi)
    if (mod > Pi) mod - 2 * Pi
    else if (mod < -Pi) mod + 2 * Pi
    else mod
  }
  /**
   * Converts a deg represented angle to rad scale
   */
  def toRad(deg: Double) = Pi / 180 * deg
  /**
   * Converts a rad represented angle to deg scale
   */
  def toDeg(rad: Double) = 180 / Pi * rad

  private val o1 = 7 * Pi / 8
  private val o2 = 5 * Pi / 8
  private val o3 = 3 * Pi / 8
  private val o4 = 1 * Pi / 8
  private val o5 = -1 * Pi / 8
  private val o6 = -3 * Pi / 8
  private val o7 = -5 * Pi / 8
  private val o8 = -7 * Pi / 8

  def octal(rad: Double): Int = {
    val n = normalize(rad)
    if (n > o1) 4
    else if (n > o2) 3
    else if (n > o3) 2
    else if (n > o4) 1
    else if (n > o5) 0
    else if (n > o6) 7
    else if (n > o7) 6
    else if (n > o8) 5
    else 4
  }
}


trait VecUtil {

  def pointCircleDistance(radius: Double, position: Point2, direction: Double): Double = {
    def intersection(r: Double, x0: Double, y0: Double, x1: Double, y1: Double): Point2 = {
      val r2 = r * r
      val dy = y1 - y0
      val dx = x1 - x0
      val dx2 = dx * dx
      val dy2 = dy * dy
      val a = x0 * dx
      val b = y0 * dy
      val root = sqrt(2 * a * b + dx2 * (r2 - y0 * y0) + dy2 * (r2 - x0 * x0))
      val t1 = -(a + b - root) / (dx2 + dy2)
      new Point2(x0 + t1 * dx, y0 + t1 * dy)
    }
    def outside = {
      position.distance(new Point2(0, 0)) > radius
    }
    if (outside) -1
    else {
      val x2 = position.xpos + cos(direction)
      val y2 = position.ypos + sin(direction)
      val inter = intersection(radius, position.xpos, position.ypos, x2, y2)
      inter.distance(position)
    }
  }
  
  def isPointInSector(origin: Point2, originDirection: Double, openingAngle: Double, point: Point2): Boolean = {
    val v = point.toVec(origin)
    val dir = v.direction
    val diff = abs(originDirection - dir)
    diff <= openingAngle
  }
}

