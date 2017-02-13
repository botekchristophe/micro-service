package com.inocybe.recipe.model.exceptions

import akka.http.scaladsl.model.{StatusCodes => SC}

object ServiceExceptions {
  def TimeoutException(msg: String) = ErrorDetail(SC.ServiceUnavailable.intValue, SC.ServiceUnavailable.defaultMessage, SC.ServiceUnavailable.reason, msg)
  def Default(e: Exception) = ErrorDetail(SC.InternalServerError.intValue, SC.InternalServerError.defaultMessage, e.getMessage, e.getStackTrace.mkString("\\n"))
}