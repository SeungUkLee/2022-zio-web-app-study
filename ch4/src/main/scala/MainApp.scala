import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server}
import zio.{ZIO, ZIOAppDefault}

object MainApp extends ZIOAppDefault {
  val program =
    ZIO.scoped {
      for {
        app <- TodoApp.httpApp
        start <- Server(app)
          .withBinding("localhost", 8090)
          .make
          .orDie
        _ <- ZIO.logInfo(s"Server started on port ${start.port}")
        _ <- ZIO.never
      } yield ()
    }

  def run =
    program.provide(
      TodoApp.layer,
      TodoService.layer,
      TodoRepoInMemory.layer,
      ServerChannelFactory.auto,
      EventLoopGroup.auto(1)
    )
}
