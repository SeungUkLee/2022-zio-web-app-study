import zio._

trait TodoService {
  def getTodo(id: Long): Task[Option[Todo]]
  def getTodoList: Task[Chunk[Todo]]
  def createTodo(title: String): Task[Todo]
}

object TodoService {
  lazy val layer: ZLayer[TodoRepo, Nothing, TodoServiceLive] = {
    ZLayer {
      for {
        todoRepo <- ZIO.service[TodoRepo]
      } yield TodoServiceLive(todoRepo)
    }
  }

  final case class TodoServiceLive(todoRepo: TodoRepo) extends TodoService {
    def getTodo(id: Long): Task[Option[Todo]] =
      todoRepo.findById(id)

    def getTodoList: Task[Chunk[Todo]] =
      todoRepo.listAll

    def createTodo(title: String): Task[Todo] =
      todoRepo.create(title)
  }
}
