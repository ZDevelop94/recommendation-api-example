package com.recommender.utils

import akka.stream.Materializer
import akka.stream.scaladsl.FileIO
import com.google.inject.Singleton

import java.nio.file.Paths
import scala.concurrent.Future

trait FileReader {
  def getMoviesFromResources(implicit mat: Materializer): Future[String]
}

@Singleton
case class FileReaderImpl() extends FileReader {

  def getMoviesFromResources(implicit mat: Materializer): Future[String] = {
    val filePath = Paths.get("src/main/resources/metadatas.json")

    FileIO
      .fromPath(filePath)
      .runFold("")((u, t) => u + t.utf8String)
  }
}
