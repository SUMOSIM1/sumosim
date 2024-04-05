package net.entelijan.sumo.robot

class ControllerWrapper(
    controller: DiffDriveController[CombiSensor],
    collector: WrapperCollector
) extends DiffDriveController[CombiSensor] {

  override def name: String = s"Wrapper of: ${controller.name}"
  override def describe: String = controller.describe

  private def createDiffDriveString(
      diffDriveValues: DiffDriveValues
  ): String = {
    s"${f(diffDriveValues.leftVelo)};${f(diffDriveValues.rightVelo)}"
  }

  private def createSensorString(sensor: CombiSensor): String = {
    val tee =
      s"${f(sensor.frontDistance)};${f(sensor.leftDistance)};${f(sensor.rightDistance)}"
    val opponent = s"${sensor.opponentInSector}"
    val self = s"${f(sensor.xpos)};${f(sensor.ypos)};${f(sensor.direction)}"
    s"$tee;$opponent;$self"
  }

  private def f(v: Double): String = {
    f"$v%.2f"
  }

  override def shortName: String = controller.shortName

  override def takeStep(sensor: CombiSensor): DiffDriveValues = {
    val sensorString = createSensorString(sensor)
    val dd: DiffDriveValues = controller.takeStep(sensor)
    val diffDriveString = createDiffDriveString(dd)
    val value = s"$sensorString;$diffDriveString"
    collector.addLine(controller.shortName, value)
    dd
  }
}

class WrapperCollector(val keys: List[String], val names: List[String]) {

  case class Line(key: String, line: String)

  private var lines = List.empty[Line]

  def addLine(key: String, line: String): Unit = {
    lines = lines ++ List(Line(key, line))
  }

  private def headers() = {
    keys.flatMap(k => names.map(n => s"${k}_$n")).mkString(";")
  }

  def transpose(): Seq[String] = {
    val x: Seq[List[Line]] = keys.map { k =>
      lines.filter(l => l.key == k)
    }.transpose
    val value = x.map(l => l.map(x => x.line)).map(x => x.mkString(";"))
    List(headers()) ++ value
  }
}
