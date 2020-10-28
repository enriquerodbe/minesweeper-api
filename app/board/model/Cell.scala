package board.model

import board.model.CellStatus.CellStatus

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
    if (isExploded) -1
    else if (isRevealed) adjacentMines // 0 to 8
    else if (isRedFlagged) 9
    else if (isQuestionMarked) 10
    else 11 // covered
  }

  def toCodeString: String = toCodeNumber match {
    case -1 => "💥"
    case 0 => "⬜️"
    case 1 => "1️⃣"
    case 2 => "2️⃣"
    case 3 => "3️⃣"
    case 4 => "4️⃣"
    case 5 => "5️⃣"
    case 6 => "6️⃣"
    case 7 => "7️⃣"
    case 8 => "8️⃣"
    case 9 => "🚩"
    case 10 => "❓"
    case 11 => "⬛️"
  }
}
