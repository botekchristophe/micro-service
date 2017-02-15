package com.inocybe.recipe.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.inocybe.shared.model.{ErrorDetail, ModelObject}
import spray.json.{DeserializationException, JsValue, RootJsonFormat, _}

object JsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val errorFormat = jsonFormat4(ErrorDetail)
  implicit val recipeFormat = jsonFormat14(Recipe)

  implicit object ModelObjectJsonFormat extends RootJsonFormat[ModelObject] {

    def write(obj: ModelObject) = obj match {
      case o: ErrorDetail => o.toJson
      case o: Recipe      => o.toJson
      case _ => throw DeserializationException("This ModelObject does not have a json format")
    }
    def read(value: JsValue) = throw DeserializationException("Cannot deserialize to ModelObject")
  }
}