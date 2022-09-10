import zhttp.http.{Http, Request, Response}
import zhttp.service.Server
import zio.{Chunk, Ref, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object MainApp extends ZIOAppDefault {
  var todoList: Chunk[Todo] = Chunk(Todo(1, "title1"), Todo(2, "title2"))
  val makeThreadSafeTodoRepo: ZIO[Any, Nothing, ThreadSafeTodoRepoInMemory] =
    for {
      ref <- Ref.make(todoList)
    } yield new ThreadSafeTodoRepoInMemory(ref)

  def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Unit] =
    for {
      repo <- makeThreadSafeTodoRepo
      _ <- Server.start(8090, TodoApp.httpApp(repo))
    } yield ()
}

object NotThreadSafeExample extends ZIOAppDefault {
  var todoList: Chunk[Todo] = Chunk(Todo(1, "title1"), Todo(2, "title2"))
  val repo = new NotThreadSafeTodoRepoInMemory(todoList)

  def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Nothing] =
    Server.start(8090, TodoApp.httpApp(repo))
}
