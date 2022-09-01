import sttp.client3.HttpClientSyncBackend
import zio.test._
import sttp.model.StatusCode
import sttp.client3._
import zio.Scope
import zio.json._

// 서버를 테스트해보세요
// https://zio.dev/reference/test/
// https://sttp.softwaremill.com/en/latest/quickstart.html
// https://sttp.softwaremill.com/en/latest/backends/zio.html

object AppSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("App")(
    test("Get Todo test") {
      val backend = HttpClientSyncBackend()
      val request = basicRequest.get(uri"http://localhost:8090/todo/1")
      val response = request.send(backend)
      val expected = Todo(1, "title1").toJson

      assertTrue(response.code == StatusCode.Ok) &&
      assertTrue(response.body == Right(expected))
    },
    test("Get Todo test 2") {
      import sttp.client3.ziojson._

      val backend = HttpClientSyncBackend()
      val request = basicRequest
        .get(uri"http://localhost:8090/todo/1")
        .response(asJson[Todo])
      val response = request.send(backend)
      val expected = Todo(1, "title1")

      assertTrue(response.code == StatusCode.Ok) &&
      assertTrue(response.body == Right(expected))
    },
    test("Get Todo test 3 - using fail-fast assertion") {
      import sttp.client3.ziojson._
      import zio._

      val backend = HttpClientSyncBackend()
      val request = basicRequest
        .get(uri"http://localhost:8090/todo/1")
        .response(asJson[Todo])

      // fail-fast assertion (zio 2.0.1)
      // @see https://github.com/zio/zio/pull/6923
      for {
        response <- ZIO.attempt(request.send(backend))
        _ <- assertTrue(response.code == StatusCode.Ok)
        expected = Todo(1, "title1")
        body = response.body
      } yield assertTrue(body == Right(expected))
    }
  )
}
