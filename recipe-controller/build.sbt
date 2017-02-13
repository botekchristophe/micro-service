name := "recipe-controller"

version := "1.0"

scalaVersion := "2.12.1"

organization := "com.inocybe"

scalacOptions := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")

fork := true

libraryDependencies ++= {
  val akkaV = "2.4.16"
  val akkaHTTPv = "10.0.3"

  Seq(
    "com.typesafe.akka"               %%  "akka-actor"          % akkaV,
    "com.typesafe.akka"               %%  "akka-cluster"        % akkaV,
    "com.typesafe.akka"               %%  "akka-cluster-tools"  % akkaV,
    "com.inocybe"                     %%  "cluster-lib"         % "1.04",
    "com.typesafe.akka"               %%  "akka-http-spray-json"% akkaHTTPv,
    "com.typesafe.akka"               %%  "akka-http"           % akkaHTTPv,
    "com.typesafe.akka"               %%  "akka-http-core"      % akkaHTTPv,
    "com.typesafe.akka"               %%  "akka-parsing"        % akkaHTTPv
  )
}

    