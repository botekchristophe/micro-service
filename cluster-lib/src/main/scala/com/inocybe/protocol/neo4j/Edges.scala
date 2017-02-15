package com.inocybe.protocol.neo4j

object Edges {
  sealed abstract class E(_label: String) extends Serializable {
    def label: String = _label
  }

  case object OwnedBy extends E("ownedBy")
  case object BuildWithRecipe extends E("buildWithRecipe")
  case object BuildWithStream extends E("buildWithStream")
}
