package com.inocybe.roles

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection}
import com.inocybe.protocol.ClusterListerner.{Resolve, SayHi}
import com.inocybe.shared.model.MicroServices.MicroService

abstract class Controller(connectors: List[MicroService] = List.empty[MicroService]) extends Actor with ActorLogging {

  implicit val system = context.system
  var clusterListener: ActorRef = null
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

  def available: Receive = ???

}

