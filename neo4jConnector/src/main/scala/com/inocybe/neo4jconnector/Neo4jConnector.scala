package com.inocybe.neo4jconnector

import com.inocybe.protocol.neo4j.{NeoRequests, Vertex}
import com.inocybe.protocol.neo4j.Vertices.V
import com.inocybe.roles.Connector
import org.neo4j.driver.v1.{AuthTokens, GraphDatabase, Record}
import com.inocybe.protocol.neo4j.CypherConverter._
import com.inocybe.shared.model.ModelObject

import scala.collection.JavaConverters._
import scala.util.Try

class Neo4jConnector extends Connector {

  val driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "botek"))
  val session = driver.session()

  def shutdown(): Unit = {
    session.close()
    driver.close()
  }

  override def receive: Receive = {
    case NeoRequests.CreateVertex(v: Vertex)       =>
      sender() ! createVertex(v)
    case NeoRequests.GetVertices(v: V)                  =>
      sender() ! getVertices(v)
    case NeoRequests.DeleteVertex(v: Vertex)            =>
      sender() ! deleteVertex(v)
    case NeoRequests.GetVertex(v: V, idxValue: String)  =>
      sender() ! getVertex(v, idxValue)
  }

  private def getVertices(v: V): List[Vertex] = {
    log.info("Received Get Vertices message")
    session.run(
      s"""
         |MATCH (v:${v.label})
         |RETURN labels(v) as label, id(v) as uuid, v
       """.stripMargin)
      .asScala
      .toList
      .map(toVertex)
  }

  private def createVertex(o: Vertex): Either[Throwable, Vertex] = {
    log.info("Creating vertex")
    Try(session.run(
      s"""
         |CREATE ${o.toCypher("v")}
         |RETURN labels(v) as label, id(v) as uuid, v
       """.stripMargin)
      .asScala
      .toList
      .map(toVertex)
      .head
    ).toEither
  }

  private def deleteVertex(v: Vertex): Unit = {
    log.info("Deleting vertex")
    session.run(
      s"""
         |MATCH (v)
         |WHERE id(v)=${v.uid.toInt}
         |DETACH DELETE v
       """.stripMargin)
      .asScala
      .toList
      .map(toVertex)
  }

  private def getVertex(v: V, idxValue: String): Either[Throwable, Vertex] = {
    log.info("Fetching one vertex")
    Try(session.run(
      s"""
         |MATCH (v: ${v.label})
         |WHERE v.${v.idx}='$idxValue'
         |RETURN labels(v) as label, id(v) as uuid, v
       """.stripMargin)
        .asScala
        .toList
        .map(toVertex)
        .head
    ).toEither
  }


  private def toVertex(record: Record): Vertex = {
    Vertex(
      label = record.get("label").asList().asScala.head.toString,
      uid = record.get("uuid").asInt().toString,
      props = record.get("v").asMap().asScala.toMap.map{ case (k,v) => (k, v.toString) })
  }
}
