package net.entelijan.sumo.scalajs

import java.io.File
import java.io.StringWriter
import java.io.PrintWriter

object CreatePreloadStatements extends App {

  case class Img(nr: Int, relPath: String)

  val baseDir = new File("src/main/resources")

  val sw = new StringWriter

  val pw = new PrintWriter(sw)

  val paths = createRelPathes(baseDir, List.empty[String])

  val imgs = paths.zipWithIndex.map { case (img, idx) => Img(idx, img) }
  imgs.foreach { i =>
    val nrStr = "%04d" format i.nr
    pw.println(s"""#preload-$nrStr { background: url('${i.relPath}') no-repeat -9999px -9999px; }""")
  }

  pw.flush
  println(sw.getBuffer.toString())

  private def createRelPathes(baseDir: File, paths: List[String]): List[String] = {
    require(baseDir.isDirectory())
    createRelPathes(baseDir.listFiles().toList, paths)
  }
  private def createRelPathes(files: List[File], paths: List[String]): List[String] = {
    def createRelPath(file: File): String = {
      val path = file.getCanonicalPath.replace(File.separatorChar, '/')
      "src/main/resources/" + path.substring(path.indexOf("/resources") + 11)
    }
    def isImageFile(f: File): Boolean = {
      f.getName.toLowerCase().endsWith("jpg") ||
        f.getName.toLowerCase().endsWith("jpeg") ||
        f.getName.toLowerCase().endsWith("png")
    }
    files match {
      case Nil => paths
      case file :: rest =>
        if (file.isDirectory()) {
          val sub = createRelPathes(file, paths)
          sub ::: createRelPathes(rest, paths)
        } else if (isImageFile(file)) {
          val relPath = createRelPath(file)
          createRelPathes(rest, relPath :: paths)
        } else createRelPathes(rest, paths)
    }

  }

}