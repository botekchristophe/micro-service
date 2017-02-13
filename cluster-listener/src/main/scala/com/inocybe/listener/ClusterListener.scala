package com.inocybe.listener

import akka.actor.{ActorLogging, ActorRef, ActorSelection, RootActorPath}
import akka.cluster.ClusterEvent.{UnreachableMember, _}
import akka.cluster.{Cluster, Member, MemberStatus}
import akka.persistence.{PersistentActor, SnapshotOffer}
import com.inocybe.listener.ClusterListener._
import com.inocybe.protocol.ClusterListerner._
import com.inocybe.shared.model.MicroServices
import com.inocybe.shared.model.MicroServices.MicroService

import scala.concurrent.duration._
import scala.util.{Failure, Success}

import scala.collection.JavaConverters._

object ClusterListener {
  case object SaveState
  case object PrintState
  case object CheckActors
  case object CheckRequests
  case class ResolveRequest(roles: List[MicroService], requester: ActorRef)
  case class ClusterState(actors: Map[MicroService, ActorSelection] = Map.empty[MicroService, ActorSelection],
                          requests: List[ResolveRequest]            = List.empty[ResolveRequest])
}

class ClusterListener extends PersistentActor with ActorLogging {

  val system = context.system
  val cluster = Cluster(system)
  import system.dispatcher

  system.scheduler.schedule(0.milliseconds, 5.seconds, self, PrintState)
  system.scheduler.schedule(0.milliseconds, 5.seconds, self, SaveState)
  system.scheduler.schedule(0.milliseconds, 10.seconds, self, CheckRequests)



  override def persistenceId: String = "cference" //id doesnt matter here, it just needs to be unique

  /**
    * Current state of the cluster
    */
  var state = ClusterState()

  /**
    * subscribe itself to the cluster on [[MemberEvent]] and [[UnreachableMember]]
    */
  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
    self ! CheckActors
  }

  /**
    * unsubscribe itself from the cluster
    */
  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  /**
    * Context used when cluster-listener is recovering from failure.
    */
  override def receiveRecover: Receive = {
    case SnapshotOffer(_, snapshot: ClusterState) => state = snapshot
  }

  /**
    * Context used when cluster-listener is
    * watching over the cluster.
    */
  override def receiveCommand: Receive = {
    case SaveState                              => saveSnapshot(state)
    case PrintState                             =>
      //println(cluster.state.getMembers.asScala.map(member => RootActorPath(member.address) / "user" / member.roles.head).mkString(" | "))
      //println(state.actors.map {case (k,v) => k} .mkString("actors alive: [", ", ", "]"))
      println(cluster.state.roleLeaderMap)
    case CheckActors                            => checkForActorsAlive()
    case CheckRequests                          => checkForRequests()
    case MemberUp(member)                       => addMember(member)
    case UnreachableMember(member)              => log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus)  => removeMember(member, previousStatus)
    case Resolve(actors: List[MicroService])    => addRequestAndCheck(ResolveRequest(actors, sender()))
    case s: CurrentClusterState                 => log.info(s"cluster state: $s")
  }


  /**
    * Add a new member to current status.
    *
    * @param member member to add.
    *
    */
  private def addMember(member: Member) = {
    log.info(s"Member is Up: ${member.address}")
    val memberRef: ActorSelection = context.actorSelection(RootActorPath(member.address) / "user" / member.roles.head)
    val microService = MicroServices.fromString(member.roles.head)
    memberRef ! SayHi
    state = state.copy(actors = state.actors + (microService -> memberRef))
    self ! CheckRequests
  }

  /**
    * Remove a member from current state.
    *
    * @param member member to remove.
    * @param previousStatus its previous status.
    */
  private def removeMember(member: Member, previousStatus: MemberStatus) = {
    log.info(s"Member is Removed: ${member.address} after $previousStatus")
    state =  state.copy(actors = state.actors - MicroServices.fromString(member.roles.head))
  }

  /**
    * Check for every actors in the cluster if they are still alive.
    */
  private def checkForActorsAlive(): Unit = {
    state.actors.par.foreach {
      case (role, selection) =>
        selection.resolveOne(1.seconds).onComplete {
          case Success(actorRef)  => // everything is OK
          case Failure(e)         =>
            log.warning(s"Cannot reach actor with role $role... Removing...")
            state = state.copy(actors = state.actors - role)
        }
    }
  }

  private def checkForRequests(): Unit = {
    val unresolvedRequest = state.requests
      .map( request =>
        if(state.actors.keys.toList.containsSlice(request.roles)) {
          log.info(s"Resolved a request for: ${request.roles.mkString("[", ", ", "]")}")
          request.requester ! resolve(request.roles)
          (request, true)
        } else {
          (request, false)
        })
      .filter { case (request, resolved) => !resolved }
      .map { case (request, resolved) => request }
    state = state.copy(requests = unresolvedRequest)
  }

  private def addRequestAndCheck(request: ResolveRequest): Unit = {
    log.info(s"Received a request for: ${request.roles.mkString("[", ", ", "]")}")
    state = state.copy(requests = state.requests.::(request))
    self ! CheckRequests
  }

  /**
    *
    * @param actors
    * @return
    */
  private def resolve(actors: List[MicroService]): Map[MicroService, ActorSelection] = {
    state.actors.filter {
      case (role, selection) => actors.contains(role)
    }
  }
}
