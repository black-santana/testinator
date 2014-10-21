package heuriqo.hsbctest.questionsParser

import heuriqo.hsbctest.BaseSpec
import heuriqo.hsbctest.model._

import scala.util.{Success, Failure}

class QuestionsParserSpec extends BaseSpec {

  trait CommonSetup {
    val parser = new QuestionsParserImpl
  }

  "Questions parser" must "understand addition" in new CommonSetup {
    parser.parse("What is 2 plus 2?") mustEqual Success(AdditionQuestion(2, 2))
    parser.parse("What is 0 plus 0?") mustEqual Success(AdditionQuestion(0, 0))
    parser.parse("What is -100 plus 000001?") mustEqual Success(AdditionQuestion(-100, 1))
  }

  it must "understand substraction" in new CommonSetup {
    parser.parse("What is 2 minus 2?") mustEqual Success(SubstractionQuestion(2, 2))
    parser.parse("What is 0 minus 0?") mustEqual Success(SubstractionQuestion(0, 0))
    parser.parse("What is -100 minus 000001?") mustEqual Success(SubstractionQuestion(-100, 1))
  }

  it must "understand multiplication" in new CommonSetup {
    parser.parse("What is 2 multiplied by 2?") mustEqual Success(MultiplicationQuestion(2, 2))
    parser.parse("What is 0 multiplied by 0?") mustEqual Success(MultiplicationQuestion(0, 0))
    parser.parse("What is -100 multiplied by 000001?") mustEqual Success(MultiplicationQuestion(-100, 1))
  }

  it must "understand <game over> marker" in new CommonSetup {
    parser.parse("You have finished") mustEqual Success(GameOverMarker)
  }

  it must "gracefully signal parsing error on empty string" in new CommonSetup {
    parser.parse("") mustBe a [Failure[_]]
    parser.parse(" ") mustBe a [Failure[_]]
  }

}
