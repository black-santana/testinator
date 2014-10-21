package heuriqo.hsbctest.model

/**
 * Base trait for representing parsed questions.
 */
trait GameQuestion {
  def generateAnswer: GameAnswer
  def isGameOverMarker: Boolean
}
