import zhttp.http._
import zio._
import zio.json._

trait TodoApp {
  def httpApp: ZIO[Any, Nothing, HttpApp[Any, Throwable]]
}

object TodoApp {
  lazy val layer: ZLayer[TodoService, Nothing, TodoApp] = {
    ZLayer {
      for {
        todoService <- ZIO.service[TodoService]
      } yield TodoAppLive(todoService)
    }
  }

  def httpApp: ZIO[TodoApp, Nothing, HttpApp[Any, Throwable]] =
    ZIO.serviceWithZIO[TodoApp](_.httpApp)

  final case class TodoAppLive(service: TodoService) extends TodoApp {
    def httpApp: ZIO[Any, Nothing, HttpApp[Any, Throwable]] = for {
      app <- ZIO.succeed(todoRoutes)
    } yield app


    def todoRoutes: Http[Any, Throwable, Request, Response] =
      Http.collectZIO[Request] {
        case Method.GET -> !! / "todo" / id =>
          for {
            target <- ZIO.foreach(id.toLongOption)(service.getTodo)
          } yield {
            target match {
              case Some(value) => Response.json(value.toJson)
              case None        => Response.status(Status.NotFound)
            }
          }

        case Method.GET -> !! / "todos" =>
          for {
            todoList <- service.getTodoList
          } yield Response.json(todoList.toJson)

        case req @ Method.POST -> !! / "todo" =>
          for {
            body <- req.bodyAsString
            form <- ZIO
              .from(body.fromJson[CreateTodoForm])
              .mapError(e => new Throwable(e))
            newTodo <- service.createTodo(form.title)
          } yield Response.json(newTodo.toJson)
      }
  }
}

