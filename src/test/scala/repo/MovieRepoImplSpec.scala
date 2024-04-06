package repo

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import com.recommender.StandardType
import com.recommender.model.{Movie, MovieEntity, TagEntity}
import com.recommender.repo.Definitions._
import com.recommender.repo.MovieRepoImpl
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import slick.basic.DatabaseConfig
import slick.jdbc.PostgresProfile

import java.util.UUID
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration.DurationInt

class MovieRepoImplSpec extends AnyWordSpec with MockFactory with ScalaFutures with Matchers with BeforeAndAfterAll {

  implicit val dbConfig: DatabaseConfig[PostgresProfile] = DatabaseConfig.forConfig("recodb")
  val db = dbConfig.db

  import dbConfig.profile.api._

  implicit val system: ActorSystem[StandardType] = ActorSystem(Behaviors.empty[StandardType], "test")
  implicit val ex: ExecutionContextExecutor = system.executionContext

  private val movieEntity1 = MovieEntity("2", "The Godfather", 105)
  private val movieEntity2 = MovieEntity("3", "Forrest Gump", 111)
  private val movieEntity3 = MovieEntity("5", "Test Movie", 50)

  private val tag1 = TagEntity(UUID.randomUUID().toString, "Crime", movieEntity1.id)
  private val tag2 = TagEntity(UUID.randomUUID().toString, "Comedy", movieEntity2.id)
  private val tag3 = TagEntity(UUID.randomUUID().toString, "Drama", movieEntity3.id)
  private val tag4 = TagEntity(UUID.randomUUID().toString, "Crime", movieEntity3.id)

  Await.result({
    for {
      _ <- db.run(movieTable += movieEntity1)
      _ <- db.run(movieTable += movieEntity2)
      _ <- db.run(movieTable += movieEntity3)
      _ <- db.run(tagTable += tag1)
      _ <- db.run(tagTable += tag2)
      _ <- db.run(tagTable += tag3)
      _ <- db.run(tagTable += tag4)
    } yield {
      println("Database insert complete in Database setup")
      Right[Exception, Boolean](true)
    }
  }, 10.seconds)

  override def afterAll(): Unit = {
    Await.result({
      for {
        _ <- db.run(tagTable.delete)
        _ <- db.run(movieTable.delete)
      } yield {
        println("Database Delete completed")
        Right[Exception, Boolean](true)
      }
    }, 10.seconds)
    super.afterAll()
  }

  private val repo = MovieRepoImpl(dbConfig)

  "MovieRepoImpl" should {

    val movie1 = Movie("2", "The Godfather", 105, Vector("Crime"))
    val movie2 = Movie("3", "Forrest Gump", 111, Vector("Comedy"))
    val movie3 = Movie("5", "Test Movie", 50, Vector("Drama", "Crime"))

    "return movies" in {
      val Right(result) = repo.findAllMovies().value.futureValue

      result.size shouldBe 3
      result should contain(movie1)
      result should contain(movie2)
      result should contain(movie3)
    }
  }
}
