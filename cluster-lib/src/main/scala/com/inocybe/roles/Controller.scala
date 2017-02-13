package com.inocybe.roles

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, RootActorPath}
import akka.cluster.Cluster
import com.inocybe.roles.Controller.ResolveConnectors
import com.inocybe.shared.model.MicroServices
import com.inocybe.shared.model.MicroServices.MicroService

import scala.concurrent.duration._

object Controller {
  case object ResolveConnectors
}

abstract class Controller(connectors: Set[MicroService] = Set.empty[MicroService]) extends Actor with ActorLogging with ClusterRole {

  implicit val system = context.system
  val cluster = Cluster(system)
  import system.dispatcher

  system.scheduler.schedule(0.milliseconds, 1.seconds, self, ResolveConnectors)
  var clusterListener: ActorRef = null
  var resolvedConnectors: Map[MicroService, ActorSelection] = Map.empty[MicroService, ActorSelection]

  def receive = unavailable

  def unavailable: Receive = {
    case ResolveConnectors =>
      log.info("Currently unavailable. Resolving connectors...")
      resolveConnector
    case _ => sender() ! "Not available."
  }

  def available: Receive = ???

  def resolveConnector: Unit = {
    val names = connectors.map(_.roleName)
    val availableConnector = cluster.state.allRoles.intersect(names)

    if (availableConnector.size == names.size) {
      resolvedConnectors = cluster.state.roleLeaderMap.map {
        case (roleName, optAddress) => (MicroServices.fromName(roleName), context.actorSelection(RootActorPath(optAddress.get) / "user" / roleName))
      }
      context.become(available)
      log.info("Resolved all connectors. Becoming avaialable...")
    }
  }

}

