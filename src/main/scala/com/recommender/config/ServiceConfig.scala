package com.recommender.config

import com.typesafe.config.{Config, ConfigFactory}

trait ServiceConfig {

  val conf: Config = ConfigFactory.load()

  lazy val serverPort: Int = conf.getInt("http.port")

}
