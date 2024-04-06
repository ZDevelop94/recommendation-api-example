package utils

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.{Materializer, SystemMaterializer}
import com.recommender.StandardType
import com.recommender.utils.FileReaderImpl
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.ExecutionContextExecutor

class FileReaderTest extends AnyWordSpec with ScalaFutures with Matchers {

  implicit val system: ActorSystem[StandardType] = ActorSystem(Behaviors.empty[StandardType], "test")
  implicit val ex: ExecutionContextExecutor = system.executionContext
  implicit val materializer: Materializer = SystemMaterializer(system).materializer

  private val fileReader = FileReaderImpl()

  "FileReader" should {
    "return a movie with The Shawshank Redemption" in {
      val result = fileReader.getMoviesFromResources.futureValue

      result.contains("The Shawshank Redemption") shouldBe true

    }
  }
}
