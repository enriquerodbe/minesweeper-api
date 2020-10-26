package board.dto

import board.dto.PlayerMoveType.PlayerMoveType

case class PlayerMove(moveType: PlayerMoveType, coordinates: Coordinates)
