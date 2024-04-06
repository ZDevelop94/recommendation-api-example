package com.recommender.service

import com.recommender.model.Movie

object RecommendationEngine {

  implicit class EngineImplicit(catalogueMovie: Movie) {
    def scorePerTag(movie: Movie): CatalogueScore = {

      val totalScore =
        movie.tags.map { tag =>
          val catalogueMovieTagScores = catalogueMovie.tags.map {
            case catTag if catTag == tag => 2
            case catTag if catTag.contains(tag) => 1
            case _ => 0
          }

          val tagScore = catalogueMovieTagScores.sum
          tagScore
        }.sum

      println(s"Total score for all tags $totalScore") //info level log

      CatalogueScore(catalogueMovie, totalScore)
    }

    def scorePerMovieLength(movie: Movie): CatalogueScore = {

      val catalogueDurationDifference = catalogueMovie.length - movie.length

      val catalogueMovieTagScores = catalogueDurationDifference match {
        case length if length >= -10 && length <= 10 => 4
        case length if length >= -20 && length <= 20 => 3
        case length if length >= -30 && length <= 30 => 2
        case length if length >= -40 && length <= 40 => 1
        case _ => 0
      }

      println(s"Score for similar movie length $catalogueMovieTagScores") //info level log

      CatalogueScore(catalogueMovie, catalogueMovieTagScores)
    }
  }
}
