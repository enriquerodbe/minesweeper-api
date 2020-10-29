package board

import board.model.BoardStatus.BoardStatus
import board.model.PlayerMoveType.PlayerMoveType
import board.model._
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
      "uid" -> board.uid,
      "rows" -> board.configuration.numRows,
      "columns" -> board.configuration.numColumns,
      "mines" -> board.configuration.numMines,
      "status" -> board.status.toString,
      "isGameOver" -> board.isGameOver,
    )
  }

  val bardSummarySeqWrites: Writes[Seq[Board]] = Writes { b =>
    JsArray(b.map(Json.toJson(_)(boardSummaryWrites)))
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

  implicit val boardStatusReads: Reads[BoardStatus] = Reads {
    case JsString(value) => JsSuccess(BoardStatus.withName(value))
    case other => JsError(s"JsString expected for BoardStatus, got $other")
  }
}
