package com.recommender.service

import com.recommender.model.Movie
import com.recommender.types.Types._

trait RecommenderService {

  def recommendationByMovieId(movieId: String): Result[Vector[Movie]]

}
