package com.inocybe.listener

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{UnreachableMember, _}
import com.inocybe.listener.ClusterListener._

import scala.concurrent.duration._

object ClusterListener {
  case object PrintState
}

class ClusterListener extends Actor with ActorLogging {

  val system = context.system
  val cluster = Cluster(system)
  import system.dispatcher

  system.scheduler.schedule(0.milliseconds, 4.seconds, self, PrintState)

  /**
    * subscribe itself to the cluster on [[MemberEvent]] and [[UnreachableMember]]
    */
  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  /**
    * unsubscribe itself from the cluster
    */
  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  def receive: Receive = {
    case PrintState                             => printState
    case MemberUp(member)                       => log.info(s"Member is Up: ${member.address}")
    case UnreachableMember(member)              => log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus)  => log.info(s"Member is Removed: ${member.address} after $previousStatus")
  }

  def printState: Unit = {
    println(cluster.state.roleLeaderMap
      .map { case (role, address) => (role, address.fold("DOWN")(_ => "UP"))}
      .mkString("Services : [", ", ", "]"))
    println(cluster.state.unreachable.mkString("Services Unreachable: [", ", ", "]"))
  }
}
