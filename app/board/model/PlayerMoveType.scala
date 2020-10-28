package board.model

object PlayerMoveType extends Enumeration {
  type PlayerMoveType = Value

  val Reveal = Value("reveal")
  val RedFlag = Value("redFlag")
  val QuestionMark = Value("questionMark")
  val ClearFlag = Value("clearFlag")
}
