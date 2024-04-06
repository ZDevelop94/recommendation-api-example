package com.recommender.api

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import com.google.inject.{Inject, Provider}
import com.recommender.StandardType
import com.recommender.config.ServiceConfig

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.DurationInt
import scala.sys.addShutdownHook
import scala.util.{Failure, Success}

class ServiceInitialiser @Inject()(routes: AllRoutes)(implicit sys: ActorSystem[StandardType], ex: ExecutionContext)
  extends Provider[RecommendationService] with ServiceConfig {

  override def get(): RecommendationService = new RecommendationService {

    var soonToBeBinded: Option[Http.ServerBinding] = None

    Http().newServerAt("127.0.0.1", serverPort).bind(routes.routes) onComplete {
      case Success(boundServer) =>
        println(" RECOMMENDATION SERVICE STARTED")
        println(s"Available at: " +
          s"\n host:${boundServer.localAddress.getHostString}" +
          s"\n port: ${boundServer.localAddress.getPort}" +
          s"\n address: ${boundServer.localAddress.getAddress.getHostAddress}")
        soonToBeBinded = Some(boundServer)
      case Failure(exception) => println(s"**** Failed to start, caused by ${exception.getCause}" +
        s"\n with message: ${exception.getMessage}")
    }

    if (soonToBeBinded.isDefined) {
      addShutdownHook(Await.result(soonToBeBinded.get.unbind(), 10.seconds))
    }
  }
}

trait RecommendationService