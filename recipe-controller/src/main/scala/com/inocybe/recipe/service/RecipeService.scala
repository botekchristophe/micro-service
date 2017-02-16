package com.inocybe.recipe.service

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.inocybe.recipe.controller.RecipeController
import com.inocybe.recipe.model.JsonProtocol._
import com.inocybe.roles.Service
import com.inocybe.shared.model.ModelObject
import spray.json.JsonWriter


class RecipeService(controller: ActorRef)(implicit timeout: Timeout) extends Service {


  implicit val writer: JsonWriter[ModelObject] = ModelObjectJsonFormat

  val route: Route =
    get {
      pathPrefix("recipes") {
        onComplete(controller ? RecipeController.GetRecipes) {
          futureHandler
        }
      }
    } ~
      get {
      pathPrefix("newrecipe") {
        onComplete(controller ? RecipeController.NewEmptyRecipe) {
          futureHandler
        }
      }
    } ~
      get {
      pathPrefix("delete" / Segment) { recipeId =>
        onComplete(controller ? RecipeController.DeleteRecipe(recipeId)) {
          futureHandler
        }
      }
    } ~
      get {
        pathPrefix("recipe" / Segment) { recipeId =>
          onComplete(controller ? RecipeController.GetRecipe(recipeId)) {
            futureHandler
          }
        }
      }
}
