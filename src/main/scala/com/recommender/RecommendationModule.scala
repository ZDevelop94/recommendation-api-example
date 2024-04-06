package com.recommender

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.server.Route
import akka.stream.{Materializer, SystemMaterializer}
import com.google.inject.AbstractModule
import com.recommender.api.{AllRoutes, RecommendationService, ServiceInitialiser}
import com.recommender.repo.{MovieRepo, MovieRepoImpl}
import com.recommender.service.{RecommenderService, RecommenderServiceImpl}
import com.recommender.utils.{FileReader, FileReaderImpl}
import com.typesafe.config.ConfigFactory
import slick.basic.DatabaseConfig
import slick.jdbc.PostgresProfile
import net.codingwell.scalaguice.ScalaModule

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

trait StandardType

class RecommendationModule extends AbstractModule with ScalaModule {

  implicit val system: ActorSystem[StandardType] = ActorSystem(Behaviors.empty[StandardType], "recommendation-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  implicit val materializer: Materializer = SystemMaterializer(system).materializer

  private val config = ConfigFactory.load()

  private val dbConfig: DatabaseConfig[PostgresProfile] =
    DatabaseConfig.forConfig("recodb", config)

  override def configure(): Unit = {
    bind[RecommendationService].toProvider[ServiceInitialiser].asEagerSingleton()
    bind[Route].toProvider[AllRoutes]
    bind[ActorSystem[StandardType]].toInstance(system)
    bind[DatabaseConfig[PostgresProfile]].toInstance(dbConfig)
    bind[ExecutionContext].toInstance(executionContext)
    bind[Materializer].toInstance(materializer)

    bind[FileReader].to[FileReaderImpl]

    bind[RecommenderService].to[RecommenderServiceImpl]
    bind[MovieRepo].to[MovieRepoImpl]

  }
}
