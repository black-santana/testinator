package heuriqo.hsbctest.client

sealed abstract class AnswerResponse {
}

/**
 * (Enum) Classification of responses to "send answer to server" in the game protocol.
 */
object AnswerResponse {
  case object PASS extends AnswerResponse
  case object FAIL extends AnswerResponse
}
