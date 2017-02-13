package com.inocybe.recipe.service

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.inocybe.recipe.controller.RecipeController

class RecipeService(controller: ActorRef)(implicit timeout: Timeout) extends Service {

  val route: Route =
    get {
      pathPrefix("recipes") {
        onComplete(controller ? RecipeController.GetRecipes) {
          futureHandler
        }
      }
    }
}
