package com.inocybe.recipe.controller

import com.inocybe.protocol.ClusterListerner.SayHi
import com.inocybe.roles.Controller
import com.inocybe.shared.model.MicroServices

class RecipeController extends Controller(List(MicroServices.ClusterListener)) {

  override def available: Receive = {
    case SayHi  => log.info("Hi ! I'm available")
    case _      => sender() ! "Available but unknown message"
  }
}
