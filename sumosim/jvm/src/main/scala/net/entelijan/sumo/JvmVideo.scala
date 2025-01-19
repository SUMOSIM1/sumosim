package net.entelijan.sumo

import doctus.awt.DoctusGraphicsAwt
import doctus.core.{DoctusColor, DoctusGraphics}
import doctus.core.color.{DoctusColorOrange, DoctusColorYellow}
import doctus.core.util.DoctusUtil

import java.awt.image.BufferedImage
import doctus.swing.DoctusBufferedImage
import db.{MongoJvmDatabaseClient, MongoJvmUtil}
import gui.renderer.{Paint, SimpleUniverseRobot}
import robot.PosDir
import reinforcement.db.{DatabaseClient, SimulationDetail, SimulationState}
import os.*

import javax.imageio.ImageIO

object JvmVideo {
  def run(namePrefix: String, outDir: os.Path): Unit = {

    val width = 900
    val height = 640
    val color1 = DoctusColorYellow
    val color2 = DoctusColorOrange
    val screenResol = 150
    val docInterval = 50


    val databaseClient = MongoJvmUtil.localClient
    val dbClient: DatabaseClient = MongoJvmDatabaseClient(databaseClient)

    def drawImage(
        graphics: DoctusGraphics,
        simulation: SimulationDetail,
        state: SimulationState,
        stateNr: Int,
        maxState: Int
    ): Unit = {
      val util = new DoctusUtil {
        override def screenResolution: Int = screenResol
      }
      val desc1 = simulation.robot1Name
      val desc2 = simulation.robot2Name

      def createRobot(posDir: PosDir, color: DoctusColor) = {
        SimpleUniverseRobot(
          posDir.pos.xpos,
          posDir.pos.ypos,
          posDir.dir,
          color,
          util
        )
      }
      val robot1 = createRobot(state.robot1, color1)
      val robot2 = createRobot(state.robot2, color2)
      val info = s"${simulation.simulationName} $stateNr / $maxState"
      Paint.paintSimple(
        graphics,
        width,
        height,
        util,
        List(robot1, robot2),
        info,
        color1,
        color2,
        desc1,
        desc2
      )
    }

    def readSimulations(): Iterable[SimulationDetail] = {
      val filtered = dbClient.overviews.filter { o =>
        o.simulationName.startsWith(namePrefix)
      }
      for overview <- filtered yield {
        dbClient.detail(overview.id)
      }
    }

    val workDir = os.Path("/tmp") / "sumosim" / "video"
    if !os.exists(workDir) then os.makeDir.all(workDir)
    val sims = readSimulations().toSeq

    val maxSim = sims.size
    println(s"Creating $maxSim video(s) for prefix: $namePrefix in $outDir")
    for (s, k) <- sims.zipWithIndex do {
      val imageDir = workDir / s.simulationName
      if !os.exists(imageDir) then os.makeDir.all(imageDir)
      val states = s.states
      val maxState = states.size
      for (state, i) <- states.zipWithIndex do {
        val bufferedImage =
          BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        DoctusBufferedImage(bufferedImage)
        val graphics = DoctusGraphicsAwt(bufferedImage.createGraphics())
        drawImage(graphics, s, state, i, maxState)
        val outfile = imageDir / f"${s.simulationName}-sumosim-video-$i%08d.png"
        ImageIO.write(bufferedImage, "PNG", outfile.toIO)
        if i % docInterval == 0 && i > 0 then {
          println(s"Created $i / $maxState images in $imageDir")
        }
      }
      val inFiles = s"$imageDir/*sumosim-video*.png"
      val videoOutFile = outDir / f"${s.simulationName}-sumosim-video.mp4"
      val cmd = Seq(
        "ffmpeg",
        "-y",
        "-framerate",
        "50",
        "-pattern_type",
        "glob",
        "-i",
        inFiles,
        "-c:v",
        "libx264",
        "-pix_fmt",
        "yuv420p",
        videoOutFile.toString
      )

      val cmdStr = cmd.mkString(" ")
      println(s"calling : '$cmdStr'")
      val result = os.call(cmd, stderr = Pipe)
      println(s"result : '${result.exitCode}'")
      if (result.exitCode != 0)
        throw RuntimeException(
          s"Error calling '$cmdStr' \n${result.exitCode} \n${result.err.toString}"
        )
      println(s"Created video ${k + 1} / $maxSim in $videoOutFile")
    }
  }
}
