package com.inocybe.recipe.model

case class Recipe(
                 id: String = java.util.UUID.randomUUID().toString,
                 name: String = "A name",
                 description: String = "A description"
                 ) extends ModelObject
