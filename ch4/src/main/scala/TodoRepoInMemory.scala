import zio.{Chunk, Ref, Task, ZLayer}

final case class TodoRepoInMemory(var ref: Ref[Chunk[Todo]]) extends TodoRepo {
  def create(title: String): Task[Todo] = ref.modify { list =>
    val newId = list.length + 1
    val newTodo = Todo(newId, title)

    (newTodo, list :+ newTodo)
  }

  def findById(id: Long): Task[Option[Todo]] =
    ref.get.map(_.find(_.id == id))

  def listAll: Task[Chunk[Todo]] = ref.get
}

object TodoRepoInMemory {
  val todoList: Chunk[Todo] =
    Chunk(Todo(1, "title-1"), Todo(2, "title-2"), Todo(3, "title-3"))

  lazy val layer: ZLayer[Any, Nothing, TodoRepoInMemory] =
    ZLayer {
      for {
        ref <- Ref.make(todoList)
      } yield new TodoRepoInMemory(ref)
    }
}
