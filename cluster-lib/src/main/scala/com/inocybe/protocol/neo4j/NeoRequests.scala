package com.inocybe.protocol.neo4j

import com.inocybe.protocol.neo4j.Edges.E
import com.inocybe.protocol.neo4j.Vertices.V

object NeoRequests {
  case class GetVertex(v: V, idxValue: String) extends Serializable
  case class GetVertices(v: V) extends Serializable
  case class CreateVertex(v: V, props: Map[String, String]) extends Serializable
  case class UpdateVertex(v: Vertex) extends Serializable
  case class DeleteVertex(v: Vertex) extends Serializable
  case class Attach(source: Vertex, edge: E, dest: Vertex) extends Serializable
}
