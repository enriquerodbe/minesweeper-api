package game.model

import game.model.Generators.{boardConfigGen, boardGen, gameGen}
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

  property("move resumes game") = {
    forAll(boardGen()) { board =>
      board.activeSet(false).redFlagged(Coordinates(0, 0)).isActive
    }
  }

  property("game over preserves game") = {
    forAll(gameGen) { board =>
      board.isGameOver ==> !board.isActive
    }
  }

  property("can't resume a game that is over") = {
    forAll(gameGen) { board =>
      board.isGameOver ==> !board.activeSet(true).isActive
    }
  }

  property("can't reveal red-flagged cell") = {
    forAll(gameGen) { board =>
      board.cells.flatten.filter(_.isRedFlagged).forall(!_.isRevealed)
    }
  }

  property("can't red-flag revealed cell") = {
    forAll(gameGen) { board =>
      board.cells.flatten.filter(_.isRevealed).forall(!_.isRedFlagged)
    }
  }
}
