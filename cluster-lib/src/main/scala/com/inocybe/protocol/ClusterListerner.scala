package com.inocybe.protocol

import com.inocybe.shared.model.MicroServices.MicroService

object ClusterListerner {
  case class Resolve(arctors: List[MicroService])
  case object SayHi
}
