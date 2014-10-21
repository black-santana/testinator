package heuriqo.hsbctest.client

/**
 * For signalling network connection problems while playing the game.
 * @param ex
 */
class ConnectionToServerFailed(ex: Throwable) extends Exception(ex) {

}
