import Dependencies._

name := "recommendation-api-example"

version := "0.2-SNAPSHOT"

scalaVersion := "2.13.13" //Scala 2.13.8 has a vulnerability that may be present 2.13.7 best to use latest

libraryDependencies ++= Seq(
  postgresql,
  googleGuice,
  slick,
  typesafeConfig,
  akka,
  akkaHttp,
  akkaHttpJson,
  cats,
  scalaGuice,
  sprayJson,
  akkaTestKit,
  akkaHttpTestkit,
  scalatest,
  scalamock,
  logback,
  scalaLogging,
  akkaStream
)