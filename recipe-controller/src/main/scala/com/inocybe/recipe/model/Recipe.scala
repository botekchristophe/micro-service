package com.inocybe.recipe.model

import com.inocybe.shared.model.ModelObject

case class Recipe(
                 id: String = java.util.UUID.randomUUID().toString,
                 name: String = "A name",
                 description: String = "A description"
                 ) extends ModelObject
