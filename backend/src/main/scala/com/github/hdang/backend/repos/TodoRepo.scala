package com.github.hdang.backend.repos
import doobie._
import doobie.implicits._

import cats.effect._
import cats.implicits._
import io.circe.Codec
import org.http4s.EntityEncoder
import org.http4s.EntityDecoder
import org.http4s.circe._
import io.circe.generic.semiauto._

class TodoRepo {
  import TodoRepo._
  private val xa = connectPostgres
  def insertTodo(todo: CreateTodo): IO[TodoRow] =
    insertQuery(todo).unique.transact(xa)
  def getAll(): IO[List[TodoRow]] = getAllQuery().to[List].transact(xa)
  def getById(id: Int): IO[Option[TodoRow]] =
    getByIdQuery(id).option.transact(xa)
}

object TodoRepo {

  implicit val todoCodec: Codec[TodoRow] = deriveCodec
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, TodoRow] = jsonOf
  implicit def entityEncoder[F[_]]: EntityEncoder[F, TodoRow] = jsonEncoderOf

  implicit val createTodoCodec: Codec[CreateTodo] = deriveCodec
  implicit def createTodoEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, CreateTodo] = jsonOf
  implicit def createTodoEntityEncoder[F[_]]: EntityEncoder[F, CreateTodo] = jsonEncoderOf

  val createTableSql =
    sql"create table todos(id serial not null primary key, description text not null)"

  def connectPostgres: Transactor[IO] = {
    // A transactor that gets connections from java.sql.DriverManager and executes blocking operations
    // on an our synchronous EC. See the chapter on connection handling for more info.
    Transactor.fromDriverManager[IO](
      driver = "org.postgresql.Driver", // JDBC driver classname
      url = "jdbc:postgresql:postgres", // Connect URL
      user = "postgres", // Database user name
      password = "example", // Database password
      logHandler =
        None // Don't setup logging for now. See Logging page for how to log events in detail
    )
  }

  case class CreateTodo(description: String)
  case class UpdateTodo(description: Option[String])
  case class TodoRow(id: Int, description: String)
  private def insertQuery(todo: CreateTodo): Query0[TodoRow] =
    sql"insert into todos (description) values (${todo.description}) returning id, description"
      .query[TodoRow]

  private def getAllQuery(): Query0[TodoRow] =
    sql"select * from todos"
      .query[TodoRow]

  private def getByIdQuery(id: Int): Query0[TodoRow] =
    sql"select * from todos where id = ${id}"
      .query[TodoRow]

  def typecheck(xa: Transactor[IO]): IO[Unit] = {
    val y = xa.yolo
    import y._
    List(
      insertQuery(CreateTodo("fake")).check,
      getAllQuery().check,
      getByIdQuery(0).check
    ).sequence.void
  }
}
