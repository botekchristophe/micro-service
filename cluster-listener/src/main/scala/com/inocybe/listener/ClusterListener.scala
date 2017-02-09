package com.inocybe.listener

import akka.actor.{ActorLogging, ActorSelection, RootActorPath}
import akka.cluster.ClusterEvent.{UnreachableMember, _}
import akka.cluster.{Cluster, Member, MemberStatus}
import akka.persistence.{PersistentActor, SnapshotOffer}
import com.inocybe.listener.ClusterListener.{ClusterState, HeartBeat, PrintState, SaveState}

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object ClusterListener {
  case object SaveState
  case object PrintState
  case object HeartBeat
  case class ClusterState(actors: Map[String, ActorSelection] = Map.empty[String, ActorSelection])
}

class ClusterListener extends PersistentActor with ActorLogging {

  val system = context.system
  val cluster = Cluster(system)
  import system.dispatcher


  system.scheduler.schedule(0.milliseconds, 5.seconds, self, PrintState)
  system.scheduler.schedule(0.milliseconds, 1.seconds, self, SaveState)
  system.scheduler.schedule(0.milliseconds, 10.seconds, self, HeartBeat)

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
    case PrintState                             => println(state.actors.map {case (k,v) => k} .mkString("actors alive: [", ", ", "]"))
    case HeartBeat                              => checkForActorsAlive()
    case MemberUp(member)                       => addMember(member)
    case UnreachableMember(member)              => log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus)  => removeMember(member, previousStatus)
  }


  /**
    * Add a new member to current status.
    *
    * @param member
    *
    */
  def addMember(member: Member) = {
    log.info(s"Member is Up: ${member.address}")
    val memberRef: ActorSelection = context.actorSelection(RootActorPath(member.address) / "user" / member.roles.head)
    memberRef ! "Hi !"
    state = state.copy(actors = state.actors + (member.roles.head -> memberRef))
  }

  /**
    * Remove a member from current state.
    *
    * @param member member to remove.
    * @param previousStatus its previous status.
    */
  def removeMember(member: Member, previousStatus: MemberStatus) = {
    log.info(s"Member is Removed: ${member.address} after $previousStatus")
    state =  state.copy(actors = state.actors - member.roles.head)
  }

  /**
    * Check for every actors in the cluster if they are still alive.
    */
  def checkForActorsAlive(): Unit = {
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
}
