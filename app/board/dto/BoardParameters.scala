package board.dto

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{Reads, _}

case class BoardParameters(rows: Int, columns: Int, mines: Int)

object BoardParameters {

  implicit val boardParametersReads: Reads[BoardParameters] = (
    (__ \ "rows").read[Int](min(2) andKeep max(20)) and
    (__ \ "columns").read[Int](min(2) andKeep max(20)) and
    (__ \ "mines").read[Int](min(1))
  )(BoardParameters.apply _)
}
