package com.inocybe.recipe.controller

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.inocybe.recipe.service.RecipeService
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

object Main {

  def main(args: Array[String]): Unit = {

    val conf = ConfigFactory.parseString(s"akka.cluster.roles=[RecipeController]").
      withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=0")).
      withFallback(ConfigFactory.load())
    implicit val system = ActorSystem("ClusterSystem", conf)

    implicit val materializer = ActorMaterializer()

    implicit val executionContext = system.dispatcher

    implicit val timeout = Timeout(15.seconds)

    val controller = system.actorOf(Props[RecipeController], "RecipeController")
    val service = new RecipeService(controller)

    Http().bindAndHandle(service.route, "localhost", 8888)
  }
}
