import zhttp.http._
import zhttp.service.Server
import zio._
import zio.json._

import java.io.IOException

// web server를 만들어 보세요
// https://github.com/dream11/zio-http/blob/main/example/src/main/scala/example/HelloWorld.scala

final case class Todo(id: Long, title: String)
object Todo {
  implicit val TodoCodec: JsonCodec[Todo] = DeriveJsonCodec.gen
}

object App extends ZIOAppDefault {
  val app: HttpApp[Any, Nothing] = Http.collect[Request] {
    case Method.GET -> !! / "todo" / id =>
      id.toLongOption match {
        case Some(id) => Response.json(Todo(id, s"title$id").toJson)
        case None     => Response.status(Status.NotFound)
      }
    case Method.GET -> !! / "todos" => {
      val todos = List(Todo(1, "title1"), Todo(2, "title2"))
      Response.json(todos.toJson)
    }
  }

  val run = Server.start(8090, app)
}
