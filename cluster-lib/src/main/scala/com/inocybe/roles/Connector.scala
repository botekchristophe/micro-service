package com.inocybe.roles

import akka.actor.{Actor, ActorLogging}

abstract class Connector extends Actor with ActorLogging with ClusterRole
