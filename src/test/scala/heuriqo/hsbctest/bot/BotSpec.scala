package heuriqo.hsbctest.bot

import heuriqo.hsbctest.BaseSpec
import heuriqo.hsbctest.client._
import heuriqo.hsbctest.config.GameConfig
import heuriqo.hsbctest.model.{GameOverMarker, AdditionQuestion}
import heuriqo.hsbctest.questionsParser.{MalformedQuestion, QuestionsParser}

import scala.util.{Success, Failure}

class BotSpec extends BaseSpec {

  trait CommonSetup {
    val clientMock = mock[TestinatorClient]
    val sessionMock = mock[TestinatorSession]
    (sessionMock.token _).expects().returns("foo").anyNumberOfTimes()
    val questionsParserMock = mock[QuestionsParser]
    val bot = new BotImpl(clientMock, questionsParserMock)

    val oneRoundGameConfig = new GameConfig {
      override def serverHost: String = "foo.com"
      override def questionsLimit: Int = 1
      override def userAlias: String = "bob"
      override def serverPort: Int = 13
    }

    val twoRoundsGameConfig = new GameConfig {
      override def serverHost: String = "foo.com"
      override def questionsLimit: Int = 2
      override def userAlias: String = "bob"
      override def serverPort: Int = 13
    }

    val networkExeption = Failure(new ConnectionToServerFailed(new RuntimeException("fake exception for testing")))
    val fakeQuestion1 = "To be or not to be?"
  }

  "Bot" must "gracefully stop the game if connection to server can't be established" in new CommonSetup {
    (clientMock.connectTo _ ).expects(*,*,*).returns(networkExeption)

    val result = bot.runTheGame(oneRoundGameConfig)
    result mustEqual GameResult.INTERRUPTED
  }

  it must "gracefully stop the game when getting next game-question fails" in new CommonSetup {
    (clientMock.connectTo _ ).expects(*,*,*).returns(Success(sessionMock))
    (sessionMock.askNextQuestion _).expects().returns(networkExeption)

    val result = bot.runTheGame(oneRoundGameConfig)
    result mustEqual GameResult.INTERRUPTED
  }

  it must "gracefully stop the game when received game-question is malformed" in new CommonSetup {
    (clientMock.connectTo _ ).expects(*,*,*).returns(Success(sessionMock))
    (sessionMock.askNextQuestion _).expects().returns(Success(fakeQuestion1))
    (questionsParserMock.parse _).expects(fakeQuestion1).returns(Failure(new MalformedQuestion("just for testing")))

    val result = bot.runTheGame(oneRoundGameConfig)
    result mustEqual GameResult.INTERRUPTED
  }

  it must "gracefully stop the game when server refuses to accept a game-answer" in new CommonSetup {
    (clientMock.connectTo _ ).expects(*,*,*).returns(Success(sessionMock))
    (sessionMock.askNextQuestion _).expects().returns(Success(fakeQuestion1))
    (questionsParserMock.parse _).expects(fakeQuestion1).returns(Success(AdditionQuestion(1, 2)))
    (sessionMock.sendAnswer _).expects("3").returns(Success(AnswerResponse.FAIL))

    val result = bot.runTheGame(oneRoundGameConfig)
    result mustEqual GameResult.LAST_ANSWER_FAILED
  }

  it must "gracefully stop the game when server response to sending game-answer is malformed" in new CommonSetup {
    (clientMock.connectTo _ ).expects(*,*,*).returns(Success(sessionMock))
    (sessionMock.askNextQuestion _).expects().returns(Success(fakeQuestion1))
    (questionsParserMock.parse _).expects(fakeQuestion1).returns(Success(AdditionQuestion(1, 2)))
    (sessionMock.sendAnswer _).expects("3").returns(Failure(new ProtocolException("just for testing")))

    val result = bot.runTheGame(oneRoundGameConfig)
    result mustEqual GameResult.INTERRUPTED
  }

  it must "respect configured limit of game-questions to be processed" in new CommonSetup {
    (clientMock.connectTo _ ).expects(*,*,*).returns(Success(sessionMock))
    (sessionMock.askNextQuestion _).expects().returns(Success(fakeQuestion1))
    (questionsParserMock.parse _).expects(fakeQuestion1).returns(Success(AdditionQuestion(1, 2)))
    (sessionMock.sendAnswer _).expects("3").returns(Success(AnswerResponse.PASS))

    val result = bot.runTheGame(oneRoundGameConfig)
    result mustEqual GameResult.INTERRUPTED
  }

  it must "successfully orchestrate full successful game sequence" in new CommonSetup {
    (clientMock.connectTo _ ).expects(*,*,*).returns(Success(sessionMock))

    //first question
    (sessionMock.askNextQuestion _).expects().returns(Success(fakeQuestion1))
    (questionsParserMock.parse _).expects(fakeQuestion1).returns(Success(AdditionQuestion(1, 2)))
    (sessionMock.sendAnswer _).expects("3").returns(Success(AnswerResponse.PASS))

    //game over
    (sessionMock.askNextQuestion _).expects().returns(Success(fakeQuestion1))
    (questionsParserMock.parse _).expects(fakeQuestion1).returns(Success(GameOverMarker))

    val result = bot.runTheGame(twoRoundsGameConfig)
    result mustEqual GameResult.ALL_QUESTIONS_ANSWERED_OK
  }

}
