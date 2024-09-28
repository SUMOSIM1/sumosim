package net.entelijan.sumo

import net.entelijan.sumo.db.*
import net.entelijan.sumo.reinforcement.Tryout
import net.entelijan.sumo.reinforcement.db.DatabaseClient

object JvmTryout {

  def run(): Unit = {
    println("Running JVM Tryout")
    Tryout.run()
  }

  
}
