package board.model

import board.model.PlayerMoveType.PlayerMoveType

case class PlayerMove(moveType: PlayerMoveType, coordinates: Coordinates)
