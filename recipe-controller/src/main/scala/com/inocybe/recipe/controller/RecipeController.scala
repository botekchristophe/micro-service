package com.inocybe.recipe.controller

import akka.pattern.ask
import akka.util.Timeout
import com.inocybe.protocol.neo4j.{NeoRequests, Vertex, Vertices}
import com.inocybe.recipe.controller.RecipeController.{DeleteRecipe, GetRecipe, GetRecipes, NewEmptyRecipe}
import com.inocybe.recipe.model.Recipe
import com.inocybe.recipe.model.VertexConverter._
import com.inocybe.roles.Controller
import com.inocybe.roles.Service.NoContent
import com.inocybe.shared.model.{MicroServices, ServiceExceptions}

import scala.concurrent.Await
import scala.concurrent.duration._

object RecipeController {
  case object GetRecipes
  case class GetRecipe(recipeId: String)
  case class DeleteRecipe(recipeId: String)
  case object NewEmptyRecipe
}

class RecipeController extends Controller (Set(MicroServices.Neo4jConnector)) {

  implicit val timeout: Timeout = 5.seconds

  override def available: Receive = {
    case NewEmptyRecipe =>
      createRecipe(Recipe())
    case GetRecipes     =>
      getRecipes
    case GetRecipe(recipeId: String) =>
      getRecipe(recipeId)
    case DeleteRecipe(recipeId: String) =>
      deleteRecipe(recipeId)
    case _              => sender ! ServiceExceptions.NotImplementedException("Recipe controller received unknown message.")
  }

  def getRecipes: Unit = {
    val dbConnector = actors(MicroServices.Neo4jConnector)
    val recipeVs = Await.result(dbConnector ? NeoRequests.GetVertices(Vertices.Recipe), Duration.Inf).asInstanceOf[List[Vertex]]
    sender() ! recipeVs.map(_.toRecipe)
  }

  def createRecipe(r: Recipe): Unit = {
    val dbConnector = actors(MicroServices.Neo4jConnector)
    val recipeV = Vertex("Recipe", "recipeId", r.toMap.map { case (k, v) => (k, v.toString)})
    val newRecipeV = Await.result(dbConnector ? NeoRequests.CreateVertex(recipeV), Duration.Inf).asInstanceOf[Either[Throwable, Vertex]]
    if(newRecipeV.isLeft)
      sender() ! ServiceExceptions.Default(new Exception(newRecipeV.left.get))
    else
      sender() ! newRecipeV.right.get.toRecipe
  }

  def getRecipe(recipeId: String): Unit = {
    val dbConnector = actors(MicroServices.Neo4jConnector)
    val recipeV = Await.result(dbConnector ? NeoRequests.GetVertex(Vertices.Recipe, recipeId), Duration.Inf).asInstanceOf[Either[Throwable, Vertex]]
    if(recipeV.isLeft)
      sender() ! ServiceExceptions.Default(new Exception(recipeV.left.get))
    else
      sender() ! recipeV.right.get.toRecipe
  }

  def deleteRecipe(recipeId: String): Unit = {
    val dbConnector = actors(MicroServices.Neo4jConnector)
    val recipeV = Await.result(dbConnector ? NeoRequests.GetVertex(Vertices.Recipe, recipeId), Duration.Inf).asInstanceOf[Either[Throwable, Vertex]]
    if(recipeV.isRight)
      dbConnector ! NeoRequests.DeleteVertex(recipeV.right.get)
    sender() ! NoContent()
  }
}
