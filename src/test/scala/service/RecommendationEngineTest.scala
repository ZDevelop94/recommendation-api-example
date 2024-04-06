package service

import com.recommender.model.Movie
import com.recommender.service.{CatalogueScore, RecommendationEngine}
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RecommendationEngineTest extends AnyWordSpec with MockFactory with Matchers {

  import RecommendationEngine._
  private val testMovieToMatch: Movie = Movie("5", "The Test", 100, Vector("Crime Family", "Drama", "Sports"))

  private val movie1 = Movie("2", "The Godfather", 105, Vector("Crime", "Drama", "Crime Family", "Mafia"))
  private val movie2 = Movie("3", "Forrest Gump", 111, Vector("Comedy", "Drama", "Romance"))
  private val movie3 = Movie("5", "Test Movie", 50, Vector("Sports Adventure", "Crime", "Drama"))

 "scorePerTag" should {
   s"return a the right score for the movie with tags ${movie1.tags.mkString(",")}" in {

     val CatalogueScore(_, result) = movie1.scorePerTag(testMovieToMatch)

     result shouldBe 4
   }

   s"return a the right score for the movie with tags ${movie2.tags.mkString(",")}" in {

     val CatalogueScore(_, result) = movie2.scorePerTag(testMovieToMatch)

     result shouldBe 2
   }

   s"return a the right score for the movie with tags ${movie3.tags.mkString(",")}" in {

     val CatalogueScore(_, result) = movie3.scorePerTag(testMovieToMatch)

     result shouldBe 3
   }
 }

  "scorePerMovieLength" should {
    s"return a the right score for the movie with length ${movie1.length}" in {

      val CatalogueScore(_, result) = movie1.scorePerMovieLength(testMovieToMatch)

      result shouldBe 4
    }

    s"return a the right score for the movie with length ${movie2.length}" in {

      val CatalogueScore(_, result) = movie2.scorePerMovieLength(testMovieToMatch)

      result shouldBe 3
    }

    "return a the right score for the movie with length 130, 139 and 1000" in {

      val testCatalogueMovie: Movie = Movie("5", "Test Movie", 130, Vector("Sports Adventure", "Crime", "Drama"))
      val testCatalogueMovie2: Movie = Movie("5", "Test Movie", 139, Vector("Sports Adventure", "Crime", "Drama"))
      val testCatalogueMovie3: Movie = Movie("5", "Test Movie", 1000, Vector("Sports Adventure", "Crime", "Drama"))

      val result = testCatalogueMovie.scorePerMovieLength(testMovieToMatch)
      val CatalogueScore(_, result2) = testCatalogueMovie2.scorePerMovieLength(testMovieToMatch)
      val CatalogueScore(_, result3) = testCatalogueMovie3.scorePerMovieLength(testMovieToMatch)

      result.score shouldBe 2
      result2 shouldBe 1
      result3 shouldBe 0
    }

    "return a the right score for the movie with length 70, 59 and 4" in {

      val testCatalogueMovie = Movie("5", "Test Movie", 74, Vector("Sports Adventure", "Crime", "Drama"))
      val testCatalogueMovie2 = Movie("5", "Test Movie", 69, Vector("Sports Adventure", "Crime", "Drama"))
      val testCatalogueMovie3 = Movie("5", "Test Movie", 4, Vector("Sports Adventure", "Crime", "Drama"))

      val CatalogueScore(_, result) = testCatalogueMovie.scorePerMovieLength(testMovieToMatch)
      val CatalogueScore(_, result2) = testCatalogueMovie2.scorePerMovieLength(testMovieToMatch)
      val CatalogueScore(_, result3) = testCatalogueMovie3.scorePerMovieLength(testMovieToMatch)

      result shouldBe 2
      result2 shouldBe 1
      result3 shouldBe 0
    }
  }
}
