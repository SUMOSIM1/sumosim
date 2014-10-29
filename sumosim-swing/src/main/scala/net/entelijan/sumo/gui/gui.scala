package net.entelijan.sumo.gui

import java.awt.Dimension
import javax.swing.{ JFrame }
import net.entelijan.sumo.gui.renderer.SumoGuiExample
import java.awt.Component
import java.awt.BorderLayout
import net.entelijan.sumo.gui.renderer.SumoViolet
import doctus.swing.DoctusImageSwing
import net.entelijan.sumo.gui.renderer.RoboBlack
import doctus.core.DoctusImage
import net.entelijan.sumo.gui.renderer.RoboRed
import net.entelijan.sumo.gui.renderer.SumoBlue

class SumoApp(example: SumoGuiExample, canvas: Component) {
  example.start()
  private val f = new JFrame()
  f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  f.setTitle(example.name)
  f.getContentPane.add(canvas, BorderLayout.CENTER)
  f.setSize(new Dimension(800, 600))
  f.setVisible(true)
}

object ImageProviderSwing {

  def sumoBlue = new SumoBlue {
    def doctusImage(index: Int): DoctusImage = DoctusImageSwing("robot-2d/sumo-blue/img%d.png" format index)
  }
  def sumoViolet = new SumoViolet {
    def doctusImage(index: Int): DoctusImage = DoctusImageSwing("robot-2d/sumo-violet/img%d.png" format index)
  }
  def roboRed = new RoboRed {
    def doctusImage(index: Int): DoctusImage = DoctusImageSwing("robot-2d/robo2/img%d.png" format index)
  }
  def roboBlack = new RoboBlack {
    def doctusImage(index: Int): DoctusImage = DoctusImageSwing("robot-2d/robo1/img%d.png" format index)
  }
  val background = new DoctusImageSwing("robot-2d/bg/pad.png")

}
