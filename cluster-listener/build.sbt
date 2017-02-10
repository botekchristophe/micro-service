name := "cluster-listener"

version := "1.0"

scalaVersion := "2.12.1"

organization := "com.inocybe"

scalacOptions := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")

fork := true

libraryDependencies ++= {
  val akkaV = "2.4.16"

  Seq(
    "com.typesafe.akka"               %%  "akka-actor"          % akkaV,
    "com.typesafe.akka"               %%  "akka-cluster"        % akkaV,
    "com.typesafe.akka"               %%  "akka-cluster-tools"  % akkaV,
    "com.typesafe.akka"               %%  "akka-persistence"    % akkaV,
    "org.iq80.leveldb"                %   "leveldb"             % "0.7",
    "org.fusesource.leveldbjni"       %   "leveldbjni-all"      % "1.8",
    "com.inocybe"                     %%  "cluster-lib"         % "1.04"
  )
}
    