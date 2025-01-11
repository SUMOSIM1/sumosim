package net.entelijan.sumo

import mainargs.{Flag, ParserForMethods, main, arg}
import os.*

object Main {

  @main
  def gui(): Unit = {
    JvmGui.open()
  }

  @main(doc = "Create a video from the robot positions in the database")
  def video(
      @arg(short = 'p', doc = "Pefix of the simulation to be visualized")
      namePrefix: String,
      @arg(short = 'o', doc = "Output directory. Must be absolute")
      outDir: String
  ): Unit = {
    val outPath = os.Path(outDir)
    JvmVideo.run(namePrefix, outPath)
  }

  @main
  def udp(port: Int): Unit = {
    UdpServer.start(port)
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
    if (args.isEmpty) then
      println("At least one argument is needed. For details type --help")
    else ParserForMethods(this).runOrExit(args.toIndexedSeq)
}
