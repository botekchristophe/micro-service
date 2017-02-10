package com.inocybe.shared.model

object MicroServices {
  sealed trait MicroService
  case object ClusterListener extends MicroService
  case object RecipeController extends MicroService
  case object DbConnector extends MicroService
  case object SonnarConnector extends MicroService

  /**
    * Match an actor role as a String to its object.
    *
    * @param role actor role as [[String]].
    * @return actor role as [[MicroService]]
    */
  def fromString(role: String): MicroService = {
    role match {
      case "ClusterListener"  => ClusterListener
      case "RecipeController" => RecipeController
      case "DbConnector"      => DbConnector
      case "SonnarConnector"  => SonnarConnector
    }
  }
}
