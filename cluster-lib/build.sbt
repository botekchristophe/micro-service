name := "cluster-lib"

version := "1.04"

scalaVersion := "2.12.1"

organization := "com.inocybe"

scalacOptions := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")

fork := true

libraryDependencies ++= {
  val akkaV = "2.4.16"

  Seq(
    "com.typesafe.akka"               %%  "akka-actor"          % akkaV,
    "com.typesafe.akka"               %%  "akka-cluster"        % akkaV,
    "com.typesafe.akka"               %%  "akka-cluster-tools"  % akkaV
  )
}