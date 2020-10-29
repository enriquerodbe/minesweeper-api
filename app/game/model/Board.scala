package game.model

import game.model.BoardException.PlayerMoveOutOfBounds
import java.time.temporal.ChronoUnit
import java.time.{Duration, Instant}
import scala.util.{Failure, Success, Try}

case class Board(
    uid: String,
    configuration: BoardConfiguration,
    cells: IndexedSeq[IndexedSeq[Cell]],
    isActive: Boolean = true,
    currentTimeSpent: Duration = Duration.ZERO,
    resumedAt: Instant = Instant.now()) {

  def readCell(coordinates: Coordinates): Cell = cells(coordinates.row)(coordinates.column)

  def revealed(coordinates: Coordinates): Board = {
    val cell = readCell(coordinates)
    if (cell.isRevealed || cell.isRedFlagged || isGameOver) this
    else revealedRecursive(coordinates)
  }

  private def revealedRecursive(coordinates: Coordinates) = {
    val coordinatesRevealed = updated(coordinates, _.revealed())
    val cell = readCell(coordinates)
    if (cell.adjacentMines == 0) {
      val neighbours = configuration.getNeighbourCoordinates(coordinates)
      neighbours.foldLeft(coordinatesRevealed)(_ revealed _)
    }
    else coordinatesRevealed
  }

  def redFlagged(coordinates: Coordinates): Board = {
    val cell = readCell(coordinates)
    if (cell.isRevealed || isGameOver) this
    else updated(coordinates, _.redFlagged())
  }

  def questionMarked(coordinates: Coordinates): Board = {
    val cell = readCell(coordinates)
    if (cell.isRevealed || isGameOver) this
    else updated(coordinates, _.questionMarked())
  }

  def flagCleared(coordinates: Coordinates): Board = {
    val cell = readCell(coordinates)
    if (cell.isRevealed || isGameOver) this
    else updated(coordinates, _.flagCleared())
  }

  private def updated(coordinates: Coordinates, update: Cell => Cell): Board = {
    val Coordinates(row, column) = coordinates
    val updatedCell = update(readCell(coordinates))
    val updatedColumn = cells(row).updated(column, updatedCell)
    val updatedCells = cells.updated(row, updatedColumn)
    val board = copy(cells = updatedCells)
    updateAfterMove(board)
  }

  private def updateAfterMove(board: Board): Board = {
    if (board.isGameOver) board.activeSet(false)
    else if (!isActive) board.activeSet(true)
    else board
  }

  def activeSet(active: Boolean): Board = {
    if (isActive == active || active && isGameOver) this
    else {
      if (active) copy(isActive = true, resumedAt = Instant.now())
      else copy(isActive = false, currentTimeSpent = timeSpentUntilNow())
    }
  }

  def timeSpent(): Duration = if (isActive) timeSpentUntilNow() else currentTimeSpent

  private def timeSpentUntilNow(): Duration = {
    val difference = resumedAt.until(Instant.now(), ChronoUnit.SECONDS)
    currentTimeSpent.plus(difference, ChronoUnit.SECONDS)
  }

  def isExploded: Boolean = cells.exists(_.exists(_.isExploded))

  def isFinished: Boolean = cells.forall(_.forall(_.isCorrect))

  def isGameOver: Boolean = isExploded || isFinished

  def stringRepresentation: String = {
    cells.map(_.map(_.toCodeString).mkString).mkString("\n")
  }

  def tryMakeMove(playerMove: PlayerMove): Try[Board] = {
    if (areCoordinatesOutOfBounds(playerMove.coordinates)) {
      Failure(PlayerMoveOutOfBounds(playerMove.coordinates, configuration))
    } else {
      Success(makeMove(playerMove))
    }
  }

  private def areCoordinatesOutOfBounds(coordinates: Coordinates): Boolean = {
    val Coordinates(row, column) = coordinates
    row < 0 || row > configuration.lastRowIndex ||
    column < 0 || column > configuration.lastColumnIndex
  }

  private def makeMove(playerMove: PlayerMove): Board = playerMove.moveType match {
    case PlayerMoveType.Reveal => revealed(playerMove.coordinates)
    case PlayerMoveType.RedFlag => redFlagged(playerMove.coordinates)
    case PlayerMoveType.QuestionMark => questionMarked(playerMove.coordinates)
    case PlayerMoveType.ClearFlag => flagCleared(playerMove.coordinates)
  }

  def getNeighbours(coordinates: Coordinates): Seq[Cell] = {
    configuration.getNeighbourCoordinates(coordinates).toSeq.map(readCell)
  }
}
