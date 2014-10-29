package net.entelijan.util

import java.io._

/**
 * Enables an object to pause for a certain time
 */
trait Pausable {

  protected def pause(pauseTimeMs: Int) {
    synchronized {
      try { wait(Math.max(pauseTimeMs, 1)) } catch { case e: InterruptedException =>}
    }
  }

}

trait InputProvider {

  def reader: Reader = new InputStreamReader(inputStream)

  def inputStream: InputStream
}

class ClasspathResourceInputProvider(resourceName: String) extends InputProvider {
  def inputStream = {
    val url = getClass.getClassLoader.getResource(resourceName)
    if (url == null) throw new IllegalStateException("Resource not found '%s'" format resourceName)
    new UrlInputProvider(url).inputStream
  }
}

trait BufferingInputProvider extends InputProvider {
  def using(is: InputStream)(f: InputStream => String): String = {
    try { f(is) } finally { is.close() }
  }
  val bs = using(super.inputStream) { is => new scala.io.BufferedSource(is).mkString }
  abstract override def inputStream = {
    new StringResourceInputProvider(bs).inputStream
  }
}

class StringResourceInputProvider(res: String) extends InputProvider {
  def inputStream = new ByteArrayInputStream(res.getBytes)
}

class UrlInputProvider(url: java.net.URL) extends InputProvider {
  def inputStream = url.openStream()
}

class FileInputProvider(file: File) extends InputProvider {
  def inputStream = new FileInputStream(file)
}

class FileNameInputProvider(fileName: String) extends InputProvider {
  def inputStream = new FileInputProvider(new File(fileName)).inputStream
}

class DirectoryFileNameInputProvider(directory: File, fileName: String) extends InputProvider {
  def inputStream = new FileInputProvider(new File(directory, fileName)).inputStream
}

trait Requester[T] {

  /**
   * Gets a Resource from the ResourcePool
   */
  final def receive(resource: T, pool: ResourcePool[T]) {
    try {
      useResource(resource)
    } finally {
      pool.receiveReturnedResource(resource)
    }
  }

  protected def useResource(resource: T)
}

trait ResourcePool[T] {

  /**
   * Gives the Requester a resource, calling 'receive' as soon as a Resource is available
   */
  def addRequester(requester: Requester[T])

  /**
   * A resource is returned if the requester does not need it any longer
   */
  def receiveReturnedResource(resource: T)

  def stop()
}

trait SynchronizedResourcePool[T] extends ResourcePool[T] {

  def resources: Iterable[T]

  var _resources: List[T] = resources.toList

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

  def close()

}
