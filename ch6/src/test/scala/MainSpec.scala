import sttp.client3.{UriContext, asString, basicRequest}
import sttp.client3.httpclient.zio.HttpClientZioBackend
import sttp.client3.ziojson.asJsonAlways
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server}
import zio._
import zio.test._

object MainSpec extends ZIOSpecDefault {
  val spec = suite("Main")(
    test("GET /hello returns 'hello'") {
      HttpClientZioBackend().flatMap { backend =>
        for {
          start <- ZIO.service[Server.Start]
          _ <- Console.printLine(s"Server started on port ${start.port}")
          res <- basicRequest
            .get(uri"http://localhost:${start.port}/hello")
            .response(asString)
            .send(backend)
        } yield assertTrue(res.body == Right("hello"))
      }
    },
    test("GET /todos returns todo list") {
      val expected = List(Todo(1, "title1"), Todo(2, "title2"))
      HttpClientZioBackend().flatMap { backend =>
        for {
          start <- ZIO.service[Server.Start]
          _ <- Console.printLine(s"Server started on port ${start.port}")
          res <- basicRequest
            .get(uri"http://localhost:${start.port}/todos")
            .response(asJsonAlways[List[Todo]])
            .send(backend)
          body <- ZIO.fromEither(res.body)
        } yield assertTrue(body == expected)
      }
    }
  ).provide(
    Scope.default,
    ZLayer {
      Server.app(Main.httpApp).withPort(0).make
    },
    EventLoopGroup.auto(1),
    ServerChannelFactory.auto
  )
}
