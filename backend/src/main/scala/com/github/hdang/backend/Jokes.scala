package com.github.hdang.backend

import cats.effect.Concurrent
import cats.implicits._
import io.circe.generic.semiauto._

import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.circe._
import org.http4s.Method._
import cats.effect.IO
import io.circe.Codec

trait Jokes {
  def get: IO[Jokes.Joke]
}

object Jokes {
  final case class Joke(joke: String)
  object Joke {
    implicit val jokeEncoder: Codec[Joke] = deriveCodec
    implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, Joke] =
      jsonOf
    implicit def entityEncoder[F[_]]: EntityEncoder[F, Joke] = jsonEncoderOf
  }
  final case class JokeError(e: Throwable) extends RuntimeException

  def impl(C: Client[IO]): Jokes = new Jokes {
    val dsl = new Http4sClientDsl[IO] {}
    import dsl._
    def get: IO[Jokes.Joke] =
      C.expect[Joke](GET(uri"https://icanhazdadjoke.com/"))
        .adaptError { case t =>
          JokeError(t)
        } // Prevent Client Json Decoding Failure Leaking
  }
}
