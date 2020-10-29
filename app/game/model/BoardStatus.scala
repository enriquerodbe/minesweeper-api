package game.model

import com.fasterxml.jackson.core.`type`.TypeReference

object BoardStatus extends Enumeration {
  type BoardStatus = Value

  val Active = Value("active")
  val Preserved = Value("preserved")
}

class BoardStatusType extends TypeReference[BoardStatus.type]
