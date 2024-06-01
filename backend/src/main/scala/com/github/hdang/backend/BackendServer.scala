package com.github.hdang.backend

import cats.syntax.all._
import com.comcast.ip4s._
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import cats.effect.IO
import com.github.hdang.backend.repos.TodoRepo

object BackendServer {
  def run: IO[Nothing] = {
    for {
      client <- EmberClientBuilder.default[IO].build
      helloWorldAlg = HelloWorld.impl
      jokeAlg = Jokes.impl(client)
      todoService = new TodoRepo()

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = (
        BackendRoutes.helloWorldRoutes(helloWorldAlg) <+>
          BackendRoutes.jokeRoutes(jokeAlg) <+>
          BackendRoutes.todoRoutes(todoService)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      _ <-
        EmberServerBuilder
          .default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build
    } yield ()
  }.useForever
}
