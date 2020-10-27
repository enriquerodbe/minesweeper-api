package board.model

import board.model.CellStatus.CellStatus

case class Cell(
    coordinates: Coordinates,
    hasMine: Boolean,
    adjacentMines: Int,
    status: CellStatus) {

  def revealed(): Cell = copy(status = CellStatus.Revealed)

  def redFlagged(): Cell = copy(status = CellStatus.RedFlag)

  def questionMarked(): Cell = copy(status = CellStatus.QuestionMark)

  def flagCleared(): Cell = copy(status = CellStatus.Covered)

  def isRedFlagged: Boolean = status == CellStatus.RedFlag

  def isFlaggedCorrectly: Boolean = hasMine && isRedFlagged

  def isRevealed: Boolean = status == CellStatus.Revealed

  def isCorrect: Boolean = isFlaggedCorrectly || isRevealed

  def isExploded: Boolean = hasMine && isRevealed
}
