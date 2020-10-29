package game.model

object CellStatus extends Enumeration {
  type CellStatus = Value

  val Covered = Value("covered")
  val Revealed = Value("revealed")
  val RedFlag = Value("redFlagged")
  val QuestionMark = Value("questionMarked")
}
