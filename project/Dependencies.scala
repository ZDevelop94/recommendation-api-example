object Dependencies {

  import sbt._
  import Versions._

  val akkaOrg = "com.typesafe.akka"

  val postgresql = "org.postgresql" % "postgresql" % postgresVersion
  val googleGuice = "com.google.inject" % "guice" % guiceVersion

  val slick = "com.typesafe.slick" %% "slick" % slickVersion

  val typesafeConfig = "com.typesafe" % "config" % configVersion

  val akka = akkaOrg %% "akka-actor-typed" % akkaVersion
  val akkaHttp = akkaOrg %% "akka-http" % akkaHttpVersion
  val akkaHttpJson = akkaOrg %% "akka-http-spray-json" % akkaHttpVersion
  val akkaStream = akkaOrg %% "akka-stream" % akkaVersion

  val cats = "org.typelevel" %% "cats-core" % catsVersion
  val scalaGuice = "net.codingwell" %% "scala-guice" % scalaGuiceVersion

  val sprayJson = "io.spray" %%  "spray-json" % sprayJsonVersion

  val akkaTestKit = akkaOrg %% "akka-actor-testkit-typed" % akkaVersion % Test
  val akkaHttpTestkit = akkaOrg %% "akka-http-testkit" % akkaHttpVersion % Test

  val scalatest = "org.scalatest" %% "scalatest" % scalaTestVersion % Test
  val scalamock = "org.scalamock" %% "scalamock" % scalaMockVersion % Test

  val logback = "ch.qos.logback" % "logback-classic" % "1.4.14"
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"

  private object Versions {

    val akkaVersion = "2.8.5"
    val akkaHttpVersion = "10.5.3"
    val catsVersion = "2.10.0"
    val configVersion = "1.4.3"
    val guiceVersion = "7.0.0"
    val postgresVersion = "42.7.1"
    val scalaGuiceVersion = "7.0.0"
    val scalaTestVersion = "3.2.17"
    val scalaMockVersion = "5.2.0"
    val slickVersion = "3.4.1"
    val sprayJsonVersion = "1.3.6"

  }
}

