package board.model

abstract class BoardException(msg: String) extends Exception(msg)

object BoardException {

  case class BoardSizeTooSmall(requestedSize: Int, minSize: Int)
    extends BoardException(s"Requested board size $requestedSize is too small. Minimum is $minSize.")

  case class BoardSizeTooBig(requestedSize: Int, maxSize: Int)
    extends BoardException(s"Requested board size $requestedSize is too big. Maximum is $maxSize.")

  case class BoardTooFewMines(requested: Int, min: Int)
    extends BoardException(s"Requested number of mines $requested is too small. Minimum is $min.")

  case class BoardTooManyMines(requested: Int, max: Int)
    extends BoardException(s"Requested number of mines $requested is too big. Maximum is $max.")

  case class PlayerMoveOutOfBounds(coordinates: Coordinates, configuration: BoardConfiguration)
    extends BoardException(
      s"Move $coordinates out of bounds. " +
      s"Coordinates must be >= 0. " +
      s"Rows must be <= ${configuration.lastRowIndex}. " +
      s"Columns must be <= ${configuration.lastColumnIndex}."
    )

  case class BoardUidNotFound(boardUid: String)
    extends BoardException(s"Board UID $boardUid not found.")
}
