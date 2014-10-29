package net.entelijan.util

import org.scalatest.FunSuite

class TimelineSuite extends FunSuite with Pausable {

  /**
   * Class that provides functions and collects the names of the methods called.
   * Used for testing timeline
   */
  class FunProvider {
    var collected = List.empty[String]

    def f1(): Unit = collected = collected ::: List("1")
    def f2(): Unit = collected = collected ::: List("2")
    def f3(): Unit = collected = collected ::: List("3")

  }
  
  def _100ms(f: () => Unit) {
    for (i <- 1 to 10) {
      f()
      pause(10)
    }
    
  }

  test("Two states. Multiple actions") {
    val fp: FunProvider = new FunProvider

    val s1 = List(
      TimelineEvent(10, Ext(fp.f3)),
      TimelineEvent(20, Ext(fp.f1)),
      TimelineEvent(31, Ext(fp.f3)),
      TimelineEvent(40, Transition(() => "s2")))

    val s2 = List(
      TimelineEvent(10, Ext(fp.f2)),
      TimelineEvent(3000, Transition(() => "s1")))

    val states = Map("s1" -> s1, "s2" -> s2)
    val ts = StatefulTimeline("s1", states)

    _100ms(() => ts.exec())
    
    assert(fp.collected === List("3", "1", "3", "2"))

  }

  test("Two states. One action at each with") {
    val fp: FunProvider = new FunProvider

    val s1 = List(
      TimelineEvent(50, Ext(fp.f1)),
      TimelineEvent(60, Transition(() => "s2")))

    val s2 = List(
      TimelineEvent(10, Ext(fp.f2)),
      TimelineEvent(100, Transition(() => "s1")))

    val states = Map("s1" -> s1, "s2" -> s2)
    val ts = StatefulTimeline("s1", states)

    _100ms(() => ts.exec())
    
    assert(fp.collected === List("1", "2"))

  }

  test("Two states. One looping state") {
    val fp: FunProvider = new FunProvider

    val s1 = List(
      TimelineEvent(30, Ext(fp.f1)),
      TimelineEvent(55, Transition(() => "s1")))

    val states = Map("s1" -> s1)
    val ts = StatefulTimeline("s1", states)

    _100ms(() => ts.exec())

    assert(fp.collected === List("1", "1"))

  }

}