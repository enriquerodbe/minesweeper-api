package model

import board.model.{Board, BoardConfiguration, Coordinates}
import org.scalacheck.Gen

object Generators {

  def boardConfigGen(maxSize: Int = 20): Gen[BoardConfiguration] =
    for {
      rows <- Gen.choose(2, maxSize)
      columns <- Gen.choose(2, maxSize)
      mines <- Gen.choose(1, rows*columns)
    } yield BoardConfiguration(rows, columns, mines)

  val coordinatesGen: Gen[(BoardConfiguration, Coordinates)] =
    for {
      boardConfig <- boardConfigGen()
      row <- Gen.choose(0, boardConfig.lastRowIndex)
      column <- Gen.choose(0, boardConfig.lastColumnIndex)
    } yield (boardConfig, Coordinates(row, column))

  val gameGen: Gen[Board] = {
    for {
      boardConfig <- boardConfigGen()
      numMoves <- Gen.choose(1, boardConfig.size)
      rows <- Gen.listOfN(numMoves, Gen.choose(0, boardConfig.lastRowIndex))
      columns <- Gen.listOfN(numMoves, Gen.choose(0, boardConfig.lastColumnIndex))
      operations <- Gen.listOfN(numMoves, Gen.choose(0, 3))
    } yield {
      val coordinates = rows.zip(columns).map(Coordinates.tupled).zip(operations)
      val initialBoard = boardConfig.generateRandomBoard()
      coordinates.foldLeft(initialBoard)(playerMove)
    }
  }

  def playerMove(board: Board, move: (Coordinates, Int)): Board = move._2 match {
    case 0 => board.revealed(move._1)
    case 1 => board.redFlagged(move._1)
    case 2 => board.questionMarked(move._1)
    case 3 => board.flagCleared(move._1)
  }
}
