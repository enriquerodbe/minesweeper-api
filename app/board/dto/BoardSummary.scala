package board.dto

import board.model.BoardStatus.BoardStatus

case class BoardSummary(
    uid: String,
    rows: Int,
    columns: Int,
    mines: Int,
    status: BoardStatus,
    isGameOver: Boolean,
)
