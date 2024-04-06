package service

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.{Materializer, SystemMaterializer}
import com.recommender.StandardType
import com.recommender.model.Errors.MOVIE_NOT_FOUND
import com.recommender.model.Movie
import com.recommender.repo.MovieRepo
import com.recommender.service.RecommenderServiceImpl
import com.recommender.types.Types.succeedPure
import com.recommender.utils.FileReader
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.{ExecutionContextExecutor, Future}

class RecommenderServiceTest
  extends AnyWordSpec with MockFactory with ScalaFutures with Matchers {

  implicit val system: ActorSystem[StandardType] = ActorSystem(Behaviors.empty[StandardType], "test")
  implicit val ex: ExecutionContextExecutor = system.executionContext
  implicit val materializer: Materializer = SystemMaterializer(system).materializer

  private val fileReader = mock[FileReader]
  private val movieRepo = mock[MovieRepo]

  val recommenderServiceImpl: RecommenderServiceImpl = RecommenderServiceImpl(fileReader, movieRepo)

  "RecommenderServiceTest" should {

    "return matching movies" in {

      val fileContents =
        """
          |{
          |  "metadatas": [
          |       {"id": "1", "title": "The Shawshank Redemption", "length": 100, "tags": ["Crime", "Drama", "Prison"]},
          |       {"id": "2", "title": "The Godfather", "length": 80, "tags": ["Crime", "Drama", "Crime Family", "Mafia"]},
          |       {"id": "3", "title": "Forrest Gump", "length": 139, "tags": ["Comedy", "Drama", "Romance"]},
          |       {"id": "11","title": "Logan", "length": 103, "tags": ["Action", "Drama", "Sci-Fi"]},
          |       {"id": "25", "title": "Inception", "length": 102, "tags": ["Action", "Adventure", "Sci-Fi"]}
          |    ]
          |}
          |""".stripMargin

      (movieRepo.findAllMovies _)
        .expects()
        .returning(succeedPure(Vector.empty))

      (fileReader.getMoviesFromResources (_: Materializer))
        .expects(materializer)
        .returning(Future(fileContents))

      val Right(result) = recommenderServiceImpl.recommendationByMovieId("1").value.futureValue

      result.size shouldBe 4

      result shouldBe Vector(
        Movie("2", "The Godfather", 80, Vector("Crime", "Drama", "Crime Family", "Mafia")),
        Movie("11", "Logan", 103, Vector("Action", "Drama", "Sci-Fi")),
        Movie("25", "Inception", 102, Vector("Action", "Adventure", "Sci-Fi")),
        Movie("3", "Forrest Gump", 139, Vector("Comedy", "Drama", "Romance"))
      )
    }

    "return matching movies when DB is used" in {

      (movieRepo.findAllMovies _)
        .expects()
        .returning(
          succeedPure(Vector(
            Movie("1", "The Shawshank Redemption", 100, Vector("Crime", "Drama", "Prison")),
            Movie("25", "Inception", 102, Vector("Action", "Adventure", "Sci-Fi")),
            Movie("11", "Logan", 103, Vector("Action", "Drama", "Sci-Fi")),
            Movie("2", "The Godfather", 80, Vector("Crime", "Drama", "Crime Family", "Mafia")),
            Movie("3", "Forrest Gump", 139, Vector("Comedy", "Drama", "Romance"))
          ))
        )

      val Right(result) = recommenderServiceImpl.recommendationByMovieId("1").value.futureValue

      result.size shouldBe 4

      result shouldBe Vector(
        Movie("2", "The Godfather", 80, Vector("Crime", "Drama", "Crime Family", "Mafia")),
        Movie("11", "Logan", 103, Vector("Action", "Drama", "Sci-Fi")),
        Movie("25", "Inception", 102, Vector("Action", "Adventure", "Sci-Fi")),
        Movie("3", "Forrest Gump", 139, Vector("Comedy", "Drama", "Romance"))
      )
    }

    "return movie not found" in {

      (movieRepo.findAllMovies _)
        .expects()
        .returning(
          succeedPure(Vector(
            Movie("25", "Inception", 102, Vector("Action", "Adventure", "Sci-Fi")),
            Movie("11", "Logan", 103, Vector("Action", "Drama", "Sci-Fi")),
            Movie("2", "The Godfather", 80, Vector("Crime", "Drama", "Crime Family", "Mafia")),
            Movie("3", "Forrest Gump", 139, Vector("Comedy", "Drama", "Romance"))
          ))
        )

      val Left(error) = recommenderServiceImpl.recommendationByMovieId("1").value.futureValue

      error shouldBe MOVIE_NOT_FOUND
    }
  }
}
