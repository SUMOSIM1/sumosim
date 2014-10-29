package net.entelijan.util

trait Timeline {

  def exec(): Unit

}

sealed trait Action 
case class Ext(f: () => Unit) extends Action
case class Transition(f: () => String) extends Action

case class TimelineEvent(time: Long, action: Action)

case class StatefulTimeline(startStateName: String, states: Map[String, List[TimelineEvent]]) extends Timeline {
  
  var _startTime = System.currentTimeMillis()
  var _events = states(startStateName)

  def exec(): Unit = {
    val at = System.currentTimeMillis()
    case class A(delay: Long, action: Action)
    
    val t = at - _startTime
    val (a, b) = _events.partition(e => e.time <= t)
    _events = b
    val actions = a.sortWith((a, b) => a.time < b.time).map(x => A(at - x.time, x.action))
    actions.foreach(a => a.action match {
      case Ext(f) => f()
      case Transition(f) =>
        _events = states(f())
        _startTime = System.currentTimeMillis()
    })
  }
  
}

