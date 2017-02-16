package com.inocybe.protocol.neo4j

case class Vertex(label: String, uid: String, props: Map[String, String] = Map.empty[String, String]) extends Serializable {

  def toCypher: String = {
    toCypher("v")
  }

  def toCypher(name: String): String = {
    s"($name: $label $asCypherProps)"
  }

  def asCypherProps: String = {
    props.map {
      case (key, value) => s"$key: \'$value\'"
    }.toList.mkString("{", ", ", "}")
  }
}