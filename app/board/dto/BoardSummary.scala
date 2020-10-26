package board.dto

import board.dto.BoardStatus.BoardStatus

case class BoardSummary(
    uid: String,
    rows: Int,
    columns: Int,
    mines: Int,
    status: BoardStatus,
    isGameOver: Boolean,
)
