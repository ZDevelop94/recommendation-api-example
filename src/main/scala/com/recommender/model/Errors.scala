package com.recommender.model

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

final case class SeriviceError(code: String, message: String) extends Exception(message)

object ServiceErrorFormatter extends DefaultJsonProtocol {
  implicit val recoErrorFormat: RootJsonFormat[SeriviceError] = jsonFormat2(SeriviceError.apply)
}

object Errors {

  val MOVIE_NOT_FOUND: SeriviceError = SeriviceError("Reco-1", "Movie provided could not be found")
  val BAD_REQUEST: String => SeriviceError =
    (message: String) => SeriviceError("Reco-2", s"Bad request made because of $message")

  val INTERNAL_SERVER_ERROR: String => SeriviceError =
    (message: String) => SeriviceError("Reco-500", s"Fatal error caused by: $message")

}
