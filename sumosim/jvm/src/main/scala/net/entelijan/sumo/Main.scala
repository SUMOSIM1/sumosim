package net.entelijan.sumo

import mainargs.{Flag, ParserForMethods, main}
import net.entelijan.sumo.reinforcement.Analyse

object Main {

  @main
  def gui(): Unit = {
    JvmGui.open()
  }

  @main
  def udp(port: Int): Unit = {
    UdpServer.start(port)
  }

  @main
  def collectValues(
      outputFile: Option[String],
      controllerIds: String = "rotating-clever",
      verbose: Flag
  ): Unit = {
    try {

      val path = outputFile.map { f =>
        os.Path(f, os.home / "work" / "sumosim")
      }
      val data = Analyse.runValueCollectingSimulation(controllerIds)
      path match {
        case Some(p) =>
          os.write(p, data, createFolders = true)
          println(s"Wrote to: $path")
        case None =>
          println(data)
      }
    } catch {
      case e: java.nio.file.FileAlreadyExistsException =>
        if (verbose.value) throw e
        else {
          println(s"ERROR: File already exists: ${e.getMessage}")
          System.exit(-1)
        }
      case e: Exception =>
        if (verbose.value) throw e
        else {
          println(s"ERROR: ${e.getMessage}")
          System.exit(-1)
        }
    }
  }

  @main
  def tryout(verbose: Flag): Unit = {
    try {
      JvmTryout.run()
    } catch {
      case e: Exception =>
        if (verbose.value) throw e
        else {
          println(s"ERROR: ${e.getMessage}")
          System.exit(-1)
        }
    }
  }

  def main(args: Array[String]): Unit =
    ParserForMethods(this).runOrExit(args.toIndexedSeq)
}
