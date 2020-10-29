package game.model

import game.model.Cell._
import game.model.CellStatus.CellStatus

case class Cell(
    coordinates: Coordinates,
    hasMine: Boolean,
    adjacentMines: Int,
    status: CellStatus,
) {

  def revealed(): Cell = copy(status = CellStatus.Revealed)

  def redFlagged(): Cell = copy(status = CellStatus.RedFlag)

  def questionMarked(): Cell = copy(status = CellStatus.QuestionMark)

  def isQuestionMarked: Boolean = status == CellStatus.QuestionMark

  def flagCleared(): Cell = copy(status = CellStatus.Covered)

  def isRedFlagged: Boolean = status == CellStatus.RedFlag

  def isFlaggedCorrectly: Boolean = hasMine && isRedFlagged

  def isRevealed: Boolean = status == CellStatus.Revealed

  def isCorrect: Boolean = isFlaggedCorrectly || isRevealed

  def isExploded: Boolean = hasMine && isRevealed

  def toCodeNumber: Int = {
    if (isExploded) CodeExploded
    else if (isRevealed) adjacentMines // 0 to 8
    else if (isRedFlagged) CodeRedFlagged
    else if (isQuestionMarked) CodeQuestionMarked
    else CodeCovered
  }

  def toCodeString: String = toCodeNumber match {
    case CodeExploded => "💥"
    case 0 => "⬜️"
    case 1 => "1️⃣"
    case 2 => "2️⃣"
    case 3 => "3️⃣"
    case 4 => "4️⃣"
    case 5 => "5️⃣"
    case 6 => "6️⃣"
    case 7 => "7️⃣"
    case 8 => "8️⃣"
    case CodeRedFlagged => "🚩"
    case CodeQuestionMarked => "❓"
    case CodeCovered => "⬛️"
  }
}

object Cell {

  val CodeExploded = -1
  val CodeRedFlagged = 9
  val CodeQuestionMarked = 10
  val CodeCovered = 11
}
