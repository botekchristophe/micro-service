package com.inocybe.neo4jconnector

import akka.actor.{Actor, ActorLogging}
import com.inocybe.protocol.neo4j.{NeoRequests, Vertex}
import com.inocybe.protocol.neo4j.Vertices.V
import org.neo4j.driver.v1.{AuthTokens, GraphDatabase, Record}

import scala.collection.JavaConverters._

class Neo4jConnector extends Actor with ActorLogging {

  val driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "botek"))
  val session = driver.session()

  def shutdown(): Unit = {
    session.close()
    driver.close()
  }

  override def receive: Receive = {
    case NeoRequests.GetVertices(v: V) => sender() ! getVertices(v)
  }

  def getVertices(v: V): List[Vertex] = {
    log.info("Received Get Vertices message")
    session.run(s"MATCH (v:${v.label}) RETURN labels(v) as label, id(v) as uuid, v")
      .asScala
      .toList
      .map(toVertex)
  }

  private def toVertex(record: Record): Vertex = {
    Vertex(
      label = record.get("label").asList().asScala.head.toString,
      uid = record.get("uuid").asInt().toString,
      props = record.get("v").asMap().asScala.toMap.map{ case (k,v) => (k, v.toString)})
  }
}
