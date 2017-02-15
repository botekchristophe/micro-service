package com.inocybe.neo4jconnector

import akka.actor.{ActorSystem, Props}
import com.inocybe.shared.model.MicroServices
import com.typesafe.config.ConfigFactory

object Main {

  def main(args: Array[String]): Unit = {

    val conf = ConfigFactory.parseString(s"akka.cluster.roles=[${MicroServices.Neo4jConnector.roleName}]").
      withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=0")).
      withFallback(ConfigFactory.load())
    implicit val system = ActorSystem("ClusterSystem", conf)

    val listener = system.actorOf(Props[Neo4jConnector], MicroServices.Neo4jConnector.roleName)
  }
}

