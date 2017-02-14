package com.inocybe.recipe.controller

import com.inocybe.recipe.controller.RecipeController.GetRecipes
import com.inocybe.recipe.model.Recipe
import com.inocybe.roles.Controller
import com.inocybe.roles.Service.ItemCreated
import com.inocybe.shared.model.{MicroServices, ServiceExceptions}

object RecipeController {
  case object GetRecipes
}

class RecipeController extends Controller (Set(MicroServices.ClusterListener)) {

  override def available: Receive = {
    case GetRecipes => sender ! getRecipes
    case _          => sender ! ServiceExceptions.NotImplementedException("Recipe controller received unknown message.")
  }

  def getRecipes = {
    val dbConnector = actors(MicroServices.ClusterListener)
    dbConnector ! ""
    ItemCreated(Recipe(name = "my recipe"))
  }
}
