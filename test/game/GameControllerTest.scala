package game

import game.model.BoardException._
import game.model._
import play.api.libs.json.JsValue
import play.api.test.Helpers._
import util.BaseControllerTest

class GameControllerTest extends BaseControllerTest {

  val controller = app.injector.instanceOf[GameController]

  "GameController" must {
    "create a new board" in {
      val result = controller.createBoard()(fakeRequest().withBody(BoardConfiguration(10, 11, 12)))

      status(result) mustEqual CREATED
      val jsonResult = contentAsJson(result)
      (jsonResult \ "uid").as[String] must have size 6
      (jsonResult \ "rows").as[Int] mustEqual 10
      (jsonResult \ "columns").as[Int] mustEqual 11
      (jsonResult \ "mines").as[Int] mustEqual 12
      (jsonResult \ "isActive").as[Boolean] mustEqual false
      (jsonResult \ "isGameOver").as[Boolean] mustEqual false
    }

    "reject invalid config" when {
      "size is too big" in {
        val request = fakeRequest().withBody(BoardConfiguration(21, 32, 2))

        val result = controller.createBoard()(request)

        intercept[BoardSizeTooBig](status(result))
      }
      "size is too small" in {
        val request = fakeRequest().withBody(BoardConfiguration(2, 1, 1))

        val result = controller.createBoard()(request)

        intercept[BoardSizeTooSmall](status(result))
      }
      "too few mines" in {
        val request = fakeRequest().withBody(BoardConfiguration(10, 10, 0))

        val result = controller.createBoard()(request)

        intercept[BoardTooFewMines](status(result))
      }
      "too many mines" in {
        val request = fakeRequest().withBody(BoardConfiguration(10, 10, 100))

        val result = controller.createBoard()(request)

        intercept[BoardTooManyMines](status(result))
      }
    }

    "retrieve all boards" in {
      val result = controller.retrieveAllBoards()(fakeRequest())

      status(result) mustEqual OK
      val jsonResult = contentAsJson(result).as[Seq[JsValue]]
      jsonResult must have size 1
      (jsonResult.head \ "uid").as[String] must have size 6
      (jsonResult.head \ "rows").as[Int] mustEqual 10
      (jsonResult.head \ "columns").as[Int] mustEqual 11
      (jsonResult.head \ "mines").as[Int] mustEqual 12
      (jsonResult.head \ "isActive").as[Boolean] mustEqual false
      (jsonResult.head \ "isGameOver").as[Boolean] mustEqual false
    }

    "retrieve one board details" in {
      val createdUid = createBoard()

      val result = controller.retrieveBoard(createdUid)(fakeRequest())

      status(result) mustEqual OK
      val jsonResult = contentAsJson(result)
      val cells = (jsonResult \ "cells").as[Seq[Seq[Int]]]
      cells must have size 10
      all(cells) must have size 10
    }

    "respond board not found" in {
      val result = controller.retrieveBoard("uid-not-to-be-found")(fakeRequest())

      intercept[BoardUidNotFound](status(result))
    }

    "reveal cell" in {
      val createdUid = createBoard()
      val move = PlayerMove(PlayerMoveType.Reveal, Coordinates(1, 1))

      val result = controller.move(createdUid)(fakeRequest().withBody(move))

      status(result) mustEqual OK
      val jsonResult = contentAsJson(result)
      val cells = (jsonResult \ "cells").as[Seq[Seq[Int]]]
      cells(1)(1) must not equal Cell.CodeCovered
    }

    "red-flag cell" in {
      val createdUid = createBoard()
      val move = PlayerMove(PlayerMoveType.RedFlag, Coordinates(1, 1))

      val result = controller.move(createdUid)(fakeRequest().withBody(move))

      status(result) mustEqual OK
      val jsonResult = contentAsJson(result)
      val cells = (jsonResult \ "cells").as[Seq[Seq[Int]]]
      cells(1)(1) mustEqual Cell.CodeRedFlagged
    }

    "question-mark cell" in {
      val createdUid = createBoard()
      val move = PlayerMove(PlayerMoveType.QuestionMark, Coordinates(1, 1))

      val result = controller.move(createdUid)(fakeRequest().withBody(move))

      status(result) mustEqual OK
      val jsonResult = contentAsJson(result)
      val cells = (jsonResult \ "cells").as[Seq[Seq[Int]]]
      cells(1)(1) mustEqual Cell.CodeQuestionMarked
    }

    "clear cell flag" in {
      val createdUid = createBoard()
      val redFlag = PlayerMove(PlayerMoveType.RedFlag, Coordinates(1, 1))
      val clearFlag = PlayerMove(PlayerMoveType.ClearFlag, Coordinates(1, 1))

      val resultRedFlag = controller.move(createdUid)(fakeRequest().withBody(redFlag))
      status(resultRedFlag) mustEqual OK

      val resultClearFlag = controller.move(createdUid)(fakeRequest().withBody(clearFlag))

      status(resultClearFlag) mustEqual OK
      val jsonResult = contentAsJson(resultClearFlag)
      val cells = (jsonResult \ "cells").as[Seq[Seq[Int]]]
      cells(1)(1) mustEqual Cell.CodeCovered
    }

    "set preserved status" in {
      val createdUid = createBoard()

      val result = controller.setBoardIsActive(createdUid)(fakeRequest().withBody(false))

      status(result) mustEqual OK
      val jsonResult = contentAsJson(result)
      (jsonResult \ "isActive").as[Boolean] mustEqual false
    }

    "set active status" in {
      val createdUid = createBoard()

      val result = controller.setBoardIsActive(createdUid)(fakeRequest().withBody(false))
      status(result) mustEqual OK

      val activeRequest = fakeRequest().withBody(true)
      val resultActive = controller.setBoardIsActive(createdUid)(activeRequest)

      status(resultActive) mustEqual OK
      val jsonResult = contentAsJson(resultActive)
      (jsonResult \ "isActive").as[Boolean] mustEqual true
    }

    "reject invalid move" when {
      "row is less than 0" in {
        val createdUid = createBoard()
        val move = PlayerMove(PlayerMoveType.QuestionMark, Coordinates(-1, 1))

        intercept[PlayerMoveOutOfBounds] {
          status(controller.move(createdUid)(fakeRequest().withBody(move)))
        }
      }
      "column is less than 0" in {
        val createdUid = createBoard()
        val move = PlayerMove(PlayerMoveType.QuestionMark, Coordinates(1, -1))

        intercept[PlayerMoveOutOfBounds] {
          status(controller.move(createdUid)(fakeRequest().withBody(move)))
        }
      }
      "row is out of bound" in {
        val createdUid = createBoard()
        val move = PlayerMove(PlayerMoveType.QuestionMark, Coordinates(1, 11))

        intercept[PlayerMoveOutOfBounds] {
          status(controller.move(createdUid)(fakeRequest().withBody(move)))
        }
      }
      "column is out of bound" in {
        val createdUid = createBoard()
        val move = PlayerMove(PlayerMoveType.QuestionMark, Coordinates(12, 1))

        intercept[PlayerMoveOutOfBounds] {
          status(controller.move(createdUid)(fakeRequest().withBody(move)))
        }
      }
    }
  }

  private def createBoard() = {
    val requestBody = fakeRequest().withBody(BoardConfiguration(10, 10, 10))
    (contentAsJson(controller.createBoard()(requestBody)) \ "uid").as[String]
  }
}
