package com.recommender.service

import akka.stream.Materializer
import cats.data.EitherT
import com.google.inject.Inject
import com.recommender.model.Errors.{INTERNAL_SERVER_ERROR, MOVIE_NOT_FOUND}
import com.recommender.model.{SeriviceError, Movie}
import com.recommender.types.Types.{Result, failLeft, succeedPure}
import com.recommender.utils.FileReader
import RecommendationEngine._
import com.recommender.repo.MovieRepo
import spray.json._

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

case class CatalogueScore(catalogueMovie: Movie, score: Int)

case class RecommenderServiceImpl @Inject() (private val fileReader: FileReader, movieRepo: MovieRepo)
                                            (implicit ec: ExecutionContext, mat: Materializer) extends RecommenderService {

  override def recommendationByMovieId(movieId: String): Result[Vector[Movie]] = {
    for {
      catalogueMovies <- getAllMovies
      foundMovie <- findMovie(movieId, catalogueMovies)
      result <- findRecommendation(foundMovie, catalogueMovies)
    } yield result
  }

  private def findRecommendation(movie: Movie, catalogueMovies: Vector[Movie]): Result[Vector[Movie]] = {
    val recommendationByTags =
      succeedPure {
        val catalogueMovieScores =
          catalogueMovies.filterNot(_.id == movie.id).map { catalogueMovie =>
            val movieTagScore = catalogueMovie.scorePerTag(movie)
            val movieSimilarLengthScore = catalogueMovie.scorePerMovieLength(movie)

            val catScore = CatalogueScore(catalogueMovie, movieTagScore.score + movieSimilarLengthScore.score)

            println(s"\nTotal score for ${catScore.catalogueMovie.title} is ${catScore.score}\n")
            catScore
          }

        val sortedMoviesByRelevance = catalogueMovieScores.sortBy(_.score).reverse.map(_.catalogueMovie)
        sortedMoviesByRelevance
      }
    recommendationByTags
  }

  private def findMovie(movieId: String, catalogueMovies: Vector[Movie]): Result[Movie] =
    catalogueMovies.find(_.id == movieId).fold(failLeft[SeriviceError, Movie](MOVIE_NOT_FOUND)) { movie =>
      succeedPure(movie)
    }

  private def getAllMovies: Result[Vector[Movie]] = {
    for {
      repoMovies <- movieRepo.findAllMovies()
      result <- {
        if (repoMovies.isEmpty) {
          EitherT {
            fileReader.getMoviesFromResources.map { fileAsString =>
              fileAsString
                .parseJson
                .asJsObject
                .getFields("metadatas") match {
                case Seq(JsArray(elements)) =>
                  Right(
                    elements.map { element =>
                      element.convertTo[Movie]
                    }
                  )
              }
            } recover {
              case NonFatal(e) => Left(INTERNAL_SERVER_ERROR(e.getMessage))
            }
          }
        } else succeedPure(repoMovies)
      }
    } yield result
  }

}
