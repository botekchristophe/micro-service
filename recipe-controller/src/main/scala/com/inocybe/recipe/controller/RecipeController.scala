package com.inocybe.recipe.controller

import akka.pattern.ask
import akka.util.Timeout
import com.inocybe.protocol.neo4j.{NeoRequests, Vertex, Vertices}
import com.inocybe.recipe.controller.RecipeController.GetRecipes
import com.inocybe.recipe.model.VertexConverter._
import com.inocybe.roles.Controller
import com.inocybe.shared.model.{MicroServices, ServiceExceptions}

import scala.concurrent.Await
import scala.concurrent.duration._

object RecipeController {
  case object GetRecipes
}

class RecipeController extends Controller (Set(MicroServices.Neo4jConnector)) {

  implicit val timeout: Timeout = 5.seconds

  override def available: Receive = {
    case GetRecipes => getRecipes
    case _          => sender ! ServiceExceptions.NotImplementedException("Recipe controller received unknown message.")
  }

  def getRecipes: Unit = {
    val dbConnector = actors(MicroServices.Neo4jConnector)
    val recipeVs = Await.result(dbConnector ? NeoRequests.GetVertices(Vertices.Recipe), Duration.Inf).asInstanceOf[List[Vertex]]
    sender() ! recipeVs.map(_.toRecipe)
  }
}
