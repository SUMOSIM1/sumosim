package net.entelijan.util

import org.scalatest.FunSuite

/**
 * Testcases for MaxCenter
 */
class MaxCenterSuite extends FunSuite {

  case class R(width: Int, height: Int) extends MaxCenterRect

  test("Scaling 1. Inner equals outer") {
    val re = MaxCenter.calc(R(10, 5), R(10, 5))
    assert(re === MaxCenterResult(0, 0, 1.0))
  }
  test("Scaling 1. Outer wider than inner") {
    val re = MaxCenter.calc(R(20, 5), R(10, 5))
    assert(re === MaxCenterResult(5, 0, 1.0))
  }

  test("Scaling 1. Outer higher than inner") {
    val re = MaxCenter.calc(R(20, 30), R(20, 10))
    assert(re === MaxCenterResult(0, 10, 1.0))
  }

  test("Scaling 1. Another outer wider than inner") {
    val re = MaxCenter.calc(R(40, 5), R(10, 5))
    assert(re === MaxCenterResult(15, 0, 1.0))
  }

  test("Scaling 2. Outer wider than inner") {
    val re = MaxCenter.calc(R(40, 6), R(5, 3))
    assert(re === MaxCenterResult(15, 0, 2.0))
  }

  test("Scaling 2. Outer higher than inner") {
    val re = MaxCenter.calc(R(20, 30), R(10, 5))
    assert(re === MaxCenterResult(0, 10, 2.0))
  }

  test("Scaling 0.5. Outer higher than inner") {
    val re = MaxCenter.calc(R(20, 30), R(40, 20))
    assert(re === MaxCenterResult(0, 10, 0.5))
  }


}
