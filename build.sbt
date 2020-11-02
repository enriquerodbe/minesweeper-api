import play.core.PlayVersion.akkaVersion
val silhouetteVersion = "7.0.0"
scalaVersion := "2.13.3"
maintainer := "enriquerodbe@gmail.com"

name := """minesweeper-api"""
organization := "quique"

version := sys.props.getOrElse("minesweeper.api.version", "1.0.0")

lazy val root = (project in file(".")).enablePlugins(PlayScala, SwaggerPlugin)

libraryDependencies ++= Seq(
  guice,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion,
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  "com.mohiva" %% "play-silhouette" % silhouetteVersion,
  "com.mohiva" %% "play-silhouette-password-bcrypt" % silhouetteVersion,
  "com.mohiva" %% "play-silhouette-persistence" % silhouetteVersion,
  "net.codingwell" %% "scala-guice" % "4.2.11",
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
coverageMinimum := 90
coverageFailOnMinimum := true
coverageExcludedPackages := Seq(
  "<empty>",
  "Reverse.*",
  "router",
  "authentication",
  "games",
).mkString(";")

// Dist files
sources in (Compile, doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false
topLevelDirectory := None
