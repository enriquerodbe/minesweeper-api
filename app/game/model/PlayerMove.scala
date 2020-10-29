package game.model

import game.model.PlayerMoveType.PlayerMoveType

case class PlayerMove(moveType: PlayerMoveType, coordinates: Coordinates)
