package com.inocybe.recipe.model

import com.inocybe.protocol.neo4j.Vertex

object VertexConverter {

  implicit class VertexConvertion(v: Vertex) {

    def toRecipe: Recipe = { Recipe().copy(recipeId = v.props("recipeId"))
      //Recipe(
      // id = v.props.getOrElse("id", "recipeId"),
      // name = v.props.getOrElse("name", "recipeName"),
      // description = v.props.getOrElse("description", "recipe description"))
    }
  }
}
