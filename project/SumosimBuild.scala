import sbt._
import Keys._
import sbt.Package.ManifestAttributes
import com.typesafe.sbteclipse.plugin.EclipsePlugin._

import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

object SumosimBuild extends Build {

  // Constant values
  object D {

    val version = "1.0-SNAPSHOT"

    val scalaMainVersion = "2.11"
    val scalaVersion = s"$scalaMainVersion.5"
    val doctusVersion = "1.0.5-SNAPSHOT"

  }

  // Settings
  object S {

    lazy val commonSettings =
        Seq(
          version := D.version,
          scalaVersion := D.scalaVersion,
          organization := "net.entelijan.sumosim2",
          publishMavenStyle := true,
          publishTo := Some("entelijan-repo" at "http://entelijan.net/artifactory/libs-release-local/"),
          credentials += Credentials("Artifactory Realm", "entelijan.net", "deploy", "deploy"),
          resolvers += "entelijan" at "http://entelijan.net/artifactory/repo/",
          EclipseKeys.withSource := true)

    lazy val sumosimSettings =
      commonSettings ++
        Seq(
          libraryDependencies += "net.entelijan" %%% "doctus-core" % D.doctusVersion,
		  libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test")

    lazy val sumosimSwingSettings =
      commonSettings ++
        Seq(
          fork := true,
          libraryDependencies ++= Seq(
            "net.entelijan" %% "doctus-swing" % D.doctusVersion))

    lazy val sumosimScalajsSettings =
      sumosimSettings ++
        Seq(
          jsDependencies += RuntimeDOM,
          libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.8.0",
          libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "0.8.0",
          libraryDependencies += "net.entelijan" %%% "doctus-scalajs" % D.doctusVersion)

  }

  // Project definitions
  lazy val root = Project(
    id = "sumosim-pom",
    base = file("."), 
    settings = S.commonSettings)
	.aggregate(sumosim, sumosimSwing, sumosimScalajs)

  lazy val sumosim = Project(
    id = "sumosim",
    base = file("sumosim"),  
    settings = S.sumosimSettings)
    .enablePlugins(ScalaJSPlugin)

  lazy val sumosimSwing = Project(
    id = "sumosim-swing",
    base = file("sumosim-swing"),
    settings = S.sumosimSwingSettings)
	.dependsOn(sumosim)

  lazy val sumosimScalajs = Project(
    id = "sumosim-scalajs",
    base = file("sumosim-scalajs"),
    settings = S.sumosimScalajsSettings)
	.dependsOn(sumosim)
    .enablePlugins(ScalaJSPlugin)

}

