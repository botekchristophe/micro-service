package com.inocybe.shared.model

object MicroServices {
  sealed abstract class MicroService(name: String = "Unknown") {
    val roleName: String = name
  }

  case object ClusterListener extends MicroService("ClusterListener")
  case object RecipeController extends MicroService("RecipeController")
  case object Neo4jConnector extends MicroService("Neo4jConnector")
  case object SonarConnector extends MicroService("SonarConnector")

  def fromName(roleName: String): MicroService = {
    roleName match {
      case ClusterListener.roleName  => ClusterListener
      case RecipeController.roleName => RecipeController
      case Neo4jConnector.roleName   => Neo4jConnector
      case SonarConnector.roleName   => SonarConnector
    }
  }
}
