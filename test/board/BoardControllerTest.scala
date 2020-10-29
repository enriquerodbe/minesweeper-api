package board

import board.model.BoardException._
import board.model._
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.JsValue
import play.api.test.FakeRequest
import play.api.test.Helpers._

class BoardControllerTest extends PlaySpec with GuiceOneAppPerSuite {

  val controller = app.injector.instanceOf[BoardController]

  "BoardController" must {
    "create a new board" in {
      val request = FakeRequest().withBody(BoardConfiguration(10, 11, 12))

      val result = controller.create()(request)

      status(result) mustEqual CREATED
      val jsonResult = contentAsJson(result)
      (jsonResult \ "uid").as[String] must have size 6
      (jsonResult \ "rows").as[Int] mustEqual 10
      (jsonResult \ "columns").as[Int] mustEqual 11
      (jsonResult \ "mines").as[Int] mustEqual 12
      (jsonResult \ "status").as[String] mustEqual "playing"
      (jsonResult \ "isGameOver").as[Boolean] mustEqual false
    }

    "reject invalid config" when {
      "size is too big" in {
        val request = FakeRequest().withBody(BoardConfiguration(21, 32, 2))

        val result = controller.create()(request)

        intercept[BoardSizeTooBig](status(result))
      }
      "size is too small" in {
        val request = FakeRequest().withBody(BoardConfiguration(2, 1, 1))

        val result = controller.create()(request)

        intercept[BoardSizeTooSmall](status(result))
      }
      "too few mines" in {
        val request = FakeRequest().withBody(BoardConfiguration(10, 10, 0))

        val result = controller.create()(request)

        intercept[BoardTooFewMines](status(result))
      }
      "too many mines" in {
        val request = FakeRequest().withBody(BoardConfiguration(10, 10, 100))

        val result = controller.create()(request)

        intercept[BoardTooManyMines](status(result))
      }
    }

    "retrieve all boards" in {
      val result = controller.retrieveAll()(FakeRequest())

      status(result) mustEqual OK
      val jsonResult = contentAsJson(result).as[Seq[JsValue]]
      jsonResult must have size 1
      (jsonResult.head \ "uid").as[String] must have size 6
      (jsonResult.head \ "rows").as[Int] mustEqual 10
      (jsonResult.head \ "columns").as[Int] mustEqual 11
      (jsonResult.head \ "mines").as[Int] mustEqual 12
      (jsonResult.head \ "status").as[String] mustEqual "playing"
      (jsonResult.head \ "isGameOver").as[Boolean] mustEqual false
    }

    "retrieve one board details" in {
      val createdUid = createBoard()

      val result = controller.retrieve(createdUid)(FakeRequest())

      status(result) mustEqual OK
      val jsonResult = contentAsJson(result)
      val cells = (jsonResult \ "cells").as[Seq[Seq[Int]]]
      cells must have size 10
      all(cells) must have size 10
    }

    "reveal cell" in {
      val createdUid = createBoard()
      val move = PlayerMove(PlayerMoveType.Reveal, Coordinates(1, 1))

      val result = controller.move(createdUid)(FakeRequest().withBody(move))

      status(result) mustEqual OK
      val jsonResult = contentAsJson(result)
      val cells = (jsonResult \ "cells").as[Seq[Seq[Int]]]
      cells(1)(1) must not equal Cell.CodeCovered
    }

    "red-flag cell" in {
      val createdUid = createBoard()
      val move = PlayerMove(PlayerMoveType.RedFlag, Coordinates(1, 1))

      val result = controller.move(createdUid)(FakeRequest().withBody(move))

      status(result) mustEqual OK
      val jsonResult = contentAsJson(result)
      val cells = (jsonResult \ "cells").as[Seq[Seq[Int]]]
      cells(1)(1) mustEqual Cell.CodeRedFlagged
    }

    "question-mark cell" in {
      val createdUid = createBoard()
      val move = PlayerMove(PlayerMoveType.QuestionMark, Coordinates(1, 1))

      val result = controller.move(createdUid)(FakeRequest().withBody(move))

      status(result) mustEqual OK
      val jsonResult = contentAsJson(result)
      val cells = (jsonResult \ "cells").as[Seq[Seq[Int]]]
      cells(1)(1) mustEqual Cell.CodeQuestionMarked
    }

    "clear cell flag" in {
      val createdUid = createBoard()
      val redFlag = PlayerMove(PlayerMoveType.RedFlag, Coordinates(1, 1))
      val clearFlag = PlayerMove(PlayerMoveType.ClearFlag, Coordinates(1, 1))

      val resultRedFlag = controller.move(createdUid)(FakeRequest().withBody(redFlag))
      status(resultRedFlag) mustEqual OK

      val resultClearFlag = controller.move(createdUid)(FakeRequest().withBody(clearFlag))

      status(resultClearFlag) mustEqual OK
      val jsonResult = contentAsJson(resultClearFlag)
      val cells = (jsonResult \ "cells").as[Seq[Seq[Int]]]
      cells(1)(1) mustEqual Cell.CodeCovered
    }

    "set paused status" in {
      val createdUid = createBoard()

      val result = controller.changeStatus(createdUid)(FakeRequest().withBody(BoardStatus.Paused))

      status(result) mustEqual OK
      val jsonResult = contentAsJson(result)
      (jsonResult \ "status").as[String] mustEqual "paused"
    }

    "set playing status" in {
      val createdUid = createBoard()

      val result = controller.changeStatus(createdUid)(FakeRequest().withBody(BoardStatus.Paused))
      status(result) mustEqual OK

      val playingRequest = FakeRequest().withBody(BoardStatus.Playing)
      val resultPlaying = controller.changeStatus(createdUid)(playingRequest)

      status(resultPlaying) mustEqual OK
      val jsonResult = contentAsJson(resultPlaying)
      (jsonResult \ "status").as[String] mustEqual "playing"
    }

    "reject invalid move" when {
      "row is less than 0" in {
        val createdUid = createBoard()
        val move = PlayerMove(PlayerMoveType.QuestionMark, Coordinates(-1, 1))

        intercept[PlayerMoveOutOfBounds] {
          status(controller.move(createdUid)(FakeRequest().withBody(move)))
        }
      }
      "column is less than 0" in {
        val createdUid = createBoard()
        val move = PlayerMove(PlayerMoveType.QuestionMark, Coordinates(1, -1))

        intercept[PlayerMoveOutOfBounds] {
          status(controller.move(createdUid)(FakeRequest().withBody(move)))
        }
      }
      "row is out of bound" in {
        val createdUid = createBoard()
        val move = PlayerMove(PlayerMoveType.QuestionMark, Coordinates(1, 11))

        intercept[PlayerMoveOutOfBounds] {
          status(controller.move(createdUid)(FakeRequest().withBody(move)))
        }
      }
      "column is out of bound" in {
        val createdUid = createBoard()
        val move = PlayerMove(PlayerMoveType.QuestionMark, Coordinates(12, 1))

        intercept[PlayerMoveOutOfBounds] {
          status(controller.move(createdUid)(FakeRequest().withBody(move)))
        }
      }
    }
  }

  private def createBoard() = {
    val requestBody = FakeRequest().withBody(BoardConfiguration(10, 10, 10))
    (contentAsJson(controller.create()(requestBody)) \ "uid").as[String]
  }
}
