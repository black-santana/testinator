package heuriqo.hsbctest

import heuriqo.hsbctest.bot.{Bot, BotImpl}
import heuriqo.hsbctest.client.{HttpClient, HttpClientImpl, TestinatorClient, TestinatorClientImpl}
import heuriqo.hsbctest.questionsParser.{QuestionsParser, QuestionsParserImpl}

/**
 * Components wiring (dependency injection).
 * No DI framework applied, just manual wiring.
 */
class ProductionModule {
  val questionsParser: QuestionsParser = new QuestionsParserImpl
  val httpClient: HttpClient = new HttpClientImpl
  val client: TestinatorClient = new TestinatorClientImpl(httpClient)
  val bot: Bot = new BotImpl(client, questionsParser)
}
