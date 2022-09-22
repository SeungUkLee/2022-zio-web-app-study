import zio.json.{DeriveJsonCodec, JsonCodec}

final case class Todo(id: Long, title: String)
object Todo {
  implicit val TodoCodec: JsonCodec[Todo] =
    DeriveJsonCodec.gen
}

final case class CreateTodoForm(title: String)
object CreateTodoForm {
  implicit val CreateTodoFormCodec: JsonCodec[CreateTodoForm] =
    DeriveJsonCodec.gen
}
