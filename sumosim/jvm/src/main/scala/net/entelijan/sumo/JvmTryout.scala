package net.entelijan.sumo

import net.entelijan.sumo.db._
import net.entelijan.sumo.reinforcement.db.DatabaseClient

object JvmTryout {

  def run(): Unit = {
    println("Running JVM Tryout")
    val c = MongoJvmUtil.localClient
    val dbc: DatabaseClient = MongoJvmDatabaseClient(c)
    try {
      val all = dbc.overviews
      for doc <- all.take(20) do {
        println("- - - - - - - - - - - -")
        pprint.pprintln(doc)
      }
      val detail = dbc.detail("667e52802a16532f0fd79967")
      pprint.pprintln(detail)
    } finally {
      dbc.close()
    }
  }

}
