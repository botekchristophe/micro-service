package com.inocybe.protocol.neo4j

import com.inocybe.shared.model.ModelObject

object CypherConverter {

  implicit class CypherConverter(o: ModelObject) {

    /**
      * Convert a ModelObject to cypher props
      *
      * @return cypher props as a String.
      */
    def asCypherProps: String = {
      o.toMap.map {
        case (key, value) => s"$key: \'${value.toString}\'"
      }.toList.mkString("{", ", ", "}")
    }

    /**
      * Convert a ModelObject as cypher object.
      *
      * @param name define a specific name for this object inside the cypher request.
      * @return cypher object as a String.
      */
    def asCypherObject(name: String): String = {
      s"($name: ${o.getClass.toString.split(".").last} $asCypherProps)"
    }
  }
}
