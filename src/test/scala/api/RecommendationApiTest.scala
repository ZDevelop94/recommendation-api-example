package api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, NotFound, OK}
import akka.http.scaladsl.model.{ContentType, MediaTypes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.recommender.api.RecommendationApi
import com.recommender.model.Errors.{BAD_REQUEST, INTERNAL_SERVER_ERROR, MOVIE_NOT_FOUND}
import com.recommender.model.ServiceErrorFormatter._
import com.recommender.model.{Movie, SeriviceError}
import com.recommender.service.RecommenderService
import com.recommender.types.Types._
import com.recommender.utils.FileReaderImpl
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json._

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class RecommendationApiTest
  extends AnyWordSpec with ScalatestRouteTest with Matchers with MockFactory with SprayJsonSupport {
  private val recommenderService = mock[RecommenderService]

  private val recommendationApi = new RecommendationApi(recommenderService)

  val stubMovies: Vector[Movie] =
    readMetadata
      .parseJson
      .asJsObject
      .getFields("metadatas") match {
      case Seq(JsArray(elements)) =>
        elements.map{ element =>
          element.convertTo[Movie]
        }
    }


  "recommendation api" should {
    "return movies" in {

      val movie1 = stubMovies.head

      val expectedMovies = Vector(
        stubMovies(1),
        stubMovies(3)
      )

      (recommenderService.recommendationByMovieId _)
        .expects(movie1.id)
        .returns(succeedPure(expectedMovies))

      Get(s"/recommendation/${movie1.id}") ~> recommendationApi.routes ~> check{
        status shouldEqual OK
        contentType shouldEqual ContentType(MediaTypes.`application/json`)
        responseAs[Vector[Movie]] shouldEqual expectedMovies
      }
    }

    "returns 404 when a movie cannot be found" in {
      val movie1 = stubMovies.head

      (recommenderService.recommendationByMovieId _)
        .expects(movie1.id)
        .returns(failLeft[SeriviceError, Vector[Movie]](MOVIE_NOT_FOUND))

      Get(s"/recommendation/${movie1.id}") ~> recommendationApi.routes ~> check{
        status shouldEqual NotFound
        contentType shouldEqual ContentType(MediaTypes.`application/json`)
        responseAs[SeriviceError] shouldEqual MOVIE_NOT_FOUND
      }
    }

    "returns 400 when a none number is provided as a movie id" in {

      Get(s"/recommendation/fvdvdf") ~> recommendationApi.routes ~> check{
        status shouldEqual BadRequest
        responseAs[SeriviceError] shouldEqual BAD_REQUEST("id provided was not a number")
      }
    }

    "returns 500 when recommenderService returns a fatal error " in {
      val movie1 = stubMovies.head

      (recommenderService.recommendationByMovieId _)
        .expects(movie1.id)
        .returns(failLeft(INTERNAL_SERVER_ERROR("error")))

      Get(s"/recommendation/${movie1.id}") ~> recommendationApi.routes ~> check{
        status shouldEqual InternalServerError
        contentType shouldEqual ContentType(MediaTypes.`application/json`)
        responseAs[SeriviceError] shouldEqual INTERNAL_SERVER_ERROR("error")
      }
    }
  }

  private def readMetadata: String = {
    Await.result(
      FileReaderImpl().getMoviesFromResources,
      10.seconds
    )
  }
}
