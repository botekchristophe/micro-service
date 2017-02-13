package com.inocybe.recipe.service

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import com.inocybe.recipe.model.JsonProtocol._
import com.inocybe.recipe.model.ModelObject
import com.inocybe.recipe.model.exceptions.{ServiceException, ServiceExceptions}
import com.inocybe.recipe.service.Service._
import spray.json.{JsNumber, JsObject, _}

import scala.util.{Failure, Success, Try}

object Service {
  case class ItemCreated(item: ModelObject)
  case class ItemInfo(item: ModelObject)
  case class ItemsInfo(items: List[ModelObject])
  case class ItemDeleted(item: ModelObject)
  case class ItemsDeleted(items: List[ModelObject])
  case class NoContent()
}


trait Service {

  val futureHandler: PartialFunction[Try[Any], server.Route] = {
    case Success(response: ItemCreated)       =>
      complete(201, response.item)

    case Success(response: ItemInfo)          =>
      complete(200, response.item)

    case Success(response: ItemsInfo)         =>
      complete(200, JsObject("items" -> response.items.toJson, "total" -> JsNumber(response.items.size)))

    case Success(response: ItemDeleted)       =>
      complete(StatusCodes.Accepted.intValue, response.item)

    case Success(response: ItemsDeleted)      =>
      complete(StatusCodes.Accepted.intValue, JsObject("items" -> response.items.toJson, "total" -> JsNumber(response.items.size)))

    case Success(response: NoContent)         =>
      complete(HttpResponse(status = StatusCodes.NoContent))

    case Success(e: ServiceException)             =>
      complete(e.code.intValue(), e.marshall)

    case Failure(e: akka.pattern.AskTimeoutException) =>
      complete(StatusCodes.ServiceUnavailable.intValue, ServiceExceptions.TimeoutException(e.getMessage))

    case Failure(e: ServiceException)             =>
      complete(e.code.intValue(), e.marshall)

    case Failure(e: Exception)                =>
      complete(500, ServiceExceptions.Default(e))

    case unknown: Any                         =>
      complete(500, unknown.toString)
  }
}
