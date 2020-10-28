package board.model

object PlayerMoveType extends Enumeration {
  type PlayerMoveType = Value

  val Reveal = Value("reveal")
  val RedFlag = Value("redFlag")
  val questionMark = Value("questionMark")
  val clearFlag = Value("clearFlag")
}
