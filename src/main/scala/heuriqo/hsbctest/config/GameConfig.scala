package heuriqo.hsbctest.config

/**
 * Configuration of a single game to be played.
 */
trait GameConfig {
  def serverHost: String
  def serverPort: Int
  def userAlias: String    
  def questionsLimit: Int
}
