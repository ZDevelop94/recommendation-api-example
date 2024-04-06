package com.recommender.repo

import cats.data.EitherT
import com.google.inject.Inject
import com.recommender.model.Errors.INTERNAL_SERVER_ERROR
import com.recommender.model.{Movie, MovieEntity, MovieJoinEntity, TagEntity}
import com.recommender.repo.Definitions.PlainSQLs.allMoviesQuery
import com.recommender.types.Types.Result

import slick.basic.DatabaseConfig
import slick.jdbc.{GetResult, PostgresProfile}
import slick.sql.SqlStreamingAction

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait MovieRepo {
  def findAllMovies() : Result[Vector[Movie]]
}

case class MovieRepoImpl @Inject()(dbConfig: DatabaseConfig[PostgresProfile])
                                  (implicit executionContext: ExecutionContext) extends MovieRepo {
  override def findAllMovies(): Result[Vector[Movie]] = EitherT {
    dbConfig.db.run(allMoviesQuery().asTry) map {
      case Success(movieEntities) =>
        Right {
          val groupedMovies = movieEntities.groupBy(movieEntity =>
            Movie(movieEntity.id, movieEntity.title, movieEntity.length, Vector.empty)
          )

          groupedMovies.map {
            movieAndTags => movieAndTags._1.copy(tags =
              movieAndTags._2.map(_.tag)
            )
          }.toVector
        }
      case Failure(ex) =>
        println(s"Error with message: ${ex.getMessage}")
        Left(INTERNAL_SERVER_ERROR(s"Error with message: ${ex.getMessage}"))
    }
  }
}

object Definitions {

  import slick.jdbc.PostgresProfile.api._

  class MovieTable(tag: Tag) extends Table[MovieEntity](tag, "movies") {
    private def id: Rep[String] = column[String]("id", O.PrimaryKey)
    private def title: Rep[String] = column[String]("title")
    private def length: Rep[Int] = column[Int]("length")

    def * = (id, title, length) <> (MovieEntity.tupled, MovieEntity.unapply)
  }

  val movieTable: TableQuery[MovieTable] = TableQuery[MovieTable]

  class TagTable(tag: Tag) extends Table[TagEntity](tag, "tags") {
    private def uuid: Rep[String] = column[String]("uuid")
    private def title: Rep[String] = column[String]("title")
    private def id: Rep[String] = column[String]("movie_id")

    def * = (uuid, title, id) <> (TagEntity.tupled, TagEntity.unapply)
  }

  val tagTable: TableQuery[TagTable] = TableQuery[TagTable]

  object PlainSQLs {
    implicit val movieEntityResult: GetResult[MovieJoinEntity] = GetResult(r => MovieJoinEntity(
      r.nextString(),
      r.nextString(),
      r.nextInt(),
      r.nextString()
    ))

    def allMoviesQuery(): SqlStreamingAction[Vector[MovieJoinEntity], MovieJoinEntity, Effect] = {
      sql"""
         SELECT m.id, m.title, m.length, tags.title
         FROM movies as m FULL JOIN tags ON m.id = tags.movie_id
         """.as[MovieJoinEntity]
    }
  }

}