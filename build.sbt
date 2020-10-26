name := """minesweeper-api"""
organization := "quique"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SwaggerPlugin)

scalaVersion := "2.13.3"

libraryDependencies += guice

swaggerDomainNameSpaces := Seq("user.dto", "board.dto")
swaggerV3 := true
