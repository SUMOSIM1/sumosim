import sbt.Keys.libraryDependencies

import scala.collection.Seq

ThisBuild / scalaVersion := "3.4.2"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val sumosimVersion = "1.0.1-SNAPSHOT"
lazy val doctusVersion = "2.0.0-SNAPSHOT"
lazy val javaFxVersion = "21.0.2" // LTS
lazy val javaVersion = "17" // Info for scalac in order to optimise
lazy val utestVersion = "0.8.2"
lazy val scalaJsJqueryVersion = "1.0.0"
lazy val organisation = "net.entelijan"

lazy val sumosimRoot = project
  .in(file("."))
  .aggregate(
    sumosim.js,
    sumosim.jvm
  )

lazy val sumosim = crossProject(JSPlatform, JVMPlatform)
  .in(file("sumosim"))
  .enablePlugins(PackPlugin)
  .settings(
    name := "sumosim",
    organization := organisation,
    version := sumosimVersion,
    organizationHomepage := Some(url("http://entelijan.net/")),
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    testFrameworks += new TestFramework("utest.runner.Framework"),
    scalacOptions ++= Seq(
      "-deprecation",
      "-Wunused:all"
    )
  )
  .jvmSettings(
    packMain := Map("sumo" -> "net.entelijan.sumo.Main"),
    libraryDependencies += "com.lihaoyi" %% "utest" % utestVersion % "test",
    libraryDependencies += "com.lihaoyi" %% "upickle" % "3.2.0",
    libraryDependencies += organisation %% "doctus-core" % doctusVersion,
    libraryDependencies += "com.lihaoyi" %% "mainargs" % "0.6.3",
    libraryDependencies += "com.lihaoyi" %% "os-lib" % "0.11.3",
    libraryDependencies += "org.mongodb" % "mongo-java-driver" % "3.12.14",
    libraryDependencies += "org.apache.logging.log4j" % "log4j-slf4j2-impl" % "2.23.1",
    libraryDependencies += "com.lihaoyi" %% "pprint" % "0.9.0",
    javacOptions ++= Seq("-source", javaVersion, "-target", javaVersion),
    fork := true
  )
  .jsSettings(
    libraryDependencies += "com.lihaoyi" %%% "utest" % utestVersion % "test",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "3.2.0",
    libraryDependencies += organisation %%% "doctus-core" % doctusVersion,
    libraryDependencies += "io.github.cquiroz" %%% "scala-java-locales" % "1.5.1",
    libraryDependencies += "com.lihaoyi" %%% "pprint" % "0.9.0"
  )
