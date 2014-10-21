package heuriqo.hsbctest.bot

/**
 * (Enum) Classificationn of possible endings of the game.
 */
sealed class GameResult {
}

object GameResult {
  //sucessfull ending, all questions accepted by the server and "game over" marker was reached
  case object ALL_QUESTIONS_ANSWERED_OK extends GameResult
  //at some poinit server refused the answer which stopped the game
  case object LAST_ANSWER_FAILED extends GameResult
  //some problem happened (communication with server failed, server violated game protocol, number of questions was exceeded, ..) and the game was prematurely stopped
  case object INTERRUPTED extends GameResult
}
