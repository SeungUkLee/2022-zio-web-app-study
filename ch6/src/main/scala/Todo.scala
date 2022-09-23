import zio.json.{DeriveJsonCodec, JsonCodec}

final case class Todo(id: Int, title: String)

object Todo {
  implicit val TodoCodec: JsonCodec[Todo] =
    DeriveJsonCodec.gen
}
