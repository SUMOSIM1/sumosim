package net.entelijan.util

import utest._

class TimelineSuite extends TestSuite with Pausable {

  /** Class that provides functions and collects the names of the methods
    * called. Used for testing timeline
    */
  class FunProvider {
    var collected = List.empty[String]

    def f1(): Unit = collected = collected ::: List("1")

    def f2(): Unit = collected = collected ::: List("2")

    def f3(): Unit = collected = collected ::: List("3")

  }

  def _100ms(f: () => Unit): Unit = {
    for (i <- 1 to 10) {
      f()
      pause(10)
    }
  }

  def tests = TestSuite {
    "Two states. Multiple actions" - {
      val fp: FunProvider = new FunProvider

      val s1 = List(
        TimelineEvent(10, Ext(fp.f3)),
        TimelineEvent(20, Ext(fp.f1)),
        TimelineEvent(31, Ext(fp.f3)),
        TimelineEvent(40, Transition(() => State_RUN))
      )

      val s2 = List(
        TimelineEvent(10, Ext(fp.f2)),
        TimelineEvent(3000, Transition(() => State_BEFORE))
      )

      val states: Map[State, List[TimelineEvent]] =
        Map(State_BEFORE -> s1, State_RUN -> s2)
      val ts = StatefulTimeline(State_BEFORE, states)

      _100ms(() => ts.exec())

      assert(fp.collected == List("3", "1", "3", "2"))

    }

    "Two states. One action at each with" - {
      val fp: FunProvider = new FunProvider

      val s1 = List(
        TimelineEvent(50, Ext(fp.f1)),
        TimelineEvent(60, Transition(() => State_RUN))
      )

      val s2 = List(
        TimelineEvent(10, Ext(fp.f2)),
        TimelineEvent(100, Transition(() => State_BEFORE))
      )

      val states: Map[State, List[TimelineEvent]] =
        Map(State_BEFORE -> s1, State_RUN -> s2)
      val ts = StatefulTimeline(State_BEFORE, states)

      _100ms(() => ts.exec())

      assert(fp.collected == List(State_BEFORE, State_RUN))

    }

    "Two states. One looping state" - {
      val fp: FunProvider = new FunProvider

      val s1 = List(
        TimelineEvent(30, Ext(fp.f1)),
        TimelineEvent(55, Transition(() => State_BEFORE))
      )

      val states: Map[State, List[TimelineEvent]] = Map(State_BEFORE -> s1)
      val ts = StatefulTimeline(State_BEFORE, states)

      _100ms(() => ts.exec())

      assert(fp.collected == List(State_BEFORE, State_BEFORE))

    }
  }
}
