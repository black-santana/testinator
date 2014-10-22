package heuriqo.hsbctest.bot

import heuriqo.hsbctest.client.{AnswerResponse, TestinatorClient, TestinatorSession}
import heuriqo.hsbctest.config.GameConfig
import heuriqo.hsbctest.model.GameQuestion
import heuriqo.hsbctest.questionsParser.QuestionsParser
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success}

/**
 * Encapsulates the logic of gameplay.
 * Keeps the state of one game.
 * Caution: this is actually a finite state machine.
 */
class Game(client: TestinatorClient, questionsParser: QuestionsParser, config: GameConfig) {
  val gameId = Game.generateNextGameId()
  private val log = LoggerFactory.getLogger("game-" + gameId)

  var questionsReceivedSoFar = 0
  var answersSentSoFar = 0
  var currentState: State = State.NewGame
  var session: Option[TestinatorSession] = None

  //the main loop of state machine transitions
  def runTheGame(): GameResult = {
    log.info("starting game " + gameId)
    var currentEvent: Event = Event.GameStarted(config.serverHost, config.serverPort, config.userAlias)

    while (! currentState.isFinal) {
      log.debug(s"(fsm) entering state $currentState with event $currentEvent")
      if (! currentState.processEvents.isDefinedAt(currentEvent))
        throw new Exception(s"(fsm) event $currentEvent was not supported in state $currentState")
      val (nextState, nextEvent): (State, Event) = currentState.processEvents(currentEvent)
      currentState = nextState
      currentEvent = nextEvent
    }

    this.translateFinalStateToGameResult(currentState)
  }

  //translates states of the FSM to game result as seen in the bot API (much simpler classification, hiding details why the game failed this time)
  private def translateFinalStateToGameResult(state: State): GameResult = state match {
    case State.GotGameOverMarker => GameResult.ALL_QUESTIONS_ANSWERED_OK
    case State.AnswerRefused => GameResult.LAST_ANSWER_FAILED
    case _ => GameResult.INTERRUPTED
  }

//oooooooooooooooooooooooooooooooooooooooo STATES OF THE MACHINE ooooooooooooooooooooooooooooooooooooooooooo
  
  sealed abstract class State(val isFinal: Boolean) {
    def processEvents: PartialFunction[Event, (State, Event)] = {
      case default => throw new Exception("processing not supported in state: " + this)
    }
  }

  object State {

    case object NewGame extends State(false) {
      override def processEvents: PartialFunction[Event, (State, Event)] = {
        case Event.GameStarted(host, port, userAlias) =>
          log.info("trying to connect to server: " + host + ":" + port)
          client.connectTo(host, port, userAlias) match {
            case Success(newSession) =>
              session = Some(newSession)
              log.info("succesfully connected to server, session token is: " + session.get.token)
              (State.ReadyToProcessNextQuestion, Event.ConnectedToServer)
            case Failure(ex) =>
              log.error("not able to connect to server", ex)
              (State.CommunicationProblemDuringConnecting, Event.Error(ex))
          }
      }
    }

    case object ReadyToProcessNextQuestion extends State(false) {
      override def processEvents: PartialFunction[Event, (State, Event)] = {
        case Event.ConnectedToServer | Event.StartNextRound =>
          session.get.askNextQuestion() match {
            case Success(rawQuestion) =>
              log.info("got question: " + rawQuestion)
              questionsReceivedSoFar += 1
              (State.HaveNextRawQuestion, Event.GotRawQuestion(rawQuestion))
            case Failure(ex) =>
              log.error("error while getting next question from server", ex)
              (State.CommunicationProblemDuringGettingNextQuestion, Event.Error(ex))
          }
      }
    }

    case object HaveNextRawQuestion extends State(false) {
      override def processEvents: PartialFunction[Event, (State, Event)] = {
        case Event.GotRawQuestion(question) =>
          questionsParser.parse(question) match {
            case Success(parsedQuestion) =>
              if (parsedQuestion.isGameOverMarker) {
                log.debug(s"game finished with final success after sending $answersSentSoFar answers")
                (State.GotGameOverMarker, Event.GameStopCondition)
              } else {
                log.debug("successfully recognized this question as: " + question)
                (State.HaveParsedQuestion, Event.QuestionParsingWasOk(parsedQuestion))
              }
            case Failure(ex) =>
              log.error("interrupting the game because of malformed question: " + question)
              (State.QuestionWasMalformed, Event.Error(ex))
          }
      }
    }

    case object HaveParsedQuestion extends State(false) {
      override def processEvents: PartialFunction[Event, (State, Event)] = {
        case Event.QuestionParsingWasOk(question) =>
          val answer = question.generateAnswer.text
          log.debug("sending answer: " + answer)
          session.get.sendAnswer(answer) match {
            case Success(AnswerResponse.PASS) =>
              answersSentSoFar += 1
              log.debug("server accepted this answer")
              (State.AnswerAcceptedByServer, Event.PassedAnswerValidation)
            case Success(AnswerResponse.FAIL) =>
              answersSentSoFar += 1
              log.debug("server refused this answer, interrupting the game now")
              (State.AnswerRefused, Event.GameStopCondition)
            case Failure(ex) =>
              log.error("error while sending answer to server", ex)
              (State.CommunicationProblemDuringAnswerSending, Event.Error(ex))
          }
      }
    }

    case object AnswerAcceptedByServer extends State(false) {
      override def processEvents: PartialFunction[Event, (State, Event)] = {
        case Event.PassedAnswerValidation =>
          if (questionsReceivedSoFar == config.questionsLimit) {
            log.debug(s"questions limit reached (${config.questionsLimit}), interrupting the game now")
            (State.QuestionsLimitReached, Event.GameStopCondition)
          } else
            (State.ReadyToProcessNextQuestion, Event.StartNextRound)
      }
    }

    case object QuestionsLimitReached extends State(true)
    case object CommunicationProblemDuringConnecting extends State(true)
    case object CommunicationProblemDuringGettingNextQuestion extends State(true)
    case object QuestionWasMalformed extends State(true)
    case object GotGameOverMarker extends State(true)
    case object CommunicationProblemDuringAnswerSending extends State(true)
    case object AnswerRefused extends State(true)
  }

//ooooooooooooooooooooooooooooooooooooo EVENTS OF THE MACHINE oooooooooooooooooooooooooooooooooooooooooooooooo
  
  sealed abstract class Event {
  }

  object Event {
    case class GameStarted(host: String, port: Int, userAlias: String) extends Event
    case object ConnectedToServer extends Event
    case class Error(ex: Throwable) extends Event
    case object GameStopCondition extends Event
    case class GotRawQuestion(question: String) extends Event
    case class QuestionParsingWasOk(question: GameQuestion) extends Event
    case object PassedAnswerValidation extends Event
    case object StartNextRound extends Event
  }

}

object Game {
  @volatile
  private var lastGameId = 0

  def generateNextGameId(): Int = {
    lastGameId += 1
    lastGameId
  }
}
