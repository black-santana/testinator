package heuriqo.hsbctest.model

case class AdditionQuestion(left: Int, right: Int) extends GameQuestion {

  override def generateAnswer: GameAnswer = GameAnswer((left + right).toString())

  override def isGameOverMarker: Boolean = false
}
