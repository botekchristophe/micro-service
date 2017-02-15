package com.inocybe.protocol.neo4j

object Vertices{
  sealed abstract class V(_label: String, _idx: String) extends Serializable {
    def label: String = _label
    def idx: String = _idx
  }

  case object Recipe        extends V("Recipe", "recipeId")
  case object Distribution  extends V("Distribution", "distributionId")
  case object Stream        extends V("Stream", "streamId")
}