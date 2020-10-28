name := """minesweeper-api"""
organization := "quique"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SwaggerPlugin)

scalaVersion := "2.13.3"

libraryDependencies += guice

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.1" % Test

swaggerDomainNameSpaces := Seq("user.dto", "board.dto")
swaggerV3 := true

// Disable warnings for auto-generated files
play.sbt.routes.RoutesKeys.routesImport := Seq.empty

// Scoverage
coverageMinimum := 90
coverageFailOnMinimum := true
// Exclude auto-generated files from coverage report
lazy val coverageExcludedPackagesSeq = Seq(
  "<empty>",
  "Reverse.*",
  "router",
  "users",
  "boards",
  "user.dto.*",
  "board.dto.*",
)
coverageExcludedPackages := coverageExcludedPackagesSeq.mkString(";")
