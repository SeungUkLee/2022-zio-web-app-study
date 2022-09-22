import zio.{Chunk, Task}

trait TodoRepo {
  def create(title: String): Task[Todo]
  def findById(id: Long): Task[Option[Todo]]
  def listAll: Task[Chunk[Todo]]
}
