import zhttp.http._
import zio._
import zio.json._

object TodoApp {
  def httpApp(todoRepo: TodoRepo): Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {
    case Method.GET -> !! / "todo" / id =>
      for {
        target <- ZIO.foreach(id.toLongOption)(todoRepo.findById)
      } yield {
        target match {
          case Some(value) => Response.json(value.toJson)
          case None        => Response.status(Status.NotFound)
        }
      }

    case Method.GET -> !! / "todos" =>
      for {
        todoList <- todoRepo.listAll
      } yield Response.json(todoList.toJson)

    case req @ Method.POST -> !! / "todo" =>
      for {
        body <- req.bodyAsString
        _ <- Console.printLine(s"body: $req")
        form <- ZIO
          .from(body.fromJson[CreateTodoForm])
          .mapError(e => new Throwable(e))
        newTodo <- todoRepo.create(form.title)
      } yield Response.json(newTodo.toJson)
  }
}
