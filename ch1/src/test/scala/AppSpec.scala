import zio.test._

import java.io.IOException

// console를 테스트해보세요
// https://zio.dev/reference/test/
// https://zio.dev/reference/test/services/console/

object AppSpec extends ZIOSpecDefault {
  override def spec: Spec[Any, IOException] =
    suite("App")(
      test("Console Test") {
        for {
          _ <- TestConsole.feedLines("Seunguk")
          _ <- App.prog
          lines <- TestConsole.output
        } yield assertTrue(
          lines == Vector("Please enter your name: ", "Hello, Seunguk!\n")
        )
      }
    )
}
