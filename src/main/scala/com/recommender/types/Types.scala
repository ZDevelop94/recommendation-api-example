package com.recommender.types

import cats.data.EitherT
import com.recommender.model.SeriviceError

import scala.concurrent.{ExecutionContext, Future}

object Types {

  type Result[T] = EitherT[Future, SeriviceError, T]

  def succeedPure[T](t: T)(implicit ec: ExecutionContext): Result[T]  =
    EitherT[Future, SeriviceError, T](Future(Right(t)))

  def failLeft[X <: SeriviceError, T](ex: X)(implicit ec: ExecutionContext): Result[T] = {
    EitherT[Future, SeriviceError, T](Future(Left(ex)))
  }
}
