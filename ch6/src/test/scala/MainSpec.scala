import sttp.capabilities
import sttp.capabilities.zio.ZioStreams
import sttp.client3.{SttpBackend, UriContext, asStringAlways, basicRequest}
import sttp.client3.httpclient.zio.HttpClientZioBackend
import sttp.client3.ziojson.asJsonAlways
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server, ServerChannelFactory}
import zio._
import zio.test.Assertion.equalTo
import zio.test._

final case class TestDriver(
    port: Int,
    backend: SttpBackend[Task, ZioStreams with capabilities.WebSockets]
) {
  def hello: ZIO[Any, Throwable, String] =
    for {
      _ <- Console.printLine(s"Server started on port ${port}")
      res <- basicRequest
        .get(uri"http://localhost:${port}/hello")
        .response(asStringAlways)
        .send(backend)
    } yield res.body

  def list: ZIO[Any, Throwable, List[Todo]] =
    for {
      _ <- Console.printLine(s"Server started on port ${port}")
      res <- basicRequest
        .get(uri"http://localhost:${port}/todos")
        .response(asJsonAlways[List[Todo]])
        .send(backend)
      body <- ZIO.fromEither(res.body) // ZIO.from(res.body)
    } yield body
}

object TestDriver {
  def hello: ZIO[TestDriver, Throwable, String] =
    ZIO.serviceWithZIO[TestDriver](_.hello)

  def list: ZIO[TestDriver, Throwable, List[Todo]] =
    ZIO.serviceWithZIO[TestDriver](_.list)

  lazy val layer: ZLayer[Server.Start, Throwable, TestDriver] =
    ZLayer {
      for {
        start <- ZIO.service[Server.Start]
        backend <- HttpClientZioBackend()
      } yield TestDriver(start.port, backend)
    }
}

object MainSpec extends ZIOSpec[EventLoopGroup with ServerChannelFactory] {

  override def bootstrap
      : ZLayer[Scope, Any, EventLoopGroup with ServerChannelFactory] =
    EventLoopGroup.auto(1) ++ ServerChannelFactory.auto

  val spec = suite("Main")(
    test("GET /hello returns 'hello'") {
      assertZIO(TestDriver.hello)(equalTo("hello"))
    },
    test("GET /todos returns todo list") {
      val expected = List(Todo(1, "title1"), Todo(2, "title2"))
      assertZIO(TestDriver.list)(equalTo(expected))
    }
  ).provideSome(
    TestDriver.layer,
    ZLayer {
      Server.app(Main.httpApp).withPort(0).make
    }
  )
}
