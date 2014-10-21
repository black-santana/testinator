package heuriqo.hsbctest

import heuriqo.hsbctest.config.HardcodedGameConfig

object Startup {

  def main(args: Array[String])  {
    val componentsContainer = new ProductionModule
    componentsContainer.bot.runTheGame(HardcodedGameConfig)
  }

}
