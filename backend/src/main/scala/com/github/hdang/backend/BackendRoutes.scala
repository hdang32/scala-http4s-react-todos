package com.github.hdang.backend

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.effect.IO
import com.github.hdang.backend.repos.TodoRepo
import com.github.hdang.backend.repos.TodoRepo.CreateTodo
import org.http4s.circe.CirceEntityCodec._

object BackendRoutes {
  val dsl = new Http4sDsl[IO] {}
  import dsl._

  def jokeRoutes(J: Jokes): HttpRoutes[IO] =
    HttpRoutes.of[IO] { case GET -> Root / "joke" =>
      for {
        joke <- J.get
        resp <- Ok(joke)
      } yield resp
    }

  def helloWorldRoutes(H: HelloWorld): HttpRoutes[IO] =
    HttpRoutes.of[IO] { case GET -> Root / "hello" / name =>
      for {
        greeting <- H.hello(HelloWorld.Name(name))
        resp <- Ok(greeting)
      } yield resp
    }

  def todoRoutes(T: TodoRepo): HttpRoutes[IO] =
    HttpRoutes.of[IO] { 
      case GET -> Root / "todos" / IntVar(id) => 
        for {
          todo <- T.getById(id)
          resp <- todo.map(Ok(_)).getOrElse(NotFound())
        } yield resp
      case GET -> Root / "todos" =>
        for {
          todos <- T.getAll()
          resp <- Ok(todos)
        } yield resp
      case req @ POST -> Root / "todos" =>
        for { 
          request <- req.as[CreateTodo]
          todo <- T.insertTodo(request)
          resp <- Ok(todo)
        } yield resp
    }
}
