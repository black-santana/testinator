package heuriqo.hsbctest.client

import scala.util.Try

/**
 * Abstraction of game server API for a running game (i.e. when the session token is already obrained).
 */
trait TestinatorSession {

  /**
   * Token of this session.
   * @return
   */
  def token: String

  /**
   * Sends a reguest for next question.
   * If successfull, returns the question as got from server (plain text).
   * Returns failure when communication with server fails (connectivity or protocol error).
   */
  def askNextQuestion(): Try[String]

  /**
   * Sends answer (= answer to the last question).
   * Returns classification of the response (passed or failed).
   * Returns failure when communication with server fails (connectivity or protocol error).
   *
   * @param answer the answer to be send to gama server (in plain text)
   * @return classification of server's response
   */
  def sendAnswer(answer: String): Try[AnswerResponse]
}
