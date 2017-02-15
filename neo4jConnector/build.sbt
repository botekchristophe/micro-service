name := "neo4jConnector"

version := "1.0"

scalaVersion := "2.12.1"

organization := "com.inocybe"

scalacOptions := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")

fork := true

libraryDependencies ++= {
  val akkaV = "2.4.17"

  Seq(
    "com.typesafe.akka"               %%  "akka-actor"          % akkaV,
    "com.typesafe.akka"               %%  "akka-cluster"        % akkaV,
    "com.typesafe.akka"               %%  "akka-cluster-tools"  % akkaV,
    "com.inocybe"                     %%  "cluster-lib"         % "1.09",
    "org.neo4j.driver"                %   "neo4j-java-driver"   % "1.1.0"
  )
}
    