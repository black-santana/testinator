package heuriqo.hsbctest.questionsParser

import heuriqo.hsbctest.model._

import scala.util.{Failure, Success, Try}

/**
 * Trivial questions parser.
 * For simplicity use regexp pattern matching as parsing approach.
 */
class QuestionsParserImpl extends QuestionsParser {

  val intNumber: String = """(-?\d+)"""
  val additionPattern = ("What is " + intNumber + " plus " + intNumber + "\\?").r
  val substractionPattern = ("What is " + intNumber + " minus " + intNumber + "\\?").r
  val multiplicationPattern = ("What is " + intNumber + " multiplied by " + intNumber + "\\?").r
  val finishPattern = """You have finished""".r


  @throws[MalformedQuestion]
  override def parse(question: String): Try[GameQuestion] = {
    if (question.trim.isEmpty)
      Failure(new MalformedQuestion("Empty question"))

    question match {
      case additionPattern(left, right) => Success(AdditionQuestion(left.toInt, right.toInt))
      case substractionPattern(left, right) => Success(SubstractionQuestion(left.toInt, right.toInt))
      case multiplicationPattern(left, right) => Success(MultiplicationQuestion(left.toInt, right.toInt))
      case finishPattern() => Success(GameOverMarker)
      case other => Failure(new MalformedQuestion("this question does not follow the game rules: " + question))
    }

  }
}
