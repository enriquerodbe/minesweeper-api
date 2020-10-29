package game.model

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import game.model.PlayerMoveType.PlayerMoveType

case class PlayerMove(
    @JsonScalaEnumeration(classOf[PlayerMoveTypeType]) moveType: PlayerMoveType,
    coordinates: Coordinates,
)
