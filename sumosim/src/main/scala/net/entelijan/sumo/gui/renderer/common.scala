package net.entelijan.sumo.gui.renderer

import net.entelijan.sumo.commons.Updatable
import doctus.core.DoctusCanvas

// TODO Make it somehow scalable. Eventually not here but in implementations
abstract class RenderUniverse(canvas: DoctusCanvas) extends Updatable 

trait SumoGuiExample {
  def name: String
  def start(): Unit
}

