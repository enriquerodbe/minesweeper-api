package game

import game.model.PlayerMoveType.PlayerMoveType
import game.model._
import play.api.libs.functional.syntax._
import play.api.libs.json._

object BoardSerializers {

  implicit val boardConfigurationReads: Reads[BoardConfiguration] = (
    (__ \ "rows").read[Int] and
    (__ \ "columns").read[Int] and
    (__ \ "mines").read[Int]
  )(BoardConfiguration.apply _)

  val boardSummaryWrites: Writes[Board] = Writes { board =>
    Json.obj(
      "uid" -> board.uid.value,
      "rows" -> board.configuration.numRows,
      "columns" -> board.configuration.numColumns,
      "mines" -> board.configuration.numMines,
      "isActive" -> board.isActive,
      "isGameOver" -> board.isGameOver,
      "timeSpent" -> board.timeSpent()
    )
  }

  val boardDetailsWrites: Writes[Board] = Writes { board =>
    Json.obj(
      "cells" -> board.cells.map(_.map(_.toCodeNumber)),
      "stringRepresentation" -> board.stringRepresentation
    )
  }

  implicit val playerMoveTypeReads: Reads[PlayerMoveType] = Reads {
    case JsString(value) => JsSuccess(PlayerMoveType.withName(value))
    case other => JsError(s"JsString expected for PlayerMoveType, got $other")
  }

  implicit val coordinatesReads: Reads[Coordinates] = Json.reads[Coordinates]
  implicit val playerMoveReads: Reads[PlayerMove] = Json.reads[PlayerMove]
}
