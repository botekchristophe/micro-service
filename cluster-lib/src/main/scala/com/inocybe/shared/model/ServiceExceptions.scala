package com.inocybe.shared.model

import akka.http.scaladsl.model.{StatusCodes => SC}

object ServiceExceptions {
  def TimeoutException(msg: String) = ServiceException(SC.ServiceUnavailable, SC.ServiceUnavailable.reason, msg)
  def Default(e: Exception) = ServiceException(SC.InternalServerError, e.getMessage, e.getStackTrace.mkString("\\n"))
  def InternalServerError = ServiceException(SC.InternalServerError, SC.InternalServerError.defaultMessage, SC.InternalServerError.defaultMessage)
  def NotImplementedException(msg: String) = ServiceException(SC.NotImplemented, SC.NotImplemented.reason, msg)
}
