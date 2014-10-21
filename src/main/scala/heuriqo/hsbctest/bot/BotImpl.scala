package heuriqo.hsbctest.bot

import heuriqo.hsbctest.client.TestinatorClient
import heuriqo.hsbctest.config.GameConfig
import heuriqo.hsbctest.questionsParser.QuestionsParser

/**
 * Implementation of game bot.
 * For each game it spawns a dedicated state machine of the game.
 * This way we support several games to run in parallel.
 */
class BotImpl(client: TestinatorClient, questionsParser: QuestionsParser) extends Bot {

  override def runTheGame(config: GameConfig): GameResult = {
    val game = new Game(client, questionsParser, config)
    //caution: encapsulate the line below in a separate thread if parallel games are to be played
    game.runTheGame()
  }
}
