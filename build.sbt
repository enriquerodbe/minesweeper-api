import play.core.PlayVersion.akkaVersion

name := """minesweeper-api"""
organization := "quique"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SwaggerPlugin)

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  guice,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion,
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
)

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.14.1",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0",
).map(_ % Test)

// play-swagger
swaggerDomainNameSpaces := Seq("placeholder") // Doesn't work without this
swaggerV3 := true

// Disable warnings for auto-generated files
play.sbt.routes.RoutesKeys.routesImport := Seq.empty

// Scoverage
coverageEnabled := true
coverageMinimum := 90
coverageFailOnMinimum := true
coverageExcludedPackages := Seq(
  "<empty>",
  "Reverse.*",
  "router",
  "authentication",
  "games",
).mkString(";")
