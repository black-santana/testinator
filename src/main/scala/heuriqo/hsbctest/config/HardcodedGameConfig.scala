package heuriqo.hsbctest.config

object HardcodedGameConfig extends GameConfig {
  override def serverHost: String = "testinator-project.appspot.com"
  override def questionsLimit: Int = 20
  override def userAlias: String = "wz"
  override def serverPort: Int = 80
}
