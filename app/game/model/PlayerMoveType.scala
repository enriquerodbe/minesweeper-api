package game.model

import com.fasterxml.jackson.core.`type`.TypeReference

object PlayerMoveType extends Enumeration {
  type PlayerMoveType = Value

  val Reveal = Value("reveal")
  val RedFlag = Value("redFlag")
  val QuestionMark = Value("questionMark")
  val ClearFlag = Value("clearFlag")
}

class PlayerMoveTypeType extends TypeReference[PlayerMoveType.type]
