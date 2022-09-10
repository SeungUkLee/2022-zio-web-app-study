import zhttp.service.{EventLoopGroup, ServerChannelFactory}
import zio._
import zhttp.http._
import zhttp.service.Server
import zhttp.service.server.ServerChannelFactory

/* Practice ZLayer example */

class Repo(ref: Ref[Int])
object Repo {
  val layer: ZLayer[Any, Nothing, Repo] =
    ZLayer {
      for {
        ref <- Ref.make(0)
      } yield new Repo(ref)
    }
}

class ExternalService {
  def callService: Task[Unit] = ???
}

object ExternalService {
  val layer: ZLayer[Any, Nothing, ExternalService] =
    ZLayer.succeed(new ExternalService)
}

class App3(userRepo: Repo, externalService: ExternalService) {
  def run =
    Console.printLine("Hello")
}

object App3 {
  val layer: ZLayer[ExternalService with Repo, Nothing, App3] =
    ZLayer {
      for {
        userRepo <- ZIO.service[Repo]
        es <- ZIO.service[ExternalService]
      } yield new App3(userRepo, es)
    }
}

object ZLayerExample extends ZIOAppDefault {
  // 1. Horizontal Composition
  val depsLayer: ZLayer[Any, Nothing, Repo with ExternalService] =
    Repo.layer ++ ExternalService.layer

  val app: HttpApp[Any, Throwable] = ???
  val makeServer: ZIO[
    Any with EventLoopGroup with ServerChannelFactory with Scope,
    Throwable,
    Server.Start
  ] = Server.make(Server.app(app).withPort(8090))

  val serverLayer: ZLayer[
    ServerChannelFactory with EventLoopGroup,
    Throwable,
    Server.Start
  ] = ZLayer.scoped(makeServer)

  val run =
    serverLayer.launch.provideSome(
      ServerChannelFactory.auto,
      EventLoopGroup.auto(1)
    )
}
