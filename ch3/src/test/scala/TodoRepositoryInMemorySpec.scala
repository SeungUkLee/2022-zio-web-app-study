import zio._
import zio.test._

object TodoRepositoryInMemorySpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("TodoRepositoryInMemory")(
      test("create (not thread safe todo repo)") {
        val todoListGen = Gen.listOfN(10)(Gen.alphaNumericStringBounded(2, 5))
        check(todoListGen) { todoList =>
          val repo = new NotThreadSafeTodoRepoInMemory(Chunk.empty)
          for {
            // newTodos <- ZIO.foreachPar(todoList)(repo.create) // ERROR: not thread safe!
            newTodos <- ZIO.foreach(todoList)(repo.create)
            list <- repo.listAll
          } yield assertTrue(newTodos.length == list.length)
        }
      },
      test("create (thread safe todo repo") {
        val todoListGen = Gen.listOfN(10)(Gen.alphaNumericStringBounded(2, 5))
        check(todoListGen) { todoList =>
          val makeRepo = for {
            ref <- Ref.make(Chunk.empty: Chunk[Todo])
          } yield new ThreadSafeTodoRepoInMemory(ref)

          for {
            repo <- makeRepo
            newTodos <- ZIO.foreachPar(todoList)(repo.create)
            list <- repo.listAll
          } yield assertTrue(newTodos.length == list.length)
        }
      }
    )
}
