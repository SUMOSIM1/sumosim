package net.entelijan.sumo.gui.controller

import doctus.core.DoctusCanvas
import doctus.core.util.DoctusUtil
import net.entelijan.sumo.gui.example.SumoLayout
import net.entelijan.sumo.gui.renderer.{
  R2DUniverse,
  RenderUniverse,
  SimpleUniverse
}

object Constants {

  val CARD_HOME = "home"
  val CARD_CODED = "coded"
  val CARD_RECORDED = "recorded"
  val CARD_RECORDED_CONTROL = "recordedControl"

}

object Util {
  def createUniverse(
      canvas: DoctusCanvas,
      layout: Option[SumoLayout],
      util: DoctusUtil
  ): RenderUniverse = {
    layout
      .map { l =>
        new R2DUniverse(
          canvas = canvas,
          fillFactor = 0.8,
          provider1 = l.robot1,
          provider2 = l.robot2,
          bgImg = l.background,
          util = l.util
        )
      }
      .getOrElse(SimpleUniverse.createDefaultUniverse(canvas, util))
  }
}
