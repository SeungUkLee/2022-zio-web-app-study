import zio.{Chunk, Ref, Task, ZIO}

trait TodoRepo {
  def create(title: String): Task[Todo]
  def findById(id: Long): Task[Option[Todo]]
  def listAll: Task[Chunk[Todo]]
}

class ThreadSafeTodoRepoInMemory(var ref: Ref[Chunk[Todo]]) extends TodoRepo {
  def create(title: String): Task[Todo] = ref.modify { list =>
    val newId = list.length + 1
    val newTodo = Todo(newId, title)

    (newTodo, list :+ newTodo)
  }

  def findById(id: Long): Task[Option[Todo]] =
    ref.get.map(_.find(_.id == id))

  def listAll: Task[Chunk[Todo]] = ref.get
}

class NotThreadSafeTodoRepoInMemory(var todoList: Chunk[Todo])
  extends TodoRepo {
  def create(title: String): Task[Todo] = ZIO.attempt {
    val todo = Todo(todoList.length + 1, title)
    todoList = todoList :+ todo
    todo
  }

  def findById(id: Long): Task[Option[Todo]] =
    ZIO.succeed(todoList.find(_.id == id))

  def listAll: Task[Chunk[Todo]] = ZIO.succeed(todoList)
}
