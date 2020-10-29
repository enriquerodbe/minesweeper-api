package game.model

import com.fasterxml.jackson.core.`type`.TypeReference

object CellStatus extends Enumeration {
  type CellStatus = Value

  val Covered = Value("covered")
  val Revealed = Value("revealed")
  val RedFlag = Value("redFlagged")
  val QuestionMark = Value("questionMarked")
}

class CellStatusType extends TypeReference[CellStatus.type]
