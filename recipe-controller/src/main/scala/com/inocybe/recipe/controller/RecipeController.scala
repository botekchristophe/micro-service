package com.inocybe.recipe.controller

import com.inocybe.protocol.ClusterListerner.SayHi
import com.inocybe.recipe.controller.RecipeController.GetRecipes
import com.inocybe.recipe.model.Recipe
import com.inocybe.recipe.service.Service.ItemInfo
import com.inocybe.roles.Controller
import com.inocybe.shared.model.MicroServices

object RecipeController {
  case object GetRecipes
}

class RecipeController extends Controller(List(MicroServices.DbConnector)) {

  override def available: Receive = {
    case SayHi      => log.info("Hi ! I'm available")
    case GetRecipes => sender ! ItemInfo(Recipe(name = "my recipe"))
    case _          => sender() ! "Available but unknown message"
  }
}
