package com.recommender

import com.google.inject.Guice

object Main extends App {
  Guice.createInjector(new RecommendationModule())
}
