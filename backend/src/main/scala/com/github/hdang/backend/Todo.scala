package com.github.hdang.backend

import scala.collection.mutable
import cats.effect.Concurrent
import org.http4s._
import org.http4s.circe._
import io.circe.generic.semiauto._
import io.circe.Codec

trait TodoService {
  import TodoService._
  def put(todo: Todo): Unit
  def get(id: String): Option[Todo]
  def getAll: List[Todo]
}

object TodoService {
  case class Todo(id: String, content: String)
  implicit val todoCodec: Codec[Todo] = deriveCodec
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, Todo] = jsonOf
  implicit def entityEncoder[F[_]]: EntityEncoder[F, Todo] = jsonEncoderOf

  def memoryTodo: TodoService = new MemoryTodoService()

  private class MemoryTodoService extends TodoService {
    private val db: mutable.HashMap[String, Todo] = mutable.HashMap.empty
    db.put("1", Todo("1", "asdf"))
    db.put("2", Todo("2", "jkl;"))
    override def put(todo: Todo): Unit = {
      val _ = db.put(todo.id, todo)
    }
    override def get(id: String): Option[Todo] = db.get(id)
    override def getAll: List[Todo] = db.values.toList
  }
}
