package com.recommender.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

//would use refined types here but not enough time
case class Movie(id: String,
                 title: String,
                 length: Int,
                 tags: Vector[String]) //None empty list at typelevel would be good here

case class MovieEntity(id: String,
                       title: String,
                       length: Int)

case class TagEntity(uuid: String, movieId: String, title: String)

case class MovieJoinEntity(id: String,
                           title: String,
                           length: Int,
                           tag: String)

object Movie extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val movieFormat: RootJsonFormat[Movie] = jsonFormat4(Movie.apply)
}
