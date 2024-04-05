package net.entelijan.sumo.db

import net.entelijan.sumo.reinforcement.db.{
  DatabaseClient,
  SimulationDetail,
  SimulationOverview,
  SimulationState
}
import net.entelijan.sumo.robot.PosDir
import net.entelijan.sumo.util.Point2
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId}
import scala.jdk.CollectionConverters._

import org.bson._
import org.bson.codecs._
import org.bson.codecs.configuration._
import org.bson.types._
import com.mongodb.client._
import com.mongodb.client.model._

object MongoJvmUtil {

  lazy val localClient: MongoClient = MongoClients.create("mongodb://localhost")
  lazy val codecRegistry: CodecRegistry = CodecRegistries.fromCodecs(
    DocumentCodec(),
    IntegerCodec(),
    DoubleCodec(),
    StringCodec()
  )

}

case class MongoJvmDatabaseClient(mongoClient: MongoClient)
    extends DatabaseClient {

  override def overviews: Seq[SimulationOverview] = {
    val database: MongoDatabase = mongoClient.getDatabase("sumosim")
    val collection: MongoCollection[Document] =
      database.getCollection("simulations")
    collection
      .find(Filters.eq("status", "finished"))
      .projection(
        Projections
          .include("_id", "status", "started_at", "name", "robot1", "robot2")
      )
      .asScala
      .map { d => Converter.overview(toBsonDoc(d)) }
      .toSeq
  }

  override def detail(id: String): SimulationDetail = {
    val database: MongoDatabase = mongoClient.getDatabase("sumosim")
    val collection: MongoCollection[Document] =
      database.getCollection("simulations")
    val doc = collection
      .find(Filters.eq("_id", new ObjectId(id)))
      .projection(
        Projections
          .include("_id", "started_at", "name", "robot1", "robot2", "states")
      )
      .asScala
      .head
    Converter.detail(toBsonDoc(doc))
  }: SimulationDetail

  override def close(): Unit = {
    mongoClient.close()
  }

  private def toBsonDoc(doc: Document): BsonDocument = {
    doc.toBsonDocument(classOf[BsonDocument], MongoJvmUtil.codecRegistry)
  }

}

object Converter {

  private def localDateTime(timestampInMillis: Long): LocalDateTime = {
    val instant = Instant.ofEpochMilli(timestampInMillis)
    LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
  }

  private val dFormatter = DateTimeFormatter.ofPattern("YY-MM-dd HH:mm:ss")

  private def id(doc: BsonDocument): String = {
    val bson = doc.asScala.apply("_id")
    bson.asObjectId().getValue.toString
  }

  private def string(doc: BsonDocument, key: String): String = {
    val bson = doc.asScala.apply(key)
    bson.asString.getValue
  }

  private def dateTime(doc: BsonDocument, key: String): String = {
    val bson = doc.asScala.apply(key)
    val lts = bson.asDateTime().getValue
    val ldt = localDateTime(lts)
    dFormatter.format(ldt)
  }

  private def robotName(doc: BsonDocument, key: String): String = {
    val bson = doc.asScala.apply(key)
    bson.asDocument().get("name").asString().getValue
  }

  def overview(doc: BsonDocument): SimulationOverview = {
    SimulationOverview(
      id = id(doc),
      startedAt = dateTime(doc, "started_at"),
      simulationName = string(doc, "name"),
      robot1Name = robotName(doc, "robot1"),
      robot2Name = robotName(doc, "robot2")
    )
  }

  private def double(doc: BsonDocument, key: String): Double =
    doc.asScala
      .apply(key)
      .asDouble()
      .getValue

  def detail(doc: BsonDocument): SimulationDetail = {

    def posDir(doc1: BsonDocument, key: String): PosDir = {
      val doc = doc1.asScala.apply(key).asDocument()
      val pos = Point2(
        double(doc, "xpos"),
        double(doc, "ypos")
      )
      PosDir(pos, double(doc, "direction"))
    }

    def state(bson: BsonDocument): SimulationState = {
      SimulationState(
        robot1 = posDir(bson, "robot1"),
        robot2 = posDir(bson, "robot2")
      )
    }

    val states =
      doc.asScala
        .apply("states")
        .asArray()
        .getValues
        .asScala
        .toSeq
        .map(b => state(b.asDocument()))
    SimulationDetail(
      id = id(doc),
      startedAt = dateTime(doc, "started_at"),
      simulationName = string(doc, "name"),
      robot1Name = robotName(doc, "robot1"),
      robot2Name = robotName(doc, "robot2"),
      states = states
    )
  }

}
