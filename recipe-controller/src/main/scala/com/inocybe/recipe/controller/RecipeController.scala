package com.inocybe.recipe.controller

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection}
import com.inocybe.protocol.ClusterListerner.{Resolve, SayHi}
import com.inocybe.shared.model.MicroServices
import com.inocybe.shared.model.MicroServices.MicroService

class RecipeController extends Actor with ActorLogging {

  val system = context.system
  var clusterListener: ActorRef = null
  val connectors = List(MicroServices.ClusterListener)
  var resolvedConnectors: Map[MicroService, ActorSelection] = Map.empty[MicroService, ActorSelection]

  def receive = unavailable

  def unavailable: Receive = {
    case SayHi =>
      log.info("Hi ! I'm not available")
      clusterListener = sender()
      sender() ! Resolve(connectors)
    case e: Map[MicroService, ActorSelection] =>
      log.info(s"Received all connectors: ${e.keys.mkString("[", ", ", "]")}")
      resolvedConnectors = e
      context.become(available)
    case _ => sender() ! "Not available"
  }

  def available: Receive = {
    case SayHi  => log.info("Hi ! I'm available")
    case _      => sender() ! "Available but unknown message"
  }

}
