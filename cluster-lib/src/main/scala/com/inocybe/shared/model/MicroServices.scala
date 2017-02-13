package com.inocybe.shared.model

object MicroServices {
  sealed abstract class MicroService(name: String) {
    def roleName: String = this.name
  }

  case object ClusterListener extends MicroService("ClusterListener")
  case object RecipeController extends MicroService("RecipeController")
  case object Neo4jConnector extends MicroService("Neo4jConnector")
  case object SonnarConnector extends MicroService("SonnarConnector")

  def fromName(roleName: String): MicroService = {
    roleName match {
      case "ClusterListener"  => ClusterListener
      case "RecipeController" => RecipeController
      case "Neo4jConnector"   => Neo4jConnector
      case "SonnarConnector"  => SonnarConnector
    }
  }
}
