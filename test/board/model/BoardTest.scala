package board.model

import board.model.Generators.{boardConfigGen, gameGen}
import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalacheck.Properties

class BoardTest extends Properties("Board") {

  private def cheat(initialBoard: Board): Board = {
    initialBoard.cells.flatten.foldLeft(initialBoard) { case (board, cell) =>
      if (cell.hasMine) board.redFlagged(cell.coordinates)
      else board.revealed(cell.coordinates)
    }
  }

  property("explodes") = {
    forAll(gameGen) { board =>
      board.cells.exists(_.exists(_.isExploded)) ==> board.isExploded
    }
  }

  property("wins") = {
    forAll(boardConfigGen()) { boardConfig =>
      val board = cheat(boardConfig.generateRandomBoard())
      board.cells.forall(_.forall(_.isCorrect)) && board.isFinished
    }
  }

  property("game over") = {
    forAll(gameGen) { board =>
      board.isExploded || board.isFinished ==> board.isGameOver
    }
  }
}
