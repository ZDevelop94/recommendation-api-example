package com.recommender.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, NotFound, OK}
import akka.http.scaladsl.server.{Directives, Route}
import com.google.inject.Inject
import com.recommender.model.SeriviceError
import com.recommender.model.Errors.{BAD_REQUEST, INTERNAL_SERVER_ERROR, MOVIE_NOT_FOUND}
import com.recommender.service.RecommenderService
import spray.json.DefaultJsonProtocol
import com.recommender.model.ServiceErrorFormatter._

import scala.util.{Failure, Success, Try}

class RecommendationApi @Inject()(recommenderService: RecommenderService)
  extends Directives with SprayJsonSupport with DefaultJsonProtocol  {

  val routes: Route =
    path("recommendation" / Segment) { movieId =>
      pathEndOrSingleSlash {
        get {
          Try(movieId.toInt) match {
            case Success(_) =>
              onComplete(recommenderService.recommendationByMovieId(movieId).value) {
                case Success(Right(movies)) => complete(movies)
                case Success(Left(MOVIE_NOT_FOUND)) => complete(NotFound -> MOVIE_NOT_FOUND)
                case Success(Left(e: SeriviceError)) if e.code == "Reco-2" => complete(BadRequest -> e)
                case Success(Left(exception)) =>  complete(InternalServerError -> exception)
                case Failure(exception) => complete(InternalServerError -> INTERNAL_SERVER_ERROR(exception.getMessage))
              }
            case Failure(_) => complete(BadRequest -> BAD_REQUEST("id provided was not a number"))
          }
        }
      }
    }
}