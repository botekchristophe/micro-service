package com.inocybe.shared.model

import akka.http.scaladsl.model.StatusCode

case class ErrorDetail(code: Int, error: String, message: String, info: String) extends ModelObject

case class ServiceException(code: StatusCode,
                            msg: String,
                            info: String) extends RuntimeException(msg) {
  def marshall: ErrorDetail = {
    ErrorDetail(
      code    = this.code.intValue(),
      error   = this.code.reason(),
      message = msg,
      info    = info
    )
  }
}