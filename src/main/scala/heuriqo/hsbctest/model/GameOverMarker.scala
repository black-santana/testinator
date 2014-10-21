package heuriqo.hsbctest.model

case object GameOverMarker extends GameQuestion {

  override def generateAnswer: GameAnswer = throw new Exception("not implemented by design")

  override def isGameOverMarker: Boolean = true
}
