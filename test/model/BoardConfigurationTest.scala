package model

import board.model.Coordinates
import model.Generators._
import org.scalacheck.Prop.{all, forAll, propBoolean}
import org.scalacheck.Properties

class BoardConfigurationTest extends Properties("BoardConfiguration") {

  property("correct number of neighbours") = {
    forAll(coordinatesGen) { case (boardConfig, coordinates) =>
      val neighbours = boardConfig.getNeighbourCoordinates(coordinates)
      (neighbours.size >= 3) :| "min 3 neighbour" &&
      (neighbours.size <= 8) :| "max 8 neighbours"
    }
  }

  property("neighbours inside board") = {
    forAll(coordinatesGen) { case (boardConfig, coordinates) =>
      val neighbours = boardConfig.getNeighbourCoordinates(coordinates)
      neighbours.forall { neighbour =>
        neighbour.row >= 0 && neighbour.row <= boardConfig.lastRowIndex &&
        neighbour.column >= 0 && neighbour.column <= boardConfig.lastColumnIndex
      }
    }
  }

  property("neighbours are adjacent") = {
    forAll(coordinatesGen) { case (boardConfig, coordinates) =>
      val neighbours = boardConfig.getNeighbourCoordinates(coordinates)
      neighbours.forall { neighbour =>
        math.abs(neighbour.row - coordinates.row) <= 1 &&
        math.abs(neighbour.column - coordinates.column) <= 1
      }
    }
  }

  property("generate correct size") = {
    forAll(boardConfigGen()) { boardConfig =>
      val board = boardConfig.generateRandomBoard()
      board.cells.size == boardConfig.numRows &&
        board.cells.forall(_.size == boardConfig.numColumns)
    }
  }

  property("generate correct number of mines") = {
    forAll(boardConfigGen()) { boardConfig =>
      val board = boardConfig.generateRandomBoard()
      val res = board.cells.map(_.count(_.hasMine)).sum
      s"config mines = ${boardConfig.numMines} | actual mines = $res" |:
        all(res == boardConfig.numMines)
    }
  }

  property("generate covered cells") = {
    forAll(boardConfigGen()) { boardConfig =>
      val board = boardConfig.generateRandomBoard()
      board.cells.forall(_.forall(!_.isRevealed))
    }
  }

  property("generate correct neighbour mines count") = {
    forAll(boardConfigGen()) { boardConfig =>
      val board = boardConfig.generateRandomBoard()

      val cells = for {
        (column, rowNum) <- board.cells.zipWithIndex
        (cell, colNum) <- column.zipWithIndex
      } yield {
        val coordinates = Coordinates(rowNum, colNum)
        (cell, board.getNeighbours(coordinates))
      }

      cells.forall { case (cell, neighbours) =>
        cell.adjacentMines == neighbours.count(_.hasMine)
      }
    }
  }
}
