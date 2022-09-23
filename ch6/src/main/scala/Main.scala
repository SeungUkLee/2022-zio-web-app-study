import zio._
import zhttp.http._
import zhttp.service.Server
import zio.json.EncoderOps

object Main extends ZIOAppDefault {
  val httpApp = Http.collectZIO[Request] {
    case Method.GET -> !! / "hello" =>
      ZIO.succeed(Response.text("hello"))
    case Method.GET -> !! / "todos" =>
      ZIO.succeed(Response.json(List(Todo(1, "title1"), Todo(2, "title2")).toJson))

  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    Server.start(8080, httpApp)
}
