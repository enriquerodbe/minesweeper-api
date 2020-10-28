package board.model

import scala.util.Random

case class BoardConfiguration(numRows: Int, numColumns: Int, numMines: Int) {

  val lastRowIndex: Int = numRows - 1
  val lastColumnIndex: Int = numColumns - 1
  val size: Int = numRows * numColumns

  def generateRandomBoard(): Board = {
    val mines = Seq.fill(numMines)(Random.nextInt(size)).toSet
    val cells = IndexedSeq.tabulate(numRows, numColumns)(generateCell(_, _, mines))
    new Board(generateUid(), this, cells, BoardStatus.Playing)
  }

  private def generateCell(row: Int, column: Int, mines: Set[Int]): Cell = {
    val coordinates = Coordinates(row, column)
    Cell(
      coordinates,
      hasMine(coordinates, mines),
      countAdjacentMines(coordinates, mines),
      CellStatus.Covered,
    )
  }

  private def generateUid(): String = Random.alphanumeric.take(6).mkString

  private def hasMine(coordinates: Coordinates, mines: Set[Int]): Boolean = {
    mines.contains(coordinates.row * numColumns + coordinates.column)
  }

  private def countAdjacentMines(coordinates: Coordinates, mines: Set[Int]): Int = {
    getNeighbourCoordinates(coordinates).count(hasMine(_, mines))
  }

  def getNeighbourCoordinates(coordinates: Coordinates): Set[Coordinates] = {
    val Coordinates(row, column) = coordinates
    Set(
      Option.when(row > 0 && column > 0)(Coordinates(row - 1, column - 1)),
      Option.when(row > 0)(Coordinates(row - 1, column)),
      Option.when(row > 0 && column < lastColumnIndex)(Coordinates(row - 1, column + 1)),
      Option.when(column > 0)(Coordinates(row, column - 1)),
      Option.when(column < lastColumnIndex)(Coordinates(row, column + 1)),
      Option.when(row < lastRowIndex && column > 0)(Coordinates(row + 1, column - 1)),
      Option.when(row < lastRowIndex)(Coordinates(row + 1, column)),
      Option.when(row < lastRowIndex && column < lastColumnIndex)(Coordinates(row + 1, column + 1)),
    ).flatten
  }
}
