package game.model

object BoardStatus extends Enumeration {
  type BoardStatus = Value

  val Active = Value("active")
  val Preserved = Value("preserved")
}
