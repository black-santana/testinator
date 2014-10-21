package heuriqo.hsbctest.questionsParser

import heuriqo.hsbctest.model.GameQuestion

import scala.util.Try

/**
 * Analyzer of plain text questions as received by the game server.
 */
trait QuestionsParser {

  /**
   * Analyzes plain text question.
   *
   * @param question question in plain text
   * @return question in parsed form (= instance of some subclass of Question) or failure is the question was empty or malformed (=syntax error)
   */
  def parse(question: String): Try[GameQuestion]

}
