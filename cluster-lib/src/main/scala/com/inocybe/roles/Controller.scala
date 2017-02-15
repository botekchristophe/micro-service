package com.inocybe.roles

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, RootActorPath}
import akka.cluster.Cluster
import com.inocybe.roles.Controller.ResolveConnectors
import com.inocybe.shared.model.MicroServices
import com.inocybe.shared.model.MicroServices.MicroService

import scala.concurrent.duration._
import scala.util.Failure

object Controller {
  case object ResolveConnectors
}

abstract class Controller(connectors: Set[MicroService] = Set.empty[MicroService]) extends Actor with ActorLogging with ClusterRole {

  implicit val system = context.system
  val cluster = Cluster(system)
  import system.dispatcher

  val resolveActors = system.scheduler.schedule(0.milliseconds, 5.seconds, self, ResolveConnectors)
  var actors: Map[MicroService, ActorSelection] = Map.empty[MicroService, ActorSelection]

  def receive = unavailable

  def unavailable: Receive = {
    case ResolveConnectors =>
      log.info("Currently unavailable. Resolving connectors...")
      resolveConnector
    case _ => sender() ! "Not available."
  }

  def available: Receive = ???

  def resolveConnector: Unit = {
    actors = cluster.state.roleLeaderMap
      .map {
        case (roleName, optAddress) => (MicroServices.fromName(roleName), context.actorSelection(RootActorPath(optAddress.get) / "user" / roleName)) }
      .filter {
        case (microservice, selection) => connectors.contains(microservice) }

    if (connectors.forall(actors.keySet.contains)) {
      context.become(available)
      log.info("Resolved all connectors. Switch context to available...")
    }
  }

  def pingActors = {
    actors.foreach {
      case (k, v) =>
        v.resolveOne(1.seconds).onComplete {
          case Failure(e) => context.become(unavailable); log.warning(s"$k failed to be resolve. Switch context to unavailable.")
      }
    }
  }
}

