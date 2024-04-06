package com.recommender.api

import akka.http.scaladsl.server.Route
import com.google.inject.{Inject, Provider, Singleton}

@Singleton
class AllRoutes @Inject()(recommendationApi: RecommendationApi) extends Provider[Route] {

  val routes: Route =
    recommendationApi.routes

  val get: Route = routes
}
