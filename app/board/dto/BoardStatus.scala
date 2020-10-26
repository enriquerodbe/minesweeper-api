package board.dto

object BoardStatus extends Enumeration {
  type BoardStatus = Value

  val Playing = Value("playing")
  val Paused = Value("paused")
}
