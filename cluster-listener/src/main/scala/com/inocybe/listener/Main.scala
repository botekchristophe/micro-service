package com.inocybe.listener

import akka.actor.{ActorSystem, Props}
import com.inocybe.shared.model.MicroServices
import com.typesafe.config.ConfigFactory

object Main {

  def main(args: Array[String]): Unit = {

    val conf = ConfigFactory.parseString(s"akka.cluster.roles=[${MicroServices.ClusterListener.roleName}]").
      withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=2551")).
      withFallback(ConfigFactory.load())
    implicit val system = ActorSystem("ClusterSystem", conf)

    val listener = system.actorOf(Props[ClusterListener], MicroServices.ClusterListener.roleName)
  }
}
