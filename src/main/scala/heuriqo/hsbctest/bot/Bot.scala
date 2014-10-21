package heuriqo.hsbctest.bot

import heuriqo.hsbctest.config.GameConfig

/**
 * Knows how to spawn new games.
 * Decides about game running semantics (parallel/sequential etc).
 */
trait Bot {

  /**
   * Runs a single game of questions.
   * During the game several questions will be answwered.
   *
   * @param config configuration of game
   * @return true if the game finished with success
   */
  def runTheGame(config: GameConfig): GameResult
}
