package com.inocybe.protocol.neo4j

case class Vertex(label: String, uid: String, props: Map[String, String] = Map.empty[String, String]) extends Serializable