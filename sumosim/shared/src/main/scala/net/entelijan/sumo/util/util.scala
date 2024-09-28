package net.entelijan.sumo.util

import doctus.core.DoctusSchedulerStopper
import net.entelijan.sumo.commons.UpdatableMsg
import net.entelijan.sumo.core.{Duel, Duels, RobotSimulation}
import net.entelijan.sumo.robot.*

import java.io.*

/** Enables an object to pause for a certain time
  */
trait Pausable {

  protected def pause(pauseTimeMs: Int): Unit = {
    synchronized {
      try { wait(Math.max(pauseTimeMs, 1)) }
      catch { case _: InterruptedException => }
    }
  }

}

trait InputProvider {

  def reader: Reader = new InputStreamReader(inputStream)

  def inputStream: InputStream
}

class ClasspathResourceInputProvider(resourceName: String)
    extends InputProvider {
  def inputStream: InputStream = {
    val url = getClass.getClassLoader.getResource(resourceName)
    if (url == null)
      throw new IllegalStateException(
        "Resource not found '%s'" format resourceName
      )
    new UrlInputProvider(url).inputStream
  }
}

trait BufferingInputProvider extends InputProvider {
  private def using(is: InputStream)(f: InputStream => String): String = {
    try { f(is) }
    finally { is.close() }
  }
  private val bs = using(super.inputStream) { is =>
    new scala.io.BufferedSource(is).mkString
  }
  abstract override def inputStream: InputStream = {
    new StringResourceInputProvider(bs).inputStream
  }
}

class StringResourceInputProvider(res: String) extends InputProvider {
  def inputStream = new ByteArrayInputStream(res.getBytes)
}

class UrlInputProvider(url: java.net.URL) extends InputProvider {
  def inputStream: InputStream = url.openStream()
}

class FileInputProvider(file: File) extends InputProvider {
  def inputStream = new FileInputStream(file)
}

class FileNameInputProvider(fileName: String) extends InputProvider {
  def inputStream: InputStream = new FileInputProvider(
    new File(fileName)
  ).inputStream
}

class DirectoryFileNameInputProvider(directory: File, fileName: String)
    extends InputProvider {
  def inputStream: InputStream = new FileInputProvider(
    new File(directory, fileName)
  ).inputStream
}

trait Requester[T] {

  /** Gets a Resource from the ResourcePool
    */
  final def receive(resource: T, pool: ResourcePool[T]): Unit = {
    try {
      useResource(resource)
    } finally {
      pool.receiveReturnedResource(resource)
    }
  }

  protected def useResource(resource: T): Unit
}

trait ResourcePool[T] {

  /** Gives the Requester a resource, calling 'receive' as soon as a Resource is
    * available
    */
  def addRequester(requester: Requester[T]): Unit

  /** A resource is returned if the requester does not need it any longer
    */
  def receiveReturnedResource(resource: T): Unit

  def stop(): Unit
}

trait SynchronizedResourcePool[T] extends ResourcePool[T] {

  def resources: Iterable[T]

  private var _resources: List[T] = resources.toList

  def addRequester(requester: Requester[T]): Unit = {
    val res: T = extractResource
    requester.receive(res, this)
  }
  def receiveReturnedResource(resource: T): Unit = {
    synchronized {
      _resources = resource :: _resources
    }
  }

  private def extractResource: T = {
    synchronized {
      _resources match {
        case Nil => throw new IllegalStateException("No resource left")
        case re :: rest =>
          _resources = rest
          re
      }
    }
  }

  def stop(): Unit

}

trait Closeable {

  def close(): Unit

}

object Helper extends TrigUtil {

  def measureTimeMillis(block: => Unit): Double = {
    val before = System.nanoTime
    block
    val after = System.nanoTime
    (after - before) / 1000000.0
  }

  def createCombiSensor(
      dir_deg: Double,
      randomizer: Randomizer
  ): CombiSensor = {

    var mySensor = Option.empty[CombiSensor]

    val c1 = new DiffDriveController[CombiSensor]() {

      override def name: String = "A"

      override def shortName: String = "A"

      override def takeStep(sensor: CombiSensor): DiffDriveValues = {
        mySensor = Some(sensor)
        DiffDriveValues(0.0, 0.0)
      }

    }

    val c2 = new DiffDriveController[CombiSensor]() {

      override def name: String = "B"

      override def shortName: String = "B"

      override def takeStep(sensor: CombiSensor): DiffDriveValues =
        DiffDriveValues(0.0, 0.0)
    }

    val r1 = new CombiSensorDiffDriveRobot(randomizer) {
      override def name: String = c1.name
    }

    val r2 = new CombiSensorDiffDriveRobot(randomizer) {
      override def name: String = c2.name
    }

    val sim = new RobotSimulation[
      CombiSensor,
      DiffDriveValues,
      CombiSensor,
      DiffDriveValues
    ] {
      override def duel
          : Duel[CombiSensor, DiffDriveValues, CombiSensor, DiffDriveValues] =
        Duels.create(c1, r1, c2, r2)

      override def startRunning(): DoctusSchedulerStopper = {
        positionRobotsToStart()
        duel.robot1.robot.adjust(
          duel.robot1.robot.xpos,
          duel.robot1.robot.ypos,
          toRad(dir_deg)
        )
        runOneCompetitionStep()
        () => ()
      }

      override def sendUpdatableMessage(msg: UpdatableMsg): Unit = {
        // Nothing to do
      }

    }

    sim.startRunning()
    mySensor.getOrElse(throw new RuntimeException("sensor was not filled"))
  }

}
