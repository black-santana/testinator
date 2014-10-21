package heuriqo.hsbctest.questionsParser

/**
 * Signals syntax error in the question received from server.
 */
class MalformedQuestion(msg: String) extends Exception(msg) {
}
