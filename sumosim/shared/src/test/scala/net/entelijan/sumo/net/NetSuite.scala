package net.entelijan.sumo.net

import net.entelijan.sumo.reinforcement._
import net.entelijan.sumo.robot.{DiffDriveValues, RandomizerImpl}
import net.entelijan.sumo.util.Helper
import utest._

object EqUtil {

  def eq(v1: Double, v2: Double, maxDiff: Double): Boolean = {
    math.abs(v1 - v2) < maxDiff
  }

}
object NetSuite extends TestSuite {

  def tests: Tests = Tests {

    test("parse start command A|") {
      val cmd = CommandParser.parse(data = "A|")
      assert(cmd == StartCommand)
    }

    test("parse start command A|hallo") {
      val cmd = CommandParser.parse(data = "A|")
      assert(cmd == StartCommand)
    }

    test("parse start command A") {
      val cmd = CommandParser.parse(data = "A")
      assert(cmd == StartCommand)
    }

    test("parse diff drive command 1") {
      val cmd = CommandParser.parse(data = "C|4.3000;1.5000#44.0000;-1.2000#0#0")
      assert(
        cmd == DiffDriveCommand(
          DiffDriveValues(4.3, 1.5),
          DiffDriveValues(44.0, -1.2),
          0,
          false
        )
      )
    }

    test("parse diff drive command 2") {
      val cmd = CommandParser.parse(data = "C|4.3000;1.5000#44.0000;-1.2000#110#1")
      assert(
        cmd == DiffDriveCommand(
          DiffDriveValues(4.3, 1.5),
          DiffDriveValues(44.0, -1.2),
          110,
          true
        )
      )
    }

    test("parse no command 'X'") {
      intercept[IllegalArgumentException] {
        CommandParser.parse(data = "X")
      }
    }

    test("parse no command 'X|'") {
      intercept[IllegalArgumentException] {
        CommandParser.parse(data = "X")
      }
    }

    test("parse no command ''") {
      intercept[IllegalArgumentException] {
        CommandParser.parse(data = "")
      }
    }

    test("parse no command '|'") {
      intercept[IllegalArgumentException] {
        CommandParser.parse(data = "|")
      }
    }

    test("format finished ERROR hallo") {
      val data = CommandFormatter.format(FinishedErrorCommand("hallo"))
      assert(data == "E|hallo")
    }

    test("format sensor 8 RIGHT") {

      val ran1 = RandomizerImpl(randomVariance = 1.0)
      val ran2 = RandomizerImpl(randomVariance = 0.0)

      val s1 = Helper.createCombiSensor(8, ran1)
      val s2 = Helper.createCombiSensor(0, ran2)
      val cmd = SensorCommand(s1, s2)

      val str = CommandFormatter.format(cmd)
      val prefix = str.split("\\|")(0)
      val rest = str.split("\\|")(1)
      val r1 = rest.split("#")(0)
      val r1v = r1.split(";")
      val x = r1v(0).toDouble
      val y = r1v(1).toDouble
      val dir = r1v(2).toDouble
      val left = r1v(3).toDouble
      val front = r1v(4).toDouble
      val right = r1v(5).toDouble
      val opponent = r1v(6)
      assert(
        prefix == "B",
        x == -80,
        y == 0,
        EqUtil.eq(dir, 0.1396, 0.1),
        EqUtil.eq(front, 479.0, 1.1),
        EqUtil.eq(left, 381.0, 1.1),
        EqUtil.eq(right, 403.2, 1.1),
        opponent == "RIGHT"
      )

    }

    test("eq OK") {
      assert(
        EqUtil.eq(1.0, 1.0, 1.0),
        EqUtil.eq(1.0, 1.1, 0.1001),
        EqUtil.eq(1.0, 0.9, 0.1001),
        EqUtil.eq(1.0, -1.0, 2.00001),
        EqUtil.eq(-1.0, -0.9, 0.1001)
      )

      test("eq not OK") {
        assert(
          !EqUtil.eq(1.0, 1.1, 0.09999),
          !EqUtil.eq(1.0, 0.9, 0.09999)
        )

      }

    }
  }
}
