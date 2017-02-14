package com.inocybe.roles

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import com.inocybe.roles.Service.{ItemCreated, NoContent}
import com.inocybe.shared.model.{ErrorDetail, ModelObject, ServiceException, ServiceExceptions}
import spray.json._

import scala.util.{Failure, Success, Try}

object Service {
  case class ItemCreated(item: ModelObject)
  case class NoContent()
}


abstract class Service(implicit jp: RootJsonFormat[ModelObject]) extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val errorFormat = jsonFormat4(ErrorDetail)

  val futureHandler: PartialFunction[Try[Any], server.Route] = {

    case Success(response: ModelObject)       =>
      complete(200, List(response).toJson)

    case Success(response: List[ModelObject]) =>
      complete(200, response.toJson)

    case Success(response: ItemCreated)       =>
      complete(201, List(response.item).toJson)

    case Success(response: NoContent)         =>
      complete(HttpResponse(status = StatusCodes.NoContent))

    case Success(e: ServiceException)         =>
      complete(e.code.intValue(), e.marshall)

    case Failure(e: akka.pattern.AskTimeoutException) =>
      complete(StatusCodes.ServiceUnavailable.intValue, ServiceExceptions.TimeoutException(e.getMessage))

    case Failure(e: ServiceException)         =>
      complete(e.code.intValue(), e.marshall)

    case Failure(e: Exception)                =>
      complete(500, ServiceExceptions.Default(e))

    case unknown: Any                         =>
      complete(500, ServiceExceptions.InternalServerError)
  }
}

