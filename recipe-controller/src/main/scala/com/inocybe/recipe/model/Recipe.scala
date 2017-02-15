package com.inocybe.recipe.model

import com.inocybe.shared.model.ModelObject

case class Recipe(
                 recipeId: String = java.util.UUID.randomUUID().toString,
                 display_name: String = "display_name",
                 category: String = "category",
                 logo_file: Option[String] = None,
                 excerpt: String = "excerpt",
                 description: String = "description",
                 video_url: Option[String] = None,
                 tutorial_url: Option[String] = None,
                 supported_Streams: Map[String, String] = Map.empty[String, String],
                 projects: List[String] = List.empty[String],
                 features: List[String] = List.empty[String],
                 tag: String = "tag",
                 add_ons: List[String] = List.empty[String],
                 applications: List[String] = List.empty[String]
                 ) extends ModelObject

case class IncomingRecipe(
                           display_name: String,
                           category: String,
                           logo_file: Option[String] = None,
                           excerpt: String,
                           description: String,
                           video_url: Option[String] = None,
                           tutorial_url: Option[String] = None,
                           supported_Streams: Map[String, String],
                           projects: List[String],
                           features: List[String],
                           add_ons: List[String],
                           applications: List[String]
                         ) extends ModelObject
